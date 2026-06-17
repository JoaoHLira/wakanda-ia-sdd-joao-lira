# Documentation Gaps

**Generated**: 2026-06-17

## Coverage Analysis

| Category | Source | Coverage | Gaps |
|----------|--------|----------|------|
| **API Endpoints** | Code | 100% | No OpenAPI/Swagger spec file |
| **Domain Entities** | Code | 100% | No entity relationship diagram |
| **Business Rules** | Code | 80% | Domain logic embedded in services, not documented |
| **External Integrations** | Code | 100% | No integration sequence diagrams |
| **Architecture** | Code + docs | 90% | `docs/arquitetura-gameficacao.md` covers gamification |
| **Security/Auth** | Code | 100% | Auth flows documented in code only |
| **Async Flows** | Code | 70% | SNS → SQS chains inferred, no explicit documentation |
| **Reports** | Code | 60% | Scheduled jobs documented in code only |

## Missing Documentation

1. **OpenAPI/Swagger spec file** — No `openapi.yaml` or `swagger.json` found
2. **Entity Relationship Diagram** — No visual schema diagram
3. **Async Event Flow Diagrams** — No documentation of SNS→SQS chains per topic
4. **Deployment/Infrastructure** — Terraform and Docker Compose exist, but no topology diagram

## Recommendations

- Generate OpenAPI spec from code annotations using SpringDoc
- Create async flow documentation for each SNS→SQS chain
- Create deployment topology diagram
