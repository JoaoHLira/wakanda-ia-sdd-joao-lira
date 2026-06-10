package academy.wakanda.wakanda_ai.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import academy.wakanda.wakanda_ai.handler.APIException;

public class ValidacaoUtils {

	private ValidacaoUtils() {
	}

	public static String validarCampoObrigatorio(String valor, String nomeCampo) {
		if (StringUtils.isBlank(valor)) {
			throw APIException.build(HttpStatus.BAD_REQUEST, String.format("%s é obrigatório", nomeCampo));
		}
		return valor.trim();
	}
}
