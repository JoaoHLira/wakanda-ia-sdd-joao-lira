# Technical Specification — WakandaAI

**Version**: 1.0
**Last Updated**: 2026-06-17
**Status**: Extracted from codebase

---

## Stack Tecnológico

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| Runtime | Java + Spring Boot | 17 / 3.3.4 |
| Build | Maven Wrapper | 3.9.9 |
| Database | PostgreSQL + Flyway + HikariCP | 17 / pool 15-50 |
| ORM | Spring Data JPA / Hibernate | — |
| Messaging | AWS SNS/SQS (FIFO) via Spring Cloud AWS | 3.2.1 |
| Auth | JWT (Auth0 java-jwt) + Spring Security | 4.4.0 |
| Frontend | Thymeleaf | — |
| Discord Bot | JDA | 5.3.0 |
| HTTP Client | WebFlux WebClient | — |
| API Docs | SpringDoc OpenAPI | 2.3.0 |
| Testes | JUnit5 + WireMock + FixtureFactory + JaCoCo | — |

---

## Arquitetura

```
┌────────────────────────────────────────────────────────────────┐
│  Controllers (application/api/)                                 │
│  REST (@RestController) + Thymeleaf (@Controller)              │
└────────────┬───────────────────────────────────────────────────┘
             │
┌────────────▼───────────────────────────────────────────────────┐
│  ApplicationServices (application/service/)                     │
│  Orquestração + Processadores (Strategy Pattern)               │
└────────────┬───────────────────────────────────────────────────┘
             │
┌────────────▼───────────────────────────────────────────────────┐
│  Domain (domain/)                                               │
│  Entidades + Value Objects + Enums                             │
└────────────┬───────────────────────────────────────────────────┘
             │
┌────────────▼───────────────────────────────────────────────────┐
│  Repository Ports (application/service/)                        │
│  *Repository interfaces — retornam domain objects              │
└────────────┬───────────────────────────────────────────────────┘
             │
┌────────────▼───────────────────────────────────────────────────┐
│  Infra (infra/)                                                 │
│  *InfraRepository (JPA/JDBC) + *Client + *ConsumerSqs          │
│  Spring Data: *SpringDataJpaRepository                          │
└────────────────────────────────────────────────────────────────┘
```

### Princípios Arquiteturais

1. **Clean Architecture**: Dependências apontam para dentro (domain não depende de infra)
2. **Ports & Adapters**: Repositórios definidos como interfaces (ports), implementados em infra (adapters)
3. **Event-Driven**: SNS → SQS FIFO para comunicação assíncrona entre módulos
4. **Strategy Pattern**: 5 chains de processadores — novo tipo = nova classe `@Component`, zero modificação
5. **DDD**: Value Objects com factories, agregação por UUID, invariantes no construtor

---

## API REST — Endpoints

### 1. Wakander (`/wakander`) — 22 endpoints
**Controller**: `WakanderAPI.java`

| Método | Path | Response | Descrição |
|--------|------|----------|-----------|
| POST | `/wakander/novo-wakander` | 201 | Matricula novo wakander (deprecated) |
| PATCH | `/wakander/{idWakander}/regulariza` | 204 | Regulariza cadastro |
| PATCH | `/wakander/{idWakander}/progresso/jornada-conhecimento` | 204 | Atualiza jornada conhecimento |
| PATCH | `/wakander/{idWakander}/progresso/jornada-habilidade` | 204 | Atualiza jornada habilidade |
| PATCH | `/wakander/{idWakander}/progresso/jornada-conquista` | 204 | Atualiza jornada conquista |
| PATCH | `/wakander/{idWakander}/progresso/vibraniun` | 204 | Atualiza vibraniun |
| PATCH | `/wakander/{idWakander}/edita-wakander` | 204 | Edita dados do wakander |
| GET | `/wakander/{idWakander}` | 200 | Busca por ID |
| GET | `/wakander/dado-oculto/{token}` | 200 | Busca com dados ocultos |
| GET | `/wakander/busca-wakanders` | 200 | Busca paginada |
| GET | `/wakander/busca-wakanders/{statusCadastro}` | 200 | Busca por status |
| PATCH | `/wakander/dados-wakander` | 200 | Recebe dados pessoais |
| PATCH | `/wakander/envia-formularios-wakanders` | 200 | Envia formulários |
| PATCH | `/wakander/atualiza-status-cadastro` | 200 | Atualiza status em lote |
| GET | `/wakander/estatistica-wakanders` | 200 | Estatísticas |
| PATCH | `/wakander/atualiza-dados-asaas` | 200 | Sincroniza Asaas |
| PATCH | `/wakander/cadastro/{token}` | 200 | Completa cadastro |
| PATCH | `/wakander/{idWakander}/cancela-assinatura` | 204 | Cancela assinatura |
| PATCH | `/wakander/{idWakander}/reverte-cancelamento` | 204 | Reverte cancelamento |
| POST | `/wakander/{idWakander}/fiador/atualizacao-link` | 201 | Link atualização fiador |
| PATCH | `/wakander/{idWakander}/inicia-onboarding-manual` | 204 | Onboarding manual |
| GET | `/wakander` | 200 | Busca com filtros |

### 2. Autenticação (`/autenticacao`) — 4 endpoints
**Controller**: `AutenticacaoApi.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/autenticacao/cadastro` | 201 |
| POST | `/autenticacao/login` | 200 |
| GET | `/autenticacao/token-teste` | 200 |
| PATCH | `/autenticacao/reativa-token/{token}` | 200 |

### 3. Financeiro Cobrança (`/financeiro/cobranca`) — 1 endpoint
**Controller**: `CobrancaAPI.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/financeiro/cobranca/processa-evento` | 204 |

### 4. Financeiro Assinatura (`/financeiro/assinaturas`) — 2 endpoints
**Controller**: `AssinaturaAPI.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/financeiro/assinaturas` | 204 |
| PATCH | `/financeiro/assinaturas/fiador/{token}` | 200 |

### 5. Comunicação WhatsApp (`/whatsapp-message`) — 3 endpoints
**Controller**: `ComunicacaoApi.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/whatsapp-message/envia` | 204 |
| POST | `/whatsapp-message/convida` | 200 |
| POST | `/whatsapp-message/publica-notificacao` | 200 |

### 6. Jornada Wakander (`/wakander/jornada`) — 1 endpoint
**Controller**: `JornadaWakanderApi.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/wakander/jornada/associar-discord` | 200 |

### 7. Gamificação — Missões (`/missoes`) — 7 endpoints
**Controller**: `MissaoWakandaAPI.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/missoes` | 201 |
| PATCH | `/missoes/{idMissao}/atualiza-missao-com-ia` | 204 |
| GET | `/missoes/{idMissao}` | 200 |
| PATCH | `/missoes/{idMissao}/status/desativar` | 200 |
| PATCH | `/missoes/{idMissao}/pontuacao` | 204 |
| PATCH | `/missoes/{idMissao}/ordem/{posicao}` | 204 |
| GET | `/missoes/{idJornada}/missoes` | 200 |

### 8. Gamificação — Trilhas (`/trilhas`) — 3 endpoints
**Controller**: `TrilhaWakandaAPI.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/trilhas` | 201 |
| GET | `/trilhas/{idTrilha}` | 200 |
| GET | `/trilhas` | 200 |

### 9. Gamificação — Jornadas (`/jornadas`) — 4 endpoints
**Controller**: `JornadaWakandaAPI.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/jornadas` | 201 |
| GET | `/jornadas/{idTrilha}/lista-jornadas` | 200 |
| GET | `/jornadas/{idJornada}` | 200 |
| GET | `/jornadas` | 200 |

### 10. Gamificação — Tipos Missão (`/tipos-missao`) — 1 endpoint
**Controller**: `TipoMissaoAPI.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/tipos-missao` | 201 |

### 11. Memberkit (`/memberkit`) — 2 endpoints
**Controller**: `MemberkitController.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/memberkit` | 200 |
| POST | `/memberkit/import` | 204 |

### 12. Gamificação — Progresso (`/gameficacao/progresso`) — 5 endpoints
**Controller**: `ProgressoWakanderApi.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/gameficacao/progresso/{idWakander}` | 201 |
| GET | `/gameficacao/progresso/ranking-wakanders` | 200 |
| POST | `/gameficacao/progresso/sincroniza-antigos` | 200 |
| POST | `/gameficacao/progresso/sincroniza-antigo/{idWakander}` | 200 |
| GET | `/gameficacao/progresso/{idWakander}` | 200 |

### 13. Gamificação — Missão Progresso (`/gameficacao/missao-progresso`) — 4 endpoints
**Controller**: `MissaoProgressoApi.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/gameficacao/missao-progresso` | 201 |
| PATCH | `/gameficacao/missao-progresso/{idMissaoProgresso}/conclui` | 204 |
| GET | `/gameficacao/missao-progresso/wakander/{idWakander}/jornada/{idJornada}` | 200 |
| GET | `/gameficacao/missao-progresso/progresso/{idProgressoWakander}/missoes-concluidas` | 200 |

### 14. Gamificação — Classes (`/classes`) — 4 endpoints
**Controller**: `ClasseWakandaAPI.java`

| Método | Path | Response |
|--------|------|----------|
| POST | `/classes` | 201 |
| GET | `/classes/{idClasse}` | 200 |
| GET | `/classes/ativas` | 200 |
| PATCH | `/classes/{idClasse}` | 204 |

---

## Database — Entidades JPA (18)

| Entidade | Tabela | PK | Chaves Únicas |
|----------|--------|----|---------------|
| Wakander | `wakander` | `id_wakander` (UUID) | `cpf`, `id_member_kit` |
| Cobranca | `cobranca` | `id_cobranca` (UUID) | `id_payment_asaas` |
| Autenticacao | `autenticacao` | `token` (String) | — |
| UsuarioAdm | `usuario_adm` | `id` (UUID) | `username` |
| AulaAssistida | `aula_assistida` | `id_aula_assistida` (UUID) | — |
| HistoricoRelatorio | `historico_relatorio` | `id_relatorio` (UUID) | — |
| JornadaWakander | `jornada_wakander` | `id_jornada_wakander` (UUID) | — |
| OnboardingWakander | `onboarding_wakander` | `id_onboarding_wakander` (UUID) | `id_wakander` |
| JornadaWakanda | `jornada_wakanda` | `id_jornada` (UUID) | — |
| MissaoWakanda | `missao_wakanda` | `id_missao` (UUID) | `titulo` |
| TipoMissao | `tipo_missao` | `id_tipo_missao` (UUID) | `descricao` |
| TrilhaWakanda | `trilha_wakanda` | `id_trilha` (UUID) | `nome` |
| ProgressoWakander | `progresso_wakander` | `id_progresso_wakander` (UUID) | `id_wakander` |
| JornadaProgresso | `jornada_progresso` | `id_jornada_progresso` (UUID) | — |
| MissaoProgresso | `missao_progresso` | `id_missao_progresso` (UUID) | `(id_missao_wakanda, id_progresso_wakander)` |
| XpWakander | `xp_wakander` | `id_xp_wakander` (UUID) | — |
| ClasseWakanda | `classe_wakanda` | `id_classe` (UUID) | `nome`, `descricao` |
| HistoricoClasseWakander | `historico_classe_wakander` | `id_historico_classe` (UUID) | — |

### Value Objects (Embedded)

| VO | Domínio | Campos |
|----|---------|--------|
| WakanderContato | wakander | `whatsapp`, `email` |
| WakanderFinanceiro | wakander | `status`, `ultimaAtualizacao`, `motivoCancelamento` |
| WakanderFiador | wakander | `idAsaas`, `idAssinatura`, `nome`, `cpf`, `telefone` |
| WakanderAulaAssistida | wakander | `dateTime`, `idAulaAssistida` |
| OrdemMissao | gameficacao | `ordem` |
| Sabedorias | gameficacao | `teorico`, `processo`, `knowHow`, `comportamental`, `criativo` |
| SabedoriasMissaoProgresso | gameficacao | `teorico`, `processo`, `knowHow`, `comportamental`, `criativo` |

### Migrations Flyway (68)
Localização: `src/main/resources/db/migration/`
Formato: `V<YYYYMMDDHHmmss>__<descricao>.sql`

---

## Event-Driven Architecture

### SNS Topics (FIFO)

| Tópico | Consumidores |
|--------|-------------|
| `zapi-requests-topic.fifo` | `ComunicacaoConsumerSqs` (zapi-requests, zapi-requests-delay, zapi-requests-dlq) |
| `memberkit-requests-topic.fifo` | `ComunicacaoConsumerSqs` (memberkit-requests-dlq), `JornadaWakanderConsumerSqs` |
| `asaas-requests-topic.fifo` | `ComunicacaoConsumerSqs` (asaas-requests) |
| `discord-requests-topic.fifo` | `ComunicacaoConsumerSqs` (discord-request, discord-dlq) |
| `progresso-wakander-requests-topic.fifo` | `ProgressoWakanderConsumer` |
| `xp-wakander-requests-topic.fifo` | `XpWakanderConsumer` |
| `clint-requests-topic.fifo` | `ComunicacaoConsumerSqs` (clint-requests, clint-requests-dlq) |
| `clint-contato-requests-topic.fifo` | `ComunicacaoConsumerSqs` (clint-contato-requests, clint-contato-requests-dlq) |

### Spring Events (Síncronos)

| Evento | Publisher | Listener |
|--------|-----------|----------|
| `AssinaturaCanceladaEvent` | WakanderApplicationService | `AssinaturaCanceladaConsumer` |
| `XpPromocaoClasseEvent` | XpWakanderApplicationService | — (processa classe) |
| `ProgressoOnboardEvent` | ProgressoWakanderApplicationService | `ProgressoOnboardConsumer` |

### Consumidores SQS (15 `@SqsListener`)

| Classe | Queue |
|--------|-------|
| `ComunicacaoConsumerSqs` | `teste` |
| `ComunicacaoConsumerSqs` | `zapi-requests` |
| `ComunicacaoConsumerSqs` | `zapi-requests-delay` |
| `ComunicacaoConsumerSqs` | `memberkit-requests-dlq` |
| `ComunicacaoConsumerSqs` | `asaas-requests` |
| `ComunicacaoConsumerSqs` | `clint-contato-requests` |
| `ComunicacaoConsumerSqs` | `clint-contato-requests-dlq` |
| `ComunicacaoConsumerSqs` | `zapi-requests-dlq` |
| `ComunicacaoConsumerSqs` | `discord-request` |
| `ComunicacaoConsumerSqs` | `discord-dlq` |
| `ComunicacaoConsumerSqs` | `clint-requests-dlq` |
| `ComunicacaoConsumerSqs` | `clint-requests` |
| `JornadaWakanderConsumerSqs` | `memberkit-requests` |
| `XpWakanderConsumer` | `xp-wakander-requests` |
| `ProgressoWakanderConsumer` | `progresso-wakander-requests` |

---

## Integrações Externas

### 1. Z-API (WhatsApp)
- **Client**: `ZApiInfraClient.java` (WebClient)
- **Base URL**: `https://api.z-api.io`
- **Endpoints**: `/send-text`, `add-participant`, `remove-participant`
- **Auth**: Instance ID + Token
- **Processors**: 4 (Normal, AddToGroup, RemoveToGroup, TodayOnly)

### 2. Asaas (Pagamentos)
- **Client**: `AsaasWebClient.java`
- **Base URL**: `https://api.asaas.com` / `https://api-sandbox.asaas.com`
- **Endpoints**: `GET /v3/customers`, `GET /v3/subscriptions`, `PUT /v3/customers`
- **Auth**: `access_token` header
- **CobrancaProcessors**: 4 (Criada, Confirmada, Vencida, Negativada)
- **AssinaturaProcessors**: 2 (Criada, Cancelada)

### 3. Memberkit (LMS)
- **Client**: `MemberkitClientHttp.java`
- **Base URL**: `https://memberkit.com.br/api/v1`
- **Endpoints**: `GET /courses`, `GET /courses/{id}`
- **Auth**: `api_key` query param
- **Gamification Processors**: 4 (Aula, AulaCriada, LoginEnviado, LoginFeito)
- **Journey Processors**: 4 (Cadastro, AcessoBloqueado, AulaComeceAqui, AulaConhecimento)

### 4. Discord
- **Runtime**: JDA Bot (`DiscordBotConfig.java`)
- **Client**: `DiscordInfraClient.java`
- **Intents**: GUILD_MEMBERS, GUILD_MESSAGES
- **Processors**: 2 (SendMessage, RemoveFromServer)

### 5. Clint CRM
- **Clients**: `ClintCRMInfra.java`, `CancelaClintInfra.java`
- **Flow**: SNS → SQS → HTTP POST to webhook URL

### 6. N8N
- **Webhook**: configured via `n8n.webhook` property

---

## Segurança

### Autenticação
- **JWT**: Auth0 java-jwt, HMAC256, 8h expiration, issuer `wakanda-ai`
- **Login**: `POST /autenticacao/login` → BCrypt validation → JWT token
- **Filter**: `SecurityFilter` (OncePerRequestFilter) before `UsernamePasswordAuthenticationFilter`

### Autorização

| Role | Paths |
|------|-------|
| `permitAll()` | Login, cadastro, painel-dados, gameficacao views, Swagger, OpenAPI |
| `ROLE_DEV` | `/autenticacao/token-teste`, `/gameficacao/**`, `/trilhas/**`, `/jornadas/**`, `/missoes/**`, `/classes/**`, `/tipos-missao/**`, `/wakander/busca-wakanders*`, `/wakander/estatistica-wakanders` |
| `ROLE_LIDERANCA` | `/wakander/atualiza-dados-asaas`, `/wakander/atualiza-status-cadastro`, `/wakander/*/inicia-onboarding-manual`, `/wakander/*/fiador/atualizacao-link` |
| `permitAll()` (default) | Todos os demais endpoints |

### CSRF: Disabled
### Session: Stateless (JWT)

---

## Scheduled Jobs

| Job | Cron | Ativo |
|-----|------|-------|
| `deletaTokensExpirados` | `0 0 5 * * 3` (Quarta 05:00) | ✅ |
| `agendaEnvioRelatorio` | `0 0 8 * * MON` (Segunda 08:00) | ✅ |
| `geraRelatorioWakandersInativos` | `0 0 8 * * 1` (Segunda 08:00) | ✅ |

---

## Testes

| Framework | Finalidade |
|-----------|-----------|
| JUnit5 | Testes unitários |
| WireMock | Mock de serviços externos (Z-API, Asaas, Memberkit, Discord) |
| FixtureFactory | Geração de dados de teste |
| JaCoCo | Cobertura (target ≥ 80%) |

**58 classes de teste** encontradas.

---

## Configuração de Infraestrutura

- **Profile `local`**: LocalStack + WireMock
- **Profile `dev`**: Desenvolvimento
- **Profile `prod`**: Produção
- **Container**: Docker Compose (`docker-compose.dev.yml`)
- **IaC**: Terraform (AWS RDS/VPC/EC2/SNS/SQS)
- **CI/CD**: GitHub Actions → ghcr.io
