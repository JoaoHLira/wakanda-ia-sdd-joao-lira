package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain;

import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Embeddable
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrdemMissao {

    @Column(name = "ordem_missao", nullable = false)
    private int ordem;

    public static OrdemMissao criar(int ordem) {
        if (ordem < 0) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "A ordem da missão não pode ser negativa");
        }
        return new OrdemMissao(ordem);
    }

    public static void validarNovaPosicao(int novaPosicao, int totalMissoes, int posicaoAtual, MissaoStatus missaoStatus) {
        validarPosicaoLimite(novaPosicao, totalMissoes);
        validarStatusMissao(missaoStatus);
        validarPosicaoAtual(novaPosicao, posicaoAtual);
    }

    private static void validarPosicaoLimite(int novaPosicao, int totalMissoes) {
        if (novaPosicao >= totalMissoes) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "A nova posição deve ser entre 0 e " + (totalMissoes - 1));
        }
    }

    private static void validarStatusMissao(MissaoStatus missaoStatus) {
        if (MissaoStatus.INATIVA.equals(missaoStatus)) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Não é possível alterar a posição de uma missão inativa.");
        }
    }

    private static void validarPosicaoAtual(int novaPosicao, int posicaoAtual) {
        if (novaPosicao == posicaoAtual) {
            throw APIException.build(HttpStatus.CONFLICT, "A missão já está nesta posição");
        }
    }

    public static void ajustarPosicoesIntermediarias(List<MissaoWakanda> missoesList, int posicaoAtual, int novaPosicao) {
        if (novaPosicao < posicaoAtual) {
            ajustarPosicoesParaCima(missoesList, novaPosicao, posicaoAtual);
        } else {
            ajustarPosicoesParaBaixo(missoesList, posicaoAtual, novaPosicao);
        }
    }

    private static void ajustarPosicoesParaCima(List<MissaoWakanda> missoesList, int novaPosicao, int posicaoAtual) {
        missoesList.stream().filter(missao -> missao.getOrdemMissao().getOrdem() >= novaPosicao && missao.getOrdemMissao().getOrdem() < posicaoAtual).forEach(missao -> missao.alterarOrdem(OrdemMissao.criar(missao.getOrdemMissao().getOrdem() + 1)));
    }

    private static void ajustarPosicoesParaBaixo(List<MissaoWakanda> missoesList, int posicaoAtual, int novaPosicao) {
        missoesList.stream().filter(missao -> missao.getOrdemMissao().getOrdem() <= novaPosicao && missao.getOrdemMissao().getOrdem() > posicaoAtual).forEach(missao -> missao.alterarOrdem(OrdemMissao.criar(missao.getOrdemMissao().getOrdem() - 1)));
    }

    public static List<MissaoWakanda> reorganizarMissoesComInativasNoFinal(List<MissaoWakanda> todasMissoes) {
        List<MissaoWakanda> missoesAtivas = separarMissoesAtivas(todasMissoes);
        List<MissaoWakanda> missoesInativas = separarMissoesInativas(todasMissoes);
        List<MissaoWakanda> missoesReordenadas = new ArrayList<>();
        missoesReordenadas.addAll(missoesAtivas);
        missoesReordenadas.addAll(missoesInativas);
        for (int i = 0; i < missoesReordenadas.size(); i++) {
            missoesReordenadas.get(i).alterarOrdem(OrdemMissao.criar(i));
        }
        return missoesReordenadas;
    }

    private static List<MissaoWakanda> separarMissoesAtivas(List<MissaoWakanda> todasMissoes) {
        return todasMissoes.stream().filter(missao -> MissaoStatus.ATIVA.equals(missao.getMissaoStatus())).collect(Collectors.toList());
    }

    private static List<MissaoWakanda> separarMissoesInativas(List<MissaoWakanda> todasMissoes) {
        return todasMissoes.stream().filter(missao -> !MissaoStatus.ATIVA.equals(missao.getMissaoStatus())).collect(Collectors.toList());
    }
}
