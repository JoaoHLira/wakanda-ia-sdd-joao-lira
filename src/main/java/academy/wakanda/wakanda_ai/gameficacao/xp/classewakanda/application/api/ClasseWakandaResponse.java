package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakandaStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Schema(description = "Response da classe criada")
public class ClasseWakandaResponse {

	@Schema(description = "ID único da classe criada", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID idClasse;

	@Schema(description = "Nome da classe", example = "Wakander Iniciante")
	private String nome;

	@Schema(description = "Descrição da classe", example = "Classe inicial para novos Wakanders que estão começando sua jornada")
	private String descricao;

	@Schema(description = "Nível necessário para atingir a classe", example = "1")
	private Integer nivelNecessario;

	@Schema(description = "Ordem/hierarquia da classe", example = "1")
	private Integer ordemClasse;

	@Schema(description = "Sabedorias envolvidas na classe")
	private Sabedorias sabedorias;

	@Schema(description = "IDs das missões necessárias para atingir a classe")
	private List<UUID> missoesNecessarias;

	@Schema(description = "Status da classe")
	private ClasseWakandaStatus status;

	public ClasseWakandaResponse(ClasseWakanda classeWakanda) {
		this.idClasse = classeWakanda.getIdClasse();
		this.nome = classeWakanda.getNome();
		this.descricao = classeWakanda.getDescricao();
		this.nivelNecessario = classeWakanda.getNivelNecessario();
		this.ordemClasse = classeWakanda.getOrdemClasse();
		this.sabedorias = classeWakanda.getSabedorias();
		this.missoesNecessarias = classeWakanda.getMissoesNecessarias();
		this.status = classeWakanda.getStatus();
	}
}
