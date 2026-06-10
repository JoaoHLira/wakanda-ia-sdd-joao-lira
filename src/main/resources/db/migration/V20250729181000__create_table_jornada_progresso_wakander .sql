CREATE TABLE jornada_progresso_wakander (
    id_jornada_wakander UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_jornada_wakanda UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    ordem_missoes_posicao INT NOT NULL,
    ultima_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jornada_progresso_jornada FOREIGN KEY (id_jornada_wakanda) REFERENCES jornada_wakanda(id_jornada)
);