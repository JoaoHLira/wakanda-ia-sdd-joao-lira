package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service;

import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoConcluidaProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MissaoProgressoRepository {
    MissaoProgresso buscaMissaoProgresso(UUID idMissao, UUID idProgressoWakander);
    Optional<MissaoProgresso> buscaOptionalMissaoProgresso(UUID idMissao, UUID idProgressoWakander);
    MissaoProgresso salvaProgressoMissao(MissaoProgresso missaoProgresso);
    boolean existeMissaoProgresso(UUID idMissao, UUID idProgressoWakander);
    List<MissaoProgresso> buscaMissoesProgressoPorIdsMissoes(List<UUID> idsMissoes);
    List<MissaoProgresso> buscaMissoesProgressoPorIdsMissoesEProgresso(List<UUID> idsMissoes, UUID idProgressoWakander);
    MissaoProgresso buscaMissaoProgressoPorId(UUID idMissaoProgresso);
    Page<MissaoConcluidaProjection> listaMissoesConcluidasPorProgresso(UUID idProgressoWakander, Pageable pageable);
}
