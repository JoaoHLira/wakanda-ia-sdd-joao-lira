# WakandaAI

Sistema Spring Boot para automatizar e otimizar os processos internos da Escola Wakanda Academy — gamificação, onboarding, cobranças e comunicação com alunos.

## Stack Técnico

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| Runtime | Java + Spring Boot | 17 / 3.3.4 |
| Build | Maven Wrapper | 3.9 |
| Database | PostgreSQL + Flyway + HikariCP | 17 / pool 15-50 |
| Messaging | AWS SNS/SQS (FIFO) via Spring Cloud AWS | 3.2.1 |
| Auth | JWT (Auth0) + Spring Security | 4.4.0 |
| Frontend | Thymeleaf | 3.3.4 |
| Discord | JDA | 5.3.0 |
| Infra | Terraform + Docker Compose | AWS RDS/VPC/EC2/SNS/SQS |
| CI/CD | GitHub Actions → ghcr.io | — |
| Testes | JUnit5 + WireMock + FixtureFactory + JaCoCo | 3.0.1 / 3.1.0 |

## Arquitetura

Clean Architecture + DDD + Event-Driven:

```
Controllers (application/api/)
    ↓
ApplicationServices (application/service/)   ← orquestração
    ↓
Domain (domain/)                             ← entidades, value objects
    ↓
Repository Interface (port)
    ↓
Infra (infra/)                               ← JPA + JDBC + SQS Consumers + HTTP clients
```

**Padrões-chave:**

- **Repository duplo (Ports/Adapters)** — aplicado em 100% das entidades:
  - Port: `*Repository` (interface) em `application/service/` — retorna **domain objects** (sem `@Entity` na assinatura)
  - Adapter: `*InfraRepository` (`@Repository`) em `infra/` — implementa o port
  - Spring Data: `*SpringDataJpaRepository extends JpaRepository` em `infra/` — usado **dentro** do adapter
  - **ApplicationServices nunca importam `*SpringDataJpaRepository` direto** — sempre via port

- **Processadores (Strategy Pattern)** — 5 chains existentes (whatsapp, memberkit-catalogo, memberkit-jornada, discord, progresso). Forma canônica:
  ```java
  public interface XxxProcessor {
      boolean validaSeProcessa(TipoEvento tipo);
      void processaEvento(EventoDto evento);
  }
  ```
  Service orquestrador injeta `List<XxxProcessor>` e seleciona com `.filter(...).findFirst()`.
  **Adicionar novo tipo = nova classe `@Component implements XxxProcessor`. Zero modificação em código existente.**

- **Event-driven assíncrono**: SNS publica → SQS FIFO consome (`@SqsListener`). Sempre `*.fifo` com DLQ correspondente
- **Spring events síncronos**: para reações intra-processo (ex: `AssinaturaCanceladaConsumer`, `XpPromocaoClasseEvent`)

## Domínios

| Pacote | Responsabilidade |
|--------|-----------------|
| `wakander` | Entidade central (aluno) com embedded VOs: Contato, Financeiro, Fiador, AulaAssistida |
| `autenticacao` | JWT token service + Spring Security + `UsuarioAdm` |
| `comunicacao` | Z-API (WhatsApp) + Discord + processadores de eventos de comunicação |
| `financeiro` | Asaas (pagamentos/cobranças) + assinaturas + eventos de cobrança |
| `gameficacao` | XP, Classes, Missões, Jornadas, Trilhas, Progresso — ver `docs/arquitetura-gameficacao.md` |
| `jornadawakander` | Onboarding, aulas assistidas, progresso de jornada, scheduler |
| `config` | AWS config, Discord config, Spring Security config, Swagger |

## Setup Local

```bash
# 1. Subir infraestrutura (Postgres + LocalStack + WireMock)
docker compose -f docker-compose.dev.yml up -d

# 2. Configurar LocalStack — somente na primeira vez
aws configure --profile localstack
# AWS Access Key ID: test
# AWS Secret Access Key: test
# Default region: us-east-1
cd infraestrutura/init && ./localstack-linux-init.sh

# 3. Rodar a aplicação
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

**URLs locais:**
- API: `http://localhost:8080/wakanda-ai/api`
- Swagger UI: `http://localhost:8080/wakanda-ai/api/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8080/wakanda-ai/api/v3/api-docs`
- LocalStack: `http://localhost:4566`
- WireMock: `http://localhost:8181`

## Convenções

**Pacote raiz:** `academy.wakanda.wakanda_ai`

**Context path:** `/wakanda-ai/api`

**Branches:** `feat/WAI-<número>-<descricao>`, `fix/`, `refactor/`, `chore/`, `release/`

**Migrations Flyway:** `V<YYYYMMDDHHmmss>__<descricao_snake_case>.sql`
- Ex: `V20250411163400__add_column_data_nascimento_wakander.sql`

**Filas SQS:** Todas FIFO (`*.fifo`) com DLQ correspondente
- Tópicos SNS: `zapi-requests-topic.fifo`, `memberkit-requests-topic.fifo`, `asaas-requests-topic.fifo`, `discord-requests-topic.fifo`, `progresso-wakander-requests-topic.fifo`, `xp-wakander-requests-topic.fifo`

**Profiles Spring:** `local` (LocalStack + WireMock), `dev`, `prod`

## Convenções de Código (seguir ao criar código novo)

**Naming:**
| Tipo | Padrão | Localização |
|------|--------|-------------|
| Controller REST | `*Api` | `application/api/` |
| Orquestrador público | `*ApplicationService implements *Service` | `application/service/` |
| Helper especializado | `*Service` (sem interface pública) | `application/service/` |
| Port repositório | `*Repository` (interface) | `application/service/` |
| Adapter repositório | `*InfraRepository` (`@Repository`) | `infra/` |
| Spring Data CRUD | `*SpringDataJpaRepository` | `infra/` |
| Consumer SQS | `*Consumer` ou `*ConsumerSqs` (`@SqsListener`) | `infra/` |
| Implementação Strategy | `*Processor` ou `*Processador` (`@Component`) | `application/service/<dominio>/processadores/` |
| External client | `*Client` ou `*InfraClient` | `infra/` |

**DDD aplicado neste projeto:**
- Lógica de negócio em **métodos do domínio**, não em service. Ex: `XpWakander.adicionarXpEAtualizarNivel()` ao invés de service calcular e setar
- Value Objects com **factories validadoras** (ex: `OrdemMissao.criar(int)` valida `ordem >= 0`)
- **Agregação por UUID** — sem `@OneToMany`/`@ManyToOne` entre agregados. Navegação requer busca explícita no repositório
- Invariantes protegidos no **construtor** + métodos comportamentais

**Transações & Eventos:**
- `@Transactional` no **nível da classe** em ApplicationServices que mutam estado (padronizar — hoje está inconsistente)
- Eventos pós-processamento: preferir `@TransactionalEventListener(phase = AFTER_COMMIT)` para evitar rollback em cascata
- Outbox Pattern ou SNS para eventos críticos que cruzam módulos

## Dívida Arquitetural Conhecida (não replicar)

Identificada em `docs/arquitetura-gameficacao.md`. Em código novo, prefira os padrões opostos:

| Anti-pattern existente | Como fazer em código novo |
|------------------------|---------------------------|
| `TipoMissao` como tabela-enum sem polimorfismo | Use **Strategy Pattern** desde o início (interface + impls `@Component`) para qualquer "tipo" com comportamento próprio |
| Cascata síncrona em `MissaoProgressoApplicationService.concluiMissao()` | Fluxos com múltiplos passos: prefira **Outbox** ou eventos `AFTER_COMMIT` |
| Spring Event síncrono dentro da TX (ex: `XpPromocaoClasseEvent`) | Sempre `@TransactionalEventListener(AFTER_COMMIT)` em listeners de pós-processamento |
| `@NotBlank`/`@NotNull` em entidades de domínio | Validação de framework fica em DTOs de API. Domínio valida via método/factory |
| DTOs externos (`ZApiEventDto`, `DiscordEventRequest`) vazando para application | Toda integração nova: adapter traduz DTO externo → DTO interno do domínio (ACL) |
| Cache manual com `HashMap` | `@Cacheable` do Spring com TTL explícito |
| `deduplicationId = UUID.randomUUID()` sem idempotência no handler | Handlers de SQS críticos: checar estado antes de processar (ex: `if (missaoProgresso.isConcluida()) return;`) |

## Comandos de Teste

```bash
./mvnw test                              # Todos os testes
./mvnw test -Dtest=NomeDaClasseTest      # Teste específico
./mvnw verify                            # Build + testes + JaCoCo
./mvnw jacoco:report                     # Relatório de cobertura em target/site/jacoco/
```

## Variáveis de Ambiente (produção)

| Variável | Descrição |
|----------|-----------|
| `SECURITY_JWT_SECRET` | Segredo para assinar JWT |
| `WAKANDA_AI_PORT` | Porta exposta do container |
| `HEALTH_URL` | URL do health check (actuator) |
| `CLINT_WEBHOOK_URL` | Webhook do CRM Clint |
| Z-API credentials | Token e instance ID do WhatsApp |
| Asaas credentials | API Key (sandbox vs prod) |
| Discord credentials | Bot token |

## Integrações Externas

| Serviço | Propósito | Mock local |
|---------|-----------|-----------|
| Z-API (`api.z-api.io`) | Envio de mensagens WhatsApp | WireMock `stub/z-api` |
| Asaas (`api.asaas.com`) | Cobranças e assinaturas | WireMock `stub/asaas` + sandbox |
| Memberkit | LMS — cadastro e acesso a cursos | WireMock `stub/memberkit` |
| Discord (`discord.com`) | Bot de comunidade (JDA) | WireMock `stub/discord` |
| Clint | CRM via webhooks | — |
| N8N | Automação via webhooks | — |

## Documentação Arquitetural

Documentos técnicos do projeto estão em `docs/`:

| Arquivo | Conteúdo |
|---------|---------|
| `docs/arquitetura-gameficacao.md` | Arquitetura completa do módulo de gamificação: XP, Classes (Bronze→Diamante), Missões, Jornadas, Trilhas, eventos assíncronos e fluxos de progressão |

> Ao gerar nova documentação arquitetural, salvar em `docs/` seguindo o padrão `arquitetura-<dominio>.md`.

## Git — Conta Pessoal

Este repositório usa a conta pessoal `vinireis` (não a MeLi). A identidade está configurada localmente:
```bash
git config user.name   # → Vinicius Reis
git config user.email  # → vinicius.reis@msn.com
git remote -v          # → git@github.com-personal:tribos-dev/wakanda-ai.git
```

---

## SDD Kit

This project uses **SDD Kit** for spec-driven development.

### Spec Language
All specifications MUST be written in **Portuguese** (`pt`).
Do not mix languages in specs. Technical terms (API, REST, CRUD) stay in English.

### Quick Reference
- Framework expert: `Skill("sdd-kit-expert")`
- Workflow: `/sdd.start` → `/sdd.spec` → `/sdd.plan` → `/sdd.build` → `/sdd.finish`
- Project conventions: `sdd/PROJECT.md`
- Discovered patterns: `sdd/PATTERNS.md`

### Rules
- Never create files under `sdd/specs/`, `sdd/wip/`, or `sdd/features/` manually
- Always go through the `/sdd.start` workflow
- Respect the phased workflow — don't skip phases

## Dicas SDD para este projeto

**Stack**: Use o skill `java-spring-expert` em specs técnicas — cobre Spring Boot 3.x, JPA, Bean Validation, JUnit5 + Mockito + MockMvc.

**Branch naming** no SDD: seguir a convenção do projeto → `feat/WAI-<número>-<descricao>`

**Domínios prioritários para documentar com SDD:**
- `gameficacao` — XP, Classes, Missões (ver `docs/arquitetura-gameficacao.md` como referência)
- `financeiro` — fluxo Asaas + assinaturas + webhooks
- `jornadawakander` — onboarding + aulas assistidas + scheduler

**Camadas de implementação** no `sdd.build` para este projeto:
- Layer 1 — código local (entities, services, controllers, testes)
- Layer 3 — quality gates (JaCoCo ≥ 80%, lint, build Maven)

**Teste de integração**: usar WireMock (já configurado em `docker-compose.dev.yml`) em vez de mocks HTTP. Preferir `@SpringBootTest` com LocalStack para testes de SQS.

@sdd-kit/CLAUDE.md
