package academy.wakanda.wakanda_ai.constants;

public enum MensagensDiscord {
	MENSAGEM_PRIVADA("""
		    👋 Olá %s!
		    
		    Para liberar os modulos no *Servidor Wakanda Academy*, clique no link abaixo e insira seu Email cadastrado no Wakanda.
		    Clique aqui: %s
		    
		    Se tiver qualquer dificuldade, estamos aqui para ajudar! 🌍
		    """
	),
	MENSAGEM_CANAL_VALIDACAO_FALHA("""
	        👋 Olá %s

	        ❌ Não conseguimos enviar a URL de validação no seu privado.

	        👉 clique no link abaixo e insira seu Email cadastrado no Wakanda!

	        url: %s
	        """
	),
    MENSAGEM_EMAIL_DISCORD(""" 
            🔗 Este é seu Email de validação do Discord!
            👉 Os proximos passos estarão no servidor...
            
            Email: %s"""),
    MENSAGEM_VALIDACAO_MEMBROS_ANTIGOS(""" 
    		👉 Falta apenas uma etapa, precisamos validar e atualizar seu cargo no Disocrd
    		
            🔗 Este é seu Email de validação do Discord!
            Email: %s
            
            Entre nesse canal e clique no botão
            Canal: %s
            
            👉 Os proximos passos estaram no servidor...
    		"""),
	MENSAGEM_CANAL_PUBLICO("""
		    👋 Fala, %s!
		    
		    Enviamos uma mensagem privada com os próximos passos para validar sua entrada e liberar os módulos.
		    
		    Se não encontrar, clique aqui: https://discord.com/channels/@me/%s
		    Qualquer dúvida, estamos aqui para te apoiar. 🚀
			"""
	),
	MENSAGEM_FALHA_LIDERANCA("""
			⚠️ Falha ao cancelar Wakander no Discord

			Nome: %s  
			Motivo: %s

			🧭 Ação sugerida: Verificar se o IdDiscord está nulo ou se o usuário ainda está no servidor.
			""");
	
	private final String mensagem;

	MensagensDiscord(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getMensagem() {
		return mensagem;
	}

	public String formataMensagem(String... parametros) {
		return String.format(mensagem, (Object[]) parametros);
	}
}