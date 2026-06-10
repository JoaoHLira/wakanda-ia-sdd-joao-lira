package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayloadAdiconaAoGrupo;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoProcessorAddToGroup implements ComunicacaoProcessorWhatsapp {

    private final ComunicacaoService comunicacaoService;

    @Value("${z-api.group-id-profissao-programador}")
    private String phoneDoGrupoID;

    @Override
    public boolean validaSeProcessaMensagem(ZApiEventype whatsappMessageDto) {
        return whatsappMessageDto.equals(ZApiEventype.ADD_TO_GROUP);
    }

    @Override
    public void processaEnvioDaMensagem(ZApiEventDto ZApiEventDto) {
        log.info("[start] ComunicacaoProcessorAddToGroup - processaEnvioDaMensagem");
        String[] whatsapp = new String[]{ZApiEventDto.getWhatsapp()};
        comunicacaoService.adicionaWakanderAoGrupo(new ZAPIPayloadAdiconaAoGrupo(
                true,
                phoneDoGrupoID,
                whatsapp
        ));
        enviaProgressoCheckList(ZApiEventDto);
        log.debug("[finish] ComunicacaoProcessorAddToGroup - processaEnvioDaMensagem");
    }

    private void enviaProgressoCheckList(ZApiEventDto ZApiEventDto) {
        log.info("[start] ComunicacaoProcessorAddToGroup - enviaProgressoCheckList");
        String whatsapp = ZApiEventDto.getWhatsapp();
        String mensagem = ZApiEventDto.getMensagem();
        comunicacaoService.enviaMensagemWhatsapp(new MensagemRequest(whatsapp, mensagem));
        log.debug("[finish] ComunicacaoProcessorAddToGroup - enviaProgressoCheckList");
    }
}
