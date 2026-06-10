package academy.wakanda.wakanda_ai.jornadawakander.application.api;

import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wakander/jornada")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "JornadaWakanderApi", description = "Controle responsavel pela jornada do wakander.")
public class JornadaWakanderApi {
    private final OnboardingWakanderService onboardingWakanderService;

	@PostMapping("/associar-discord")
	@ResponseStatus(value = HttpStatus.OK)
	public void associarContaDiscord(@RequestBody @Validated DiscordRequest request) {
		log.info("[inicia] AssociacaoController - associarContaDiscord");
		log.debug("[request] {}", request.toString());
		onboardingWakanderService.associarUsuarioDiscord(request);
		log.debug("[finaliza] AssociacaoController - associarContaDiscord");
	}
}
