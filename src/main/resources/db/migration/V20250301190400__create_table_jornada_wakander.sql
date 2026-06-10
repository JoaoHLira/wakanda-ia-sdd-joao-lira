CREATE TABLE jornada_wakander (
    id_jornada_wakander UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    id_wakander UUID NOT NULL,
    jornada_atual VARCHAR(255) NOT NULL,
    jornada_concluida VARCHAR(255),
    momento_alteracao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wakander FOREIGN KEY (id_wakander) REFERENCES wakander(id_wakander)
);
