package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import academy.wakanda.wakanda_ai.docs.swagger.ClasseWakandaAPIDocs;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.service.ClasseWakandaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@Tag(name = "ClasseWakandaAPI", description = "Controle responsável pelas operações da classe Wakanda.")
public class ClasseWakandaAPI {

	private final ClasseWakandaService classeWakandaService;

	@ClasseWakandaAPIDocs.CriaNovaClasse
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public ClasseWakandaResponse criaNovaClasse(@RequestBody @Valid ClasseWakandaRequest classeRequest) {
		log.info("[start] ClasseWakandaAPI - criaNovaClasse");
		ClasseWakandaResponse classeResponse = classeWakandaService.criaNovaClasse(classeRequest);
		log.debug("[finish] ClasseWakandaAPI - criaNovaClasse");
		return classeResponse;
	}

	@ClasseWakandaAPIDocs.BuscaClassePorId
	@GetMapping("/{idClasse}")
	@ResponseStatus(code = HttpStatus.OK)
	public ClasseWakandaResponse buscaClassePorId(@PathVariable UUID idClasse) {
		log.info("[start] ClasseWakandaAPI - buscaClassePorId");
		ClasseWakandaResponse classeResponse = classeWakandaService.buscaClassePorId(idClasse);
		log.debug("[finish] ClasseWakandaAPI - buscaClassePorId");
		return classeResponse;
	}

	@ClasseWakandaAPIDocs.BuscaClassesAtivas
	@GetMapping("/ativas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ClasseWakandaResponse> buscaClassesAtivas() {
		log.info("[start] ClasseWakandaAPI - buscaClassesAtivas");
		List<ClasseWakandaResponse> classesAtivas = classeWakandaService.buscaClassesAtivas();
		log.debug("[finish] ClasseWakandaAPI - buscaClassesAtivas total={}", classesAtivas.size());
		return classesAtivas;
	}

	@ClasseWakandaAPIDocs.AtualizaRequisitosClasse
	@PatchMapping("/{idClasse}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void atualizaRequisitosClasse(@RequestBody @Valid AtualizaRequisitosRequest atualizaRequisitosRequest,
			@PathVariable UUID idClasse) {
		log.info("[start] ClasseWakandaAPI - atualizaRequisitosClasse");
		classeWakandaService.atualizaRequisitosClasse(atualizaRequisitosRequest, idClasse);
		log.debug("[finish] ClasseWakandaAPI - atualizaRequisitosClasse");
	}

}
