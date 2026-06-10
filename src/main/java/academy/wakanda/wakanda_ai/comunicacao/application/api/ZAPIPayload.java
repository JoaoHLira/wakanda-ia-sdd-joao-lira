package academy.wakanda.wakanda_ai.comunicacao.application.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZAPIPayload {

    private String phone;
    private String message;

    public ZAPIPayload(MensagemRequest request) {
        this.phone = request.getWhatsapp();
        this.message = request.getMensagem();
    }

    public ZAPIPayload(String phone, String message) {
        this.phone = phone;
        this.message = message;
    }
}