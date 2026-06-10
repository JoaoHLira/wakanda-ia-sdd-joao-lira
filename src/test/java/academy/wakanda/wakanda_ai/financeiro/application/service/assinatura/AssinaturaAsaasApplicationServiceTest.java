package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import academy.wakanda.wakanda_ai.autenticacao.application.service.AutenticacaoService;
import academy.wakanda.wakanda_ai.financeiro.application.api.AssinaturaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.datahelper.FinanceiroDataHelper;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaType;
import academy.wakanda.wakanda_ai.financeiro.infra.AsaasClient;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssinaturaAsaasApplicationServiceTest {

    @InjectMocks
    private AssinaturaAsaasApplicationService assinaturaAsaasApplicationService;

    @Mock
    private AssinaturaProcessorAsaas assinaturaProcessorAsaas;
    @Mock
    private AssinaturaProcessorCancelada assinaturaProcessorCancelada;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private AsaasClient asaasClient;
    @Mock
    private WakanderService wakanderService;

    @BeforeEach
    void setUp() {
        List<AssinaturaProcessorAsaas> processadores = List.of(assinaturaProcessorAsaas, assinaturaProcessorCancelada);
        ReflectionTestUtils.setField(assinaturaAsaasApplicationService, "assinaturaProcessorAsaas", processadores);
    }


    @Test
    @DisplayName("Deve processar eventos de assinatura suportados")
    void deveProcessarAssinaturaParaEventoExistente() {
        AssinaturaAsaasDto assinaturaAsaasDto = DataHelper.criaAssinaturaAsaasDto(AssinaturaType.SUBSCRIPTION_DELETED);

        when(assinaturaProcessorAsaas.validaSeEventoProcessa(any(AssinaturaEvento.class))).thenReturn(true);
        doNothing().when(assinaturaProcessorAsaas).processaEvento(any());

        assinaturaAsaasApplicationService.processaAssinaturaPorEvento(assinaturaAsaasDto);

        verify(assinaturaProcessorAsaas, times(1)).validaSeEventoProcessa(any(AssinaturaEvento.class));
        verify(assinaturaProcessorAsaas, times(1)).processaEvento(any(AssinaturaEvento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção eventos quando não existir estratégias")
    void deveLancarExcecaoQuandoNenhumaEstrategiaCorresponde() {
        AssinaturaAsaasDto assinaturaAsaasDto = DataHelper.criaAssinaturaAsaasDto(AssinaturaType.SUBSCRIPTION_DELETED);

        when(assinaturaProcessorAsaas.validaSeEventoProcessa(any())).thenReturn(false);

        APIException exception = assertThrows(APIException.class,
                () -> assinaturaAsaasApplicationService.processaAssinaturaPorEvento(assinaturaAsaasDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals("O evento não corresponde a nenhuma estrategia!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar dados do fiador com sucesso")
    void atualizaDadosFiador_ComSucesso() {
        String token = "YhngL1DqQXNNuwPnp5LrWy";
        Wakander wakander = DataHelper.criaWakander();

        FiadorDTO fiadorDTO = FinanceiroDataHelper.criaFiadorDTO();
        AsaasResponse asaasResponse = FinanceiroDataHelper.criaAsaasResponseFiador();

        when(autenticacaoService.buscaWakanderPeloToken(token)).thenReturn(wakander);
        when(asaasClient.atualizaDadosFiador(wakander.getFiador().getIdAsaas(), fiadorDTO)).thenReturn(asaasResponse);

        assinaturaAsaasApplicationService.atualizaDadosFiador(token, fiadorDTO);

        verify(autenticacaoService, times(1)).buscaWakanderPeloToken(token);
        verify(asaasClient, times(1)).atualizaDadosFiador(wakander.getFiador().getIdAsaas(), fiadorDTO);
        verify(wakanderService, times(1)).atualizaFiador(token, fiadorDTO);
    }

    @Test
    @DisplayName("Não deve atualizar dados do fiador quando AsaasResponse for nulo")
    void naoDeveAtualizarDadosFiador_QuandoAsaasResponseForNulo() {
        String token = "YhngL1DqQXNNuwPnp5LrWy";
        Wakander wakander = DataHelper.criaWakander();

        FiadorDTO fiadorDTO = FinanceiroDataHelper.criaFiadorDTO();

        when(autenticacaoService.buscaWakanderPeloToken(token)).thenReturn(wakander);
        when(asaasClient.atualizaDadosFiador(wakander.getFiador().getIdAsaas(), fiadorDTO)).thenReturn(null);

        assinaturaAsaasApplicationService.atualizaDadosFiador(token, fiadorDTO);

        verify(autenticacaoService, times(1)).buscaWakanderPeloToken(token);
        verify(asaasClient, times(1)).atualizaDadosFiador(wakander.getFiador().getIdAsaas(), fiadorDTO);
        verify(wakanderService, times(0)).atualizaFiador(token, fiadorDTO);
    }
}