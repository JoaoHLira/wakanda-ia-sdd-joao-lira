package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEventoType;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaProcessadorCriadaTest {
    @InjectMocks
    private CobrancaProcessadorCriada cobrancaProcessadorCriada;
    @Mock
    WakanderRepository wakanderRepository;
    
    @BeforeAll
    static void setUp() {
        loadTemplates(Templates.BASE_PACKAGE);
    }

    @Test
    void deveValidarSeProcessa() {
        CobrancaEvento cobrancaEvento = new CobrancaEvento(
                DataHelper.criaEventoDeCobrancaDoAsaas(CobrancaEventoType.PAYMENT_CREATED));

        boolean result = cobrancaProcessadorCriada.validaSeProcessa(cobrancaEvento);

        assertTrue(result);
    }

    @Test
    void deveProcessar() {
        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        CobrancaEvento cobrancaEvento = new CobrancaEvento(
                DataHelper.criaEventoDeCobrancaDoAsaas(CobrancaEventoType.PAYMENT_CREATED));

        when(wakanderRepository.buscaWakanderPorIdAssinatura(any())).thenReturn(Optional.of(wakander));
        Cobranca cobranca = cobrancaProcessadorCriada.processa(cobrancaEvento);

        assertNotNull(cobranca);
        verify(wakanderRepository, times(1)).buscaWakanderPorIdAssinatura(any());
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarWakander() {
        CobrancaEvento cobrancaEvento = new CobrancaEvento(
                DataHelper.criaEventoDeCobrancaDoAsaas(CobrancaEventoType.PAYMENT_CREATED));

        doThrow(APIException.build(HttpStatus.NOT_FOUND, "Wakander não encontrado para assinatura!"))
                .when(wakanderRepository).buscaWakanderPorIdAssinatura(any());

        APIException exception = assertThrows(APIException.class, () -> cobrancaProcessadorCriada.processa(cobrancaEvento));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        verify(wakanderRepository, times(1)).buscaWakanderPorIdAssinatura(any());
    }
}
