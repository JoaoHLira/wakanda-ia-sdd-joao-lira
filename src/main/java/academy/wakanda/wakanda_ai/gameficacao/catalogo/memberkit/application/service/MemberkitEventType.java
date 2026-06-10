package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service;

public enum MemberkitEventType {
    LOGIN_FEITO("user.signed_in"),
    AULA_ASSISTIDA("lesson_status.saved"),
    LOGIN_ENVIADO("login.sent"),
    AULA_CRIADA("lesson.created");

    public String getDescricao() {
        return descricao;
    }

    private String descricao;

    MemberkitEventType(String descricao) {
        this.descricao = descricao;
    }
}
