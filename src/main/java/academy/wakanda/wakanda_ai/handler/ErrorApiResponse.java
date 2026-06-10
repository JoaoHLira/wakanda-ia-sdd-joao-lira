package academy.wakanda.wakanda_ai.handler;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Schema(description = "Detalhes do erro da API")
public class ErrorApiResponse {
	@Schema(description = "Mensagem de erro detalhada", example = "Por favor, verifique os dados fornecidos e tente novamente.")
	private String message;

	@Schema(description = "Descrição do erro", example = "Erro na solicitação")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String description;
}