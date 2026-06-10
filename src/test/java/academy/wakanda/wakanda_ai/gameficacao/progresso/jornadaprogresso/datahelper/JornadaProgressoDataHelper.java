package academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgressoStatus;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JornadaProgressoDataHelper {

    public static JornadaProgresso criarJornadaProgressoEmAndamento(UUID idJornadaWakanda, UUID idProgressoWakander) {
        return new JornadaProgresso(
                UUID.randomUUID(),
                idJornadaWakanda,
                idProgressoWakander,
                JornadaProgressoStatus.EM_ANDAMENTO,
                150,
                LocalDateTime.of(2020, 1, 1, 0, 0),
                null
        );
    }

    public static List<JornadaProgresso> criarListaJornada( UUID idProgressoWakander) {
        return List.of(
                criarJornadaProgressoEmAndamento(UUID.randomUUID(), idProgressoWakander),
                criarJornadaProgressoEmAndamento(UUID.randomUUID(), idProgressoWakander),
                criarJornadaProgressoEmAndamento(UUID.randomUUID(), idProgressoWakander)
        );
    }

    public static ProgressoWakanderEventDto criarProgressoWakanderEventDto(JornadaProgresso jornadaProgresso) {
        return ProgressoWakanderEventDto.onJornadaProgresso(jornadaProgresso);
    }
}
