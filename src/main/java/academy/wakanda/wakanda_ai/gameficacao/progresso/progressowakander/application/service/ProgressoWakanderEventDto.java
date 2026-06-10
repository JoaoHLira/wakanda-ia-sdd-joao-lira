package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProgressoWakanderEventDto {
    private ProgressoWakanderEventType typeProgressoWakander;
    private UUID idWakander;
    private String idMissaoExterna;
    private UUID idJornadaProgresso;
    private LocalDate dataEnvio;

    public static ProgressoWakanderEventDto onJornadaProgresso(JornadaProgresso jornadaProgresso) {
        return ProgressoWakanderEventDto.builder()
                .typeProgressoWakander(ProgressoWakanderEventType.JORNADA_PROGRESSO)
                .idJornadaProgresso(jornadaProgresso.getIdJornadaProgresso())
                .dataEnvio(LocalDate.now())
                .build();
    }

    public static ProgressoWakanderEventDto onMissaoProgresso(UUID idWakander, String idMissaoExterna) {
        return ProgressoWakanderEventDto.builder()
                .typeProgressoWakander(ProgressoWakanderEventType.MISSAO_PROGRESSO)
                .idWakander(idWakander)
                .idMissaoExterna(idMissaoExterna)
                .dataEnvio(LocalDate.now())
                .build();
    }
}
