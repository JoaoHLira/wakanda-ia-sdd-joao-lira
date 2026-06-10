ALTER TABLE tipo_missao
ADD CONSTRAINT uq_tipo_missao_descricao UNIQUE (descricao);
