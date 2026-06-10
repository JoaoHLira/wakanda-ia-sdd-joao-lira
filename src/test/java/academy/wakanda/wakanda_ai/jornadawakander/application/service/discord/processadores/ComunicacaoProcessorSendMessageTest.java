package academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.config.DiscordProperties;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;
import academy.wakanda.wakanda_ai.wakander.domain.DiscordEventype;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComunicacaoProcessorSendMessageTest {

    @InjectMocks
    private ComunicacaoProcessorSendMessage processor;

    @Mock
    private OnboardingWakanderService onboardingWakanderService;
    @Mock
    private ComunicacaoService comunicacaoService;
    @Mock
    private DiscordProperties discordProperties;
    @Mock
    private DiscordProperties.GuildWakanda guildWakanda;
    @Mock
    private DiscordProperties.Channels channels;

	@BeforeEach
	void setUp() {
		loadTemplates(Templates.BASE_PACKAGE);
	}
	
    @Test
    @DisplayName("Deve enviar mensagem quando Wakander não entrou no Discord")
    void deveEnviarMensagemQuandoNaoEntrouDiscord() {
		Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        DiscordEventRequest request = new DiscordEventRequest(DiscordEventype.SEND_DISCORD_URL, wakander);
        OnboardingWakander onboarding = new OnboardingWakander(wakander);
        
        when(onboardingWakanderService.buscaOnboardingPorIdWakander(any())).thenReturn(onboarding);
        when(discordProperties.getGuildWakanda()).thenReturn(guildWakanda);
        when(discordProperties.getChannels()).thenReturn(channels);
        when(guildWakanda.getId()).thenReturn("guild-id");
        when(channels.getIniciarValidacao()).thenReturn("canal-id");
        
        processor.processaPorTipoEvento(request);

        verify(comunicacaoService).enviaMensagemWhatsapp(any(MensagemRequest.class));
    }
}