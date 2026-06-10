package academy.wakanda.wakanda_ai.autenticacao.application.service;

import academy.wakanda.wakanda_ai.autenticacao.domain.PerfilUsuario;
import academy.wakanda.wakanda_ai.autenticacao.domain.UsuarioAdm;
import academy.wakanda.wakanda_ai.autenticacao.infra.UsuarioAdmInfraRepository;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioAdmApplicationServiceTest {

    @InjectMocks
    private UsuarioAdmApplicationService usuarioAdmApplicationService;

    @Mock
    private UsuarioAdmInfraRepository usuarioAdmInfraRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve cadastrar usuário com perfil NAO_VERIFICADO e senha codificada")
    void deveCadastrarUsuarioNaoVerificado() {
        when(passwordEncoder.encode("123456")).thenReturn("hash-bcrypt");

        var request = DataHelper.criaUsuarioAdmCadastroRequest();

        usuarioAdmApplicationService.cadastrar(request);

        ArgumentCaptor<UsuarioAdm> captor = ArgumentCaptor.forClass(UsuarioAdm.class);
        verify(usuarioAdmInfraRepository).save(captor.capture());
        UsuarioAdm salvo = captor.getValue();
        assertEquals("Nome Teste", salvo.getNome());
        assertEquals("email@email.com", salvo.getUsername());
        assertEquals("hash-bcrypt", salvo.getSenha());
        assertEquals(PerfilUsuario.NAO_VERIFICADO, salvo.getPerfil());
        verify(passwordEncoder).encode("123456");
    }

    @Test
    @DisplayName("Deve lançar conflito quando username já existir")
    void deveLancarConflitoQuandoUsernameExiste() {
        var request = DataHelper.criaUsuarioAdmCadastroRequest();
        when(passwordEncoder.encode("123456")).thenReturn("hash-bcrypt");
        doThrow(APIException.build(HttpStatus.CONFLICT, "Já existe um usuário cadastrado com este nome de usuário."))
                .when(usuarioAdmInfraRepository).save(any(UsuarioAdm.class));

        APIException ex = assertThrows(APIException.class, () -> usuarioAdmApplicationService.cadastrar(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        verify(usuarioAdmInfraRepository, times(1)).save(any(UsuarioAdm.class));
        verify(passwordEncoder, times(1)).encode("123456");
    }
}
