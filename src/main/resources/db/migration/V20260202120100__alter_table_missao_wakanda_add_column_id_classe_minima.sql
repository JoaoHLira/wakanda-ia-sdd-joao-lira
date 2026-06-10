ALTER TABLE missao_wakanda 
ADD COLUMN id_classe_minima UUID 
    REFERENCES classe_wakanda(id_classe);
