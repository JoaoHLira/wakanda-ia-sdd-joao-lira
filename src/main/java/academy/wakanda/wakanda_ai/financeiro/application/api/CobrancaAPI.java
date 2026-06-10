package academy.wakanda.wakanda_ai.financeiro.application.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import academy.wakanda.wakanda_ai.docs.swagger.FinanceiroAPIDocs;
import academy.wakanda.wakanda_ai.financeiro.application.service.cobranca.CobrancaAsaasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/financeiro/cobranca")
@Tag(name = "CobrancaAPI", description = "Controle responsavel pelo financeiro do wakander.")
public class CobrancaAPI {
	private final CobrancaAsaasService cobrancaAsaasService;

	@FinanceiroAPIDocs.Cobranca.ProcessaEvento
	@PostMapping("/processa-evento")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void processaEvento(@Valid @RequestBody CobrancaAsaasDto request) {
		log.info("[start] CobrancaAPI - processaEvento");
		log.debug("Evento recebido: {}", request);
		cobrancaAsaasService.processaEvento(request);
		log.debug("[finish] CobrancaAPI - processaEvento");
	}
}
