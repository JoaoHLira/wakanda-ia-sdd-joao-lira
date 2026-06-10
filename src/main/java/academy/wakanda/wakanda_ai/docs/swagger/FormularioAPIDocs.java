package academy.wakanda.wakanda_ai.docs.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface FormularioAPIDocs {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Exibe o formulário de atualização de fiador", description = "Endpoint responsável por exibir o formulário de atualização de dados do fiador com base no token fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulário de atualização exibido com sucesso", content = @Content(mediaType = MediaType.TEXT_HTML_VALUE, examples = @ExampleObject(value = "Retorna a view 'atualiza-fiador' com o ID do Wakander"))),
            @ApiResponse(responseCode = "400", description = "Token inválido ou expirado", content = @Content(mediaType = MediaType.TEXT_HTML_VALUE, examples = @ExampleObject(value = "Retorna a view 'token-invalido'"))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Falha de autenticação. Você não tem permissão para acessar este recurso."))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface AtualizaFiador {
    }
}