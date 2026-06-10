package academy.wakanda.wakanda_ai.wakander.application.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.Getter;

@Getter
public class WakanderInativoResponse {
	private String nome;
	private String dataUltimaAulaAssistida;
	
	private WakanderInativoResponse(Wakander wakander) {
		this.nome = wakander.getNome();
		this.dataUltimaAulaAssistida = getAulaAssistida(wakander.getUltimaAulaAssistida().getDateTime());
	}

	private String getAulaAssistida(LocalDateTime aulaAssistida) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
		return aulaAssistida.format(formatter).toString();
	}

	public static List<WakanderInativoResponse> converteParaResponse(List<Wakander> wakanders) {
	    return wakanders.stream()
	            .map(WakanderInativoResponse::new)
	            .sorted(Comparator.comparing(WakanderInativoResponse::getDataUltimaAulaAssistida))
	            .collect(Collectors.toList());
	}
}