package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.AulaMemberKitDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.JornadaWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AulaAssistidaApplicationServiceTest {

    @InjectMocks
    private AulaAssistidaApplicationService aulaAssistidaApplicationService;

    @Mock
    private WakanderRepository wakanderService;
    @Mock
    private JornadaWakanderRepository jornadaWakanderRepository;
    @Mock
    private AulaAssistidaProcessador aulaAssistidaProcessador;
    @Mock
    private AulaAssistidaProcessador processador1;
    @Mock
    private AulaAssistidaProcessador processador2;
    @Mock
    private MissaoWakandaRepository missaoWakandaRepository;
    @Mock
    private PublicadorNotificacaoSns publicadorNotificacaoSns;
    @Mock
    private TopicNames topicNames;

    private static final String MISSING_MISSION = "101";

    @BeforeEach
    void setUp() {
        List<AulaAssistidaProcessador> processadores = List.of(aulaAssistidaProcessador, processador1, processador2);
        ReflectionTestUtils.setField(aulaAssistidaApplicationService, "processadoresAulaAssistida", processadores);
        loadTemplates(Templates.BASE_PACKAGE);
    }

    @Test
    @DisplayName("Deve processar evento de aula assistida quando houver processador valido")
    void deveProcessarEventoAulaAssistidaComProcessadorValido() {
        AulaMemberKitDTO aulaMemberKitDTO = DataHelper.criaAulaMemberKitDTOJornadaConhecimento();
        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        AulaAssistida aulaAssistida = DataHelper.criaAulaAssistida();

        when(wakanderService.buscaWakanderPorIdMemberKit(any())).thenReturn(wakander);
        when(wakanderService.save(any(Wakander.class))).thenReturn(wakander);
        when(jornadaWakanderRepository.save(any(AulaAssistida.class))).thenReturn(aulaAssistida);
        when(processador1.validaSeEventoProcessa(any(AulaAssistida.class), any(Wakander.class))).thenReturn(true);
        when(missaoWakandaRepository.buscaIdMissaoWakandaExterno(anyString()))
                .thenReturn(Optional.empty());

        aulaAssistidaApplicationService.processaEventoAulaAssistida(aulaMemberKitDTO);

        verify(processador1, times(1)).validaSeEventoProcessa(any(AulaAssistida.class), any(Wakander.class));
        verify(processador1, times(1)).processaEvento(any(AulaAssistida.class), any(Wakander.class));
    }

    @Test
    @DisplayName("Deve finalizar missao e gerar XP quando aula estiver ligada a uma missao")
    void deveFinalizarMissaoEGerarXP_quandoAulaEstiverLigadaAMissao() {
        AulaMemberKitDTO aulaMemberKitDTO = DataHelper.criaAulaMemberKitDTOJornadaConhecimento();
        Wakander wakander = DataHelper.criaWakander();
        AulaAssistida aulaAssistida = DataHelper.criaAulaAssistida();
        UUID idWakander = UUID.randomUUID();
        ReflectionTestUtils.setField(wakander, "idWakander", idWakander);
        UUID idMissao = UUID.randomUUID();

        when(wakanderService.buscaWakanderPorIdMemberKit(any())).thenReturn(wakander);
        when(wakanderService.save(any(Wakander.class))).thenReturn(wakander);
        when(jornadaWakanderRepository.save(any(AulaAssistida.class))).thenReturn(aulaAssistida);

        var missao = mock(MissaoWakanda.class);
        when(missao.getIdMissao()).thenReturn(idMissao);
        when(missaoWakandaRepository.buscaIdMissaoWakandaExterno(MISSING_MISSION)).thenReturn(Optional.of(missao));

        when(topicNames.getProgressoWakanderRequests()).thenReturn("progresso-topic");
        doNothing().when(publicadorNotificacaoSns).enviaNotificacaoSns(anyString(), any(), anyString());

        aulaAssistidaApplicationService.processaEventoAulaAssistida(aulaMemberKitDTO);

        verify(publicadorNotificacaoSns).enviaNotificacaoSns(eq(idWakander.toString()), any(ProgressoWakanderEventDto.class), eq("progresso-topic"));
    }
}
