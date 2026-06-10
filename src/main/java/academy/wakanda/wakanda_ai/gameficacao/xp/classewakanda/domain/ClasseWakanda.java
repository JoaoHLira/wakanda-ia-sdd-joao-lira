package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.AtualizaRequisitosRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.ClasseWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.service.ValidadorMissoesClasse;
import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "classe_wakanda")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClasseWakanda {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "uuid", name = "id_classe", nullable = false)
	private UUID idClasse;

	@Column(nullable = false, unique = true)
	private String nome;

	@Column(nullable = false, unique = true)
	private String descricao;

	@Column(name = "nivel_necessario", nullable = false)
	private Integer nivelNecessario;

	@Column(name = "ordem_classe", nullable = false)
	private Integer ordemClasse;

	@Embedded
	private Sabedorias sabedorias;

	@ElementCollection
	@CollectionTable(name = "classe_wakanda_missoes", joinColumns = @JoinColumn(name = "id_classe"))
	@Column(name = "id_missao")
	private List<UUID> missoesNecessarias;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ClasseWakandaStatus status;

	public ClasseWakanda(ClasseWakandaRequest request, Integer ordemClasse) {
		this.nome = request.getNome();
		this.descricao = request.getDescricao();
		this.nivelNecessario = request.getNivelNecessario();
		this.ordemClasse = ordemClasse;
		this.sabedorias = Sabedorias.ofNullable(request.getSabedorias());
		this.missoesNecessarias = request.getMissoesNecessarias();
		this.status = ClasseWakandaStatus.ATIVA;
		validaCamposObrigatorios();
	}

	private void validaCamposObrigatorios() {
		validaNivelNecessario();
		validaSabedorias();
		validaMissoesNecessarias();
	}

	private void validaNivelNecessario() {
		if (this.nivelNecessario == null || this.nivelNecessario <= 0) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Nível Necessário deve ser maior que zero!");
		}
	}

	private void validaSabedorias() {
		if (this.sabedorias == null || this.sabedorias.isEmpty()) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Informe ao menos um tipo de sabedoria!");
		}
	}

	private void validaMissoesNecessarias() {
		if (this.missoesNecessarias == null || this.missoesNecessarias.isEmpty()) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Informe ao menos uma missão necessária!");
		}
	}

	public void atualizaParcialmente(AtualizaRequisitosRequest dto, ValidadorMissoesClasse validador) {
		this.nome = dto.getNome() != null ? dto.getNome() : this.nome;
		this.descricao = dto.getDescricao() != null ? dto.getDescricao() : this.descricao;
		this.nivelNecessario = dto.getNivelNecessario() != null ? dto.getNivelNecessario() : this.nivelNecessario;
		if (dto.getMissoesNecessarias() != null) {
			this.missoesNecessarias = dto.getMissoesNecessarias();
		}

		if (dto.getSabedorias() != null) {
			this.sabedorias = dto.getSabedorias();
		}
		validador.valida(this.missoesNecessarias);
		validaCamposObrigatorios();
	}

	public boolean verificaSeIgualOuSuperior(ClasseWakanda outraClasse) {
		return outraClasse != null && this.ordemClasse >= outraClasse.getOrdemClasse();
	}
}
