package academy.wakanda.wakanda_ai.wakander.application.api;


import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "Estatisticas de Wakanders")
public class EstatisticaWakanderDTO {

    private Integer totalWakanders;
    private Long totalWakandersCadastroCompleto;
    private Long totalWakandersCadastroIncompleto;

    public EstatisticaWakanderDTO(List<Wakander> wakanders) {
        this.totalWakanders = wakanders.size();
        this.totalWakandersCadastroCompleto = wakanders.stream().filter(w -> w.getStatusCadastro() == StatusCadastro.COMPLETO).count();
        this.totalWakandersCadastroIncompleto = wakanders.stream().filter(w -> w.getStatusCadastro() == StatusCadastro.INCOMPLETO).count();
    }

}
