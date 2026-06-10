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

public @interface JornadaWakandaAPIDocs {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Cria nova Jornada", description = "Este método cria uma nova Jornada de gameficação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Jornada criada com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro nos campos de entrada.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface CriaNovaJornada {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Busca Jornada por ID", description = "Retorna uma jornada detalhada e a quantidade de missões pertencentes a ela")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jornada encontrada com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Jornada não encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Jornada não encontrada!\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface BuscaJornadaPorId {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Lista Todas as jornadas", description = "Retorna todas as jornadas cadastradas no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "jornadas encontradas com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface ListaJornadas {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Busca Jornada Atual da Trilha", description = "Retorna o estado da jornada atual em uma trilha com base no progresso do Wakander.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description =  "jornadas encontradas com sucesso", content = @Content(mediaType = "application/json")),
                            @ApiResponse(responseCode = "404", description = "Trilha ou jornada não encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Trilha não encontrada!\" }"))),
                            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface BuscaJornadaPorIdTrilha {}
}