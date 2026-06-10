ALTER TABLE progresso_wakander
    ALTER COLUMN id_wakander SET NOT NULL;

ALTER TABLE progresso_wakander
    ADD CONSTRAINT uk_progresso_wakander_id_wakander UNIQUE (id_wakander);