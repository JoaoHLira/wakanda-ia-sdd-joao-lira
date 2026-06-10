package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api;

import java.util.List;
import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.validation.SabedoriaAnnotation;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "Informações para criação de uma nova classe Wakanda")
public class ClasseWakandaRequest {

	@Schema(description = "Nome da classe", example = "Wakander Iniciante")
	@NotBlank(message = "Informe o nome da classe")
	private String nome;

	@Schema(description = "Descrição da classe", example = "Classe inicial para novos Wakanders que estão começando sua jornada")
	@NotBlank(message = "Informe a descrição da classe")
	private String descricao;

	@Schema(description = "Nível necessário para atingir a classe", example = "1")
	@NotNull(message = "Informe o nível necessário")
	@Min(value = 1, message = "Nível necessário deve ser maior que zero")
	private Integer nivelNecessario;

	@SabedoriaAnnotation
	@Schema(description = "Sabedorias envolvidas na classe", example = "{ \"teorico\": 10, \"processo\": 5, \"knowHow\": 15, \"comportamental\": 5, \"criativo\": 10 }")
	@NotNull(message = "Informe as sabedorias da classe")
	private Sabedorias sabedorias;

	@Schema(description = "Lista de IDs das missões necessárias para atingir a classe")
	@NotEmpty(message = "Informe ao menos uma missão necessária")
	private List<UUID> missoesNecessarias;

}
