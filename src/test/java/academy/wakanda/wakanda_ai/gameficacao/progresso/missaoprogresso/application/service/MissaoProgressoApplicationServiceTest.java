package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service;

import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.datahelper.MissaoWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.OrdemMissao;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service.JornadaProgressoService;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoConcluidaProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoConcluidaResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoDisponibilidadeResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoDisponibilidadeStatus;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoProgressoRequest;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoProgressoResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.datahelper.MissaoProgressoDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgressoStatus;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.SabedoriasMissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoGameficacaoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.dataHelper.ProgressoWakanderDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository.ClasseWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.datahelper.ClasseWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.application.repository.HistoricoClasseWakanderRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.datahelper.HistoricoClasseWakanderDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.service.XpWakanderService;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderDetalhadoResponse;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissaoProgressoApplicationServiceTest {

    @InjectMocks
    private MissaoProgressoApplicationService progressoApplicationService;

    @Mock
    private MissaoProgressoRepository missaoProgressoRepository;

    @Mock
    private MissaoWakandaRepository missaoWakandaRepository;

    @Mock
    private PublicadorNotificacaoSns publicadorNotificacaoSns;

    @Mock
    private TopicNames topicNames;

    @Mock
    private ProgressoGameficacaoRepository progressoGameficacaoRepository;

    @Mock
    private JornadaProgressoService jornadaProgressoService;

    @Mock
    private JornadaWakandaRepository jornadaWakandaRepository;

    @Mock
    private MissaoDisponibilidadeService missaoDisponibilidadeService;

    @Mock
    private WakanderService wakanderService;

    @Mock
    private XpWakanderService xpWakanderService;

    @BeforeEach
    void setup() {
        HistoricoClasseWakander historicoAtual = HistoricoClasseWakanderDataHelper
                .criarHistoricoClasseWakanderEmAndamento(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        ClasseWakanda classeAtual = ClasseWakandaDataHelper
                .criarClasseWakandaClasseAtual(historicoAtual.getIdClasse(), UUID.randomUUID(), UUID.randomUUID());
        lenient().when(missaoDisponibilidadeService.buscaClasseAtual(any())).thenReturn(classeAtual);
        lenient().doNothing().when(missaoDisponibilidadeService)
                .validaMissaoLiberadaParaClasseAtual(any(), any(), anyMap());
        lenient().when(missaoDisponibilidadeService.verificaSeMissaoLiberadaParaClasseAtual(any(), any(), anyMap()))
                .thenReturn(true);
    }

    @Test
    @DisplayName("Deve concluir missão com sucesso")
    void concluiMissaoComSucesso() {

        ProgressoWakanderEventDto progressoWakanderEventDto = MissaoProgressoDataHelper.criarProgressoWakanderEventDto();
        MissaoWakanda missaoWakanda = MissaoWakandaDataHelper.criaMissaoWakanda();
        ProgressoWakander progressoWakander = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        MissaoProgresso missaoProgresso = MissaoProgressoDataHelper.criarMissaoProgressoEmAndamento();
        MissaoWakanda proximaMissao = MissaoWakandaDataHelper.criaMissaoWakanda();

        when(missaoWakandaRepository.buscaMissaoPorIdMissaoExterna(any())).thenReturn(missaoWakanda);
        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(any())).thenReturn(progressoWakander);
        when(missaoProgressoRepository.buscaOptionalMissaoProgresso(any(), any())).thenReturn(Optional.of(missaoProgresso));
        when(topicNames.getXpWakanderRequests()).thenReturn("topic");
        when(missaoWakandaRepository.buscaMissoesOrdenadasPorJornada(any())).thenReturn(List.of(missaoWakanda));
        when(missaoWakandaRepository.buscaProximaMissao(any(), anyInt())).thenReturn(Optional.of(proximaMissao));
        when(missaoProgressoRepository.existeMissaoProgresso(any(), any())).thenReturn(false);

        progressoApplicationService.concluiMissao(progressoWakanderEventDto);

        verify(missaoWakandaRepository).buscaMissaoPorIdMissaoExterna(any());
        verify(progressoGameficacaoRepository).buscaProgressoPorIdWakander(any());
        assertEquals(missaoWakanda.getSabedorias().getTeorico(), missaoProgresso.getSabedorias().getTeorico());
        assertEquals(missaoWakanda.getSabedorias().getProcesso(), missaoProgresso.getSabedorias().getProcesso());
        assertEquals(missaoWakanda.getSabedorias().getKnowHow(), missaoProgresso.getSabedorias().getKnowHow());
        verify(publicadorNotificacaoSns).enviaNotificacaoSns(anyString(), any(), anyString());
        verify(jornadaProgressoService).concluiJornadaSeMissoesConcluidas(any(), any());
    }

    @Test
    @DisplayName("Deve falhar quando a missão já estiver concluída")
    void falhaQuandoMissaoJaConcluida() {

        ProgressoWakanderEventDto progressoWakanderEventDto = MissaoProgressoDataHelper.criarProgressoWakanderEventDto();
        ProgressoWakander progressoWakander = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        MissaoWakanda missaoWakanda = MissaoWakandaDataHelper.criaMissaoWakanda();
        MissaoProgresso missaoProgresso = MissaoProgressoDataHelper.criarMissaoProgressoConcluida();

        when(missaoWakandaRepository.buscaMissaoPorIdMissaoExterna(any())).thenReturn(missaoWakanda);
        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(any())).thenReturn(progressoWakander);
        when(missaoProgressoRepository.buscaOptionalMissaoProgresso(any(), any())).thenReturn(Optional.of(missaoProgresso));

        APIException exception = assertThrows(APIException.class, () -> {
            progressoApplicationService.concluiMissao(progressoWakanderEventDto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals("A missão já está concluída.", exception.getMessage());
    }

    @Test
    @DisplayName("ProcessaPróximaMissao: deve liberar próxima missão quando não existir progresso")
    void processaProximaDeveLiberarQuandoNaoExisteProgresso() {
        ProgressoWakanderEventDto evento = MissaoProgressoDataHelper.criarProgressoWakanderEventDto();
        MissaoWakanda missaoAtual = MissaoWakandaDataHelper.criaMissaoWakanda();
        MissaoWakanda proximaMissao = MissaoWakandaDataHelper.criaMissaoWakanda();
        ProgressoWakander progressoWakander = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        MissaoProgresso progressoAtual = MissaoProgressoDataHelper.criarMissaoProgressoEmAndamento();

        when(missaoWakandaRepository.buscaMissaoPorIdMissaoExterna(any())).thenReturn(missaoAtual);
        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(any())).thenReturn(progressoWakander);
        when(missaoProgressoRepository.buscaOptionalMissaoProgresso(any(), any())).thenReturn(Optional.of(progressoAtual));
        when(topicNames.getXpWakanderRequests()).thenReturn("topic");
        when(missaoWakandaRepository.buscaMissoesOrdenadasPorJornada(any())).thenReturn(List.of(missaoAtual));
        when(missaoWakandaRepository.buscaProximaMissao(any(), anyInt())).thenReturn(Optional.of(proximaMissao));
        when(missaoProgressoRepository.existeMissaoProgresso(any(), any())).thenReturn(false);

        progressoApplicationService.concluiMissao(evento);

        verify(missaoProgressoRepository, times(2)).salvaProgressoMissao(any(MissaoProgresso.class));
    }

    @Test
    @DisplayName("ValidaSequencia: deve lançar 'Missão Fora de Sequência' quando não há próxima esperada e existe missão posterior")
    void validaSequenciaDeveLancarQuandoForaDeSequencia() {
        ProgressoWakanderEventDto evento = MissaoProgressoDataHelper.criarProgressoWakanderEventDto();
        MissaoWakanda atual = MissaoWakandaDataHelper.criaMissaoWakanda();
        MissaoWakanda posterior = mock(academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda.class);
        when(posterior.getOrdemMissao()).thenReturn(OrdemMissao.criar(atual.getOrdemMissao().getOrdem() + 2));

        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        MissaoProgresso progressoAtual = MissaoProgressoDataHelper.criarMissaoProgressoEmAndamento();

        when(missaoWakandaRepository.buscaMissaoPorIdMissaoExterna(any())).thenReturn(atual);
        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(any())).thenReturn(progresso);
        when(missaoProgressoRepository.buscaOptionalMissaoProgresso(any(), any())).thenReturn(Optional.of(progressoAtual));
        when(missaoWakandaRepository.buscaMissoesOrdenadasPorJornada(any())).thenReturn(List.of(atual, posterior));
        when(missaoWakandaRepository.buscaProximaMissao(any(), anyInt())).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class, () -> progressoApplicationService.concluiMissao(evento));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        assertEquals("Missão Fora de Sequência", ex.getMessage());
    }

    @Test
    @DisplayName("LiberaProximaMissao: não deve criar quando já existir progresso para a próxima missão")
    void liberaProximaNaoCriaQuandoJaExisteProgresso() {
        ProgressoWakanderEventDto evento = MissaoProgressoDataHelper.criarProgressoWakanderEventDto();
        MissaoWakanda atual = MissaoWakandaDataHelper.criaMissaoWakanda();
        MissaoWakanda proxima = MissaoWakandaDataHelper.criaMissaoWakanda();
        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        MissaoProgresso progressoAtual = MissaoProgressoDataHelper.criarMissaoProgressoEmAndamento();

        when(missaoWakandaRepository.buscaMissaoPorIdMissaoExterna(any())).thenReturn(atual);
        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(any())).thenReturn(progresso);
        when(missaoProgressoRepository.buscaOptionalMissaoProgresso(any(), any())).thenReturn(Optional.of(progressoAtual));
        when(topicNames.getXpWakanderRequests()).thenReturn("topic");
        when(missaoWakandaRepository.buscaMissoesOrdenadasPorJornada(any())).thenReturn(List.of(atual, proxima));
        when(missaoWakandaRepository.buscaProximaMissao(any(), anyInt())).thenReturn(Optional.of(proxima));
        when(missaoProgressoRepository.existeMissaoProgresso(any(), any())).thenReturn(true);

        progressoApplicationService.concluiMissao(evento);

        verify(missaoProgressoRepository, never()).salvaProgressoMissao(
                argThat(m -> m.getIdMissaoWakanda().equals(proxima.getIdMissao()))
        );
    }

    @Test
    @DisplayName("Deve criar progresso de missão com sucesso")
    void criaProgressoDeMissaoComSucesso() {
        UUID idMissao = UUID.randomUUID();
        UUID idWakander = ProgressoWakanderDataHelper.getIdWakander();

        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(idMissao, UUID.randomUUID());
        ProgressoWakander progressoWakander = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();

        when(missaoWakandaRepository.buscaMissaoPorId(idMissao)).thenReturn(missao);
        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(idWakander)).thenReturn(progressoWakander);

        MissaoProgressoRequest request = MissaoProgressoDataHelper.criarMissaoProgressoRequest(idMissao, idWakander);

        MissaoProgressoResponse response = progressoApplicationService.criaProgressoDeMissao(request);

        assertEquals(request.getIdMissao(), response.getIdMissaoWakanda());
        verify(missaoProgressoRepository).salvaProgressoMissao(any(MissaoProgresso.class));
    }

    @Test
    @DisplayName("Deve falhar quando missão não existe ao criar um progresso de missão")
    void criaProgressoDeMissaoFalhaQuandoMissaoNaoExiste() {
        UUID idMissao = UUID.randomUUID();
        UUID idWakander = ProgressoWakanderDataHelper.getIdWakander();
        MissaoProgressoRequest request = MissaoProgressoDataHelper.criarMissaoProgressoRequest(idMissao, idWakander);

        when(missaoWakandaRepository.buscaMissaoPorId(idMissao))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Missão não encontrada"));

        APIException ex = assertThrows(APIException.class,
                () -> progressoApplicationService.criaProgressoDeMissao(request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Missão não encontrada", ex.getMessage());
        verify(missaoProgressoRepository, never()).salvaProgressoMissao(any());
    }

    @Test
    @DisplayName("Deve concluir missão com sucesso")
    void concluiMissaoManualmenteComSucesso() {
        MissaoProgresso missaoProgresso = MissaoProgressoDataHelper.criarMissaoProgressoEmAndamento();
        MissaoWakanda missaoWakanda = MissaoWakandaDataHelper.criaMissaoWakanda();

        when(missaoProgressoRepository.buscaMissaoProgressoPorId(missaoProgresso.getIdMissaoProgresso()))
                .thenReturn(missaoProgresso);
        when(missaoWakandaRepository.buscaMissaoPorId(missaoProgresso.getIdMissaoWakanda()))
                .thenReturn(missaoWakanda);
        when(progressoGameficacaoRepository.buscaProgressoPorId(missaoProgresso.getIdProgressoWakander()))
                .thenReturn(ProgressoWakanderDataHelper.getProgressoWakanderIniciante());
        when(topicNames.getXpWakanderRequests()).thenReturn("topic");

        progressoApplicationService.concluiMissaoManualmente(missaoProgresso.getIdMissaoProgresso());

        assertEquals(MissaoProgressoStatus.CONCLUIDA, missaoProgresso.getStatusProgresso());
        assertEquals(missaoWakanda.getXpBase().intValue(), missaoProgresso.getXpObtido());
        assertEquals(missaoWakanda.getSabedorias().getTeorico(), missaoProgresso.getSabedorias().getTeorico());
        assertEquals(missaoWakanda.getSabedorias().getProcesso(), missaoProgresso.getSabedorias().getProcesso());
        assertEquals(missaoWakanda.getSabedorias().getKnowHow(), missaoProgresso.getSabedorias().getKnowHow());
        verify(missaoProgressoRepository).salvaProgressoMissao(missaoProgresso);
        verify(publicadorNotificacaoSns).enviaNotificacaoSns(anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Deve listar missões concluídas com sabedorias ganhas")
    void deveListarMissoesConcluidasComSabedoriasGanhas() {
        UUID idProgressoWakander = UUID.randomUUID();
        UUID idMissaoProgresso = UUID.randomUUID();
        UUID idMissao = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        MissaoConcluidaProjection projection = mock(MissaoConcluidaProjection.class);

        when(projection.getIdMissaoProgresso()).thenReturn(idMissaoProgresso);
        when(projection.getIdMissao()).thenReturn(idMissao);
        when(projection.getTitulo()).thenReturn("Missão Java Básico");
        when(projection.getXpObtido()).thenReturn(100);
        when(projection.getSabTeorico()).thenReturn(0);
        when(projection.getSabProcesso()).thenReturn(5);
        when(projection.getSabKnowHow()).thenReturn(5);
        when(projection.getSabComportamental()).thenReturn(0);
        when(projection.getSabCriativo()).thenReturn(0);
        when(missaoProgressoRepository.listaMissoesConcluidasPorProgresso(idProgressoWakander, pageable))
                .thenReturn(new PageImpl<>(List.of(projection), pageable, 1));

        Page<MissaoConcluidaResponse> response = progressoApplicationService
                .listaMissoesConcluidas(idProgressoWakander, pageable);

        assertEquals(1, response.getContent().size());
        assertEquals(idMissaoProgresso, response.getContent().get(0).getIdMissaoProgresso());
        assertEquals(idMissao, response.getContent().get(0).getIdMissao());
        assertEquals("Missão Java Básico", response.getContent().get(0).getTitulo());
        assertEquals(100, response.getContent().get(0).getXpObtido());
        assertEquals(5, response.getContent().get(0).getSabedoriasGanhas().getProcesso().intValue());
        assertEquals(5, response.getContent().get(0).getSabedoriasGanhas().getKnowHow().intValue());
        verify(missaoProgressoRepository).listaMissoesConcluidasPorProgresso(idProgressoWakander, pageable);
    }

    @Test
    @DisplayName("Deve falhar quando missão já estiver concluída")
    void concluiMissaoManualmenteFalhaQuandoJaConcluida() {
        MissaoProgresso missaoProgresso = MissaoProgressoDataHelper.criarMissaoProgressoConcluida();
        MissaoWakanda missaoWakanda = MissaoWakandaDataHelper.criaMissaoWakanda();

        when(missaoProgressoRepository.buscaMissaoProgressoPorId(missaoProgresso.getIdMissaoProgresso()))
                .thenReturn(missaoProgresso);
        when(missaoWakandaRepository.buscaMissaoPorId(missaoProgresso.getIdMissaoWakanda()))
                .thenReturn(missaoWakanda);
        when(progressoGameficacaoRepository.buscaProgressoPorId(missaoProgresso.getIdProgressoWakander()))
                .thenReturn(ProgressoWakanderDataHelper.getProgressoWakanderIniciante());

        APIException ex = assertThrows(APIException.class,
                () -> progressoApplicationService.concluiMissaoManualmente(missaoProgresso.getIdMissaoProgresso()));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
        assertEquals("A missão já está concluída.", ex.getMessage());
        verify(missaoProgressoRepository, never()).salvaProgressoMissao(any());
        verify(publicadorNotificacaoSns, never()).enviaNotificacaoSns(anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Não deve criar progresso quando missão está bloqueada por classe mínima")
    void naoDeveCriarProgressoQuandoMissaoBloqueadaPorClasse() {
        UUID idMissao = UUID.randomUUID();
        UUID idWakander = ProgressoWakanderDataHelper.getIdWakander();
        UUID idClasseAtual = UUID.randomUUID();

        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(idMissao, UUID.randomUUID());
        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();

        ClasseWakanda classeAtual = ClasseWakandaDataHelper.criarClasseWakandaClasseAtual(idClasseAtual, UUID.randomUUID(), UUID.randomUUID());

        when(missaoWakandaRepository.buscaMissaoPorId(idMissao)).thenReturn(missao);
        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(idWakander)).thenReturn(progresso);
        when(missaoDisponibilidadeService.buscaClasseAtual(idWakander)).thenReturn(classeAtual);
        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "Missão não está liberada para a classe atual"))
                .when(missaoDisponibilidadeService)
                .validaMissaoLiberadaParaClasseAtual(eq(missao), eq(classeAtual), anyMap());

        MissaoProgressoRequest request = MissaoProgressoDataHelper.criarMissaoProgressoRequest(idMissao, idWakander);

        APIException ex = assertThrows(APIException.class, () -> progressoApplicationService.criaProgressoDeMissao(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
        assertEquals("Missão não está liberada para a classe atual", ex.getMessage());
        verify(missaoProgressoRepository, never()).salvaProgressoMissao(any());
    }

    @Test
    @DisplayName("Deve liberar missões elegíveis ao reavaliar após promoção")
    void deveLiberarMissoesElegiveisAoReavaliar() {
        UUID idWakander = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();

        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        ClasseWakanda classeAtual = ClasseWakandaDataHelper.criarClasseWakandaProximaClasse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        MissaoWakanda missaoElegivel = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(UUID.randomUUID(), idJornada);

        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(idWakander)).thenReturn(progresso);
        when(missaoDisponibilidadeService.buscaClasseAtual(idWakander)).thenReturn(classeAtual);
        when(missaoDisponibilidadeService.verificaSeMissaoLiberadaParaClasseAtual(eq(missaoElegivel), eq(classeAtual), anyMap()))
                .thenReturn(true);
        when(jornadaWakandaRepository.listaJornadas()).thenReturn(List.of(
                new JornadaWakanda(idJornada, "Jornada", "Desc", 100, UUID.randomUUID(), StatusJornada.ATIVA, 0, 1)
        ));
        when(missaoWakandaRepository.buscaMissoesOrdenadasPorJornada(idJornada)).thenReturn(List.of(missaoElegivel));
        when(missaoProgressoRepository.buscaMissoesProgressoPorIdsMissoesEProgresso(List.of(missaoElegivel.getIdMissao()), progresso.getIdProgressoWakander()))
                .thenReturn(List.of());

        progressoApplicationService.reavaliaMissoesDisponiveis(idWakander);

        verify(missaoProgressoRepository, times(1)).salvaProgressoMissao(any(MissaoProgresso.class));
        verify(missaoProgressoRepository, never()).existeMissaoProgresso(any(), any());
    }

    @Test
    @DisplayName("Deve propagar falha ao buscar a classe atual do Wakander")
    void devePropagarFalhaAoBuscarClasseAtualDoWakander() {
        UUID idMissao = UUID.randomUUID();
        UUID idWakander = ProgressoWakanderDataHelper.getIdWakander();

        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(idMissao, UUID.randomUUID());
        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();

        when(missaoWakandaRepository.buscaMissaoPorId(idMissao)).thenReturn(missao);
        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(idWakander)).thenReturn(progresso);
        when(missaoDisponibilidadeService.buscaClasseAtual(idWakander))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Classe atual do Wakander não encontrada!"));

        MissaoProgressoRequest request = MissaoProgressoDataHelper.criarMissaoProgressoRequest(idMissao, idWakander);

        APIException ex = assertThrows(APIException.class, () -> progressoApplicationService.criaProgressoDeMissao(request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Classe atual do Wakander não encontrada!", ex.getMessage());
        verify(missaoProgressoRepository, never()).salvaProgressoMissao(any());
    }

    @Test
    @DisplayName("Deve retornar missão INATIVA como BLOQUEADA mesmo com progresso")
    void deveRetornarMissaoInativaComoBloqueadaMesmoComProgresso() {
        UUID idWakander = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();
        UUID idClasseAtual = UUID.randomUUID();
        UUID idProgressoWakander = UUID.randomUUID();

        ProgressoWakander progresso = mock(ProgressoWakander.class);
        when(progresso.getIdProgressoWakander()).thenReturn(idProgressoWakander);

        ClasseWakanda classeAtual = ClasseWakandaDataHelper
                .criarClasseWakandaClasseAtual(idClasseAtual, UUID.randomUUID(), UUID.randomUUID());

        MissaoWakanda missaoInativa = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(UUID.randomUUID(), idJornada);
        ReflectionTestUtils.setField(missaoInativa, "missaoStatus", MissaoStatus.INATIVA);

        MissaoProgresso missaoProgresso = new MissaoProgresso(
                UUID.randomUUID(),
                missaoInativa.getIdMissao(),
                idProgressoWakander,
                MissaoProgressoStatus.EM_ANDAMENTO,
                0,
                0,
                null,
                null,
                new SabedoriasMissaoProgresso(0, 0, 0, 0, 0)
        );

        when(progressoGameficacaoRepository.buscaProgressoPorIdWakander(idWakander)).thenReturn(progresso);
        when(missaoDisponibilidadeService.buscaClasseAtual(idWakander)).thenReturn(classeAtual);
        when(missaoWakandaRepository.buscaMissoesOrdenadasPorJornada(idJornada)).thenReturn(List.of(missaoInativa));
        when(missaoProgressoRepository.buscaMissoesProgressoPorIdsMissoesEProgresso(anyList(), eq(idProgressoWakander)))
                .thenReturn(List.of(missaoProgresso));
        when(missaoDisponibilidadeService.montaDisponibilidadeMissao(eq(missaoInativa), eq(classeAtual), eq(true), anyMap()))
                .thenReturn(MissaoDisponibilidadeResponse.builder()
                        .idMissao(missaoInativa.getIdMissao())
                        .titulo(missaoInativa.getTitulo())
                        .statusDisponibilidade(MissaoDisponibilidadeStatus.BLOQUEADA)
                        .build());

        List<MissaoDisponibilidadeResponse> resposta = progressoApplicationService
                .listaDisponibilidadeMissoes(idWakander, idJornada);

        assertEquals(1, resposta.size());
        assertEquals(MissaoDisponibilidadeStatus.BLOQUEADA, resposta.get(0).getStatusDisponibilidade());
    }
}
