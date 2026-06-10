INSERT INTO onboarding_wakander (
    id_onboarding_wakander,
    id_wakander,
    cadastro_confirmado,
    entrou_discord,
    entrou_grupo_whatsapp,
    acessou_plataforma_estudo,
    concluiu_comece_aqui,
    concluiu_primeira_aula_jornada,
    id_discord,
    user_discord
)
SELECT
    gen_random_uuid(),
    w.id_wakander,
    TRUE,
    FALSE,
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    NULL,
    NULL
FROM wakander w
LEFT JOIN onboarding_wakander o ON o.id_wakander = w.id_wakander
WHERE o.id_wakander IS NULL;