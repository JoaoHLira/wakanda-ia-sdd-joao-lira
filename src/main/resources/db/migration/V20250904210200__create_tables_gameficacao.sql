
CREATE TABLE trilha_wakanda (
    id_trilha UUID PRIMARY KEY,
    nome VARCHAR(255) UNIQUE NOT NULL,
    descricao TEXT,
    xp_total INT
);

CREATE TABLE tipo_missao (
    id_tipo_missao UUID PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL
);

CREATE TABLE jornada_wakanda (
    id_jornada UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    xp_total INT NOT NULL,
    id_trilha_wakanda UUID NOT NULL,
    status_jornada VARCHAR(50) NOT NULL,
    xp_bonus INT NOT NULL DEFAULT 0,
    ordem_jornada INT NOT NULL,
    CONSTRAINT fk_jornada_trilha FOREIGN KEY (id_trilha_wakanda) REFERENCES trilha_wakanda(id_trilha)
);

CREATE TABLE progresso_wakander (
    id_progresso_wakander UUID PRIMARY KEY,
    id_wakander UUID NOT NULL UNIQUE,
    data_criacao TIMESTAMP,
    CONSTRAINT fk_progresso_wakander FOREIGN KEY (id_wakander) REFERENCES wakander(id_wakander)
);

CREATE TABLE jornada_progresso (
    id_jornada_progresso UUID PRIMARY KEY,
    id_jornada_wakanda UUID NOT NULL,
    id_progresso_wakander UUID NOT NULL,
    status VARCHAR(50),
    xp_obtido INT NOT NULL,
    data_inicio TIMESTAMP,
    ultima_atualizacao TIMESTAMP,
    CONSTRAINT fk_jornada_progresso_jornada FOREIGN KEY (id_jornada_wakanda) REFERENCES jornada_wakanda(id_jornada),
    CONSTRAINT fk_jornada_progresso_progresso FOREIGN KEY (id_progresso_wakander) REFERENCES progresso_wakander(id_progresso_wakander)
);

CREATE TABLE missao_wakanda (
    id_missao UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    xp_base INT NOT NULL,
    id_tipo_missao UUID NOT NULL,
    id_jornada UUID NOT NULL,
    missao_status VARCHAR(50),
    ordem_missao INT NOT NULL,
    sab_teorico INT NOT NULL,
    sab_processo INT NOT NULL,
    sab_know_how INT NOT NULL,
    sab_comportamental INT NOT NULL,
    sab_criativo INT NOT NULL,
    CONSTRAINT fk_missao_tipo FOREIGN KEY (id_tipo_missao) REFERENCES tipo_missao(id_tipo_missao),
    CONSTRAINT fk_missao_jornada FOREIGN KEY (id_jornada) REFERENCES jornada_wakanda(id_jornada)
);

CREATE TABLE missao_progresso (
    id_missao_progresso UUID PRIMARY KEY,
    id_missao_wakanda UUID NOT NULL,
    id_progresso_wakander UUID NOT NULL,
    status VARCHAR(50),
    xp_obtido INT NOT NULL,
    tentativas INT NOT NULL,
    ultima_atualizacao TIMESTAMP,
    CONSTRAINT fk_missao_progresso_missao FOREIGN KEY (id_missao_wakanda) REFERENCES missao_wakanda(id_missao),
    CONSTRAINT fk_missao_progresso_progresso FOREIGN KEY (id_progresso_wakander) REFERENCES progresso_wakander(id_progresso_wakander)
);

CREATE TABLE xp_wakander (
    id_xp_wakander UUID PRIMARY KEY,
    id_progresso_wakander UUID NOT NULL,
    classe_atual VARCHAR(100) NOT NULL,
    xp_total INT NOT NULL,
    nivel_atual INT NOT NULL,
    ultima_atualizacao TIMESTAMP,
    CONSTRAINT fk_xp_progresso FOREIGN KEY (id_progresso_wakander) REFERENCES progresso_wakander(id_progresso_wakander)
);