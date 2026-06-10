CREATE TABLE progresso_wakander (
    id_progresso_wakander UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_wakander UUID NOT NULL,
    nivel_atual VARCHAR(50) NOT NULL,
    classe_atual VARCHAR(100) NOT NULL,
    xp_total INT NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_progresso_wakander_wakander FOREIGN KEY (id_wakander) REFERENCES wakander(id_wakander)
);