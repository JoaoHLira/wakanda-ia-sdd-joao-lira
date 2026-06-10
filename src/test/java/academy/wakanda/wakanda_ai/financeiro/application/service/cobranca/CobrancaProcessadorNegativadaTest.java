package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEventoType;
import academy.wakanda.wakanda_ai.financeiro.infra.CobrancaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaProcessadorNegativadaTest {

    @InjectMocks
    private CobrancaProcessadorNegativada cobrancaProcessadorNegativada;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Test
    void deveValidarSeProcessa() {
        CobrancaEvento cobrancaEvento = mock(CobrancaEvento.class);
        when(cobrancaEvento.getEvent()).thenReturn(CobrancaEventoType.PAYMENT_DUNNING_RECEIVED);

        boolean result = cobrancaProcessadorNegativada.validaSeProcessa(cobrancaEvento);

        assertTrue(result);
    }

    @Test
    void naoDeveProcessarQuandoEventoDiferente() {
        CobrancaEvento cobrancaEvento = mock(CobrancaEvento.class);
        when(cobrancaEvento.getEvent()).thenReturn(CobrancaEventoType.PAYMENT_CONFIRMED);

        boolean result = cobrancaProcessadorNegativada.validaSeProcessa(cobrancaEvento);

        assertFalse(result);
    }

    @Test
    void deveProcessar() {
        Cobranca cobranca = mock(Cobranca.class);
        CobrancaEvento cobrancaEvento = mock(CobrancaEvento.class);

        when(cobrancaRepository.buscaCobrancaPorIdPaymentAsaas(cobrancaEvento)).thenReturn(cobranca);

        Cobranca resultado = cobrancaProcessadorNegativada.processa(cobrancaEvento);

        assertNotNull(resultado);
        verify(cobranca).alteraStatusParaNegativado();
        verify(cobrancaRepository, times(1)).buscaCobrancaPorIdPaymentAsaas(cobrancaEvento);
    }

}

