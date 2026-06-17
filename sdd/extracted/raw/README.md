# Extraction Raw Data

**Generated**: 2026-06-17
**Repository**: WakandaAI
**Mode**: FULL EXTRACTION

## Sources

| Source | Description | Location |
|--------|-------------|----------|
| Code analysis | Full codebase scan | `code-analysis/` |
| Documentation | Project docs | `docs/` directory |
| Existing specs | Detection report | `existing-specs/DETECTION_REPORT.md` |

## Extraction Commands Executed

1. Stack detection via `pom.xml`
2. Controller/endpoint extraction via annotation scanning (17 controllers, 72+ endpoints)
3. Entity extraction (18 JPA entities, 68 migrations)
4. Service layer extraction (27 application services)
5. SQS consumer extraction (15 consumers)
6. Security configuration extraction
7. External integration extraction (6 systems)

## Limitations

- No MCP/Fury documentation available — code is sole source of truth
- No OpenAPI/Swagger specs found
- No external architecture docs beyond `docs/arquitetura-gameficacao.md`
