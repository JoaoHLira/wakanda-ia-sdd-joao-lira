package academy.wakanda.wakanda_ai.comunicacao.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ClintResponse;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCanceladoClintDTO;

public interface CancelaClintService {
    ClintResponse cancelaWakanderClint(WakanderCanceladoClintDTO wakanderCanceladoClint);
}
