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

public @interface ClasseWakandaAPIDocs {

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @Operation(summary = "Cria uma nova classe Wakanda", description = "Este método permite criar uma nova classe Wakanda")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Classe criada com sucesso", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro nos campos de entrada.\" }"))),
                        @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Já existe uma classe com esse nome.\" }"))),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }"))) })
        public @interface CriaNovaClasse {
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @Operation(summary = "Busca classe por ID", description = "Este método permite buscar uma classe Wakanda por ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Classe encontrada com sucesso", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "Classe não encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Classe Wakanda não encontrada\" }"))),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }"))) })
        public @interface BuscaClassePorId {
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @Operation(summary = "Atuaqliza os requisitos de uma classe Wakanda", description = "Este método permite atualizar os requisitos de uma classe Wakanda")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Classe atualizada com sucesso", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "400", description = "Regras de negócio violadas ao tentar remover todas as missoes ou todas as sabedorias", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "RemoverTodasMissoes", value = "{ \"message\": \"Não é permitido remover todas as missões da classe\" }"),
                                        @ExampleObject(name = "RemoverTodasSabedorias", value = "{ \"message\": \"Não é permitido remover todas as sabedorias da classe, informe ao menos uma sabedoria\" }") })),
                        @ApiResponse(responseCode = "404", description = "Classe Wakanda não encontrada", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "409", description = "Já existe outra classe com o mesmo nome ou descrição!", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "NomeDuplicado", value = "{ \"message\": \"Já existe uma classe com esse nome!\" }"),
                                        @ExampleObject(name = "DescricaoDuplicada", value = "{ \"message\": \"Já existe uma classe com essa descrição!\" }") })),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }"))) })
        public @interface AtualizaRequisitosClasse {
        }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Buscar classes ativas", description = "Retorna todas as classes Wakanda com status ATIVA, incluindo nome e requisitos (nível necessário, sabedorias e missões necessárias).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de classes ativas retornada com sucesso (pode ser vazia).", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface BuscaClassesAtivas {
    }
}
