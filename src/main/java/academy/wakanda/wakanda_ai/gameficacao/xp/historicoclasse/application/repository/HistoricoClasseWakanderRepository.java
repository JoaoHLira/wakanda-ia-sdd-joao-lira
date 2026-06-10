package academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.application.repository;

import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;

import java.util.Optional;
import java.util.UUID;

public interface HistoricoClasseWakanderRepository {

    void salvaHistoricoClasseWakander(HistoricoClasseWakander historicoClasse);
    HistoricoClasseWakander buscaHistoricoClasseAtual(XpPromocaoClasseDTO xpPromocaoClasseDTO);
    Optional<HistoricoClasseWakander> buscaOptionalHistoricoClasseAtual(XpPromocaoClasseDTO xpPromocaoClasseDTO);
    HistoricoClasseWakander buscaClasseAtualPorWakander(UUID idWakander);
    Optional<HistoricoClasseWakander> buscaOptionalClasseAtualPorWakander(UUID idWakander);

}
