package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.application.api.CobrancaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEventoType;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaStatus;
import academy.wakanda.wakanda_ai.financeiro.infra.CobrancaRepository;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaAsaasApplicationServiceTest {
    @InjectMocks
    private CobrancaAsaasApplicationService cobrancaAsaasApplicationService;
    @Mock
    private CobrancaRepository cobrancaRepository;
    @Mock
    private CobrancaProcessadorAsaas cobrancaProcessadorAsaas;
    @Mock
    private CobrancaProcessadorCriada cobrancaProcessadorCriada;
    @Mock
    private CobrancaProcessadorVencida cobrancaProcessadorVencida;

    @BeforeEach
    void setUp() {
        Cobranca dummyCobranca = DataHelper.criaCobranca(CobrancaStatus.PENDENTE);

        when(cobrancaProcessadorAsaas.validaSeProcessa(any())).thenReturn(true);
        when(cobrancaProcessadorAsaas.processa(any())).thenReturn(dummyCobranca);

        List<Object> processadores = List.of(cobrancaProcessadorAsaas, cobrancaProcessadorCriada, cobrancaProcessadorVencida);
        ReflectionTestUtils.setField(cobrancaAsaasApplicationService, "processadoresCobrancaAssas", processadores);
    }

    @Test
    void deveProcessarEvento() {
        CobrancaAsaasDto request = DataHelper.criaEventoDeCobrancaDoAsaas(CobrancaEventoType.PAYMENT_CREATED);

        cobrancaAsaasApplicationService.processaEvento(request);

        verify(cobrancaProcessadorAsaas, times(1)).validaSeProcessa(any());
        verify(cobrancaProcessadorAsaas, times(1)).processa(any());
        verify(cobrancaRepository, times(1)).salvaCobranca(any());
    }
}
