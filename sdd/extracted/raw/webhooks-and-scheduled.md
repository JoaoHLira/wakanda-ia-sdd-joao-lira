# Wakanda-AI-Dev: Webhooks Recebidos & Endpoints Internos

## §1 — Webhooks Recebidos (Consolidado)

### 1.1 — Tabela Completa de Webhooks

| Path | Provider | DTO Esperado | Controller/Handler | Processador | Auth | Status |
|------|----------|--------------|-------------------|-------------|------|--------|
| `POST /memberkit` | Memberkit | `MemberkitEventRequest` | [`MemberkitController`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/memberkit/application/api/MemberkitController.java) | `List<MemberkitProcessor>` strategy | ❌ Nenhuma | ✅ Ativo |
| `POST /memberkit/import` | Memberkit | Query params: `idTipoMissao`, `idJornada` | [`MemberkitController`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/memberkit/application/api/MemberkitController.java) | `MemberkitIntegrationImporter` | ❌ Nenhuma | ✅ Ativo |
| `POST /financeiro/cobranca/processa-evento` | Asaas | `CobrancaAsaasDto` | [`CobrancaAPI`](src/main/java/academy/wakanda/wakanda_ai/financeiro/application/api/CobrancaAPI.java) | `List<CobrancaProcessadorAsaas>` strategy | ❌ Nenhuma | ✅ Ativo |

### 1.2 — Webhook Gap Analysis

**Webhooks Esperados mas NÃO Implementados:**

| Provider | Webhook Esperado | Path Proposto | Observação |
|----------|-----------------|--------------|-----------|
| Z-API | Confirmação de envio/recebimento | `POST /comunicacao/zapi/webhook` | Status vem via polling, não webhook |
| Discord | Eventos de servidor (member join/leave) | `POST /comunicacao/discord/webhook` | Usa JDA + manual command, não webhook |
| Clint | Callback de contato sincronizado | `POST /comunicacao/clint/callback` | Webhook configurado externamente, app não recebe |
| N8N | Resultado de workflow | `POST /gameficacao/n8n/callback` | Webhook saída apenas, sem entrada mapeada |

---

## §2 — APIs Internas (Endpoints REST não-webhook)

### 2.1 — Autenticação

Todos os endpoints internos (auth=❌) **não têm @PreAuthorize** → **CRÍTICO: exposto publicamente**.

### 2.2 — Endpoints por Módulo

#### AUTENTICAÇÃO (`/wakanda-ai/api/autenticacao`)

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `POST /cadastro` | [`AutenticacaoApi.cadastro()`](src/main/java/academy/wakanda/wakanda_ai/autenticacao/application/api/AutenticacaoApi.java) | Cadastro novo Wakander | `CadastroRequest` → `Wakander` | ❌ Público |
| `POST /login` | [`AutenticacaoApi.login()`](src/main/java/academy/wakanda/wakanda_ai/autenticacao/application/api/AutenticacaoApi.java) | Login + geração token JWT | `LoginRequest` → `TokenResponse` | ❌ Público |

**Status:** 🟡 Sem validação CSRF, sem rate-limit

---

#### WAKANDER (`/wakanda-ai/api/wakander`)

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `POST /novo-wakander` | [`WakanderAPI.criaWakander()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/api/WakanderAPI.java:30-35) | Cria novo Wakander | `WakanderDTO` → `Wakander` | ❌ Público |
| `POST /{idWakander}/fiador/atualizacao-link` | [`WakanderAPI.atualizaLinkFiador()`](src/main/java/academy/wakanda/wakanda_ai/wakander/application/api/WakanderAPI.java) | Atualiza dados do fiador | `FiadorDTO` | ❌ Público |

**Status:** 🔴 Sem autorização — qualquer um pode criar/editar

---

#### COMUNICAÇÃO (`/wakanda-ai/api/comunicacao`)

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `POST /envia` | [`ComunicacaoApi.enviaMensagem()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/api/ComunicacaoApi.java) | Envia mensagem WhatsApp | `MensagemRequest` | ❌ Público |
| `POST /convida` | [`ComunicacaoApi.convite()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/api/ComunicacaoApi.java) | Cria convite Discord | `DiscordConviteRequest` | ❌ Público |
| `POST /publica-notificacao` | [`ComunicacaoApi.publicaNotificacao()`](src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/api/ComunicacaoApi.java) | Publica notificação via SNS | `NotificacaoRequest` | ❌ Público |

**Status:** 🔴 Endpoint direto para enviar mensagens — sem controle de quota

---

#### FINANCEIRO (Assinatura)

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `POST /financeiro/assinatura` | [`AssinaturaAPI.criaAssinatura()`](src/main/java/academy/wakanda/wakanda_ai/financeiro/application/api/AssinaturaAPI.java) | Cria assinatura Asaas | `AssinaturaRequest` | ❌ Público |

**Status:** 🔴 Sem autorização — qualquer um pode criar assinatura

---

#### GAMIFICAÇÃO

**Jornada Wakander:**

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `POST /jornada/associar-discord` | [`JornadaWakanderApi`](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/api/JornadaWakanderApi.java) | Associa Discord ID | `DiscordAssociacaoRequest` | ❌ Público |

**Progresso Wakander:**

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `POST /progresso/{idWakander}` | [`ProgressoWakanderApi.criaProgresso()`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/progressowakander/application/api/ProgressoWakanderApi.java) | Registra progresso | `ProgressoRequest` | ❌ Público |
| `POST /progresso/sincroniza-antigos` | [`ProgressoWakanderApi`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/progressowakander/application/api/ProgressoWakanderApi.java) | Sincroniza histórico | `SincronizaRequest` | ❌ Público |
| `POST /progresso/sincroniza-antigo/{idWakander}` | [`ProgressoWakanderApi`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/progressowakander/application/api/ProgressoWakanderApi.java) | Sincroniza um Wakander | — | ❌ Público |

**Missão Progresso:**

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `POST /missao-progresso` | [`MissaoProgressoApi`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/application/api/MissaoProgressoApi.java) | Completa missão | `MissaoProgressoRequest` | ❌ Público |

**Jornada Wakanda (Catálogo):**

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `POST /jornada-wakanda` | [`JornadaWakandaAPI`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/jornadawakanda/application/api/JornadaWakandaAPI.java) | Cria jornada | `JornadaWakandaRequest` | ❌ Admin (?) |
| `POST /missao-wakanda` | [`MissaoWakandaAPI`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/application/api/MissaoWakandaAPI.java) | Cria missão | `MissaoWakandaRequest` | ❌ Admin (?) |
| `POST /tipo-missao` | [`TipoMissaoAPI`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/tipomissao/application/api/TipoMissaoAPI.java) | Cria tipo missão | `TipoMissaoRequest` | ❌ Admin (?) |
| `POST /trilha-wakanda` | [`TrilhaWakandaAPI`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/trilhawakanda/application/api/TrilhaWakandaAPI.java) | Cria trilha | `TrilhaWakandaRequest` | ❌ Admin (?) |
| `POST /classe-wakanda` | [`ClasseWakandaAPI`](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/classewakanda/application/api/ClasseWakandaAPI.java) | Cria classe | `ClasseWakandaRequest` | ❌ Admin (?) |

**Status:** 🔴 Tudo público, sem autenticação/autorização

---

#### FRONTEND/DASHBOARD

| Method+Path | Controller | Operação | DTOs | Auth |
|----------|----------|----------|------|------|
| `GET /dashboard` | [`DashboardApi`](src/main/java/academy/wakanda/wakanda_ai/frontend/web/dashboard/DashboardApi.java) | Retorna dados dashboard | — | ❌ Público |
| `GET /formulario` | [`FormularioApi`](src/main/java/academy/wakanda/wakanda_ai/frontend/web/formulario/FormularioApi.java) | Retorna estrutura de form | — | ❌ Público |

---

## §3 — Schedulers (Consolidado)

### 3.1 — Tabela Completa de @Scheduled

| Classe.método | Cron | Frequência | Domínio | O que faz | Risco |
|---|---|---|---|---|---|
| [`AutenticacaoSchedulerService.deletaTokensExpirados()`](src/main/java/academy/wakanda/wakanda_ai/autenticacao/application/service/AutenticacaoSchedulerService.java) | `0 0 5 * * 3` | **Toda quarta às 5h UTC** | `autenticacao` | DELETE tokens_table WHERE expiracao < NOW() | 🟢 Baixo — sem dependência |
| [`JornadaWakanderSchedulerService.agendaEnvioRelatorio()`](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/service/JornadaWakanderSchedulerService.java) | `0 0 8 * * MON` | **Segunda-feira às 8h UTC** | `jornadawakander` | Publica SNS com relatório de progresso | 🟡 Médio — SNS pode falhar |
| [`JornadaWakanderSchedulerService.geraRelatorioWakandersInativos()`](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/service/JornadaWakanderSchedulerService.java) | `0 0 8 * * 1` | **Segunda-feira às 8h UTC** | `jornadawakander` | SELECT Wakanders com 0 atividade 7 dias, gera relatório | 🟡 Médio — query pode ser lenta |

### 3.2 — Schedulers Desabilitados (Comentados)

| Classe.método | Cron Original | Motivo Provável |
|---|---|---|
| `JornadaWakanderSchedulerService.notificaWakandersNaoEstudaram()` | `0 0 8 * * 1,3,5` | Volume alto de notificações, possível spam |
| `JornadaWakanderSchedulerService.checaWakanderQueEstudaram()` | `0 0 8 * * 1,3,5` | Requer integração com Memberkit real |

---

## §4 — Resumo de Riscos de Segurança

### 🔴 CRÍTICO

1. **Todos os endpoints PUBLIC (sem @PreAuthorize)**
   - Qualquer pessoa pode criar Wakander, assinatura, enviar mensagens
   - **Recomendação:** Implementar `@PreAuthorize("hasRole('USER')")`

2. **Webhook sem validação de signature**
   - Memberkit, Asaas → não verificam `X-Signature` ou `X-Webhook-Secret`
   - **Recomendação:** Validar HMAC-SHA256 do payload

3. **Idempotência não garantida**
   - Webhook Asaas pode processar 2x se chamado 2x
   - **Recomendação:** Verificar `id` de evento na BD

### 🟡 MÉDIO

1. **Rate-limit faltando**
   - Endpoints `/envia` pode ser abusado (enviar 10k mensagens)
   - **Recomendação:** RateLimiter com `@RateLimiter`

2. **Logging de dados sensíveis**
   - CPF, email, whatsapp logados em DEBUG
   - **Recomendação:** Mascarar dados sensíveis

3. **Sem validação de entrada**
   - DTOs usam `@Valid` mas regras de negócio faltam
   - **Recomendação:** Adicionar `@NotBlank`, `@Pattern(regexp=...)`

---

## §5 — Fluxo Típico de Requisição Externa (end-to-end)

```
[Webhook externo: POST /memberkit]
  ↓
[MemberkitController.receberEventoMemberkit()]
  ├─ Validação: @Valid @RequestBody
  └─ Sem validação de signature ❌
    ↓
[MemberkitService.processaWebhook()]
    ↓ Strategy: List<MemberkitProcessor>
    ├─ findFirst(processor.validaSeProcessa(type))
    └─ processor.processaEvento(request)
      ↓
[MemberkitProcessor impl.]
  ├─ Deserialização JSON
  ├─ HTTP POST para Memberkit (se saída) ou BD (se entrada)
  └─ Sem logging de trace ID ❌
    ↓
[Response 200 OK ou Exception]
  ├─ Success: Webhook confirmado
  └─ Fail: Sem retry automático do provedor ❌
```

---

## §6 — Recomendações Finais

### Segurança (Prioridade 1)
- [ ] Implementar validação de webhook signature (HMAC-SHA256)
- [ ] Adicionar `@PreAuthorize` em todos endpoints
- [ ] Rate-limiter em endpoints públicos

### Confiabilidade (Prioridade 2)
- [ ] Idempotência: verificar event_id antes de processar
- [ ] Tracing: injetar trace ID em logs e SNS
- [ ] Retry: implementar exponential backoff em failures

### Observabilidade (Prioridade 3)
- [ ] Mascarar dados sensíveis em logs
- [ ] Alertar se scheduler falhar
- [ ] Métrica: latência por webhook, taxa de erro

