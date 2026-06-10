package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain;

public enum StatusJornada {
    ATIVA,
    INATIVA;

    public boolean isAtiva() {
        return this == ATIVA;
    }
    public boolean isInativa() {
        return this == INATIVA;
    }
}
