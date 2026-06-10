package academy.wakanda.wakanda_ai.wakander.application.event;

import academy.wakanda.wakanda_ai.comunicacao.application.api.DiscordConviteRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.DiscordConviteResponse;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.MensagensDiscord;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderApplicationService;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProgressoOnboardConsumer {
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final TopicNames topicNames;
    private final ComunicacaoService comunicacaoService;
    private final OnboardingWakanderService onboardingWakanderService;
    private final ProgressoWakanderApplicationService progressoWakanderApplicationService;
    @Value("${z-api.lideres-group-id}")
    private String idGrupoLideres;
    private final ObjectMapper objectMapper;

    @EventListener
    public void atualizaStatusCadastroOnboarding(CadastroCompletoEvent evento) {
        log.info("[start] ProgressoOnboard - atualizaStatusCadastroOnboarding");
        log.debug("[received] Mensagem: {}", evento.toString());
        OnboardingWakander onboardingWakander = onboardingWakanderService.buscaOnboardingPorIdWakander(evento.getIdWakander());
        onboardingWakander.atualizaCadastroConfirmado();
        onboardingWakanderService.save(onboardingWakander);
        enviaChecklist(evento, onboardingWakander);
        log.debug("[finish] ProgressoOnboard - atualizaStatusCadastroOnboarding");
    }

    private void enviaChecklist(CadastroCompletoEvent evento, OnboardingWakander onboardingWakander) {
        log.info("[start] WakanderApplicationService - enviaChecklist");
        String checklist = onboardingWakanderService.retornaChecklist(onboardingWakander);
        List<String> mensagens = List.of(checklist, MensagensWhatsapp.MENSAGEM_CADASTRO_SUCESSO.getMensagem());
        mensagens.forEach(mensagem ->
                publicaMensagem(ZApiEventype.NORMAL_MESSAGE, evento, mensagem));
        log.debug("[finish] WakanderApplicationService - enviaChecklist");
    }

    private void publicaMensagem(ZApiEventype eventype, CadastroCompletoEvent evento, String mensagem) {
        log.info("[start] WakanderApplicationService - publicaMensagem");
        ZApiEventDto ZApiEventDto = new ZApiEventDto(eventype, evento.getWhatsapp(), mensagem);
        publicadorNotificacaoSns.enviaNotificacaoSns(
                evento.getIdWakander().toString(),
                ZApiEventDto,
                topicNames.getZapiRequests()
        );
        log.debug("[finish] WakanderApplicationService - publicaMensagem");
    }

    @EventListener
    public void adicionaWakanderAoGrupo(CadastroCompletoEvent evento) {
        log.info("[start] ProgressoOnboard - adicionaWakanderAoGrupo");
        log.debug("[received] Mensagem: {}", evento.toString());
        OnboardingWakander onboardingWakander = onboardingWakanderService.buscaOnboardingPorIdWakander(evento.getIdWakander());
        onboardingWakander.atualizaEntrouGrupoWhatsapp();
        onboardingWakanderService.save(onboardingWakander);
        String checklist = onboardingWakanderService.retornaChecklist(onboardingWakander);
        publicaMensagem(ZApiEventype.ADD_TO_GROUP, evento, MensagensWhatsapp.mensagemBoasVindasComChecklist(checklist));
        log.debug("[finish] ProgressoOnboard - adicionaWakanderAoGrupo");
    }

    @EventListener
    public void convidaWakanderParaDiscord(CadastroCompletoEvent evento) {
        log.info("[start] ProgressoOnboard - convidaWakanderParaDiscord");
        log.debug("[received] Mensagem: {}", evento.toString());
        DiscordConviteResponse conviteResponse = criaLinkDoConvite();
        String url = "https://discord.gg/" + conviteResponse.getCode();
        publicaMensagensDiscord(evento, url, conviteResponse);
        log.debug("[finish] ProgressoOnboard - convidaWakanderParaDiscord");
    }

    private void publicaMensagensDiscord(CadastroCompletoEvent evento, String url, DiscordConviteResponse conviteResponse) {
        List<String> mensagens = List.of(MensagensWhatsapp.MENSAGEM_CONVIDA_DISCORD.formataConviteDiscord(url),
                MensagensDiscord.MENSAGEM_EMAIL_DISCORD.formataMensagem(evento.getEmail()));
        mensagens.forEach(mensagem -> {
            ZApiEventDto ZApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, evento.getWhatsapp(), mensagem);
            publicadorNotificacaoSns.enviaNotificacaoSns(evento.getIdWakander().toString(), ZApiEventDto, topicNames.getZapiRequests());
        });
    }

    private DiscordConviteResponse criaLinkDoConvite() {
        log.info("[start] WakanderApplicationService - criaLinkDoConvite");
        DiscordConviteResponse discordConviteResponse = comunicacaoService.convidaParaCanalDiscord(new DiscordConviteRequest(1, true, 0));
        log.debug("[finish] WakanderApplicationService - criaLinkDoConvite");
        return discordConviteResponse;
    }

    @EventListener
    public void cadastraWakanderNoMemberkit(CadastroCompletoEvent evento) {
        log.info("[start] ProgressoOnboard - cadastraWakanderNoMemberkit");
        log.debug("[received] Mensagem: {}", evento.toString());
        JsonNode payloadNode = objectMapper.valueToTree(evento);
        publicadorNotificacaoSns.enviaNotificacaoSns(
                evento.getIdWakander().toString(),
                new MemberKitMessageEnvelope(MemberKitTipoRequisicao.CADASTRO_NOVO_MEMBRO, payloadNode),
                topicNames.getMemberkitRequests()
        );
        log.debug("[finish] ProgressoOnboard - cadastraWakanderNoMemberkit");
    }

    @EventListener
    public void enviaMensagemAoGrupoDeLideres(CadastroCompletoEvent evento) {
        log.info("[start] ProgressoOnboard - enviaMensagemAoGrupoDeLideres");

        ZApiEventDto ZApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE,
                idGrupoLideres,
                MensagensWhatsapp.MENSAGEM_NOVO_WAKANDER_GRUPO_LIDERES.mensagemNovoWakanderGrupoLideres(
                        evento.getNome(),
                        LocalDateTime.now()
                ));

        publicadorNotificacaoSns.enviaNotificacaoSns(idGrupoLideres,
                ZApiEventDto,
                topicNames.getZapiRequests());

        log.debug("[finish] ProgressoOnboard - enviaMensagemAoGrupoDeLideres");
    }

    @EventListener
    public void criaNovoProgressoWakander(CadastroCompletoEvent evento) {
        log.info("[start] ProgressoOnboardConsumer - criaNovoProgressoWakander");
        progressoWakanderApplicationService.novoProgresso(evento.getIdWakander());
        log.debug("[finish] ProgressoOnboardConsumer - criaNovoProgressoWakander");
    }


}
