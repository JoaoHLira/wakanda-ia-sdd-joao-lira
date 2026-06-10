package academy.wakanda.wakanda_ai.wakander.application.event;

import java.util.UUID;

import academy.wakanda.wakanda_ai.wakander.domain.DiscordEventype;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor	
public class DiscordEventRequest {
	private DiscordEventype type;
	private UUID idWakander;
	private String nome;
	private String telefone;
	private String motivoCancelamento;
	private String email;
	
	public DiscordEventRequest(DiscordEventype type, AssinaturaCanceladaEvent evento) {
		this.type = type;
		this.idWakander = evento.getIdWakander();
		this.nome = evento.getNome();
		this.telefone = evento.getTelefone();
		this.motivoCancelamento = evento.getMotivoCancelamento();
		this.email = evento.getEmail();
	}
	
	public DiscordEventRequest(DiscordEventype type, Wakander wakander) {
		this.type = type;
		this.idWakander = wakander.getIdWakander();
		this.nome = wakander.getNome();
		this.telefone = wakander.getContato().getWhatsapp();
		this.motivoCancelamento = wakander.getFinanceiro().getMotivoCancelamento();
		this.email = wakander.getContato().getEmail();
	}
}
