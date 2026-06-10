package academy.wakanda.wakanda_ai.autenticacao.datahelper;

import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import academy.wakanda.wakanda_ai.autenticacao.domain.StatusToken;

import java.time.LocalDateTime;
import java.util.UUID;

public class AutenticacaoDataHelper {

    public static Autenticacao criaAutenticacao() {
        return new Autenticacao("G0lLSGkv6cqzgJ8VUrevaQ",
                UUID.fromString("14bdf572-ff78-469a-aa02-e5c068f6b197"), 5);
    }

    public static Autenticacao criaAutenticacaoExpirada() {
        return new Autenticacao("G0lLSGkv6cqzgJ8VUrevaQ",
                UUID.fromString("14bdf572-ff78-469a-aa02-e5c068f6b197"),
                LocalDateTime.now().minusMinutes(2880), StatusToken.EXPIRADO);
    }
    public static Autenticacao criaAutenticacaoComDataExpirada() {
        return new Autenticacao("G0lLSGkv6cqzgJ8VUrevaQ",
                UUID.fromString("14bdf572-ff78-469a-aa02-e5c068f6b197"),
                LocalDateTime.now().minusMinutes(2880), StatusToken.VALIDO);
    }
}
