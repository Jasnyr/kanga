package spreadCalc.application.usecase;

import java.util.List;

import spreadCalc.domain.model.MarketPair;

public interface GetMarketPairsUseCase {

	List<MarketPair> getMarketPairs();
}
