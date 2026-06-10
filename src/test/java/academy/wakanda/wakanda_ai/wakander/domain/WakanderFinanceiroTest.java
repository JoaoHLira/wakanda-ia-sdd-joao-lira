package academy.wakanda.wakanda_ai.wakander.domain;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class WakanderFinanceiroTest {
    private WakanderFinanceiro wakanderFinanceiro;

    @BeforeEach
    void setUp() {
        wakanderFinanceiro = new WakanderFinanceiro();
    }

    @Test
    @DisplayName("Deve alterar o status para regular")
    void testMudarStatusParaAtivado_DeveAlterarStatusParaRegular() {
        WakanderFinanceiro wakanderFinanceiro1 = DataHelper.criaWakanderFinanceiroCancelado();

        assertEquals(WakanderStatusFinanceiro.CANCELADO, wakanderFinanceiro1.getStatus());

        wakanderFinanceiro1.mudaStatus(WakanderStatusFinanceiro.REGULAR, LocalDateTime.now());

        assertEquals(WakanderStatusFinanceiro.REGULAR, wakanderFinanceiro1.getStatus());
        assertNotNull(wakanderFinanceiro1.getUltimaAtualizacao());
        assertTrue(wakanderFinanceiro1.getUltimaAtualizacao().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve retornar erro quando status já estiver regular")
    void testMudaStatusParaAtivado_QuandoStatusJaForRegular_DeveLancarExcecao() {

        APIException exception = assertThrows(APIException.class, () -> {
            wakanderFinanceiro.mudaStatus(WakanderStatusFinanceiro.REGULAR, LocalDateTime.now());
        });

        assertEquals("O status financeiro do Wakander já está REGULAR.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve alterar status para CANCELADO quando status for CANCELAMENTO_SOLICITADO")
    void mudaStatusParaCancelado_ComSucesso() {
        WakanderFinanceiro wakanderFinanceiro = new WakanderFinanceiro();

        wakanderFinanceiro.mudaStatus(WakanderStatusFinanceiro.CANCELAMENTO_SOLICITADO, LocalDateTime.now());

        LocalDateTime dataCancelamentp = LocalDateTime.now();
        wakanderFinanceiro.mudaStatusParaCancelado("Motivo Teste", dataCancelamentp);

        assertEquals(WakanderStatusFinanceiro.CANCELADO, wakanderFinanceiro.getStatus());
        assertEquals("Motivo Teste", wakanderFinanceiro.getMotivoCancelamento());
        assertEquals(dataCancelamentp, wakanderFinanceiro.getUltimaAtualizacao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar quando status não for CANCELAMENTO_SOLICITADO")
    void mudaStatusParaCancelado_DeveLancarExcecao_QuandoStatusInvalido() {
        WakanderFinanceiro wakanderFinanceiro = new WakanderFinanceiro();

        APIException ex = assertThrows(APIException.class, () -> {
            wakanderFinanceiro.mudaStatusParaCancelado("Motivo", LocalDateTime.now());
        });

        assertTrue(ex.getMessage().contains("Não é possível cancelar a assinatura. O status atual é:"));
    }

    @Test
    @DisplayName("Deve reverter para REGULAR quando status for CANCELAMENTO_SOLICITADO")
    void reverteParaRegular_ComSucesso() {
        WakanderFinanceiro wakanderFinanceiro = new WakanderFinanceiro();
        wakanderFinanceiro.mudaStatus(WakanderStatusFinanceiro.CANCELAMENTO_SOLICITADO, LocalDateTime.now());

        LocalDateTime dataDaReversao = LocalDateTime.now();
        wakanderFinanceiro.reverteParaRegular(WakanderStatusFinanceiro.REGULAR, dataDaReversao);

        assertEquals(WakanderStatusFinanceiro.REGULAR, wakanderFinanceiro.getStatus());
        assertEquals("Desistiu do Cancelamento.", wakanderFinanceiro.getMotivoCancelamento());
        assertEquals(dataDaReversao, wakanderFinanceiro.getUltimaAtualizacao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reverter quando status não for CANCELAMENTO_SOLICITADO")
    void reverteParaRegular_DeveLancarExcecao_QuandoStatusInvalido() {
        WakanderFinanceiro wakanderFinanceiro = new WakanderFinanceiro();

        APIException ex = assertThrows(APIException.class, () -> {
            wakanderFinanceiro.reverteParaRegular(WakanderStatusFinanceiro.REGULAR, LocalDateTime.now());
        });

        assertTrue(ex.getMessage().contains("Não é possível reverter o cancelamento. O status atual é:"));
    }
}