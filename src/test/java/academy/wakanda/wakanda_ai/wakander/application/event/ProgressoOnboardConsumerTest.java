package academy.wakanda.wakanda_ai.wakander.application.event;

import academy.wakanda.wakanda_ai.comunicacao.application.api.DiscordConviteRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.DiscordConviteResponse;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressoOnboardConsumerTest {
    @InjectMocks
    private ProgressoOnboardConsumer progressoOnboardConsumer;
    @Mock
    private PublicadorNotificacaoSns publicadorNotificacaoSns;
    @Mock
    private TopicNames topicNames;
    @Mock
    private ComunicacaoService comunicacaoService;
    @Mock
    private OnboardingWakanderService onboardingWakanderService;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        loadTemplates(Templates.BASE_PACKAGE);
    }

    @Test
    void deveEnviarNotificacaoDeCadastroWakanderMemberkit() {
        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        CadastroCompletoEvent evento = new CadastroCompletoEvent(wakander);

        doNothing().when(publicadorNotificacaoSns).enviaNotificacaoSns(any(), any(), any());
        
        progressoOnboardConsumer.cadastraWakanderNoMemberkit(evento);
        
        verify(topicNames, times(1)).getMemberkitRequests();
        verify(publicadorNotificacaoSns, times(1)).enviaNotificacaoSns(any(), any(), any());
    }

    @Test
    void deveConvidarWakanderParaDiscordComSucesso() {
        CadastroCompletoEvent evento = mock(CadastroCompletoEvent.class);
        when(evento.getWhatsapp()).thenReturn("5573999999999");
        when(evento.getIdWakander()).thenReturn(UUID.randomUUID());

        DiscordConviteResponse conviteResponse = new DiscordConviteResponse("ABC123");
        when(comunicacaoService.convidaParaCanalDiscord(any(DiscordConviteRequest.class))).thenReturn(conviteResponse);
        when(topicNames.getZapiRequests()).thenReturn("zapi-requests-topic");

        progressoOnboardConsumer.convidaWakanderParaDiscord(evento);

        ArgumentCaptor<ZApiEventDto> captor = ArgumentCaptor.forClass(ZApiEventDto.class);
        verify(publicadorNotificacaoSns, times(2)).enviaNotificacaoSns(
                eq(evento.getIdWakander().toString()),
                captor.capture(),
                eq("zapi-requests-topic")
        );

        ZApiEventDto capturedEvent = captor.getValue();
        Assertions.assertEquals("5573999999999", capturedEvent.getWhatsapp());
        Assertions.assertEquals(ZApiEventype.NORMAL_MESSAGE, capturedEvent.getType());
    }

    @Test
    void deveLancarExcecaoQuandoFalharAoConvidarWakanderParaDiscord() {
        CadastroCompletoEvent evento = mock(CadastroCompletoEvent.class);

        when(comunicacaoService.convidaParaCanalDiscord(any(DiscordConviteRequest.class)))
                .thenThrow(new RuntimeException("Erro ao criar convite"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            progressoOnboardConsumer.convidaWakanderParaDiscord(evento);
        });

        Assertions.assertEquals("Erro ao criar convite", exception.getMessage());
        verify(publicadorNotificacaoSns, never()).enviaNotificacaoSns(anyString(), any(ZApiEventDto.class), anyString());
    }
    
    @Test
    void deveAtualizarStatusCadastroComSucesso() {
        UUID idWakander = UUID.randomUUID();
        CadastroCompletoEvent evento = mock(CadastroCompletoEvent.class);
        OnboardingWakander onboardingMock = mock(OnboardingWakander.class);

        when(evento.getIdWakander()).thenReturn(idWakander);
        when(evento.getWhatsapp()).thenReturn("5511999999999");
        when(onboardingWakanderService.buscaOnboardingPorIdWakander(idWakander)).thenReturn(onboardingMock);
        when(onboardingWakanderService.retornaChecklist(onboardingMock))
        	.thenReturn(MensagensWhatsapp.PROGRESSO_CHECKLIST.getMensagem());
        when(topicNames.getZapiRequests()).thenReturn("zapi-requests");

        progressoOnboardConsumer.atualizaStatusCadastroOnboarding(evento);

        verify(onboardingMock).atualizaCadastroConfirmado();
        verify(onboardingWakanderService).save(onboardingMock);
        verify(publicadorNotificacaoSns, times(2)).enviaNotificacaoSns(
                eq(idWakander.toString()),
                any(ZApiEventDto.class),
                eq("zapi-requests")
        );
    }
    
    @Test
    void deveLancarExcecaoQuandoOnboardingNaoEncontrado() {
        UUID idWakander = UUID.randomUUID();
        CadastroCompletoEvent evento = mock(CadastroCompletoEvent.class);

        when(evento.getIdWakander()).thenReturn(idWakander);
        when(onboardingWakanderService.buscaOnboardingPorIdWakander(idWakander))
            .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Onboarding não encontrado para este Wakander!"));

        APIException exception = assertThrows(APIException.class,
            () -> progressoOnboardConsumer.atualizaStatusCadastroOnboarding(evento)
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        verify(onboardingWakanderService, times(1)).buscaOnboardingPorIdWakander(any());
    }
}
