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

	private static final double SMALL_SPREAD = 2;
	private static final Executor VIRTUAL_EXECUTOR = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
	private final GetMarketPairsUseCase marketPairsUseCase;
	private final GetMarketPairOrderbookUseCase marketPairOrderbookUseCase;
	private final SpreadCalcResultsService calcResultsService;

	@Override
	public void calculateMarketRanking() {
		List<MarketPair> marketPairs = marketPairsUseCase.getMarketPairs();
		SpreadRanking ranking = null;

		try {
			List<OrderBook> orderBooks = fetchOrderBooks(marketPairs);
			ranking = createRankingFromOrderbook(orderBooks);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		SpreadCalcResults spreadCalcResults = new SpreadCalcResults(ZonedDateTime.now(ZoneOffset.UTC), ranking);
		calcResultsService.setSpreadCalcResults(spreadCalcResults);
	}

	private List<OrderBook> fetchOrderBooks(List<MarketPair> marketPairs) throws InterruptedException, ExecutionException {
		List<CompletableFuture<OrderBook>> futureOrderBooks = marketPairs.stream().map(this::fetchOrderBookFuture).toList();
		CompletableFuture.allOf(futureOrderBooks.toArray(new CompletableFuture[0])).get();
		return futureOrderBooks.stream().map(CompletableFuture::join).collect(Collectors.toList());
	}

	private CompletableFuture<OrderBook> fetchOrderBookFuture(MarketPair marketPair) {
		return CompletableFuture.supplyAsync(() -> marketPairOrderbookUseCase.getMarketPairOrderbook(marketPair.ticker_id()), VIRTUAL_EXECUTOR);
	}

	private SpreadRanking createRankingFromOrderbook(List<OrderBook> orderBookResults) {
		Map<String, MarketSpreadPair> smallSpread = new TreeMap<>();
		Map<String, MarketSpreadPair> largeSpread = new TreeMap<>();
		Map<String, MarketSpreadPair> incalculableSpread = new TreeMap<>();
		
		for (OrderBook orderbook : orderBookResults) {
			Double spread = orderbook.calculateSpread();
			if (spread == null) {
				incalculableSpread.put(orderbook.ticker_id(), new MarketSpreadPair(orderbook.ticker_id(), "N/A"));
			} else if (spread > SMALL_SPREAD) {
				largeSpread.put(orderbook.ticker_id(), new MarketSpreadPair(orderbook.ticker_id(),
						BigDecimal.valueOf(spread).setScale(2, RoundingMode.HALF_UP).toString()));
			} else {
				smallSpread.put(orderbook.ticker_id(), new MarketSpreadPair(orderbook.ticker_id(),
						BigDecimal.valueOf(spread).setScale(2, RoundingMode.HALF_UP).toString()));
			}
		}
		
		return new SpreadRanking(
				smallSpread.values().toArray(),
				largeSpread.values().toArray(),
				incalculableSpread.values().toArray());
	}
}
