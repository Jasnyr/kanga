package spreadCalc.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import spreadCalc.application.usecase.GetMarketPairOrderbookUseCase;
import spreadCalc.config.KangaProperties;
import spreadCalc.domain.model.OrderBook;

@Service
@AllArgsConstructor
public class GetMarketPairOrderbookService implements GetMarketPairOrderbookUseCase {

	private final KangaProperties properties;
	private final WebClient webClient;

	@Override
	public OrderBook getMarketPairOrderbook(String pair) {
		return webClient.get()
				.uri(properties.getOrderbookPath(), pair)
				.retrieve()
				.bodyToMono(OrderBook.class)
				.block();
	}

}
