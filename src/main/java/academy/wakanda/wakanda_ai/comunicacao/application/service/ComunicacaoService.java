package academy.wakanda.wakanda_ai.comunicacao.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.api.*;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;

public interface ComunicacaoService {
    void enviaMensagemWhatsapp(MensagemRequest request);
    void adicionaWakanderAoGrupo(ZAPIPayloadAdiconaAoGrupo zapiPayloadAdiconaAoGrupo);
    void publicaNotificacao(NotificacaoRequest request);
    DiscordConviteResponse convidaParaCanalDiscord(DiscordConviteRequest conviteRequest);
    void removeWakanderDoGrupo(ZAPIPayloadRemoveDoGrupo zApiPayloadRemoveDoGrupo);
    void enviaContatoParaClint(ClintContatoRequest contatoClint);
    void cancelaWakanderClint(Wakander wakander);
    void enviaMsgCancelamentoClint(Wakander wakander, boolean success);
}