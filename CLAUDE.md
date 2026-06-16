# WakandaAI

Sistema Spring Boot para automatizar e otimizar os processos internos da Escola Wakanda Academy — gamificação, onboarding, cobranças e comunicação com alunos.

## 🧭 Norte Estratégico (ler antes de qualquer decisão)

**Visão:** plataforma que escala empregabilidade de programadores juniors via gamificação narrativa (Wakanda + 5 Sabedorias Fleury) + AI + comunidade que ensina e é recompensada.

**Grupo-alvo:** aspirantes a programadores e programadores em início de carreira (< R$4k/mês) — Norte/Nordeste/periferia, com vetor crescente pra mulheres/LGBTQIA+.

**Modelo de monetização:** mensalidade (recursos básicos + quotas) + créditos compráveis (premium: mentoria 1:1, imersão, review extra) + **créditos ganhos contribuindo** (mentorar, criar conteúdo). Economia circular.

**Atributos de qualidade priorizados (em ordem):**
**Extensibilidade > Mantenibilidade > Disponibilidade > Observabilidade > Segurança > Performance > Escalabilidade**

**Princípios não-negociáveis:**
- **Trade-off triplo** em toda automação: (1) melhor UX do Wakander × (2) maior impacto comunitário × (3) viabilidade. Sem categorias proibidas de AI — análise caso a caso.
- **Tom Wakanda** ("não nos levamos tão a sério") obrigatório em automação.
- **Toda integração externa nasce com ACL** — DTO externo nunca toca application layer.

**Documentação estratégica completa:**
- 📘 [`docs/architecture-haiku.md`](docs/architecture-haiku.md) — restrições, atributos de qualidade, decisões arquiteturais
- 📗 [`docs/product-vision-board.md`](docs/product-vision-board.md) — visão, grupo-alvo, necessidades, diferenciais, metas
- 📕 [`docs/arquitetura-gameficacao.md`](docs/arquitetura-gameficacao.md) — estado do código + §11 com priorização derivada dos atributos
- 📓 [`docs/_extraction/strategic-context.md`](docs/_extraction/strategic-context.md) — material-fonte (Modelagem Fase 5, drawio, sessões prévias)

**Para tomar qualquer decisão de trade-off técnico:** derive da §11 do `arquitetura-gameficacao.md` (gap → atributo comprometido → necessidade VB bloqueada → prioridade).

---

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

> **⚠️ Java version:** Lombok 1.18.34 tem incompatibilidade parcial com Java 21+ (annotation processor falha em alguns arquivos com `@Log4j2`/`@Getter`). Use **Java 17** para builds locais. Se seu `java -version` mostrar Java 21+, use o wrapper `./mvnw17` (instalado em `brew install openjdk@17`) em vez de `./mvnw` direto. Quando Lombok subir para 1.18.38+, este wrapper pode ser removido.

```bash
# 0. (Opcional) Garantir Java 17 disponível
brew install openjdk@17    # se ainda não tiver

# 1. Subir infraestrutura (Postgres + LocalStack + WireMock)
docker compose -f docker-compose.dev.yml up -d

# 2. Configurar LocalStack — somente na primeira vez
aws configure --profile localstack
# AWS Access Key ID: test
# AWS Secret Access Key: test
# Default region: us-east-1
cd infraestrutura/init && ./localstack-linux-init.sh

# 3. Rodar a aplicação (use ./mvnw17 se seu java -version mostrar Java 21+)
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
- **Construção via construtor**: construtor **público** em agregados raiz; **package-private** em entidades/VOs internos (só o agregado raiz cria). Factory `criar()` é exceção legítima (defaults complexos / estado determinístico / validação sobre primitivo — ex: `OrdemMissao.criar(int)` valida `ordem >= 0`, `XpWakander.novoComDefaults` calcula fibonacci)
- **Agregação por UUID** — sem `@OneToMany`/`@ManyToOne` entre agregados. Navegação requer busca explícita no repositório
- Invariantes protegidos no **construtor** + métodos comportamentais

**Transações & Eventos:**
- `@Transactional` no **nível da classe** em ApplicationServices que mutam estado (padronizar — hoje está inconsistente)
- Eventos pós-processamento: preferir `@TransactionalEventListener(phase = AFTER_COMMIT)` para evitar rollback em cascata
- Outbox Pattern ou SNS para eventos críticos que cruzam módulos

## SDD: Padrões obrigatórios em specs (P1-P22 / A1-A12)

> Toda spec gerada via `/sdd.spec` e todo código gerado via `/sdd.build` deve respeitar os padrões abaixo. Code review **bloqueia merge** em violações. Detalhes, exemplos canônicos e contraexemplos em [`sdd/PATTERNS.md`](sdd/PATTERNS.md).

**Padrões obrigatórios — Arquiteturais (P1-P11):**
- [P1](sdd/PATTERNS.md#p1-repository-portadapter-hexagonal) Repository Port/Adapter (3 arquivos por agregado)
- [P2](sdd/PATTERNS.md#p2-strategy-pattern-processador) Strategy Pattern via `*Processor` + `List<>` injetada
- [P3](sdd/PATTERNS.md#p3-event-driven-snssqs-fifo) Event-driven SNS/SQS FIFO + DLQ
- [P4](sdd/PATTERNS.md#p4-domínio-rico) Domínio rico (lógica no domínio, não em service)
- [P5](sdd/PATTERNS.md#p5-construção-via-construtor-factory-como-exceção) Construção via construtor (factory como exceção); agregados raiz com construtor público, internos package-private
- [P6](sdd/PATTERNS.md#p6-agregação-por-uuid) Agregação por UUID (nunca `@OneToMany`/`@ManyToOne` cross-context)
- [P7](sdd/PATTERNS.md#p7-idempotência-em-consumers-sqs-a-canonizar) Idempotência em consumers SQS via flag no agregado
- [P8](sdd/PATTERNS.md#p8-observabilidade-estruturada-a-canonizar) Observabilidade — JSON log + MDC + Micrometer + tracing
- [P9](sdd/PATTERNS.md#p9-error-handling--retry-a-canonizar) `@RestControllerAdvice` + ProblemDetail + retry/circuit-breaker em integrações
- [P10](sdd/PATTERNS.md#p10-estrutura-de-testes-pirâmide-a-canonizar) Pirâmide de testes (unit > integration > contract)
- [P11](sdd/PATTERNS.md#p11-transactional-no-nível-da-classe-em-applicationservices-a-canonizar--hoje-inconsistente) `@Transactional` no nível da classe em ApplicationServices

**Padrões obrigatórios — Convenções de código executáveis (P12-P16):**
- [P12](sdd/PATTERNS.md#p12-logs-estruturados-startfinish-em-todo-método-público) Logs `[start]`/`[finish]` em todo método público — formato `[marcador] NomeDaClasse - nomeDoMetodo`, `log.info` no start e `log.debug` no finish
- [P13](sdd/PATTERNS.md#p13-tamanho-de-método--meta-5-linhas-de-lógica-hard-cap-10) Tamanho de método — meta 5 linhas de lógica, hard-cap 10 (logs e return não contam; orquestradores delegando podem chegar a 10)
- [P14](sdd/PATTERNS.md#p14-documentação-swagger-via-apidocsinterface-não-inline) Documentação Swagger via `*APIDocs.@interface` em `src/main/java/.../docs/swagger/`
- [P15](sdd/PATTERNS.md#p15-validação-extraída-em-método-privado-nomeado) Validação em método privado nomeado (`validaTextoNaoVazio`, `validaConclusaoMissao`)
- [P16](sdd/PATTERNS.md#p16-resposta-http-via-responsestatus-não-responseentity) `@ResponseStatus(HttpStatus.X)` + retorno direto do DTO (nunca `ResponseEntity`)
- [P17](sdd/PATTERNS.md#p17-webmvctest-obrigatório-em-controllers-thymeleaf-e-rest) `@WebMvcTest` obrigatório em controllers (`@Controller` e `@RestController`) — cobertura mínima 99% inclui controllers
- [P18](sdd/PATTERNS.md#p18-logging-lgpd--metadata-only-para-conteúdo-pii) Logging LGPD: só metadata (hash + tamanho + ID) em logs com PII — nunca conteúdo
- [P19](sdd/PATTERNS.md#p19-defense-in-depth-em-in-memory-repositories) Defense-in-depth em in-memory repositories: cap + TTL + cleanup scheduler + remoção imediata pós-uso
- [P20](sdd/PATTERNS.md#p20-retryablerecover-testáveis-só-via-integration-test-spring-aop) `@Retryable`/`@Recover` testáveis só via integration test (Spring AOP) — não em test unit puro

**Padrões obrigatórios — AI / LLM (P21-P22):**
- [P21](sdd/PATTERNS.md#p21-ai-agent-portadapter) AI Agent Port/Adapter — necessidade de IA = port `*AIAgent` por propósito no subdomínio + adapter na infra que sobe seu próprio LLM (modelo via **constante** no adapter/temp/tokens), dono do system prompt + parse + retry. **Saída do LLM em JSON estruturado** (DTO ACL na infra, ex: `AnkiRespostaJsonMapper`), nunca magic-string em prosa. Kernel `ai/domain` (`MensagemChat`/`ChatHistory`/`RespostaIA`) reutilizável; mecânica compartilhada por **composição** (`LangChainMensagemMapper`). **Sem port genérico** de LLM; interface só no port do agente
- [P22](sdd/PATTERNS.md#p22-apiexception-no-domínio-é-permitida-decisão-deliberada) `APIException(HttpStatus)` no domínio é **permitida** (decisão deliberada: advisor enxuto, sem duplicar lógica de exceção) — não tratar como dívida

**Anti-patterns (A1-A12):**
- [A1](sdd/PATTERNS.md#a1-cascata-síncrona-em-concluimissao) Cascata síncrona em método transacional
- [A2](sdd/PATTERNS.md#a2-spring-eventlistener-síncrono-dentro-da-tx) `@EventListener` síncrono em TX (usar `AFTER_COMMIT`)
- [A3](sdd/PATTERNS.md#a3-notblanknotnull-em-entidades-de-domínio) `@NotBlank`/`@NotNull` em entidades de domínio
- [A4](sdd/PATTERNS.md#a4-dto-externo-vazando-para-application-layer) DTO externo vazando para application layer
- [A5](sdd/PATTERNS.md#a5-cache-manual-com-hashmap) Cache manual com `HashMap`
- [A6](sdd/PATTERNS.md#a6-deduplicationid--uuidrandomuuid-sem-idempotência-no-handler) `deduplicationId = UUID.randomUUID()` sem idempotência
- [A7](sdd/PATTERNS.md#a7-unsupportedoperationexception-ou-exceptions-java-padrão-em-adapters) `UnsupportedOperationException` (ou exception Java padrão) em adapter
- [A8](sdd/PATTERNS.md#a8-var-em-métodos-de-applicationdomainapi) `var` em método de application/domain/api
- [A9](sdd/PATTERNS.md#a9-responseentity-em-controllers) `ResponseEntity` em controller
- [A10](sdd/PATTERNS.md#a10-emoji-em-mensagens-server-side-exceptions-logs-response-bodies) Emoji em mensagem server-side (exception, log, response body)
- [A11](sdd/PATTERNS.md#a11-comentários-inline--explicando-passos) Comentário inline `// passo N` (substituir por método privado nomeado)
- [A12](sdd/PATTERNS.md#a12-instanciação-seguida-de-mutação-externa-new-entity-entityattr--xpto) Instanciação seguida de mutação externa (`new Entity(); entity.attr = xpto;`) — construtor recebe tudo + método comportamental

> **Exceção:** divergir desses padrões em uma spec só é aceitável via ADR explícito no `technical-spec.md` da feature, com justificativa do trade-off. Sem ADR, code review bloqueia.

## Dívida Arquitetural Conhecida (não replicar)

Identificada em `docs/arquitetura-gameficacao.md`. Em código novo, prefira os padrões opostos:

| Anti-pattern existente | Como fazer em código novo |
|------------------------|---------------------------|
| `TipoMissao` como tabela-enum sem polimorfismo | Use **Strategy Pattern** desde o início (interface + impls `@Component`) para qualquer "tipo" com comportamento próprio |
| Cascata síncrona em `MissaoProgressoApplicationService.concluiMissao()` | Fluxos com múltiplos passos: prefira **Outbox** ou eventos `AFTER_COMMIT` |
| Spring Event síncrono dentro da TX (ex: `XpPromocaoClasseEvent`) | Sempre `@TransactionalEventListener(AFTER_COMMIT)` em listeners de pós-processamento |
| `@NotBlank`/`@NotNull` em entidades de domínio | Validação de framework fica em DTOs de API. Domínio valida via construtor (ou método/factory quando exceção P5 aplica) |
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

Documentos técnicos e estratégicos do projeto estão em `docs/`:

| Arquivo | Tipo | Conteúdo |
|---------|------|----------|
| [`docs/architecture-haiku.md`](docs/architecture-haiku.md) | Estratégico | Descritivo do sistema + objetivos de negócio + restrições + atributos de qualidade priorizados + decisões arquiteturais. **Fonte de verdade pra trade-offs técnicos.** |
| [`docs/product-vision-board.md`](docs/product-vision-board.md) | Estratégico | Visão + Grupo-alvo + 6 Necessidades + 6 Diferenciais + 6 Metas + KPIs + economia interna de créditos. **Fonte de verdade pra decisões de produto.** |
| [`docs/arquitetura-gameficacao.md`](docs/arquitetura-gameficacao.md) | Técnico (design intencional histórico) | Arquitetura desejada de gamificação: XP, Classes, Missões, Jornadas, Trilhas, eventos. **§11 traz priorização de gaps derivada do Haiku.** ⚠️ **Não é mais atualizado** — `sdd/specs/` reflete o código atual. |
| [`docs/_extraction/strategic-context.md`](docs/_extraction/strategic-context.md) | Material-fonte | Modelagem Estratégica Fase 5 (DDD) + extração do drawio + cruzamento com contexto game-theory |

> Ao gerar nova documentação arquitetural por domínio, salvar em `docs/` seguindo o padrão `arquitetura-<dominio>.md` e fazer referência cruzada ao Haiku (qual atributo de qualidade compromete) e Vision Board (qual necessidade endereça).

## SDD Workflow

Este projeto adota o **SDD Kit** para evolução baseada em specs. Artefatos vivos em `sdd/`:

| Arquivo | Tipo | Conteúdo |
|---------|------|----------|
| [`sdd/PROJECT.md`](sdd/PROJECT.md) | Configuração | Visão + Target group + 7 Quality Attributes + 7 Domains + 4 Active Initiatives (Sistema de Créditos, TipoMissao Strategy, Plataforma de Agentes AI Tier 1+2, Hardening) + Non-Negotiables + Business Health + Bootcamp + 6 Open Strategic Questions |
| [`sdd/PATTERNS.md`](sdd/PATTERNS.md) | Padrões obrigatórios | 11 padrões obrigatórios (P1-P11, com exemplos canônicos extraídos do código) + 6 anti-patterns (A1-A6, com contraexemplos reais) + padrões de extensão. **Toda nova feature deve seguir.** |
| [`sdd/specs/functional-spec.md`](sdd/specs/functional-spec.md) | Fonte de verdade técnica viva | 31 casos de uso reais por domínio + 7 user journeys + cobertura VB (6 necessidades + 6 diferenciais) + 20 gaps catalogados. **Reflete código atual.** |
| [`sdd/specs/technical-spec.md`](sdd/specs/technical-spec.md) | Fonte de verdade técnica viva | Arquitetura completa: 7 bounded contexts + 22 ApplicationServices + 16 entidades + 67 migrations + 9 SNS topics + 11 SQS queues + 6 integrações + 12 dívidas §11 + 17 gaps novos. **Reflete código atual.** |
| [`sdd/DETECTION_REPORT.md`](sdd/DETECTION_REPORT.md) | Auditoria | Histórico de execuções do reverse-eng + findings críticos consolidados |
| [`sdd/extracted/raw/`](sdd/extracted/raw/) | Material bruto | 6 arquivos com deep-dive técnico por domínio (architecture, application-spec, domain-model, data-schema, events-messaging, webhooks). **Referência para enriquecer specs sob demanda.** |
| `sdd/backlog.md` | Backlog | Active Initiatives + Open Strategic Questions + 17 gaps novos descobertos no reverse-eng |

**Hierarquia de fontes:**
1. `sdd/PROJECT.md` + `sdd/PATTERNS.md` — regras vivas do projeto
2. `sdd/specs/` — snapshot do código atual (verdade viva)
3. `docs/architecture-haiku.md` + `docs/product-vision-board.md` — norte estratégico (estável)
4. `docs/arquitetura-gameficacao.md` — design intencional histórico (não atualizar mais; usar `sdd/specs/` para verdade)

**Para começar uma feature nova:**
```
/sdd.start "WAI-XX descricao-da-feature"
/sdd.spec          # gera functional + technical spec da feature
/sdd.plan          # quebra em tasks
/sdd.build         # implementa com quality gates
/sdd.finish        # arquivar
```

O kit puxa contexto automático de `PROJECT.md` + `PATTERNS.md` + `sdd/specs/`.

## Git — Conta Pessoal

Este repositório usa a conta pessoal `vinireis` (GitHub pessoal). A identidade está configurada localmente:
```bash
git config user.name   # → Vinicius Reis
git config user.email  # → vinicius.reis@msn.com
git remote -v          # → git@github.com-personal:tribos-dev/wakanda-ai.git
```

---

# SDD Kit (Generic Framework)

Este projeto usa o **SDD Kit** para desenvolvimento orientado a especificações. Os comandos `/sdd.*` estão disponíveis no Claude Code.

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

**Quando criar uma feature nova:**
```
/sdd.start "WAI-XX descricao-da-feature"
/sdd.spec
/sdd.plan
/sdd.build
/sdd.finish
```

@sdd-kit/CLAUDE.md
