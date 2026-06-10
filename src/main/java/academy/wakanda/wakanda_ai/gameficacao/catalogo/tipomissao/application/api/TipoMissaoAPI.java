package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api;

import academy.wakanda.wakanda_ai.docs.swagger.TipoMissaoAPIDocs;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service.TipoMissaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/tipos-missao")
@Tag(name = "TipoMissaoApi", description = "Controle responsável pelo Tipo de Missão.")
public class TipoMissaoAPI {

    private final TipoMissaoService tipoMissaoService;

    @TipoMissaoAPIDocs.InsereTipoMissao
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TipoMissaoResponse insereTipoMissao(@Valid @RequestBody TipoMissaoRequest tipoMissaoRequest) {
        log.info("[start] TipoMissaoAPI - insereTipoMissao");
        TipoMissaoResponse tipoMissaoResponse = tipoMissaoService.insereTipoMissao(tipoMissaoRequest);
        log.debug("[finish] TipoMissaoAPI - insereTipoMissao");
        return tipoMissaoResponse;
    }
}
