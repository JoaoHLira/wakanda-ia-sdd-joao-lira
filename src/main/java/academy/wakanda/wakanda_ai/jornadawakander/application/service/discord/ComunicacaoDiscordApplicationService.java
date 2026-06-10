package academy.wakanda.wakanda_ai.jornadawakander.application.service.discord;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.processadores.ComunicacaoProcessorDiscord;
import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ComunicacaoDiscordApplicationService implements ComunicacaoDiscordService {
	private final List<ComunicacaoProcessorDiscord> comunicacaoProcessorDiscord;

	@Override
	public void processaPorTipoMensagem(DiscordEventRequest discordEventRequest) {
        log.info("[start] ComunicacaoDiscordApplicationService - processaPorTipoMensagem");
		log.debug("[evento] {}", discordEventRequest.toString());
		ComunicacaoProcessorDiscord comunicacaoProcessorDiscord = strategyComunicacaoDiscordProcessor(discordEventRequest);
		comunicacaoProcessorDiscord.processaPorTipoEvento(discordEventRequest);
        log.debug("[finish] ComunicacaoDiscordApplicationService - processaPorTipoMensagem");
	}
	
    private ComunicacaoProcessorDiscord strategyComunicacaoDiscordProcessor(DiscordEventRequest message) {
        return comunicacaoProcessorDiscord.stream()
                .filter(m -> m.validaSeProcessa(message.getType()))
                .findFirst()
                .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Tipo da mensagem nao é processada"));
    }
    
}