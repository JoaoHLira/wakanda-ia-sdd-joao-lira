package academy.wakanda.wakanda_ai.autenticacao.domain;

import academy.wakanda.wakanda_ai.autenticacao.application.api.UsuarioAdmTesteDto;
import academy.wakanda.wakanda_ai.autenticacao.application.service.TipoLogin;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "usuario_admin")
public class UsuarioAdm implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "tentativa_login", nullable = false)
    private Integer tentativaLogin = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_usuario", nullable = false, length = 20)
    private StatusUsuario statusUsuario;

    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PerfilUsuario perfil;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public UsuarioAdm(UsuarioAdmTesteDto usuarioAdmTesteDto) {
        this.nome = usuarioAdmTesteDto.getNome();
        this.username = usuarioAdmTesteDto.getUsername();
        this.senha = usuarioAdmTesteDto.getSenha();
        this.perfil = usuarioAdmTesteDto.getPerfil();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.perfil == PerfilUsuario.NAO_VERIFICADO) {
            return authorities;
        }
        if (Objects.requireNonNull(this.perfil) == PerfilUsuario.LIDERANCA) {
            authorities.add(new SimpleGrantedAuthority("ROLE_DEV"));
            authorities.add(new SimpleGrantedAuthority("ROLE_LIDERANCA"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_DEV"));
        }
        return authorities;
    }

    public static UsuarioAdm novoUsuarioNaoVerificado(String nome, String username, String senhaCodificada) {
        UsuarioAdm usuario = new UsuarioAdm();
        usuario.nome = nome;
        usuario.username = username.toLowerCase();
        usuario.statusUsuario = StatusUsuario.ATIVO;
        usuario.senha = senhaCodificada;
        usuario.perfil = PerfilUsuario.NAO_VERIFICADO;
        usuario.criadoEm = LocalDateTime.now();
        return usuario;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isEnabled() {
        return this.statusUsuario == StatusUsuario.ATIVO;
    }

    public void verificaTipoLogin(TipoLogin tipoLogin) {
        if (tipoLogin == TipoLogin.FALHOU) {
            this.tentativaLogin += 1;
        }else {
            this.tentativaLogin = 0;
        }
        verificaStatus();
    }

    private void verificaStatus() {
       if(this.tentativaLogin >= 5){
           this.statusUsuario = StatusUsuario.BLOQUEADO;
       }
    }
}
