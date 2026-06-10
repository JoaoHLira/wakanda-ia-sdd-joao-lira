package academy.wakanda.wakanda_ai.autenticacao.domain;

import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "autenticacao")

public class Autenticacao {

    @Id
    private String token;
    private UUID idWakander;
    private LocalDateTime dataExpiracao;
    @Enumerated(EnumType.STRING)
    private StatusToken statusToken;

    public Autenticacao(String token, UUID idWakander, Integer tempoExpiracao) {
        this.token = token;
        this.idWakander = idWakander;
        this.dataExpiracao = LocalDateTime.now().plusDays(tempoExpiracao).truncatedTo(ChronoUnit.SECONDS);
        this.statusToken = StatusToken.VALIDO;
    }

    public boolean verificaDataDeExpiracao() {
        return this.dataExpiracao.isBefore(LocalDateTime.now());
    }

    public void mudaStatusTokenParaExpirado() {
        this.statusToken = StatusToken.EXPIRADO;
    }

    public void reativaTokenExpirado() {
        verificaSeTokenEstaExpirado();
        this.dataExpiracao = LocalDateTime.now().plusDays(2);
        this.statusToken = StatusToken.VALIDO;
    }

    private void verificaSeTokenEstaExpirado() {
        if (!this.statusToken.equals(StatusToken.EXPIRADO)) {
            throw APIException.build(HttpStatus.CONFLICT,
                    "O token não esta expirado! Ele expirará em " + this.dataExpiracao);
        }
    }

    public void mudaStatusTokenParaUtilizado() {
        this.statusToken = StatusToken.UTILIZADO;
    }

}
