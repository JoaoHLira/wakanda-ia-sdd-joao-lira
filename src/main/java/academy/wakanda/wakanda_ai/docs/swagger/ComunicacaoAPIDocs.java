package academy.wakanda.wakanda_ai.docs.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.MediaType;

import academy.wakanda.wakanda_ai.handler.ErrorApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public @interface ComunicacaoAPIDocs {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "Envia uma mensagem pelo WhatsApp", description = "Este método permite enviar uma mensagem utilizando um número de WhatsApp válido")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "204", description = "Mensagem enviada com sucesso"),
			@ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorApiResponse.class), examples = @ExampleObject(value = "{ \"whatsapp\": \"O número do WhatsApp não pode estar vazio\", \"mensagem\": \"A mensagem não pode estar vazia\" }"))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }"))) })
	public @interface EnvioMensagem {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "Envia uma mensagem de teste para SNS e SQS", description = "Este método permite enviar uma notificação para um tópico de teste que será consumido e logado por um consumidor SQS.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Mensagem enviada com sucesso"),
			@ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n"
					+ "  \"groupId\": \"O campo groupId não pode ser vazio.\",\n"
					+ "  \"message\": \"O campo message não pode ser vazio.\"\n" + "}"))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }"))) })
	public @interface PublicaNotificacaoTeste {
	}
}