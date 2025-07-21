package spreadCalc.application.service;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import spreadCalc.application.dto.SpreadCalcResults;
import spreadCalc.application.usecase.GetMarketRankingUseCase;
import spreadCalc.repository.SpreadCalcResultsService;

@Service
@AllArgsConstructor
public class GetMarketRankingService implements GetMarketRankingUseCase {
	
	private final SpreadCalcResultsService calcResultsService;
	
	@Override
	public SpreadCalcResults getMarketRanking() {
		return calcResultsService.getSpreadCalcResults();
	}

}
