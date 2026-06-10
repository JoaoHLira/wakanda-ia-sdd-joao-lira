package academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.processadores;

import academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.DiscordService;
import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;
import academy.wakanda.wakanda_ai.wakander.domain.DiscordEventype;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderInfraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoProcessorRemoveFromServer implements ComunicacaoProcessorDiscord {
	private final WakanderInfraRepository wakanderInfraRepository;
	private final DiscordService discordService;
	
	@Override
	public boolean validaSeProcessa(DiscordEventype discordEventype) {
        return discordEventype.equals(DiscordEventype.REMOVE_FROM_SERVER);
	}

	@Override
	public void processaPorTipoEvento(DiscordEventRequest discordEventRequest) {
        log.info("[start] ComunicacaoProcessorRemoveFromServer - processaPorTipoEvento");
    	Wakander wakander = wakanderInfraRepository.buscaWakanderPorId(discordEventRequest.getIdWakander());
		wakander.validaIdDiscord();
    	discordService.cancelaMembroDoServidor(wakander.getIdDiscord());
        log.debug("[finish] ComunicacaoProcessorRemoveFromServer - processaPorTipoEvento");
	}
}