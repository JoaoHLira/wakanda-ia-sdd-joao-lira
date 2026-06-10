package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayloadRemoveDoGrupo;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoProcessorRemoveToGroup implements ComunicacaoProcessorWhatsapp {
	private final ComunicacaoService comunicacaoService;

	@Value("${z-api.group-id-profissao-programador}")
	private String phoneDoGrupoID;

	@Override
	public boolean validaSeProcessaMensagem(ZApiEventype whatsappMessageDto) {
		return whatsappMessageDto.equals(ZApiEventype.REMOVE_TO_GROUP);
	}

	@Override
	public void processaEnvioDaMensagem(ZApiEventDto ZApiEventDto) {
		log.info("[start] ComunicacaoProcessorRemoveToGroup - processaEnvioDaMensagem");
		String[] whatsapp = new String[] { ZApiEventDto.getWhatsapp() };
		comunicacaoService.removeWakanderDoGrupo(new ZAPIPayloadRemoveDoGrupo(phoneDoGrupoID, whatsapp));
		log.debug("[finish] ComunicacaoProcessorRemoveToGroup - processaEnvioDaMensagem");
	}
}
