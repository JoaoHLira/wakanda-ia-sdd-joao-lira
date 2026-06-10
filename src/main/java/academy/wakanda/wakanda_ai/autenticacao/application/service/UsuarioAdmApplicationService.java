package academy.wakanda.wakanda_ai.autenticacao.application.service;

import academy.wakanda.wakanda_ai.autenticacao.application.api.UsuarioAdmCadastroRequest;
import academy.wakanda.wakanda_ai.autenticacao.domain.UsuarioAdm;
import academy.wakanda.wakanda_ai.autenticacao.infra.AutenticacaoInfraRepository;
import academy.wakanda.wakanda_ai.autenticacao.infra.UsuarioAdmInfraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UsuarioAdmApplicationService {

    private final UsuarioAdmInfraRepository usuarioAdmInfraRepository;
    private final PasswordEncoder passwordEncoder;

    public void cadastrar(UsuarioAdmCadastroRequest request) {
        log.info("[start] CadastroUsuarioAdmApplicationService - cadastrar");
        String usernameNormalizado = request.getUsername().trim();
        String senhaCodificada = passwordEncoder.encode(request.getSenha());
        UsuarioAdm novoUsuario = UsuarioAdm.novoUsuarioNaoVerificado(
                request.getNome().trim(), usernameNormalizado, senhaCodificada);
        usuarioAdmInfraRepository.save(novoUsuario);
        log.debug("[finish] CadastroUsuarioAdmApplicationService - cadastrar");
    }
}
