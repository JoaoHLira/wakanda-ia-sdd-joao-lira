# Architecture Haiku — WakandaAI

> **Versão:** 1.0 (aprovada 2026-05-24)
> **Equipe geradora:** Eric Evans (orquestrador) + Arquiteto de Integração, de Dados, de Nuvem, de Segurança, de Infraestrutura + Designer de Negócios
> **Fontes:** [`product-vision-board.md`](product-vision-board.md), [`arquitetura-gameficacao.md`](arquitetura-gameficacao.md), [`_extraction/strategic-context.md`](_extraction/strategic-context.md), [`game-theory/dominios/wakanda/contexto/`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/)

## 📝 Descritivo do sistema

WakandaAI é a plataforma operacional da edtech Wakanda Academy — escola que forma programadores juniors do Norte/Nordeste/periferia através de uma **jornada gamificada** (Trilha do Sábio → Jornadas → Missões) fundamentada no modelo Fleury & Fleury de competências (5 Sabedorias). O sistema orquestra **onboarding, conteúdo (Memberkit), cobrança (Asaas), comunicação (WhatsApp/Discord) e progressão gamificada (XP, Classes, Sabedorias)**, com **economia interna de créditos** que recompensa quem contribui pra comunidade, permitindo acesso a serviços premium (mentoria 1:1, imersões, reviews de entrevista). Objetivo arquitetural: **escalar via AI o que hoje é manual**, mantendo a "alma Wakanda" através de um trade-off explícito **UX × Impacto Comunitário × Viabilidade**, sem dogmas de "AI proibida aqui". Em paralelo, gerar prova de conceito do framework **Fleury+AI** — ativo intelectual de longo prazo do fundador.

## 🎯 Principais objetivos de negócio

(Derivados de [`product-vision-board.md`](product-vision-board.md) §"Metas de Negócio")

- **O1** Aumentar receita — escalar entrega de valor a custo reduzido via automação AI
- **O2** Posicionar Wakanda como referência em formação prática
- **O3** Adquirir conhecimento valioso — dados de performance pra melhorar continuamente
- **O4** Desenvolver marca + comunidade engajada
- **O5** Expandir mercado — atrair e reter programadores em início de carreira
- **O6** Validar e publicar o framework **Fleury+AI** como ativo intelectual long-term (livro/consultoria/palestras 2027+)

## ⛓️ Principais restrições

### Técnicas
- **R1** Stack consolidada: Java 17 + Spring Boot 3.3.4 + PostgreSQL + AWS (SNS/SQS) — confirmado pelo drawio desde 2024, **não há tese alternativa válida**
- **R2** Integrações third-party com contrato limitado: Memberkit (webhook), Asaas (cobrança), Z-API (WhatsApp), Discord
- **R3** Conta GitHub pessoal — infra não-corporativa
- **R4** Sem ambiente formal de staging documentado

### Capacidade
- **R5** Vis tem **5h/sem** — operação real é Jefferson + Esténio (Red Wakandas)
- **R6** Edtech bootstrapped — cada decisão arquitetural precisa ser barata de testar

### Regulatórias
- **R7** LGPD: dados sensíveis do Wakander (perfil comportamental, propósito, sonhos) coletados na Jornada da Clareza
- **R8** Anti-fraude: economia interna de créditos cria incentivo a farming — mitigar desde o design

### Estratégicas / não-negociáveis
- **R9** Tom Wakanda em toda automação — "não nos levamos tão a sério", intensidade com leveza
- **R10** Trade-off triplo em qualquer automação: **UX do Wakander × Impacto Comunitário × Viabilidade**. Sem categorias proibidas — análise caso-a-caso
- **R11** Economia interna **circular**: mensalidade libera quotas básicas + créditos compráveis + créditos ganhos contribuindo (mentoria, ajuda comunitária) → integridade econômica do sistema é requisito

## 🏆 Atributos de qualidade priorizados

> **Extensibilidade > Mantenibilidade > Disponibilidade > Observabilidade > Segurança > Performance > Escalabilidade**

| # | Atributo | Justificativa |
|---|----------|---------------|
| 1 | **Extensibilidade** | Roadmap tem 4 fases declaradas (XP→Classes→AI Coach→Clãs). Tipos novos de missão (Anki, Quiz, Projeto Avaliado, Espontânea) são previsíveis. Adicionar tipo NÃO PODE exigir mexer em código existente. |
| 2 | **Mantenibilidade** | Vis tem 5h/sem. Código difícil de manter mata a operação. Convenções consistentes (Ports/Adapters, Strategy Processador) são requisito permanente. |
| 3 | **Disponibilidade** | Wakander estuda fora do horário comercial — sistema fora do ar = engajamento perdido. Mentor humano cobre só o crítico, não a indisponibilidade técnica. |
| 4 | **Observabilidade** | Fluxos event-driven com 3-4 hops (missão→XP→classe→whatsapp) só são debugáveis com tracing. Também alimenta O3 (coleta de dados pra melhorar). |
| 5 | **Segurança** | LGPD + economia de créditos + integração LinkedIn futura. Top-5 mas não top-3 — Wakanda não maneja dados financeiros bancários nem PII crítica. |
| 6 | **Performance** | 200+ Wakanders ativos hoje, picos previsíveis (semana de XP, eventos). Otimização vem só quando dor aparece. |
| 7 | **Escalabilidade** | Crescimento 5×-10× é hipótese, não realidade. Escalar via processo+AI antes de hardware. |

## 🏗️ Principais decisões de design da arquitetura

### Estilo arquitetural
- **Clean Architecture + DDD + Event-Driven** — consolidado (100% do código segue)
- **Ports & Adapters** para todo Repository — zero acoplamento JPA na camada application
- **Strategy Pattern (Processadores)** como padrão de extensão — aplicado em 5 chains
- **Bounded Contexts explícitos** alinhados com a [Modelagem Fase 5](/Users/visreis/workspace/pessoal/wakanda-ai-dev/docs/_extraction/strategic-context.md#2-modelagem-estratégica-fase-5-conceitual-—-gerada-em-sessão-ddd-prévia) — Catálogo (suporte), Progresso (core), XP/Gamificação (core)

### Decisões técnicas firmes

| Camada | Decisão | Status |
|--------|---------|--------|
| Backend | Java 17 + Spring Boot 3.3.4 | ✅ Consolidado |
| Banco | **PostgreSQL** (`@MissaoConfig` JSONB pra polimorfismo de TipoMissao quando precisar) | ✅ Consolidado — tese MongoDB descartada |
| Mensageria | AWS SNS/SQS FIFO + DLQ | ✅ Consolidado |
| Auth | JWT (Auth0 lib) + Spring Security | ✅ Consolidado |
| Front operacional | Thymeleaf | ✅ Para dashboards admin / Game Masters |
| Infra | Docker + GitHub Actions → ghcr.io → AWS (EC2/RDS/VPC) | ✅ Consolidado |
| ACL para externos | Adapter traduz DTO externo → DTO interno | ⚠️ Padrão estabelecido pra Memberkit/Asaas; **a estender pra Z-API/Discord** |

### Decisões (atualizado 2026-05-27 pós-reverse-eng)

| Tema | Decisão | Status |
|------|---------|--------|
| **LLM Provider** | ✅ **Pluggable via ACL (decidido 2026-05-27, refinado com T1.0 anki-coach)** — port `LLMProvider` em novo BC `ai/` + 2 adapters: OpenAI (ativo no MVP por estar pago) e Anthropic (preparado). Stack: LangChain4j. ACL anti-SDK: aplicação layer NUNCA importa `dev.langchain4j.*` | DECIDIDO |
| **Plataforma de desafios** | ⏳ HackerRank, Coodesh, build próprio, ou postergar | A decidir antes de Jornada da Habilidade automatizada |
| **Front Wakander** | ⏳ Thymeleaf, React/Vue, ou continuar via Memberkit/WhatsApp | A decidir quando Fase 2 (Classes/Hierarquia) precisar de UI rica |
| **Sistema de Créditos** | ⏳ Subdomínio próprio (BC #8) vs. parte do contexto XP — ver `sdd/PROJECT.md` OSQ-6 | **Bloqueante do modelo de monetização L2** |

> 📓 Outras decisões em aberto pós-reverse-eng estão em [`sdd/PROJECT.md` §Open Strategic Questions](../sdd/PROJECT.md) (OSQ-2 a OSQ-6).

### Componentes principais (bounded contexts)

| # | Contexto | Estado | Tipo (DDD) |
|---|----------|--------|------------|
| 1 | **Catálogo Wakanda** (Trilha → Jornada → Missão → TipoDeMissao) | Implementado parcial — sem polimorfismo de TipoDeMissao | Suporte (Game Masters editam) |
| 2 | **Progresso Wakander** (WakanderProgress, JornadaDoWakander, MissaoProgress) | Implementado — múltiplas jornadas ativas a confirmar | Core |
| 3 | **Gamificação XP** (XpWakander, ClasseWakander, SabedoriaXP, LiberacaoMissao) | Implementado parcial — sem XPMinimoPorTipo | Core |
| 4 | **Wakander** (cadastro, perfil, contatos) | Implementado | Core |
| 5 | **Financeiro** (cobrança Asaas, assinaturas) | Implementado | Core |
| 6 | **Comunicação** (WhatsApp Z-API, Discord, processadores) | Implementado — ACL fraca | Suporte |
| 7 | **Onboarding/Jornada do Wakander** (orquestrador entre contextos) | Implementado | Suporte |
| 8 | **Economia de Créditos** (saldo, transações, marketplace de mentoria) | ❌ **Não implementado — bloqueante L2** | Core (a definir) |
| 9 | **AI Coach** (sessões com LLM, personalização, recomendação) | ❌ **Não implementado — Fase 3** | Core (futuro) |
| 10 | **AI Platform** (port `LLMProvider` + adapters OpenAI/Anthropic + ACL anti-LangChain4j) | 🚧 **Em construção — T1.0 anki-coach (2026-05-27)** | Suporte (foundation reutilizada por T1.1–T2.5) |
| 11 | **Anki Coach** (gera deck Anki CSV a partir de transcrição — v1 single-shot + v2 iterativa) | 🚧 **Em construção — T1.0** | Suporte (didático + foundation) |

### Padrões de extensão (canônicos — replicar)

| Adicionar... | Como | Modificação em código existente |
|--------------|------|------------------------------------|
| Tipo de evento (whatsapp/memberkit/discord/...) | Nova classe `@Component implements XxxProcessor` | Zero |
| Repositório | Port em `application/service/` + Adapter em `infra/` | Zero |
| Integração externa | Adapter + ACL traduzindo DTO externo → domain DTO | Zero |
| Tipo de missão com comportamento próprio | **Requer refactor de `TipoMissao` para Strategy** (ver dívida #1) | **Não-zero ainda** |
| LLM provider | Port `LLMProvider` + adapter por provider | Zero (quando port for criado) |

### Dívida arquitetural reconhecida (em ordem de impacto vs atributos priorizados)

> Mapping completo em [`arquitetura-gameficacao.md` §11](arquitetura-gameficacao.md#11-priorização-de-gaps-derivada-do-architecture-haiku).

1. **`TipoMissao` como tabela-enum** → compromete **Extensibilidade (#1)** — bloqueia Fase 2+
2. **Sem `ValidadorDeMissaoEspontanea`** → compromete **Extensibilidade (#1)** + grupo-alvo "Clã"
3. **Cascata síncrona em `concluiMissao()`** → compromete **Mantenibilidade (#2)** + **Disponibilidade (#3)**
4. **`XpPromocaoClasseEvent` síncrono na TX de XP** → compromete **Disponibilidade (#3)** — sem rollback compensatório
5. **Sem distributed tracing** → compromete **Observabilidade (#4)**
6. **ACL fraca em Z-API/Discord** → compromete **Mantenibilidade (#2)**
7. **`@Transactional` inconsistente** → compromete **Mantenibilidade (#2)**
8. **Idempotência ausente em consumers SQS críticos** → compromete **Disponibilidade (#3)**
9. **Bean Validation no domínio** → compromete **Mantenibilidade (#2)**
10. **Sem sistema de Créditos** → compromete **modelo de monetização (Vision Board)** — não Haiku diretamente, mas bloqueia O1
11. **Sem AI Coach** → compromete **diferencial de produto #4** (Vision Board) — não Haiku diretamente

### Princípios operacionais (para qualquer decisão futura)

1. **Trade-off triplo L3** — toda automação responde 3 perguntas:
   - Melhor experiência para o Wakander?
   - Maior impacto para a comunidade?
   - Viável (custo, capacidade, complexidade)?
   Se as 3 respostas são positivas: faz. Se 1 é claramente negativa: não faz ou repensa.

2. **Sem categorias proibidas de AI** — mentor humano não é dogma. AI 1:1 treinado resolvendo o básico pode ser legítimo.

3. **Extensibilidade primeiro, performance depois** — código que não escala (200 → 2000 wakanders) é problema futuro. Código que não estende é problema HOJE.

4. **Toda integração externa nasce com ACL** — DTO externo nunca toca application layer.

5. **Domínio rico** — lógica de negócio em métodos do domínio, services orquestram. Nunca o oposto.

## 🔗 Cross-references

- **Por que existe / pra quem é** → [`product-vision-board.md`](product-vision-board.md)
- **Como está implementado hoje** → [`arquitetura-gameficacao.md`](arquitetura-gameficacao.md)
- **Material de origem** → [`_extraction/strategic-context.md`](_extraction/strategic-context.md)
- **Norte estratégico Wakanda (negócio)** → [`game-theory/dominios/wakanda/contexto/INDEX.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/INDEX.md)
