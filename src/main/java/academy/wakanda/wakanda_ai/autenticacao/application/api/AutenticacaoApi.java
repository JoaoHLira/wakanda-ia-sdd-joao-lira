package academy.wakanda.wakanda_ai.autenticacao.application.api;

import academy.wakanda.wakanda_ai.docs.swagger.AuthenticationAPIDocs;
import academy.wakanda.wakanda_ai.autenticacao.application.service.AutenticacaoService;
import academy.wakanda.wakanda_ai.autenticacao.application.service.UsuarioAdmApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/autenticacao")
@Tag(name = "AuthenticationAPI", description = "Controle responsavel pela autenticação do usuario ADM.")
public class AutenticacaoApi {

    private final AutenticacaoService autenticacaoService;
    private final UsuarioAdmApplicationService usuarioAdmApplicationService;

    @AuthenticationAPIDocs.Cadastro
    @PostMapping("/cadastro")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void cadastrarUsuario(@RequestBody @Valid UsuarioAdmCadastroRequest request) {
        log.info("[start] AuthenticationAPI - cadastrarUsuario");
        usuarioAdmApplicationService.cadastrar(request);
        log.debug("[finish] AuthenticationAPI - cadastrarUsuario");
    }

    @AuthenticationAPIDocs.Login
    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.OK)
    public AuthenticationResponseDto patchRegularizaWakander(@RequestBody @Valid UsuarioAdmLoginDto usuarioAdmLoginDto) {
        log.info("[start] AuthenticationAPI - patchRegularizaWakander");
        AuthenticationResponseDto authenticationResponse = autenticacaoService.authenticate(usuarioAdmLoginDto);
        log.debug("[finish] AuthenticationAPI - patchRegularizaWakander");
        return authenticationResponse;
    }

    @AuthenticationAPIDocs.tokenTeste
    @GetMapping("/token-teste")
    @ResponseStatus(code = HttpStatus.OK)
    public Map<String, String> tokenTeste() {
        log.info("[start] AuthenticationAPI - tokenTeste");
        String message = "Token valido!";
        log.debug("[finish] AuthenticationAPI - tokenTeste");
        return Map.of("message", message);
    }

    @PatchMapping("/reativa-token/{token}")
    @ResponseStatus(code = HttpStatus.OK)
    public AutenticacaoTokenReativado reativaToken(@PathVariable String token) {
        log.info("[start] AutenticacaoApi - reativaToken");
        AutenticacaoTokenReativado tokenReativado = autenticacaoService.reativaToken(token);
        log.debug("[finish] AutenticacaoApi - reativaToken");
        return tokenReativado;
    }

}
