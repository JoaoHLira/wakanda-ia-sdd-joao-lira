CREATE TABLE onboarding_wakander (
    id_onboarding_wakander UUID PRIMARY KEY,
    id_wakander UUID NOT NULL UNIQUE,
    cadastro_confirmado BOOLEAN NOT NULL,
    entrou_discord BOOLEAN NOT NULL,
    entrou_grupo_whatsapp BOOLEAN NOT NULL,
    acessou_plataforma_estudo BOOLEAN NOT NULL,
    concluiu_comece_aqui BOOLEAN NOT NULL,
    concluiu_primeira_aula_jornada BOOLEAN NOT NULL,
    CONSTRAINT fk_onboarding_wakander_wakander
        FOREIGN KEY (id_wakander) REFERENCES wakander(id_wakander)
);