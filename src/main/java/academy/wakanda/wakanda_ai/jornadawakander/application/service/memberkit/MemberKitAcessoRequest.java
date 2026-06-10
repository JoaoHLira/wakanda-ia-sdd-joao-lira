package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit;

import lombok.Getter;

@Getter
public class MemberKitAcessoRequest {

    private String email;
    private Boolean blocked;

    public MemberKitAcessoRequest(String email, Boolean blocked) {
        this.email = email;
        this.blocked = blocked;
    }
}
