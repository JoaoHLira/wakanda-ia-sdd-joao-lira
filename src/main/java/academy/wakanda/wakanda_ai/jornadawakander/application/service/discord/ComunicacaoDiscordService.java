package academy.wakanda.wakanda_ai.jornadawakander.application.service.discord;

import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;

public interface ComunicacaoDiscordService {
    void processaPorTipoMensagem(DiscordEventRequest discordEventRequest);
}