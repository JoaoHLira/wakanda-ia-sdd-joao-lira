package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.LoginMemberkitDto;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitEventType;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class MemberkitLoginEnviadoProcessor implements MemberkitProcessor {
    private final TopicNames topicNames;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    @Value("${memberkit.url-memberkit-wakanda}")
    private String urlMemberkit;
    private final ObjectMapper objectMapper;
    private final WakanderRepository wakanderRepository;

    @Override
    public boolean validaSeEventoProcessa(String type) {
        return type.equals(MemberkitEventType.LOGIN_ENVIADO.getDescricao());
    }

    @Override
    public void processaEvento(MemberkitEventRequest request) {
        log.info("[start] LoginEnviadoMemberkitProcessor - processaEvento");
        try {
            LoginMemberkitDto loginMemberkitDto = deserializeMessageContent(request, LoginMemberkitDto.class);
            log.debug("[LoginMemberKitDTO] {}", loginMemberkitDto);
            enviaMensagem(loginMemberkitDto);
        } catch (JsonProcessingException e) {
            log.error("Erro ao desserializar o conteúdo da mensagem: ", e);
        }
        log.debug("[request] {}", request);
        log.debug("[finish] LoginEnviadoMemberkitProcessor - processaEvento");
    }

    private void enviaMensagem(LoginMemberkitDto loginMemberkitDto) {
        List<String> mensagens = geraMensagemDeAcessoMemberkit(loginMemberkitDto);
        Wakander wakander = wakanderRepository.buscaWakanderPorIdMemberKit(loginMemberkitDto.getData().getIdMemberKit());
        CompletableFuture.runAsync(() -> {
            mensagens.forEach(mensagem ->
                    publicaSnsMensagemWhatsapp(wakander.getIdWakander(), wakander.getContato().getWhatsapp(), mensagem));
        }, CompletableFuture.delayedExecutor(1, TimeUnit.MINUTES));
    }

    private List<String> geraMensagemDeAcessoMemberkit(LoginMemberkitDto loginMemberkit) {
        String login = MensagensWhatsapp.MENSAGEM_LOGIN_ONBOARDING.formataLoginAcessoMemberkit(loginMemberkit);
        String mensagemAcessoMemberkit = MensagensWhatsapp.CHECKLIST_ACESSO_MEMBERKIT.getMensagem(urlMemberkit);
        return List.of(login, mensagemAcessoMemberkit);
	}

    private void publicaSnsMensagemWhatsapp(UUID idWakander, String whatsapp, String mensagem) {
        ZApiEventDto ZApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, whatsapp, mensagem);
        publicadorNotificacaoSns.enviaNotificacaoSns(
                idWakander.toString(),
                ZApiEventDto,
                topicNames.getZapiRequests()
        );
    }

    public <T> T deserializeMessageContent(MemberkitEventRequest request, Class<T> classeModelo )
            throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(request);
        return objectMapper.readValue(payload, classeModelo );
    }
}
