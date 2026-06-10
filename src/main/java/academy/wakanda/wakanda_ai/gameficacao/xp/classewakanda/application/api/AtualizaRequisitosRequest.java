package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AtualizaRequisitosRequest {

	@Schema(description = "Nome da classe", example = "Wakander Iniciante")
	private String nome;

	@Schema(description = "Descrição da classe", example = "Classe inicial para novos Wakanders que estão começando sua jornada")
	private String descricao;

	@Schema(description = "Nível necessário para atingir a classe", example = "1")
	@Min(value = 1, message = "Nível necessário deve ser maior que zero")
	private Integer nivelNecessario;

	@Schema(description = "Sabedorias envolvidas na classe", example = "{ \"teorico\": 10, \"processo\": 5, \"knowHow\": 15, \"comportamental\": 5, \"criativo\": 10 }")
	private Sabedorias sabedorias;

	@Schema(description = "Lista de IDs das missões necessárias para atingir a classe")
	private List<UUID> missoesNecessarias;

}
