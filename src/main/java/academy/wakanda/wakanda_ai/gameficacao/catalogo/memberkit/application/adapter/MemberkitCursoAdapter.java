package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.adapter;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MissaoDataDTO;
import academy.wakanda.wakanda_ai.utils.ValidacaoUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberkitCursoAdapter implements MemberkitDTOAdapter {

	private final MemberkitCourseDTO memberkitCourseDTO;

	@Override
	public MissaoDataDTO toMissaoData(UUID cursoPai, UUID tipoMissao, UUID idJornada) {
		String idExterno = memberkitCourseDTO.getId();
		String titulo = ValidacaoUtils.validarCampoObrigatorio(memberkitCourseDTO.getName(), "título do curso");
		String descricao = StringUtils.isNotBlank(memberkitCourseDTO.getDescription())
				? memberkitCourseDTO.getDescription().trim()
				: "Sem descrição disponível";
		return new MissaoDataDTO(idExterno, titulo, descricao, "Curso", null);
	}

}
