package academy.wakanda.wakanda_ai.comunicacao.infra;

import academy.wakanda.wakanda_ai.comunicacao.application.api.NotificacaoRequest;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsOperations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageDeliveryException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PublicadorNotificacaoInfraSnsTest {

    @Mock
    private SnsOperations snsOperations;

    @InjectMocks
    private PublicadorNotificacaoInfraSns publicadorNotificacaoInfraSNS;

    @Test
    @DisplayName("Deve publicar uma mensagem no SNS")
    void devePublicarMensagemNoSNS() {
   	 	NotificacaoRequest request = DataHelper.criaNotificatioRequest();

        publicadorNotificacaoInfraSNS.enviaNotificacaoSns(request.getGroupId(), request.getMessage(), "test-topic");
        verify(snsOperations).sendNotification(any(), any());
    }

    @Test
    @DisplayName("Deve lançar MessageDeliveryException quando SQS não for encontrada")
    void deveLancarExcecaoQuandoFalharEnvioMensagem() {
        String topico = "test-topic";
   	 	NotificacaoRequest request = DataHelper.criaNotificatioRequest();
        SnsNotification<String> notification = DataHelper.criaSnsNotification();

        doThrow(new MessageDeliveryException("Failed to send message to"))
            .when(snsOperations)
            .sendNotification(topico, notification);

        assertThrows(
        		PotentialStubbingProblem.class,
            () -> publicadorNotificacaoInfraSNS.enviaNotificacaoSns(request.getGroupId() , request.getMessage(), topico)
        );
    }
}