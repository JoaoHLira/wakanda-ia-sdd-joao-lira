package academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.processadores;

import academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.DiscordService;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;
import academy.wakanda.wakanda_ai.wakander.domain.DiscordEventype;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderInfraRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComunicacaoProcessorRemoveFromServerTest {
    @InjectMocks
    private ComunicacaoProcessorRemoveFromServer processor;
    @Mock
    private DiscordService discordService;
    @Mock
    private WakanderInfraRepository wakanderInfraRepository;

    @BeforeEach
	void setUp() {
		loadTemplates(Templates.BASE_PACKAGE);
	}
	
    @Test
    @DisplayName("Deve remover Wakander do servidor com sucesso")
    void deveRemoverWakanderDoServidorComSucesso() {
		Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        DiscordEventRequest request = new DiscordEventRequest(DiscordEventype.REMOVE_FROM_SERVER, wakander);

        when(wakanderInfraRepository.buscaWakanderPorId(wakander.getIdWakander()))
                .thenReturn(wakander);

        processor.processaPorTipoEvento(request);

        verify(discordService).cancelaMembroDoServidor(wakander.getIdDiscord());

    }
}