package spreadCalc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient kangaWebClient(WebClient.Builder builder, KangaProperties properties) {
		return builder.baseUrl(properties.getBaseUrl()).build();
	}
}