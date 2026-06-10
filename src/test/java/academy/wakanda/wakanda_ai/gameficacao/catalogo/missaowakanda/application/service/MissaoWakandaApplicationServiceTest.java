package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.datahelper.JornadaWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.*;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.datahelper.MissaoWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.*;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service.TipoMissaoService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.datahelper.TipoMissaoDataHelper;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MissaoWakandaApplicationServiceTest {

    @InjectMocks
    private MissaoWakandaApplicationService missaoWakandaApplicationService;

    @Mock
    private MissaoWakandaRepository missaoRepository;

    @Mock
    private JornadaWakandaService jornadaService;

    @Mock
    private MissaoWakandaService missaoService;

    private MissaoWakandaRequest requestValida;

    @Mock
    private TipoMissaoService tipoMissaoService;

    @Mock
    private MissaoWebhookService missaoWebhookService;

    @Mock
    private MissaoWakanda missaoWakanda;

    private UUID idMissao;

    private UUID jornadaId;

    @BeforeEach
    void setUp() {
        idMissao = UUID.randomUUID();
        jornadaId = UUID.randomUUID();
        requestValida = MissaoWakandaDataHelper.criarMissaoValida();
        lenient().doNothing().when(jornadaService).recalculaXpJornada(any(UUID.class), any(TipoRecalculo.class),
                anyInt(), anyInt());

        // sem cache externo; o serviço usa cache interno

    }

    @Test
    @DisplayName("Deve criar missão com sucesso")
    void criaMissaoComSucesso() {
        JornadaResponse jornadaAtiva = JornadaWakandaDataHelper.criarJornadaResponseAtiva();
        TipoMissaoResponse tipoMissaoValido = TipoMissaoDataHelper.criarTipoMissaoResponseValido();
        MissaoWakandaRequest missaoValida = MissaoWakandaDataHelper.criarMissaoValida();

        when(jornadaService.buscaJornada(any(UUID.class))).thenReturn(jornadaAtiva);
        when(tipoMissaoService.buscaTipoMissaoId(any(UUID.class))).thenReturn(tipoMissaoValido);
        when(missaoRepository.validaTitulo(anyString())).thenReturn(false);
        MissaoWakandaResponse response = missaoWakandaApplicationService.criaMissao(missaoValida);

        Assertions.assertNotNull(response);
        verify(jornadaService).buscaJornada(missaoValida.getIdJornada());
        verify(tipoMissaoService).buscaTipoMissaoId(missaoValida.getIdTipoMissao());
        verify(missaoRepository).validaTitulo(missaoValida.getTitulo());
        verify(missaoRepository).salvaMissao(any(MissaoWakanda.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar missão com título duplicado")
    void criaMissaoComTituloDuplicado() {
        when(missaoRepository.validaTitulo(requestValida.getTitulo())).thenReturn(true);
        when(jornadaService.buscaJornada(requestValida.getIdJornada()))
                .thenReturn(JornadaWakandaDataHelper.criarJornadaResponseAtiva());

        APIException exception = Assertions.assertThrows(APIException.class, () -> {
            missaoWakandaApplicationService.criaMissao(requestValida);
        });

        Assertions.assertEquals("Já existe uma missão com esse título!", exception.getMessage());
        verify(missaoRepository).validaTitulo(requestValida.getTitulo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar missão com XP inválido")
    void criaMissaoComXPInvalido() {
        MissaoWakandaRequest requestInvalido = MissaoWakandaDataHelper.criarMissaoXPRequestInvalido();

        when(jornadaService.buscaJornada(any(UUID.class)))
                .thenReturn(JornadaWakandaDataHelper.criarJornadaResponseAtiva());

        APIException exception = Assertions.assertThrows(APIException.class, () -> {
            missaoWakandaApplicationService.criaMissao(requestInvalido);
        });

        Assertions.assertEquals("XPBase deve ser informado e maior que zero!", exception.getMessage());

        verify(missaoRepository, never()).salvaMissao(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar missão com jornada inativa")
    void criaMissaoComJornadaInativa() {
        JornadaResponse jornadaInativa = JornadaWakandaDataHelper.criarJornadaResponseInativa();
        when(jornadaService.buscaJornada(requestValida.getIdJornada())).thenReturn(jornadaInativa);

        APIException exception = Assertions.assertThrows(APIException.class, () -> {
            missaoWakandaApplicationService.criaMissao(requestValida);
        });

        Assertions.assertEquals("Jornada não está ativa!", exception.getMessage());
        verify(jornadaService).buscaJornada(requestValida.getIdJornada());
    }

    @Test
    @DisplayName("Deve buscar missão por ID com sucesso")
    void buscaMissaoPorIdComSucesso() {
        UUID idMissao = UUID.randomUUID();
        MissaoWakanda missaoWakanda = MissaoWakandaDataHelper.criaMissaoWakanda();

        when(missaoRepository.buscaMissaoPorId(idMissao)).thenReturn(missaoWakanda);

        MissaoWakandaDetalhadoResponse response = missaoWakandaApplicationService.buscaMissaoPorId(idMissao);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(missaoWakanda.getIdJornada(), response.getIdJornada());

        verify(missaoRepository).buscaMissaoPorId(idMissao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar missão por ID")
    void deveLancarExcecaoAoNaoEncontrarMissaoPorId() {
        UUID idMissao = UUID.randomUUID();
        when(missaoRepository.buscaMissaoPorId(idMissao))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Missão não encontrada"));

        APIException exception = Assertions.assertThrows(APIException.class, () -> {
            missaoWakandaApplicationService.buscaMissaoPorId(idMissao);
        });

        Assertions.assertEquals("Missão não encontrada", exception.getMessage());

        verify(missaoRepository).buscaMissaoPorId(idMissao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar missão sem sabedorias")
    void criaMissaoSemSabedorias() {
        MissaoWakandaRequest criaMissao = MissaoWakandaDataHelper.criarMissaoSemSabedorias();

        when(jornadaService.buscaJornada(any(UUID.class)))
                .thenReturn(JornadaWakandaDataHelper.criarJornadaResponseAtiva());

        APIException exception = Assertions.assertThrows(APIException.class, () -> {
            missaoWakandaApplicationService.criaMissao(criaMissao);
        });

        Assertions.assertEquals("Informe ao menos um tipo de sabedoria!", exception.getMessage());
        verify(tipoMissaoService, times(1)).buscaTipoMissaoId(criaMissao.getIdTipoMissao());
        verify(missaoRepository, never()).salvaMissao(any());
    }

    @Test
    @DisplayName("Deve alterar XPBase da missao com sucesso")
    public void deveAlterarXpBaseComSucesso() {
        MissaoAlteracaoXpBaseRequest request = new MissaoAlteracaoXpBaseRequest(350);

        when(missaoRepository.buscaMissaoPorId(idMissao)).thenReturn(missaoWakanda);
        missaoWakandaApplicationService.alteraXpBase(idMissao, request);

        verify(missaoWakanda, times(1)).alteraXpBase(request);
        verify(missaoRepository, times(1)).buscaMissaoPorId(idMissao);
        verify(missaoRepository, times(1)).salvaMissao(missaoWakanda);
        verify(jornadaService, times(1)).recalculaXpJornada(isNull(), eq(TipoRecalculo.XP_ALTERADO), anyInt(),
                anyInt());
    }

    @Test
    @DisplayName("Deve mover missão para posição anterior e reordenar corretamente")
    void alteraPosicao_moverParaCima() {
        UUID jornadaId = UUID.randomUUID();

        MissaoWakanda missaoA = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("A",
                OrdemMissao.criar(0), jornadaId, MissaoStatus.ATIVA);
        MissaoWakanda missaoB = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("B",
                OrdemMissao.criar(1), jornadaId, MissaoStatus.ATIVA);
        MissaoWakanda missaoC = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("C",
                OrdemMissao.criar(2), jornadaId, MissaoStatus.ATIVA);

        when(missaoRepository.buscaMissaoPorId(missaoC.getIdMissao())).thenReturn(missaoC);
        when(missaoRepository.buscarMissoesAtivasOrdenadas(jornadaId))
                .thenReturn(Arrays.asList(missaoA, missaoB, missaoC));

        // Mock do novo método em lote
        when(missaoRepository.salvaMissoes(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        missaoWakandaApplicationService.alteraPosicaoMissao(missaoC.getIdMissao(), 0);

        Assertions.assertEquals(0, missaoC.getOrdemMissao().getOrdem());
        Assertions.assertEquals(1, missaoA.getOrdemMissao().getOrdem());
        Assertions.assertEquals(2, missaoB.getOrdemMissao().getOrdem());
        // agora esperamos uma chamada ao batch com 3 missões alteradas
        verify(missaoRepository, times(1)).salvaMissoes(argThat(list -> list.size() == 3));
    }

    @Test
    @DisplayName("Deve mover missão para posição posterior e reordenar corretamente")
    void alteraPosicao_moverParaBaixo() {
        UUID jornadaId = UUID.randomUUID();
        MissaoWakanda missaoA = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("missao A",
                OrdemMissao.criar(0), jornadaId, MissaoStatus.ATIVA);
        MissaoWakanda missaoB = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("B",
                OrdemMissao.criar(1), jornadaId, MissaoStatus.ATIVA);
        MissaoWakanda missaoC = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("C",
                OrdemMissao.criar(2), jornadaId, MissaoStatus.ATIVA);

        when(missaoRepository.buscaMissaoPorId(missaoA.getIdMissao())).thenReturn(missaoA);
        when(missaoRepository.buscarMissoesAtivasOrdenadas(jornadaId))
                .thenReturn(Arrays.asList(missaoA, missaoB, missaoC));

        // Mock do novo método em lote
        when(missaoRepository.salvaMissoes(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        missaoWakandaApplicationService.alteraPosicaoMissao(missaoA.getIdMissao(), 2);

        Assertions.assertEquals(2, missaoA.getOrdemMissao().getOrdem());
        Assertions.assertEquals(0, missaoB.getOrdemMissao().getOrdem());
        Assertions.assertEquals(1, missaoC.getOrdemMissao().getOrdem());
        // agora esperamos uma chamada ao batch com 3 missões alteradas
        verify(missaoRepository, times(1)).salvaMissoes(argThat(list -> list.size() == 3));
    }

    @Test
    @DisplayName("Deve lançar erro quando nova posição é igual à atual")
    void alteraPosicao_posicaoIgual() {
        UUID jornadaId = UUID.randomUUID();
        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("missao A",
                OrdemMissao.criar(1), jornadaId, MissaoStatus.ATIVA);
        List<MissaoWakanda> lista = Arrays.asList(
                MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("missao X", OrdemMissao.criar(0),
                        jornadaId, MissaoStatus.ATIVA),
                missao, MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("missao Y",
                        OrdemMissao.criar(1), jornadaId, MissaoStatus.ATIVA));

        when(missaoRepository.buscaMissaoPorId(missao.getIdMissao())).thenReturn(missao);
        when(missaoRepository.buscarMissoesAtivasOrdenadas(jornadaId)).thenReturn(lista);

        APIException ex = Assertions.assertThrows(APIException.class,
                () -> missaoWakandaApplicationService.alteraPosicaoMissao(missao.getIdMissao(), 1));
        Assertions.assertEquals("A missão já está nesta posição", ex.getMessage());
        verify(missaoRepository, never()).salvaMissao(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando nova posição é inválida (fora do limite)")
    void alteraPosicao_posicaoInvalida() {
        UUID jornadaId = UUID.randomUUID();
        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("Missao 1",
                OrdemMissao.criar(0), jornadaId, MissaoStatus.ATIVA);
        List<MissaoWakanda> lista = Arrays.asList(missao);

        when(missaoRepository.buscaMissaoPorId(missao.getIdMissao())).thenReturn(missao);
        when(missaoRepository.buscarMissoesAtivasOrdenadas(jornadaId)).thenReturn(lista);

        APIException ex = Assertions.assertThrows(APIException.class,
                () -> missaoWakandaApplicationService.alteraPosicaoMissao(missao.getIdMissao(), 5));
        Assertions.assertTrue(ex.getMessage().startsWith("A nova posição deve ser entre 0"));
        verify(missaoRepository, never()).salvaMissao(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando missão está inativa")
    void alteraPosicao_missaoInativa() {
        UUID jornadaId = UUID.randomUUID();
        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("Missao 1",
                OrdemMissao.criar(0), jornadaId, MissaoStatus.INATIVA);
        List<MissaoWakanda> lista = Arrays.asList(missao);

        when(missaoRepository.buscaMissaoPorId(missao.getIdMissao())).thenReturn(missao);
        when(missaoRepository.buscarMissoesAtivasOrdenadas(jornadaId)).thenReturn(lista);

        APIException ex = Assertions.assertThrows(APIException.class,
                () -> missaoWakandaApplicationService.alteraPosicaoMissao(missao.getIdMissao(), 0));
        Assertions.assertEquals("Não é possível alterar a posição de uma missão inativa.", ex.getMessage());
        verify(missaoRepository, never()).salvaMissao(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando novo XPBase é igual ao atual")
    void deveLancarExcecaoQuandoXpBaseIgual() {
        MissaoAlteracaoXpBaseRequest request = new MissaoAlteracaoXpBaseRequest(200);

        when(missaoRepository.buscaMissaoPorId(idMissao)).thenReturn(missaoWakanda);
        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "O novo XPBase deve ser diferente do valor atual."))
                .when(missaoWakanda).alteraXpBase(request);

        APIException exception = assertThrows(APIException.class,
                () -> missaoWakandaApplicationService.alteraXpBase(idMissao, request));

        assertEquals("O novo XPBase deve ser diferente do valor atual.", exception.getMessage());
        verify(missaoRepository, times(1)).buscaMissaoPorId(idMissao);
        verify(missaoWakanda, times(1)).alteraXpBase(request);
        verify(missaoRepository, never()).salvaMissao(any());
        verify(jornadaService, never()).recalculaXpJornada(any(UUID.class), any(TipoRecalculo.class), anyInt(),
                anyInt());
    }

    @Test
    @DisplayName("Deve buscar pagina de missoes com sucesso e mapear para detalhado")
    void buscaMissoes_Paginadas_ComSucesso() {
        UUID idJornada = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 2);
        MissaoWakanda missao1 = MissaoWakandaDataHelper.criaMissaoWakanda();
        MissaoWakanda missao2 = MissaoWakandaDataHelper.criaMissaoWakanda();
        Page<MissaoWakanda> page = new PageImpl<>(List.of(missao1, missao2), pageable, 5);

        when(missaoRepository.buscaMissoesPaginadas(pageable, idJornada, MissaoStatus.ATIVA)).thenReturn(page);

        Page<MissaoWakandaDetalhadoResponse> result = missaoWakandaApplicationService
                .buscaMissoesPaginadas(pageable, idJornada, MissaoStatus.ATIVA);

        assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(page.getTotalElements(), result.getTotalElements());
        Assertions.assertEquals(missao1.getIdMissao(), result.getContent().get(0).getIdMissao());
        Assertions.assertEquals(missao2.getIdMissao(), result.getContent().get(1).getIdMissao());
        verify(missaoRepository, times(1)).buscaMissoesPaginadas(pageable, idJornada, MissaoStatus.ATIVA);
    }

    @Test
    @DisplayName("Deve inserir missão antes da primeira inativa e deslocar as demais")
    void deveInserirAntesDaInativa() {
        MissaoWakanda ativa = MissaoWakandaDataHelper.criaMissao("A", OrdemMissao.criar(1), jornadaId,
                MissaoStatus.ATIVA);
        MissaoWakanda inativa = MissaoWakandaDataHelper.criaMissao("B", OrdemMissao.criar(2), jornadaId,
                MissaoStatus.INATIVA);
        List<MissaoWakanda> missoes = new ArrayList<>(List.of(ativa, inativa));

        when(missaoRepository.buscaMissoesPorIdJornada(jornadaId)).thenReturn(new ArrayList<>(missoes));

        // Mock do batch (deslocaESalva) e do save individual (adicionaMissao salva a nova missão)
        when(missaoRepository.salvaMissoes(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(missaoRepository.salvaMissao(any(MissaoWakanda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MissaoWakanda nova = MissaoWakandaDataHelper.criaMissao("C", OrdemMissao.criar(1), jornadaId,
                MissaoStatus.ATIVA);
        MissaoWakanda salva = missaoWakandaApplicationService.adicionaMissaoAntesDasInativasESalva(nova, true);

        assertNotNull(salva);
        assertEquals(2, salva.getOrdemMissao().getOrdem());
        verify(missaoRepository, times(1)).salvaMissoes(anyList());
        verify(missaoRepository, times(1)).salvaMissao(any(MissaoWakanda.class));
    }

    @Test
    @DisplayName("Deve inserir missão após última ativa quando não há inativas")
    void deveInserirAposUltimaAtiva() {
        MissaoWakanda ativa1 = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("A", OrdemMissao.criar(1),
                jornadaId, MissaoStatus.ATIVA);
        MissaoWakanda ativa2 = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("A", OrdemMissao.criar(2),
                jornadaId, MissaoStatus.ATIVA);

        when(missaoRepository.buscaMissoesPorIdJornada(jornadaId))
                .thenReturn(new ArrayList<>(List.of(ativa1, ativa2)));
        when(missaoRepository.salvaMissao(any())).thenAnswer(inv -> inv.getArgument(0));

        MissaoWakanda nova = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("C", OrdemMissao.criar(0),
                jornadaId, MissaoStatus.ATIVA);

        MissaoWakanda salva = missaoWakandaApplicationService.adicionaMissaoAntesDasInativasESalva(nova, true);

        assertEquals(3, salva.getOrdemMissao().getOrdem());
        verify(missaoRepository).salvaMissao(nova);
    }

    @Test
    @DisplayName("Deve buscar no repositório novamente quando cache é invalidado")
    void deveBuscarRepositorioQuandoCacheInvalidado() {
        UUID jornadaId = UUID.randomUUID();
        MissaoWakanda missaoBase = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("A",
                OrdemMissao.criar(1), jornadaId, MissaoStatus.ATIVA);

        when(missaoRepository.salvaMissao(any(MissaoWakanda.class))).thenAnswer(inv -> inv.getArgument(0));
        List<MissaoWakanda> primeiraCarga = new ArrayList<>(List.of(missaoBase));
        List<MissaoWakanda> segundaCarga = new ArrayList<>(List.of(missaoBase));
        when(missaoRepository.buscaMissoesPorIdJornada(jornadaId)).thenReturn(primeiraCarga)
                .thenReturn(segundaCarga);

        // primeira chamada emLote = true → mantém cache
        MissaoWakanda nova1 = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("B", OrdemMissao.criar(0),
                jornadaId, MissaoStatus.ATIVA);
        missaoWakandaApplicationService.adicionaMissaoAntesDasInativasESalva(nova1, true);

        // segunda chamada emLote = false → invalida cache (rebuild ocorrerá na próxima
        // necessidade)
        MissaoWakanda nova2 = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("C", OrdemMissao.criar(0),
                jornadaId, MissaoStatus.ATIVA);
        missaoWakandaApplicationService.adicionaMissaoAntesDasInativasESalva(nova2, false);

        // terceira chamada força reconstrução e nova busca no repositório
        MissaoWakanda nova3 = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("D", OrdemMissao.criar(0),
                jornadaId, MissaoStatus.ATIVA);
        missaoWakandaApplicationService.adicionaMissaoAntesDasInativasESalva(nova3, true);

        verify(missaoRepository, times(2)).buscaMissoesPorIdJornada(jornadaId);
    }

    @Test
    @DisplayName("Deve reutilizar cache quando está em lote e não chamar repositório novamente")
    void deveReutilizarCacheQuandoEmLote() {
        UUID jornadaId = UUID.randomUUID();
        MissaoWakanda missaoBase = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("A",
                OrdemMissao.criar(1), jornadaId, MissaoStatus.ATIVA);

        // primeira chamada: popula cache
        when(missaoRepository.buscaMissoesPorIdJornada(jornadaId))
                .thenReturn(new ArrayList<>(List.of(missaoBase)));
        when(missaoRepository.salvaMissao(any(MissaoWakanda.class))).thenAnswer(inv -> inv.getArgument(0));

        MissaoWakanda nova1 = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("B", OrdemMissao.criar(0),
                jornadaId, MissaoStatus.ATIVA);
        missaoWakandaApplicationService.adicionaMissaoAntesDasInativasESalva(nova1, true);

        // segunda chamada: como está em lote, deve usar cache e não chamar repo de novo
        MissaoWakanda nova2 = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("C", OrdemMissao.criar(0),
                jornadaId, MissaoStatus.ATIVA);
        missaoWakandaApplicationService.adicionaMissaoAntesDasInativasESalva(nova2, true);

        // repositório chamado apenas uma vez
        verify(missaoRepository, times(1)).buscaMissoesPorIdJornada(jornadaId);
    }

    @Test
    @DisplayName("Deve criar missão externa corretamente")
    void deveCriarMissaoExterna() {
        MissaoExternaDTO dto = MissaoWakandaDataHelper.criarMissao("externo123", "Missão Externa");

        when(missaoRepository.buscaMissoesPorIdJornada(any(UUID.class))).thenReturn(new ArrayList<>());
        when(missaoRepository.salvaMissao(any())).thenAnswer(inv -> inv.getArgument(0));

        MissaoWakanda salva = missaoWakandaApplicationService.criaMissaoExterna(dto, false);

        assertEquals("Missão Externa", salva.getTitulo());
        assertEquals("externo123", salva.getIdMissaoExterna());
        verify(missaoRepository).salvaMissao(any());
    }

    @Test
    @DisplayName("Deve criar missão externa salvando conteudoUrl")
    void deveCriarMissaoExternaSalvandoConteudoUrl() {
        String conteudoUrl = "https://www.youtube.com/watch?v=video-123";
        MissaoExternaDTO dto = MissaoExternaDTO.builder()
                .idExterno("externo-video")
                .titulo("Missão com vídeo")
                .descricao("Descrição com vídeo")
                .idTipoMissao(UUID.randomUUID())
                .idJornada(UUID.randomUUID())
                .idExternoPai(java.util.Optional.empty())
                .conteudoUrl(conteudoUrl)
                .build();

        when(missaoRepository.buscaMissoesPorIdJornada(any(UUID.class))).thenReturn(new ArrayList<>());
        when(missaoRepository.salvaMissao(any())).thenAnswer(inv -> inv.getArgument(0));

        MissaoWakanda salva = missaoWakandaApplicationService.criaMissaoExterna(dto, false);

        assertEquals(conteudoUrl, salva.getConteudoUrl());
        verify(missaoRepository).salvaMissao(any());
    }

    @Test
    @DisplayName("Deve atualizar missão IA quando status estiver EM_PROCESSO")
    void deveAtualizarMissaoComStatusEmProcesso() {
        UUID idMissao = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();
        AtualizaMissaoRequest request = new AtualizaMissaoRequest(
                200,
                new Sabedorias(5, 5, 5, 5, 5),
                "Descrição IA", UUID.randomUUID()
        );

        when(missaoRepository.buscaMissaoPorId(idMissao)).thenReturn(missaoWakanda);
        when(missaoWakanda.getXpBase()).thenReturn(100, 200);
        when(missaoWakanda.getIdJornada()).thenReturn(idJornada);

        missaoWakandaApplicationService.atualizaMissaoComDadosIA(idMissao, request);

        verify(missaoWakanda, times(1)).atualizaMissaoComDadosIA(request);
        verify(missaoRepository, times(1)).salvaMissao(missaoWakanda);
        verify(jornadaService, times(1)).recalculaXpJornada(
                idJornada,
                TipoRecalculo.XP_ALTERADO,
                100,
                200
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando missão não existe ao atualizar missao")
    void deveLancarExcessaoQuandoMissaoNaoEncontrada() {
        UUID idMissao = UUID.randomUUID();
        when(missaoRepository.buscaMissaoPorId(idMissao))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Missão não encontrada"));

        APIException exception = assertThrows(APIException.class,
                () -> missaoWakandaApplicationService.atualizaMissaoComDadosIA(idMissao,
                        new AtualizaMissaoRequest(150, new Sabedorias(1, 1, 1, 1, 1), "desc", UUID.randomUUID())));

        assertEquals("Missão não encontrada", exception.getMessage());
        verify(missaoWakanda, never()).atualizaMissaoComDadosIA(any());
        verify(missaoRepository, never()).salvaMissao(any());
        verify(jornadaService, never()).recalculaXpJornada(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando missão já foi processada pela IA")
    void deveLancarExcecaoQuandoMissaoJaProcessada() {
        UUID idMissao = UUID.randomUUID();
        AtualizaMissaoRequest request = new AtualizaMissaoRequest(
                150,
                new Sabedorias(5, 5, 5, 5, 5),
                "Descrição processada pela IA", UUID.randomUUID()
        );

        when(missaoRepository.buscaMissaoPorId(idMissao)).thenReturn(missaoWakanda);
        doThrow(APIException.build(HttpStatus.CONFLICT, "Missão já foi processada por IA."))
                .when(missaoWakanda).atualizaMissaoComDadosIA(request);

        APIException exception = assertThrows(APIException.class,
                () -> missaoWakandaApplicationService.atualizaMissaoComDadosIA(idMissao, request));

        assertEquals("Missão já foi processada por IA.", exception.getMessage());
        verify(missaoRepository, never()).salvaMissao(any());
        verify(jornadaService, never()).recalculaXpJornada(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Ao reordenar, deve salvar apenas missões entre posições afetadas")
    void alteraPosicao_salvaApenasMissoesImpactadas() {
        UUID jornadaId = UUID.randomUUID();
        List<MissaoWakanda> missoes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            missoes.add(MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("M" + i,
                    OrdemMissao.criar(i), jornadaId, MissaoStatus.ATIVA));
        }

        MissaoWakanda mover = missoes.get(2);
        when(missaoRepository.buscaMissaoPorId(mover.getIdMissao())).thenReturn(mover);
        when(missaoRepository.buscarMissoesAtivasOrdenadas(jornadaId)).thenReturn(new ArrayList<>(missoes));
        when(missaoRepository.salvaMissoes(anyList())).thenAnswer(inv -> inv.getArgument(0));

        missaoWakandaApplicationService.alteraPosicaoMissao(mover.getIdMissao(), 5);

        // verify que salvamos em lote as 4 missões impactadas
        verify(missaoRepository, times(1)).salvaMissoes(argThat(list -> list.size() == 4));

        Assertions.assertEquals(5, mover.getOrdemMissao().getOrdem());
        Assertions.assertEquals(2, missoes.get(3).getOrdemMissao().getOrdem());
        Assertions.assertEquals(3, missoes.get(4).getOrdemMissao().getOrdem());
        Assertions.assertEquals(4, missoes.get(5).getOrdemMissao().getOrdem());
    }

    @Test
    @DisplayName("Ao desativar missão, deve salvar somente missões impactadas pela reordenação")
    void desativaMissao_salvaApenasImpactados() {
        UUID jornadaId = UUID.randomUUID();
        MissaoWakanda a = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("A", OrdemMissao.criar(0),
                jornadaId, MissaoStatus.ATIVA);
        MissaoWakanda b = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("B", OrdemMissao.criar(1),
                jornadaId, MissaoStatus.ATIVA);
        MissaoWakanda c = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("C", OrdemMissao.criar(2),
                jornadaId, MissaoStatus.ATIVA);
        MissaoWakanda d = MissaoWakandaDataHelper.criaMissaoWakandaPersonalizada("D", OrdemMissao.criar(3),
                jornadaId, MissaoStatus.ATIVA);

        List<MissaoWakanda> todas = new ArrayList<>(List.of(a, b, c, d));

        when(missaoRepository.buscaMissaoPorId(b.getIdMissao())).thenReturn(b);
        when(missaoRepository.buscarTodasMissoesOrdenadas(jornadaId)).thenReturn(new ArrayList<>(todas));
        // mock do save individual (marcar inativa) e do batch para reordenar as demais
        when(missaoRepository.salvaMissao(any(MissaoWakanda.class))).thenAnswer(inv -> inv.getArgument(0));
        when(missaoRepository.salvaMissoes(anyList())).thenAnswer(inv -> inv.getArgument(0));


        missaoWakandaApplicationService.desativaMissao(b.getIdMissao());

        // Esperamos uma chamada ao salvaMissao (para marcar como inativa) e uma chamada ao batch com 3 missões alteradas
        verify(missaoRepository, times(1)).salvaMissao(any(MissaoWakanda.class));
        verify(missaoRepository, times(1)).salvaMissoes(argThat(list -> list.size() == 3));

        Assertions.assertEquals(MissaoStatus.INATIVA, b.getMissaoStatus());
        Assertions.assertEquals(0, a.getOrdemMissao().getOrdem());
        Assertions.assertEquals(1, c.getOrdemMissao().getOrdem());
        Assertions.assertEquals(2, d.getOrdemMissao().getOrdem());
        Assertions.assertEquals(3, b.getOrdemMissao().getOrdem());
    }
}
