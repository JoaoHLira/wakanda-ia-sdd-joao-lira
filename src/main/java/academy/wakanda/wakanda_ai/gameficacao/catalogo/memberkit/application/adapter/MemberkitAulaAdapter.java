package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.adapter;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MissaoDataDTO;
import academy.wakanda.wakanda_ai.utils.ValidacaoUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberkitAulaAdapter implements MemberkitDTOAdapter {

	private static final String YOUTUBE_WATCH_URL = "https://www.youtube.com/watch?v=";
	private static final String YOUTUBE_EMBED_URL_REGEX = "https://www\\.youtube\\.com/embed/([a-zA-Z0-9_-]+)";

	private final MemberkitLessonDTO memberkitLessonDTO;

	@Override
	public MissaoDataDTO toMissaoData(UUID cursoPai, UUID tipoMissao, UUID idJornada) {
		return new MissaoDataDTO(idExterno(), titulo(), descricao(), "Aula", extrairVideoUrl());
	}

	private String idExterno() {
		return memberkitLessonDTO.getId();
	}

	private String titulo() {
		return ValidacaoUtils.validarCampoObrigatorio(memberkitLessonDTO.getTitle(), "título da aula");
	}

	private String descricao() {
		return StringUtils.isNotBlank(memberkitLessonDTO.getContent())
				? memberkitLessonDTO.getContent().trim()
				: "Sem descrição disponível";
	}

	private String extrairVideoUrl() {
		String uid = extrairUidDoVideo();
		if (StringUtils.isNotBlank(uid)) return YOUTUBE_WATCH_URL + uid.trim();

		String videoId = extrairVideoIdDoIframe(memberkitLessonDTO.getContent());
		if (StringUtils.isNotBlank(videoId)) return YOUTUBE_WATCH_URL + videoId;

		return null;
	}

	private String extrairUidDoVideo() {
		if (memberkitLessonDTO.getVideo() == null) return null;
		return memberkitLessonDTO.getVideo().getUid();
	}

	private String extrairVideoIdDoIframe(String content) {
		if (content == null) return null;
		java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(YOUTUBE_EMBED_URL_REGEX).matcher(content);
		return matcher.find() ? matcher.group(1) : null;
	}

}
