# Índice de Referência Rápida

## 🔗 Links Diretos por Tópico

### Integrações Externas

**Memberkit (LMS)**
- [`integrations.md §2.1`](integrations.md#21--memberkit-lms) — Client, Webhook, Processadores, ACL status
- [`events-messaging.md §4 Fluxo 1`](events-messaging.md#fluxo-1-cadastro--memberkit--clint) — Fluxo cadastro
- Controllers: [`MemberkitController`](../../../src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/memberkit/application/api/MemberkitController.java)

**Asaas (Pagamentos)**
- [`integrations.md §2.2`](integrations.md#22--asaas-processamento-de-pagamentos) — Client, Webhook, Processadores
- [`events-messaging.md §4 Fluxo 5`](events-messaging.md#fluxo-5-asaas-webhook--processamento--cobrança) — Webhook sync
- Controllers: [`CobrancaAPI`](../../../src/main/java/academy/wakanda/wakanda_ai/financeiro/application/api/CobrancaAPI.java)
- **Crítico:** Sem deduplicação — mesmo event pode processar 2x

**Z-API (WhatsApp)**
- [`integrations.md §2.3`](integrations.md#23--z-api-whatsapp) — Client, Tipos de eventos
- DTOs: [`ZApiEventDto`](../../../src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/service/whatsapp/ZApiEventDto.java)

**Discord**
- [`integrations.md §2.4`](integrations.md#24--discord-servidor-da-comunidade) — Client, Event types
- [`events-messaging.md §4 Fluxo 2`](events-messaging.md#fluxo-2-assinatura-cancelada--whatsapp--discord--memberkit--clint) — Cascata cancelamento

**Clint (CRM)**
- [`integrations.md §2.5`](integrations.md#25--clint-crm) — Client push-only
- DTOs: [`ClintContatoRequest`](../../../src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/api/ClintContatoRequest.java)

**N8N (Automação)**
- [`integrations.md §2.6`](integrations.md#26--n8n-orquestração-de-workflows) — Webhook push

---

### Arquitetura Event-Driven

**SNS Topics & SQS Queues**
- [`events-messaging.md §1`](events-messaging.md#1--sns-topics-e-sqs-queues) — Tabela completa (9 topics, 11 queues)
- Consumers: [`ComunicacaoConsumerSqs`](../../../src/main/java/academy/wakanda/wakanda_ai/comunicacao/infra/ComunicacaoConsumerSqs.java) (12 @SqsListener)

**Spring Events**
- [`events-messaging.md §2`](events-messaging.md#2--spring-events-applicationeventpublisher) — 3 events + listeners
- **Problema A2:** Listeners síncronos em TX

**Schedulers**
- [`events-messaging.md §3`](events-messaging.md#3--schedulers-scheduled) — 5 schedulers
- [`webhooks-and-scheduled.md §3`](webhooks-and-scheduled.md#3--schedulers-consolidado)

**Fluxos End-to-End**
- [`events-messaging.md §4`](events-messaging.md#4--fluxos-assíncronos-end-to-end) — 5 fluxos completos com idempotência analysis

---

### Webhooks & APIs

**Webhooks Recebidos (3 ativos)**
- [`webhooks-and-scheduled.md §1`](webhooks-and-scheduled.md#1--webhooks-recebidos-consolidado) — Tabela com provider, path, DTO, auth
- **Gap:** 4 webhooks esperados mas não implementados

**Endpoints Internos (30+)**
- [`webhooks-and-scheduled.md §2`](webhooks-and-scheduled.md#2--apis-internas-endpoints-rest-não-webhook)
- **Crítico:** Todos sem @PreAuthorize

---

### DTOs Externos

**Consolidação de 15 DTOs**
- [`integrations.md §3`](integrations.md#3--dtos-externos-no-código)
- **Problema A4:** 12 DTOs vazam para camadas application/domain

---

### Problemas & Recomendações

**Anti-patterns Críticos**
- A4 (ACL missing): [`integrations.md §4`](integrations.md#4--resumo-de-problemas-de-arquitetura)
- A2 (Sync listeners): [`events-messaging.md §2.2`](events-messaging.md#22--listeners-síncronos-com-eventlistener)
- A6 (Idempotência): [`events-messaging.md §1.3`](events-messaging.md#13--deduplicationid--messagegroupid-idempotência)

**Security Gaps**
- [`webhooks-and-scheduled.md §4`](webhooks-and-scheduled.md#4--resumo-de-riscos-de-segurança) — 🔴 CRÍTICO (endpoints públicos, webhook sem signature)
- [`webhooks-and-scheduled.md §6`](webhooks-and-scheduled.md#6--recomendações-finais) — Roadmap de fixes

---

## 📊 Tabelas de Referência

### Providers x Features

| Provider | Entrada | Saída | ACL | Async | Idempotent |
|----------|---------|-------|-----|-------|-----------|
| Memberkit | ✅ POST | ✅ HTTP | ❌ | ✅ SNS | ❌ |
| Asaas | ✅ POST | ✅ HTTP | ❌ | ❌ Sync | ❌ |
| Z-API | ❌ | ✅ HTTP | ❌ | ✅ SNS | ❌ |
| Discord | ❌ | ✅ HTTP | ⚠️ | ✅ SNS | ❌ |
| Clint | ❌ | ✅ HTTP | ❌ | ✅ SNS | N/A |
| N8N | ❌ | ✅ HTTP | ❌ | N/A | N/A |

### Event Flows

| Fluxo | Início | Fim | Sincro | Cascata | Risco |
|-------|--------|-----|--------|---------|-------|
| Cadastro → Memberkit+Clint | API | SQS | Partial | ✅ 2 branches | 🟡 |
| Cancelamento → 3 integrações | Event | SNS | Full | ✅ 3 SNS | 🔴 |
| XP → Promoção | SQS | BD | Sync | ✅ 1 event | 🟡 |
| Progresso → SQS → BD | API | BD | Async | ✅ XP | 🟡 |
| Asaas webhook → Cobranca | API | BD | Sync | ❌ | 🔴 |

---

## 🔧 Para Cada Cenário

### **Entendo a arquitetura?**
1. Leia [`integrations.md §1`](integrations.md#1--visão-geral-das-integrações-externas) — tabela providers
2. Leia [`events-messaging.md §1`](events-messaging.md#1--sns-topics-e-sqs-queues) — topics/queues
3. Leia [`webhooks-and-scheduled.md §1-2`](webhooks-and-scheduled.md) — webhooks + endpoints

### **Quero adicionar novo webhook (ex: Z-API)?**
1. Copiar padrão de [`integrations.md §2.2`](integrations.md#22--asaas-processamento-de-pagamentos) (Asaas)
2. Implementar Controller + Service + DTO + Signature validation
3. Revisar [`webhooks-and-scheduled.md §4`](webhooks-and-scheduled.md#4--resumo-de-riscos-de-segurança) para security

### **Preciso fazer code review?**
1. Verificar ACL: [`integrations.md §3`](integrations.md#3--dtos-externos-no-código)
2. Verificar auth: [`webhooks-and-scheduled.md §2`](webhooks-and-scheduled.md#2--apis-internas-endpoints-rest-não-webhook)
3. Verificar idempotência: [`events-messaging.md §1.3`](events-messaging.md#13--deduplicationid--messagegroupid-idempotência)

### **Quero mapear fluxo de feature?**
1. Procurar trigger (API/Event/Schedule)
2. Procurar consumidores em [`events-messaging.md §4`](events-messaging.md#4--fluxos-assíncronos-end-to-end)
3. Verificar possíveis falhas (DLQ, retry)

---

## 📋 Checklist de Verificação

### Novo Provider (Integração)
- [ ] HTTP Client (interface + impl) — verificar em `infra/`
- [ ] Webhook Receiver (controller + DTO) — verificar em `application/api/`
- [ ] Signature validation — `X-Signature` ou `X-Webhook-Secret`?
- [ ] DTOs em pacote externo? — Não devem vazar para `application/`
- [ ] Processadores (Strategy) — lista de implementações
- [ ] SNS/SQS topic (se assíncrono)
- [ ] DLQ configurada?

### Code Review Checklist
- [ ] DTOs externos não vazam para domain? [`integrations.md §3`](integrations.md#3--dtos-externos-no-código)
- [ ] Endpoints têm `@PreAuthorize`? [`webhooks-and-scheduled.md §2`](webhooks-and-scheduled.md#2--apis-internas-endpoints-rest-não-webhook)
- [ ] Webhook valida signature? [`webhooks-and-scheduled.md §4`](webhooks-and-scheduled.md#4--resumo-de-riscos-de-segurança)
- [ ] Event listener é `@TransactionalEventListener`? [`events-messaging.md §2.2`](events-messaging.md#22--listeners-síncronos-com-eventlistener)
- [ ] SNS deduplication é determinístico? [`events-messaging.md §1.3`](events-messaging.md#13--deduplicationid--messagegroupid-idempotência)
- [ ] Trace ID propagado? [`events-messaging.md §5`](events-messaging.md#5--recomendações-de-melhoria)

---

## 📞 Dúvidas Frequentes

**P: Qual provider é mais crítico?**  
R: **Asaas** — webhook sem deduplication pode duplicar cobranças. Ver [`events-messaging.md §4 Fluxo 5`](events-messaging.md#fluxo-5-asaas-webhook--processamento--cobrança)

**P: Por que endpoints são públicos?**  
R: Não há `@PreAuthorize` — design inicial para MVP sem auth. Ver [`webhooks-and-scheduled.md §4`](webhooks-and-scheduled.md#4--resumo-de-riscos-de-segurança)

**P: Qual é o maior risco operacional?**  
R: **Listeners síncronos em TX** — se SNS falhar, TX inteira falha. Ver [`events-messaging.md §2.2`](events-messaging.md#22--listeners-síncronos-com-eventlistener)

**P: Como é rastreado o fluxo completo?**  
R: **Não é** — sem trace ID entre eventos. Ver [`events-messaging.md §5.C`](events-messaging.md#5--recomendações-de-melhoria)

---

**Última atualização:** 27-05-2026
