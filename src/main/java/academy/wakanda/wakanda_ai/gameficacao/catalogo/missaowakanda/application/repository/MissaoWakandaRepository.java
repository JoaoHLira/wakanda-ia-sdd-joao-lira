package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.infra.MissaoWakandaProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MissaoWakandaRepository {
    MissaoWakanda salvaMissao(MissaoWakanda novaMissao);

    MissaoWakanda buscaMissaoPorId(UUID idMissao);

    boolean validaTitulo(String titulo);

    List<MissaoWakanda> buscaMissoesPorIdJornada(UUID jornada);

    MissaoWakanda buscaMissaoPorIdMissaoExterna(String IdMissaoExterna);

    List<MissaoWakanda> buscaMissoesOrdenadasPorJornada(UUID idJornada);

    Optional<MissaoWakanda> buscaProximaMissao(UUID idJornada, int ordemProximaMissao);

    List<MissaoWakanda> buscaMissoesAtivasPorIdJornada(UUID idJornada, MissaoStatus missaoStatus);

    Integer contarMissoesAtivasPorJornada(UUID idJornada);

    List<MissaoWakanda> buscarMissoesAtivasOrdenadas(UUID idJornada);

    List<MissaoWakanda> buscarTodasMissoesOrdenadas(UUID idJornada);

    Optional<MissaoWakanda> buscaMissaoPai(UUID idMissaoPai);

    Page<MissaoWakanda> buscaMissoesPaginadas(Pageable pageable, UUID idJornada, MissaoStatus missaoStatus);

    Optional<MissaoWakanda> buscaIdMissaoWakandaExterno(String idMissao);

    List<MissaoWakandaProjection> buscaMissoesPorIds(List<UUID> idsMissoes);

    List<MissaoWakanda> salvaMissoes(List<MissaoWakanda> missoes);

}
