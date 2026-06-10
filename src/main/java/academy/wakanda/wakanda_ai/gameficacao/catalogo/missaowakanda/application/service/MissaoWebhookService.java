package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoWebhookDTO;

public interface MissaoWebhookService {

	void enviaWebhookProcessaMissao (MissaoWebhookDTO missaoWebhookDTO);

}
