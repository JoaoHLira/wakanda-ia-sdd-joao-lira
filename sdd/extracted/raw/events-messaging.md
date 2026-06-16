# Wakanda-AI-Dev: Arquitetura Event-Driven (SNS/SQS) & Spring Events

## §1 — SNS Topics e SQS Queues

### 1.1 — Topics SNS (Configuração em TopicNames.java)

| Tópico SNS | Descrição | Producers | Subscribers (Filas) |
|----------|-----------|-----------|-------------------|
| `teste-topic.fifo` | Teste/debug | Manual | `teste-queue.fifo` |
| `zapi-requests-topic.fifo` | Envio WhatsApp | `AssinaturaCanceladaConsumer`, `ProgressoOnboardConsumer` | `zapi-requests-queue.fifo`, `zapi-requests-queue-dlq.fifo`, `zapi-requests-queue-delay` |
| `memberkit-requests-topic.fifo` | Requisições MemberKit (novo membro, acesso bloqueado) | `AssinaturaCanceladaConsumer`, `ProgressoOnboardConsumer` | `memberkit-requests-queue.fifo`, `memberkit-requests-queue-dlq.fifo` |
| `asaas-requests-topic.fifo` | Processamento de eventos Asaas | — | `asaas-requests-queue.fifo` |
| `clint-contato-requests-topic.fifo` | Envio de contatos para Clint CRM | `ProgressoOnboardConsumer` | `clint-contato-requests-queue.fifo`, `clint-contato-requests-dlq.fifo` |
| `clint-requests-topic.fifo` | Cancelamento no Clint | `AssinaturaCanceladaConsumer` | `clint-requests-queue.fifo`, `clint-requests-dlq.fifo` |
| `discord-requests-topic.fifo` | Remoção Discord | `AssinaturaCanceladaConsumer` | `discord-requests-queue.fifo`, `discord-requests-queue-dlq.fifo` |
| `progresso-wakander-requests-topic.fifo` | Atualização de progresso | — | `progresso-wakander-requests-queue.fifo`, `progresso-wakander-requests-queue-dlq.fifo` |
| `xp-wakander-requests-topic.fifo` | Cálculo de XP | — | `xp-wakander-requests-queue.fifo`, `xp-wakander-requests-queue-dlq.fifo` |

### 1.2 — Consumers SQS (por fila)

#### Fila: `zapi-requests-queue.fifo`
- **Consumer:** [`ComunicacaoConsumerSqs.consomeQueueDoWhatsapp()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/infra/ComunicacaoConsumerSqs.java:51-62)
- **DTO:** `SqsMessageDto` com payload `ZApiEventDto`
- **Processamento:** `ComunicacaoWhatsappService.processaPorTipoMensagem()` → envia mensagem
- **DLQ:** `zapi-requests-queue-dlq.fifo` consumida por `consomeDlqQueueDoWhatsapp()` → re-notifica

#### Fila: `zapi-requests-queue-delay` (sem FIFO)
- **Consumer:** [`ComunicacaoConsumerSqs.consomeQueueDoWhatsappComDelay()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/infra/ComunicacaoConsumerSqs.java:64-75)
- **DTO:** `SqsMessageDto` com `ZApiEventDto`
- **DelaySeconds:** 10s * index (mín 10s, máx 850s) — implementado em [`ComunicacaoSendSqs.enviaMensagemZAPIComDelay()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/infra/ComunicacaoSendSqs.java:26-42)
- **Uso:** Reenvios escalonados de WhatsApp com backoff exponencial

#### Fila: `memberkit-requests-queue.fifo`
- **Consumer:** [`JornadaWakanderConsumerSqs.consomeMensagemMemberkitRequests()`](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/infra/JornadaWakanderConsumerSqs.java:23-30)
- **DTO:** `SqsMessageDto` com payload `MemberKitMessageEnvelope`
- **Processamento:** Strategy via lista `List<MemberKitRequestProcessor>` — filtra por tipo e processa
- **Tipos suportados:** `CADASTRO_NOVO_MEMBRO` → `MemberKitCadastraNovoMembroProcessador`, `ACESSO_BLOQUEADO` → `MemberKitAcessoPlataformaBlockProcessador`
- **DLQ:** `memberkit-requests-queue-dlq.fifo` consumida por `consomeDlqQueueDoMemberkit()` → notifica líderes via WhatsApp

#### Fila: `asaas-requests-queue.fifo`
- **Consumer:** [`ComunicacaoConsumerSqs.consomeAsaasRequest()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/infra/ComunicacaoConsumerSqs.java:91-102)
- **DTO:** `SqsMessageDto` com payload `Wakander`
- **Processamento:** `WakanderService.buscaDadosAsaas()` → busca status de pagamento no Asaas

#### Fila: `clint-contato-requests-queue.fifo`
- **Consumer:** [`ComunicacaoConsumerSqs.consomeQueueDaClintContato()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/infra/ComunicacaoConsumerSqs.java:121-132)
- **DTO:** `SqsMessageDto` com `ClintContatoRequest`
- **Processamento:** `ComunicacaoService.enviaContatoParaClint()` → webhook POST para Clint
- **DLQ:** `clint-contato-requests-dlq.fifo` → `consomeDlqQueueDaClintContato()` notifica líderes

#### Fila: `clint-requests-queue.fifo`
- **Consumer:** [`ComunicacaoConsumerSqs.consomeQueueClint()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/infra/ComunicacaoConsumerSqs.java:210-222)
- **DTO:** `SqsMessageDto` com `Wakander`
- **Processamento:** `ComunicacaoService.cancelaWakanderClint()` → cancela acesso no CRM
- **DLQ:** `clint-requests-dlq.fifo` → `consomeQueueClintDlq()` notifica falhas

#### Fila: `discord-requests-queue.fifo`
- **Consumer:** [`ComunicacaoConsumerSqs.removeWakanderDoServidorDiscord()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/infra/ComunicacaoConsumerSqs.java:176-183)
- **DTO:** `SqsMessageDto` com `DiscordEventRequest`
- **Processamento:** `ComunicacaoDiscordService.processaPorTipoMensagem()` → remove membro via JDA ou envia invite
- **DLQ:** `discord-requests-queue-dlq.fifo` → `consumeDlqDiscord()` notifica líderes

#### Fila: `xp-wakander-requests-queue.fifo`
- **Consumer:** [`XpWakanderConsumer.consumeXpWakanderQueueMessage()`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/infra/XpWakanderConsumer.java:24-35)
- **DTO:** `SqsMessageDto` com `XpWakanderEventDTO`
- **Processamento:** `XpWakanderService.processaXP()` → calcula XP, atualiza BD
- **DLQ:** `xp-wakander-requests-queue-dlq.fifo`

#### Fila: `progresso-wakander-requests-queue.fifo`
- **Consumer:** [`ProgressoWakanderConsumer.consomeQueueProgressoWakander()`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/progressowakander/infra/ProgressoWakanderConsumer.java:23-34)
- **DTO:** `SqsMessageDto` com `ProgressoWakanderEventDto`
- **Processamento:** `ProgressoWakanderService.processaPorTipoProgresso()` → atualiza progresso (missão ou jornada)
- **DLQ:** `progresso-wakander-requests-queue-dlq.fifo`

### 1.3 — DeduplicationId & MessageGroupId (Idempotência)

⚠️ **PROBLEMA A6: Idempotência Insuficiente**

**Implementação Atual:**
```java
// PublicadorNotificacaoInfraSns.java:20-23
SnsNotification<T> notification = SnsNotification.<T>builder(payload)
    .deduplicationId(UUID.randomUUID().toString())  // ← ALEATÓRIO! Não idempotente
    .groupId(groupId)  // ← groupId é idWakander.toString()
    .build();
```

**Problemas:**
1. `deduplicationId` é gerado aleatoriamente → **não garante idempotência**
2. Se a mesma mensagem for reenviada, terá ID diferente → duplicação
3. Para idempotência verdadeira, deveria ser hash(payload) ou businessKey

**Recomendação:** Usar `hash(message_content)` ou `eventId` persistido no domínio.

---

## §2 — Spring Events (ApplicationEventPublisher)

### 2.1 — Event Classes

#### `CadastroCompletoEvent`
- **Arquivo:** [`wakander/application/event/CadastroCompletoEvent.java`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/event/CadastroCompletoEvent.java)
- **Publicado por:** [`WakanderApplicationService.criaWakander()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/service/WakanderApplicationService.java)
- **Listeners:**
  1. [`ProgressoOnboardConsumer.publicaEventoCadastroNoSns()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/event/ProgressoOnboardConsumer.java) — envia para `clint-contato-requests-topic.fifo`
  2. [`ProgressoOnboardConsumer.publicaEventoCadastroMemberkitNoSns()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/event/ProgressoOnboardConsumer.java) — envia para `memberkit-requests-topic.fifo`
- **Tipo de evento:** ✅ Spring Event
- **Síncronidade:** ⚠️ **Síncrono dentro da TX** — risco de deadlock se service chamar BD

#### `AssinaturaCanceladaEvent`
- **Arquivo:** [`wakander/application/event/AssinaturaCanceladaEvent.java`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/event/AssinaturaCanceladaEvent.java)
- **Publicado por:** [`WakanderApplicationService.cancelaAssinatura()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/service/WakanderApplicationService.java)
- **Listeners:**
  1. [`AssinaturaCanceladaConsumer.removeWakanderDoGrupoWhatsapp()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/event/AssinaturaCanceladaConsumer.java) — publica em `zapi-requests-topic.fifo`
  2. [`AssinaturaCanceladaConsumer.bloqueiaWakanderNoMemberKit()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/event/AssinaturaCanceladaConsumer.java) — publica em `memberkit-requests-topic.fifo`
  3. [`AssinaturaCanceladaConsumer.removeWakanderDoServidorDiscord()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/event/AssinaturaCanceladaConsumer.java) — publica em `discord-requests-topic.fifo`
- **Síncronidade:** ⚠️ **Síncrono** — **Anti-pattern A2: múltiplas chamadas SNS dentro de TX**

#### `XpPromocaoClasseEvent`
- **Arquivo:** [`gameficacao/xp/xpwakander/application/event/XpPromocaoClasseEvent.java`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/application/event/XpPromocaoClasseEvent.java)
- **Publicado por:** [`XpWakanderApplicationService.processaXP()`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/application/service/XpWakanderApplicationService.java) quando XP cruza threshold de classe
- **Listeners:**
  1. [`XpWakanderConsumer.promoveClasseXpWakander()`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/infra/XpWakanderConsumer.java:42-49) — processa promoção de classe
- **Síncronidade:** ⚠️ **Síncrono dentro da TX**

### 2.2 — Listeners Síncronos com @EventListener

⚠️ **Problemas Identificados:**

| Listener | Classe | Listener | Processamento | Risco | Recomendação |
|----------|--------|----------|---------------|-------|--------------|
| `ProgressoOnboardConsumer` | `ProgressoOnboardConsumer.java:23-34` | `@EventListener(CadastroCompletoEvent)` | Publica 2 mensagens SNS síncrono | 🔴 **Alto** — TX pode falhar | Usar `@TransactionalEventListener(AFTER_COMMIT)` |
| `AssinaturaCanceladaConsumer` | `AssinaturaCanceladaConsumer.java:28-64` | `@EventListener` (3 métodos) | Publica 3 SNS síncronos | 🔴 **Crítico** — cascata de falhas | Usar `@TransactionalEventListener` com fallback |
| `XpWakanderConsumer` | `XpWakanderConsumer.java:42-49` | `@EventListener(XpPromocaoClasseEvent)` | Processa promoção BD + DTO | 🟡 **Médio** | Migrar para `@TransactionalEventListener` |

---

## §3 — Schedulers @Scheduled

| Classe.método | Cron | Frequência | O que faz | Domínio | Risco |
|-------|------|-----------|----------|---------|-------|
| [`AutenticacaoSchedulerService.deletaTokensExpirados()`](src/main/java/academy/wakanda/wakanda_ai/autenticacao/application/service/AutenticacaoSchedulerService.java) | `0 0 5 * * 3` | Toda quarta às 5h | Limpa tokens expirados da BD | `autenticacao` | 🟢 Baixo |
| `JornadaWakanderSchedulerService.notificaWakandersNaoEstudaram()` | `//comentado` | — | Envia notificação WhatsApp para inativos | `jornadawakander` | 🟡 Desabilitado |
| `JornadaWakanderSchedulerService.checaWakanderQueEstudaram()` | `//comentado` | — | Verifica progresso de estudantes | `jornadawakander` | 🟡 Desabilitado |
| [`JornadaWakanderSchedulerService.agendaEnvioRelatorio()`](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/service/JornadaWakanderSchedulerService.java) | `0 0 8 * * MON` | Segunda-feira 8h | Agenda envio de relatório para líderes | `jornadawakander` | 🟢 Baixo |
| [`JornadaWakanderSchedulerService.geraRelatorioWakandersInativos()`](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/service/JornadaWakanderSchedulerService.java) | `0 0 8 * * 1` | Segunda-feira 8h | Gera relatório de inativos | `jornadawakander` | 🟢 Baixo |

---

## §4 — Fluxos Assíncronos End-to-End

### Fluxo 1: Cadastro → Memberkit & Clint

```
[WakanderAPI.novoWakander()]
    ↓
[WakanderApplicationService.criaWakander()]
    ↓ publishEvent(CadastroCompletoEvent)
[ProgressoOnboardConsumer.@EventListener(2 métodos)]
    ├─→ publicaMensagem() → SNS [memberkit-requests-topic.fifo]
    └─→ publicaMensagem() → SNS [clint-contato-requests-topic.fifo]
    
SNS → SQS [memberkit-requests-queue.fifo]
    ↓
[JornadaWakanderConsumerSqs.consomeMensagemMemberkitRequests()]
    ↓ Strategy
[MemberKitCadastraNovoMembroProcessador.processaEvento()]
    ↓ HTTP POST
[JornadaWakanderWebClient.requisicaoPostParaOMemberKit()]
    ↓ Response
[MemberkitUserDto] → salva ID no Wakander

SNS → SQS [clint-contato-requests-queue.fifo]
    ↓
[ComunicacaoConsumerSqs.consomeQueueDaClintContato()]
    ↓ HTTP POST
[ClintCRMInfra.enviaContatoParaClint()]
```

**Análise:**
- **Idempotência:** ❌ Não garantida — `deduplicationId` aleatório
- **Tracing:** ❌ Faltam trace IDs entre eventos e SQS
- **Fallback:** ⚠️ DLQs existem mas sem retry policy visível
- **Latência:** 🟡 Síncrono no evento → possível timeout

---

### Fluxo 2: Assinatura Cancelada → WhatsApp + Discord + Memberkit + Clint

```
[WakanderAPI.cancelaAssinatura()]
    ↓
[WakanderApplicationService.cancelaAssinatura()]
    ↓ publishEvent(AssinaturaCanceladaEvent)
[AssinaturaCanceladaConsumer.@EventListener(3 métodos)]
    ├─→ removeWakanderDoGrupoWhatsapp()
    │   ↓ publicaMensagem() → SNS [zapi-requests-topic.fifo]
    ├─→ bloqueiaWakanderNoMemberKit()
    │   ↓ publicaMensagem() → SNS [memberkit-requests-topic.fifo]
    └─→ removeWakanderDoServidorDiscord()
        ↓ publicaMensagem() → SNS [discord-requests-topic.fifo]

SNS → SQS [zapi-requests-queue.fifo]
    ↓ [ComunicacaoConsumerSqs.consomeQueueDoWhatsapp()]
    ↓ HTTP POST → Z-API.remove-participant

SNS → SQS [memberkit-requests-queue.fifo]
    ↓ [JornadaWakanderConsumerSqs.consomeMensagemMemberkitRequests()]
    ↓ Strategy
    ↓ HTTP POST → Memberkit.block-access

SNS → SQS [discord-requests-queue.fifo]
    ↓ [ComunicacaoConsumerSqs.removeWakanderDoServidorDiscord()]
    ↓ JDA.removeUserFromGuild()
```

**Análise:**
- **Idempotência:** ❌ Nenhuma garantia
- **Tracing:** ❌ Sem correlation ID
- **Fallback:** ⚠️ DLQs enviam notificação ao grupo líderes via WhatsApp
- **Atomicidade:** ❌ Parcial — se uma falhar, outras prosseguem (saga sem compensação)
- **Veredito:** **🔴 Crítico** — 3 integrações síncronos na mesma TX

---

### Fluxo 3: XP Processado → Promoção de Classe

```
[MissaoProgressoService/JornadaWakanderService]
    ↓ SNS publish → [xp-wakander-requests-topic.fifo]
    
SNS → SQS [xp-wakander-requests-queue.fifo]
    ↓
[XpWakanderConsumer.consumeXpWakanderQueueMessage()]
    ↓
[XpWakanderService.processaXP()]
    ↓ UPDATE BD (xp_wakander table)
    ↓ IF xp >= threshold
[XpWakanderApplicationService.processaXP()
    ↓ publishEvent(XpPromocaoClasseEvent)
    
[XpWakanderConsumer.@EventListener(XpPromocaoClasseEvent)]
    ↓
[XpWakanderService.processaPromocaoClasse()]
    ↓ UPDATE classe_wakander, histórico
```

**Análise:**
- **Idempotência:** ⚠️ Parcial — XP é idempotente (não duplica), mas promoção poderia ser
- **Tracing:** ❌ Sem rastreamento
- **Atomicidade:** ✅ Tudo em 1 consumer SQS
- **Veredito:** 🟡 Aceitável mas síncrono pode travar

---

### Fluxo 4: Progresso → SNS/SQS → Service

```
[ProgressoWakanderApi.criaProgresso() / JornadaWakanderService]
    ↓ SNS publish → [progresso-wakander-requests-topic.fifo]
    
SNS → SQS [progresso-wakander-requests-queue.fifo]
    ↓
[ProgressoWakanderConsumer.consomeQueueProgressoWakander()]
    ↓
[ProgressoWakanderService.processaPorTipoProgresso()]
    ├─ IF MISSAO_PROGRESSO
    │  ↓ UPDATE missao_wakander
    │  ↓ Incrementa XP
    │  ↓ SNS publish [xp-wakander-requests-topic.fifo] ← **cascata**
    │
    └─ IF JORNADA_PROGRESSO
       ↓ UPDATE jornada_progresso
```

**Análise:**
- **Cascata:** ⚠️ Progresso → XP → Promoção → (possível notificação)
- **Idempotência:** ❌ Sem verificação de duplicação
- **Veredito:** 🟡 Cascata de eventos precisa de idempotência forte

---

### Fluxo 5: Asaas Webhook → Processamento → Cobrança

```
[CobrancaAPI.processaEvento(CobrancaAsaasDto)]
    ↓
[CobrancaAsaasApplicationService.processaEvento()]
    ↓ Strategy (List<CobrancaProcessadorAsaas>)
    ├─ PAYMENT_CONFIRMED
    │  ↓ [CobrancaPaymentConfirmedProcessador.processa()]
    │  ↓ UPDATE cobranca, ativa_assinatura
    │
    ├─ PAYMENT_RECEIVED
    │  ↓ [CobrancaPaymentReceivedProcessador.processa()]
    │  ↓ UPDATE cobranca, marca_pago
    │
    └─ PAYMENT_OVERDUE
       ↓ [CobrancaPaymentOverdueProcessador.processa()]
       ↓ UPDATE cobranca, aviso_líderes
```

**Análise:**
- **Tipo:** ✅ Síncrono (webhook receptor)
- **Idempotência:** ❌ Sem verificação de event_id duplicado
- **Veredito:** 🔴 Crítico — sem deduplic, webhook pode processar 2x

---

## §5 — Recomendações de Melhoria

### A. Idempotência (Gap A6)
- Implementar `EventId` ou hash no domínio
- Verificar idempotency key antes de processar
- Usar SQS ReceiveCount para limitar retries

### B. Transactionality (Gap A2)
- Converter `@EventListener` → `@TransactionalEventListener(AFTER_COMMIT)`
- Usar 1 evento por listener ou agregar em saga

### C. Tracing (Gap 11.5)
- Injetar `TraceId` em todos os eventos
- Propagar via SNS message attributes
- Log correlation em todos os consumers

### D. ACL (Gap A4)
- Mover DTOs externos para pacote `.external.dto`
- Criar conversores explícitos (Mappers)
- Validar contrato de webhook com schema

