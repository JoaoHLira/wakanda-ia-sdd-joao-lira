package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitAcessoRequest;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitRequestProcessor;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import academy.wakanda.wakanda_ai.jornadawakander.infra.JornadaWakanderClient;
import academy.wakanda.wakanda_ai.wakander.application.event.AssinaturaCanceladaEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MemberKitAcessoPlataformaBlockProcessador implements MemberKitRequestProcessor {

    private final JornadaWakanderClient jornadaWakanderClient;
    private final ObjectMapper objectMapper;

    @Override
    public boolean validaSeProcessa(MemberKitTipoRequisicao tipo) {
        return tipo.equals(MemberKitTipoRequisicao.ACESSO_BLOQUEADO);
    }

    @Override
    public void processaEvento(MemberKitMessageEnvelope envelope) {
        log.info("[start] MemberKitAcessoPlataformaBlockProcessador - processaEvento");
        AssinaturaCanceladaEvent event = deserializaPayload(envelope);
        MemberKitAcessoRequest request = new MemberKitAcessoRequest(event.getEmail(), true);
        jornadaWakanderClient.requisicaoPostParaOMemberKit(request, Void.class);
        log.debug("[finish] MemberKitAcessoPlataformaBlockProcessador - processaEvento");
    }

    private AssinaturaCanceladaEvent deserializaPayload(MemberKitMessageEnvelope envelope) {
        try {
            return objectMapper.treeToValue(envelope.getPayload(), AssinaturaCanceladaEvent.class);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Erro ao deserializar json!");
        }
    }
}
