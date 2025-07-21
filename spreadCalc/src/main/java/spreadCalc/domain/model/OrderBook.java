package spreadCalc.domain.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import spreadCalc.application.helper.BidAskPairDeserializer;

public record OrderBook(
		Long timestamp,
		@JsonDeserialize(using = BidAskPairDeserializer.class) List<BidAskPair> bids,
		@JsonDeserialize(using = BidAskPairDeserializer.class) List<BidAskPair> asks,
		String ticker_id)
		implements ICalcSpread {

	@Override
	public Double calculateSpread() {
		if (asks.isEmpty() || bids.isEmpty()) {
			return null;
		}

		// a for loop could be used here because it's faster,
		// but since the data is fetched via a get request
		// the difference would be negligible due to network latency
		double minAsk = asks.stream().mapToDouble(BidAskPair::price).min().orElse(0);
		double maxBid = bids.stream().mapToDouble(BidAskPair::price).max().orElse(0);
		double midPrice = (minAsk + maxBid) / 2;
		if (midPrice == 0) {
			return null;
		}
		return (minAsk - maxBid) / midPrice * 100;
	}

}
