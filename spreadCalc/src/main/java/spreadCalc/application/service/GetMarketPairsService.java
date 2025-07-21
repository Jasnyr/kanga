package spreadCalc.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import spreadCalc.application.usecase.GetMarketPairsUseCase;
import spreadCalc.config.KangaProperties;
import spreadCalc.domain.model.MarketPair;

@Service
@AllArgsConstructor
public class GetMarketPairsService implements GetMarketPairsUseCase {

	private final KangaProperties properties;
	private final WebClient webClient;

	@Override
	public List<MarketPair> getMarketPairs() {
		return webClient.get()
				.uri(properties.getMarketPairsPath())
				.retrieve()
				.bodyToFlux(MarketPair.class)
				.collectList()
				.block();
	}

}
