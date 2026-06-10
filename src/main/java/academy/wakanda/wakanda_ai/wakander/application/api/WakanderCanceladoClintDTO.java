package academy.wakanda.wakanda_ai.wakander.application.api;

import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class WakanderCanceladoClintDTO {

    private String email;
    private StatusClint status;
    private String telefone;

    public WakanderCanceladoClintDTO(Wakander wakander) {
        this.email = wakander.getContato().getEmail();
        this.status = StatusClint.PERDIDO;
        this.telefone = wakander.getContato().getWhatsapp();
    }
}
