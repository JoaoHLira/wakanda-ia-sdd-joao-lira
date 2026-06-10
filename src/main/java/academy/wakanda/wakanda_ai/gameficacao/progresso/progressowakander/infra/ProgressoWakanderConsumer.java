package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.infra;

import academy.wakanda.wakanda_ai.comunicacao.infra.SqsMessageDto;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderService;
import academy.wakanda.wakanda_ai.handler.APIException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProgressoWakanderConsumer {

    private final ObjectMapper objectMapper;
    private final ProgressoWakanderService progressoWakanderService;

    @SqsListener("${aws.queue.progresso-wakander-requests}")
    public void consomeQueueProgressoWakander(SqsMessageDto sqsMessageDto) {
        log.info("[start] ProgressoWakanderConsumer - consomeQueueProgressoWakander");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            ProgressoWakanderEventDto progressoWakander = deserializesqsMessageContent(sqsMessageDto, ProgressoWakanderEventDto.class);
            progressoWakanderService.processaPorTipoProgresso(progressoWakander);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ProgressoWakanderConsumer - consomeQueueProgressoWakander");
    }

    public <T> T deserializesqsMessageContent(SqsMessageDto sqsMessageDto, Class<T> clazz)
            throws JsonProcessingException {
        return objectMapper.readValue(sqsMessageDto.getMessage(), clazz);
    }
}
