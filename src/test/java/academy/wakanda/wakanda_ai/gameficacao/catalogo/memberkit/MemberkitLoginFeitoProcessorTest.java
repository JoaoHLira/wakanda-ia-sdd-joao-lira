package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.LoginMemberkitDto;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores.MemberkitLoginFeitoProcessor;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberkitLoginFeitoProcessorTest {
    @InjectMocks
    private MemberkitLoginFeitoProcessor loginFeitoMemberkitProcessor;
    @Mock
    private PublicadorNotificacaoSns publicadorNotificacaoSns;
    @Mock
    private WakanderRepository wakanderRepository;
    @Mock
    OnboardingWakanderService onboardingWakanderService;
    @Mock
    private TopicNames topicNames;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve validar se processa evento de login")
    void deveRetornarTrueQuandoValidarTipoCorretoDeEvento() {
        String tipoEvento = "user.signed_in";
        boolean resultado = loginFeitoMemberkitProcessor.validaSeEventoProcessa(tipoEvento);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Não deve validar quando tipo do evento é diferente de login")
    void deveRetornarFalseQuandoValidarTipoEventoDiferenteDeLogin() {
        String tipoEvento = "user.last_seen";
        boolean resultado = loginFeitoMemberkitProcessor.validaSeEventoProcessa(tipoEvento);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve processar evento de primeiro login e enviar notificação")
    void deveProcessarEventoDePrimeiroLoginEEnviarNotificacao() throws Exception {
        Wakander wakander = DataHelper.criaWakander();
        wakander.atualizaStatusJornada(JornadaWakanda.ONBOARD);
        LoginMemberkitDto loginMemberkitDto = DataHelper.criaLoginMemberkit("user.signed_in");
        
        MemberkitEventRequest request = new MemberkitEventRequest("user.signed_in", loginMemberkitDto.getData());
        
        OnboardingWakander onboardingWakander = mock(OnboardingWakander.class);
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"type\":\"user.signed_in\",\"data\":{}}");
        when(objectMapper.readValue(anyString(), eq(LoginMemberkitDto.class))).thenReturn(loginMemberkitDto);
        when(wakanderRepository.buscaWakanderPorIdMemberKit(anyString())).thenReturn(wakander);
        when(onboardingWakanderService.buscaOnboardingPorIdWakander(wakander.getIdWakander()))
                .thenReturn(onboardingWakander);
        when(onboardingWakanderService.retornaChecklist(any(OnboardingWakander.class)))
                .thenReturn(MensagensWhatsapp.PROGRESSO_CHECKLIST.getMensagem());
        when(topicNames.getZapiRequests()).thenReturn("zapi-topic-name");

        loginFeitoMemberkitProcessor.processaEvento(request);

        verify(publicadorNotificacaoSns).enviaNotificacaoSns(eq("checklist"), any(ZApiEventDto.class), eq("zapi-topic-name"));
        verify(wakanderRepository).save(wakander);
    }


    @Test
    @DisplayName("Não deve enviar notificação quando não for o primeiro login")
    void naoDeveEnviarNotificacaoQuandoNaoForPrimeiroLogin() throws Exception {
        Wakander wakander = DataHelper.criaWakander();
        wakander.atualizaStatusJornada(JornadaWakanda.JORNADA_CONQUISTA);
        LoginMemberkitDto loginMemberkitDto = DataHelper.criaLoginMemberkit("user.signed_in");
        
        MemberkitEventRequest request = new MemberkitEventRequest("user.signed_in", loginMemberkitDto.getData());
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"type\":\"user.signed_in\",\"data\":{}}");
        when(objectMapper.readValue(anyString(), eq(LoginMemberkitDto.class))).thenReturn(loginMemberkitDto);
        when(wakanderRepository.buscaWakanderPorIdMemberKit(anyString())).thenReturn(wakander);
        
        loginFeitoMemberkitProcessor.processaEvento(request);

        verify(publicadorNotificacaoSns, never()).enviaNotificacaoSns(any(), any(), any());
        verify(wakanderRepository, never()).save(any());
    }
}
