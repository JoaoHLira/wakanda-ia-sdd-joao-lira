package academy.wakanda.wakanda_ai.constants;

public enum StatusChecklist {
	CONFIRMAR_DADOS_CADASTRO(0), 
	ENTRAR_DISCORD(1), 
	ENTRAR_GRUPO_WHATSAPP(2), 
	ACESSAR_MEMBERKIT(3),
	CONCLUIR_COMECE_AQUI(4), 
	CONCLUIR_PRIMEIRA_AULA(5);

	private final Integer posicao;

	private StatusChecklist(Integer posicao) {
		this.posicao = posicao;
	}

	public Integer getposicao() {
		return posicao;
	}

}