# Especificação Funcional — WakandaAI

> Reverse-engineered em 2026-06-10 (modo FULL, estratégia ASSISTED).
> Indicadores de confiança: ✅✅ VERIFIED (código + doc) · 🔸 CODE_ONLY · ⚠️ DOCS_ONLY · ❓ UNKNOWN.

## Visão Geral

WakandaAI automatiza e otimiza os processos internos da **Escola Wakanda Academy**:
gamificação da jornada de aprendizado, onboarding de alunos (Wakanders), cobranças/assinaturas
e comunicação multicanal (WhatsApp, Discord). É um backend Spring Boot orientado a eventos que
integra com serviços externos (Z-API, Asaas, Memberkit, Discord, Clint, N8N) e serve telas
Thymeleaf de formulário e dashboard.

## Contexto do Sistema

> Sem MeliSystemMCP/ disponível — atores inferidos de código, `application.yml`, security config e docs. 🔸

### Clientes de Entrada (quem chama o sistema)

| Cliente | Tipo | Interação | Evidência |
|---------|------|-----------|-----------|
| Equipe administrativa (DEV / LIDERANÇA) | Humano | Endpoints REST protegidos por JWT + roles (`hasRole("DEV")`, `hasRole("LIDERANCA")`) | `config/security/SecurityConfiguration.java` 🔸 |
| Aluno (Wakander) | Humano | Telas Thymeleaf de formulário (cadastro, dados complementares, fiador, associação Discord) | `frontend/web/formulario/FormularioApi.java` 🔸 |
| Asaas (webhook) | Serviço externo | `POST /financeiro/cobranca/processa-evento`, `POST /financeiro/assinaturas` | `financeiro/application/api/` 🔸 |
| Memberkit (webhook) | Serviço externo | `POST /memberkit`, `POST /memberkit/import` | `MemberkitController.java` 🔸 |
| Clint (CRM) / N8N | Serviço externo | Webhooks de comunicação e contato (filas `clint-*`) | `application.yml` + consumers 🔸 |
| Bot Discord (JDA) | Sistema | Eventos de comunidade Discord consumidos via fila `discord-requests` | `comunicacao/infra` + JDA 🔸 |

### Dependências de Saída (o que o sistema chama)

| Dependência | Tipo | Propósito | Evidência |
|-------------|------|-----------|-----------|
| Z-API | Serviço externo | Envio de mensagens e gestão de grupos WhatsApp | `ZApiClient` / `ZApiInfraClient` 🔸 |
| Asaas | Serviço externo | Cobranças e assinaturas (pagamentos) | `AsaasClient` / `AsaasWebClient` 🔸 |
| Memberkit | Serviço externo | LMS — cadastro de membros e acesso a cursos | `JornadaWakanderClient`, processadores Memberkit 🔸 |
| Discord | Serviço externo | Bot de comunidade (JDA) e mensagens | `DiscordClient` / `DiscordInfraClient` 🔸 |
| PostgreSQL | Datastore | Persistência (Flyway + HikariCP) | `pom.xml`, `application.yml` 🔸 |
| AWS SNS/SQS | Plataforma | Mensageria assíncrona FIFO entre módulos | `application.yml` (tópicos/filas) ✅✅ |

### Atores

| Ator | Tipo | Interação | Evidência |
|------|------|-----------|-----------|
| Administrador (DEV) | Humano interno | Gestão de catálogo (trilhas, jornadas, missões, classes, tipos), sincronizações | roles em SecurityConfiguration 🔸 |
| Liderança | Humano interno | Operações de liderança (paths `liderancaPaths`) | SecurityConfiguration 🔸 |
| Wakander (aluno) | Humano externo | Preenche formulários, associa Discord, progride na jornada | FormularioApi 🔸 |
| Serviços externos | Sistema | Disparam webhooks (Asaas, Memberkit, Clint) e consomem filas | controllers + consumers 🔸 |

## Capacidades / Casos de Uso

### CU — Gestão de Wakanders (Alunos) 🔸
- Cadastrar novo Wakander e completar cadastro via token de formulário.
- Editar dados pessoais, regularizar status, cancelar/reverter assinatura.
- Buscar Wakanders (por status de cadastro), estatísticas, dado oculto (CPF mascarado).
- Iniciar onboarding manual; enviar formulários em lote; atualizar dados Asaas.
- Associar conta Discord ao Wakander.
- Avançar progresso de jornada (conhecimento, habilidade, conquista, vibranium).

### CU — Gamificação ✅✅ (corroborado por `arquitetura-gameficacao.md`)
- **Catálogo (imutável)**: criar/consultar Trilhas → Jornadas → Missões; gerir Tipos de Missão e Classes (Bronze→Diamante).
- **Progresso (dinâmico)**: registrar progresso do Wakander, concluir missões, liberar próxima missão da jornada, ranking de Wakanders.
- **XP (derivado)**: acumular XP (cálculo estilo Fibonacci no domínio), subir de nível, promover de classe, registrar histórico de promoções.
- Atualizar missão com auxílio de IA (`PATCH /missoes/{idMissao}/atualiza-missao-com-ia`).
- Reordenar missões dentro da jornada; ajustar pontuação; desativar missão.

### CU — Financeiro 🔸
- Processar eventos de cobrança Asaas (criada, confirmada, vencida, negativada) via processadores Strategy.
- Criar assinaturas e atualizar dados de fiador via token.
- Refletir status financeiro no Wakander (REGULAR, CANCELADO, etc.).

### CU — Comunicação 🔸
- Enviar mensagem WhatsApp, convidar para grupo, publicar notificação.
- Processar eventos WhatsApp/Discord/Memberkit via chains de processadores.
- Onboarding e jornada conduzidos por mensagens automáticas (Z-API + Discord).

### CU — Autenticação 🔸
- Cadastro e login de usuário administrativo (JWT/Auth0).
- Geração de token de teste; reativação de token.

### CU — Onboarding & Jornada do Wakander 🔸
- Onboarding (manual e automático) com checklist e aulas assistidas.
- Acompanhamento de progresso de jornada e scheduler (jornadawakander).

## Regras de Negócio (extraídas do domínio) ✅✅ / 🔸
- Wakander só avança jornada se estiver **REGULAR** financeiramente (`validaWakanderRegular`). 🔸
- Não é permitido reentrar na jornada atual (`validaJornadaAtual` → 409). 🔸
- CPF único e `id_member_kit` único (constraints de tabela). 🔸
- Missão exige `xpBase > 0` e ao menos uma sabedoria (`validaXPBase`, `validaSabedorias`). ✅✅
- `OrdemMissao` exige `ordem >= 0` (factory `criar`). ✅✅
- XP é monotônico; nível sobe em loop enquanto `xpTotal >= xpProximoNivel`. ✅✅
- Conclusão de missão dispara XP (SNS → SQS) e pode promover de classe (Spring Event). ✅✅

## Fluxo Principal — Missão → XP → Classe → Notificação ✅✅
Concluir missão → publica `XpWakanderEventDTO` (SNS `xp-wakander-requests`) → `XpWakanderConsumer`
processa XP → atualiza nível → evento de promoção de classe → notificação ao Wakander.
(Detalhe e pontos de atenção em `technical-spec.md` e `docs/arquitetura-gameficacao.md` §6.)

## Fora de Escopo / Lacunas
- Telas de dashboard além dos dados servidos por `/painel-dados/dashboard`. ❓
- Contrato exato dos webhooks externos (payloads) não documentado como spec — ver DTOs no código. ⚠️
