package academy.wakanda.wakanda_ai.comunicacao.infra;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ClintContatoRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ComunicacaoWhatsappService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.constants.MensagensDiscord;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.ComunicacaoDiscordService;
import academy.wakanda.wakanda_ai.wakander.application.event.CadastroCompletoEvent;
import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.domain.DiscordEventype;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoConsumerSqs {
    private final ObjectMapper objectMapper;
    private final ComunicacaoWhatsappService comunicacaoWhatsAppService;
    private final WakanderService wakanderService;
    private final ComunicacaoService comunicacaoService;
    private final ComunicacaoDiscordService comunicacaoDiscordService;

    @Value("${z-api.lideres-group-id}")
    private String groupIdLideres;

    @SqsListener("${aws.queue.teste}")
    public void consomeMensagem(SqsMessageDto sqsMessageDto) {
        log.info("[start] JornadaWakanderConsumerSqs - consomeMensagem");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        log.debug("[finish] JornadaWakanderConsumerSqs - consomeMensagem");
    }

    @SqsListener("${aws.queue.zapi-requests}")
    public void consomeQueueDoWhatsapp(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeQueueDoWhatsApp");
        log.info("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            ZApiEventDto message = deserializesqsMessageContent(sqsMessageDto, ZApiEventDto.class);
            comunicacaoWhatsAppService.processaPorTipoMensagem(message);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeQueueDoWhatsApp");
    }

    @SqsListener("${aws.queue.zapi-requests-delay}")
    public void consomeQueueDoWhatsappComDelay(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeQueueDoWhatsappComDelay");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            ZApiEventDto message = deserializesqsMessageContent(sqsMessageDto, ZApiEventDto.class);
            comunicacaoWhatsAppService.processaPorTipoMensagem(message);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeQueueDoWhatsappComDelay");
    }

    @SqsListener("${aws.queue.memberkit-requests-dlq}")
    public void consomeDlqQueueDoMemberkit(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeQueueDoMemberkit");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            String mensagem = formataMensagemErroMemberkit(sqsMessageDto);
            ZApiEventDto zApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, groupIdLideres, mensagem);
            comunicacaoWhatsAppService.processaPorTipoMensagem(zApiEventDto);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeQueueDoMemberkit");
    }

    @SqsListener("${aws.queue.asaas-requests}")
    public void consomeAsaasRequest(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeAsaasRequest");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            Wakander wakander = deserializesqsMessageContent(sqsMessageDto, Wakander.class);
            wakanderService.buscaDadosAsaas(wakander);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeAsaasRequest");
    }

    private String formataMensagemErroMemberkit(SqsMessageDto sqsMessageDto) throws JsonProcessingException {
        CadastroCompletoEvent evento = deserializesqsMessageContent(sqsMessageDto, CadastroCompletoEvent.class);
        String mensagem = MensagensWhatsapp.MENSAGEM_FALHA_MEMBERKIT.formataMensagemFalhaMemberkit(
                evento.getIdWakander().toString(),
                evento.getNome(),
                evento.getEmail(),
                evento.getWhatsapp(),
                LocalDateTime.now().toString()
        );
        return mensagem;
    }

    public <T> T deserializesqsMessageContent(SqsMessageDto sqsMessageDto, Class<T> clazz)
            throws JsonProcessingException {
        return objectMapper.readValue(sqsMessageDto.getMessage(), clazz);
    }

    @SqsListener("${aws.queue.clint-contato-requests}")
    public void consomeQueueDaClintContato(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeQueueDaClint");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            ClintContatoRequest contatoClint = deserializesqsMessageContent(sqsMessageDto, ClintContatoRequest.class);
            comunicacaoService.enviaContatoParaClint(contatoClint);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeQueueDaClint");
    }

    @SqsListener("${aws.queue.clint-contato-requests-dlq}")
    public void consomeDlqQueueDaClintContato(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeDlqQueueDaClintContato");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            ClintContatoRequest contatoClint = deserializesqsMessageContent(sqsMessageDto, ClintContatoRequest.class);
            String mensagemFalhaClint = MensagensWhatsapp.MENSAGEM_FALHA_ENVIO_CONTATO_CLINT.mensagemFalhaEnvioContatoClint(
                    contatoClint.getIdWakander(), contatoClint.getNome(), contatoClint.getEmail(), contatoClint.getWhatsapp(),
                    LocalDateTime.now().toString());
            ZApiEventDto zApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, groupIdLideres, mensagemFalhaClint);
            comunicacaoWhatsAppService.processaPorTipoMensagem(zApiEventDto);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeDlqQueueDaClintContato");
    }

    @SqsListener("${aws.queue.zapi-requests-dlq}")
    public void consomeDlqQueueDoWhatsapp(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeDlqQueueDoWhatsapp");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            processaDlqWhatsapp(sqsMessageDto);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        } catch (APIException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Tipo de evento não tratado, a mensagem será reprocessada.");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeDlqQueueDoWhatsapp");
    }

	private void processaDlqWhatsapp(SqsMessageDto sqsMessageDto) throws JsonProcessingException {
		ZApiEventDto zApiEventDto = deserializesqsMessageContent(sqsMessageDto, ZApiEventDto.class);
		if (zApiEventDto.validaSeTypeRemoveToGroup()) {
			String mensagem = MensagensWhatsapp.MENSAGEM_FALHA_AO_REMOVER_WAKANDER_WHATSAPP
					.formataMensagem(zApiEventDto.getMensagem(), zApiEventDto.getWhatsapp());
			comunicacaoService.enviaMensagemWhatsapp(new MensagemRequest(groupIdLideres, mensagem));
		} else {
		    throw APIException.build(HttpStatus.BAD_REQUEST, "Tipo da mensagem nao é processada no momento.");
		}
	}
	
	@SqsListener("${aws.queue.discord-request}")
	public void removeWakanderDoServidorDiscord(SqsMessageDto sqsMessageDto) throws JsonProcessingException{
        log.info("[start] ComunicacaoConsumerSqs - removeWakanderDoServidorDiscord");
        DiscordEventRequest discordEventRequest = deserializesqsMessageContent(sqsMessageDto, 
        		DiscordEventRequest.class);
        comunicacaoDiscordService.processaPorTipoMensagem(discordEventRequest);
        log.debug("[finish] ComunicacaoConsumerSqs - removeWakanderDoServidorDiscord");
	}
	
	@SqsListener("${aws.queue.discord-dlq}")
	public void consumeDlqDiscord(SqsMessageDto sqsMessageDto) throws JsonProcessingException{
        log.info("[start] ComunicacaoConsumerSqs - consumeDlqDiscord");
	        DiscordEventRequest request = deserializesqsMessageContent(sqsMessageDto, DiscordEventRequest.class);
	    if(request.getType().equals(DiscordEventype.REMOVE_FROM_SERVER)) {
	        String mensagem = MensagensDiscord.MENSAGEM_FALHA_LIDERANCA.formataMensagem(request.getNome(), request.getMotivoCancelamento());
	        ZApiEventDto zApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, groupIdLideres, mensagem);
	        comunicacaoWhatsAppService.processaPorTipoMensagem(zApiEventDto);
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consumeDlqDiscord");
	}

    @SqsListener("${aws.queue.clint-requests-dlq}")
    public void consomeQueueClintDlq(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeQueueClintDlq");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            Wakander wakander = deserializesqsMessageContent(sqsMessageDto, Wakander.class);
            comunicacaoService.enviaMsgCancelamentoClint(wakander, false);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeQueueClintDlq");
    }

    @SqsListener("${aws.queue.clint-requests}")
    public void consomeQueueClint(SqsMessageDto sqsMessageDto) {
        log.info("[start] ComunicacaoConsumerSqs - consomeQueueClint");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            Wakander wakander = deserializesqsMessageContent(sqsMessageDto, Wakander.class);
            comunicacaoService.cancelaWakanderClint(wakander);
        }
        catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "[error] Não foi possível mapear o json!");
        }
        log.debug("[finish] ComunicacaoConsumerSqs - consomeQueueClint");
    }
}
