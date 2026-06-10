package academy.wakanda.wakanda_ai.comunicacao.infra;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayload;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayloadAdiconaAoGrupo;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayloadRemoveDoGrupo;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPITypeGrupo;
import academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper;
import academy.wakanda.wakanda_ai.handler.APIException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper.criaZAPIPayloadAdicionaAoGrupo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
class ZApiInfraClientTest {

    private WireMockServer wireMockServer;
    private ZApiInfraClient zApiInfraClient;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        WebClient.Builder builder = WebClient.builder()
                .baseUrl(wireMockServer.baseUrl());

        zApiInfraClient = new ZApiInfraClient(builder);

        ReflectionTestUtils.setField(zApiInfraClient, "instancia", "12345");
        ReflectionTestUtils.setField(zApiInfraClient, "tokenInstancia", "54321");
        ReflectionTestUtils.setField(zApiInfraClient, "clienteToken", "15243");
        ReflectionTestUtils.setField(zApiInfraClient, "baseUrl", wireMockServer.baseUrl());
    }

    @AfterEach
    void teardown() {
        wireMockServer.stop();
    }

    @DisplayName("Teste para quando o Wakander é adicionado com sucesso ao grupo")
    @Test
    void adicionaWakanderAoGrupoWhatsapp_Sucesso() {

        wireMockServer.stubFor(
                post(urlEqualTo("/instances/12345/token/54321/add-participant"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"value\":true}"))
        );

        ZAPIPayloadAdiconaAoGrupo payload = criaZAPIPayloadAdicionaAoGrupo();

        zApiInfraClient.processaRequisicaoGrupoWhatsapp(payload, ZAPITypeGrupo.ADD_TO_GROUP.getDescricao());

        wireMockServer.verify(postRequestedFor(urlEqualTo("/instances/12345/token/54321/add-participant"))
                .withRequestBody(containing("5511999999999"))
                .withRequestBody(containing("12345-group")));
    }

    @DisplayName("Teste para quando o Wakander não é adicionado ao grupo")
    @Test
    void adiconaWakanderAoGrupoWhatsapp_Falha() {

        wireMockServer.stubFor(
                WireMock.post(urlEqualTo("/instances/12345/token/54321/add-participant"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"value\":false}"))
        );

        ZAPIPayloadAdiconaAoGrupo zapiPayloadAdiconaAoGrupo = criaZAPIPayloadAdicionaAoGrupo();

        zApiInfraClient.processaRequisicaoGrupoWhatsapp(zapiPayloadAdiconaAoGrupo, ZAPITypeGrupo.ADD_TO_GROUP.getDescricao());

        wireMockServer.verify(postRequestedFor(urlEqualTo("/instances/12345/token/54321/add-participant"))
                .withRequestBody(containing("5511999999999"))
                .withRequestBody(containing("12345-group")));
    }

    @DisplayName("Teste para quando ocorre erro no envio e APIException é lançada")
    @Test
    void adiconaWakanderAoGrupoWhatsapp_LançaExcecao() {

        wireMockServer.stubFor(
                WireMock.post(urlEqualTo("/instances/12345/token/54321/add-participant"))
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"exception\":\"Bad Request\"}"))
        );

        ZAPIPayloadAdiconaAoGrupo zapiPayloadAdiconaAoGrupo = criaZAPIPayloadAdicionaAoGrupo();

        APIException exception = assertThrows(APIException.class, () -> {
            zApiInfraClient.processaRequisicaoGrupoWhatsapp(zapiPayloadAdiconaAoGrupo, ZAPITypeGrupo.ADD_TO_GROUP.getDescricao());
        });

        assertTrue(exception.getMessage().contains("Bad Request"));
    }

    @Test
    @DisplayName("Deve lançar APIException com status 500 quando ocorrer erro inesperado")
    void adiconaWakanderAoGrupoWhatsapp_ErroInesperado() {

        wireMockServer.stubFor(
                post(urlEqualTo("/instances/12345/token/54321/add-participant"))
                        .willReturn(aResponse()
                                .withFault(Fault.CONNECTION_RESET_BY_PEER))
        );

        ZAPIPayloadAdiconaAoGrupo zapiPayloadAdiconaAoGrupo = criaZAPIPayloadAdicionaAoGrupo();

        APIException exception = assertThrows(APIException.class, () -> {
            zApiInfraClient.processaRequisicaoGrupoWhatsapp(zapiPayloadAdiconaAoGrupo, ZAPITypeGrupo.ADD_TO_GROUP.getDescricao());
        });

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusException());
        Assertions.assertEquals("Erro inesperado ao fazer a requisição ao Z-API.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve enviar mensagem WhatsApp com sucesso")
    void enviaMensagemWhatsApp_Sucesso() {

        wireMockServer.stubFor(
                post(urlEqualTo("/instances/12345/token/54321/send-text"))
                        .withHeader("Client-Token", equalTo("15243"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json"))
        );

        ZAPIPayload zapiPayload = ComunicacaoDataHelper.criaZapiPayload();

        zApiInfraClient.enviaMensagemWhatsApp(zapiPayload);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/instances/12345/token/54321/send-text"))
                .withRequestBody(containing("5511999999999"))
                .withRequestBody(containing("Mensagem de Teste")));
    }

    @Test
    @DisplayName("Deve lançar APIException quando a API retornar erro 400")
    void enviaMensagemWhatsApp_Erro400() {

        wireMockServer.stubFor(
                post(urlEqualTo("/instances/12345/token/54321/send-text"))
                        .withHeader("Client-Token", equalTo("15243"))
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"exception\":\"Parâmetros inválidos\"}"))
        );

        ZAPIPayload zapiPayload = ComunicacaoDataHelper.criaZapiPayload();

        APIException exception = assertThrows(APIException.class, () -> {
            zApiInfraClient.enviaMensagemWhatsApp(zapiPayload);
        });

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        Assertions.assertTrue(exception.getMessage().contains("400 Bad Request"));

        wireMockServer.verify(postRequestedFor(urlEqualTo("/instances/12345/token/54321/send-text")));
    }

    @Test
    @DisplayName("Deve lançar APIException com status 500 quando ocorrer erro inesperado")
    void enviaMensagemWhatsApp_ErroInesperado() {

        wireMockServer.stubFor(
                post(urlEqualTo("/instances/12345/token/54321/send-text"))
                        .willReturn(aResponse()
                                .withFault(Fault.CONNECTION_RESET_BY_PEER))
        );

        ZAPIPayload zapiPayload = ComunicacaoDataHelper.criaZapiPayload();

        APIException exception = assertThrows(APIException.class, () -> {
            zApiInfraClient.enviaMensagemWhatsApp(zapiPayload);
        });

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusException());
        Assertions.assertEquals("Erro inesperado ao enviar mensagem.", exception.getMessage());
    }
    
    @DisplayName("Teste para quando o Wakander é removido com sucesso do grupo")
    @Test
    void removeWakanderDoGrupoWhatsapp_Sucesso() {

        wireMockServer.stubFor(
                post(urlEqualTo("/instances/12345/token/54321/remove-participant"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"value\":true}"))
        );

        ZAPIPayloadRemoveDoGrupo payload = ComunicacaoDataHelper.criaZAPIPayloadRemoveDoGrupo();

        zApiInfraClient.processaRequisicaoGrupoWhatsapp(payload, ZAPITypeGrupo.REMOVE_TO_GROUP.getDescricao());

        wireMockServer.verify(postRequestedFor(urlEqualTo("/instances/12345/token/54321/remove-participant"))
                .withRequestBody(containing("5511999999999"))
                .withRequestBody(containing("12345-group")));
    }
    
    @DisplayName("Teste para quando o Wakander não é removido do grupo")
    @Test
    void removeWakanderDoGrupoWhatsapp_Falha() {

        wireMockServer.stubFor(
                WireMock.post(urlEqualTo("/instances/12345/token/54321/remove-participant"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"value\":false}"))
        );

        ZAPIPayloadRemoveDoGrupo payload = ComunicacaoDataHelper.criaZAPIPayloadRemoveDoGrupo();

        zApiInfraClient.processaRequisicaoGrupoWhatsapp(payload, ZAPITypeGrupo.REMOVE_TO_GROUP.getDescricao());

        wireMockServer.verify(postRequestedFor(urlEqualTo("/instances/12345/token/54321/remove-participant"))
                .withRequestBody(containing("5511999999999"))
                .withRequestBody(containing("12345-group")));
    }
}
