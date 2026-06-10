ALTER TABLE missao_progresso
ADD COLUMN sab_teorico INT NOT NULL DEFAULT 0,
ADD COLUMN sab_processo INT NOT NULL DEFAULT 0,
ADD COLUMN sab_know_how INT NOT NULL DEFAULT 0,
ADD COLUMN sab_comportamental INT NOT NULL DEFAULT 0,
ADD COLUMN sab_criativo INT NOT NULL DEFAULT 0;

UPDATE missao_progresso mp
SET sab_teorico = mw.sab_teorico,
    sab_processo = mw.sab_processo,
    sab_know_how = mw.sab_know_how,
    sab_comportamental = mw.sab_comportamental,
    sab_criativo = mw.sab_criativo
FROM missao_wakanda mw
WHERE mp.id_missao_wakanda = mw.id_missao
  AND mp.status = 'CONCLUIDA';
