package academy.wakanda.wakanda_ai.autenticacao.application.service;

import academy.wakanda.wakanda_ai.autenticacao.application.api.AutenticacaoTokenReativado;
import academy.wakanda.wakanda_ai.autenticacao.application.api.AuthenticationResponseDto;
import academy.wakanda.wakanda_ai.autenticacao.application.api.UsuarioAdmLoginDto;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;

public interface AutenticacaoService {
    AuthenticationResponseDto authenticate(UsuarioAdmLoginDto usuarioAdmLoginDto);
    AutenticacaoTokenReativado reativaToken(String token);
    Wakander buscaWakanderPeloToken(String token);
    void alteraStatusTokenParaUtilizado(String token);
}
