# Extraction Raw Data — WakandaAI

**Extraction date**: 2026-06-10
**Mode**: FULL EXTRACTION
**Strategy**: ASSISTED (Plain Docs + Claude Code corroborating code analysis)

## Sources

| Source | Available | Notes |
|--------|-----------|-------|
| Code analysis | ✅ | Primary source of truth (378 Java files, `src/main/java`) |
| Plain Docs | ✅ | `docs/arquitetura-gameficacao.md`, `README.md`, `CLAUDE.md` |
|  /  | ❌ | Not configured in this environment |
| MeliSystemMCP | ❌ | Not available — actors inferred from code + config + docs |
| OpenAPI export | ❌ | No committed spec; live at `/v3/api-docs` when running |

## Codebase Metrics

| Metric | Value |
|--------|-------|
| Java source files | 378 |
| Test files | 82 |
| Flyway migrations | 67 |
| REST controllers (`@RestController`) | 17 |
| JPA entities (`@Entity`) | 18 |
| SQS consumers (`@SqsListener`) | 4 classes (15 listener methods) |
| External clients (`*Client`) | 4 integrations (Asaas, Discord, Z-API, JornadaWakander/Memberkit) |
| Strategy processors | ~22 across 5 chains |
| Enums | 30+ |

## Stack (from pom.xml)

- Java 17, Spring Boot 3.3.4
- Spring Data JPA + JDBC, Flyway, PostgreSQL
- Spring Cloud AWS 3.2.1 (SNS + SQS), AWS SDK 2.29.43
- Spring Security + Auth0 java-jwt 4.4.0
- Thymeleaf, JDA 5.3.0 (Discord)
- spring-webflux (reactive WebClient for external HTTP)
- Test: JUnit5, WireMock 3.0.1, fixture-factory 3.1.0, JaCoCo 0.8.12
