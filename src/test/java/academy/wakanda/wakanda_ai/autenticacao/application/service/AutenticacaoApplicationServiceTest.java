package academy.wakanda.wakanda_ai.autenticacao.application.service;

import academy.wakanda.wakanda_ai.autenticacao.application.api.AutenticacaoTokenReativado;
import academy.wakanda.wakanda_ai.autenticacao.application.api.TokenType;
import academy.wakanda.wakanda_ai.autenticacao.application.api.UsuarioAdmLoginDto;
import academy.wakanda.wakanda_ai.autenticacao.application.api.UsuarioAdmTesteDto;
import academy.wakanda.wakanda_ai.autenticacao.datahelper.AutenticacaoDataHelper;
import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import academy.wakanda.wakanda_ai.autenticacao.domain.PerfilUsuario;
import academy.wakanda.wakanda_ai.autenticacao.domain.StatusToken;
import academy.wakanda.wakanda_ai.autenticacao.domain.UsuarioAdm;
import academy.wakanda.wakanda_ai.autenticacao.repository.AutenticacaoRepository;
import academy.wakanda.wakanda_ai.autenticacao.infra.UsuarioAdmSpringDataJpaRepository;
import academy.wakanda.wakanda_ai.config.security.TokenService;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AutenticacaoApplicationServiceTest {

    @InjectMocks
    AutenticacaoApplicationService autenticacaoApplicationService;

    @InjectMocks
    private AuthozirationApplicationService authozirationApplicationService;

    @Mock
    AutenticacaoRepository autenticacaoRepository;

    @Mock
    WakanderRepository wakanderRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioAdmSpringDataJpaRepository usuarioAdmSpringDataJpaRepository;


    private final String urlInstancia = "http://localhost:8080";

    private final Long expiration = 1L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(autenticacaoApplicationService, "urlInstancia", urlInstancia);
        loadTemplates(Templates.BASE_PACKAGE);
        ReflectionTestUtils.setField(autenticacaoApplicationService, "expiration", expiration);
    }

    @Test
    @DisplayName("Deve reativar o token da sessao do formulario")
    void deveReativarTokenDaSessaoDoFormulario() {
        Autenticacao autenticacao = AutenticacaoDataHelper.criaAutenticacaoExpirada();
        when(autenticacaoRepository.buscaAutenticacao(autenticacao.getToken())).thenReturn(autenticacao);
        when(autenticacaoRepository.salvaAutenticacao(any())).thenReturn(autenticacao);

        AutenticacaoTokenReativado tokenReativado = autenticacaoApplicationService.reativaToken(autenticacao.getToken());

        assertEquals("http://localhost:8080/wakanda-ai/api/formulario/cadastro/G0lLSGkv6cqzgJ8VUrevaQ",
                tokenReativado.getLinkFormulario());
        assertEquals(StatusToken.VALIDO, autenticacao.getStatusToken());
        verify(autenticacaoRepository, times(1)).buscaAutenticacao(autenticacao.getToken());
        verify(autenticacaoRepository, times(1)).salvaAutenticacao(autenticacao);
    }

    @Test
    @DisplayName("Não deve reativar o token quando ainda estiver válido")
    void naoDeveReativarTokenQuandoAindaEstiverValido() {
        Autenticacao autenticacao = AutenticacaoDataHelper.criaAutenticacao();
        when(autenticacaoRepository.buscaAutenticacao(autenticacao.getToken())).thenReturn(autenticacao);

        APIException exception = assertThrows(APIException.class,
                () -> autenticacaoApplicationService.reativaToken(autenticacao.getToken()));

        assertEquals(StatusToken.VALIDO, autenticacao.getStatusToken());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
        assertEquals("O token não esta expirado! Ele expirará em " + autenticacao.getDataExpiracao(),
                exception.getMessage());

        verify(autenticacaoRepository, times(1)).buscaAutenticacao(autenticacao.getToken());
        verify(autenticacaoRepository, never()).salvaAutenticacao(autenticacao);
    }


    @Test
    @DisplayName("Deve buscar Wakander quando token for válido")
    void deveBuscarWakanderQuandoTokenValido() {
        Autenticacao autenticacao = AutenticacaoDataHelper.criaAutenticacao();
        Wakander wakander = DataHelper.criaWakander();
        when(autenticacaoRepository.buscaAutenticacao(autenticacao.getToken())).thenReturn(autenticacao);
        when(wakanderRepository.buscaWakanderPorId(any())).thenReturn(wakander);

        autenticacaoApplicationService.buscaWakanderPeloToken(autenticacao.getToken());

        verify(autenticacaoRepository, times(1)).buscaAutenticacao(autenticacao.getToken());
        verify(wakanderRepository, times(1)).buscaWakanderPorId(any());
    }

    @Test
    @DisplayName("Não deve buscar Wakander quando token estiver expirado")
    void naoDeveBuscarWakanderQuandoTokenEstiverExpirado() {
        Autenticacao autenticacao = AutenticacaoDataHelper.criaAutenticacaoExpirada();
        when(autenticacaoRepository.buscaAutenticacao(autenticacao.getToken())).thenReturn(autenticacao);

        APIException exception = assertThrows(APIException.class,
                () -> autenticacaoApplicationService.buscaWakanderPeloToken(autenticacao.getToken()));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusException());
        assertEquals("Token expirado, informe ao suporte!", exception.getMessage());
        verify(autenticacaoRepository, times(1)).buscaAutenticacao(autenticacao.getToken());
        verify(wakanderRepository, never()).buscaWakanderPorId(any());
    }

    @Test
    @DisplayName("Deve mudar status da autenticacao para expirado")
    void deveMudarStatusDaAutenticacaoParaExpirada() {
        Autenticacao autenticacao = AutenticacaoDataHelper.criaAutenticacaoComDataExpirada();
        when(autenticacaoRepository.buscaAutenticacao(autenticacao.getToken())).thenReturn(autenticacao);
        when(autenticacaoRepository.salvaAutenticacao(autenticacao)).thenReturn(autenticacao);

        APIException exception = assertThrows(APIException.class,
                () -> autenticacaoApplicationService.buscaWakanderPeloToken(autenticacao.getToken()));

        assertEquals(StatusToken.EXPIRADO, autenticacao.getStatusToken());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusException());
        assertEquals("Token expirado, informe ao suporte!", exception.getMessage());
        verify(autenticacaoRepository, times(1)).buscaAutenticacao(autenticacao.getToken());
        verify(autenticacaoRepository, times(1)).salvaAutenticacao(autenticacao);
        verify(wakanderRepository, never()).buscaWakanderPorId(any());
    }

    @Test
    void alteraStatusTokenParaUtilizado() {
        Autenticacao autenticacao = AutenticacaoDataHelper.criaAutenticacao();
        when(autenticacaoRepository.buscaAutenticacao(autenticacao.getToken())).thenReturn(autenticacao);
        when(autenticacaoRepository.salvaAutenticacao(autenticacao)).thenReturn(autenticacao);

        autenticacaoApplicationService.alteraStatusTokenParaUtilizado(autenticacao.getToken());

        assertEquals(StatusToken.UTILIZADO, autenticacao.getStatusToken());
        verify(autenticacaoRepository, times(1)).buscaAutenticacao(autenticacao.getToken());
        verify(autenticacaoRepository, times(1)).salvaAutenticacao(autenticacao);
    }

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar o token")
    void deveAutenticarComSucesso() {
        // Arrange
        var loginDto = UsuarioAdmLoginDto.builder()
                .username("admin")
                .senha("senha123")
                .build();

        UsuarioAdmTesteDto usuarioAdmTesteDto = UsuarioAdmTesteDto.builder()
                .id(UUID.randomUUID())
                .nome("teste")
                .username("admin")
                .senha("senhaEncriptada")
                .perfil(PerfilUsuario.DEV)
                .build();

        UsuarioAdm usuarioAdm = new UsuarioAdm(usuarioAdmTesteDto);

        var authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(usuarioAdm);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenService.generateToken(usuarioAdm)).thenReturn("token-gerado");

        // Act
        var response = autenticacaoApplicationService.authenticate(loginDto);

        // Assert
        assertEquals(TokenType.BEARER, response.type());
        assertEquals("token-gerado", response.token());
        assertNotNull(response.expiracao());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateToken(usuarioAdm);
    }

    @Test
    @DisplayName("Não deve autenticar quando usuário estiver NAO_VERIFICADO")
    void naoDeveAutenticarQuandoUsuarioNaoVerificado() {
        var loginDto = UsuarioAdmLoginDto.builder()
                .username("nao@verificado.com")
                .senha("senha123")
                .build();

        UsuarioAdm usuarioAdm = DataHelper.criaUsuarioAdm(PerfilUsuario.NAO_VERIFICADO);
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuarioAdm);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        APIException exception = assertThrows(APIException.class, () -> autenticacaoApplicationService.authenticate(loginDto));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusException());
        assertEquals("Usuário ainda não liberado. Solicite liberação para a liderança.", exception.getMessage());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando autenticação falhar")
    void deveLancarExcecaoQuandoAutenticacaoFalhar() {

        var loginDto = UsuarioAdmLoginDto.builder()
                .username("admin")
                .senha("senhaErrada")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));


        APIException exception = assertThrows(APIException.class, () ->
                autenticacaoApplicationService.authenticate(loginDto)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusException());
        assertNotNull(exception.getMessage());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve buscar usuário por username com sucesso")
    void deveBuscarUsuarioPorUsernameComSucesso() {
        String username = "admin";
        UsuarioAdmTesteDto usuarioAdmTesteDto = UsuarioAdmTesteDto.builder()
                .id(UUID.randomUUID())
                .username(username)
                .senha("senhaEncriptada")
                .perfil(PerfilUsuario.DEV)
                .build();

        UsuarioAdm usuarioAdm = new UsuarioAdm(usuarioAdmTesteDto);

        when(usuarioAdmSpringDataJpaRepository.findByUsername(username))
                .thenReturn(Optional.of(usuarioAdm));


        UserDetails userDetails = authozirationApplicationService.loadUserByUsername(username);


        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(usuarioAdm.getSenha(), userDetails.getPassword());

        verify(usuarioAdmSpringDataJpaRepository, times(1))
                .findByUsername(username);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não encontrar usuário")
    void deveLancarExcecaoQuandoNaoEncontrarUsuario() {
        // Arrange
        String username = "naoexiste";

        when(usuarioAdmSpringDataJpaRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        // Act + Assert
        APIException exception = assertThrows(APIException.class, () ->
                authozirationApplicationService.loadUserByUsername(username)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        assertEquals("Usuario não encontrado!", exception.getMessage());

        verify(usuarioAdmSpringDataJpaRepository, times(1))
                .findByUsername(username);
    }
}
