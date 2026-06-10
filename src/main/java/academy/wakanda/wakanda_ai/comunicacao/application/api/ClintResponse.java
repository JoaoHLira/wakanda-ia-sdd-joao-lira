package academy.wakanda.wakanda_ai.comunicacao.application.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ClintResponse {
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("message")
    private String message;
}
