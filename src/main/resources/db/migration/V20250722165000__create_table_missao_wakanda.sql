CREATE TABLE missao_wakanda
(
    id_missao   UUID PRIMARY KEY,
    titulo      TEXT    NOT NULL,
    descricao   TEXT    NOT NULL,
    xp_base     INTEGER NOT NULL,
    tipo_missao UUID    NOT NULL,
    jornada     UUID    NOT NULL,
    CONSTRAINT fk_tipo_missao FOREIGN KEY (tipo_missao) REFERENCES tipo_missao (id_tipo_missao),
    CONSTRAINT fk_jornada FOREIGN KEY (jornada) REFERENCES jornada_wakanda (id_jornada)
);