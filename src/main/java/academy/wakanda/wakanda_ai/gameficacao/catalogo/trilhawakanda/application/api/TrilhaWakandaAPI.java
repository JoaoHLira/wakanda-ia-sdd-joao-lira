package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api;

import academy.wakanda.wakanda_ai.docs.swagger.TrilhaWakandaAPIDocs;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.service.TrilhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/trilhas")
public class TrilhaWakandaAPI {

    private final TrilhaService trilhaService;

    @TrilhaWakandaAPIDocs.CriaNovaTrilha
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TrilhaCriadaResponse criarNovaTrilha(@Valid @RequestBody TrilhaWakandaRequest novaTrilha) {
        log.info("[start] TrilhaWakandaAPI - trilhaCriada");
        TrilhaCriadaResponse trilhaCriada = trilhaService.criaTrilha(novaTrilha);
        log.debug("[finish] TrilhaWakandaAPI - trilhaCriada");
        return trilhaCriada;
    }

    @TrilhaWakandaAPIDocs.BuscaTrilhaPorId
    @GetMapping("/{idTrilha}")
    @ResponseStatus(code = HttpStatus.OK)
    public TrilhaDetalhadaResponse buscaTrilhaPorId(@PathVariable UUID idTrilha) {
        log.info("[start] TrilhaWakandaAPI - buscaTrilhaPorId");
        TrilhaDetalhadaResponse trilhaDetalhadaResponse = trilhaService.buscaTrilhaDetalhada(idTrilha);
        log.debug("[finish] TrilhaWakandaAPI - buscaTrilhaPorId");
        return trilhaDetalhadaResponse;
    }

    @TrilhaWakandaAPIDocs.ListaTrilhas
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<TrilhaListResponse> listaTrilhas() {
        log.info("[start] TrilhaWakandaAPI - listaTrilhas");
        List<TrilhaListResponse> trilhaListResponse = trilhaService.listaTrilhas();
        log.debug("[finish] TrilhaWakandaAPI - listaTrilhas");
        return trilhaListResponse;
    }
}
