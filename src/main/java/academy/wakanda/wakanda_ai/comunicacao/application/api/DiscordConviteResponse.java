package academy.wakanda.wakanda_ai.comunicacao.application.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class DiscordConviteResponse {
    @JsonProperty("code")
    private String code;

    @JsonCreator
    public DiscordConviteResponse(@JsonProperty("code") String code) {
        this.code = code;
    }
}
