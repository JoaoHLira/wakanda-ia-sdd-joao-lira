# Project Configuration — WakandaAI

> **Versão:** v1 (integrado com contexto game-theory) · **Data:** 2026-05-27
> **Status:** Aguardando aprovação final do Vis antes de rodar `/sdd.reverse-eng`
> **Fontes:** [`docs/architecture-haiku.md`](../docs/architecture-haiku.md), [`docs/product-vision-board.md`](../docs/product-vision-board.md), [`docs/arquitetura-gameficacao.md`](../docs/arquitetura-gameficacao.md), [`CLAUDE.md`](../CLAUDE.md), [`game-theory/dominios/wakanda/jogos/wakanda-ai/`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/jogos/wakanda-ai/)

> **Contexto temporal:** Pivot operacional Wakanda 27/abr/2026 — saiu do `minimum-effort-principle` para **laboratório ativo Fleury+AI**. Bootcamp piloto "Trilha do Sábio Java+AI" inicia **28/mai/2026**.

---

## Project Vision

```yaml
vision:
  summary: |
    Plataforma operacional da edtech Wakanda Academy — escala empregabilidade de programadores juniors
    do Norte/Nordeste/periferia via gamificação narrativa (Wakanda + 5 Sabedorias Fleury), AI e
    comunidade que ensina e é recompensada.

  target_users:
    - "Aspirante a programador: buscando ingressar no mercado de tecnologia com empregos bem remunerados"
    - "Programador em início de carreira: empregado ganhando <R$ 4.000, sente falta de crescimento"
    - "Wakander avançado/mentor: quer ter voz, contribuir e ganhar créditos pela contribuição"
    - "Red Wakandas (operação): Jefferson + Esténio orquestram cohorts, Vis arquiteta (5h/sem)"

  demographics:
    primary: "Homens negros 20-35 anos, Norte / Nordeste / periferia"
    growing_vector: "Mulheres e LGBTQIA+"

  value_proposition: |
    Transforma o paradoxo "sem emprego pq sem competência; sem competência pq sem emprego" em jornada
    gamificada que entrega: (1) competência prática, (2) experiência via clãs simulados, (3) suporte
    em processos seletivos, (4) economia de créditos circular que recompensa quem ensina a comunidade.
    Em paralelo, valida o framework Fleury+AI como ativo intelectual long-term.

  principles:
    - "Trade-off triplo (L3): toda automação responde 3 perguntas (UX × Impacto Comunitário × Viabilidade) — sem categorias proibidas de AI"
    - "Tom Wakanda: 'não nos levamos tão a sério' — automações carregam intensidade com leveza, empatia com diversidade"
    - "Toda integração externa nasce com ACL: DTO externo nunca toca application layer"
    - "Modelo Fleury & Fleury: subida de Classe exige equilíbrio nas 5 Sabedorias, não acúmulo bruto"
    - "Extensibilidade primeiro, performance depois: código que não escala 200→2000 é problema futuro; código que não estende é problema HOJE"

  anti_goals:
    - "Não substituir mentor humano onde os 3 critérios do trade-off pedem humano"
    - "Não dogmatizar 'AI proibida em X' — análise caso a caso"
    - "Não otimizar performance prematuramente — gargalo declarado primeiro"
    - "Não bancar features que custam mais que o LTV do Wakander que beneficiam"
```

---

## Quality Attributes (ordem de prioridade — confirmada pelo Vis em L1)

> **Extensibilidade > Mantenibilidade > Disponibilidade > Observabilidade > Segurança > Performance > Escalabilidade**

| # | Atributo | Justificativa (do Haiku) |
|---|----------|---------------------------|
| 1 | **Extensibilidade** | Roadmap 4 fases declaradas. Tipos novos de missão (Anki, Quiz, Projeto, Espontânea) são previsíveis. Adicionar tipo NÃO PODE exigir mexer em código existente |
| 2 | **Mantenibilidade** | Vis tem 5h/sem. Código difícil mata operação. Convenções consistentes (Ports/Adapters, Strategy) são requisito permanente |
| 3 | **Disponibilidade** | Wakander estuda fora de horário comercial — sistema fora = engajamento perdido |
| 4 | **Observabilidade** | Fluxos event-driven com 3-4 hops (missão→XP→classe→whatsapp) só são debugáveis com tracing |
| 5 | **Segurança** | LGPD + economia de créditos + LinkedIn futuro. Top-5 mas não top-3 |
| 6 | **Performance** | 200+ Wakanders hoje, picos previsíveis. Otimização sob dor declarada |
| 7 | **Escalabilidade** | Crescimento 5×–10× é hipótese. Escalar via processo+AI antes de hardware |

---

## Domains (7 bounded contexts + módulos de suporte)

| # | Domínio | Arquivos | LoC | Tipo (DDD) | 1-line resumo |
|---|---------|----------|-----|------------|----------------|
| 1 | **gameficacao** | 154 | 7.226 | CORE | Trilhas, Jornadas, Missões, XP, Classes, Progressão. 3 sub-contextos: catálogo (suporte), progresso (core), xp (core) |
| 2 | **wakander** | 42 | 2.720 | CORE | Cadastro central do Wakander + VOs embedded (Contato, Financeiro, Fiador, AulaAssistida) |
| 3 | **jornadawakander** | 48 | 1.673 | CORE | Onboarding orquestrador entre contextos + aulas assistidas + 4 schedulers |
| 4 | **financeiro** | 34 | 1.434 | SUPPORT | Asaas integration + Cobrança + Assinaturas. **Pré-requisito do Sistema de Créditos** |
| 5 | **comunicacao** | 37 | 1.397 | SUPPORT | 12 SQS listeners + Z-API (WhatsApp) + Discord (JDA) + processadores |
| 6 | **autenticacao** | 24 | 698 | CORE | JWT (Auth0) + Spring Security + UsuarioAdm |
| 7 | **config** | 8 | 467 | SUPPORT | AWS config, Discord config, Spring Security config, Swagger |

> **Bounded contexts a criar** (do Haiku §Componentes):
> - **#8 Economia de Créditos** — saldo, transações, marketplace de mentoria (P0, novo)
> - **#9 AI Coach** — sessões LLM, personalização, recomendação (Fase 3)

---

## Active Initiatives Q3–Q4 2026

> Derivadas das decisões do Vis em 2026-05-27. Todas as 4 entram no roadmap como P0/P1.
> **Ordem de execução:** a definir APÓS reverse-eng profundo, quando teremos dados reais por domínio (gaps descobertos podem antecipar hardening, ou expor que `financeiro` está mais maduro do que esperado, mudando precedência do Sistema de Créditos).

### 1. Sistema de Créditos — P0
- **Bloqueia:** O1 (receita) + modelo de monetização circular do VB §Diferencial #6
- **Atributo afetado:** Não é gap atual, é feature nova — afeta Extensibilidade (novo bounded context bem desenhado)
- **Pré-requisitos:** `financeiro` deep-dive (entender Asaas E2E); decisão LGPD sobre transações de mentoria
- **Esforço:** XL (novo bounded context)

### 2. TipoMissao polimórfico (Strategy Pattern) — P0
- **Bloqueia:** Extensibilidade #1 (atributo prioritário) + Fase 2 do roadmap
- **Atributo afetado:** Extensibilidade comprometida — hoje `TipoMissao` é tabela-enum, novos tipos exigem refactor
- **Pré-requisitos:** Nenhum — escopo bem conhecido
- **Esforço:** M (refactor `MissaoConfig` JSONB + Strategy interfaces)
- **Owner:** João Lira (tech ref) · **Timeline:** ~jul/2026

### 3. Plataforma de Agentes AI (Tier 1 + Tier 2) — substituiu "AI Coach genérico"
> 12 candidatos consolidados em `brainstorm-problemas-wakanda.md`. Cada um vira candidato a `/sdd.start` separado após reverse-eng.

**Decisão Agentes de IA (P21, atualizado 08-06):** cada necessidade de IA = port `*AIAgent` por propósito no subdomínio + adapter na infra que sobe seu próprio LLM (modelo via **constante** no adapter / temperatura / tokens) e é dono do system prompt. **Sem port genérico** (`LLMProvider` foi removido — encapsulava o builder do LangChain4j, que já é abstração limpa). SDK confinado nos adapters; kernel `ai/domain` (`MensagemChat`/`ChatHistory`/`RespostaIA`) reutilizável por composição. **Saída do LLM em JSON estruturado** (DTO ACL na infra), não CSV/magic-string em prosa — ver TD-6 + [P21](PATTERNS.md#p21-ai-agent-portadapter).

> **Roadmap de persistência (memória de sessão iterativa):** hoje a `SessaoIterativa` do Anki Coach vive **in-memory** (`ConcurrentHashMap` com TTL 30min + cleanup scheduler + cap 100), por decisão consciente do MVP (sem persistir transcrição/cards — LGPD). Quando uma feature precisar de memória durável de conversa (multi-dispositivo, retomar sessão, analytics), o caminho é **coluna `jsonb`** no Postgres (serializar `ChatHistory`), seguindo o padrão de polimorfismo JSONB já usado em gamificação — não um novo store.

**Tier 1 — mai-set/2026 (8 agentes, ataque imediato a dores + foundation didática)**

| # | Agente | Dor que ataca | Bloqueia |
|---|--------|---------------|----------|
| **T1.0** | **Anki Coach** (didático + foundation) — agente que gera deck Anki CSV a partir de transcrição de aula | Bootcamp Encontro 1 (28/mai) precisa material vivo + projeto precisa do baseline do pattern AI Agent (P21) | Estabelece o pattern AI Agent (P21) reutilizado por T1.1–T2.5; valida OSQ-1 |
| T1.1 | Coleta Jornada da Clareza estruturada (conversacional) | Hoje é Google Forms; perfil rico inexistente | Personalização downstream + AI Coach |
| T1.2 | **Régua cobrança preventiva (D-3) + recuperação (D+3)** | 8 inadimplentes em 68 pagantes → ROI imediato R$ | Receita (O1) |
| T1.3 | Tutor AI Jornada Conhecimento (RAG curriculum) | Mentor humano saturado em tier 1 | Disponibilidade #3 + escala |
| T1.4 | Suporte tier 1 (FAQ) | Same — libera mentor para Codificação | Disponibilidade #3 |
| T1.5 | Orquestração onboarding pós-compra | Tempo compra→1ª aula longo → churn | Mantenibilidade + retenção |
| T1.6 | Detector Wakander travado (churn prevention) | Churn 2-3/mês sem intervenção preventiva | Disponibilidade comunidade |
| T1.7 | Avaliador currículo + otimização LinkedIn | Quick win Conquista (alavanca empregabilidade) | Diferencial #5 VB |

**Tier 2 — set/26-Q1/27 (5 agentes, valor estrutural)**

| # | Agente | Dependência |
|---|--------|-------------|
| T2.1 | Simulador entrevista técnica (voz + feedback) | Audio infra + LLM com voz |
| T2.2 | Avaliação semântica código (GitHub API + LLM) | Decisão plataforma desafios (HackerRank vs Coodesh vs build) |
| T2.3 | AI Coach genuíno (lê histórico + XP + perfil) | T1.1 (coleta Clareza) + dados acumulados de XP |
| T2.4 | Multiagentes recrutamento offshore (sourcing + screening + scoring) | Modelo B2B (camada 3 latente) |
| T2.5 | Ranqueador participação + código (GitHub + canais) | T2.2 (avaliação semântica) |

**Atributos afetados:** Extensibilidade #1 (novos agentes não devem exigir refactor) + Observabilidade #4 (LLM calls precisam tracing) + Segurança #5 (perfil comportamental é PII)
**Esforço total:** XL — entrar agente por agente via `/sdd.start`, não como big-bang

### 4. Hardening de dívidas (alinhado com `arquitetura-gameficacao.md §11`)
- **Bloqueia:** Disponibilidade #3 + Mantenibilidade #2 + Observabilidade #4
- **Sub-itens P1 — Confiabilidade (Sprint 2 do §11):**
  - §11.3: Cascata síncrona em `MissaoProgressoApplicationService.concluiMissao()` → Outbox/AFTER_COMMIT
  - §11.4: `XpPromocaoClasseEvent` síncrono na TX de XP → `@TransactionalEventListener(AFTER_COMMIT)`
  - §11.5: Distributed tracing ausente → OpenTelemetry + AWS X-Ray + correlationId no MDC
  - §11.6: ACL frágil em Z-API/Discord → adapter + DTO interno (P2 em §11, mas eleva por Mantenibilidade #2)
  - §11.8: Idempotência ausente em consumers SQS críticos → checar estado no handler (P7 do PATTERNS.md)
- **Sub-itens P2/P3 — Higiene (Sprint 0/4 do §11):**
  - §11.7: `@Transactional` inconsistente → padronizar nível de classe (P11 do PATTERNS.md)
  - §11.10: Bean Validation no domínio → mover para DTO de API (A3 do PATTERNS.md)
  - §11.11: Cache manual com `HashMap` → `@Cacheable` com TTL (A5 do PATTERNS.md)
- **Funcionalidade-gap não-pattern:**
  - §11.9: Sem `XPMinimoPorTipo` em `LiberacaoMissao` → promoção quantitativa hoje, não multidimensional (regra de negócio Hierarquia Wakandana incompleta)
- **Esforço total:** L (8 sub-tickets de S/M cada — Sprint 0 são quick wins de 1 PR)

---

## Non-Negotiables (princípios operacionais)

1. **Trade-off triplo** (L3 do Vis) — toda automação responde:
   1. Melhor experiência para o Wakander?
   2. Maior impacto para a comunidade?
   3. Viável (custo, capacidade, complexidade)?
   Se as 3 batem → faz com AI. Se 1 não bate claramente → repensa, humano cobre, ou híbrido.

2. **Tom Wakanda** — "não nos levamos tão a sério". Toda automação (prompts, mensagens, fluxos) carrega tom acolhedor, intensidade com leveza, empatia com diversidade.

3. **ACL em toda integração externa** — DTO externo (Memberkit, Asaas, Z-API, Discord) NUNCA toca application layer. Adapter traduz no infra → DTO interno do domínio.

4. **Domínio rico** — lógica de negócio em métodos do domínio. Services orquestram. Nunca o oposto.

5. **Extensibilidade > Performance** — código que não estende é problema HOJE; código que não escala é problema futuro.

6. **Anti-prescriptive operacional** — "Todo mundo é adulto. Quem faz ganha, quem não faz perde." Sem cobrança, sem juramento, sem gates, sem expulsão. Porta de saída sempre aberta — é a garantia. Aplica-se em todo touchpoint: cobrança, mentoria, avaliação de missão, comunicação WhatsApp.

7. **Convenções de código executáveis (P12-P16, A7-A11)** — toda spec daqui em diante deve respeitar os padrões de PATTERNS.md `P12-P16` (logs `[start]/[finish]`, método ≤ 5 linhas de lógica/cap 10, Swagger via `*APIDocs.@interface`, validação privada nomeada, `@ResponseStatus`) e evitar `A7-A11` (`UnsupportedOperationException` em adapters, `var`, `ResponseEntity`, emoji server-side, comentário inline). Code review **bloqueia merge** em violações. Specs feitas via `/sdd.spec` herdam essas regras automaticamente — divergência precisa de ADR explícito no spec.

---

## Platform Configuration

```yaml
platform:
  type: backend          # backend Java + Spring Boot
  context_path: /wakanda-ai/api
  package_root: academy.wakanda.wakanda_ai
```

---

## Technology Preferences

```yaml
preferences:
  language: java
  language_version: "17"
  framework: spring-boot
  framework_version: "3.3.4"
  build: maven-wrapper

  # Persistência
  orm: jpa                      # Hibernate via Spring Data JPA
  database: postgresql          # ← decisão CONFIRMADA; MongoDB descartado
  migrations: flyway
  connection_pool: hikari       # pool 15-50

  # Mensageria
  messaging: aws-sns-sqs        # FIFO sempre, com DLQ
  messaging_lib: spring-cloud-aws-3.2.1

  # Auth
  auth: jwt-auth0
  security: spring-security

  # Frontend operacional
  frontend: thymeleaf           # dashboards admin / Game Masters

  # Discord
  discord_lib: jda-5.3.0

  # Testes
  testing_framework: junit5
  testing_libs:
    - mockito
    - wiremock
    - fixturefactory
    - jacoco
    - testcontainers           # quando precisar de PG real em testes
  local_aws_emulator: localstack

  # Logging / Observabilidade (a CANONIZAR)
  logging: slf4j
  log_format: json              # estruturado, com MDC (correlationId, wakanderId)
  metrics: micrometer
  tracing: distributed          # OpenTelemetry / Spring Sleuth — a definir

  # AI Stack (decidido 2026-05-27, refinado 2026-05-27 com T1.0 anki-coach)
  ai_orchestration: langchain4j           # Java-native, suportado pela Trilha Java+AI
  ai_provider_pluggable: [openai, anthropic]  # providers plugáveis por adapter de agente (P21); sem port genérico
  ai_provider_active_mvp: openai          # OpenAI ativo no MVP (já pago); Anthropic preparado
  ai_rag: a-definir                       # vector DB + retriever (pós-T1.3 Tutor AI)
  ai_mcp: planned                         # Multi-Component Protocol — Fase 2 do bootcamp

lombok_usage:                   # Lombok É usado — anotações permitidas
  permitted:
    - "@Getter"                 # geração de getters
    - "@NoArgsConstructor"      # com AccessLevel.PRIVATE em entidades de domínio
    - "@AllArgsConstructor"
    - "@RequiredArgsConstructor"  # em services/repositories para injeção via final
    - "@Log4j2"                 # logger padrão (log.info/debug)
    - "@Embeddable"             # JPA Value Objects
  forbidden:
    - "@Data"                   # gera setters — viola imutabilidade do domínio rico
    - "@Builder"                # encoraja construção fora de factories validadoras
    - "@Setter"                 # mesmo motivo do @Data

forbidden_dependencies:
  - mongodb                     # decisão consolidada — PostgreSQL é único banco
  # OpenAI direto NÃO é forbidden — SDK confinado nos adapters *AIAgent em <subdominio>/infra + mapper em ai/infra/langchain (P21)
```

---

## Technology Decisions

> Decisões arquiteturais consolidadas durante implementações. Cada uma tem rationale + classe canônica no código. Atualizar conforme novas decisões nascerem.

```yaml
technology_decisions:
  - id: TD-1
    date: 2026-05-28
    feature: anki-coach (T1.0)
    decision: Spring Retry escolhido sobre Resilience4j para retry exponencial em adapters de integração externa
    rationale: |
      Mais leve (transitive via spring-boot-starter-aop). @Retryable + @Recover declarativos.
      Suficiente para retry simples (3× backoff exponencial). Resilience4j fica para T2.x
      quando precisar de circuit breaker / rate limiter / bulkhead.
    canonical: OpenAiGeradorDeckAIAgent.java + OpenAiGeradorDeckIterativoAIAgent.java (anki-coach T1.0, @Retryable/@Recover por adapter de agente)
    related_pattern: P20 (Retryable/Recover testáveis só via integration test) + P21 (AI Agent)

  - id: TD-2
    date: 2026-05-28
    feature: anki-coach (T1.0)
    decision: LangChain4j 0.36.0 validado com Spring Boot 3.3.4
    rationale: |
      Compatibilidade confirmada via dependency:tree (sem conflito). Versão estável (mai/2026).
      O SDK fica confinado nos adapters de agente (<subdominio>/infra) e no mapper compartilhado
      (ai/infra/langchain) — zero imports dev.langchain4j.* em application/ ou domain/ (P21).
      Quando subir versão LangChain4j, o impacto fica restrito aos adapters + mapper.
    canonical: OpenAiGeradorDeckAIAgent.java + LangChainMensagemMapper.java

  - id: TD-3
    date: 2026-05-28
    feature: anki-coach (T1.0)
    decision: Wrapper ./mvnw17 para forçar JAVA_HOME=openjdk@17 em máquinas dev com Java 21+
    rationale: |
      Lombok 1.18.34 tem annotation processor parcial em Java 21+ — Java 23 (Homebrew default)
      quebra build em arquivos específicos com @Log4j2/@Getter. Wrapper transparente força Java 17
      sem mexer no shell global do dev. Quando Lombok subir para 1.18.38+, wrapper pode ser removido.
    canonical: ./mvnw17 (root do repo) + CLAUDE.md §"Setup Local"

  - id: TD-4
    date: 2026-05-28
    feature: anki-coach (T1.0)
    decision: '@Qualifier("openai" | "anthropic") como padrão de seleção de provider LLM'
    rationale: |
      2 beans ChatLanguageModel sempre ativos no Spring context (sem @ConditionalOnProperty).
      Consumer (ApplicationService) escolhe explicitamente via @Qualifier no construtor —
      grep por @Qualifier("openai") localiza imediatamente todos os pontos de uso. Em T1.x
      quando Anthropic for implementado de verdade, troca-se 1 linha por consumer.
    canonical: AnkiCoachInstantaneoApplicationService.java + AnkiCoachIterativoApplicationService.java
    related_pattern: P5 (Construção via construtor)

  - id: TD-5
    date: 2026-05-28
    feature: anki-coach (T1.0)
    decision: lombok.config com lombok.addLombokGeneratedAnnotation=true (raiz do repo)
    rationale: |
      JaCoCo (com plugin 0.8.12+) exclui automaticamente métodos/construtores marcados com
      @lombok.Generated do report de cobertura. Sem essa flag, getters/setters/construtores
      gerados por Lombok contam como "linhas descobertas" inflando cobertura artificial
      ou bloqueando atingir 99% no padrão P17. Solução = 1 arquivo de 4 linhas na raiz.
    canonical: lombok.config (root do repo)

  - id: TD-6
    date: 2026-06-08
    feature: anki-coach (T1.0 — revisão pós-code-review)
    decision: Saída do LLM como JSON estruturado (via responseFormat json_object) com DTO ACL na infra, em vez de CSV/magic-string em prosa
    rationale: |
      O contrato anterior pedia CSV em prosa e (no iterativo) sinalizava o fim com a string mágica
      "GERANDO CARDS...", exigindo parse defensivo de texto. Migrado para JSON: o provider devolve
      {"cards":[...]} ou {"pergunta":"..."}; a presença de "cards" decide o ramo. Desserialização via
      Jackson + DTOs AnkiCardJson/AnkiRespostaJson (ACL na infra do subdomínio, domínio não os conhece).
      O CSV final (importável pelo Anki) continua sendo gerado server-side via jackson-dataformat-csv
      no AnkiCsvFormatter — o contrato da API REST (campo csv) não mudou. Modelo do LLM passou a ser
      constante no adapter (não @Value global); api-key/base-url ficam no yml sem default no @Value.
    canonical: AnkiRespostaJsonMapper.java + AnkiCardJson.java + AnkiCsvFormatter.java
    related_pattern: P21 (AI Agent — padrão de saída JSON) + A4 (ACL)
```

---

## Branching Strategy

```yaml
branching:
  model: simple-flow            # fluxo simplificado para projeto pessoal open-source
  base_branch: main
  branch_pattern: "{type}/WAI-{number}-{descricao}"
  types:
    - feat                      # nova funcionalidade
    - fix                       # bug fix
    - refactor                  # refatoração sem mudança de comportamento
    - chore                     # housekeeping, infra, docs
    - release                   # preparação de release
  pr_target: main
  protected: [main]
```

---

## Team Conventions

```yaml
language:
  specs: pt                     # Português Brasil
  comments: pt                  # comentários em PT (alinhado com cultura Wakanda)
  code_identifiers: pt          # nomes de classes/métodos em PT (Wakander, Jornada, Missao)

naming:
  feature_prefix: "WAI-"        # Wakanda AI ticket prefix
  branch_pattern: "feat/WAI-{number}-{descricao}"

communication:
  discord_server: "Wakanda Academy"
  documentation_url: "docs/"

git_identity:
  account_type: personal        # vinireis (conta pessoal GitHub)
  email: vinicius.reis@msn.com
  remote: "git@github.com-personal:tribos-dev/wakanda-ai.git"
```

---

## Quality Gates

```yaml
coverage:
  min_coverage: 80              # JaCoCo target
  critical_paths_only: false
  current_coverage: 21          # ← gap reconhecido (82/378 classes)

reviews:
  code_review: mandatory
  spec_approval: mandatory      # specs SDD aprovadas antes de build

  security_review_for:
    - auth                      # autenticacao
    - payments                  # financeiro + créditos
    - pii                       # wakander (Jornada da Clareza)
    - lgpd                      # R7 do Haiku

  dba_review_for:
    - schema_changes            # Flyway migration
    - migrations
    - jsonb_columns             # @MissaoConfig JSONB para polimorfismo
```

---

## Default Feature Settings

```yaml
defaults:
  project_type: production      # MVP em produção com 200+ Wakanders formados
  execution_strategy: sequential
  user_profile: technical       # Vis é arquiteto sênior; Red Wakandas são técnicos
  ltp_enabled: false            # E2E formal não-existente ainda — fica para roadmap
```

---

## Team Info

```yaml
team:
  name: "Wakanda Academy core team"
  founder: "Vinicius Reis (vinireis) — arquiteto + figura pública"
  operation: "Jeferson (Head Ops) + Jusci (CEO) + Champions"
  capacity:
    vis: "3-4h/sem em arquitetura/estratégia (revisado mai/26; antes 5h)"
    operacao: "Jeferson + Jusci — operação principal (onboarding, cobrança, comunicação)"
  champions:                    # Wakanders avançados que contribuem (substituem mentor humano cedo demais)
    - nome: "Lua"
      papel: "Co-mentora pública"
    - nome: "Igor Alves"
      papel: "Especialista LinkedIn + entrevistas (mentor 1:1 — APENAS via créditos)"
      modelo_remuneracao: "Por trabalho realizado (sem custo extra), Wakander acessa só via créditos ganhos"
    - nome: "João Lira"
      papel: "Tech ref (responsável pelo refactor TipoMissao Strategy ~jul/2026)"
  equity_status: "INDEFINIDA — pausada até provar escala (MRR R$22k+). Risco: ambiguidade Jeferson/João Lira/Jusci"
  philosophy: "Edtech bootstrapped — cada decisão arquitetural precisa ser barata de testar"
```

---

## Registered Overrides

```yaml
overrides:
  - standard: testing-strategy.md
    rule: "Minimum coverage 80%"
    project_value: 21
    reason: "MVP em produção com cobertura legada; meta de 80% é alvo, não bloqueador imediato"
    registered_at: 2026-05-27
    registered_by: vinireis

  # (override sobre gitflow corporativo removido — projeto open-source usa simple-flow nativamente)
```

---

## Business Health (estado mai/2026)

> Dado interno — não publicado. Justifica priorização de **T1.2 Régua de Cobrança** (alto ROI imediato).

```yaml
financial:
  mrr: 17000                    # R$ — Monthly Recurring Revenue (mai/26)
  monthly_cost: 17000           # R$ — operação + infra + Champions
  net_today: 0                  # lucro zero
  pagantes_efetivos: 60
  inadimplentes: 8              # 11.7% inadimplência
  total_assinantes: 68
  ticket_atual: "R$275/mês"     # mensalidade Wakander
  ticket_externo_bootcamp: "R$500 onboarding + R$275/mês"  # piloto Trilha do Sábio Java+AI

targets:
  break_even: "MRR R$18.000 (+4 Wakanders efetivos)"
  vis_get_4k_renda: "MRR R$22.000 (+20 Wakanders efetivos)"

hypotheses_to_validate:
  - id: H1
    desc: "Régua de cobrança automatizada reduz inadimplência de 8 → <3"
    blocker: "Implementar T1.2 (Tier 1 Agente #2)"
  - id: H2
    desc: "Cobrança preventiva (D-3) reduz churn de assinatura"
    blocker: "Coleta de motivos de não-pagamento (T1.1 + T1.6)"
  - id: H3
    desc: "Automação onboarding reduz churn primeiros 30 dias"
    blocker: "T1.5 Orquestração onboarding"
```

---

## Bootcamp "Trilha do Sábio Java+AI" (laboratório ativo Fleury+AI)

> Contexto operacional — alimenta funil + valida hipóteses pedagógicas que retroalimentam a plataforma.

```yaml
bootcamp:
  nome: "Trilha do Sábio Java+AI"
  status: "Piloto — início 28/mai/2026"
  participantes: 40             # ~40 Wakanders (Workshop 17/mai + externos)
  duracao: "14-18 semanas (sequência pedagógica, sem datas fixas)"
  recorrencia: "Terças 18:30-20:30"

  fases:
    - id: 1
      escopo: "E1-E3 — Vocabulário LLM + Vibe Coding (N8N, Lovable)"
      saida: "Anki Coach vivo (primeiro agente AI funcional)"
    - id: 2
      escopo: "E4-E8 — Bootcamp Java + LangChain4j + RAG + MCP + multiagentes"
      saida: "Integração ao wakanda-ai-dev (este repositório)"
    - id: 3
      escopo: "E9-E14 — Clã Startup Weekend"
      saida: "Squads constroem 1 dos 12 agentes; retro coletiva INTERNA (Demo Day cancelado)"
      recompensa: "Squad com maior impacto ganha créditos"
    - id: 4
      escopo: "E15+ — Igor coaching + processos seletivos REAIS"
      saida: "Acompanhamento contínuo até assinar CLT"

  privacidade_estrategica: "Wakanda permanece privada externamente durante piloto até estabilização"
  diferencial_intransferivel: "Único bootcamp Java+AI que PAGA você para estagiar enquanto aprende, com Champions empregados ao lado, e acompanhamento contínuo até CLT"
```

**Impacto no projeto wakanda-ai-dev:**
- Wakanders do bootcamp Fase 2 contribuem código real (PR review, refactors, novos agentes)
- João Lira refactora TipoMissao (~jul/2026) como exercício pedagógico + dívida real
- Bootcamp valida hipóteses Fleury+AI que viram conteúdo do framework long-term (O6 Haiku)

---

## Open Strategic Questions

> Decisões em aberto que afetam o roadmap. Não bloqueiam reverse-eng do código atual, mas devem ser respondidas conforme aparecem em features SDD.

| # | Pergunta | Quando precisa decidir | Owner |
|---|----------|------------------------|-------|
| ~~OSQ-1~~ ✅ DECIDIDO 2026-05-27, refinado 2026-06-01 (P21) | **Pattern AI Agent**: port `*AIAgent` por propósito no subdomínio + adapter na infra que sobe seu LLM. Kernel `ai/domain` reutilizável; SDK confinado nos adapters + `ai/infra/langchain`. (O port genérico `LLMProvider` da decisão original foi removido — ver P21.) Materializado pela T1.0 anki-coach. | — | Vis + João Lira |
| OSQ-2 | **Métrica para validar se automação está matando a "alma Wakanda"** (engajamento de Champions, tom de respostas, satisfação NPS) | Antes de lançar T1.4 (Suporte tier 1) | Vis + Jeferson |
| OSQ-3 | **Equity Wakanda: quando MRR atingir R$22k+, quem tem direito ao upside?** Jeferson, João Lira, Jusci, Champions ativos? | Quando MRR atingir R$20k (proximidade do gatilho) | Vis (founder) |
| OSQ-4 | **Como medir se "AI gera quick wins → engenharia reversa de entendimento" funciona** vs. virar muleta? Gateway de validação no bootcamp? | Durante bootcamp Fase 2 (E4-E8) | Vis + João Lira |
| OSQ-5 | **Timing do pivô B2B** (empregadores pagam pipeline de Wakanders) — ainda especulativo (Camada 3 latente). Quando ativar? | Pós-validação framework Fleury+AI (Q4/26 ou Q1/27) | Vis |
| OSQ-6 | **Sistema de Créditos: subdomínio próprio (bounded context #8) vs. parte do contexto XP?** Haiku lista como decisão pendente. Subdomínio próprio = mais limpo conceitualmente, mas precisa de mais código. Parte de XP = aproveita estrutura, mas mistura "ganhar XP" com "ganhar/gastar moeda". | Antes de `/sdd.start` da feature "Sistema de Créditos" | Vis + João Lira |

---

## Referências

### Documentação técnica/estratégica deste repo
- 📘 [`docs/architecture-haiku.md`](../docs/architecture-haiku.md) — atributos de qualidade, restrições, decisões arquiteturais, princípios operacionais
- 📗 [`docs/product-vision-board.md`](../docs/product-vision-board.md) — visão, grupo-alvo, 6 necessidades, 6 diferenciais, economia de créditos, KPIs
- 📕 [`docs/arquitetura-gameficacao.md`](../docs/arquitetura-gameficacao.md) — análise técnica profunda do domínio gamificação (canônico de design intencional)
- 📓 [`docs/_extraction/strategic-context.md`](../docs/_extraction/strategic-context.md) — material-fonte (Modelagem Fase 5 DDD, drawio, sessões prévias)
- 📙 [`CLAUDE.md`](../CLAUDE.md) — guia de navegação + convenções de código + dívida arquitetural reconhecida

### Contexto estratégico externo (game-theory)
- 🎲 [`game-theory/dominios/wakanda/INDEX.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/INDEX.md) — mapa do domínio Wakanda no GT
- 🎲 [`game-theory/dominios/wakanda/jogos/wakanda-ai/wakanda-ai.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/jogos/wakanda-ai/wakanda-ai.md) — jogo central
- 🎲 [`game-theory/dominios/wakanda/contexto/repo-wakanda-ai-dev.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/repo-wakanda-ai-dev.md) — contexto direto sobre este repo
- 🎲 [`game-theory/dominios/wakanda/jogos/wakanda-ai/artefatos/brainstorm-problemas-wakanda.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/jogos/wakanda-ai/artefatos/brainstorm-problemas-wakanda.md) — 12 agentes priorizados
- 🎲 [`game-theory/dominios/wakanda/jogos/wakanda-ai/subjogos/formacao/metodologia/INDEX.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/jogos/wakanda-ai/subjogos/formacao/metodologia/INDEX.md) — metodologia bootcamp Trilha do Sábio
- 🎲 [`game-theory/dominios/wakanda/personas/`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/personas/) — Wakander Primeiro Emprego + Wakander Champion (com casos reais)
