package academy.wakanda.wakanda_ai.comunicacao.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayload;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIResponseGrupo;

public interface ZApiClient {
	void enviaMensagemWhatsApp(ZAPIPayload zapiPayload);
	<T> ZAPIResponseGrupo processaRequisicaoGrupoWhatsapp(T zApiPayloadRemoveDoGrupo, String typeRequisicao);
}