package academy.wakanda.wakanda_ai.jornadawakander.domain;

import lombok.Getter;

@Getter
public enum StatusAula {
    CONCLUIDO("LESSON_STATUS.SAVED");

    private final String status;

    StatusAula(String status) {
        this.status = status;
    }
}
