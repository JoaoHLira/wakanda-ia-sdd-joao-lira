package academy.wakanda.wakanda_ai.comunicacao.application.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.docs.swagger.ComunicacaoAPIDocs;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/whatsapp-message")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "ComunicacaoApi", description = "Controle responsável pelas operações de comunicação, incluindo o envio de mensagens via WhatsApp.")
public class ComunicacaoApi {
    private final ComunicacaoService comunicacaoService;

    @ComunicacaoAPIDocs.EnvioMensagem
    @PostMapping("/envia")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void postMensagem(@RequestBody MensagemRequest request) {
        log.info("[start] ComunicacaoApi - postMensagem");
        comunicacaoService.enviaMensagemWhatsapp(request);
        log.debug("[finish] ComunicacaoApi - postMensagem");
    }

    @PostMapping("/convida")
    @ResponseStatus(HttpStatus.OK)
    public DiscordConviteResponse convidaParaCanalDiscord(@RequestBody DiscordConviteRequest conviteRequest){
        log.info("[start] ComunicacaoApi - convidaWakanderParaCanalDiscord");
        DiscordConviteResponse response = comunicacaoService.convidaParaCanalDiscord(conviteRequest);
        log.debug("[finish] ComunicacaoApi - convidaWakanderParaCanalDiscord");
        return response;
    }

    @ComunicacaoAPIDocs.PublicaNotificacaoTeste
    @PostMapping("/publica-notificacao")
    @ResponseStatus(HttpStatus.OK)
    public void postPublicaNotificacao(@RequestBody NotificacaoRequest request) {
        log.info("[start] ComunicacaoApi - postPublicaMensagem");
        comunicacaoService.publicaNotificacao(request);
        log.debug("[finish] ComunicacaoApi - postPublicaMensagem");
    }
}