package academy.wakanda.wakanda_ai.autenticacao.infra;

import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import academy.wakanda.wakanda_ai.autenticacao.repository.AutenticacaoRepository;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Repository
@RequiredArgsConstructor
public class AutenticacaoInfraRepository implements AutenticacaoRepository {

    private final AutenticacaoSpringDataJPARepository autenticacaoSpringDataJPARepository;

    @Override
    public Autenticacao salvaAutenticacao(Autenticacao autenticacao) {
        log.info("[start] AutenticacaoInfraRepository - salvaAutenticacao");
        autenticacaoSpringDataJPARepository.save(autenticacao);
        log.debug("[finish] AutenticacaoInfraRepository - salvaAutenticacao");
        return autenticacao;
    }

    @Override
    public Autenticacao buscaAutenticacao(String token) {
        log.info("[start] AutenticacaoInfraRepository - buscaAutenticacao");
        Autenticacao autenticacao = autenticacaoSpringDataJPARepository.findByToken(token)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Autenticacao não encontrada!"));
        log.debug("[finish] AutenticacaoInfraRepository - buscaAutenticacao");
        return autenticacao;
    }

    @Override
    @Transactional
    public void deletaTokensExpirados(LocalDateTime dataBase) {
        log.info("[start] AutenticacaoInfraRepository - deletaTokensExpirados");
        autenticacaoSpringDataJPARepository.deleteByDataExpiracaoBefore(dataBase);
        log.debug("[finish] AutenticacaoInfraRepository - deletaTokensExpirados");
    }

}
