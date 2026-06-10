package academy.wakanda.wakanda_ai.wakander.domain;

public enum WakanderStatusFinanceiro {
	REGULAR("Regular"), CANCELAMENTO_SOLICITADO("Aguardando cancelamento"), CANCELADO("Cancelado");

	private final String descricao;

	WakanderStatusFinanceiro(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}