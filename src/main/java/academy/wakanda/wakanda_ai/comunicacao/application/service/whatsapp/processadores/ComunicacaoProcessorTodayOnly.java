package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoProcessorTodayOnly implements ComunicacaoProcessorWhatsapp {

    private final ComunicacaoService comunicacaoService;

    @Override
    public boolean validaSeProcessaMensagem(ZApiEventype whatsappMessageDto){
        return whatsappMessageDto.equals(ZApiEventype.TODAY_ONLY_MESSAGE);
    }

    @Override
    public void processaEnvioDaMensagem(ZApiEventDto ZApiEventDto){
        log.info("[start] ComunicacaoProcessorGroup - processaEnvioDaMensagem");
        validaSeEventoEhDeHoje(ZApiEventDto.getDataEnvio());
        comunicacaoService.enviaMensagemWhatsapp(new MensagemRequest(ZApiEventDto.getWhatsapp(),
                ZApiEventDto.getMensagem()));
        log.debug("[finish] ComunicacaoProcessorGroup - processaEnvioDaMensagem");
    }

    private void validaSeEventoEhDeHoje(LocalDate dataEvento) {
        if (!dataEvento.isEqual(LocalDate.now())) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Data do evento não é hoje!");
        }
    }
}
