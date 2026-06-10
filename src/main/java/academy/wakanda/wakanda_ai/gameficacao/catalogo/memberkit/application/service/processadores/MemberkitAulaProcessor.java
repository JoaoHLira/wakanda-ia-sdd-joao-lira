package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.AulaMemberKitDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitEventType;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.AulaAssistidaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class MemberkitAulaProcessor implements MemberkitProcessor {

    private final ObjectMapper objectMapper;
    private final AulaAssistidaService aulaAssistidaService;
    @Override
    public boolean validaSeEventoProcessa(String type) {
        return type.equals(MemberkitEventType.AULA_ASSISTIDA.getDescricao());
    }

    @Override
    public void processaEvento(MemberkitEventRequest request) {
        log.info("[start] MemberkitAulaProcessor - processaEvento");
        log.debug("[request] {}", request);
        try {
            AulaMemberKitDTO aulaMemberKitDTO = deserializeMessageContent(request, AulaMemberKitDTO.class);
            log.debug("[AulaMemberKitDTO] {}", aulaMemberKitDTO);
            aulaAssistidaService.processaEventoAulaAssistida(aulaMemberKitDTO);
        } catch (JsonProcessingException e) {
            log.error("Erro ao desserializar o conteúdo da mensagem: ", e);
        }
        log.debug("[finish] MemberkitAulaProcessor - processaEvento");
    }

    public <T> T deserializeMessageContent(MemberkitEventRequest request, Class<T> classeModelo )
            throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(request);
        return objectMapper.readValue(payload, classeModelo );
    }
}
