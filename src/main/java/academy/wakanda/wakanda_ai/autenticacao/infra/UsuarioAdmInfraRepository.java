package academy.wakanda.wakanda_ai.autenticacao.infra;

import academy.wakanda.wakanda_ai.autenticacao.domain.UsuarioAdm;
import academy.wakanda.wakanda_ai.autenticacao.repository.UsuarioAdmRepository;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Log4j2
public class UsuarioAdmInfraRepository implements UsuarioAdmRepository {

    private final UsuarioAdmSpringDataJpaRepository usuarioAdmSpringDataJpaRepository;

    @Override
    public UsuarioAdm save(UsuarioAdm usuarioAdm) {
        log.info("[start] UsuarioAdmInfraRepository - save");
        try {
            usuarioAdmSpringDataJpaRepository.save(usuarioAdm);
        } catch (DataIntegrityViolationException ex) {
            throw APIException.build(HttpStatus.CONFLICT, "Já existe um usuário cadastrado com este nome de usuário.");
        }
        log.debug("[finish] UsuarioAdmInfraRepository - save");
        return usuarioAdm;
    }
}