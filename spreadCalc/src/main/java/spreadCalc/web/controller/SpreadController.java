package spreadCalc.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import spreadCalc.application.dto.SpreadCalcResults;
import spreadCalc.application.usecase.CalculateMarketRankingUseCase;
import spreadCalc.application.usecase.GetMarketRankingUseCase;

@RestController
@RequestMapping("/api/spread")
@AllArgsConstructor
public class SpreadController {

	private final CalculateMarketRankingUseCase calculateMarketRankingUseCase;
	private final GetMarketRankingUseCase getMarketRankingUseCase;

	@PostMapping("/calculate")
	public ResponseEntity<String> calculateRanking() {
		calculateMarketRankingUseCase.calculateMarketRanking();
	    return ResponseEntity.ok("Calculation done, call /ranking endpoint for results");
	}
	
	@GetMapping("/ranking")
	public ResponseEntity<SpreadCalcResults> fetchRanking() {
		return ResponseEntity.ok(getMarketRankingUseCase.getMarketRanking());
	}
}
