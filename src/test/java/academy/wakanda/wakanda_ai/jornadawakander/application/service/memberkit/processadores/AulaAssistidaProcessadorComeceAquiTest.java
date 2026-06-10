package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.JornadaWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.ProgressoWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.datahelper.JornadaWakanderDataHelper;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderContato;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AulaAssistidaProcessadorComeceAquiTest {

    @InjectMocks
    AulaAssistidaProcessadorComeceAqui aulaAssistidaProcessadorComeceAqui;
    @Mock
    JornadaWakanderRepository jornadaWakanderRepository;
    @Mock
    WakanderRepository wakanderRepository;
    @Mock
    ProgressoWakanderRepository progressoWakanderRepository;
    @Mock
    PublicadorNotificacaoSns publicadorNotificacaoSns;
    @Mock
    OnboardingWakanderService onboardingWakanderService;

    @Mock
    TopicNames topicNames;

    private final Long comeceAquiIdEsperado = 12345L;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(aulaAssistidaProcessadorComeceAqui, "IDComeceAqui", comeceAquiIdEsperado);
    }

    @Test
    @DisplayName("Deve retornar true quando o ID do curso for igual ao do 'Comece Aqui'")
    void validaSeEventoProcessa_RetornaTrueQuandoIdCursoForComeceAqui() {
        AulaAssistida aula = JornadaWakanderDataHelper.criaAulaAssistida();
        ReflectionTestUtils.setField(aula, "idCurso", comeceAquiIdEsperado);

        boolean resultado = aulaAssistidaProcessadorComeceAqui.validaSeEventoProcessa(aula, null);

        Assertions.assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve Retornar false quando o ID do curso for diferente do 'Comece Aqui'")
    void validaSeEventoProcessa_RetornaFalseQuandoIdCursoForDiferente() {
        AulaAssistida aula = JornadaWakanderDataHelper.criaAulaAssistida();
        ReflectionTestUtils.setField(aula, "idCurso", 99999L);

        boolean resultado = aulaAssistidaProcessadorComeceAqui.validaSeEventoProcessa(aula, null);

        Assertions.assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve processar evento e publicar notificação quando wakander assistir 3 aulas do Comece Aqui")
    void processaEvento_QuandoWakanderAssistir3Aulas() {
        AulaAssistida aula1 = JornadaWakanderDataHelper.criaAulaAssistida();
        AulaAssistida aula2 = JornadaWakanderDataHelper.criaAulaAssistida();
        AulaAssistida aula3 = JornadaWakanderDataHelper.criaAulaAssistida();
        UUID wakanderId = aula1.getIdWakander();

        ReflectionTestUtils.setField(aula1, "idWakander", wakanderId);
        ReflectionTestUtils.setField(aula1, "idCurso", comeceAquiIdEsperado);
        ReflectionTestUtils.setField(aula2, "idWakander", wakanderId);
        ReflectionTestUtils.setField(aula2, "idCurso", comeceAquiIdEsperado);
        ReflectionTestUtils.setField(aula3, "idWakander", wakanderId);
        ReflectionTestUtils.setField(aula3, "idCurso", comeceAquiIdEsperado);

        when(jornadaWakanderRepository.buscaAulasPorIdCurso(comeceAquiIdEsperado))
                .thenReturn(List.of(aula1, aula2, aula3));

        WakanderContato contato = DataHelper.criaWakander().getContato();
        Wakander wakander = mock(Wakander.class);
        when(wakander.getJornadaAtual()).thenReturn(JornadaWakanda.ONBOARD);
        when(wakander.getContato()).thenReturn(contato);
        when(wakander.getIdWakander()).thenReturn(wakanderId);

        when(topicNames.getZapiRequests()).thenReturn("zapi-topic");

        OnboardingWakander onboardingWakander = mock(OnboardingWakander.class);
        when(onboardingWakanderService.buscaOnboardingPorIdWakander(wakanderId))
                .thenReturn(onboardingWakander);

        when(onboardingWakanderService.retornaChecklist(any(OnboardingWakander.class))).thenReturn("✅Concluir Comece aqui");

        aulaAssistidaProcessadorComeceAqui.processaEvento(aula1, wakander);

        verify(progressoWakanderRepository).salvaProgresso(wakanderId, JornadaWakanda.ONBOARD, JornadaWakanda.FINALIZOU_COMECE_AQUI);
        verify(wakander).atualizaStatusJornada(JornadaWakanda.FINALIZOU_COMECE_AQUI);
        verify(wakanderRepository).save(wakander);

        verify(onboardingWakander).atualizaComeceAqui();
        verify(onboardingWakanderService).save(onboardingWakander);

        var captor = ArgumentCaptor.forClass(ZApiEventDto.class);
        verify(publicadorNotificacaoSns).enviaNotificacaoSns(eq(wakanderId.toString()), captor.capture(), eq("zapi-topic"));
        var dto = captor.getValue();

        Assertions.assertEquals(ZApiEventype.NORMAL_MESSAGE, dto.getType());
        Assertions.assertEquals("5511987654321", dto.getWhatsapp());
        Assertions.assertTrue(dto.getMensagem().contains("✅Concluir Comece aqui"));
    }

    @Test
    @DisplayName("Não deve processar evento quando Wakander assistir apenas 1 aula do Comece Aqui")
    void processaEvento_NaoProcessaQuandoApenas1Aula() {

        AulaAssistida aula1 = JornadaWakanderDataHelper.criaAulaAssistida();
        UUID wakanderId = aula1.getIdWakander();

        ReflectionTestUtils.setField(aula1, "idWakander", wakanderId);
        ReflectionTestUtils.setField(aula1, "idCurso", comeceAquiIdEsperado);

        when(jornadaWakanderRepository.buscaAulasPorIdCurso(comeceAquiIdEsperado))
                .thenReturn(List.of(aula1));

        Wakander wakander = mock(Wakander.class);

        aulaAssistidaProcessadorComeceAqui.processaEvento(aula1, wakander);

        verify(progressoWakanderRepository, never()).salvaProgresso(any(), any(), any());
        verify(wakander, never()).atualizaStatusJornada(any());
        verify(wakanderRepository, never()).save(any());
        verify(publicadorNotificacaoSns, never()).enviaNotificacaoSns(any(), any(), any());
    }

    @Test
    @DisplayName("Não deve processar evento quando Wakander assistir mais de 3 aulas")
    void processaEvento_NaoProcessaQuandoMaisDe3Aulas() {
        AulaAssistida aula1 = JornadaWakanderDataHelper.criaAulaAssistida();
        AulaAssistida aula2 = JornadaWakanderDataHelper.criaAulaAssistida();
        AulaAssistida aula3 = JornadaWakanderDataHelper.criaAulaAssistida();
        AulaAssistida aula4 = JornadaWakanderDataHelper.criaAulaAssistida();
        UUID wakanderId = aula1.getIdWakander();

        ReflectionTestUtils.setField(aula1, "idWakander", wakanderId);
        ReflectionTestUtils.setField(aula1, "idCurso", comeceAquiIdEsperado);
        ReflectionTestUtils.setField(aula2, "idWakander", wakanderId);
        ReflectionTestUtils.setField(aula2, "idCurso", comeceAquiIdEsperado);
        ReflectionTestUtils.setField(aula3, "idWakander", wakanderId);
        ReflectionTestUtils.setField(aula3, "idCurso", comeceAquiIdEsperado);
        ReflectionTestUtils.setField(aula4, "idWakander", wakanderId);
        ReflectionTestUtils.setField(aula4, "idCurso", comeceAquiIdEsperado);

        when(jornadaWakanderRepository.buscaAulasPorIdCurso(comeceAquiIdEsperado))
                .thenReturn(List.of(aula1, aula2, aula3, aula4));

        Wakander wakander = mock(Wakander.class);

        aulaAssistidaProcessadorComeceAqui.processaEvento(aula1, wakander);

        verify(progressoWakanderRepository, never()).salvaProgresso(any(), any(), any());
        verify(wakander, never()).atualizaStatusJornada(any());
        verify(wakanderRepository, never()).save(any());
        verify(publicadorNotificacaoSns, never()).enviaNotificacaoSns(any(), any(), any());
    }

}