package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.validation.SabedoriaAnnotation;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@AllArgsConstructor
@Schema(description = "Informações para criação de uma nova missão Wakanda")
public class MissaoWakandaRequest {

        @Schema(description = "Título da missão", example = "Estudar Java")
        @NotBlank(message = "Informe o título da missão")
        private String titulo;

        @Schema(description = "Descrição da missão", example = "Java para iniciantes: Aprender os conceitos básicos de Java.")
        @NotBlank(message = "Informe a descrição da missão")
        private String descricao;

        @Schema(description = "Experiência base da missão", example = "100")
        @NotNull(message = "Informe o XPBase da missão")
        private Integer xpBase;

        @Schema(description = "Tipo da missão (ID)", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "Informe o tipo da missão")
        private UUID idTipoMissao;

        @Schema(description = "ID da jornada associada à missão", example = "123e4567-e89b-12d3-a456-426614174001")
        @NotNull(message = "Informe a jornada da missão")
        private UUID idJornada;

        @SabedoriaAnnotation
        @Schema(description = "Sabedorias da Missão", example = "{ \"teorico\": 15, \"processo\": 0, \"knowHow\": 10, \"comportamental\": 0, \"criativo\": 5 }")
        private Sabedorias sabedorias;

        @Schema(description = "O ID da missão na API Externa", example = "12345")
        @NotNull(message = "O ID da missão que vem da API externa deve ser informado")
        private String idMissaoExterna;

        @Schema(description = "Deixe nulo para missões pai. Caso seja uma missão filha, informe o ID da missão pai correspondente.", example = "123e4567-e89b-12d3-a456-426614174001")
        private UUID idMissaoPai;

        @Schema(description = "URL do conteúdo da missão", example = "https://www.wakanda.ai/missao/123e4567-e89b-12d3-a456-426614174001")
        @NotNull(message = "Informe a URL do conteúdo da missão")
        private String conteudoUrl;

        @Schema(description = "ID da classe mínima da missão", example = "123e4567-e89b-12d3-a456-426614174002")
        private UUID idClasseMinima;
}
