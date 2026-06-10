package academy.wakanda.wakanda_ai.autenticacao.application.api;

import academy.wakanda.wakanda_ai.autenticacao.domain.PerfilUsuario;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UsuarioAdmTesteDto {
    private UUID id;
    private String username;
    private String senha;
    private PerfilUsuario perfil;
    private String nome;
}
