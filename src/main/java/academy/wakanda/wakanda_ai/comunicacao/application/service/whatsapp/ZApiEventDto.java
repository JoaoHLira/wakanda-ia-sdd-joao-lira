package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZApiEventDto {
    private ZApiEventype type;
    private String whatsapp;
    private String mensagem;
    private LocalDate dataEnvio;

    public ZApiEventDto(ZApiEventype type, String whatsapp, String mensagem){
        this.type = type;
        this.whatsapp = whatsapp;
        this.mensagem = mensagem;
        this.dataEnvio = LocalDate.now();
    }

	public boolean validaSeTypeRemoveToGroup() {
		return ZApiEventype.REMOVE_TO_GROUP.equals(type);
	}
}