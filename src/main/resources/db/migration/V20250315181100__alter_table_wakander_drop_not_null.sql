ALTER TABLE wakander
    ALTER COLUMN nome DROP NOT NULL,
    ALTER COLUMN id_member_kit DROP NOT NULL,
    ALTER COLUMN whatsapp DROP NOT NULL,
    ALTER COLUMN email DROP NOT NULL,
    ALTER COLUMN status_financeiro DROP NOT NULL,
    ALTER COLUMN ultima_atualizacao_financeiro DROP NOT NULL;
