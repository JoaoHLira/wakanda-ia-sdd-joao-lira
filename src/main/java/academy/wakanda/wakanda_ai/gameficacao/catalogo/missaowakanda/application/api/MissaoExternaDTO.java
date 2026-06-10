package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api;

import java.util.Optional;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MissaoExternaDTO {
	private String idExterno;
	private String titulo;
	private UUID idTipoMissao;
	private UUID idJornada;
	private Optional<UUID> idExternoPai;
	private String descricao;
	private String conteudoUrl;
}
