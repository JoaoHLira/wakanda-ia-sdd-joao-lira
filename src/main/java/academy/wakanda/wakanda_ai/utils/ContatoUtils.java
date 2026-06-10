package academy.wakanda.wakanda_ai.utils;

import academy.wakanda.wakanda_ai.handler.APIException;
import org.springframework.http.HttpStatus;

public  class ContatoUtils {

    public static String validarTelefone(String telefone) {
        if(telefone == null || !telefone.matches("\\d{13}") || !telefone.startsWith("55")){
            throw APIException.build(HttpStatus.BAD_REQUEST, "O numero de Whatsapp Cadastrado não corresponde à um numero brasileiro válido.");
        };
        return telefone;
    }

    public static String validarEmail(String email) {
        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (email == null || !email.matches(regexEmail)) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "O e-mail informado não é válido.");
        }

        return email.toLowerCase();
    }


}
