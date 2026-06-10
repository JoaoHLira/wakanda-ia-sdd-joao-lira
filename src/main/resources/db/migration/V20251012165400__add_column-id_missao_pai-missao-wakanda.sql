ALTER TABLE missao_wakanda
ADD COLUMN id_missao_pai UUID;

ALTER TABLE missao_wakanda
ADD CONSTRAINT fk_missao_pai
FOREIGN KEY (id_missao_pai)
REFERENCES missao_wakanda(id_missao)
ON DELETE CASCADE
ON UPDATE CASCADE;
