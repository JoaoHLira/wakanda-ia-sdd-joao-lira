# Especificação Técnica — WakandaAI

> Reverse-engineered em 2026-06-10 (modo FULL, estratégia ASSISTED).
> Confiança: ✅✅ VERIFIED (código + doc) · 🔸 CODE_ONLY · ⚠️ DOCS_ONLY · ❓ UNKNOWN.
> **Código é a fonte da verdade.** Context path global: `/wakanda-ai/api`.

## Stack 🔸 (pom.xml)

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| Runtime | Java / Spring Boot | 17 / 3.3.4 |
| Persistência | Spring Data JPA + JDBC, Flyway, PostgreSQL | — |
| Mensageria | Spring Cloud AWS (SNS + SQS FIFO) | 3.2.1 / SDK 2.29.43 |
| Auth | Spring Security + Auth0 java-jwt | 4.4.0 |
| Frontend | Thymeleaf | (starter) |
| Discord | JDA | 5.3.0 |
| HTTP externo | spring-webflux (WebClient) | — |
| Docs API | springdoc-openapi (Swagger UI) | 2.3.0 |
| Testes | JUnit5, WireMock, fixture-factory, JaCoCo | 3.0.1 / 3.1.0 / 0.8.12 |

## Arquitetura ✅✅

Clean Architecture + DDD + Event-Driven. Padrão **Repository duplo (Ports/Adapters)** em 100% das
entidades; **Strategy Pattern** em 5 chains de processadores; eventos assíncronos via SNS→SQS FIFO
e eventos Spring síncronos intra-processo. Detalhamento completo do módulo de gamificação em
`docs/arquitetura-gameficacao.md`.

```
Controllers (application/api/) → ApplicationServices (application/service/)
  → Domain (domain/) → Repository port (application/service/) → Infra (infra/: JPA + SQS + clients)
```

Domínios: `wakander`, `autenticacao`, `comunicacao`, `financeiro`, `gameficacao`
(catalogo/progresso/xp), `jornadawakander`, `frontend/web`, `config`.

## Inventário de Endpoints REST 🔸

> Todos prefixados por `/wakanda-ai/api`. Autenticação: JWT, exceto os marcados `permitAll`.
> Atualmente `anyRequest().permitAll()` está ativo (ver §Segurança).

### `/autenticacao` — AutenticacaoApi
| Método | Path | Acesso |
|--------|------|--------|
| POST | `/cadastro` | permitAll |
| POST | `/login` | permitAll |
| GET | `/token-teste` | autenticado |
| PATCH | `/reativa-token/{token}` | autenticado |

### `/wakander` — WakanderAPI
| Método | Path |
|--------|------|
| POST | `/novo-wakander` |
| PATCH | `/{idWakander}/regulariza` |
| PATCH | `/{idWakander}/progresso/jornada-conhecimento` |
| PATCH | `/{idWakander}/progresso/jornada-habilidade` |
| PATCH | `/{idWakander}/progresso/jornada-conquista` |
| PATCH | `/{idWakander}/progresso/vibraniun` |
| PATCH | `/{idWakander}/edita-wakander` |
| GET | `/{idWakander}` |
| GET | `/dado-oculto/{token}` |
| GET | `/busca-wakanders` |
| GET | `/busca-wakanders/{statusCadastro}` |
| PATCH | `/dados-wakander` |
| PATCH | `/envia-formularios-wakanders` |
| PATCH | `/atualiza-status-cadastro` |
| GET | `/estatistica-wakanders` |
| PATCH | `/atualiza-dados-asaas` |
| PATCH | `/cadastro/{token}` |
| PATCH | `/{idWakander}/cancela-assinatura` |
| PATCH | `/{idWakander}/reverte-cancelamento` |
| POST | `/{idWakander}/fiador/atualizacao-link` |
| PATCH | `/{idWakander}/inicia-onboarding-manual` |
| GET | `/` (lista) |

### `/whatsapp-message` — ComunicacaoApi
| Método | Path |
|--------|------|
| POST | `/envia` |
| POST | `/convida` |
| POST | `/publica-notificacao` |

### `/wakander/jornada` — JornadaWakanderApi
| Método | Path |
|--------|------|
| POST | `/associar-discord` |

### `/gameficacao/missao-progresso` — MissaoProgressoApi
| Método | Path |
|--------|------|
| POST | `/` |
| PATCH | `/{idMissaoProgresso}/conclui` |
| GET | `/wakander/{idWakander}/jornada/{idJornada}` |
| GET | `/progresso/{idProgressoWakander}/missoes-concluidas` |

### `/gameficacao/progresso` — ProgressoWakanderApi
| Método | Path |
|--------|------|
| POST | `/{idWakander}` |
| GET | `/ranking-wakanders` |
| POST | `/sincroniza-antigos` |
| POST | `/sincroniza-antigo/{idWakander}` |
| GET | `/{idWakander}` |

### `/financeiro/cobranca` — CobrancaAPI
| Método | Path |
|--------|------|
| POST | `/processa-evento` |

### `/financeiro/assinaturas` — AssinaturaAPI
| Método | Path |
|--------|------|
| POST | `/` |
| PATCH | `/fiador/{token}` |

### `/memberkit` — MemberkitController
| Método | Path |
|--------|------|
| POST | `/` |
| POST | `/import` |

### `/trilhas` — TrilhaWakandaAPI
| Método | Path |
|--------|------|
| POST | `/` |
| GET | `/{idTrilha}` |
| GET | `/` |

### `/jornadas` — JornadaWakandaAPI
| Método | Path |
|--------|------|
| POST | `/` |
| GET | `/{idTrilha}/lista-jornadas` |
| GET | `/{idJornada}` |
| GET | `/` |

### `/classes` — ClasseWakandaAPI
| Método | Path |
|--------|------|
| POST | `/` |
| GET | `/{idClasse}` |
| GET | `/ativas` |
| PATCH | `/{idClasse}` |

### `/tipos-missao` — TipoMissaoAPI
| Método | Path |
|--------|------|
| POST | `/` |

### `/missoes` — MissaoWakandaAPI
| Método | Path |
|--------|------|
| POST | `/` |
| PATCH | `/{idMissao}/atualiza-missao-com-ia` |
| GET | `/{idMissao}` |
| PATCH | `/{idMissao}/status/desativar` |
| PATCH | `/{idMissao}/pontuacao` |
| PATCH | `/{idMissao}/ordem/{posicao}` |
| GET | `/{idJornada}/missoes` |

### `/painel-dados` — DashboardApi
| Método | Path | Acesso |
|--------|------|--------|
| GET | `/dashboard` | autenticado |
| GET | `/login` | permitAll |

### `/formulario` — FormularioApi (telas Thymeleaf)
| Método | Path |
|--------|------|
| GET | `/cadastro/{token}` |
| GET | `/cancelamento-assinatura/{idWakander}` |
| GET | `/dados-complementares/{token}` |
| GET | `/{username}/{idDiscord}/associar-discord` |
| GET | `/resposta-discord` |
| GET | `/atualiza-fiador/{token}` |
| GET | `/resposta-atualiza-fiador` |

## Modelo de Dados — Entidades JPA 🔸 (18)

| Entidade | Domínio | Observação |
|----------|---------|-----------|
| `Wakander` | wakander | Raiz do aluno; VOs embarcados: Contato, Financeiro, Fiador, AulaAssistida; CPF/idMemberKit únicos ✅✅ |
| `UsuarioAdm` | autenticacao | Usuário administrativo |
| `Autenticacao` | autenticacao | Token/credencial |
| `Cobranca` | financeiro | Evento/estado de cobrança Asaas |
| `OnboardingWakander` | jornadawakander | Estado de onboarding |
| `AulaAssistida` | jornadawakander | Aula concluída |
| `JornadaWakander` | jornadawakander | Progresso de jornada do aluno |
| `HistoricoRelatorio` | jornadawakander | Auditoria de relatórios |
| `TrilhaWakanda` | gameficacao/catalogo | Sequência de jornadas ✅✅ |
| `JornadaWakanda` | gameficacao/catalogo | Agrupamento de missões ✅✅ |
| `MissaoWakanda` | gameficacao/catalogo | Domínio rico; VOs OrdemMissao, Sabedorias ✅✅ |
| `TipoMissao` | gameficacao/catalogo | Lookup table (anêmica — débito conhecido) ✅✅ |
| `ProgressoWakander` | gameficacao/progresso | Raiz de progresso (1:1 Wakander) ✅✅ |
| `JornadaProgresso` | gameficacao/progresso | Status de jornada por Wakander ✅✅ |
| `MissaoProgresso` | gameficacao/progresso | Status de missão por Wakander ✅✅ |
| `XpWakander` | gameficacao/xp | XP total + nível + sabedorias (lógica Fibonacci) ✅✅ |
| `ClasseWakanda` | gameficacao/xp | Rank Bronze→Diamante ✅✅ |
| `HistoricoClasseWakander` | gameficacao/xp | Auditoria de promoções ✅✅ |

> Relacionamentos são **lógicos** (FK por UUID), sem `@OneToMany`/`@ManyToOne` entre agregados. ✅✅
> 67 migrations Flyway em `src/main/resources/db/migration/`.

## Mensageria — Tópicos SNS / Filas SQS ✅✅ (application.yml)

Todas FIFO (`*.fifo`) com DLQ correspondente.

| Tópico SNS | Fila SQS | DLQ |
|------------|----------|-----|
| `zapi-requests-topic.fifo` | `zapi-requests-queue.fifo` (+ delay) | `zapi-requests-queue-dlq.fifo` |
| `memberkit-requests-topic.fifo` | `memberkit-requests-queue.fifo` | `memberkit-requests-queue-dlq.fifo` |
| `asaas-requests-topic.fifo` | `asaas-requests-queue.fifo` | — |
| `clint-contato-requests-topic.fifo` | `clint-contato-requests-queue.fifo` | `clint-contato-requests-dlq.fifo` |
| `clint-requests-topic.fifo` | `clint-requests-queue.fifo` | `clint-requests-dlq.fifo` |
| `discord-requests-topic.fifo` | `discord-requests-queue.fifo` | `discord-requests-queue-dlq.fifo` |
| `progresso-wakander-requests-topic.fifo` | `progresso-wakander-requests-queue.fifo` | `progresso-wakander-requests-queue-dlq.fifo` |
| `xp-wakander-requests-topic.fifo` | `xp-wakander-requests-queue.fifo` | `xp-wakander-requests-queue-dlq.fifo` |

### Consumers (`@SqsListener`) 🔸
- `ComunicacaoConsumerSqs` — 9 filas (zapi/memberkit/asaas/clint/discord), dispatch por tipo.
- `JornadaWakanderConsumerSqs` — `memberkit-requests` (Strategy).
- `ProgressoWakanderConsumer` — `progresso-wakander-requests` (Strategy).
- `XpWakanderConsumer` — `xp-wakander-requests` (`@SqsListener` + `@EventListener` híbrido).

### Publisher central
`comunicacao/infra/PublicadorNotificacaoInfraSns.java` — `deduplicationId = UUID.randomUUID()`
(⚠️ dedup FIFO efetivamente desligada; idempotência deve estar no consumer — débito §9.8 do doc).

## Integrações Externas (clients) 🔸

| Serviço | Port / Adapter | Config |
|---------|----------------|--------|
| Z-API (WhatsApp) | `ZApiClient` / `ZApiInfraClient` | `z-api.*` |
| Asaas | `AsaasClient` / `AsaasWebClient` | `asaas.*` |
| Memberkit | `JornadaWakanderClient` / `JornadaWakanderWebClient` | `memberkit.*` (classroom/course ids) |
| Discord | `DiscordClient` / `DiscordInfraClient` (JDA) | `discord.*` |

ACL: Memberkit e Asaas bem encapsulados; Z-API e Discord vazam DTOs externos (débito §9.4). ⚠️/✅✅

## Segurança 🔸 (`SecurityConfiguration.java`)
- Filtro JWT (`SecurityFilter`), stateless.
- `permitAll`: `POST /autenticacao/login`, `POST /autenticacao/cadastro`, `GET /painel-dados/login`,
  alguns `GET /gameficacao/*` (home, catalogo/missoes, missoes/*, ProgressoWakanders, progresso),
  `GET /swagger-ui/**`, `GET /v3/api-docs/**`.
- Roles: `devPaths` → `hasRole("DEV")`; `liderancaPaths` → `hasRole("LIDERANCA")`.
- ⚠️ **`anyRequest().permitAll()`** está ativo — regras de role só aplicam se a requisição casar
  com os matchers anteriores; o restante fica aberto. Revisar antes de produção.

## Enums de Domínio 🔸 (principais)
`StatusCadastro`, `WakanderStatusFinanceiro`, `StatusDadosFiador`, `NivelWakander`,
`MissaoStatus`, `MissaoProgressoStatus`, `MissaoDisponibilidadeStatus`, `ProcessamentoStatus`,
`JornadaProgressoStatus`, `StatusJornada`, `ClasseWakandaStatus`, `HistoricoClasseWakanderStatus`,
`CobrancaStatus`, `CobrancaEventoType`, `AssinaturaStatus`, `AssinaturaType`,
`ProgressoWakanderEventType`, `MemberkitEventType`, `MemberKitTipoRequisicao`,
`ZApiEventype`, `ZAPITypeGrupo`, `DiscordEventype`, `StatusAula`, `StatusChecklist`,
`StatusClint`, `StatusContatoClint`, `StatusRelatorio`, `StatusToken`, `StatusUsuario`,
`PerfilUsuario`, `TipoLogin`, `TokenType`, `TipoRecalculo`.

## Débito Técnico Conhecido (de `arquitetura-gameficacao.md`) ✅✅
1. `TipoMissao` sem polimorfismo (tabela-como-enum) — bloqueia tipos de missão com comportamento próprio.
2. Cascata síncrona em `MissaoProgressoApplicationService.concluiMissao()` — preferir Outbox / `AFTER_COMMIT`.
3. `XpPromocaoClasseEvent` síncrono dentro da TX de XP — usar `@TransactionalEventListener(AFTER_COMMIT)`.
4. ACL fraca em Z-API/Discord; `@Transactional` inconsistente; Bean Validation no domínio.
5. Idempotência de consumers SQS (dedup por UUID aleatório).
