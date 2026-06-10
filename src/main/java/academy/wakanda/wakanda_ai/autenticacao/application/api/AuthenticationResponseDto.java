package academy.wakanda.wakanda_ai.autenticacao.application.api;

import java.time.LocalDateTime;


public record AuthenticationResponseDto(TokenType type, LocalDateTime expiracao, String token) {
}
