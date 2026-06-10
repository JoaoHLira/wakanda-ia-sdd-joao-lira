package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service;


import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service.JornadaProgressoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.*;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.dataHelper.ProgressoWakanderDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProgressoWakanderApplicationServiceTest {


    @Mock
    private ProgressoGameficacaoRepository progressoRepository;
    @Mock
    private WakanderRepository wakanderRepository;
    @Mock
    private JornadaWakandaRepository jornadaWakandaRepository;
    @Mock
    private JornadaProgressoRepository jornadaProgressoRepository;


    @InjectMocks
    private ProgressoWakanderApplicationService service;

    private UUID idWakander;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        idWakander = ProgressoWakanderDataHelper.getIdWakander();
    }

    @Test
    void deveCriarNovoProgressoQuandoNaoExiste() {
        // Arrange
        when(progressoRepository.buscaProgressoPorIdWakander(idWakander)).thenReturn(null);
        ProgressoWakander progressoSalvo = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        when(progressoRepository.novoProgresso(any(ProgressoWakander.class))).thenReturn(progressoSalvo);

        // Act
        ProgressoWakanderResponse response = service.novoProgresso(idWakander);

        // Assert
        assertThat(response).isNotNull();
        verify(progressoRepository).buscaProgressoPorIdWakander(idWakander);
        verify(progressoRepository).novoProgresso(any(ProgressoWakander.class));
    }

    @Test
    void deveLancarExcecaoQuandoJaExisteProgresso() {
        // Arrange
        when(progressoRepository.buscaProgressoPorIdWakander(idWakander))
                .thenReturn(ProgressoWakanderDataHelper.getProgressoWakanderIniciante());

        // Act & Assert
        APIException ex = assertThrows(APIException.class, () -> service.novoProgresso(idWakander));
        assertThat(ex.getStatusException()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).contains("Já existe progresso");
        verify(progressoRepository, never()).novoProgresso(any());
    }

    private static class MockRanking implements RankingWakanderProjection {
        private final UUID id;
        private final String nome;
        private final Long missoes;
        private final Integer xp;

        MockRanking(UUID id, String nome, Long missoes, Integer xp) {
            this.id = id;
            this.nome = nome;
            this.missoes = missoes;
            this.xp = xp;
        }

        public UUID getIdWakander() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public Long getMissoesConcluidas() {
            return missoes;
        }

        public Integer getXpTotal() {
            return xp;
        }
    }

    @Test
    void deveRetornarRankingGeralPaginado() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RankingWakanderProjection> pageMock = new PageImpl<>(Arrays.asList(
                new MockRanking(UUID.randomUUID(), "W1", 5L, 300),
                new MockRanking(UUID.randomUUID(), "W2", 4L, 250)
        ), pageable, 2);
        when(progressoRepository.rankingDestaqueGeral(eq(pageable))).thenReturn(pageMock);
        Page<RankingWakanderProjection> result = service.rankingDestaqueGeral(pageable);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(progressoRepository).rankingDestaqueGeral(eq(pageable));
    }

    @Test
    void deveRetornarRankingPorPeriodoQuandoHaResultados() {
        Pageable pageable = PageRequest.of(0, 5);
        LocalDate inicio = LocalDate.of(2025, 9, 1);
        LocalDate fim = LocalDate.of(2025, 9, 30);
        Page<RankingWakanderProjection> pageMock = new PageImpl<>(Arrays.asList(
                new MockRanking(UUID.randomUUID(), "W1", 3L, 180)
        ), pageable, 1);
        when(progressoRepository.rankingDestaquePorPeriodo(any(), any(), eq(pageable))).thenReturn(pageMock);
        Page<RankingWakanderProjection> result = service.rankingDestaquePorPeriodo(inicio, fim, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(progressoRepository).rankingDestaquePorPeriodo(any(), any(), eq(pageable));
    }

    @Test
    void deveRetornarMensagemNenhumWakanderNoPeriodo() {
        Pageable pageable = PageRequest.of(0, 5);
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);
        when(progressoRepository.rankingDestaquePorPeriodo(any(), any(), eq(pageable)))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Nenhum Wakander se destacou neste período"));

        APIException ex = assertThrows(APIException.class, () -> service.rankingDestaquePorPeriodo(inicio, fim, pageable));

        assertThat(ex.getStatusException()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("Nenhum Wakander se destacou neste período");
        verify(progressoRepository).rankingDestaquePorPeriodo(any(), any(), eq(pageable));
    }

    @Test
    void sincronizaWakandersAntigos_CriaRegistrosParaElegiveis() {
        Wakander w1 = DataHelper.criaWakander();
        Wakander w2 = DataHelper.criaWakander();
        when(wakanderRepository.buscaWakandersRegularesSemProgresso()).thenReturn(List.of(w1, w2));

        JornadaWakanda ativa = new JornadaWakanda(UUID.randomUUID(), "Ativa", "Desc", 10, UUID.randomUUID(), StatusJornada.ATIVA, 5, 1);
        JornadaWakanda inativa = new JornadaWakanda(UUID.randomUUID(), "Inativa", "Desc", 0, UUID.randomUUID(), StatusJornada.INATIVA, 0, 2);
        when(jornadaWakandaRepository.listaJornadas()).thenReturn(List.of(ativa, inativa));

        when(progressoRepository.novoProgresso(any(ProgressoWakander.class)))
                .thenReturn(ProgressoWakanderDataHelper.getProgressoWakanderIniciante());

        when(wakanderRepository.buscaWakandersIrregulares()).thenReturn(List.of(DataHelper.criaWakanderCancelado()));
        when(wakanderRepository.buscaWakandersRegularesComProgresso()).thenReturn(List.of(DataHelper.criaWakander()));

        SincronizacaoProgressoResponse resumo = service.sincronizaWakandersAntigos();

        assertThat(resumo).isNotNull();
        assertEquals(2, resumo.getRegistrosCriados());
        assertEquals(2, resumo.getRegistrosIgnorados());

        verify(progressoRepository, times(2)).novoProgresso(any(ProgressoWakander.class));
        verify(jornadaProgressoRepository, times(2)).salvaJornadaProgresso(any());
    }

    @Test
    void sincronizaWakandersAntigos_SemElegiveis_RetornaApenasIgnorados() {
        when(wakanderRepository.buscaWakandersRegularesSemProgresso()).thenReturn(List.of());
        when(wakanderRepository.buscaWakandersIrregulares()).thenReturn(List.of(DataHelper.criaWakanderCancelado()));
        when(wakanderRepository.buscaWakandersRegularesComProgresso()).thenReturn(List.of(DataHelper.criaWakander()));

        SincronizacaoProgressoResponse resumo = service.sincronizaWakandersAntigos();

        assertEquals(0, resumo.getRegistrosCriados());
        assertEquals(2, resumo.getRegistrosIgnorados());
        verify(progressoRepository, never()).novoProgresso(any());
        verify(jornadaProgressoRepository, never()).salvaJornadaProgresso(any());
    }

    @Test
    void quandoNaoExisteProgresso_criaProgressoECriaJornadas() {
        UUID idWakander = UUID.randomUUID();

        Wakander wakander = mock(Wakander.class);
        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);

        when(progressoRepository.buscaProgressoPorIdWakander(idWakander)).thenReturn(null);

        var idProgresso = UUID.randomUUID();
        ProgressoWakander progressoPersistido = new ProgressoWakander(idProgresso, idWakander, LocalDateTime.now());
        when(progressoRepository.novoProgresso(any(ProgressoWakander.class))).thenReturn(progressoPersistido);

        JornadaWakanda jornadaAtiva = mock(JornadaWakanda.class, RETURNS_DEEP_STUBS);
        when(jornadaAtiva.getStatusJornada().isAtiva()).thenReturn(true);
        when(jornadaAtiva.getIdJornada()).thenReturn(UUID.randomUUID());
        when(jornadaWakandaRepository.listaJornadas()).thenReturn(List.of(jornadaAtiva));

        service.sincronizaWakanderAntigo(idWakander);

        verify(wakanderRepository).buscaWakanderPorId(idWakander);
        verify(wakander).validaWakanderRegular();

        verify(progressoRepository).buscaProgressoPorIdWakander(idWakander);
        verify(progressoRepository).novoProgresso(any(ProgressoWakander.class));

        verify(jornadaWakandaRepository).listaJornadas();
        verify(jornadaProgressoRepository, times(1)).salvaJornadaProgresso(any());
        verifyNoMoreInteractions(jornadaProgressoRepository);
    }

    @Test
    void quandoJaExisteProgresso_lancaApiExceptionENaoCriaNada() {
        UUID idWakander = UUID.randomUUID();

        Wakander wakander = mock(Wakander.class);
        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);

        when(progressoRepository.buscaProgressoPorIdWakander(idWakander)).thenReturn(mock(ProgressoWakander.class));

        APIException ex = assertThrows(APIException.class, () -> service.sincronizaWakanderAntigo(idWakander));
        assertTrue(ex.getMessage().contains("Já existe progresso para este Wakander"));

        verify(wakanderRepository).buscaWakanderPorId(idWakander);
        verify(wakander).validaWakanderRegular();
        verify(progressoRepository).buscaProgressoPorIdWakander(idWakander);

        verify(progressoRepository, never()).novoProgresso(any());
        verifyNoInteractions(jornadaWakandaRepository);
        verifyNoInteractions(jornadaProgressoRepository);
    }

    @Test
    void quandoWakanderIrregular_lancaApiExceptionENaoCriaProgresso() {
        UUID idWakander = UUID.randomUUID();

        Wakander wakander = mock(Wakander.class);
        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);

        doThrow(APIException.build(org.springframework.http.HttpStatus.BAD_REQUEST, "Wakander não está regularizado!"))
                .when(wakander).validaWakanderRegular();

        assertThrows(APIException.class, () -> service.sincronizaWakanderAntigo(idWakander));

        verify(wakanderRepository).buscaWakanderPorId(idWakander);
        verify(wakander).validaWakanderRegular();

        verify(progressoRepository, never()).buscaProgressoPorIdWakander(any());
        verify(progressoRepository, never()).novoProgresso(any());
        verifyNoInteractions(jornadaWakandaRepository);
        verifyNoInteractions(jornadaProgressoRepository);
    }


    @Test
    void progressoIndividual_DeveRetornarProgressoIndividual() {
        // GIVEN
        UUID idWakander = UUID.randomUUID();

        ProgressoIndividualDetalhadoProjection projection =
                mock(ProgressoIndividualDetalhadoProjection.class);

        when(projection.getNome()).thenReturn("João da Silva");
        when(projection.getNivelAtual()).thenReturn(3);
        when(projection.getXpTotal()).thenReturn(450);
        when(projection.getUltimaAtualizacao()).thenReturn(LocalDateTime.now());

        when(progressoRepository.buscaprogressoIndividual(idWakander))
                .thenReturn(projection);

        // WHEN
        ProgressoIndividualResponse response =
                service.progressoIndividual(idWakander);

        // THEN
        assertNotNull(response);
        assertEquals("João da Silva", response.getNome());
        assertEquals(3, response.getNivelAtual());
        assertEquals(450L, response.getXpTotal());
        assertNotNull(response.getUltimaAtualizacao());

        verify(progressoRepository, times(1))
                .buscaprogressoIndividual(idWakander);
    }

    @Test
    void progressoIndividual_DeveLancarExcecaoQuandoProgressoNaoEncontrado() {
        when(progressoRepository.buscaprogressoIndividual(idWakander))
                .thenThrow(APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Wakander nao encontrado"));

        APIException exception = assertThrows(
                APIException.class, () -> service.progressoIndividual(idWakander));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        assertEquals("Wakander nao encontrado", exception.getMessage());
        verify(progressoRepository).buscaprogressoIndividual(idWakander);
    }

    @Test
    void progressoIndividual_DeveExibirWakanderComProgressoZerado() {
        ProgressoIndividualDetalhadoProjection progressoMock = mock(ProgressoIndividualDetalhadoProjection.class);
        when(progressoMock.getNome()).thenReturn("Maria Souza");
        when(progressoMock.getNivelAtual()).thenReturn(1);
        when(progressoMock.getXpTotal()).thenReturn(0);
        when(progressoMock.getUltimaAtualizacao()).thenReturn(LocalDateTime.now());

        when(progressoRepository.buscaprogressoIndividual(idWakander)).thenReturn(progressoMock);

        ProgressoIndividualResponse response = service.progressoIndividual(idWakander);

        assertEquals("Maria Souza", response.getNome());
        assertEquals(1, response.getNivelAtual());
        assertEquals(0L, response.getXpTotal());
        assertNotNull(response.getUltimaAtualizacao());
        verify(progressoRepository).buscaprogressoIndividual(idWakander);

    }
}


