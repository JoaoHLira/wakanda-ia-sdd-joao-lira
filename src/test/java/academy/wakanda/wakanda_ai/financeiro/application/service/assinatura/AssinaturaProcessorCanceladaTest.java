package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaType;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssinaturaProcessorCanceladaTest {

    @InjectMocks
    private AssinaturaProcessorCancelada assinaturaProcessorCancelada;

    @Mock
    private WakanderService wakanderService;

    @Test
    @DisplayName("Deve retornar true se evento for SUBSCRIPTION DELETED")
    public void deveRetornarTrueSeEventoForSubscriptionDeleted() {
        AssinaturaEvento evento = DataHelper.criaAssinaturaRequest(AssinaturaType.SUBSCRIPTION_DELETED);

        boolean resultado = assinaturaProcessorCancelada.validaSeEventoProcessa(evento);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve retornar false se evento for diferente SUBSCRIPTION DELETED")
    public void deveRetornarFalseSeEventoForDiferenteDeSubscriptionDeleted() {
        AssinaturaEvento evento = DataHelper.criaAssinaturaRequest(AssinaturaType.SUBSCRIPTION_UPDATED);

        boolean resultado = assinaturaProcessorCancelada.validaSeEventoProcessa(evento);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve solicitar cancelamento do Wakander")
    public void deveSolicitarCancelamentoDoWakander() {
        AssinaturaEvento evento = DataHelper.criaAssinaturaRequest(AssinaturaType.SUBSCRIPTION_DELETED);
        Wakander wakander = DataHelper.criaWakander();

        when(wakanderService.buscaWakanderPorIdAssinatura(evento.getId())).thenReturn(wakander);
        
        assinaturaProcessorCancelada.processaEvento(evento);
    }
}