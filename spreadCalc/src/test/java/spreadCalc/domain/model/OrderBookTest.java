package spreadCalc.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

public class OrderBookTest {

	@Test
	public void testCalculateSpread_HappyPath() {
		OrderBook orderBook = new OrderBook(123L,
				List.of(new BidAskPair(4.2610, 1.0), new BidAskPair(4.0, 2.0)),
				List.of(new BidAskPair(4.5997, 1.5), new BidAskPair(5.0, 3.0)), "EUR_PLN");

		Double spread = orderBook.calculateSpread();
		// Expected: ((minAsk - maxBid)/midPrice)*100
		// minAsk = 4.5997, maxBid = 4.2610, midPrice = (4.5997 + 4.2610)/2 = 4.43035
		// Spread = (4.5997 - 4.2610) / 4.43035 * 100 = 7.64 approx
		assertNotNull(spread);
		assertEquals(7.64, spread, 0.01);
	}

	@Test
	public void testCalculateSpread_EmptyAsks() {
		OrderBook orderBook = new OrderBook(123L, List.of(new BidAskPair(100.0, 1.0)), List.of(), "BTC_USD");
		assertNull(orderBook.calculateSpread());
	}

	@Test
	public void testCalculateSpread_EmptyBids() {
		OrderBook orderBook = new OrderBook(123L, List.of(), List.of(new BidAskPair(105.0, 1.5)), "BTC_USD");
		assertNull(orderBook.calculateSpread());
	}

	@Test
	public void testCalculateSpread_MidPriceZero() {
		OrderBook orderBook = new OrderBook(123L, List.of(new BidAskPair(0.0, 1.0)), List.of(new BidAskPair(0.0, 1.0)),
				"BTC_USD");
		assertNull(orderBook.calculateSpread());
	}

}
