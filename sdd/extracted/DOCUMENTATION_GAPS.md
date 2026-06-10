# Documentation Gaps — WakandaAI

**Generated**: 2026-06-10 · Mode: FULL · Strategy: ASSISTED

## Cobertura por fonte

| Categoria | Código | Plain Docs | / | Cobertura |
|-----------|:------:|:----------:|:----------:|-----------|
| Gamificação (catalogo/progresso/xp) | ✅ | ✅ (`arquitetura-gameficacao.md`) | ❌ | Alta (VERIFIED) |
| Wakander / onboarding | ✅ | parcial (CLAUDE.md) | ❌ | Média (CODE_ONLY) |
| Financeiro (Asaas) | ✅ | parcial | ❌ | Média (CODE_ONLY) |
| Comunicação (Z-API/Discord) | ✅ | parcial | ❌ | Média (CODE_ONLY) |
| Autenticação | ✅ | parcial | ❌ | Média (CODE_ONLY) |

## Limitações da descoberta de atores

- MeliSystemMCP indisponível — clientes/dependências inferidos de `application.yml`, security config e clients.
-  indisponível — sem corroboração de documentação de plataforma.
- Recomendado: configurar os MCPs para dados autoritativos de arquitetura/consumidores.

## Lacunas específicas

| Lacuna | Impacto | Ação sugerida |
|--------|---------|---------------|
| Sem OpenAPI commitado | Médio | Exportar `/v3/api-docs` e versionar para re-extração API_ANCHORED |
| Contratos de webhook externos (payloads Asaas/Memberkit/Clint) não especificados | Médio | Documentar DTOs de entrada como spec ou ACL |
| `anyRequest().permitAll()` ativo | Alto (segurança) | Revisar política de autorização antes de produção |
| Idempotência de consumers SQS | Médio | Verificar estado no handler (ex.: `if (missaoProgresso.isConcluida()) return;`) |
| Schemas de DTO de request/response por endpoint | Baixo | Enriquecer via `/sdd.reverse-eng --focus <Controller>` quando necessário |
| Domínios `wakander`/`financeiro` sem doc arquitetural dedicada | Médio | Gerar `docs/arquitetura-financeiro.md` e `arquitetura-wakander.md` |

## Próximos passos
- `/sdd.reverse-eng --focus WakanderApplicationService` para aprofundar onboarding/financeiro.
- Promover specs para `sdd/specs/` (Fase 7) e iniciar evolução com `/sdd.start`.
