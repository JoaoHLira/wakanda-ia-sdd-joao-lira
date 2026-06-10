package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaDetalhadaProjection;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaDetalhadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaListResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.datahelper.TrilhaWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import academy.wakanda.wakanda_ai.handler.APIException;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrilhaApplicationServiceTest {

    @InjectMocks
    TrilhaApplicationService trilhaApplicationService;

    @Mock
    TrilhaRepository trilhaRepository;

    @Test
    @DisplayName("Deve criar trilha com sucesso quando dados são válidos")
    void criaTrilha_QuandoDadosValidos_ComSucesso() {

        TrilhaWakanda trilhaCriada = TrilhaWakandaDataHelper.criaTrilhaWakandaValida();
        TrilhaWakandaRequest trilhaRequest = TrilhaWakandaDataHelper.criaTrilhaWakandaRequest();

        when(trilhaRepository.save(any(TrilhaWakanda.class))).thenReturn(trilhaCriada);

        trilhaApplicationService.criaTrilha(trilhaRequest);

        verify(trilhaRepository, times(1)).save(any(TrilhaWakanda.class));
        assertEquals("Profissão Programador", trilhaCriada.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome da trilha já existe")
    void criaTrilha_QuandoNomeDuplicado_LancaExcecao() {
        TrilhaWakandaRequest trilhaRequest = TrilhaWakandaDataHelper.criaTrilhaWakandaRequest();

        when(trilhaRepository.save(any(TrilhaWakanda.class)))
                .thenThrow(APIException.build(HttpStatus.CONFLICT, "Trilha com esse nome já cadastrada"));

        APIException exception = assertThrows(APIException.class, () -> {
            trilhaApplicationService.criaTrilha(trilhaRequest);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
        assertEquals("Trilha com esse nome já cadastrada", exception.getMessage());

        verify(trilhaRepository, times(1)).save(any(TrilhaWakanda.class));
    }

    @Test
    @DisplayName("Deve recalcular XP total da trilha com sucesso")
    void deveRecalcularXpTotalTrilhaComSucesso() {
        UUID idTrilha = UUID.randomUUID();
        TrilhaWakanda trilhaMock = mock(TrilhaWakanda.class);

        when(trilhaRepository.buscaTrilhaPorId(idTrilha)).thenReturn(trilhaMock);
        trilhaApplicationService.recalculaXpTotalTrilha(idTrilha, TipoRecalculo.MISSAO_ADICIONADA, 100, 200);

        verify(trilhaRepository).buscaTrilhaPorId(idTrilha);
        verify(trilhaMock).recalculaXpTrilha(TipoRecalculo.MISSAO_ADICIONADA, 100, 200);
        verify(trilhaRepository).save(trilhaMock);
    }

    @Test
    @DisplayName("Deve buscar detalhes da trilha com sucesso")
    void deveBuscarDetalhesDaTrilhaComSucesso() {
        UUID idTrilha = UUID.randomUUID();
        TrilhaDetalhadaProjection trilhaDetalhadaProjection = mock(TrilhaDetalhadaProjection.class);

        when(trilhaRepository.buscaTrilhaDetalhada(idTrilha)).thenReturn(trilhaDetalhadaProjection);
        TrilhaDetalhadaResponse trilhaDetalhada = trilhaApplicationService.buscaTrilhaDetalhada(idTrilha);

        assertNotNull(trilhaDetalhada);
        verify(trilhaRepository).buscaTrilhaDetalhada(idTrilha);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a trilha não for encontrada")
    void buscaTrilhaDetalhada_QuandoTrilhaNaoExistir_DeveLancarExcecao() {
        UUID idTrilha = UUID.randomUUID();

        APIException notFound = APIException.build(HttpStatus.NOT_FOUND, "Trilha não encontrada!");

        when(trilhaRepository.buscaTrilhaDetalhada(idTrilha)).thenThrow(notFound);

        APIException exception = assertThrows(APIException.class, () -> trilhaApplicationService.buscaTrilhaDetalhada(idTrilha));

        assertEquals("Trilha não encontrada!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        verify(trilhaRepository).buscaTrilhaDetalhada(idTrilha);
    }

    @Test
    @DisplayName("Deve listar as trilhas com sucesso")
    void listaTrilhas_DeveRetornarListaDeTrilhas() {

        TrilhaWakanda trilha1 = TrilhaWakandaDataHelper.criaTrilhaWakandaValida();
        TrilhaWakanda trilha2 = TrilhaWakandaDataHelper.criaTrilhaWakandaValida();

        List<TrilhaWakanda> trilhas = Arrays.asList(trilha1, trilha2);

        when(trilhaRepository.listaTrilhas()).thenReturn(trilhas);

        List<TrilhaListResponse> response = trilhaApplicationService.listaTrilhas();

        assertNotNull(response);
        verify(trilhaRepository).listaTrilhas();
    }
}
