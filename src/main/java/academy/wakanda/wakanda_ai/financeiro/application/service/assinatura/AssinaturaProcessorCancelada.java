package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaType;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class AssinaturaProcessorCancelada implements AssinaturaProcessorAsaas {
	private final WakanderService wakanderService;
	private final PublicadorNotificacaoSns publicadorNotificacaoSns;
	private final TopicNames topicNames;
	@Value("${z-api.lideres-group-id}")
	private String lideresPhoneGroupId;

	@Override
	public boolean validaSeEventoProcessa(AssinaturaEvento assinaturaEvento) {
		return assinaturaEvento.getEvent().equals(AssinaturaType.SUBSCRIPTION_DELETED);
	}

	@Override
	public void processaEvento(AssinaturaEvento assinaturaEvento) {
		log.info("[start] AssinaturaCanceladaProcessor - processaEvento");
		try {
			Wakander wakander = wakanderService.buscaWakanderPorIdAssinatura(assinaturaEvento.getId());
			wakanderService.solicitaCancelamentoWakander(wakander);
		} catch (APIException e) {
			log.info("Erro ao cancelar assinatura com ID: {}", assinaturaEvento.getId());
			publicaMensagem(assinaturaEvento.getId());
		}
		log.debug("[finish] AssinaturaCanceladaProcessor - processaEvento");
	}

	private void publicaMensagem(String idAssinatura) {
		log.info("[start] WakanderApplicationService - publicaSnsMensagemWhatsapp");
		String mensagem = MensagensWhatsapp.NOTIFICA_ERRO_CANCELAMENTO_PADRAO.getMensagem(idAssinatura);
		ZApiEventDto ZApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, lideresPhoneGroupId, mensagem);
		publicadorNotificacaoSns.enviaNotificacaoSns(idAssinatura, ZApiEventDto, topicNames.getZapiRequests());
		log.debug("[finish] WakanderApplicationService - publicaSnsMensagemWhatsapp");
	}
}
