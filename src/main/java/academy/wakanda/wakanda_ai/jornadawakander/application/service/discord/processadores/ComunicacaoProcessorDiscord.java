package academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.processadores;

import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;
import academy.wakanda.wakanda_ai.wakander.domain.DiscordEventype;

public interface ComunicacaoProcessorDiscord {
    boolean validaSeProcessa(DiscordEventype discordEventype);
    void processaPorTipoEvento(DiscordEventRequest discordEventRequest);
}