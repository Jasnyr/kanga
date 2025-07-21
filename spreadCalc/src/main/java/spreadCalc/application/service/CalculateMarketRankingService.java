package spreadCalc.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import spreadCalc.application.dto.MarketSpreadPair;
import spreadCalc.application.dto.SpreadCalcResults;
import spreadCalc.application.dto.SpreadRanking;
import spreadCalc.application.usecase.CalculateMarketRankingUseCase;
import spreadCalc.application.usecase.GetMarketPairOrderbookUseCase;
import spreadCalc.application.usecase.GetMarketPairsUseCase;
import spreadCalc.domain.model.MarketPair;
import spreadCalc.domain.model.OrderBook;
import spreadCalc.repository.SpreadCalcResultsService;

@Service
@AllArgsConstructor
public class CalculateMarketRankingService implements CalculateMarketRankingUseCase {

	private final GetMarketPairsUseCase marketPairsUseCase;
	private final GetMarketPairOrderbookUseCase marketPairOrderbookUseCase;
	private final SpreadCalcResultsService calcResultsService;

	@Override
	public void calculateMarketRanking() {
		List<MarketPair> marketPairs = marketPairsUseCase.getMarketPairs();
		Map<String, MarketSpreadPair> smallSpread = new TreeMap<>();
		Map<String, MarketSpreadPair> largeSpread = new TreeMap<>();
		Map<String, MarketSpreadPair> incalculableSpread = new TreeMap<>();

		try {
			List<CompletableFuture<OrderBook>> futureOrderBooks = marketPairs.stream().map(this::fetchOrderBook).toList();
			CompletableFuture.allOf(futureOrderBooks.toArray(new CompletableFuture[0])).get();
			List<OrderBook> orderBookResults = futureOrderBooks.stream().map(CompletableFuture::join).collect(Collectors.toList());

			for (OrderBook orderbook : orderBookResults) {
				Double spread = orderbook.calculateSpread();
				if (spread == null) {
					incalculableSpread.put(orderbook.ticker_id(), new MarketSpreadPair(orderbook.ticker_id(), "N/A"));
				} else if (spread > 2) {
					largeSpread.put(orderbook.ticker_id(), new MarketSpreadPair(orderbook.ticker_id(),
							BigDecimal.valueOf(spread).setScale(2, RoundingMode.HALF_UP).toString()));
				} else {
					smallSpread.put(orderbook.ticker_id(), new MarketSpreadPair(orderbook.ticker_id(),
							BigDecimal.valueOf(spread).setScale(2, RoundingMode.HALF_UP).toString()));
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		SpreadRanking ranking = new SpreadRanking(
				smallSpread.values().toArray(),
				largeSpread.values().toArray(),
				incalculableSpread.values().toArray());
		calcResultsService.setSpreadCalcResults(new SpreadCalcResults(ZonedDateTime.now(ZoneOffset.UTC), ranking));
	}

	private CompletableFuture<OrderBook> fetchOrderBook(MarketPair marketPair) {
		Executor virtualThreadExecutor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
		return CompletableFuture.supplyAsync(() -> {
			OrderBook orderbook = marketPairOrderbookUseCase.getMarketPairOrderbook(marketPair.ticker_id());
			return orderbook;
		}, virtualThreadExecutor);
	}
}
