CREATE TABLE jornada_wakanda(
    id_jornada UUID PRIMARY KEY,
    titulo     VARCHAR(255) NOT NULL,
    descricao  TEXT NOT NULL,
    xp_total   BIGINT NOT NULL,
    trilha_wakanda UUID NOT NULL,
    CONSTRAINT fk_trilha_wakanda FOREIGN KEY (trilha_wakanda) REFERENCES trilha_wakanda (id_trilha)

);