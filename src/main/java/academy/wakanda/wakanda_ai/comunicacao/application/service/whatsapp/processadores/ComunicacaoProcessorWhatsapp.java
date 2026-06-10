package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;

public interface ComunicacaoProcessorWhatsapp {
    boolean validaSeProcessaMensagem(ZApiEventype whatsappMessageDto);
    void processaEnvioDaMensagem(ZApiEventDto ZApiEventDto);
}
