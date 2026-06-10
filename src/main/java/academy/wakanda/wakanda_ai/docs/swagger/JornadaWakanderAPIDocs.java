package academy.wakanda.wakanda_ai.docs.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public @interface JornadaWakanderAPIDocs {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "Registra uma aula assistida", description = "Este método registra uma aula como assistida por um Wakander no sistema")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "201", description = "Aula registrada com sucesso", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro nos campos de entrada.\" }"))),
	        @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Não há Wakander cadastrado com esse ID MemberKit!\" }"))),
	        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
	})
	public @interface RegistraAula {
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "Processa evento de login", description = "Este método permite Processar eventos de login")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Dados enviado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro no envio da mensagem.\" }"))),
			@ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Não há Wakander cadastrado com esse ID MemberKit!\" }"))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }"))) })
	public @interface EventoLoginMemberkit {
	}
}