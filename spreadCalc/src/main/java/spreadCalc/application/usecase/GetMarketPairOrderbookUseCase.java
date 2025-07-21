package spreadCalc.application.usecase;

import spreadCalc.domain.model.OrderBook;

public interface GetMarketPairOrderbookUseCase {

	OrderBook getMarketPairOrderbook(String pair);
}
