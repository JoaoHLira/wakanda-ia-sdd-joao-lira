package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "tipo_missao")
public class TipoMissao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_tipo_missao")
    private UUID idTipoMissao;
    @Column(name = "descricao", unique = true)
    private String descricao;
    

    public TipoMissao(TipoMissaoRequest tipoMissaoRequest) {
        this.descricao = tipoMissaoRequest.getDescricao();
    }
}
