package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores.ComunicacaoProcessorWhatsapp;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ComunicacaoWhatsappApplicationService implements ComunicacaoWhatsappService {
    private final List<ComunicacaoProcessorWhatsapp> comunicacaoProcessorWhatsapp;

    @Override
    public void processaPorTipoMensagem(ZApiEventDto message) {
        log.info("[start] ComunicacaoWhatsAppApplicationService - processaPorTipoMensagem");
        ComunicacaoProcessorWhatsapp whatsAppProcessor = strategyComunicacaoWhatsAppProcessor(message);
        whatsAppProcessor.processaEnvioDaMensagem(message);
        log.debug("[finish] ComunicacaoWhatsAppApplicationService - processaPorTipoMensagem");
    }

    private ComunicacaoProcessorWhatsapp strategyComunicacaoWhatsAppProcessor(ZApiEventDto message) {
        return comunicacaoProcessorWhatsapp.stream()
                .filter(m -> m.validaSeProcessaMensagem(message.getType()))
                .findFirst()
                .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Tipo da mensagem nao é processada"));
    }
}
