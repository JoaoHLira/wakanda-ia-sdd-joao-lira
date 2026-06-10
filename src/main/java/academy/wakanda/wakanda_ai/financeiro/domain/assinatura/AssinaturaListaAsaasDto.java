package academy.wakanda.wakanda_ai.financeiro.domain.assinatura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssinaturaListaAsaasDto {
    private List<AssinaturaAsaasDto> data;
    private int limit;
    private int offset;
    private boolean hasMore;
}

