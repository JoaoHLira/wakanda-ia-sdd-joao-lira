package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Configuration
@ConfigurationProperties(prefix = "memberkit")
@Getter
@Setter
@Log4j2
public class MemberkitConfig {
	private String urlApi;
	private String apiKey;
	private String specificCourseId;
}
