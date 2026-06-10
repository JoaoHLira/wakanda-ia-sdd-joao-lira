package academy.wakanda.wakanda_ai.config.security;


import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import academy.wakanda.wakanda_ai.autenticacao.repository.AutenticacaoRepository;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.autenticacao.domain.UsuarioAdm;
import academy.wakanda.wakanda_ai.autenticacao.infra.UsuarioAdmSpringDataJpaRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class TokenService {
    private final AutenticacaoRepository autenticacaoRepository;

    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${security.token.jwt.secret}")
    private String secret;

    @Value("${security.token.jwt.expiration}")
    private Long expiration;



    private final UsuarioAdmSpringDataJpaRepository usuarioAdmSpringDataJpaRepository;

    public String generateToken(UsuarioAdm user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("wakanda-ai")
                    .withSubject(user.getUsername())
                    .withExpiresAt(genarateExpirationTime())
                    .sign(algorithm);
        }catch (JWTCreationException exception) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar token" + exception.getMessage());
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("wakanda-ai")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException exception){
            log.error("Erro ao validar token: " + exception.getMessage());
            return null;
        }
    }

    private Instant genarateExpirationTime() {
        return LocalDateTime.now().plusHours(expiration).toInstant(ZoneOffset.of("-03:00"));
    }

    public String geraTokenDeAutenticacao(UUID idWakander, Integer tempoExpiracao) {
        log.info("[start] TokenService - geraTokenDeAutenticacao");
        byte[] randomBytes = new byte[16];
        SECURE_RANDOM.nextBytes(randomBytes);
        String token = URL_ENCODER.encodeToString(randomBytes);
        autenticacaoRepository.salvaAutenticacao(new Autenticacao(token, idWakander, tempoExpiracao));
        log.debug("[finish] TokenService - geraTokenDeAutenticacao");
        return token;
    }


}
