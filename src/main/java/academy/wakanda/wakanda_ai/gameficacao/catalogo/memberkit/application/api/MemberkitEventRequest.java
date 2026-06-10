package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class MemberkitEventRequest {

    @JsonProperty("type")
    private String type;
    @JsonProperty("data")
    private Object data;


}
