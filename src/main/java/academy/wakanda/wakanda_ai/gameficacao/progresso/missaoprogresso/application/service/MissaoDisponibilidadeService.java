package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoDisponibilidadeResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoDisponibilidadeStatus;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository.ClasseWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.application.repository.HistoricoClasseWakanderRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MissaoDisponibilidadeService {

    private final HistoricoClasseWakanderRepository historicoClasseWakanderRepository;
    private final ClasseWakandaRepository classeWakandaRepository;

    public ClasseWakanda buscaClasseAtual(UUID idWakander) {
        return historicoClasseWakanderRepository.buscaOptionalClasseAtualPorWakander(idWakander)
                .map(historico -> classeWakandaRepository.buscaClassePorId(historico.getIdClasse()))
                .orElseGet(() -> classeWakandaRepository.buscaOptionalProximaClassePorOrdem(0)
                        .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Nenhuma classe inicial encontrada no sistema!")));
    }

    public void validaMissaoLiberadaParaClasseAtual(MissaoWakanda missao, ClasseWakanda classeAtual, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        validaMissaoAtiva(missao);
        if (!missao.possuiClasseMinimaDefinida()) {
            return;
        }
        validaClasseAtualInformada(classeAtual);
        ClasseWakanda classeMinima = buscaClasseMinimaDaMissao(missao, cacheClassesMinimas);
        validaClasseMinimaEncontrada(classeMinima);
        validaClasseAtualAtendeClasseMinima(classeAtual, classeMinima);
    }

    public boolean verificaSeMissaoLiberadaParaClasseAtual(MissaoWakanda missao, ClasseWakanda classeAtual, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        if (!missao.estaAtiva()) {
            return false;
        }
        if (!missao.possuiClasseMinimaDefinida()) {
            return true;
        }

        ClasseWakanda classeMinima = buscaClasseMinimaDaMissao(missao, cacheClassesMinimas);
        return classeAtual != null
                && classeMinima != null
                && classeAtual.verificaSeIgualOuSuperior(classeMinima);
    }

    public MissaoDisponibilidadeResponse montaDisponibilidadeMissao(MissaoWakanda missao, ClasseWakanda classeAtual, boolean possuiProgresso, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        return MissaoDisponibilidadeResponse.builder()
                .idMissao(missao.getIdMissao())
                .titulo(missao.getTitulo())
                .statusDisponibilidade(determinaStatusDisponibilidade(missao, classeAtual, possuiProgresso, cacheClassesMinimas))
                .build();
    }

    private MissaoDisponibilidadeStatus determinaStatusDisponibilidade(MissaoWakanda missao, ClasseWakanda classeAtual, boolean possuiProgresso, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        if (!missao.estaAtiva()) {
            return MissaoDisponibilidadeStatus.BLOQUEADA;
        }

        return possuiProgresso || verificaSeMissaoLiberadaParaClasseAtual(missao, classeAtual, cacheClassesMinimas)
                ? MissaoDisponibilidadeStatus.LIBERADA
                : MissaoDisponibilidadeStatus.BLOQUEADA;
    }

    private ClasseWakanda buscaClasseMinimaDaMissao(MissaoWakanda missao, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        if (!missao.possuiClasseMinimaDefinida()) {
            return null;
        }

        return cacheClassesMinimas.computeIfAbsent(missao.getIdClasseMinima(),
                classeWakandaRepository::buscaClassePorId);
    }

    private void validaMissaoAtiva(MissaoWakanda missao) {
        if (!missao.estaAtiva()) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Missão não está ativa!");
        }
    }

    private void validaClasseAtualInformada(ClasseWakanda classeAtual) {
        if (classeAtual == null) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Classe atual deve ser informada para liberar a missão");
        }
    }

    private void validaClasseMinimaEncontrada(ClasseWakanda classeMinima) {
        if (classeMinima == null) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Classe mínima da missão não foi encontrada");
        }
    }

    private void validaClasseAtualAtendeClasseMinima(ClasseWakanda classeAtual, ClasseWakanda classeMinima) {
        if (!classeAtual.verificaSeIgualOuSuperior(classeMinima)) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Missão não está liberada para a classe atual");
        }
    }
}
