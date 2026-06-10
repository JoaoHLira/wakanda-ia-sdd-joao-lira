package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissaoAlteracaoXpBaseRequest {

    @NotNull(message = "O XPBase não pode ser nulo")
    @Min(value = 1, message = "O XPBase deve ser maior que zero")
    private Integer xpBase;
}
