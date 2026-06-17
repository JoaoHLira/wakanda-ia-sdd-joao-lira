# Functional Specification — WakandaAI

**Version**: 1.0
**Last Updated**: 2026-06-17
**Status**: Extracted from codebase (🔸 CODE_ONLY)

---

## System Context

### Missão
Automatizar e otimizar os processos internos da Escola Wakanda Academy — gamificação, onboarding, cobranças e comunicação com alunos.

### Domínios

| Domínio | Responsabilidade | Confiança |
|---------|-----------------|-----------|
| Wakander | Gestão do aluno (entidade central) | 🔸 CODE_ONLY |
| Autenticação | JWT + Spring Security + usuários admin | 🔸 CODE_ONLY |
| Comunicação | WhatsApp (Z-API), Discord, Clint CRM | 🔸 CODE_ONLY |
| Financeiro | Asaas (pagamentos/cobranças/assinaturas) | 🔸 CODE_ONLY |
| Gamificação | XP, Classes, Missões, Jornadas, Trilhas | 🔸 CODE_ONLY |
| Jornada Wakander | Onboarding, aulas assistidas, progresso, scheduler | 🔸 CODE_ONLY |

---

## Atores

| Ator | Tipo | Descrição | Evidência |
|------|------|-----------|-----------|
| Wakander (Aluno) | 👤 Humano | Estudante da Wakanda Academy | `Wakander.java` |
| Admin (UsuarioAdm) | 👤 Humano | Administrador com perfil LIDERANCA ou DEV | `UsuarioAdm.java` |
| Mentor | 👤 Humano | Acompanha progresso via frontend Thymeleaf | `GameficacaoViewController.java` |
| Asaas | 🤖 Sistema Externo | Gateway de pagamentos | `AsaasWebClient.java` |
| Z-API | 🤖 Sistema Externo | Serviço de WhatsApp | `ZApiInfraClient.java` |
| Memberkit | 🤖 Sistema Externo | LMS (Learning Management System) | `MemberkitClientHttp.java` |
| Discord | 🤖 Sistema Externo | Comunidade via bot JDA | `DiscordInfraClient.java` |
| Clint | 🤖 Sistema Externo | CRM | `ClintCRMInfra.java` |
| N8N | 🤖 Sistema Externo | Automação via webhooks | `application.yml` |

### Perfis de Admin

| Perfil | Acessos |
|--------|---------|
| `DEV` | Gamificação completa (missões, classes, jornadas, trilhas), estatísticas, importação Memberkit, token de teste |
| `LIDERANCA` | Onboarding manual, atualização de dados Asaas, status de cadastro, link de fiador (também herda `DEV`) |
| `NAO_VERIFICADO` | Login bloqueado |

---

## Casos de Uso

### UC-001: Matrícula de Wakander
**Ator**: Wakander
**Fluxo**: POST `/wakander/novo-wakander` → Cria registro com dados básicos → Gera token de autenticação → Envia link de cadastro via WhatsApp
**Regras**:
- CPF único por wakander
- Status inicial: `INCOMPLETO`
- Gera `Autenticacao` com token e data de expiração

### UC-002: Completa Cadastro
**Ator**: Wakander
**Fluxo**: Acessa link → Preenche formulário web → PATCH `/wakander/cadastro/{token}` → Valida token → Salva dados complementares
**Regras**:
- Token deve estar `VALIDO` e não expirado
- Após uso, token muda para `UTILIZADO`
- Status muda para `COMPLETO`

### UC-003: Onboarding de Wakander
**Ator**: Sistema (automático)
**Fluxo**: Cadastro completo → Cria registro de onboarding → Envia convite Discord → Adiciona ao grupo WhatsApp → Libera acesso Memberkit → Acompanha checkpoints
**Regras**:
- Checklist: `StatusChecklist` (7 passos sequenciais)
- Onboarding manual disponível via LIDERANÇA
- Discord: envia mensagem privada com botão de validação

### UC-004: Gerenciamento de Cobranças
**Ator**: Asaas (webhook)
**Fluxo**: POST `/financeiro/cobranca/processa-evento` → CobrancaProcessador (Strategy) → Atualiza status → Notifica wakander
**Eventos**:
- `PAYMENT_CREATED`, `PAYMENT_CONFIRMED`, `PAYMENT_OVERDUE`, `PAYMENT_CHARGEBACK_REQUESTED` (27 tipos)

### UC-005: Gerenciamento de Assinaturas
**Ator**: Asaas (webhook)
**Fluxo**: POST `/financeiro/assinaturas` → AssinaturaProcessor (Strategy) → Atualiza fiador → Cancela/reverte assinatura
**Eventos**: `SUBSCRIPTION_CREATED`, `SUBSCRIPTION_DELETED`, `SUBSCRIPTION_CANCELLED`

### UC-006: Envio de Mensagens WhatsApp
**Ator**: Admin (via frontend) / Sistema (automático)
**Fluxo**: POST `/whatsapp-message/envia` → Publica SNS → SQS consome → Z-API send-text → Processa resposta
**Tipos**: Normal, AddToGroup, RemoveToGroup, TodayOnly

### UC-007: Gamificação — Missões
**Ator**: DEV / Sistema
**Fluxo**:
- Criação: POST `/missoes` → Associa a jornada → Define XP base → Ordem
- Conclusão: Wakander completa → MissaoProgresso cria/conclui → XP adicionado
- Reavaliação: Ao concluir missão, reavalia disponibilidade das próximas
**Regras**:
- Missões têm ordem (`OrdemMissao`) — valida `ordem >= 0`
- Missão pode ter classe mínima como requisito
- Missão pode ter missão pai (encadeamento)
- Missão contém `Sabedorias` (5 dimensões: teórico, processo, know-how, comportamental, criativo)

### UC-008: Gamificação — Progresso e Ranking
**Ator**: Wakander / Mentor
**Fluxo**: Ações disparam eventos → ProgressoWakander atualiza → XP acumula → Ranking calculado
**Regras**:
- Ranking por destaque geral e por período
- Níveis calculados por XP total
- Classes promovem automaticamente ao atingir requisitos

### UC-009: Gamificação — Classes
**Ator**: DEV
**Fluxo**: Criação → Define nível necessário, sabedorias, missões necessárias → Wakander promove automaticamente
**Regras**:
- Ordem sequencial de classes (Bronze → Prata → Ouro → ... → Diamante)
- `@ElementCollection` de missões necessárias
- Promoção validada por `XpPromocaoClasseService`

### UC-010: Sincronização com Memberkit (LMS)
**Ator**: Memberkit (webhook)
**Fluxo**: POST `/memberkit` → MemberkitProcessor (Strategy) → Importa aulas → Cria missões externas → Sincroniza progresso
**Eventos**: `LOGIN_FEITO`, `AULA_ASSISTIDA`, `LOGIN_ENVIADO`, `AULA_CRIADA`

### UC-011: Relatórios e Notificações Agendadas
**Ator**: Sistema (scheduler)
**Jobs**:
- Quarta 05:00 → Deleta tokens expirados
- Segunda 08:00 → Envia relatório semanal de inativos
- Segunda 08:00 → Gera relatório de wakanders inativos

### UC-012: Autenticação de Admin
**Ator**: Admin
**Fluxo**: POST `/autenticacao/login` → Valida BCrypt → Gera JWT (Auth0 HMAC256) → Retorna token Bearer
**Regras**:
- Token expira em 8 horas
- Issuer: `wakanda-ai`
- Perfis: LIDERANCA, DEV, NAO_VERIFICADO

### UC-013: Associação Discord
**Ator**: Wakander
**Fluxo**: POST `/wakander/jornada/associar-discord` → Valida username → Associa ID Discord ao wakander → Atualiza cargo

### UC-014: Cancelamento de Assinatura
**Ator**: Wakander / Sistema
**Fluxo**:
- Wakander solicita cancelamento → Status muda para `CANCELAMENTO_SOLICITADO`
- Liderança pode reverter cancelamento
- Clint CRM notificado sobre cancelamento

### UC-015: Gestão de Fiador
**Ator**: LIDERANCA
**Fluxo**: Gera link de atualização → Wakander acessa → Preenche dados do fiador → Asaas atualiza customer

---

## Regras de Negócio Transversais

1. **Idempotência SQS**: Handlers devem verificar estado antes de processar (`if (missaoProgresso.isConcluida()) return;`)
2. **Transações**: `@Transactional` em ApplicationServices que mutam estado; eventos pós-processo com `@TransactionalEventListener(AFTER_COMMIT)`
3. **Agregação por UUID**: Sem `@OneToMany`/`@ManyToOne` entre agregados — navegação via repositório
4. **Value Objects com Factories**: Validam invariantes no construtor (ex: `OrdemMissao.criar(int)`)
5. **Deduplication ID**: `UUID.randomUUID()` sem garantia de idempotência real — handlers precisam checar estado
