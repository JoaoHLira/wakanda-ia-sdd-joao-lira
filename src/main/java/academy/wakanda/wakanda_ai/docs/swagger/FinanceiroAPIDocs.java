package academy.wakanda.wakanda_ai.docs.swagger;

import academy.wakanda.wakanda_ai.handler.ErrorApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface FinanceiroAPIDocs {

    public @interface Cobranca {

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @Operation(summary = "Processa evento de cobrança", description = "Este método Processa eventos de cobranças")
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Evento processado com sucesso"),
                @ApiResponse(responseCode = "400", description = "Evento não processado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"O evento não corresponde a nenhuma estratégia!\" }"))),
                @ApiResponse(responseCode = "409", description = "Conflito de dados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorApiResponse.class), examples = @ExampleObject(value = "[\n"
                        + "  { \"message\": \"Conflito! O status da cobrança já está como: STATUS_COBRANCA\" },\n"
                        + "  { \"message\": \"Erro ao salvar cobrança! Existem dados duplicados.\" }\n" + "]"))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
        public @interface ProcessaEvento {
        }
    }

    public @interface Financeiro {

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @Operation(summary = "Processa evento de assinatura", description = "Este método Processa evento de assinatura criada e cancelada")
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Evento processado com sucesso"),
                @ApiResponse(responseCode = "400", description = "Evento não processado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"O evento não corresponde a nenhuma estratégia!\" }"))),
                @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado para assinatura!\" }"))),
                @ApiResponse(responseCode = "409", description = "Conflito de dados", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"O status financeiro do Wakander já está STATUS_FINANCEIRO.\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
        public @interface ProcessaEvento {
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @Operation(summary = "Atualiza dados do Fiador no Asaas e banco de dados.", description = "Atualiza os dados do Fiador na API do Asaas e no banco de dados.")
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Dados Atualizado com sucesso"),
                @ApiResponse(responseCode = "400", description = "Dados não atualizado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Falha na atualização. Dados inválidos ou erro no Asaas.\" }"))),
                @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Falha de autenticação. Você não tem permissão para acessar este recurso.\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})

        public @interface AtualizaDadosFiador {
        }
    }
}