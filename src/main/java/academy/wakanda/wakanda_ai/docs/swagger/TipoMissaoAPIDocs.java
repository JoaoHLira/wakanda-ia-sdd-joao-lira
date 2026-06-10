package academy.wakanda.wakanda_ai.docs.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface TipoMissaoAPIDocs {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Inserir um novo Tipo de Missão", description = "Este método permite inserir um novo Tipo de Missão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de Missão inserido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro no envio do Tipo de Missão\" }"))),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Já existe outro Tipo de Missão com a mesma descrição\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface InsereTipoMissao {
    }
}
