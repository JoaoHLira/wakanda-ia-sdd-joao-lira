package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.infra.MissaoWakandaProjection;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidadorMissoesClasse {
    private final MissaoWakandaRepository missaoWakandaRepository;

    public void valida(List<UUID> idsMissoes) {
        if (idsMissoes != null && !idsMissoes.isEmpty()) {
            List<MissaoWakandaProjection> missoesEncontradas = missaoWakandaRepository.buscaMissoesPorIds(idsMissoes);
            List<UUID> idsNaoEncontrados = calculaIdsNaoEncontrados(idsMissoes, missoesEncontradas);
            List<UUID> idsInativos = calculaIdsInativos(missoesEncontradas);
            verificaErros(idsNaoEncontrados, idsInativos);
        }
    }

    private List<UUID> calculaIdsNaoEncontrados(List<UUID> idsSolicitados,
            List<MissaoWakandaProjection> missoesEncontradas) {
        Set<UUID> idsEncontrados = missoesEncontradas.stream().map(MissaoWakandaProjection::getIdMissao)
                .collect(Collectors.toSet());
        return idsSolicitados.stream().filter(id -> !idsEncontrados.contains(id)).toList();
    }

    private List<UUID> calculaIdsInativos(List<MissaoWakandaProjection> missoesEncontradas) {
        return missoesEncontradas.stream().filter(m -> !MissaoStatus.ATIVA.equals(m.getMissaoStatus()))
                .map(MissaoWakandaProjection::getIdMissao).toList();
    }

    private void verificaErros(List<UUID> idsNaoEncontrados, List<UUID> idsInativos) {
        if (!idsNaoEncontrados.isEmpty() || !idsInativos.isEmpty()) {
            String mensagemErro = String.format("Erro na validação das missões. Não encontradas: %s. Inativas: %s.",
                    idsNaoEncontrados, idsInativos);
            throw APIException.build(HttpStatus.BAD_REQUEST, mensagemErro);
        }
    }

}
