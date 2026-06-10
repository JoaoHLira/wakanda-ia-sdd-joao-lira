package academy.wakanda.wakanda_ai.wakander.application.event;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AssinaturaCanceladaEvent {
    private UUID idWakander;
    private String nome;
    private String idMemberKit;
    private String telefone;
    private String email;
    private String motivoCancelamento;
    private LocalDateTime ultimaAtualizacao;

    public AssinaturaCanceladaEvent(Wakander wakander) {
        this.idWakander = wakander.getIdWakander();
        this.nome = wakander.getNome();
        this.idMemberKit = wakander.getIdMemberKit();
        this.telefone = wakander.getContato() != null ? wakander.getContato().getWhatsapp() : null;
        this.email = wakander.getContato() != null ? wakander.getContato().getEmail() : null;
        this.motivoCancelamento = wakander.getFinanceiro().getMotivoCancelamento();
        this.ultimaAtualizacao = wakander.getFinanceiro().getUltimaAtualizacao();
    }
}