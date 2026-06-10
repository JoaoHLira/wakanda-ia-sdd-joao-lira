package academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakanderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistoricoClasseWakanderDataHelper {

    public static HistoricoClasseWakander criarHistoricoClasseWakanderEmAndamento(UUID idHistoricoClasse, UUID idClasseAtual, UUID idXpWakander,
                                                                       UUID idProgressoWakander, UUID idWakander) {
        return new HistoricoClasseWakander(
                idHistoricoClasse,
                idClasseAtual,
                idXpWakander,
                idProgressoWakander,
                idWakander,
                HistoricoClasseWakanderStatus.EM_ANDAMENTO,
                LocalDateTime.now(),
                null
        );
    }

}
