package academy.wakanda.wakanda_ai.financeiro.application.api;

import academy.wakanda.wakanda_ai.docs.swagger.FinanceiroAPIDocs;
import academy.wakanda.wakanda_ai.financeiro.application.service.assinatura.AssinaturaAsaasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/financeiro/assinaturas")
@Tag(name = "AssinaturaAPI", description = "Controle responsavel pela Assinatura do wakander.")
public class AssinaturaAPI {

    private final AssinaturaAsaasService assinaturaAsaasService;

    @FinanceiroAPIDocs.Financeiro.ProcessaEvento
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void processaEventoDeAssinatura(@RequestBody AssinaturaAsaasDto assinaturaEvento) {
        log.info("[start] AssinaturaAPI - processaEventoDeAssinatura");
        assinaturaAsaasService.processaAssinaturaPorEvento(assinaturaEvento);
        log.debug("[finish] AssinaturaAPI - processaEventoDeAssinatura");
    }

    @FinanceiroAPIDocs.Financeiro.AtualizaDadosFiador
    @PatchMapping("/fiador/{token}")
    @ResponseStatus(HttpStatus.OK)
    void atualizaDadosFiador(@PathVariable String token, @Valid @RequestBody FiadorDTO fiadorDTO) {
        log.info("[start] AssinaturaAPI - atualizaDadosFiador");
        assinaturaAsaasService.atualizaDadosFiador(token, fiadorDTO);
        log.debug("[finish] AssinaturaAPI - atualizaDadosFiador");
    }
}
