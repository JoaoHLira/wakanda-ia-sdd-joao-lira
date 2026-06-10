package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder

public class ProgressoIndividualResponse {

	private String nome;
	private int nivelAtual;
	private int xpTotal;
	private Sabedorias sabedorias;
	private String titulo;
	private int xpProximoNivel;
    private int xpAtual;
    private LocalDateTime ultimaAtualizacao;

	private static final ObjectMapper mapper = new ObjectMapper();

	public String getSabedoriasJson() {
		try {
			if (sabedorias == null) return "{}";
			return mapper.writeValueAsString(sabedorias);
		} catch (JsonProcessingException e) {
			return "{}";
		}
	}

	// Apenas os valores em lista
	public String getValoresSabedoriasJson() {
		try {
			if (sabedorias == null) {
				return "[]";
			}
			return mapper.writeValueAsString(List.of(
					sabedorias.getTeorico(),
					sabedorias.getProcesso(),
					sabedorias.getKnowHow(),
					sabedorias.getComportamental(),
					sabedorias.getCriativo()
			));
		} catch (JsonProcessingException e) {
			return "[]";
		}
	}

	public String getLabelsSabedoriasJson() {
		try {
			return mapper.writeValueAsString(List.of(
				"Teórico", "Processo", "Know-How", "Comportamental", "Criativo"
			));
		} catch (JsonProcessingException e) {
			return "[]";
		}
	}
    public ProgressoIndividualResponse(ProgressoIndividualDetalhadoProjection progressoIndividualDetalhadoProjection) {
        this.nome = progressoIndividualDetalhadoProjection.getNome();
        this.nivelAtual = progressoIndividualDetalhadoProjection.getNivelAtual();
        this.xpTotal = progressoIndividualDetalhadoProjection.getXpTotal();
        this.xpAtual = progressoIndividualDetalhadoProjection.getXpAtual();
        this.titulo = progressoIndividualDetalhadoProjection.getTitulo();
        this.ultimaAtualizacao = progressoIndividualDetalhadoProjection.getUltimaAtualizacao();
        this.xpProximoNivel = progressoIndividualDetalhadoProjection.getXpProximoNivel();
        this.sabedorias = new Sabedorias(
                progressoIndividualDetalhadoProjection.getSabTeorico(),
                progressoIndividualDetalhadoProjection.getSabProcesso(),
                progressoIndividualDetalhadoProjection.getSabKnowHow(),
                progressoIndividualDetalhadoProjection.getSabComportamental(),
                progressoIndividualDetalhadoProjection.getSabCriativo()
        );
    }
}
