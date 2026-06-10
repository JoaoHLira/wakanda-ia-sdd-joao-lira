package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoProcessorNormal implements ComunicacaoProcessorWhatsapp {

    private final ComunicacaoService comunicacaoService;

    @Override
    public boolean validaSeProcessaMensagem(ZApiEventype whatsappMessageDto){
        return whatsappMessageDto.equals(ZApiEventype.NORMAL_MESSAGE);
    }

    @Override
    public void processaEnvioDaMensagem(ZApiEventDto ZApiEventDto){
        log.info("[start] ComunicacaoProcessorNormal - processaEnvioDaMensagem");
        comunicacaoService.enviaMensagemWhatsapp(new MensagemRequest(ZApiEventDto.getWhatsapp(),
                ZApiEventDto.getMensagem()));
        log.debug("[finish] ComunicacaoProcessorNormal - processaEnvioDaMensagem");
    }
}
