package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import static academy.wakanda.wakanda_ai.constants.MensagensWhatsapp.MENSAGEM_BOAS_VINDAS;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.config.security.TokenService;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaType;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.ClienteAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.infra.AsaasClient;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class AssinaturaProcessorCriada implements AssinaturaProcessorAsaas {
    private final WakanderRepository wakanderRepository;
    private final AsaasClient asaasClient;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final TopicNames topicNames;
    private final TokenService tokenService;

    @Value("${aws.url}")
    private String urlInstancia;

    @Value("${autenticacao.tempo-expiracao-cadastro}")
    private Integer tempoExpiracaoToken;

    @Override
    public boolean validaSeEventoProcessa(AssinaturaEvento assinaturaEvento) {
        return assinaturaEvento.getEvent().equals(AssinaturaType.SUBSCRIPTION_CREATED);
    }

    @Override
    public void processaEvento(AssinaturaEvento assinaturaEvento) {
        log.info("[start] AssinaturaProcessorCriada - processaEvento");
        try {
            buscaWakanderPorAssinatura(assinaturaEvento);
        } catch (APIException e) {
            verificaStatusException(e);
            log.info("Não foi encontrado Wakander com idAssinatura: {}", assinaturaEvento.getId());
            criaNovoWakanderIncompleto(assinaturaEvento);
        }
        log.debug("[finish] AssinaturaProcessorCriada - processaEvento");
    }

	private void buscaWakanderPorAssinatura(AssinaturaEvento assinaturaEvento) {
		wakanderRepository.buscaWakanderPorIdAssinatura(assinaturaEvento.getId())
        .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Wakander não encontrado para assinatura!"));
	}

    private void verificaStatusException(APIException e) {
        if (!e.getStatusException().equals(HttpStatus.NOT_FOUND)) {
            throw e;
        }
    }

    private void criaNovoWakanderIncompleto(AssinaturaEvento assinaturaEvento) {
        ClienteAsaasDto fiadorDto = asaasClient.buscaCliente(assinaturaEvento.getCustomer());
        Wakander wakander = wakanderRepository.save(new Wakander(assinaturaEvento, fiadorDto));
        enviaFormularioDeCadastro(wakander);
    }

    private void enviaFormularioDeCadastro(Wakander wakander) {
        log.info("[start] AssinaturaProcessorCriada - enviaFormularioDeCadastro");
        List<String> mensagens = geraMensagemDeBoasVindas(wakander);
        enviaMensagens(wakander, mensagens);
        log.debug("[finish] AssinaturaProcessorCriada - enviaFormularioDeCadastro");
    }

    private void enviaMensagens(Wakander wakander, List<String> mensagens) {
        mensagens.forEach(mensagem ->
                publicadorNotificacaoSns.enviaNotificacaoSns(
                        wakander.getIdWakander().toString(),
                        new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, wakander.getFiador().getTelefone(), mensagem),
                        topicNames.getZapiRequests()
                ));
    }

    private List<String> geraMensagemDeBoasVindas(Wakander wakander) {
        String mensagemBoasVindas = MENSAGEM_BOAS_VINDAS.getMensagem(urlFormulario(wakander));
        String mensagemChecklist = MensagensWhatsapp.mensagemChecklistPadrao();
        return List.of(mensagemBoasVindas, mensagemChecklist);
    }

    private String urlFormulario(Wakander wakander) {
        String token = tokenService.geraTokenDeAutenticacao(wakander.getIdWakander(), tempoExpiracaoToken);
        String url = UriComponentsBuilder.fromHttpUrl(urlInstancia)
                .path("/wakanda-ai/api/formulario/cadastro")
                .path("/" + token)
                .toUriString();
        log.debug("[formulario] Link do Formulário {}", url);
        return url;
    }
}
