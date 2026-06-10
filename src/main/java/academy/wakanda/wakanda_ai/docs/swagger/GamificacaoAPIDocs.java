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

public @interface GamificacaoAPIDocs {

    public @interface Catalogo {

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @Operation(summary = "Desativar uma Missão", description = "Este método permite desativar uma missão específica vinculada a uma jornada.")
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Missão desativada com sucesso"),
                @ApiResponse(responseCode = "404", description = "Missão ou Jornada não encontrada", content = @Content(mediaType = "application/json", examples = {
                        @ExampleObject(value = "{ \"message\": \"Missão não encontrada\" }"),
                        @ExampleObject(value = "{ \"message\": \"Jornada não encontrada\" }")})),
                @ApiResponse(responseCode = "409", description = "Missão já está desativada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Esta missão já está desativada.\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
        public @interface DesativaMissao {
        }
    }

    public @interface Progresso {

    }

    public @interface XpCore {

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Altera XP Base da Missão", description = "Esse metodo permitirá alterar o XP Base de uma missão no sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "XPBase alterado com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"xpBase\": \"O XPBase deve ser maior que zero.\" }"))),
            @ApiResponse(responseCode = "404", description = "Missão não encontrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Missão não encontrada!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface AlteraXpBaseMissao {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Alterar Ordem da Missão", description = "Este método permite alterar a posição/ordem de execução de uma missão dentro de uma jornada. Somente missões ativas podem ter sua ordem alterada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ordem da missão alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação na posição informada", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(value = "{ \"message\": \"A posição deve ser um número maior ou igual a zero.\" }"),
                    @ExampleObject(value = "{ \"message\": \"A posição não pode ser maior ou igual ao total de missões na jornada.\" }")
            })),
            @ApiResponse(responseCode = "404", description = "Missão não encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Missão não encontrada\" }"))),
            @ApiResponse(responseCode = "409", description = "Missão não pode ter ordem alterada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Somente missões ativas podem ter sua posição alterada.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface AlteraOrdemMissao {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Atualizar missão com dados da IA",
            description = "Este método permite atualizar XP, sabedorias e descrição de uma missão com base no processamento da IA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Missão atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos dados de processamento",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{ \"message\": \"Esta missão está desativada.\" }"),
                            @ExampleObject(value = "{ \"message\": \"Missão já foi processada por IA.\" }"),
                            @ExampleObject(value = "{ \"message\": \"XPBase deve ser informado e maior que zero!\" }"),
                            @ExampleObject(value = "{ \"message\": \"Informe ao menos um tipo de sabedoria!\" }"),
                            @ExampleObject(value = "{ \"message\": \"A descricao nao pode ser nula.\" }")
                    })
            ),
            @ApiResponse(responseCode = "404", description = "Missão não encontrada",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{ \"message\": \"Missão não encontrada\" }")
                    })
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")
                    })
            )
    })
    public @interface AtualizaMissaoComDadosIA {
    }
}

