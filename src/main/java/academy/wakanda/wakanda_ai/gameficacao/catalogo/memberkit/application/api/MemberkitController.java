package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitService;

@RestController
@RequestMapping("/memberkit")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "MemberKit Operacoes", description = "Controller responsável pelas operações do Memberkit")
public class MemberkitController {

    private final MemberkitService memberkitService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void receberEventoMemberkit(@Valid @RequestBody MemberkitEventRequest request) {
        log.info("[start] MemberkitController - receberEventoMemberkit");
        log.debug("[request] Tipo de evento: {}", request.getType());
        memberkitService.processaWebhook(request);
        log.debug("[finish] MemberkitController - receberEventoMemberkit - Evento processado com sucesso");
    }

    @PostMapping("/import")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void importMemberkit(@RequestParam UUID idTipoMissao, @RequestParam UUID idJornada) {
        log.info("[start] MemberkitController - importMemberkit");
        memberkitService.importaMemberkit(idTipoMissao, idJornada);
        log.debug("[finish] MemberkitController - importMemberkit");
    }
}
