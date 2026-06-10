package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitLessonWebhookDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoExternaDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.OrdemMissao;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.ProcessamentoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class MemberkitDataHelper {

	private MemberkitDataHelper() {
	}

	public static MemberkitCourseDTO criarCurso(String id, String nome, String descricao) {
		return MemberkitCourseDTO.builder().id(id).name(nome).description(descricao).build();
	}

	public static MemberkitCourseDTO criarCursoPadrao() {
		return criarCurso("course-" + UUID.randomUUID(), "Curso Padrão", "Descrição do curso padrão");
	}

	public static MemberkitCourseDTO criarCursoSemDescricao(String id, String nome) {
		return criarCurso(id, nome, null);
	}

	public static MemberkitLessonDTO criarAula(String id, String titulo, String conteudo) {
		return MemberkitLessonDTO.builder().id(id).title(titulo).content(conteudo).build();
	}

	public static MemberkitLessonDTO criarAulaPadrao() {
		return criarAula("lesson-" + UUID.randomUUID(), "Aula Padrão", "Conteúdo padrão");
	}

	public static List<MemberkitLessonDTO> criarDuasAulas(String idCurso) {
		MemberkitLessonDTO a1 = criarAula("lesson-1-" + idCurso, "Aula 1", "Conteúdo 1");
		MemberkitLessonDTO a2 = criarAula("lesson-2-" + idCurso, "Aula 2", "Conteúdo 2");
		return List.of(a1, a2);
	}

	public static List<MemberkitLessonDTO> criarListaAulasVazia() {
		return List.of();
	}

	public static CursoComAulas criarCursoComDuasAulas(String idCurso, String nome, String descricao) {
		MemberkitCourseDTO curso = criarCurso(idCurso, nome, descricao);
		List<MemberkitLessonDTO> aulas = criarDuasAulas(idCurso);
		return new CursoComAulas(curso, aulas);
	}

	public static CursoComAulas criarCursoSemAulas(String idCurso, String nome, String descricao) {
		MemberkitCourseDTO curso = criarCurso(idCurso, nome, descricao);
		return new CursoComAulas(curso, List.of());
	}

	public record CursoComAulas(MemberkitCourseDTO curso, List<MemberkitLessonDTO> aulas) {
	}

	public static MemberkitLessonWebhookDTO criaCurso(String id, String titulo, String descricao) {
		MemberkitLessonWebhookDTO.Course course = MemberkitLessonWebhookDTO.Course.builder().id(id).name(titulo)
				.description(descricao).build();

		MemberkitLessonWebhookDTO.LessonData data = MemberkitLessonWebhookDTO.LessonData.builder().course(course)
				.build();

		return MemberkitLessonWebhookDTO.builder().type("course.created").data(data).build();
	}

	public static MemberkitLessonWebhookDTO criaAula(String idAula, String tituloAula, String idCurso, String nomeCurso,
			String descricao) {
		MemberkitLessonWebhookDTO.Course course = MemberkitLessonWebhookDTO.Course.builder().id(idCurso).name(nomeCurso)
				.description(descricao).build();

		MemberkitLessonWebhookDTO.LessonData data = MemberkitLessonWebhookDTO.LessonData.builder().id(idAula)
				.title(tituloAula).course(course).build();

		return MemberkitLessonWebhookDTO.builder().type("lesson.created").data(data).build();
	}

	public static TipoMissao criarTipoMissaoComId(UUID id) {
		return new TipoMissao(id, "Descrição teste");
	}

	public static JornadaWakanda criarJornadaWakandaComId(UUID id) {
		return new JornadaWakanda(id, "Título teste", "Descrição teste", 0, UUID.randomUUID(), StatusJornada.ATIVA, 0,
				1);
	}

	public static MissaoWakanda criarMissaoWakandaComId(UUID id) {
		return new MissaoWakanda(id, "Título teste", "Descrição teste", 0, UUID.randomUUID(), UUID.randomUUID(),
				MissaoStatus.ATIVA, new OrdemMissao(1), new Sabedorias(0, 0, 0, 0, 0), "123", UUID.randomUUID(), UUID.randomUUID(),
				"http://wakanda.com.br", ProcessamentoStatus.EM_PROCESSO);
	}

	public static MissaoExternaDTO criarMissaoExterna(String idExterno, String titulo, UUID idTipoMissao,
			UUID idJornada, UUID idExternoPai, String descricao) {
		return MissaoExternaDTO.builder().idExterno(idExterno).titulo(titulo).idTipoMissao(idTipoMissao)
				.idJornada(idJornada).idExternoPai(Optional.ofNullable(idExternoPai)).descricao(descricao).build();
	}

}
