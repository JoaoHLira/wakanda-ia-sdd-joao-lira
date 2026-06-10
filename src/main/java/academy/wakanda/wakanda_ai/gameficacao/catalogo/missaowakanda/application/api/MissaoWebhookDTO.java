package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api;

import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import lombok.Value;

@Value
public class MissaoWebhookDTO {
	private UUID idMissao;
	private String conteudoUrl;

	public MissaoWebhookDTO(MissaoWakanda novaMissao) {
		this(novaMissao.getIdMissao(), novaMissao.getConteudoUrl());
	}

	public MissaoWebhookDTO(UUID idMissao, String conteudoUrl) {
		this.idMissao = idMissao;
		this.conteudoUrl = conteudoUrl;
	}

}
