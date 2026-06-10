package academy.wakanda.wakanda_ai.autenticacao.repository;

import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;

import java.time.LocalDateTime;

public interface AutenticacaoRepository {
    Autenticacao salvaAutenticacao(Autenticacao autenticacao);
    Autenticacao buscaAutenticacao(String token);
    void deletaTokensExpirados(LocalDateTime dataBase);
}
