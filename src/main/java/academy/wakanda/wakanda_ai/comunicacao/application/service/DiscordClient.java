package academy.wakanda.wakanda_ai.comunicacao.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.api.DiscordConviteRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.DiscordConviteResponse;

public interface DiscordClient {
    DiscordConviteResponse criaConviteDoCanalParaWakander(DiscordConviteRequest conviteRequest);
}