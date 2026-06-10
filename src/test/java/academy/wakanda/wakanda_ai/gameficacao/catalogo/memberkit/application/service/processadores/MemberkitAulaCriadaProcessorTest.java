package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitConfig;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitLessonWebhookDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitClientService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitEventType;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.datahelper.MemberkitDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoExternaDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service.MissaoWakandaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service.TipoMissaoRepository;
import academy.wakanda.wakanda_ai.handler.APIException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberkitAulaCriadaProcessorTest {

    @Mock
    private MissaoWakandaService missaoWakandaService;
    @Mock
    private MissaoWakandaRepository missaoWakandaRepository;
    @Mock
    private TipoMissaoRepository tipoMissaoRepository;
    @Mock
    private JornadaWakandaRepository jornadaWakandaRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private MemberkitClientService memberkitClientService;

    @Mock
    private MemberkitConfig memberkitConfig;

    private MemberkitAulaCriadaProcessor processor;

    @BeforeEach
    void setup() {
        processor = new MemberkitAulaCriadaProcessor(objectMapper, missaoWakandaService, missaoWakandaRepository,
                tipoMissaoRepository, jornadaWakandaRepository, memberkitClientService, memberkitConfig);
        ReflectionTestUtils.setField(processor, "jornadaWakanda", "Jornada Teste");
        ReflectionTestUtils.setField(processor, "tipoMissaoWakanda", "Tipo Missao Teste");
    }


    @Test
    @DisplayName("Deve validar evento quando tipo aula criada")
    void deveValidarEventoProcessaQuandoTipoAulaCriada() {
        assertTrue(processor.validaSeEventoProcessa(MemberkitEventType.AULA_CRIADA.getDescricao()));
    }

    @Test
    @DisplayName("Nao deve validar evento quando tipo diferente")
    void naoDeveValidarEventoProcessaQuandoTipoDiferente() {
        assertFalse(processor.validaSeEventoProcessa("outro.tipo"));
    }

    @Test
    @DisplayName("Deve processar evento criando missao filha quando missao pai existir")
    void deveProcessarEventoCriandoMissaoFilha_quandoMissaoPaiExiste() {
        UUID tipoId = UUID.randomUUID();
        UUID jornadaId = UUID.randomUUID();
        UUID missaoPaiId = UUID.randomUUID();

        when(tipoMissaoRepository.buscaTipoMissaoPorDescricao("Tipo Missao Teste"))
                .thenReturn(MemberkitDataHelper.criarTipoMissaoComId(tipoId));
        when(jornadaWakandaRepository.buscaJornadaPorTitulo("Jornada Teste"))
                .thenReturn(MemberkitDataHelper.criarJornadaWakandaComId(jornadaId));
        when(missaoWakandaRepository.buscaIdMissaoWakandaExterno("curso123"))
                .thenReturn(Optional.ofNullable(MemberkitDataHelper.criarMissaoWakandaComId(missaoPaiId)));
        when(missaoWakandaRepository.buscaIdMissaoWakandaExterno("aula123")).thenReturn(Optional.empty());

        MemberkitLessonWebhookDTO dto = MemberkitDataHelper.criaAula("aula123", "Titulo Aula", "curso123",
                "Curso Teste", "");
        MemberkitEventRequest request = new MemberkitEventRequest("lesson.created", dto.getData());
        when(objectMapper.convertValue(eq(request), eq(MemberkitLessonWebhookDTO.class))).thenReturn(dto);

        processor.processaEvento(request);

        MissaoExternaDTO esperado = MemberkitDataHelper.criarMissaoExterna("aula123", "Titulo Aula", tipoId, jornadaId,
                missaoPaiId, "Sem descrição disponível");

        verify(missaoWakandaService).criaMissaoExterna(eq(esperado), eq(false));
    }

    @Test
    @DisplayName("Deve processar evento criando missao pai e filha quando missao pai nao existir")
    void deveProcessarEventoCriandoMissaoFilha_quandoMissaoPaiNaoExiste_criaPaiAntes() {
        UUID tipoId = UUID.randomUUID();
        UUID jornadaId = UUID.randomUUID();
        UUID missaoPaiIdCriado = UUID.randomUUID();

        when(tipoMissaoRepository.buscaTipoMissaoPorDescricao("Tipo Missao Teste"))
                .thenReturn(MemberkitDataHelper.criarTipoMissaoComId(tipoId));
        when(jornadaWakandaRepository.buscaJornadaPorTitulo("Jornada Teste"))
                .thenReturn(MemberkitDataHelper.criarJornadaWakandaComId(jornadaId));
        when(missaoWakandaRepository.buscaIdMissaoWakandaExterno("curso123")).thenReturn(Optional.empty());
        when(missaoWakandaRepository.buscaIdMissaoWakandaExterno("aula123")).thenReturn(Optional.empty());

        MissaoWakanda missaoPai = MemberkitDataHelper.criarMissaoWakandaComId(missaoPaiIdCriado);
        when(missaoWakandaService.criaMissaoExterna(any(MissaoExternaDTO.class), eq(false))).thenReturn(missaoPai);

        MemberkitLessonWebhookDTO dto = MemberkitDataHelper.criaAula("aula123", "Titulo Aula", "curso123",
                "Curso Teste", "");
        MemberkitEventRequest request = new MemberkitEventRequest("lesson.created", dto.getData());
        when(objectMapper.convertValue(eq(request), eq(MemberkitLessonWebhookDTO.class))).thenReturn(dto);
        processor.processaEvento(request);

        MissaoExternaDTO esperadoPai = MemberkitDataHelper.criarMissaoExterna("curso123", "Curso Teste", tipoId,
                jornadaId, null, "Sem descrição disponível");
        MissaoExternaDTO esperadoFilha = MemberkitDataHelper.criarMissaoExterna("aula123", "Titulo Aula", tipoId,
                jornadaId, missaoPaiIdCriado, "Sem descrição disponível");

        verify(missaoWakandaService).criaMissaoExterna(eq(esperadoPai), eq(false));
        verify(missaoWakandaService).criaMissaoExterna(eq(esperadoFilha), eq(false));
    }

    @Test
    @DisplayName("Deve lancar ApiException quando missao ja existir")
    void deveLancarApiExceptionQuandoMissaoJaExiste() {
        UUID missaoIdExistente = UUID.randomUUID();

        when(missaoWakandaRepository.buscaIdMissaoWakandaExterno("aula123"))
                .thenReturn(Optional.of(MemberkitDataHelper.criarMissaoWakandaComId(missaoIdExistente)));

        MemberkitLessonWebhookDTO dto = MemberkitDataHelper.criaAula("aula123", "Titulo Aula", "curso123",
                "Curso Teste", "");
        MemberkitEventRequest request = new MemberkitEventRequest("lesson.created", dto.getData());

        when(objectMapper.convertValue(eq(request), eq(MemberkitLessonWebhookDTO.class))).thenReturn(dto);

        APIException ex = assertThrows(APIException.class, () -> processor.processaEvento(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        assertTrue(ex.getMessage().contains("Missão já existe: aula123"));
    }

}
