package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.adapter.MemberkitAulaAdapter;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.adapter.MemberkitCursoAdapter;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.adapter.MemberkitDTOAdapter;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitConfig;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MissaoDataDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MissaoOrigemDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoExternaDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service.MissaoWakandaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.utils.ValidacaoUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@AllArgsConstructor
public class MemberkitIntegrationImporter {

	private final MemberkitConfig memberkitConfig;
	private final MemberkitClientService memberkitClientService;
	private final MissaoWakandaService missaoWakandaService;

	protected void processarCurso(MemberkitCourseDTO curso, Map<String, List<MissaoOrigemDTO>> titulosDuplicados,
			UUID tipoMissao, UUID idJornada) {
		log.info("[start] MemberkitImportProcessor - processarCurso");
		MissaoWakanda missaoCurso = criarMissao(new MemberkitCursoAdapter(curso), tipoMissao, null, titulosDuplicados,
				idJornada);
		UUID idCurso = missaoCurso.getIdMissao();
		log.info("Curso processado com sucesso: {} (ID: {})", curso.getName(), curso.getId());
		buscarEArmazenarAulas(curso, titulosDuplicados, tipoMissao, idCurso, idJornada);
		log.debug("[finish] MemberkitImportProcessor - processarCurso");
	}

	protected void validarConfiguracoes() {
		log.info("[start] MemberkitImportProcessor - Validando configurações do Memberkit");
		ValidacaoUtils.validarCampoObrigatorio(memberkitConfig.getUrlApi(), "URL da API do Memberkit");
		ValidacaoUtils.validarCampoObrigatorio(memberkitConfig.getApiKey(), "API Key do Memberkit");
		log.debug("[finish] MemberkitImportProcessor - Configurações do Memberkit validadas com sucesso");
	}

	protected List<MemberkitCourseDTO> buscarCursosDoMemberkit() {
		log.info("Buscando cursos do Memberkit");
		return memberkitClientService.getCourses(memberkitConfig.getApiKey());
	}

	protected void buscarEArmazenarAulas(MemberkitCourseDTO curso, Map<String, List<MissaoOrigemDTO>> titulosDuplicados,
			UUID tipoMissao, UUID idCursoPai, UUID idJornada) {
		log.info("[start] MemberkitImportProcessor - buscarEArmazenarAulas");
		log.info("Buscando aulas para o curso: {}", curso.getName());
		List<MemberkitLessonDTO> aulas = memberkitClientService.getLessonsByCourse(curso.getId(),
				memberkitConfig.getApiKey());
		verificarSeAulasExistem(curso, aulas);
		log.info("Processando {} aulas do curso {}", aulas.size(), curso.getName());
		aulas.forEach(aula -> processarAula(aula, idCursoPai, titulosDuplicados, tipoMissao, idJornada));
		log.debug("[finish] MemberkitImportProcessor - buscarEArmazenarAulas");
	}

	private void verificarSeAulasExistem(MemberkitCourseDTO curso, List<MemberkitLessonDTO> aulas) {
		if (aulas.isEmpty()) {
			log.warn("Nenhuma aula encontrada para o curso: {}", curso.getName());
		}
	}

	protected void processarAula(MemberkitLessonDTO aula, UUID idCursoPai,
			Map<String, List<MissaoOrigemDTO>> titulosDuplicados, UUID tipoMissao, UUID idJornada) {
		log.info("[start] MemberkitImportProcessor - processarAula");
		try {
			criarMissao(new MemberkitAulaAdapter(aula), tipoMissao, idCursoPai, titulosDuplicados, idJornada);
			log.info("Aula processada com sucesso: {} (ID: {})", aula.getTitle(), aula.getId());
		} catch (Exception e) {
			log.error("[error] Falha ao processar aula {}: {}", aula.getTitle(), e.getMessage(), e);
		}
		log.debug("[finish] MemberkitImportProcessor - processarAula");
	}

	protected MissaoWakanda criarMissao(MemberkitDTOAdapter memberkitDTOAdapter, UUID tipoMissao, UUID cursoPai,
			Map<String, List<MissaoOrigemDTO>> titulosDuplicados, UUID idJornada) {

		MissaoDataDTO missaoDataDTO = memberkitDTOAdapter.toMissaoData(cursoPai, tipoMissao, idJornada);

		titulosDuplicados.computeIfAbsent(missaoDataDTO.titulo(), t -> new ArrayList<>()).add(new MissaoOrigemDTO(
				missaoDataDTO.idExterno(), missaoDataDTO.tipo(), cursoPai != null ? cursoPai.toString() : "-"));
		return salvaMissao(tipoMissao, cursoPai, missaoDataDTO.idExterno(), missaoDataDTO.titulo(),
				missaoDataDTO.descricao(), missaoDataDTO.conteudoUrl(), idJornada);

	}

	protected MissaoWakanda salvaMissao(UUID tipoMissao, UUID cursoPai, String idExterno, String titulo,
										String descricao, String conteudoUrl, UUID idJornada) {
		MissaoExternaDTO externoDto = MissaoExternaDTO.builder().titulo(titulo).descricao(descricao)
				.idTipoMissao(tipoMissao).idJornada(idJornada).idExterno(idExterno).conteudoUrl(conteudoUrl)
				.idExternoPai(Optional.ofNullable(cursoPai)).build();

		return missaoWakandaService.criaMissaoExterna(externoDto, true);

	}

}
