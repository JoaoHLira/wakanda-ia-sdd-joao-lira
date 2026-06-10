CREATE TABLE classe_wakanda (
    id_classe UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT NOT NULL UNIQUE,
    xp_necessario INTEGER NOT NULL,
    ordem_classe INTEGER NOT NULL,
    sab_teorico INTEGER NOT NULL,
    sab_processo INTEGER NOT NULL,
    sab_know_how INTEGER NOT NULL,
    sab_comportamental INTEGER NOT NULL,
    sab_criativo INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE classe_wakanda_missoes (
    id_classe UUID NOT NULL,
    id_missao UUID NOT NULL,
    CONSTRAINT fk_classe_wakanda FOREIGN KEY (id_classe) 
        REFERENCES classe_wakanda(id_classe),
    CONSTRAINT fk_missao_wakanda FOREIGN KEY (id_missao) 
        REFERENCES missao_wakanda(id_missao)
);
