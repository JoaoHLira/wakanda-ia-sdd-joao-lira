package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.infra;

import academy.wakanda.wakanda_ai.comunicacao.infra.SqsMessageDto;
import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;
import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpWakanderEventDTO;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.event.XpPromocaoClasseEvent;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.service.XpWakanderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class XpWakanderConsumer {

	private final ObjectMapper objectMapper;
	private final XpWakanderService xpWakanderService;

	@SqsListener("${aws.queue.xp-wakander-requests}")
	public void consumeXpWakanderQueueMessage(SqsMessageDto sqsMessageDto) {
		log.info("[start] XpWakanderConsumer - consumeXpWakanderQueueMessage");
		log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
		try {
			XpWakanderEventDTO xpWakanda = deserializesqsMessageContent(sqsMessageDto, XpWakanderEventDTO.class);
			xpWakanderService.processaXP(xpWakanda);
		} catch (JsonProcessingException e) {
			log.info("[error] Não foi possível mapear o json!");
		}
		log.debug("[finish] XpWakanderConsumer - consumeXpWakanderQueueMessage");
	}

	public <T> T deserializesqsMessageContent(SqsMessageDto sqsMessageDto, Class<T> clazz)
			throws JsonProcessingException {
		return objectMapper.readValue(sqsMessageDto.getMessage(), clazz);
	}

    @EventListener
    public void promoveClasseXpWakander(XpPromocaoClasseEvent evento) {
        log.info("[start] XpWakanderConsumer - promoveClasseXpWakander");
        log.debug("[received] Mensagem: {}", evento.toString());
        XpPromocaoClasseDTO xpPromocaoClasseDTO = new XpPromocaoClasseDTO(evento);
        xpWakanderService.processaPromocaoClasse(xpPromocaoClasseDTO);
        log.debug("[finish] XpWakanderConsumer - promoveClasseXpWakander");
    }

}
