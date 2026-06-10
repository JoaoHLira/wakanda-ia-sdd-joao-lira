package academy.wakanda.wakanda_ai.comunicacao.infra;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ClintResponse;
import academy.wakanda.wakanda_ai.comunicacao.application.service.CancelaClintService;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCanceladoClintDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Log4j2
public class CancelaClintInfra implements CancelaClintService {

    @Value("${clint.perdido}")
    private String clintUrl;

    private final WebClient webClient;

    public CancelaClintInfra(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public ClintResponse cancelaWakanderClint(WakanderCanceladoClintDTO wakanderCanceladoClint) {
        log.info("[start] ClintInfraClient - cancelaWakanderClint");
        ClintResponse response;
        try {
            response = webClient.post()
            .uri(clintUrl)
            .bodyValue(wakanderCanceladoClint)
            .retrieve()
            .bodyToMono(ClintResponse.class)
            .block();
            log.info("[success] Wakander cancelado com sucesso na Clint!");
        } catch (WebClientResponseException e) {
            log.error("[error] Erro ao cancelar Wakander na Clint: {}", e.getResponseBodyAsString());
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        log.debug("[finish] ClintInfraClient - cancelaWakanderClint");
        return response;
    }
}
