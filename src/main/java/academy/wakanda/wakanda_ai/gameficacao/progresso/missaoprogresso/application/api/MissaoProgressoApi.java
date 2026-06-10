package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api;

import academy.wakanda.wakanda_ai.docs.swagger.MissaoProgressoAPIDocs;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/gameficacao/missao-progresso")
@Tag(name = "MissaoProgressoApi", description = "Controle responsável pelas operações de progresso de missão")
public class MissaoProgressoApi {

    private final MissaoProgressoService missaoProgressoService;

    @MissaoProgressoAPIDocs.CriaMissaoProgresso
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    MissaoProgressoResponse criaMissaoProgressoParaWakander(@RequestBody @Valid MissaoProgressoRequest missaoProgressoRequest) {
        log.info("[start] MissaoProgressoApi - criaMissaoProgressoParaWakander");
        MissaoProgressoResponse missaoProgressoCriada = missaoProgressoService.criaProgressoDeMissao(missaoProgressoRequest);
        log.debug("[finish] MissaoProgressoApi - criaMissaoProgressoParaWakander");
        return missaoProgressoCriada;
    }

    @MissaoProgressoAPIDocs.ConcluiMissaoManualmente
    @PatchMapping("/{idMissaoProgresso}/conclui")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void concluiMissaoManualmente(@PathVariable UUID idMissaoProgresso) {
        log.info("[start] MissaoProgressoApi - concluiMissaoManualmente");
        missaoProgressoService.concluiMissaoManualmente(idMissaoProgresso);
        log.debug("[finish] MissaoProgressoApi - concluiMissaoManualmente");
    }

    @MissaoProgressoAPIDocs.ListaDisponibilidadeMissoes
    @GetMapping("/wakander/{idWakander}/jornada/{idJornada}")
    @ResponseStatus(HttpStatus.OK)
    List<MissaoDisponibilidadeResponse> listaDisponibilidadeMissoes(@PathVariable UUID idWakander,
                                                                     @PathVariable UUID idJornada) {
        log.info("[start] MissaoProgressoApi - listaDisponibilidadeMissoes");
        List<MissaoDisponibilidadeResponse> resposta = missaoProgressoService
                .listaDisponibilidadeMissoes(idWakander, idJornada);
        log.debug("[finish] MissaoProgressoApi - listaDisponibilidadeMissoes");
        return resposta;
    }

    @MissaoProgressoAPIDocs.ListaMissoesConcluidas
    @GetMapping("/progresso/{idProgressoWakander}/missoes-concluidas")
    @ResponseStatus(HttpStatus.OK)
    Page<MissaoConcluidaResponse> listaMissoesConcluidas(@PathVariable UUID idProgressoWakander, Pageable pageable) {
        log.info("[start] MissaoProgressoApi - listaMissoesConcluidas");
        Page<MissaoConcluidaResponse> resposta = missaoProgressoService.listaMissoesConcluidas(idProgressoWakander, pageable);
        log.debug("[finish] MissaoProgressoApi - listaMissoesConcluidas");
        return resposta;
    }

}
