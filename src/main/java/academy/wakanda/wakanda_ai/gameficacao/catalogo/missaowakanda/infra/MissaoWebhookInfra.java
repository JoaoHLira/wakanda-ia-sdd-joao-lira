package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoWebhookDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service.MissaoWebhookService;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class MissaoWebhookInfra implements MissaoWebhookService {

    @Value("${n8n.webhook}")
    private String n8nWebhookUrl;

    private final WebClient webClient;

    public MissaoWebhookInfra(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void enviaWebhookProcessaMissao (MissaoWebhookDTO missaoWebhookDTO) {
        log.info("[start] MissaoWebhookInfra - enviaWebhookProcessaMissao");

        webClient.post().uri(n8nWebhookUrl).bodyValue(missaoWebhookDTO).retrieve().toBodilessEntity().subscribe(
                success -> log.info("[success] Missão enviada com sucesso para o N8N!"),
                error -> log.error("[error] Erro ao enviar requisição para o N8N: {}", error.getMessage()));

        log.debug("[finish] MissaoWebhookInfra - enviaWebhookProcessaMissao");

    }

}
