CREATE TABLE autenticacao (
    token VARCHAR(64) PRIMARY KEY,
    id_wakander UUID NOT NULL,
    data_expiracao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status_token VARCHAR(10) NOT NULL,
    data_utilizacao TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT fk_token_wakander FOREIGN KEY (id_wakander) REFERENCES wakander(id_wakander)
);
