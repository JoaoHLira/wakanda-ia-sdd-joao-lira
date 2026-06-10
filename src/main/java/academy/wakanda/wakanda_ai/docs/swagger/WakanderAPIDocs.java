package academy.wakanda.wakanda_ai.docs.swagger;

import academy.wakanda.wakanda_ai.handler.ErrorApiResponse;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderComDadosPessoaisOcultoResponse;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCriadoResponse;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderDetalhadoResponse;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderPaginadoResponse;
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

public @interface WakanderAPIDocs {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Busca um Wakander por ID", description = "Este método busca um Wakander pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wakander encontrado com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WakanderDetalhadoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"mensagem qualquer.\" }"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface BuscaPorIdWakander {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Busca um Wakander por ID e retorna dados ocultados", description = "Este método busca um Wakander pelo seu ID e retorna dados sensiveis ocultado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wakander encontrado com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WakanderComDadosPessoaisOcultoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"mensagem qualquer.\" }"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface BuscaPorIdWakanderRetornaDadosOcultos {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "RetornaTodosOsWakanders", description = "Este método busca um todos Wakander")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca de Wakanders feita com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"mensagem qualquer.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface RetornaTodosOsWakandersDeFormaPaginada {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "RetornaTodosOsWakandersPorStatus", description = "Este método busca um todos Wakander Pelo Status Cadastro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca de Wakanders feita com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"mensagem qualquer.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface RetornaTodosOsWakandersDeFormaPaginadaPorStatus {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Cria um novo Wakander", description = "Este método permite criar um novo Wakander no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Wakander criado com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WakanderCriadoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorApiResponse.class), examples = @ExampleObject(value = "{\n"
                    + "  \"nome\": \"O nome não pode estar vazio.\",\n"
                    + "  \"cpf\": \"O CPF informado é inválido.\",\n"
                    + "  \"message\": \"O CPF deve ter 11 dígitos numéricos, sem espaços, pontos ou hífen, apenas os números.\",\n"
                    + "  \"idMemberKit\": \"O ID do Kit de Membro não pode estar vazio.\",\n"
                    + "  \"whatsapp\": \"O número de WhatsApp não pode estar vazio.\",\n"
                    + "  \"email\": \"O email não pode estar vazio.\",\n"
                    + "  \"idAsaasFiador\": \"O ID do fiador no Asaas não pode estar vazio.\",\n"
                    + "  \"idAssinaturaFiador\": \"O ID da assinatura do fiador no Asaas não pode estar vazio.\",\n"
                    + "  \"nomeFiador\": \"O nome do fiador não pode estar vazio.\",\n"
                    + "  \"cpfFiador\": \"O CPF do fiador informado é inválido.\",\n"
                    + "  \"telefoneFiador\": \"O número de telefone do fiador não pode estar vazio.\"\n" + "}"))),
            @ApiResponse(responseCode = "409", description = "Existem dados duplicados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorApiResponse.class), examples = @ExampleObject(value = "{ \"message\": \"Existem dados duplicados\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface CriaNovoWakander {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Regulariza o status financeiro de um Wakander", description = "Este método permite regularizar o status financeiro de um Wakander pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status financeiro do Wakander regularizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"mensagem qualquer.\" }"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "409", description = "O status financeiro já está regular", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"O status financeiro do Wakander já está regular.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface RegularizaWakander {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Atualiza jornada", description = "Este método permite gerenciar a jornada de um Wakander")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Wakander não está regular", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Wakander não está regularizado!\" }"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "409", description = "Conflito de dados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Wakander já está nessa jornada da wakanda!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))})
    public @interface AtualizaJornadaWakander {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Edita os dados de um Wakander", description = "Este método edita os dados de um Wakander por ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Wakander editado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\n"
                    + "  \"nome\": \"O nome não pode estar vazio.\",\n" + "  \"cpf\": \"O CPF informado é inválido.\"\n"
                    + "}"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface EditaWakander {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Completa cadastro de um Wakander", description = "Este método completa o cadastro de um Wakander.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Wakander editado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"email\": \"O email deve ser válido.\", \"cpf\": \"O CPF informado é inválido.\" }"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface CompletaCadastro {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Recebe dados pessoais faltantes do wakander", description = "Este metodo recebe e persiste dados que o wakander estava em falta.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Dados carregados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"email\": \"O email deve ser válido.\", \"cpf\": \"O CPF informado é inválido.\" }"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface AdicionaDadosFaltantesDoWakander {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Solicita o envio do formulário de dados complementares", description = "Este método envia o link do formulario solicitando que o wakander preencha-o com dados complementares necessários.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Solicitação enviada com sucesso!"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface SolicitaEnvioDeFormularioDadosComplementares {
    }


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Faz requisiçao para API do Asaas e busca dados faltantes de Wakanders", description = "Esse metodo faz uma requisição para API do Asaas e preenche os dados dos Asaas que estão faltando para cada wakander")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Dados carregados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"email\": \"O email deve ser válido.\", \"cpf\": \"O CPF informado é inválido.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface BuscaDadosWakanderNoAsaas {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Busca no banco estatisticas sobre wakanders", description = "Esse metodo retornará estatisticas como, total de Wakanders no banco, total com status completo e incompleto")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Dados carregados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"email\": \"O email deve ser válido.\", \"cpf\": \"O CPF informado é inválido.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface RetornaEstatisticasSobreWakanders {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Regulariza status cadastro do Wakander", description = "Esse metodo verifica os dados pessoais e de asaas e altera o status cadastro do Wakander para COMPLETO ou INCOMPLETO dependendo dos seus dados")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Dados Atualizados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"email\": \"O email deve ser válido.\", \"cpf\": \"O CPF informado é inválido.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface RegularizaStatusCadastroWakanders {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Cancela assinatura do Wakander", description = "Este método permite cancelar a assinatura de um Wakander")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assinatura cancelada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"motivoCancelamento\": \"O motivo do cancelamento é obrigatório.\",\n" +
                                    "  \"dataCancelamento\": [\n" +
                                    "    \"A data do cancelamento é obrigatória.\",\n" +
                                    "    \"A data deve estar no formato dd/mm/aaaa\"\n" +
                                    "  ]\n" +
                                    "}"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "409", description = "Wakander já foi cancelado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Wakander já foi cancelado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface CancelaAssinatura {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Reverte cancelamento da assinatura", description = "Este método permite reverter o cancelamento da assinatura de um Wakander")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cancelamento revertido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"motivoCancelamento\": \"O motivo do cancelamento é obrigatório.\",\n" +
                                    "  \"dataCancelamento\": [\n" +
                                    "    \"A data do cancelamento é obrigatória.\",\n" +
                                    "    \"A data deve estar no formato dd/mm/aaaa\"\n" +
                                    "  ]\n" +
                                    "}"))),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "409", description = "Wakander não está com cancelamento solicitado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Wakander não está com cancelamento solicitado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface ReverteCancelamento {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Cria uma nova missão", description = "Esse metodo permitirá criar uma nova missão no sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Missão criada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"titulo\": \"Preencha o título.\", \"descricao\": \"Informe a descrição.\", \"xpBase\": \"XP base é obrigatório.\", \"tipoMissao\": \"Tipo da missão é obrigatório.\", \"jornada\": \"Selecione a jornada.\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface CriaNovaMissao {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Busca missão", description = "Esse metodo permitirá buscar uma missão no sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Missão encontrada com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Missão não encontrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Missão não encontrada!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface BuscaMissaoPorId {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Gera link atualização fiador", description = "Esse metodo permitirá gerar um link de atualização de fiador")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Link gerado com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Wakander não encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Wakander não encontrado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))})
    public @interface GeraLinkAtualizacaoFiador {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Inicia onboarding manual", description = "Esse metodo envia manualmente o formulario de cadastro e o checklist inicial do onboarding para um Wakander com cadastro incompleto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Onboarding manual iniciado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Wakander nao elegivel para iniciar o onboarding manual",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{ \"message\": \"Onboarding so pode ser iniciado para Wakanders com cadastro INCOMPLETO.\" }"))),
            @ApiResponse(responseCode = "404", description = "Wakander nao encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{ \"message\": \"Wakander nao encontrado!\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"Por favor, informe ao administrador do sistema!\" }")))
    })
    public @interface IniciaOnboardingManual {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Busca Wakanders", description = "Este método realiza a busca de Wakanders com filtros dinâmicos, paginação e ordenação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca de Wakanders realizada com sucesso", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = WakanderPaginadoResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Campo de ordenação inválido", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = "{ \"message\": \"Campo de ordenação inválido: banana\" }")
            )
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = "{ \"description\": \"INTERNAL SERVER ERROR!\", \"message\": \"POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!\" }")))
    })
    public @interface BuscaWakanders {
    }
}
