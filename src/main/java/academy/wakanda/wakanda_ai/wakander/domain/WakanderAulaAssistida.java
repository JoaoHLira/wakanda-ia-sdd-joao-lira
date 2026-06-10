package academy.wakanda.wakanda_ai.wakander.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter

@Embeddable
public class WakanderAulaAssistida {
    @Column(name = "ultima_aula_assistida_datetime")
    private LocalDateTime dateTime;
    @Column(name = "id_ultima_aula_assistida")
    private UUID idAulaAssistida;
    
	public WakanderAulaAssistida(UUID idAulaAssistida, LocalDateTime dataConclusao) {
		this.dateTime = dataConclusao;
		this.idAulaAssistida = idAulaAssistida;
	}

    public WakanderAulaAssistida() {
        this.dateTime = LocalDateTime.now();
        this.idAulaAssistida = UUID.randomUUID();
    }
}
