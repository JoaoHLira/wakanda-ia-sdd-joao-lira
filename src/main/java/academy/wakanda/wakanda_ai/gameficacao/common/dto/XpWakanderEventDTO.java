package academy.wakanda.wakanda_ai.gameficacao.common.dto;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString
public class XpWakanderEventDTO {
	private UUID idProgressoWakander;
	private int xpObtido;
	private Sabedorias sabedorias;

	public XpWakanderEventDTO(UUID idProgressoWakander, int xpObtido, Sabedorias sabedorias) {
		this.idProgressoWakander = idProgressoWakander;
		this.xpObtido = xpObtido;
		this.sabedorias = sabedorias;
	}

	public XpWakanderEventDTO(UUID idProgressoWakander, int xpObtido) {
		this.idProgressoWakander = idProgressoWakander;
		this.xpObtido = xpObtido;
	}

	public static XpWakanderEventDTO onMissaaoProgresso(MissaoProgresso missaoProgresso, Sabedorias sabedorias) {
		return new XpWakanderEventDTO(missaoProgresso.getIdProgressoWakander(),
				missaoProgresso.getXpObtido() ,sabedorias);
	}

	public static XpWakanderEventDTO onJornadaProgresso(JornadaProgresso jornadaProgresso) {
		return new XpWakanderEventDTO(jornadaProgresso.getIdProgressoWakander(),
				jornadaProgresso.getXpObtido());
	}
}
