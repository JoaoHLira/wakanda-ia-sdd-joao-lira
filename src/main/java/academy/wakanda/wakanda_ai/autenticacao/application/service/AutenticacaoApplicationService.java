package academy.wakanda.wakanda_ai.autenticacao.application.service;

import academy.wakanda.wakanda_ai.autenticacao.application.api.AutenticacaoTokenReativado;
import academy.wakanda.wakanda_ai.autenticacao.application.api.AuthenticationResponseDto;
import academy.wakanda.wakanda_ai.autenticacao.application.api.TokenType;
import academy.wakanda.wakanda_ai.autenticacao.application.api.UsuarioAdmLoginDto;
import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import academy.wakanda.wakanda_ai.autenticacao.domain.PerfilUsuario;
import academy.wakanda.wakanda_ai.autenticacao.domain.StatusToken;
import academy.wakanda.wakanda_ai.autenticacao.domain.StatusUsuario;
import academy.wakanda.wakanda_ai.autenticacao.domain.UsuarioAdm;
import academy.wakanda.wakanda_ai.autenticacao.repository.AutenticacaoRepository;
import academy.wakanda.wakanda_ai.autenticacao.infra.UsuarioAdmSpringDataJpaRepository;
import academy.wakanda.wakanda_ai.config.security.TokenService;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class AutenticacaoApplicationService implements AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioAdmSpringDataJpaRepository usuarioAdmSpringDataJpaRepository;
    private final AutenticacaoRepository autenticacaoRepository;
    private final WakanderRepository wakanderRepository;

    @Value("${aws.url}")
    private String urlInstancia;

    @Value("${security.token.jwt.expiration}")
    private Long expiration;

    @Override
    public AuthenticationResponseDto authenticate(UsuarioAdmLoginDto usuarioAdmLoginDto) {
        log.info("[start] AuthenticationApplicationService - authenticate");
        var usernamePassword = new UsernamePasswordAuthenticationToken(usuarioAdmLoginDto.getUsername().toLowerCase(), usuarioAdmLoginDto.getSenha());
        UsuarioAdm usuario;
        try {
            var auth = this.authenticationManager.authenticate(usernamePassword);
            usuario = (UsuarioAdm) auth.getPrincipal();
        } catch (Exception e) {
            log.error("[error] AuthenticationApplicationService - authenticate - {}", e.getMessage());
            verificaTentativasLogin(TipoLogin.FALHOU, usuarioAdmLoginDto.getUsername());
            throw APIException.build(HttpStatus.FORBIDDEN, mensagemPorTipoErro(usuarioAdmLoginDto.getUsername()));
        }
        verificaPerfilUsuario(usuario);
        var token = tokenService.generateToken(usuario);
        verificaTentativasLogin(TipoLogin.SUCESSO, usuarioAdmLoginDto.getUsername());
        log.debug("[finish] AuthenticationApplicationService - authenticate");
        return new AuthenticationResponseDto(TokenType.BEARER, LocalDateTime.now().plusHours(expiration), token);
    }

    private void verificaPerfilUsuario(UsuarioAdm usuario) {
        if (usuario.getPerfil() == PerfilUsuario.NAO_VERIFICADO) {
            throw APIException.build(HttpStatus.FORBIDDEN, "Usuário ainda não liberado. Solicite liberação para a liderança.");
        }
    }

    private String mensagemPorTipoErro(String username) {
        return usuarioAdmSpringDataJpaRepository.findByUsername(username)
                .map(usuario -> {
                    if (usuario.getStatusUsuario() == StatusUsuario.ATIVO) {
                        return "Usuário ou senha inválidos. Verifique e tente novamente.";
                    }
                    return "Usuário bloqueado por excesso de tentativas. Entre em contato com o suporte.";
                })
                .orElse("Usuário não encontrado. Verifique e tente novamente.");
    }

    private void verificaTentativasLogin(TipoLogin tipoLogin, String username) {
        log.info("[start] AuthenticationApplicationService - verificaTentativasLogin");
        usuarioAdmSpringDataJpaRepository.findByUsername(username)
                .ifPresent(usuario -> {
                    usuario.verificaTipoLogin(tipoLogin);
                    usuarioAdmSpringDataJpaRepository.save(usuario);
                });
        log.debug("[finish] AuthenticationApplicationService - verificaTentativasLogin");
    }

    @Override
    public AutenticacaoTokenReativado reativaToken(String token) {
        log.info("[start] AutenticacaoApplicationService - reativaToken");
        Autenticacao autenticacao = autenticacaoRepository.buscaAutenticacao(token);
        autenticacao.reativaTokenExpirado();
        autenticacaoRepository.salvaAutenticacao(autenticacao);
        String linkFormulario = geraLinkDoFormulario(token);
        log.debug("[finish] AutenticacaoApplicationService - reativaToken");
        return new AutenticacaoTokenReativado(linkFormulario, autenticacao);
    }

    private String geraLinkDoFormulario(String token) {
        String url = UriComponentsBuilder.fromHttpUrl(urlInstancia)
                .path("/wakanda-ai/api/formulario/cadastro")
                .path("/" + token)
                .toUriString();
        log.debug("[formulario] Link do Formulário {}", url);
        return url;
    }

    @Override
    public Wakander buscaWakanderPeloToken(String token) {
        log.info("[start] AutenticacaoApplicationService - buscaWakanderPeloToken");
        Autenticacao autenticacao = autenticacaoRepository.buscaAutenticacao(token);
        verificaDataDeExpiracaoDoToken(autenticacao);
        Wakander wakander = wakanderRepository.buscaWakanderPorId(autenticacao.getIdWakander());
        log.debug("[finish] AutenticacaoApplicationService - buscaWakanderPeloToken");
        return wakander;
    }

    private void verificaDataDeExpiracaoDoToken(Autenticacao autenticacao) {
        if (autenticacao.verificaDataDeExpiracao() || autenticacao.getStatusToken().equals(StatusToken.EXPIRADO)) {
            autenticacao.mudaStatusTokenParaExpirado();
            autenticacaoRepository.salvaAutenticacao(autenticacao);
            throw APIException.build(HttpStatus.UNAUTHORIZED, "Token expirado, informe ao suporte!");
        }
    }

    @Override
    public void alteraStatusTokenParaUtilizado(String token) {
        log.info("[start] AutenticacaoApplicationService - alteraStatusTokenParaUtilizado");
        Autenticacao autenticacao = autenticacaoRepository.buscaAutenticacao(token);
        autenticacao.mudaStatusTokenParaUtilizado();
        autenticacaoRepository.salvaAutenticacao(autenticacao);
        log.debug("[finish] AutenticacaoApplicationService - alteraStatusTokenParaUtilizado");
    }
}
