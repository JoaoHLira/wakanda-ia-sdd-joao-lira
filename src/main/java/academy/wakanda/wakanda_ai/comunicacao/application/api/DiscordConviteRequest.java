package academy.wakanda.wakanda_ai.comunicacao.application.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiscordConviteRequest {
    private int maxUses;
    private boolean unique;
    private int maxAge;
}
