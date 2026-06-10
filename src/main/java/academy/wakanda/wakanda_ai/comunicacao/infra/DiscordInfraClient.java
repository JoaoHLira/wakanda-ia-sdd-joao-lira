package academy.wakanda.wakanda_ai.comunicacao.infra;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import academy.wakanda.wakanda_ai.comunicacao.application.api.DiscordConviteRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.DiscordConviteResponse;
import academy.wakanda.wakanda_ai.comunicacao.application.service.DiscordClient;
import academy.wakanda.wakanda_ai.config.DiscordProperties;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class DiscordInfraClient implements DiscordClient {
	private final DiscordProperties discordProperties;
    private final WebClient webClient;
    
    public DiscordInfraClient(WebClient.Builder webClientBuilder, DiscordProperties discordProperties) {
        this.webClient = webClientBuilder.build();
        this.discordProperties = discordProperties;
    }

    @Override
    public DiscordConviteResponse criaConviteDoCanalParaWakander(DiscordConviteRequest conviteRequest) {
        String url = discordProperties.getApiUrl() + "/api/v10/channels/" + discordProperties.getChannels().getId() + "/invites";
        DiscordConviteResponse response;
        try {
            response = webClient.post()
                    .uri(url)
                    .header("Authorization","Bot " + discordProperties.getBot().getToken())
                    .header("Content-Type", "application/json")
                    .bodyValue(conviteRequest)
                    .retrieve()
                    .bodyToMono(DiscordConviteResponse.class)
                    .block();
            log.info("Convite criado com sucesso!");
        } catch (WebClientResponseException e) {
            String errorMessage = e.getResponseBodyAsString();
            log.error("[error] Erro ao criar convite: {}", errorMessage);
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("[error] Erro inesperado ao criar convite: {}", e.getMessage());
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao criar convite.");
        }
        return response;
    }

}
