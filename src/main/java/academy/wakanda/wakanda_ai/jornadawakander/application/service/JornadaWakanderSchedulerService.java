package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.MotivationalMessages;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.domain.StatusRelatorio;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderRelatorioDTO;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderInativoResponse;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderEstudo;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderJDBCRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class JornadaWakanderSchedulerService {
    private final TopicNames topicNames;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final WakanderService wakanderService;
    private final WakanderJDBCRepository jdbcRepository;
    private final HistoricoRelatorioService historicoRelatorioService;

    @Value("${wakander.inatividade.dias}")
    private int diasInatividade;
    @Value("${z-api.lideres-group-id}")
    private String lideresPhoneGroupId;

//    @Scheduled(cron = "0 0 8 * * 1,3,5")
    public void notificaWakandersNaoEstudaram() {
        log.info("[start] SchedulerService - notificaWakandersNaoEstudaram");
        MotivationalMessages.changeCurrentMessage();
        LocalDateTime dataUltimaVerificacao = LocalDate.now().minusDays(diasInatividade).atTime(8, 0, 1);
        List<WakanderEstudo> wakandersSemEstudar = wakanderService.buscaWakandersSemEstudar(dataUltimaVerificacao);
        String mensagemIncentivo = MotivationalMessages.getCurrentMessage();
        publicaSnsWakandersEstudo(wakandersSemEstudar, mensagemIncentivo);
        log.info("Número de Wakanders sem estudar: {}", wakandersSemEstudar.size());
        log.debug("[finish] SchedulerService - notificaWakandersNaoEstudaram");
    }

//    @Scheduled(cron = "0 0 8 * * 1,3,5")
    public void checaWakanderQueEstudaram() {
        log.info("[start] SchedulerService - checaWakanderQueEstudaram");
        LocalDateTime dataInicio = LocalDate.now().minusDays(2).atTime(8, 0, 1);
        List<WakanderEstudo> wakanders = jdbcRepository.buscaWakandersQueEstudaram(
                dataInicio, LocalDate.now().atTime(8, 0, 1));
        String mensagemParabenizando = MensagensWhatsapp.MENSAGEM_PARABENIZANDO.getMensagem();
        publicaSnsWakandersEstudo(wakanders, mensagemParabenizando);
        log.debug("[finish] SchedulerService - checaWakanderQueEstudaram");
    }

    private void publicaSnsWakandersEstudo(List<WakanderEstudo> wakanders, String mensagem) {
        wakanders.forEach(payload -> {
            String mensagemFinal = formataMensagem(payload.getNome(), mensagem);
            ZApiEventDto whatsappMessage = new ZApiEventDto(
                    ZApiEventype.TODAY_ONLY_MESSAGE,
                    payload.getWhatsapp(),
                    mensagemFinal
            );
            publicadorNotificacaoSns.enviaNotificacaoSns(payload.getIdWakander(), whatsappMessage, topicNames.getZapiRequests());
        });
    }

    private String formataMensagem(String nomeWakander, String mensagem){
        return mensagem.replaceAll("Wakander", nomeWakander.split(" ")[0]);
    }

    @Scheduled(cron = "0 0 8 * * MON")
    public void agendaEnvioRelatorio() {
        log.info("[start] SchedulerService - agendaEnvioRelatorio");
        WakanderRelatorioDTO metricasDosWakanders = jdbcRepository.buscaMetricasWakander();
        validaMetricas(metricasDosWakanders);
        String relatorio = MensagensWhatsapp.RELATORIO_ATIVIDADE_WAKANDERS.formataRelatorioWakanderAtivos(metricasDosWakanders);
        publicaSnsRelatorioWhatsappLideranca(relatorio);
        historicoRelatorioService.registraRelatorio(StatusRelatorio.SUCESSO, relatorio);
        log.debug("[finish] SchedulerService - agendaEnvioRelatorio");
    }

  private void validaMetricas(WakanderRelatorioDTO metricasDosWakanders) {
        if (metricasDosWakanders.getWakandersConhecimento() == 0) {
            log.warn("Não existe wakanders ativos na jornada de conhecimento. Nenhuma mensagem será enviada.");
            throw APIException.build(HttpStatus.NO_CONTENT, "Nenhum wakander ativo na jornada de conhecimento.");
        }
    }

    @Scheduled(cron = "0 0 8 * * 1")
    public void geraRelatorioWakandersInativos() {
        log.info("[start] SchedulerService - geraRelatorioWakandersInativos");
        LocalDateTime dataInatividade = LocalDate.now().minusDays(15).atTime(8, 0, 0);
        List<WakanderInativoResponse> wakanders = wakanderService.buscaWakandersInativos(dataInatividade);
        enviaRelatorioWakanderInativos(wakanders);
        log.debug("[finish] SchedulerService - geraRelatorioWakandersInativos");
    }

    private void enviaRelatorioWakanderInativos(List<WakanderInativoResponse> wakanders) {
        log.info("[start] SchedulerService - enviaRelatorioWakanderInativos");
        if (wakanders.isEmpty()) {
            publicaSnsRelatorioWhatsappLideranca(MensagensWhatsapp.RELATORIO_INATIVIDADE_LIDERANCA.getMensagem());
        } else {
            String relatorio = MensagensWhatsapp.formataRelatorioWakandersInativos(wakanders);
            publicaSnsRelatorioWhatsappLideranca(relatorio);
        }
        log.debug("[finish] SchedulerService - enviaRelatorioWakanderInativos");
    }

    private void publicaSnsRelatorioWhatsappLideranca(String mensagem) {
        publicadorNotificacaoSns.enviaNotificacaoSns(
                UUID.randomUUID().toString(),
                new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, lideresPhoneGroupId, mensagem),
                topicNames.getZapiRequests()
        );
    }
}
