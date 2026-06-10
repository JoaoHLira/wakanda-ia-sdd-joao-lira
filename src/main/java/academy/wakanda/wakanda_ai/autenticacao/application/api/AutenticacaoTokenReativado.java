package academy.wakanda.wakanda_ai.autenticacao.application.api;

import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class AutenticacaoTokenReativado {

    private String linkFormulario;
    private LocalDate diaDeExpiracao;
    private LocalTime horaDeExpiracao;

    public AutenticacaoTokenReativado(String linkFormulario, Autenticacao autenticacao) {
        this.linkFormulario = linkFormulario;
        this.diaDeExpiracao = autenticacao.getDataExpiracao().toLocalDate();
        this.horaDeExpiracao = autenticacao.getDataExpiracao().toLocalTime();
    }
}
