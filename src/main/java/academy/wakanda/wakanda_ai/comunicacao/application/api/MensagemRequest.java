package academy.wakanda.wakanda_ai.comunicacao.application.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Informações para envio de mensagem via WhatsApp")
public class MensagemRequest {

    @NotEmpty(message = "O número do WhatsApp não pode estar vazio")
    @Schema(description = "Número do WhatsApp para envio", example = "5511987654321")
    private String whatsapp;

    @NotEmpty(message = "A mensagem não pode estar vazia")
    @Schema(description = "Conteúdo da mensagem a ser enviada", example = "Olá, esta é uma mensagem de teste!")
    private String mensagem;
}