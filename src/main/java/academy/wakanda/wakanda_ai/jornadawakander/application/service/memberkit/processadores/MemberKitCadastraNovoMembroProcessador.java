package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores;

import academy.wakanda.wakanda_ai.constants.MemberkitProperties;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.CadastraMembroRequest;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.MemberkitUserDto;
import academy.wakanda.wakanda_ai.jornadawakander.infra.JornadaWakanderClient;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitRequestProcessor;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import academy.wakanda.wakanda_ai.wakander.application.event.CadastroCompletoEvent;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Log4j2
public class MemberKitCadastraNovoMembroProcessador implements MemberKitRequestProcessor {
    private final ObjectMapper objectMapper;
    private final WakanderRepository wakanderRepository;
    private final JornadaWakanderClient jornadaWakanderClient;
    private final MemberkitProperties memberkitProperties;

    @Override
    public boolean validaSeProcessa(MemberKitTipoRequisicao tipo) {
        return tipo.equals(MemberKitTipoRequisicao.CADASTRO_NOVO_MEMBRO);
    }

    @Override
    public void processaEvento(MemberKitMessageEnvelope envelope) {
        log.info("[start] MemberKitCadastraNovoMembroProcessador - processaEvento");
        CadastroCompletoEvent evento = deserializaPayload(envelope);
        CadastraMembroRequest request = new CadastraMembroRequest(evento, memberkitProperties.getMembershipLevelId(),
                memberkitProperties.getClassroomIds());
        MemberkitUserDto memberkitUser = jornadaWakanderClient.requisicaoPostParaOMemberKit(request, MemberkitUserDto.class);
        salvaIdMemberkit(evento, memberkitUser);
        log.debug("[finish] MemberKitCadastraNovoMembroProcessador - processaEvento");
    }

    private CadastroCompletoEvent deserializaPayload(MemberKitMessageEnvelope envelope) {
        try {
            return objectMapper.treeToValue(envelope.getPayload(), CadastroCompletoEvent.class);
        } catch (JsonProcessingException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Erro ao deserializar json!");
        }
    }

    private void salvaIdMemberkit(CadastroCompletoEvent evento, MemberkitUserDto memberkitUser) {
        Wakander wakander = wakanderRepository.buscaWakanderPorId(evento.getIdWakander());
        wakander.alteraIdMemberkit(memberkitUser.getId().toString());
        wakanderRepository.save(wakander);
    }
}
