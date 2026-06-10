package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.CadastraMembroRequest;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.MemberkitUserDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Log4j2
@Component
public class JornadaWakanderWebClient implements JornadaWakanderClient {
    private final WebClient webClient;

    @Value("${memberkit.url}")
    private String memberkitUrl;

    @Value("${memberkit.api-key}")
    private String apiKey;

    public JornadaWakanderWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public <T> T requisicaoPostParaOMemberKit(Object requestBody, Class<T> responseType) {
        log.info("[start] JornadaWakanderWebClient - requisicaoParaOMemberKit");
        log.debug("Request: {}", requestBody);
        String uri = memberkitUrl + "/api/v1/users?api_key=" + apiKey;
        try {
            T response = webClient.post()
                    .uri(uri)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
            log.debug("[finish] JornadaWakanderWebClient - requisicaoParaOMemberKit");
            return response;
        } catch (WebClientResponseException e) {
            log.error("Erro inesperado ao fazer a requisição ao MemberKit: {}\n ResponseBody: {}", e.getMessage(), e.getResponseBodyAsString());
            throw APIException.build(HttpStatus.valueOf(e.getStatusCode().value()), e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao fazer a requisição ao MemberKit: {}", e.getMessage());
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao buscar cliente: ", e);
        }
    }
}
