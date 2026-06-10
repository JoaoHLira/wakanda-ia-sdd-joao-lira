package academy.wakanda.wakanda_ai.comunicacao.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.api.*;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCanceladoClintDTO;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ComunicacaoApplicationService implements ComunicacaoService {
	private final ZApiClient zapiClient;
	private final DiscordClient discordClient;
	private final PublicadorNotificacaoSns publicadorNotificacaoSns;
	private final TopicNames topicNames;
    private final ClintCRM clintInfra;
    private final CancelaClintService cancelaClintService;
    @Value("${z-api.lideres-group-id}")
    private String lideresPhoneGroupId;

	@Override
	public void enviaMensagemWhatsapp(MensagemRequest request) {
		log.info("[start] ComunicacaoApplicationService - enviaMensagem");
		zapiClient.enviaMensagemWhatsApp(new ZAPIPayload(request));
		log.debug("[finish] ComunicacaoApplicationService - enviaMensagem");
	}

	@Override
	public void adicionaWakanderAoGrupo(ZAPIPayloadAdiconaAoGrupo zapiPayloadAdiconaAoGrupo) {
		log.info("[start] ComunicacaoApplicationService - adicionaWakanderAoGrupo");
		processaRequisicaoGrupoWhatsapp(zapiPayloadAdiconaAoGrupo, "adicionar", ZAPITypeGrupo.ADD_TO_GROUP.getDescricao());
		log.debug("[finish] ComunicacaoApplicationService - adicionaWakanderAoGrupo");
	}

	@Override
	public void publicaNotificacao(NotificacaoRequest request) {
		log.info("[start] ComunicacaoApplicationService - publicaNotificacao");
		publicadorNotificacaoSns.enviaNotificacaoSns(request.getGroupId(), request.getMessage(), topicNames.getTeste());
		log.debug("[finish] ComunicacaoApplicationService - publicaNotificacao");
	}

	@Override
	public DiscordConviteResponse convidaParaCanalDiscord(DiscordConviteRequest conviteRequest) {
		log.info("[start] ComunicacaoApplicationService - convidaWakanderParaCanalDiscord");
		DiscordConviteResponse conviteResponse = discordClient.criaConviteDoCanalParaWakander(conviteRequest);
		log.debug("[finish] ComunicacaoApplicationService - convidaWakanderParaCanalDiscord");
		return conviteResponse;
	}

	@Override
	public void removeWakanderDoGrupo(ZAPIPayloadRemoveDoGrupo payloadRemoveDoGrupo ) {
		log.info("[start] ComunicacaoApplicationService - removeWakanderDoGrupo");
		processaRequisicaoGrupoWhatsapp(payloadRemoveDoGrupo, "remover", ZAPITypeGrupo.REMOVE_TO_GROUP.getDescricao());
		log.debug("[finish] ComunicacaoApplicationService - removeWakanderDoGrupo");
	}

	private <T> void processaRequisicaoGrupoWhatsapp(T payload, String acao, String typeRequisicao) {
		log.info("[start] ComunicacaoApplicationService - processaRequisicaoGrupoWhatsapp");
		ZAPIResponseGrupo response = zapiClient.processaRequisicaoGrupoWhatsapp(payload, typeRequisicao);
		validaRespostaZAPI(response, acao);
		log.debug("[finish] ComunicacaoApplicationService - processaRequisicaoGrupoWhatsapp");
	}

	private void validaRespostaZAPI(ZAPIResponseGrupo response, String acao) {
		if (!response.validaRequisicao()) {
			log.error("Falha ao {} Wakander no grupo via Z-API. Status: {}", acao, response.getValue());
			throw APIException.build(HttpStatus.BAD_GATEWAY, "Falha ao " + acao + " Wakander no grupo via Z-API.");
		}
	}

    @Override
    public void enviaContatoParaClint(ClintContatoRequest contatoClint) {
        log.info("[start] ComunicacaoApplicationService - enviaContatoParaClint");
        contatoClint.formataDataNascimento(contatoClint.getDataNascimento());
        ClintResponse clintResponse = clintInfra.enviaContatoParaClint(contatoClint);
        if (clintResponse.getSuccess()) {
            enviaMensagemGrupoLideres(contatoClint);
        }
    }

    private void enviaMensagemGrupoLideres(ClintContatoRequest contatoClint) {
        log.info("[start] ComunicacaoApplicationService - enviaMensagemGrupoLideres");
        String mensagemEnviadoComSucesso = MensagensWhatsapp.MENSAGEM_CONTATO_ENVIADO_CLINT.mensagemContatoEnviadoClint(
                contatoClint.getNome());
        publicadorNotificacaoSns.enviaNotificacaoSns(
                contatoClint.getIdWakander().toString(),
                new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, lideresPhoneGroupId, mensagemEnviadoComSucesso),
                topicNames.getZapiRequests());
        log.debug("[finish] ComunicacaoApplicationService - enviaMensagemGrupoLideres: Nome: {}",
                contatoClint.getNome());
    }

    @Override
    public void cancelaWakanderClint(Wakander wakander) {
        log.info("[start] ComunicacaoApplicationService - cancelaWakanderClint");
        WakanderCanceladoClintDTO wakanderCanceladoClint = new WakanderCanceladoClintDTO(wakander);
        ClintResponse clintResponse = cancelaClintService.cancelaWakanderClint(wakanderCanceladoClint);
        if (Boolean.TRUE.equals(clintResponse.getSuccess())) {
            enviaMsgCancelamentoClint(wakander, true);
        }
        log.debug("[finish] ComunicacaoApplicationService - cancelaWakanderClint");
    }

    public void enviaMsgCancelamentoClint(Wakander wakander, boolean success) {
        log.info("[start] ComunicacaoApplicationService - enviaMsgCancelamentoClint");
        String mensagem = success
                ? MensagensWhatsapp.MENSAGEM_SUCESSO_CANCELAMENTO_CLINT.mensagemCancelamentoClint(wakander)
                : MensagensWhatsapp.MENSAGEM_FALHA_CANCELAMENTO_CLINT.mensagemCancelamentoClint(wakander);
        publicadorNotificacaoSns.enviaNotificacaoSns(
                wakander.getIdWakander().toString(),
                new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, lideresPhoneGroupId, mensagem),
                topicNames.getZapiRequests());
        log.debug("[finish] ComunicacaoApplicationService - enviaMsgCancelamentoClint");
    }
}
