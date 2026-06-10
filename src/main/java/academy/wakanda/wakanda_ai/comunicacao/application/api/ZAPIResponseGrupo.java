package academy.wakanda.wakanda_ai.comunicacao.application.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class ZAPIResponseGrupo {
	private Boolean value;

	public Boolean validaRequisicao() {
		return Boolean.TRUE.equals(value);
	}
	
	@JsonCreator
	public ZAPIResponseGrupo(@JsonProperty("value") Boolean value) {
		this.value = value;
	}
}