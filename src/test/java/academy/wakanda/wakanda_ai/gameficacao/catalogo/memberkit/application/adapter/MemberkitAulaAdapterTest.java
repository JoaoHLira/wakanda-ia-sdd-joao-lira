package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.adapter;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MissaoDataDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MemberkitAulaAdapterTest {

	@Test
	@DisplayName("Deve associar link do video da aula ao converter para missao")
	void deveAssociarLinkDoVideoDaAulaAoConverterParaMissao() {
		MemberkitLessonDTO aula = MemberkitLessonDTO.builder()
				.id("lesson-1")
				.title("Aula com video")
				.content("Conteudo da aula")
				.video(MemberkitLessonDTO.Video.builder().uid(" video-123 ").build())
				.build();

		MissaoDataDTO missaoDataDTO = new MemberkitAulaAdapter(aula).toMissaoData(null, null, null);

		assertEquals("https://www.youtube.com/watch?v=video-123", missaoDataDTO.conteudoUrl());
	}

	@Test
	@DisplayName("Deve usar iframe quando uid do video estiver em branco")
	void deveUsarIframeQuandoUidDoVideoEstiverEmBranco() {
		MemberkitLessonDTO aula = MemberkitLessonDTO.builder()
				.id("lesson-2")
				.title("Aula com iframe")
				.content("<iframe src=\"https://www.youtube.com/embed/iframe-456\"></iframe>")
				.video(MemberkitLessonDTO.Video.builder().uid("   ").build())
				.build();

		MissaoDataDTO missaoDataDTO = new MemberkitAulaAdapter(aula).toMissaoData(null, null, null);

		assertEquals("https://www.youtube.com/watch?v=iframe-456", missaoDataDTO.conteudoUrl());
	}

	@Test
	@DisplayName("Deve retornar missao sem link quando aula nao possuir video")
	void deveRetornarMissaoSemLinkQuandoAulaNaoPossuirVideo() {
		MemberkitLessonDTO aula = MemberkitLessonDTO.builder()
				.id("lesson-3")
				.title("Aula sem video")
				.content("Conteudo sem video")
				.build();

		MissaoDataDTO missaoDataDTO = new MemberkitAulaAdapter(aula).toMissaoData(null, null, null);

		assertNull(missaoDataDTO.conteudoUrl());
	}
}
