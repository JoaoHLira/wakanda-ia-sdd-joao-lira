package academy.wakanda.wakanda_ai.comunicacao.application.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ZAPIPayloadAdiconaAoGrupo {
    private  boolean autoInvite;
    private  String groupId;
    private String[] phones;
}
