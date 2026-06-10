package academy.wakanda.wakanda_ai.comunicacao.application.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
@Schema(description = "Informações para publicação de uma mensagem no SNS.")
public class NotificacaoRequest {
    @Schema(description = "Identificador do grupo relacionado à mensagem.", example = "123456")
    @NotEmpty(message = "O campo groupId não pode ser vazio.")
    String groupId;
    @Schema(description = "Conteúdo da mensagem a ser publicada.", example = "Olá, esta é uma mensagem de teste!")
    @NotEmpty(message = "O campo message não pode ser vazio.")
    String message;
}