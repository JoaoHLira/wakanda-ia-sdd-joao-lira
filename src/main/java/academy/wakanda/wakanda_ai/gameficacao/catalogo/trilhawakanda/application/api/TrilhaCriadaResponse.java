package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TrilhaCriadaResponse {
    private UUID idTrilha;

    public TrilhaCriadaResponse(TrilhaWakanda trilhaCriada) {
        this.idTrilha = trilhaCriada.getIdTrilha();
    }
}
