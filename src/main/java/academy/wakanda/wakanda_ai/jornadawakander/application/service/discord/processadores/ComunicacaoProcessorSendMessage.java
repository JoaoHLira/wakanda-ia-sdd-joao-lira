package academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.processadores;

import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.config.DiscordProperties;
import academy.wakanda.wakanda_ai.constants.MensagensDiscord;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;
import academy.wakanda.wakanda_ai.wakander.domain.DiscordEventype;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoProcessorSendMessage implements ComunicacaoProcessorDiscord {
	private final OnboardingWakanderService onboardingWakanderService;
	private final ComunicacaoService comunicacaoService;
	private final DiscordProperties discordProperties;
	
	@Override
	public boolean validaSeProcessa(DiscordEventype discordEventype) {
        return discordEventype.equals(DiscordEventype.SEND_DISCORD_URL);
	}

	@Override
	public void processaPorTipoEvento(DiscordEventRequest request) {
        log.info("[start] ComunicacaoProcessorSendMessage - processaPorTipoEvento");
    	OnboardingWakander onboardingWakander = onboardingWakanderService.buscaOnboardingPorIdWakander(request.getIdWakander());
    	if(!onboardingWakander.entrouDiscord() || request.getEmail() != null) {
    		String mensagem = MensagensDiscord.MENSAGEM_VALIDACAO_MEMBROS_ANTIGOS.formataMensagem(request.getEmail(), retornaUrl());
    		comunicacaoService.enviaMensagemWhatsapp(new MensagemRequest(request.getTelefone(), mensagem));
    	}
        log.debug("[finish] ComunicacaoProcessorSendMessage - processaPorTipoEvento");
	}
	
	private String retornaUrl(){
		return String.format("https://discord.com/channels/%s/%s", discordProperties.getGuildWakanda().getId(), 
				discordProperties.getChannels().getIniciarValidacao());
	}
}