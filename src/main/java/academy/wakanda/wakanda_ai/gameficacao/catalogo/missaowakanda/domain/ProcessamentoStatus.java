package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain;

public enum ProcessamentoStatus {
	EM_PROCESSO("Processo em andamento"), COMPLETO("Processo concluído");

	private final String descricao;

	ProcessamentoStatus(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
