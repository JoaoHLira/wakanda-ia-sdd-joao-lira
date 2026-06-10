package academy.wakanda.wakanda_ai.comunicacao.application.api;

public enum ZAPITypeGrupo {
	ADD_TO_GROUP("add-participant"), REMOVE_TO_GROUP("remove-participant");

	private String descricao;

	ZAPITypeGrupo(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}