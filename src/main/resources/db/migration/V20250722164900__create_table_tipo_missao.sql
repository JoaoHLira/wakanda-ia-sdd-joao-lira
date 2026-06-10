CREATE TABLE tipo_missao
(
    id_tipo_missao     UUID PRIMARY KEY,
    descricao          TEXT    NOT NULL,
    sab_teorico        INTEGER NOT NULL,
    sab_processo       INTEGER NOT NULL,
    sab_know_how       INTEGER NOT NULL,
    sab_comportamental INTEGER NOT NULL,
    sab_criativo       INTEGER NOT NULL
);