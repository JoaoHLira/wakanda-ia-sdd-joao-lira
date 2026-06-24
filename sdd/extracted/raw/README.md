# Wakanda-AI-Dev: Extração de Integrações, Eventos & Webhooks

Documento de engenharia reversa profunda do projeto Wakanda-AI-Dev, catalogando TODAS as integrações externas, arquitetura event-driven (SNS/SQS), Spring Events, DTOs externos e endpoints.

## Arquivos Gerados

### 1. `integrations.md` (294 linhas)
**Escopo:** Todas as integrações externas

- **§1:** Tabela visual com 6 providers (Memberkit, Asaas, Z-API, Discord, Clint, N8N)
- **§2:** Detalhamento por provider com:
  - HTTP Clients (saída) — interface + implementação + endpoints
  - Webhook Receivers (entrada) — controller + DTO + auth
  - Processadores (Strategy pattern) — interface + implementações
  - ACL status — anti-pattern A4 sinalizado quando DTO vaza
- **§3:** Consolidação de todos os DTOs externos — 15 DTOs catalogados
- **§4:** Resumo de problemas arquiteturais
- **§5:** Configuração de properties

**Veredito:** 5/6 integradores SEM ACL adequada

---

### 2. `events-messaging.md` (330 linhas)
**Escopo:** Arquitetura event-driven e messaging

- **§1:** SNS Topics (9) + SQS Queues (11) — tabela completa
  - Producer e subscribers de cada tópico
  - DLQ associadas
  - DeduplicationId & MessageGroupId — **GAP A6 identificado** (dedup aleatório)
  
- **§2:** Spring Events (3 events)
  - `CadastroCompletoEvent` → 2 listeners
  - `AssinaturaCanceladaEvent` → 3 listeners (cascata de SNS)
  - `XpPromocaoClasseEvent` → 1 listener
  - **Problemas:** Listeners síncronos, sem `@TransactionalEventListener`
  
- **§3:** Schedulers (5 total, 2 desabilitados)
  - Token cleanup (4:05 Wed)
  - Relatório (8:00 Mon)
  - Verificação inatividade (8:00 Mon)
  
- **§4:** Fluxos end-to-end (5 fluxos)
  - Cadastro → Memberkit & Clint
  - Cancelamento → 3 integrações síncronos (🔴 **crítico**)
  - XP → Promoção
  - Progresso → cascata
  - Asaas webhook → Cobrança
  - Análise de idempotência, tracing, fallback
  
- **§5:** Recomendações (A, B, C, D)

**Veredito:** Cascatas de eventos síncronos, sem idempotência, sem tracing

---

### 3. `webhooks-and-scheduled.md` (216 linhas)
**Escopo:** Webhooks recebidos, endpoints internos, schedulers

- **§1:** Webhooks recebidos (3 ativos)
  - Memberkit: `POST /memberkit` + `/memberkit/import`
  - Asaas: `POST /financeiro/cobranca/processa-evento`
  - **Gap:** Z-API, Discord, Clint, N8N não recebem webhooks (faltam)
  
- **§2:** APIs internas (30+ endpoints)
  - Autenticação (cadastro, login) — públicos
  - Wakander (criar, atualizar fiador) — públicos
  - Comunicação (envia, convida) — públicos
  - Financeiro (assinatura) — públicos
  - Gamificação (progresso, missão, jornada) — públicos
  - Dashboard/Formulário — públicos
  - **Status:** 🔴 **TODOS SEM AUTH** — exposto publicamente
  
- **§3:** Schedulers consolidado (5 + 2 desabilitados)
  
- **§4:** Riscos de segurança
  - 🔴 CRÍTICO: endpoints públicos, webhook sem signature, sem idempotência
  - 🟡 MÉDIO: sem rate-limit, logging de dados sensíveis
  
- **§5:** Fluxo típico de requisição
  
- **§6:** Recomendações (segurança, confiabilidade, observabilidade)

**Veredito:** Sistema completamente aberto — sem autenticação/autorização

---

## Resumo Executivo

### Integrações Mapeadas
- ✅ **6 providers:** Memberkit (LMS), Asaas (pagamentos), Z-API (WhatsApp), Discord, Clint (CRM), N8N (automação)
- ✅ **15 DTOs externos** catalogados
- ✅ **3 webhooks receptivos** + 4 esperados mas não implementados
- ✅ **30+ endpoints** internos expostos

### Arquitetura Event-Driven
- ✅ **9 SNS topics** (FIFO)
- ✅ **11 SQS queues** (FIFO + standard)
- ✅ **3 Spring Events** internos
- ✅ **5 fluxos end-to-end** catalogados
- ✅ **5 schedulers** (2 desabilitados)

### Problemas Críticos Identificados

| Anti-pattern | Qtd | Severidade | Arquivo |
|---|---|---|---|
| **A4 — ACL Faltando** | 5 providers | 🔴 CRÍTICO | integrations.md §2 |
| **A2 — Listeners Síncronos em TX** | 3 events | 🔴 CRÍTICO | events-messaging.md §2.2 |
| **A6 — Idempotência Aleatória** | SNS/SQS | 🔴 CRÍTICO | events-messaging.md §1.3 |
| **Webhook sem Signature** | Memberkit, Asaas | 🔴 CRÍTICO | webhooks-and-scheduled.md §4 |
| **Endpoints Sem Auth** | 30+ endpoints | 🔴 CRÍTICO | webhooks-and-scheduled.md §2 |
| **Sem Tracing** | Todo sistema | 🟡 MÉDIO | events-messaging.md §4 |

---

## Como Usar Este Documento

### Para Especificação Técnica
→ Ler `integrations.md` + `events-messaging.md` para entender fluxos

### Para Code Review
→ Ler `webhooks-and-scheduled.md §4-5` para security gaps

### Para Refactoring
→ Ler `integrations.md §4` + `events-messaging.md §5` para recomendações

### Para Operações
→ Ler `events-messaging.md §3` para schedulers + `webhooks-and-scheduled.md §3` para monitoramento

---

## Estatísticas

| Métrica | Valor |
|---------|-------|
| Linhas totais | 840 |
| Providers integrados | 6 |
| DTOs externos | 15 |
| SNS Topics | 9 |
| SQS Queues | 11 |
| Spring Events | 3 |
| Endpoints REST | 30+ |
| Schedulers | 5 (+ 2 disabled) |
| Webhooks receptivos | 3 |
| Fluxos end-to-end | 5 |
| Anti-patterns encontrados | 6 |

---

**Data de Extração:** 27-05-2026  
**Versão:** 1.0  
**Escopo:** Completo — todas as camadas (infra, application, domain)  
**Qualidade:** Rigor total — cada item verificado em código-fonte
