package academy.wakanda.wakanda_ai.financeiro.infra;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaInfraRepositoryTest {

    @InjectMocks
    private CobrancaInfraRepository cobrancaInfraRepository;
    @Mock
    private WakanderRepository wakanderRepository;
    @Mock
    private CobrancaSpringDataJPARepository cobrancaSpringDataJPARepository;


    @Test
    void deveChamarCriaCobrancaSeNaoExistirQuandoNaoEncontrarCobranca() {
        CobrancaEvento cobrancaEvento = mock(CobrancaEvento.class);
        when(cobrancaEvento.getPaymentId()).thenReturn("payment123");
        when(cobrancaEvento.getSubscriptionId()).thenReturn("subscription123");

        when(cobrancaSpringDataJPARepository.findByIdPaymentAsaas(cobrancaEvento.getPaymentId()))
                .thenReturn(Optional.empty());

        when(wakanderRepository.buscaWakanderPorIdAssinatura(cobrancaEvento.getSubscriptionId()))
        .thenReturn(Optional.of(DataHelper.criaWakander()));

        Cobranca cobranca = cobrancaInfraRepository.buscaCobrancaPorIdPaymentAsaas(cobrancaEvento);

        assertNotNull(cobranca);
        verify(cobrancaSpringDataJPARepository, times(1)).findByIdPaymentAsaas(cobrancaEvento.getPaymentId());
        verify(cobrancaSpringDataJPARepository, times(1)).save(any(Cobranca.class));
        verify(wakanderRepository, times(1)).buscaWakanderPorIdAssinatura(cobrancaEvento.getSubscriptionId());
    }

    @Test
    void naoDeveChamarCriaCobrancaSeExistirCobranca() {
        CobrancaEvento cobrancaEvento = mock(CobrancaEvento.class);
        Cobranca cobrancaExistente = mock(Cobranca.class);

        when(cobrancaSpringDataJPARepository.findByIdPaymentAsaas(cobrancaEvento.getPaymentId()))
                .thenReturn(Optional.of(cobrancaExistente));

        Cobranca cobranca = cobrancaInfraRepository.buscaCobrancaPorIdPaymentAsaas(cobrancaEvento);

        assertEquals(cobrancaExistente, cobranca);
        verify(cobrancaSpringDataJPARepository, times(1)).findByIdPaymentAsaas(cobrancaEvento.getPaymentId());
        verify(cobrancaSpringDataJPARepository, never()).save(any(Cobranca.class));
    }
}