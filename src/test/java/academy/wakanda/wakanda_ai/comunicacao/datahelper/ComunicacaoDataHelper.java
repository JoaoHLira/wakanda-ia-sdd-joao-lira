package academy.wakanda.wakanda_ai.comunicacao.datahelper;

import academy.wakanda.wakanda_ai.comunicacao.application.api.*;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;

import java.time.LocalDate;
import java.util.UUID;

public class ComunicacaoDataHelper {

    public static MensagemRequest mensagemParaSerEnviada() {
        return new MensagemRequest("5511999999999", "Teste");
    }

    public static ZAPIPayloadAdiconaAoGrupo criaZAPIPayloadAdicionaAoGrupo() {
        return new ZAPIPayloadAdiconaAoGrupo(true, "12345-group", new String[]{"5511999999999"});
    }

    public static ZAPIPayload criaZapiPayload() {
        return new ZAPIPayload("5511999999999", "Mensagem de Teste");
    }

    public static ZApiEventDto criaWhatsAppMessageDto() {
        return new ZApiEventDto(
                ZApiEventype.NORMAL_MESSAGE,
                "5573988000000",
                "Mensagem de teste!"
        );
    }

    public static ZApiEventDto criaWhatsAppMessageDataDiferente() {
        return new ZApiEventDto(
                ZApiEventype.NORMAL_MESSAGE,
                "5573988000000",
                "Mensagem de teste!",
                LocalDate.now().minusDays(1)
        );
    }

    public static ClintContatoRequest criaClintContatoRequest() {
        return new ClintContatoRequest(
                UUID.randomUUID().toString(),
                "João",
                "11122233344",
                "5573988000000",
                "teste@testte.com",
                "2011-11-11",
                "João Fiador",
                "Formulario_Preenchido"
        );
    }
    
    public static ZAPIResponseGrupo criaZAPIResponseGrupo() {
    	return new ZAPIResponseGrupo(true);
    }
    
	public static ZAPIPayloadRemoveDoGrupo criaZAPIPayloadRemoveDoGrupo() {
		return new ZAPIPayloadRemoveDoGrupo("12345-group", new String[]{"5511999999999"});
	}
}
