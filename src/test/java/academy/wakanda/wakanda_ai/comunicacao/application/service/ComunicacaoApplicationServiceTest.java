package academy.wakanda.wakanda_ai.comunicacao.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.api.*;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCanceladoClintDTO;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderContato;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper.criaZAPIPayloadAdicionaAoGrupo;
import static academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper.mensagemParaSerEnviada;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacaoApplicationServiceTest {
    @InjectMocks
    ComunicacaoApplicationService comunicacaoApplicationService;
    @Mock
    DiscordClient discordService;
    @Mock
    ZApiClient zapiClient;
    @Mock
    PublicadorNotificacaoSns publicadorNotificacaoSNS;
    @Mock
    TopicNames topicNames;
    @Mock
    ClintCRM clintInfra;
    @Mock
    CancelaClintService cancelaClintService;

    @DisplayName("Deve Enviar Mensagem via WhatsApp Quando Enviado com Sucesso")
    @Test
    void enviaMensagemWhatsapp_SucessoQuandoDadosValidos() {
        MensagemRequest mensagemRequest = mensagemParaSerEnviada();
        comunicacaoApplicationService.enviaMensagemWhatsapp(mensagemRequest);

        assertEquals("5511999999999", mensagemRequest.getWhatsapp());
        assertEquals("Teste", mensagemRequest.getMensagem());

        verify(zapiClient, times(1)).enviaMensagemWhatsApp(any(ZAPIPayload.class));
    }

    @DisplayName("Deve Lançar Exceção Quando Falha ao Enviar Mensagem via WhatsApp")
    @Test
    void enviaMensagemWhatsapp_FalhaQuandoErroNoEnvio() {
        MensagemRequest mensagemRequest = mensagemParaSerEnviada();

        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "Erro ao enviar mensagem via WhatsApp"))
                .when(zapiClient)
                .enviaMensagemWhatsApp(any(ZAPIPayload.class));

        assertThrows(APIException.class, () -> {
            comunicacaoApplicationService.enviaMensagemWhatsapp(mensagemRequest);
        });
    }

    @DisplayName("Deve Adicionar Wakander ao Grupo com Sucesso Quando Dados Corretos")
    @Test
    void adicionaWakanderAoGrupo_SucessoQuandoDadosCorretos() {
    	ZAPIResponseGrupo responseGrupo = ComunicacaoDataHelper.criaZAPIResponseGrupo();
        ZAPIPayloadAdiconaAoGrupo adiconaAoGrupo = criaZAPIPayloadAdicionaAoGrupo();
        when(zapiClient.processaRequisicaoGrupoWhatsapp(adiconaAoGrupo, ZAPITypeGrupo.ADD_TO_GROUP.getDescricao()))
        .thenReturn(responseGrupo);

        comunicacaoApplicationService.adicionaWakanderAoGrupo(adiconaAoGrupo);

        verify(zapiClient, times(1)).processaRequisicaoGrupoWhatsapp(adiconaAoGrupo, ZAPITypeGrupo.ADD_TO_GROUP.getDescricao());
        assertEquals("5511999999999", adiconaAoGrupo.getPhones()[0]);
        assertEquals("12345-group", adiconaAoGrupo.getGroupId());
    }

    @DisplayName("Deve Lançar Exceção Quando Falha ao Adicionar Wakander ao Grupo")
    @Test
    void adicionaWakanderAoGrupo_LancaExcecaoQuandoErroNaComunicacao() {

        ZAPIPayloadAdiconaAoGrupo adiconaAoGrupo = criaZAPIPayloadAdicionaAoGrupo();

        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "Erro de comunnicação com ZAPi"))
                .when(zapiClient)
                .processaRequisicaoGrupoWhatsapp(any(), any());

        assertThrows(APIException.class, () -> {
            comunicacaoApplicationService.adicionaWakanderAoGrupo(adiconaAoGrupo);
        });
    }

    @Test
    void deveConvidarParaCanalDiscordComSucesso() {
        DiscordConviteRequest conviteRequest = new DiscordConviteRequest(1, true, 3600);
        DiscordConviteResponse expectedResponse = new DiscordConviteResponse("ABC123");

        when(discordService.criaConviteDoCanalParaWakander(conviteRequest)).thenReturn(expectedResponse);

        DiscordConviteResponse actualResponse = comunicacaoApplicationService.convidaParaCanalDiscord(conviteRequest);

        assertNotNull(actualResponse);
        assertEquals("ABC123", actualResponse.getCode());
        verify(discordService, times(1)).criaConviteDoCanalParaWakander(conviteRequest);
    }

    @Test
    void deveLancarExcecaoQuandoFalharAoConvidarParaCanalDiscord() {
        DiscordConviteRequest conviteRequest = new DiscordConviteRequest(1, true, 3600);

        when(discordService.criaConviteDoCanalParaWakander(conviteRequest))
                .thenThrow(new RuntimeException("Erro ao criar convite"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            comunicacaoApplicationService.convidaParaCanalDiscord(conviteRequest);
        });

        assertEquals("Erro ao criar convite", exception.getMessage());
        verify(discordService, times(1)).criaConviteDoCanalParaWakander(conviteRequest);
    }

    @Test
    @DisplayName("Deve enviar uma mensagem para o Publicador SNS")
    void deveEnviarMensagemPublicadorSNS() {
        String mensagem = "Hello Word!";
        NotificacaoRequest request = new NotificacaoRequest("teste-grupo", mensagem);

        doNothing().when(publicadorNotificacaoSNS).enviaNotificacaoSns(any(), any(), any());
        comunicacaoApplicationService.publicaNotificacao(request);

        verify(publicadorNotificacaoSNS, times(1)).enviaNotificacaoSns(request.getGroupId(), request.getMessage(),
                topicNames.getTeste());
    }

    @Test
    @DisplayName("Deve enviar contato para Clint com sucesso")
    void enviaContatoParaClint_SucessoQuandoDadosValidos() {
        ClintContatoRequest contatoRequest = ComunicacaoDataHelper.criaClintContatoRequest();
        ClintResponse clintResponse = new ClintResponse(true, "Contato enviado com sucesso");
        when(clintInfra.enviaContatoParaClint(any(ClintContatoRequest.class)))
                .thenReturn(clintResponse);

        comunicacaoApplicationService.enviaContatoParaClint(contatoRequest);

        verify(clintInfra, times(1)).enviaContatoParaClint(contatoRequest);
    }

    @Test
    @DisplayName("Não deve notificar grupo de líderes quando envio para Clint falhar")
    void enviaContatoParaClint_NaoNotificaQuandoFalha() {
        ClintContatoRequest contatoRequest = ComunicacaoDataHelper.criaClintContatoRequest();
        ClintResponse clintResponse = new ClintResponse(false, "Falha ao enviar contato");

        when(clintInfra.enviaContatoParaClint(any(ClintContatoRequest.class)))
                .thenReturn(clintResponse);

        comunicacaoApplicationService.enviaContatoParaClint(contatoRequest);

        verify(clintInfra, times(1)).enviaContatoParaClint(contatoRequest);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer erro ao enviar contato para Clint")
    void enviaContatoParaClint_LancaExcecaoQuandoErro() {
        ClintContatoRequest contatoRequest = ComunicacaoDataHelper.criaClintContatoRequest();

        when(clintInfra.enviaContatoParaClint(any(ClintContatoRequest.class)))
                .thenThrow(APIException.build(HttpStatus.BAD_REQUEST, "Erro ao enviar contato para Clint"));

        assertThrows(APIException.class, () -> {
            comunicacaoApplicationService.enviaContatoParaClint(contatoRequest);
        });

        verify(clintInfra, times(1)).enviaContatoParaClint(contatoRequest);
        verify(zapiClient, never()).enviaMensagemWhatsApp(any(ZAPIPayload.class));
    }

    @DisplayName("Deve remover Wakander do Grupo com Sucesso Quando Dados Corretos")
    @Test
    void removeWakanderDoGrupo_SucessoQuandoDadosCorretos() {
    	ZAPIResponseGrupo responseGrupo = ComunicacaoDataHelper.criaZAPIResponseGrupo();
    	ZAPIPayloadRemoveDoGrupo request = ComunicacaoDataHelper.criaZAPIPayloadRemoveDoGrupo();

        when(zapiClient.processaRequisicaoGrupoWhatsapp(request, ZAPITypeGrupo.REMOVE_TO_GROUP.getDescricao()))
        .thenReturn(responseGrupo);

        comunicacaoApplicationService.removeWakanderDoGrupo(request);

        verify(zapiClient, times(1)).processaRequisicaoGrupoWhatsapp(request, ZAPITypeGrupo.REMOVE_TO_GROUP.getDescricao());
        assertEquals("5511999999999", request.getPhones()[0]);
        assertEquals("12345-group", request.getGroupId());
    }

    @DisplayName("Deve Lançar Exceção Quando Falha ao Remover Wakander do Grupo")
    @Test
    void removeWakanderDoGrupo_LancaExcecaoQuandoErroNaComunicacao() {

    	ZAPIPayloadRemoveDoGrupo request = ComunicacaoDataHelper.criaZAPIPayloadRemoveDoGrupo();

        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "Erro de comunnicação com ZAPi"))
                .when(zapiClient)
                .processaRequisicaoGrupoWhatsapp(any(), any());

        assertThrows(APIException.class, () -> {
            comunicacaoApplicationService.removeWakanderDoGrupo(request);
        });
    }


    @Test
    @DisplayName("Deve cancelar Wakander na Clint com Sucesso")
    void deveCancelarWakanderNoClintComSucesso() {
        Wakander wakander = mock(Wakander.class);

        when(wakander.getContato()).thenReturn(mock(WakanderContato.class));
        when(wakander.getIdWakander()).thenReturn(UUID.randomUUID());
        when(cancelaClintService.cancelaWakanderClint(any()))
                .thenReturn(new ClintResponse(true, "Cancelado com sucesso"));

        comunicacaoApplicationService.cancelaWakanderClint(wakander);

        verify(cancelaClintService).cancelaWakanderClint(any(WakanderCanceladoClintDTO.class));
    }

    @Test
    @DisplayName("Deve enviar mensagem de sucesso quando cancelado na Clint")
    void deveEnviarMensagemSucessoQuandoCanceladoNaClint() {
        Wakander wakander = mock(Wakander.class);
        when(wakander.getContato()).thenReturn(mock(WakanderContato.class));
        UUID id = UUID.randomUUID();
        when(wakander.getIdWakander()).thenReturn(id);
        when(cancelaClintService.cancelaWakanderClint(any()))
                .thenReturn(new ClintResponse(true, "Cancelado com sucesso"));

        comunicacaoApplicationService.cancelaWakanderClint(wakander);

        verify(publicadorNotificacaoSNS).enviaNotificacaoSns(eq(id.toString()), any(ZApiEventDto.class), any());
    }
}
