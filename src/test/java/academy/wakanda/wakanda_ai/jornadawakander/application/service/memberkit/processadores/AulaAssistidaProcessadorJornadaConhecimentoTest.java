package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores;

import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AulaAssistidaProcessadorJornadaConhecimentoTest {

    @InjectMocks
    AulaAssistidaProcessadorJornadaConhecimento processadorJornadaConhecimento;

    @Mock
    WakanderRepository wakanderRepository;

    @Mock
    PublicadorNotificacaoSns publicadorNotificacaoSns;

    @Mock
    OnboardingWakanderService onboardingWakanderService;

    @Mock
    TopicNames topicNames;

    @BeforeEach
    void setUp() {
        loadTemplates(Templates.BASE_PACKAGE);
        ReflectionTestUtils.setField(processadorJornadaConhecimento, "idPrimeiraAulaJornadaConhecimento", "101");
    }

    @Test
    void deveRetornarTrueSeWakanderEstaNoOnboardEidAulaEhAPrimeira() {
        AulaAssistida aulaAssistida = DataHelper.criaAulaAssistidaJornadaConhecimento();
        Wakander wakander = DataHelper.criaWakanderStatusFinalizouComeceAqui();

        boolean resultado = processadorJornadaConhecimento.validaSeEventoProcessa(aulaAssistida, wakander);

        assertEquals(JornadaWakanda.FINALIZOU_COMECE_AQUI, wakander.getJornadaAtual());
        assertTrue(resultado);
    }

    @Test
    void naoDeveProcessarSeWakanderNaoEstaOnboardENaoForPrimeiraAula() {
        AulaAssistida aulaAssistida = DataHelper.criaAulaAssistida();
        Wakander wakander = DataHelper.criaWakander();
        boolean resultado = processadorJornadaConhecimento.validaSeEventoProcessa(aulaAssistida, wakander);

        assertFalse(resultado);
    }

    @Test
    void deveAtualizarProgressoDoWakanderEnviarMensagem() {
        AulaAssistida aulaAssistida = DataHelper.criaAulaAssistidaJornadaConhecimento();
        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        OnboardingWakander onboardingWakander = DataHelper.criaOnboardingWakander(wakander);

        when(wakanderRepository.save(wakander)).thenReturn(wakander);
        when(onboardingWakanderService.buscaOnboardingPorIdWakander(wakander.getIdWakander()))
                .thenReturn(onboardingWakander);
        when(onboardingWakanderService.retornaChecklist(onboardingWakander))
                .thenReturn("Mensagem de checklist");
        doNothing().when(onboardingWakanderService).save(onboardingWakander);
        doNothing().when(publicadorNotificacaoSns).enviaNotificacaoSns(any(), any(), any());

        processadorJornadaConhecimento.processaEvento(aulaAssistida, wakander);

        assertEquals(JornadaWakanda.JORNADA_CONHECIMENTO, wakander.getJornadaAtual());
        verify(wakanderRepository, times(1)).save(wakander);
        verify(onboardingWakanderService, times(1)).buscaOnboardingPorIdWakander(wakander.getIdWakander());
        verify(onboardingWakanderService, times(1)).save(onboardingWakander);
        verify(publicadorNotificacaoSns, times(1)).enviaNotificacaoSns(any(),any(),any());
    }


}
