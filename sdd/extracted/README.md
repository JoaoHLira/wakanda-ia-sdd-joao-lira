# Extraction Index — WakandaAI

**Extraction date**: 2026-06-10 · **Mode**: FULL · **Strategy**: ASSISTED

## Artifacts

| File | Phase | Content |
|------|-------|---------|
| `raw/existing-specs/DETECTION_REPORT.md` | 0 | Frameworks detectados + estratégia |
| `raw/README.md` | 0-1 | Metadados e métricas da extração |
| `functional-spec.md` | 4 | Atores, contexto, casos de uso, regras de negócio |
| `technical-spec.md` | 4 | Stack, 17 controllers + endpoints, 18 entidades, SNS/SQS, segurança, débito |
| `PATTERNS.md` | 5 | 8 padrões estabelecidos com evidência de código |
| `DOCUMENTATION_GAPS.md` | 2 | Cobertura por fonte e lacunas |

## Resumo

- 378 arquivos Java · 82 testes · 67 migrations Flyway
- 17 controllers REST · 18 entidades JPA · 4 consumers SQS · ~22 processadores Strategy
- Stack: Java 17 / Spring Boot 3.3.4, PostgreSQL+Flyway, Spring Cloud AWS (SNS/SQS FIFO), JWT, Thymeleaf, JDA
- Corroboração forte do módulo de gamificação via `docs/arquitetura-gameficacao.md`

## Consistência Funcional ↔ Técnica (Fase 6)
- Casos de uso de gamificação ↔ endpoints `/trilhas`, `/jornadas`, `/missoes`, `/classes`, `/gameficacao/*` ✅
- CU Wakander/onboarding ↔ `/wakander`, `/formulario`, `/wakander/jornada` ✅
- CU Financeiro ↔ `/financeiro/cobranca`, `/financeiro/assinaturas` ✅
- CU Comunicação ↔ `/whatsapp-message` + consumers ✅
- Sem inconsistências CRÍTICAS detectadas. WARNING: contratos de DTO por endpoint não detalhados.
