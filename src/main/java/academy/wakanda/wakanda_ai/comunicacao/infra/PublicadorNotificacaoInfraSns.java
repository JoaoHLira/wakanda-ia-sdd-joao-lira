package academy.wakanda.wakanda_ai.comunicacao.infra;

import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@RequiredArgsConstructor
public class PublicadorNotificacaoInfraSns implements PublicadorNotificacaoSns {
    private final SnsOperations snsOperations;

    @Override
    public <T> void enviaNotificacaoSns(String groupId, T payload, String topic) {
        log.info("[start] PublicadorNotificacaoInfraSns - publicaMensagem");
        SnsNotification<T> notification = SnsNotification.<T>builder(payload)
                .deduplicationId(UUID.randomUUID().toString())
                .groupId(groupId)
                .build();
        log.debug("[sending] Notificação com valor \"{}\" para o tópico \"{}\"", payload.toString(), topic);
        snsOperations.sendNotification(topic, notification);
        log.debug("[finish] PublicadorNotificacaoInfraSns - publicaMensagem");
    }
}
