package spreadCalc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kanga")
public class KangaProperties {

	private String baseUrl;
	private String orderbookPath;
	private String marketPairsPath;
}