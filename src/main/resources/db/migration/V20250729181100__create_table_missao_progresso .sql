CREATE TABLE missao_progresso (
    id_missao_progresso UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_missao_wakanda UUID NOT NULL,
    id_tipo_missao UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    xp_obtido INT NOT NULL,
    tentativas INT NOT NULL,
    ultima_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_missao_progresso_missao FOREIGN KEY (id_missao_wakanda) REFERENCES missao_wakanda(id_missao),
    CONSTRAINT fk_missao_progresso_tipo FOREIGN KEY (id_tipo_missao) REFERENCES tipo_missao(id_tipo_missao)
);