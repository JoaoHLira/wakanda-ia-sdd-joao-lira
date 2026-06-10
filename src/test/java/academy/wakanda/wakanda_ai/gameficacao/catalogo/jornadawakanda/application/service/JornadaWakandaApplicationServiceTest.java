package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.*;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.datahelper.JornadaWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.service.TrilhaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JornadaWakandaApplicationServiceTest {

    @InjectMocks
    private JornadaWakandaApplicationService jornadaService;

    @Mock
    private TrilhaService trilhaService;

    private TrilhaWakanda trilhaWakanda;

    @Mock
    JornadaWakandaRepository jornadaWakandaRepository;

    @BeforeEach
    void setUp() {
        trilhaWakanda = new TrilhaWakanda(UUID.fromString("a330c529-c5d9-408c-b07a-e882365f23f2"), "Trilha Java",
                "Descricao trilha");
    }

    @Test
    @DisplayName("Deve criar jornada com sucesso")
    public void criarJornada() {
        JornadaCriacaoRequest request = JornadaWakandaDataHelper.criarJornadaCriacaoRequestJavaBasico();
        JornadaWakanda jornadaMockada = JornadaWakandaDataHelper.criarJornadaJavaBasico();

        when(trilhaService.buscaTrilhaPorId(request.getIdTrilhaWakanda())).thenReturn(trilhaWakanda);
        when(jornadaWakandaRepository.save(any(JornadaWakanda.class))).thenReturn(jornadaMockada);

        JornadaResponse response = jornadaService.criarJornada(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(jornadaMockada.getTitulo(), response.getTitulo());
        Assertions.assertEquals(trilhaWakanda.getNome(), response.getIdTrilhaWakanda().getNome());

        verify(trilhaService, times(1)).buscaTrilhaPorId(request.getIdTrilhaWakanda());
        verify(jornadaWakandaRepository, times(1)).save(any(JornadaWakanda.class));
    }

    @Test
    public void deveLancarExcecaoQuandoTrilhaNaoExiste() {
        JornadaCriacaoRequest request = JornadaWakandaDataHelper.criarJornadaCriacaoRequestValido();
        UUID idTrilhaInexistente = request.getIdTrilhaWakanda();

        when(jornadaWakandaRepository.buscaJornadasPorIdTrilha(idTrilhaInexistente)).thenReturn(List.of());

        when(trilhaService.buscaTrilhaPorId(idTrilhaInexistente)).thenThrow(APIException.build(HttpStatus.NOT_FOUND,
                "Id não está associado a nenhuma trilha, verifique a requisição e tente novamente"));

        APIException exception = assertThrows(APIException.class, () -> jornadaService.criarJornada(request));
        Assertions.assertTrue(exception.getMessage().contains("Id não está associado a nenhuma trilha"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());

        verify(trilhaService, times(1)).buscaTrilhaPorId(idTrilhaInexistente);
        verify(jornadaWakandaRepository, never()).save(any());
    }

    @Test
    public void deveRemoverXpDaJornadaAoDesativarMissao() {
        JornadaWakanda jornada = JornadaWakandaDataHelper.criarJornadaJavaBasico();
        UUID idJornada = jornada.getIdJornada();

        when(jornadaWakandaRepository.buscaJornadaId(idJornada)).thenReturn(jornada);

        when(jornadaWakandaRepository.save(any())).thenReturn(jornada);

        jornadaService.removerXpJornada(idJornada, 20);

        verify(jornadaWakandaRepository).buscaJornadaId(eq(idJornada));
        verify(jornadaWakandaRepository).save(any());
    }

    @Test
    public void deveLancarExcecaoQuandoJornadaNaoEncontradaAoDesativarMissao() {
        UUID idJornada = UUID.randomUUID();

        doThrow(APIException.build(HttpStatus.NOT_FOUND, "Jornada não encontrada"))
                .when(jornadaWakandaRepository).buscaJornadaId(any());

        APIException exception = assertThrows(APIException.class,
                () -> jornadaService.removerXpJornada(idJornada, 20));

        verify(jornadaWakandaRepository).buscaJornadaId(eq(idJornada));
        verify(jornadaWakandaRepository, never()).save(any());

        assertEquals("Jornada não encontrada", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
    }

    @Test
    @DisplayName("Deve recalcular XP total da jornada com sucesso")
    void deveRecalcularXpTotalJornadaComSucesso() {
        UUID idJornada = UUID.randomUUID();
        JornadaWakanda jornadaMock = mock(JornadaWakanda.class);
        UUID idTrilha = UUID.randomUUID();

        when(jornadaWakandaRepository.buscaJornadaId(idJornada)).thenReturn(jornadaMock);
        when(jornadaMock.getXpTotal()).thenReturn(700, 800);
        when(jornadaMock.getIdTrilhaWakanda()).thenReturn(idTrilha);
        doNothing().when(jornadaMock).recalculaXpJornada(any(), any(), any());
        jornadaService.recalculaXpJornada(idJornada, TipoRecalculo.XP_ALTERADO, 100, 200);

        verify(jornadaWakandaRepository).buscaJornadaId(idJornada);
        verify(jornadaMock).recalculaXpJornada(TipoRecalculo.XP_ALTERADO, 100, 200);
        verify(jornadaWakandaRepository).save(jornadaMock);
        verify(trilhaService).recalculaXpTotalTrilha(idTrilha, TipoRecalculo.XP_ALTERADO, 700, 800);
    }

    @Test
    @DisplayName("Deve adicionar XP à jornada com sucesso")
    void deveAdicionarXpJornadaComSucesso() {
        UUID idJornada = UUID.randomUUID();
        JornadaWakanda jornadaMock = mock(JornadaWakanda.class);
        UUID idTrilha = UUID.randomUUID();

        when(jornadaWakandaRepository.buscaJornadaId(idJornada)).thenReturn(jornadaMock);
        when(jornadaMock.getXpTotal()).thenReturn(150, 200);
        when(jornadaMock.getIdTrilhaWakanda()).thenReturn(idTrilha);
        doNothing().when(jornadaMock).recalculaXpJornada(TipoRecalculo.MISSAO_ADICIONADA, null, 50);
        jornadaService.recalculaXpJornada(idJornada, TipoRecalculo.MISSAO_ADICIONADA, null, 50);

        verify(jornadaWakandaRepository).buscaJornadaId(idJornada);
        verify(jornadaMock).recalculaXpJornada(TipoRecalculo.MISSAO_ADICIONADA, null, 50);
        verify(jornadaWakandaRepository).save(jornadaMock);
        verify(trilhaService).recalculaXpTotalTrilha(idTrilha, TipoRecalculo.MISSAO_ADICIONADA, 150, 200);
    }

    @Test
    @DisplayName("Deve buscar detalhes da jornada com sucesso")
    void buscaJornadaDetalhada_ComSucesso() {
        UUID idJornada = UUID.randomUUID();
        JornadaDetalhadaProjection JornadaDetalhadaProjection = mock(JornadaDetalhadaProjection.class);

        when(jornadaWakandaRepository.buscaJornadaDetalhada(idJornada)).thenReturn(JornadaDetalhadaProjection);
        JornadaDetalhadaResponse jornadaDetalhada = jornadaService.buscaJornadaDetalhada(idJornada);

        assertNotNull(jornadaDetalhada);
        verify(jornadaWakandaRepository).buscaJornadaDetalhada(idJornada);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a Jornada não for encontrada")
    void buscaJornadaDetalhada_QuandoJornadaNaoExistir_DeveLancarExcecao() {
        UUID idJornada = UUID.randomUUID();

        APIException notFound = APIException.build(HttpStatus.NOT_FOUND, "Jornada não encontrada!");

        when(jornadaWakandaRepository.buscaJornadaDetalhada(idJornada)).thenThrow(notFound);

        APIException exception = Assert.assertThrows(APIException.class,
                () -> jornadaWakandaRepository.buscaJornadaDetalhada(idJornada));

        Assert.assertEquals("Jornada não encontrada!", exception.getMessage());
        Assert.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        verify(jornadaWakandaRepository).buscaJornadaDetalhada(idJornada);
    }

    @Test
    @DisplayName("Deve listar as jornadas com sucesso")
    void listaJornadas_DeveRetornarListaDeJornadas() {

        JornadaWakanda jornada1 = JornadaWakandaDataHelper.criarJornadaValida();
        JornadaWakanda jornada2 = JornadaWakandaDataHelper.criarJornadaJavaBasico();

        List<JornadaWakanda> jornadas = Arrays.asList(jornada1, jornada2);

        when(jornadaWakandaRepository.listaJornadas()).thenReturn(jornadas);

        List<JornadaListResponse> response = jornadaService.listaJornadas();

        assertNotNull(response);
        verify(jornadaWakandaRepository).listaJornadas();
    }
}
