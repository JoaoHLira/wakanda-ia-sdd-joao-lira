UPDATE wakander w
SET
    id_discord = CASE
        WHEN (w.id_discord IS NULL OR w.id_discord = '') AND o.id_discord IS NOT NULL THEN o.id_discord
        ELSE w.id_discord
    END,
    user_discord = CASE
        WHEN (w.user_discord IS NULL OR w.user_discord = '') AND o.user_discord IS NOT NULL THEN o.user_discord
        ELSE w.user_discord
    END
FROM onboarding_wakander o
WHERE o.id_wakander = w.id_wakander
  AND (o.id_discord IS NOT NULL OR o.user_discord IS NOT NULL);
