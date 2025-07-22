package spreadCalc.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import spreadCalc.application.dto.SpreadCalcResults;
import spreadCalc.application.usecase.GetMarketPairOrderbookUseCase;
import spreadCalc.application.usecase.GetMarketPairsUseCase;
import spreadCalc.domain.model.BidAskPair;
import spreadCalc.domain.model.MarketPair;
import spreadCalc.domain.model.OrderBook;
import spreadCalc.repository.SpreadCalcResultsService;

@ExtendWith(MockitoExtension.class)
public class CalculateMarketRankingServiceTest {

    @Mock
    private GetMarketPairsUseCase getMarketPairsUseCase;

    @Mock
    private GetMarketPairOrderbookUseCase getMarketPairOrderbookUseCase;

    @Mock
    private SpreadCalcResultsService calcResultsService;

    @InjectMocks
    private CalculateMarketRankingService calculateMarketRankingService;

    @Test
    public void shouldCalculateRankingAndStoreResult() {
        // Given
        List<MarketPair> marketPairs = List.of(
            new MarketPair("BTC_USD", "BTC", "USD"), new MarketPair("ETH_PLN", "ETH", "PLN")
        );

        OrderBook btcOrderBookWithSmallSpread = new OrderBook(
            123L,
            List.of(new BidAskPair(118000, 1.0)),
            List.of(new BidAskPair(120000, 1.0)),
            "BTC_USD"
        );

        OrderBook ethOrderBookWithLargeSpread = new OrderBook(
            123L,
            List.of(new BidAskPair(13000, 1.0)),
            List.of(new BidAskPair(14000, 1.0)),
            "ETH_PLN"
        );

        when(getMarketPairsUseCase.getMarketPairs()).thenReturn(marketPairs);
        when(getMarketPairOrderbookUseCase.getMarketPairOrderbook("BTC_USD")).thenReturn(btcOrderBookWithSmallSpread);
        when(getMarketPairOrderbookUseCase.getMarketPairOrderbook("ETH_PLN")).thenReturn(ethOrderBookWithLargeSpread);

        // When
        calculateMarketRankingService.calculateMarketRanking();

        // Then
        ArgumentCaptor<SpreadCalcResults> captor = ArgumentCaptor.forClass(SpreadCalcResults.class);
        verify(calcResultsService).setSpreadCalcResults(captor.capture());

        SpreadCalcResults result = captor.getValue();
        assertNotNull(result);
        assertNotNull(result.timestamp());
        assertNotNull(result.ranking());
        
        Object[] small = result.ranking().group1();
        Object[] large = result.ranking().group2();
        Object[] na = result.ranking().group3();

        assertEquals(1, small.length);
        assertEquals(1, large.length);
        assertEquals(0, na.length);
    }
}