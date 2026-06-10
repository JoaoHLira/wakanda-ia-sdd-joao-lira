package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.config.security.TokenService;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaType;
import academy.wakanda.wakanda_ai.financeiro.infra.AsaasClient;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssinaturaProcessorCriadaTest {
    @InjectMocks
    private AssinaturaProcessorCriada assinaturaProcessorCriada;
    @Mock
    private WakanderRepository wakanderRepository;
    @Mock
    private AsaasClient asaasClient;
    @Mock
    private TopicNames topicNames;
    @Mock
    private PublicadorNotificacaoSns publicadorNotificacaoSNS;
    @Mock
    private TokenService tokenService;

    private final String urlInstancia = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(assinaturaProcessorCriada, "urlInstancia", urlInstancia);
        loadTemplates(Templates.BASE_PACKAGE);
    }

    @Test
    @DisplayName("Deve validar se o evento recebido eh processavel")
    void deveValidarSeProcessaEvento() {
        AssinaturaEvento evento = new AssinaturaEvento(
                DataHelper.criaAssinaturaAsaasDto(AssinaturaType.SUBSCRIPTION_CREATED));

        boolean result = assinaturaProcessorCriada.validaSeEventoProcessa(evento);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve processar evento e criar Wakander incompleto")
    void deveProcessarEventoECriarWakander() {
        AssinaturaEvento evento = new AssinaturaEvento(
                DataHelper.criaAssinaturaAsaasDto(AssinaturaType.SUBSCRIPTION_CREATED));
        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        APIException thrown = APIException.build(HttpStatus.NOT_FOUND, "Wakander não encontrado para assinatura!");

        when(wakanderRepository.buscaWakanderPorIdAssinatura(any())).thenThrow(thrown);
        when(asaasClient.buscaCliente(any())).thenReturn(DataHelper.criaClienteAsaasDto());
        when(wakanderRepository.save(any())).thenReturn(wakander);

        doNothing().when(publicadorNotificacaoSNS).enviaNotificacaoSns(any(), any(), any());

        assinaturaProcessorCriada.processaEvento(evento);

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusException());
        verify(wakanderRepository, times(1)).buscaWakanderPorIdAssinatura(any());
        verify(asaasClient, times(1)).buscaCliente(any());
    }

    @Test
    @DisplayName("Deve processar evento sem criar novo Wakander")
    void deveProcessarEventoSemCriarWakander() {
        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        AssinaturaEvento evento = new AssinaturaEvento(
                DataHelper.criaAssinaturaAsaasDto(AssinaturaType.SUBSCRIPTION_CREATED));

        when(wakanderRepository.buscaWakanderPorIdAssinatura(any())).thenReturn(Optional.of(wakander));
        assinaturaProcessorCriada.processaEvento(evento);

        verify(wakanderRepository, times(1)).buscaWakanderPorIdAssinatura(any());
        verify(asaasClient, never()).buscaCliente(any());
        verify(wakanderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando status for diferente de NOT_FOUND")
    void deveLancarExceptionQuandoStatusDiferenteDeNotFound() {
        AssinaturaEvento evento = new AssinaturaEvento(
                DataHelper.criaAssinaturaAsaasDto(AssinaturaType.SUBSCRIPTION_CREATED));
        APIException excecaoStatusDiferente = APIException.build(HttpStatus.BAD_REQUEST, "Erro genérico");

        when(wakanderRepository.buscaWakanderPorIdAssinatura(any())).thenThrow(excecaoStatusDiferente);

        APIException exception = assertThrows(APIException.class,
                () -> assinaturaProcessorCriada.processaEvento(evento));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        verify(asaasClient, never()).buscaCliente(any());
        verify(wakanderRepository, never()).save(any());
    }
}

