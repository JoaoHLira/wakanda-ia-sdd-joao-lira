package academy.wakanda.wakanda_ai.comunicacao.infra;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Instant;

@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoSendSqs {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.queue.zapi-requests-delay}")
    private String urlFila;

    public void enviaMensagemZAPIComDelay(ZApiEventDto payload, int index)  {
        log.info("Enviando mensagem para SQS com delay de 10s: {}", payload);
        try {
            SqsMessageDto sqsMessageDto = new SqsMessageDto(payload.getWhatsapp(), null, objectMapper.writeValueAsString(payload), Instant.now());

            String body = objectMapper.writeValueAsString(sqsMessageDto);
            SendMessageRequest req = SendMessageRequest.builder()
                    .queueUrl(urlFila)
                    .messageBody(body)
                    .delaySeconds(Math.min(10 * index, 850))
                    .build();
            sqsClient.sendMessage(req);
        } catch (JsonProcessingException e) {
            log.error("Não foi possível serializar SqsMessageDto", e);
            throw new RuntimeException(e);
        }
    }

}
