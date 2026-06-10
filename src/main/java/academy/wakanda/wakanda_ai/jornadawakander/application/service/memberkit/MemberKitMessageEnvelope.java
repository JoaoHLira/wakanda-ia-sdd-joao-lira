package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class MemberKitMessageEnvelope {

    private MemberKitTipoRequisicao tipo;
    private JsonNode payload;

    public MemberKitMessageEnvelope(MemberKitTipoRequisicao memberKitTipoRequisicao, JsonNode payload) {
        this.tipo = memberKitTipoRequisicao;
        this.payload = payload;
    }
}
