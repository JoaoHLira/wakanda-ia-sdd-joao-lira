CREATE TABLE historico_classe_wakander (
    id_historico_classe UUID PRIMARY KEY,
    id_classe UUID NOT NULL,
    id_xp_wakander UUID NOT NULL,
    id_progresso_wakander UUID NOT NULL,
    id_wakander UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP
);

-- Adiciona as constraints de foreign key
ALTER TABLE historico_classe_wakander
ADD CONSTRAINT fk_historico_classe_wakander_classe
FOREIGN KEY (id_classe) REFERENCES classe_wakanda(id_classe);

ALTER TABLE historico_classe_wakander
ADD CONSTRAINT fk_historico_classe_wakander_xp
FOREIGN KEY (id_xp_wakander) REFERENCES xp_wakander(id_xp_wakander);

ALTER TABLE historico_classe_wakander
ADD CONSTRAINT fk_historico_classe_wakander_progresso
FOREIGN KEY (id_progresso_wakander) REFERENCES progresso_wakander(id_progresso_wakander);

ALTER TABLE historico_classe_wakander
ADD CONSTRAINT fk_historico_classe_wakander_wakander
FOREIGN KEY (id_wakander) REFERENCES wakander(id_wakander);
