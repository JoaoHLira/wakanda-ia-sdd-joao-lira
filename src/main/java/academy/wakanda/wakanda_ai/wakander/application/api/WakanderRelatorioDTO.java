package academy.wakanda.wakanda_ai.wakander.application.api;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WakanderRelatorioDTO {
    private Integer wakandersConhecimento;
    private Integer wakanderEstudaram;
    private Integer wakanderEstudaramConhecimento;
    private Integer aulasAssistidasWakander;
    private Integer EstudaramUltimos30DiasConhecimento;
}
