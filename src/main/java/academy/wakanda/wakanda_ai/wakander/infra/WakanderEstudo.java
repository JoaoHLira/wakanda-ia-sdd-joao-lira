package academy.wakanda.wakanda_ai.wakander.infra;

import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class WakanderEstudo {
    private String idWakander;
    private String nome;
    private String whatsapp;

    public WakanderEstudo(Wakander wakander){
        this.idWakander = wakander.getIdWakander().toString();
        this.nome = wakander.getNome();
        this.whatsapp = wakander.getContato().getWhatsapp();
    }

    public static List<WakanderEstudo> converteParaResponse(List<Wakander> wakanders) {
        return wakanders.stream()
                .map(WakanderEstudo::new)
                .collect(Collectors.toList());
    }
}