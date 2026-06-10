package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.comunicacao.infra.SqsMessageDto;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitRequestProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class JornadaWakanderConsumerSqs {

    private final ObjectMapper objectMapper;
    private final List<MemberKitRequestProcessor> processors;

    @SqsListener("${aws.queue.memberkit-requests}")
    public void consomeMensagemMemberkitRequests(SqsMessageDto sqsMessageDto) throws JsonProcessingException {
        log.info("[start] JornadaWakanderConsumerSqs - consomeMensagemMemberkitRequests");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        MemberKitMessageEnvelope envelope = objectMapper.readValue(sqsMessageDto.getMessage(), MemberKitMessageEnvelope.class);
        strategyMemberkitRequestProcessor(envelope);
        log.debug("[finish] JornadaWakanderConsumerSqs - consomeMensagemMemberkitRequests");
    }

    private void strategyMemberkitRequestProcessor(MemberKitMessageEnvelope envelope) {
        processors.stream()
                .filter(p -> p.validaSeProcessa(envelope.getTipo()))
                .findFirst()
                .ifPresent(p -> p.processaEvento(envelope));
    }
}
