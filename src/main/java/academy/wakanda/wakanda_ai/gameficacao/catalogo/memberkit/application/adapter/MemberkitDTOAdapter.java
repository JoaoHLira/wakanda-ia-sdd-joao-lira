package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.adapter;

import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MissaoDataDTO;

public interface MemberkitDTOAdapter {

	MissaoDataDTO toMissaoData(UUID cursoPai, UUID tipoMissao, UUID idJornada);

}
