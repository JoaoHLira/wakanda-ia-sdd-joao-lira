package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class TrilhaListResponse {
    private UUID idTrilha;
    private String nome;
    private String descricao;
    private Integer xpTotal;

    public static List<TrilhaListResponse> converte(List<TrilhaWakanda> trilhas) {
        return trilhas.stream()
                .map(TrilhaListResponse::new)
                .collect(Collectors.toList());
    }
    public TrilhaListResponse(TrilhaWakanda trilhaWakanda) {
        this.idTrilha = trilhaWakanda.getIdTrilha();
        this.nome = trilhaWakanda.getNome();
        this.descricao = trilhaWakanda.getDescricao();
        this.xpTotal = trilhaWakanda.getXpTotal();
    }
}
