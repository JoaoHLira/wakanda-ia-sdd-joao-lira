package academy.wakanda.wakanda_ai.jornadawakander.application.api;

import academy.wakanda.wakanda_ai.wakander.application.event.CadastroCompletoEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CadastraMembroRequest {
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("blocked")
    private boolean blocked;
    @JsonProperty("unlimited")
    private boolean unlimited;
    @JsonProperty("classroom_ids")
    private List<Integer> classroomIds;
    @JsonProperty("membership_level_id")
    private Integer membershipLevelId;
    @JsonProperty("expires_at")
    private String expiresAt;
    @JsonProperty("cpf_cnpj")
    private String cpfCnpj;
    @JsonProperty("phone_local_code")
    private String phoneLocalCode;
    @JsonProperty("phone_number")
    private String phoneNumber;

    public enum Status {
        @JsonProperty("active")
        ACTIVE,
        @JsonProperty("inactive")
        INACTIVE,
        @JsonProperty("pending")
        PENDING,
        @JsonProperty("expired")
        EXPIRED
    }

    public CadastraMembroRequest(CadastroCompletoEvent wakander, Integer membershipLevelId, List<Integer> classroomIds) {
        this.fullName = wakander.getNome();
        this.email = wakander.getEmail();
        this.status = Status.ACTIVE;
        this.blocked = false;
        this.unlimited = false;
        this.classroomIds = classroomIds;
        this.membershipLevelId = membershipLevelId;
        this.expiresAt = null;
        this.cpfCnpj = wakander.getCpf();
        if (validaSeNumeroEhDoBrasil(wakander.getWhatsapp())) {
            this.phoneLocalCode = wakander.getWhatsapp().substring(0, 2);
            this.phoneNumber = wakander.getWhatsapp().substring(2);
        }
    }

    private Boolean validaSeNumeroEhDoBrasil(String whatsapp) {
        return whatsapp.startsWith("55") && whatsapp.length() == 13;
    }
}