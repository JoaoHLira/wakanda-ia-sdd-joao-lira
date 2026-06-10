package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEventoType;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaStatus;
import academy.wakanda.wakanda_ai.financeiro.infra.CobrancaRepository;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaProcessadorConfirmadaTest {

    @InjectMocks
    private CobrancaProcessadorConfirmada cobrancaProcessadorConfirmada;
    @Mock
    CobrancaRepository cobrancaRepository;

    @Test
    @DisplayName("Deve validar se evento com status PAYMENT_CONFIRMED pode processar")
    void deveValidarSeProcessa() {
        CobrancaEvento cobrancaEvento = new CobrancaEvento(
                DataHelper.criaEventoDeCobrancaDoAsaas(CobrancaEventoType.PAYMENT_CONFIRMED));

        boolean result = cobrancaProcessadorConfirmada.validaSeProcessa(cobrancaEvento);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve mudar status da cobranca para PAGAMENTO_CONFIRMADO")
    void deveProcessar() {
        Cobranca cobranca = DataHelper.criaCobranca(CobrancaStatus.PENDENTE);
        CobrancaEvento cobrancaEvento = new CobrancaEvento(
                DataHelper.criaEventoDeCobrancaDoAsaas(CobrancaEventoType.PAYMENT_CONFIRMED));
        when(cobrancaRepository.buscaCobrancaPorIdPaymentAsaas(cobrancaEvento)).thenReturn(cobranca);

        Cobranca cobrancaConfirmada = cobrancaProcessadorConfirmada.processa(cobrancaEvento);

        assertEquals(CobrancaStatus.PAGAMENTO_CONFIRMADO, cobrancaConfirmada.getStatus());
        verify(cobrancaRepository, times(1)).buscaCobrancaPorIdPaymentAsaas(cobrancaEvento);
    }

}