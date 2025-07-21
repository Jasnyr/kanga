package spreadCalc.repository;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import spreadCalc.application.dto.SpreadCalcResults;

@Service
public class SpreadCalcResultsService {

	@Getter
	@Setter
	private SpreadCalcResults spreadCalcResults;
}
