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

public @interface MissaoProgressoAPIDocs {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Criar progresso de missão", description = "Este método cria o progresso de uma missão para o Wakander quando a missão está liberada para a classe atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Progresso de missão criado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Missão não está liberada para a classe atual ou dados inválidos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(value = "{ \"message\": \"Missão não está liberada para a classe atual\" }"),
                    @ExampleObject(value = "{ \"idWakander\": \"must not be null\" }")
            })),
            @ApiResponse(responseCode = "404", description = "Missão ou progresso do Wakander não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Missão não encontrada!\" }"))),
            @ApiResponse(responseCode = "409", description = "Progresso da missão já existe", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Missão Progresso já existe!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    @interface CriaMissaoProgresso {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Concluir missão manualmente", description = "Este método conclui manualmente um progresso de missão, aplicando validações de liberação por classe e status da missão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Missão concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Missão já concluída ou não liberada para a classe atual", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(value = "{ \"message\": \"A missão já está concluída.\" }"),
                    @ExampleObject(value = "{ \"message\": \"Missão não está liberada para a classe atual\" }")
            })),
            @ApiResponse(responseCode = "404", description = "Progresso de missão não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Progresso de missão não encontrado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    @interface ConcluiMissaoManualmente {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Listar disponibilidade de missões", description = "Este método lista as missões de uma jornada com status de disponibilidade para o Wakander (LIBERADA ou BLOQUEADA).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidade de missões retornada com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Classe atual do Wakander não encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Classe atual do Wakander não encontrada!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    @interface ListaDisponibilidadeMissoes {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Listar missões concluídas", description = "Este método lista as missões concluídas por um Wakander com XP, data de conclusão e sabedorias ganhas salvas no momento da conclusão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Missões concluídas retornadas com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    @interface ListaMissoesConcluidas {
    }
}
