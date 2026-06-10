package academy.wakanda.wakanda_ai.comunicacao.infra;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ClintContatoRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ClintResponse;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ClintCRM;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Log4j2
public class ClintCRMInfra implements ClintCRM {

    private final WebClient webClient;

    @Value("${clint.contato}")
    private String clintWebhookContato;

    public ClintCRMInfra(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public ClintResponse enviaContatoParaClint(ClintContatoRequest contatoClint) {
        log.info("[start] ClintCRMInfra - enviaContatoParaClint");
        try {
            return webClient.post()
                    .uri(clintWebhookContato)
                    .bodyValue(contatoClint)
                    .retrieve()
                    .bodyToMono(ClintResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[error] Erro ao enviar contato para Clint: {}", e.getResponseBodyAsString());
            throw APIException.build(HttpStatus.BAD_REQUEST, "Erro ao enviar contato para Clint.");
        } catch (Exception e) {
            log.error("[erro] Erro inesperado ao enviar contato para Clint");
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao enviar contato para Clint");
        }
    }
}
