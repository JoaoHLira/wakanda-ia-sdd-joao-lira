package academy.wakanda.wakanda_ai.wakander.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import academy.wakanda.wakanda_ai.wakander.domain.DiscordEventype;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class AssinaturaCanceladaConsumer {
	private final PublicadorNotificacaoSns publicadorNotificacaoSns;
	private final TopicNames topicNames;
	private final ObjectMapper objectMapper;

	@EventListener
	public void removeWakanderDoGrupoWhatsapp(AssinaturaCanceladaEvent evento) {
		log.info("[start] AssinaturaCanceladaConsumer - removeWakanderDoGrupoWhatsapp");
		log.debug("[received] Mensagem: {}", evento.toString());
		log.debug("Remoção do grupo Whatsapp. Wakander: {}, Motivo:  {}, Data: {}", evento.getNome(),
				evento.getMotivoCancelamento(), evento.getUltimaAtualizacao());
		publicaMensagem(ZApiEventype.REMOVE_TO_GROUP, evento, evento.getNome());
		log.debug("[finish] AssinaturaCanceladaConsumer - removeWakanderDoGrupoWhatsapp");
	}

	private <T> void publicaMensagem(ZApiEventype eventype, AssinaturaCanceladaEvent evento, String mensagem) {
		log.info("[start] AssinaturaCanceladaConsumer - publicaMensagem");
		ZApiEventDto ZApiEventDto = new ZApiEventDto(eventype, evento.getTelefone(), mensagem);
		publicadorNotificacaoSns.enviaNotificacaoSns(evento.getIdWakander().toString(), ZApiEventDto,
				topicNames.getZapiRequests());
		log.debug("[finish] AssinaturaCanceladaConsumer - publicaMensagem");
	}

	@EventListener
	public void bloqueiaWakanderNoMemberKit(AssinaturaCanceladaEvent evento) {
		log.info("[start] AssinaturaCanceladaConsumer - bloqueiaWakanderNoMemberKit");
		JsonNode jsonNode = objectMapper.valueToTree(evento);
		MemberKitMessageEnvelope envelope = new MemberKitMessageEnvelope(MemberKitTipoRequisicao.ACESSO_BLOQUEADO,
				jsonNode);
		publicadorNotificacaoSns.enviaNotificacaoSns(evento.getIdWakander().toString(), envelope,
				topicNames.getMemberkitRequests());
		log.debug("[finish] AssinaturaCanceladaConsumer - bloqueiaWakanderNoMemberKit");
	}

	@EventListener
	public void removeWakanderDoServidorDiscord(AssinaturaCanceladaEvent evento) {
		log.info("[start] AssinaturaCanceladaConsumer - removeWakanderDoServidorDiscord");
		DiscordEventRequest canalComunicacaoEnvelope = new DiscordEventRequest(
				DiscordEventype.REMOVE_FROM_SERVER, evento);
		publicadorNotificacaoSns.enviaNotificacaoSns(evento.getIdWakander().toString(), canalComunicacaoEnvelope,
				topicNames.getDiscordRequest());
		log.debug("[finish] AssinaturaCanceladaConsumer - removeWakanderDoServidorDiscord");
	}
}