package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api;

import academy.wakanda.wakanda_ai.docs.swagger.JornadaWakandaAPIDocs;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/jornadas")
@RequiredArgsConstructor
@Tag(name = "JornadaAPI", description = "Controle responsavel pelas operações da Jornada.")
public class JornadaWakandaAPI {

    private final JornadaWakandaService jornadaWakandaService;

    @JornadaWakandaAPIDocs.CriaNovaJornada
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public JornadaResponse criaNovaJornada(@RequestBody @Valid JornadaCriacaoRequest jornadaCriacaoRequest){
        log.info("[start] JornadaAPI - criaNovaJornada");
        JornadaResponse jornadaResponse = jornadaWakandaService.criarJornada(jornadaCriacaoRequest);
        log.debug("[finish] JornadaAPI - criaNovaJornada");
        return jornadaResponse;
    }

    @JornadaWakandaAPIDocs.BuscaJornadaPorIdTrilha
    @GetMapping("/{idTrilha}/lista-jornadas")
    @ResponseStatus(code = HttpStatus.OK)
    public List<JornadaListResponse> buscaJornadasPorIdTrilha(@PathVariable UUID idTrilha){
        log.info("[start] JornadaAPI - buscaJornadasPorIdTrilha");
        List<JornadaListResponse> jornadasResponse = jornadaWakandaService.buscaJornadasPorIdTrilha(idTrilha);
        log.debug("[finish] JornadaAPI - buscaJornadasPorIdTrilha");
        return jornadasResponse;
    }

    @JornadaWakandaAPIDocs.BuscaJornadaPorId
    @GetMapping("/{idJornada}")
    @ResponseStatus(code = HttpStatus.OK)
    public JornadaDetalhadaResponse buscaJornadaPorId(@PathVariable UUID idJornada) {
        log.info("[start] JornadaWakandaAPI - buscaJornadaPorId");
       JornadaDetalhadaResponse jornadaDetalhadaResponse = jornadaWakandaService.buscaJornadaDetalhada(idJornada);
        log.debug("[finish] JornadaWakandaAPI - buscaJornadaPorId");
        return jornadaDetalhadaResponse;
    }

    @JornadaWakandaAPIDocs.ListaJornadas
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<JornadaListResponse> listaJornadas() {
        log.info("[start] JornadaWakandaAPI - listaJornadas");
        List<JornadaListResponse> jornadaListResponse = jornadaWakandaService.listaJornadas();
        log.debug("[finish] JornadaWakandaAPI - listaJornadas");
        return jornadaListResponse;
    }
}
