package academy.wakanda.wakanda_ai.wakander.domain;

import academy.wakanda.wakanda_ai.utils.ContatoUtils;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderContatoAlteracaoRequest;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderNovoRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor
public class WakanderContato {
    @Column(name = "whatsapp")
    private String whatsapp;
    @Column(name = "email")
    @Email(message = "O email deve ser válido.")
    private String email;

    public WakanderContato(WakanderNovoRequest wakanderNovo) {
        this.whatsapp = wakanderNovo.getWhatsapp();
        this.email = wakanderNovo.getEmail();
    }

    public WakanderContato(String whatsapp, String email) {
        this.whatsapp = whatsapp;
        this.email = email;
    }

    public void editaContato(WakanderContatoAlteracaoRequest contatoAlteracaoRequest) {
        this.whatsapp = contatoAlteracaoRequest.getWhatsapp();
        this.email = contatoAlteracaoRequest.getEmail();
    }

    public void ocultaEmail() {
        if (this.email == null || this.email.isBlank() || this.email.length() < 3) {
            return;
        }

        String primeirosTres = this.email.substring(0, 3);
        StringBuilder ocultado = new StringBuilder(primeirosTres);

        for (int i = 3; i < this.email.length(); i++) {
            ocultado.append('*');
        }

        this.email = ocultado.toString();
    }

    public void ocultarWhatsapp() {
        if (this.whatsapp == null || this.whatsapp.isBlank() || this.whatsapp.length() < 4){
            return;
        }

        int tamanho = this.whatsapp.length();
        String ultimosQuatro = this.whatsapp.substring(tamanho - 4);

        StringBuilder ocultado = new StringBuilder();
        for (int i = 0; i < tamanho - 4; i++) {
            ocultado.append('*');
        }

        ocultado.append(ultimosQuatro);

        this.whatsapp = ocultado.toString();
    }

    public void preencheContatoIncompleto(String emailFromRequest, String whatsappFromRequest) {
        this.whatsapp = estaPreenchido(this.whatsapp) ?  this.whatsapp : ContatoUtils.validarTelefone(whatsappFromRequest) ;
        this.email = estaPreenchido(this.email) ? this.email : ContatoUtils.validarEmail(emailFromRequest);
    }

    private boolean estaPreenchido(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

}
