CREATE TABLE aula_assistida (
    id_aula_assistida UUID PRIMARY KEY,
    id_aula BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,
    id_wakander UUID NOT NULL,
    data_conclusao TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL
);