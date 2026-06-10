package academy.wakanda.wakanda_ai.comunicacao.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ClintContatoRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ClintResponse;

public interface ClintCRM {
    ClintResponse enviaContatoParaClint(ClintContatoRequest contatoClint);
}
