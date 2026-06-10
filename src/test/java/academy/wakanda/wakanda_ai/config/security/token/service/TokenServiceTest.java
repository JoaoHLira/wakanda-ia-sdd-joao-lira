package academy.wakanda.wakanda_ai.config.security.token.service;

import academy.wakanda.wakanda_ai.autenticacao.datahelper.AutenticacaoDataHelper;
import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import academy.wakanda.wakanda_ai.autenticacao.repository.AutenticacaoRepository;
import academy.wakanda.wakanda_ai.config.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    TokenService tokenService;

    @Mock
    AutenticacaoRepository autenticacaoRepository;

    @Test
    void geraTokenDeAutenticacao() {
        Autenticacao autenticacao = AutenticacaoDataHelper.criaAutenticacao();
        UUID idWakander = UUID.randomUUID();
        when(autenticacaoRepository.salvaAutenticacao(any())).thenReturn(autenticacao);

        tokenService.geraTokenDeAutenticacao(idWakander, 5);

        verify(autenticacaoRepository, times(1)).salvaAutenticacao(any());
    }
}