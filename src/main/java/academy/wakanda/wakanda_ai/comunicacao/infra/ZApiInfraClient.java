package academy.wakanda.wakanda_ai.comunicacao.infra;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayload;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIResponseGrupo;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ZApiClient;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Component
public class ZApiInfraClient implements ZApiClient {
    @Value("${z-api.instancia-id}")
    private String instancia;
    @Value("${z-api.token-instancia}")
    private String tokenInstancia;
    @Value("${z-api.token-cliente}")
    private String clienteToken;
    @Value("${z-api.base-url}")
    private String baseUrl;

    private final WebClient webClient;

    public ZApiInfraClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void enviaMensagemWhatsApp(ZAPIPayload zapiPayload) {
        String url = geraUrlRequisicaoZapi("send-text");
        try {
            webClient.post()
                    .uri(url)
                    .header("Client-Token", clienteToken)
                    .bodyValue(zapiPayload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("Mensagem enviada com sucesso!");
        } catch (WebClientResponseException e) {
            String errorMessage = e.getResponseBodyAsString();
            log.error("[error] Erro ao enviar mensagem: {}", errorMessage);
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("[error] Erro inesperado ao enviar mensagem: {}", e.getMessage());
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao enviar mensagem.");
        }
    }

	@Override
	public <T> ZAPIResponseGrupo processaRequisicaoGrupoWhatsapp(T zApiPayloadRemoveDoGrupo, String typeRequisicao) {
        log.info("[start] ZAPIClient - processaRequisicaoGrupoWhatsapp");
		ZAPIResponseGrupo response = realizaRequisaoAoGrupoWhatsapp(zApiPayloadRemoveDoGrupo, typeRequisicao);
        log.debug("[finish] ZAPIClient - processaRequisicaoGrupoWhatsapp");
		return response;
	}
	
	private <T> ZAPIResponseGrupo realizaRequisaoAoGrupoWhatsapp(T payload, String tipoRequisicao) {
        log.info("[start] ZAPIClient - realizaRequisaoAoGrupoWhatsapp");
        String url = geraUrlRequisicaoZapi(tipoRequisicao);
        try {
            ZAPIResponseGrupo response = webClient.post()
                    .uri(url)
                    .header("Client-Token", clienteToken)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(ZAPIResponseGrupo.class)
                    .doOnSuccess(request -> log.info("[sucess] Operação realizada com sucesso ao grupo profissão programador. Status: {}", request.getValue()))
                    .block(); 
            return response;
        } catch (WebClientResponseException e) {
        	 log.error("[error] Falha ao realizadar Operação de adicionar/remover do grupo: {}", e.getResponseBodyAsString());
             throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
			log.error("[error] Erro inesperado ao realizadar Operação ao grupo");
			throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao fazer a requisição ao Z-API.");
		}
	}

	private String geraUrlRequisicaoZapi(String tipoRequisicao) {
	    String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
	            .pathSegment("instances", instancia, "token", tokenInstancia, tipoRequisicao)
	            .toUriString();
	    log.debug("[ZAPI] URL montada '{}'", url);
	    return url;
	}

}