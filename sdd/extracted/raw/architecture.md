# Arquitetura Aplicacional - Wakanda-AI-Dev

**Data de Extração:** 2026-05-27  
**Versão:** Java 17, Spring Boot 3.3.4  
**Context Path:** `/wakanda-ai/api`

---

## §1 — Visão Geral de Pacotes

A aplicação é organizada em 7 **domínios raiz** (bounded contexts do DDD) com estrutura consistente. Context path global: `/wakanda-ai/api`.

### Domínios Identificados

| Domínio | Pacote Raiz | Sub-estrutura | Responsabilidade |
|---------|------------|--------------|------------------|
| **Wakander** | `academy.wakanda.wakanda_ai.wakander` | `application/api`, `application/service`, `application/event`, `domain`, `infra` | Gerenciar entidades principais (aluno), cadastro, progressão, status financeiro |
| **Gamificação Catálogo** | `academy.wakanda.wakanda_ai.gameficacao.catalogo.*` | `*/application/api`, `*/application/service`, `*/domain`, `*/infra` | Catálogos: Jornadas, Missões, Trilhas, TipoMissão, Memberkit (integração) |
| **Gamificação Progresso** | `academy.wakanda.wakanda_ai.gameficacao.progresso.*` | `*/application/api`, `*/application/service`, `*/domain`, `*/infra` | XP, Classes, Histórico, Progresso geral do Wakander |
| **Jornada Wakander** | `academy.wakanda.wakanda_ai.jornadawakander` | `application/api`, `application/service`, `domain`, `infra` | Onboarding, Discord, Memberkit webhook, AulaAssistida, ProgressoOnboard |
| **Autenticação** | `academy.wakanda.wakanda_ai.autenticacao` | `application/api`, `application/service`, `domain`, `infra`, `repository` | Login, tokens, usuários ADM, autorizações |
| **Financeiro** | `academy.wakanda.wakanda_ai.financeiro` | `application/api`, `application/service`, `domain`, `infra` | Assinaturas Asaas, Cobrança, integração financeira |
| **Comunicação** | `academy.wakanda.wakanda_ai.comunicacao` | `application/api`, `application/service`, `infra` | WhatsApp (Z-API), Discord, Clint CRM, notificações |

### Estrutura Transversal

- **`config`** → Spring Security, JWT, OpenAPI/Swagger
- **`frontend/web`** → Dashboard Thymeleaf, Formulários (cadastro, cancelamento, dados complementares)
- **`handler`** → Tratamento de exceções (`APIException`, `RestResponseEntityExceptionHandler`)
- **`constants`** → `TopicNames` (SNS/SQS), `MensagensWhatsapp`, `MensagensDiscord`
- **`utils`** → Utilitários (NomeUtils, etc.)
- **`docs`** → Swagger annotations

---

## §2 — ApplicationServices (Orquestradores)

### Domínio: Wakander

**[WakanderApplicationService](src/main/java/academy/wakanda/wakanda_ai/wakander/application/service/WakanderApplicationService.java)**
- **Domínio:** Wakander
- **Responsabilidade:** Orquestra todo o ciclo de vida do Wakander (cadastro, progressão de jornada, atualização de dados, cancelamento de assinatura)
- **Dependências:** `WakanderRepository`, `ProgressoWakanderRepository`, `PublicadorNotificacaoSns`, `ApplicationEventPublisher`, `AsaasClient`, `OnboardingWakanderService`, `AutenticacaoService`, `TokenService`
- **Métodos Públicos:**
  - `matriculaWakander(WakanderNovoRequest)` → Cria novo Wakander (DEPRECATED)
  - `geraLinkAtualizacaoFiador(UUID)` → Gera link com token para atualizar fiador
  - `buscaWakanderPorId(UUID)` → Busca Wakander por ID
  - `regularizaWakander(UUID)` → Altera status financeiro para REGULAR
  - `buscaWakanderPorIdMemberKit(String)` → Busca por ID Memberkit
  - `atualizaProgressoParaJornada(UUID, JornadaWakanda)` → Avança Wakander para próxima jornada
  - `solicitaCancelamentoWakander(Wakander)` → Inicia fluxo de cancelamento
  - `completaCadastroWakander(String token, ...)` → Completa cadastro e publica evento
  - `cancelaAssinatura(UUID, ...)` → Cancela assinatura (publica evento)
  - `buscaTodosOsWakanderComPaginacao(Pageable, ...)` → Busca com filtros
  - `solicitaEnvioDeFormularioDadosComplementares()` → Envia formulários para Wakanders com dados incompletos
- **Transacional:** Não declarado explicitamente (usa `@Service`)
- **Eventos Publicados:** `CadastroCompletoEvent`, `AssinaturaCanceladaEvent`

---

### Domínio: Gamificação - Progresso

**[ProgressoWakanderApplicationService](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/progressowakander/application/service/ProgressoWakanderApplicationService.java)**
- **Domínio:** Gamificação (Progresso)
- **Responsabilidade:** Gerenciar progresso geral do Wakander no sistema (novo progresso, processamento por tipo, ranking)
- **Dependências:** `ProgressoGameficacaoRepository`, `WakanderRepository`, `JornadaProgressoRepository`, `JornadaWakandaRepository`, `List<ProgressoWakanderProcessor>`
- **Métodos Públicos:**
  - `novoProgresso(UUID)` → Cria novo registro de progresso
  - `processaPorTipoProgresso(ProgressoWakanderEventDto)` → Processa eventos de progresso (strategy pattern)
  - `rankingDestaqueGeral(Pageable)` → Retorna ranking de Wakanders
  - `rankingDestaquePorPeriodo(LocalDate, LocalDate, Pageable)` → Ranking filtrado por período
  - `sincronizaWakandersAntigos()` → Cria progresso para Wakanders legacy sem registro

**[XpWakanderApplicationService](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/application/service/XpWakanderApplicationService.java)**
- **Domínio:** Gamificação (XP/Classes)
- **Responsabilidade:** Processa XP ganho em missões e gerencia promoção de classes
- **Dependências:** `XpWakanderRepository`, `ProgressoGameficacaoRepository`, `ApplicationEventPublisher`, `HistoricoClasseWakanderRepository`, `ClasseWakandaRepository`, `XpPromocaoClasseService`, `MissaoProgressoService`
- **Métodos Públicos:**
  - `processaXP(XpWakanderEventDTO)` → Adiciona XP, atualiza nível, publica evento de promoção
  - `processaPromocaoClasse(XpPromocaoClasseDTO)` → Valida e promove Wakander de classe se atender critérios
- **Transacional:** `@Transactional` **na classe**
- **Eventos Publicados:** `XpPromocaoClasseEvent`

**[MissaoProgressoApplicationService](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/application/service/MissaoProgressoApplicationService.java)**
- **Domínio:** Gamificação (Progresso de Missões)
- **Responsabilidade:** Gerenciar progresso de missões, validar disponibilidade, finalizar missões e disparar cascatas de eventos
- **Dependências:** `MissaoProgressoRepository`, `MissaoWakandaRepository`, `PublicadorNotificacaoSns`, `ProgressoGameficacaoRepository`, `JornadaProgressoService`, `JornadaWakandaRepository`, `MissaoDisponibilidadeService`, `TopicNames`
- **Métodos Públicos:**
  - `concluiMissao(ProgressoWakanderEventDto)` → Finaliza missão (webhook Memberkit), publica XP, processa próxima
  - `criaProgressoDeMissao(MissaoProgressoRequest)` → Cria novo registro de progresso de missão
  - `concluiMissaoManualmente(UUID)` → Conclui via API (Game Master)
  - `reavaliaMissoesDisponiveis(UUID)` → Re-cria registros de progresso para missões elegíveis
  - `listaDisponibilidadeMissoes(UUID, UUID)` → Lista missões disponíveis para Wakander de uma jornada
  - `listaMissoesConcluidas(UUID, Pageable)` → Retorna missões finalizadas
- **Publica SNS:** `XpWakanderEventDTO` → topic `xp-wakander-requests`

---

### Domínio: Jornada Wakander

**[OnboardingWakanderApplicationService](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/service/OnboardingWakanderApplicationService.java)**
- **Domínio:** Jornada Wakander
- **Responsabilidade:** Gerenciar etapas de onboarding (cadastro, Discord, WhatsApp, primeira aula, etc.)
- **Dependências:** `OnboardingWakanderRepository`, `WakanderRepository`, `DiscordService`
- **Métodos Públicos:**
  - `save(OnboardingWakander)` → Persiste onboarding
  - `buscaOnboardingPorIdWakander(UUID)` → Busca registro
  - `retornaChecklist(OnboardingWakander)` → Gera checklist visual de etapas completas
  - `associarUsuarioDiscord(DiscordRequest)` → Associa Discord ID com Wakander, atualiza cargo

**[AulaAssistidaApplicationService](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/service/memberkit/AulaAssistidaApplicationService.java)**
- **Domínio:** Jornada Wakander
- **Responsabilidade:** Processa webhook "aula assistida" do Memberkit, registra aula, publica progresso de missão
- **Dependências:** `WakanderRepository`, `JornadaWakanderRepository`, `List<AulaAssistidaProcessador>`, `MissaoWakandaRepository`, `PublicadorNotificacaoSns`, `TopicNames`
- **Métodos Públicos:**
  - `processaEventoAulaAssistida(AulaMemberKitDTO)` → Webhook do Memberkit: valida aula, registra, processa com strategy
- **Publica SNS:** `ProgressoWakanderEventDto` → topic `progresso-wakander-requests`

---

### Domínio: Gamificação - Catálogo

**[MemberkitApplicationService](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/memberkit/application/service/MemberkitApplicationService.java)**
- **Domínio:** Gamificação (Catálogo - Memberkit)
- **Responsabilidade:** Importa cursos/conteúdos do Memberkit como missões, processa webhooks de eventos
- **Dependências:** `TipoMissaoRepository`, `JornadaWakandaRepository`, `List<MemberkitProcessor>`, `MemberkitIntegrationImporter`
- **Métodos Públicos:**
  - `importaMemberkit(UUID idTipoMissao, UUID idJornada)` → Importa cursos como missões
  - `processaWebhook(MemberkitEventRequest)` → Webhook do Memberkit (strategy pattern)

**[JornadaWakandaApplicationService](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/jornadawakanda/application/service/JornadaWakandaApplicationService.java)**
- **Domínio:** Gamificação (Catálogo - Jornadas)
- **Responsabilidade:** Gerenciar jornadas (coleções de trilhas/missões)
- Métodos: CRUD básico

**[MissaoWakandaApplicationService](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/application/service/MissaoWakandaApplicationService.java)**
- **Domínio:** Gamificação (Catálogo - Missões)
- **Responsabilidade:** Gerenciar catálogo de missões (CRUD)

**[TrilhaApplicationService](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/trilhawakanda/application/service/TrilhaApplicationService.java)**
- **Domínio:** Gamificação (Catálogo - Trilhas)
- **Responsabilidade:** Gerenciar trilhas (coleções de missões)

**[ClasseWakandaApplicationService](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/classewakanda/application/service/ClasseWakandaApplicationService.java)**
- **Domínio:** Gamificação (XP/Classes)
- **Responsabilidade:** Gerenciar classes (níveis) do Wakander

---

### Domínio: Financeiro

**[AssinaturaAsaasApplicationService](src/main/java/academy/wakanda/wakanda_ai/financeiro/application/service/assinatura/AssinaturaAsaasApplicationService.java)**
- **Domínio:** Financeiro
- **Responsabilidade:** Processa webhooks de assinatura do Asaas (criar, atualizar, cancelar)
- **Dependências:** `List<AssinaturaProcessorAsaas>`, `WakanderService`, `AsaasClient`, `AutenticacaoService`
- **Métodos Públicos:**
  - `processaAssinaturaPorEvento(AssinaturaAsaasDto)` → Webhook Asaas: usa strategy para processar tipo de evento
  - `atualizaDadosFiador(String token, FiadorDTO)` → Atualiza dados do fiador no Asaas
- **Webhooks:** `/wakanda-ai/api/financeiro/assinaturas` (POST)

**[CobrancaAsaasApplicationService](src/main/java/academy/wakanda/wakanda_ai/financeiro/application/service/cobranca/CobrancaAsaasApplicationService.java)**
- **Domínio:** Financeiro
- **Responsabilidade:** Processa webhooks de cobrança do Asaas (pagamento, falha, etc.)
- **Dependências:** `CobrancaRepository`, `List<CobrancaProcessadorAsaas>`
- **Métodos Públicos:**
  - `processaEvento(CobrancaAsaasDto)` → Webhook Asaas: usa strategy para processar tipo de cobrança
- **Webhooks:** `/wakanda-ai/api/financeiro/cobranca/processa-evento` (POST)

---

### Domínio: Autenticação

**[AutenticacaoApplicationService](src/main/java/academy/wakanda/wakanda_ai/autenticacao/application/service/AutenticacaoApplicationService.java)**
- **Domínio:** Autenticação
- **Responsabilidade:** Gerencia tokens, autenticação de Wakanders (via token), validação
- **Métodos:** `buscaWakanderPeloToken()`, `alteraStatusTokenParaUtilizado()`, `authenticate()`, etc.

**[UsuarioAdmApplicationService](src/main/java/academy/wakanda/wakanda_ai/autenticacao/application/service/UsuarioAdmApplicationService.java)**
- **Domínio:** Autenticação
- **Responsabilidade:** Gerencia usuários administradores (cadastro, login)

---

### Domínio: Comunicação

**[ComunicacaoApplicationService](src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/service/ComunicacaoApplicationService.java)**
- **Domínio:** Comunicação
- **Responsabilidade:** Orquestra envio de mensagens WhatsApp, convites Discord, integração Clint CRM
- **Dependências:** `ZApiClient`, `DiscordClient`, `PublicadorNotificacaoSns`, `ClintCRM`, `CancelaClintService`, `TopicNames`
- **Métodos Públicos:**
  - `enviaMensagemWhatsapp(MensagemRequest)` → Envia via Z-API
  - `adicionaWakanderAoGrupo(ZAPIPayloadAdiconaAoGrupo)` → Adiciona ao grupo WhatsApp
  - `convidaParaCanalDiscord(DiscordConviteRequest)` → Cria convite Discord
  - `removeWakanderDoGrupo(...)` → Remove do grupo WhatsApp
  - `enviaContatoParaClint(ClintContatoRequest)` → Envia contato para CRM Clint

**[ComunicacaoWhatsappApplicationService](src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/service/whatsapp/ComunicacaoWhatsappApplicationService.java)**
- **Domínio:** Comunicação
- **Responsabilidade:** Processa webhooks e eventos WhatsApp (Z-API consumer)

**[ComunicacaoDiscordApplicationService](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/service/discord/ComunicacaoDiscordApplicationService.java)**
- **Domínio:** Jornada Wakander / Comunicação
- **Responsabilidade:** Processa webhooks Discord (evento de envio, atualização de cargo)

---

## §3 — Controllers REST (`*Api` em `application/api/`)

Todos os endpoints herdam o context path `/wakanda-ai/api` do `application.yml`.

### Autenticação

**[AutenticacaoApi](src/main/java/academy/wakanda/wakanda_ai/autenticacao/application/api/AutenticacaoApi.java)**
- `POST /autenticacao/cadastro` → Cadastra usuário ADM (`UsuarioAdmCadastroRequest`)
- `POST /autenticacao/login` → Autentica usuário ADM (`UsuarioAdmLoginDto`) → `AuthenticationResponseDto` (JWT token)
- `GET /autenticacao/token-teste` → Valida token vigente
- `PATCH /autenticacao/reativa-token/{token}` → Reatifica token expirado → `AutenticacaoTokenReativado`

### Comunicação

**[ComunicacaoApi](src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/api/ComunicacaoApi.java)**
- `POST /whatsapp-message/envia` → Envia mensagem WhatsApp (`MensagemRequest`)
- `POST /whatsapp-message/convida` → Cria convite Discord (`DiscordConviteRequest`) → `DiscordConviteResponse`
- `POST /whatsapp-message/publica-notificacao` → Publica notificação teste (`NotificacaoRequest`)

### Gamificação - Progresso

**[ProgressoWakanderApi](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/progressowakander/application/api/ProgressoWakanderApi.java)**
- `POST /gameficacao/progresso/{idWakander}` → Cria novo progresso → `ProgressoWakanderResponse` (201)
- `GET /gameficacao/progresso/ranking-wakanders` → Lista ranking com paginação (com filtro de período opcional) → `Page<RankingWakanderProjection>`
- `POST /gameficacao/progresso/sincroniza-antigos` → Sincroniza Wakanders legacy → `SincronizacaoProgressoResponse`
- `POST /gameficacao/progresso/sincroniza-antigo/{idWakander}` → Sincroniza um Wakander específico
- `GET /gameficacao/progresso/{idWakander}` → Progresso individual → `ProgressoIndividualResponse`

**[MissaoProgressoApi](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/application/api/MissaoProgressoApi.java)**
- `POST /gameficacao/missao-progresso` → Cria novo progresso de missão (`MissaoProgressoRequest`) → `MissaoProgressoResponse` (201)
- `PATCH /gameficacao/missao-progresso/{idMissaoProgresso}/conclui` → Conclui missão manualmente (204)
- `GET /gameficacao/missao-progresso/wakander/{idWakander}/jornada/{idJornada}` → Lista missões disponíveis → `List<MissaoDisponibilidadeResponse>`
- `GET /gameficacao/missao-progresso/progresso/{idProgressoWakander}/missoes-concluidas` → Missões finalizadas com paginação → `Page<MissaoConcluidaResponse>`

### Jornada Wakander

**[JornadaWakanderApi](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/application/api/JornadaWakanderApi.java)**
- `POST /wakander/jornada/associar-discord` → Associa Discord ao Wakander (`DiscordRequest`)

### Memberkit (Webhook)

**[MemberkitController](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/memberkit/application/api/MemberkitController.java)**
- `POST /memberkit` → Webhook do Memberkit (`MemberkitEventRequest`)
- `POST /memberkit/import` → Importa cursos como missões (params: `idTipoMissao`, `idJornada`)

### Formulários (Frontend)

**[FormularioApi](src/main/java/academy/wakanda/wakanda_ai/frontend/web/formulario/FormularioApi.java)**
- `GET /formulario/cadastro/{token}` → Renderiza formulário de cadastro de Wakander (Thymeleaf)
- `GET /formulario/cancelamento-assinatura/{idWakander}` → Renderiza formulário de cancelamento (Thymeleaf)
- `GET /formulario/dados-complementares/{token}` → Renderiza formulário de dados pessoais (Thymeleaf)
- `GET /formulario/{username}/{idDiscord}/associar-discord` → Renderiza formulário de associação Discord (Thymeleaf)
- `GET /formulario/resposta-discord` → Página de confirmação Discord (Thymeleaf)

### Webhooks Externos - Financeiro

**[AssinaturaAPI](src/main/java/academy/wakanda/wakanda_ai/financeiro/application/api/AssinaturaAPI.java)**
- `POST /financeiro/assinaturas` → Webhook Asaas de assinatura (`AssinaturaAsaasDto`)
- `PATCH /financeiro/assinaturas/fiador/{token}` → Atualiza dados do fiador (`FiadorDTO`)

**[CobrancaAPI](src/main/java/academy/wakanda/wakanda_ai/financeiro/application/api/CobrancaAPI.java)**
- `POST /financeiro/cobranca/processa-evento` → Webhook Asaas de cobrança (`CobrancaAsaasDto`)

### Dashboard (Frontend)

**[DashboardApi](src/main/java/academy/wakanda/wakanda_ai/frontend/web/dashboard/DashboardApi.java)**
- Renderiza dashboard administrativo (Thymeleaf)

---

## §4 — Bounded Contexts

### BC1: Wakander (Aluno)

- **Pacote:** `academy.wakanda.wakanda_ai.wakander`
- **Agregado Raiz:** `Wakander` (UUID `idWakander`)
  - Value Objects: `WakanderContato`, `WakanderFinanceiro`, `WakanderFiador`, `WakanderAulaAssistida`
  - Enums: `StatusCadastro` (INCOMPLETO, COMPLETO), `WakanderStatusFinanceiro` (REGULAR, CANCELAMENTO_SOLICITADO, CANCELADO)
- **Referencia:**
  - `idMemberKit` (String) → Memberkit
  - `idDiscord` (String) → Discord
  - `JornadaWakanda` (Enum) → Jornada Wakander / Catálogo
  - Fiador (dados financeiros) → Financeiro
- **Eventos Publicados:**
  - `CadastroCompletoEvent` → publica em SNS (topic: `zapi-requests`, `memberkit-requests`, `discord-request`)
  - `AssinaturaCanceladaEvent` → processado por listeners
  - `DiscordEventRequest` → publica em SNS
- **Consumers (SQS):** Nenhum direto (apenas event listeners locais)

### BC2: Gamificação - Catálogo

- **Pacotes:** 
  - `academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda`
  - `academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda`
  - `academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda`
  - `academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao`
  - `academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit`
- **Agregados:**
  - `JornadaWakanda` (UUID `idJornada`) — coleção de trilhas
  - `MissaoWakanda` (UUID `idMissao`) — conteúdo atomizado com XP base, Sabedorias, OrdemMissão
  - `Trilha` (UUID `idTrilha`) — coleção de missões
  - `TipoMissao` (UUID `idTipoMissao`) — categoria
- **Referências:**
  - Memberkit → ID externo do curso
  - Classes (para validar disponibilidade de missão)
- **Integração Memberkit:**
  - Webhook: `POST /memberkit` → processa eventos (type-based processor)
  - Import: `POST /memberkit/import` → carrega cursos como missões

### BC3: Gamificação - Progresso

- **Pacotes:**
  - `academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander`
  - `academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso`
  - `academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso`
  - `academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander`
  - `academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda`
  - `academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse`
- **Agregados:**
  - `ProgressoWakander` (UUID `idProgressoWakander`) — raiz de toda a gamificação de um Wakander
  - `MissaoProgresso` (UUID `idMissaoProgresso`) — estado de uma missão (Em Andamento, Concluída)
  - `JornadaProgresso` (UUID `idJornadaProgresso`) — estado de uma jornada
  - `XpWakander` (UUID `idXpWakander`) — nível, XP total acumulado
  - `HistoricoClasseWakander` → histórico de promoções entre classes
  - `ClasseWakanda` (UUID `idClasse`) — catálogo (Classe 1, 2, 3, ...)
- **Eventos/SQS:**
  - **Consumer:** `ProgressoWakanderConsumer` ← topic `progresso-wakander-requests` (aula assistida)
  - **Consumer:** `XpWakanderConsumer` ← topic `xp-wakander-requests` (XP ganho em missão)
  - **Publisher:** `PublicadorNotificacaoSns.enviaNotificacaoSns()` → topic `xp-wakander-requests`
  - **EventListener:** `XpPromocaoClasseEvent` → promoção de classe

### BC4: Jornada Wakander (Onboarding e Integração Memberkit)

- **Pacote:** `academy.wakanda.wakanda_ai.jornadawakander`
- **Agregados:**
  - `OnboardingWakander` (UUID `idWakander`) — etapas: cadastroConfirmado, entrouDiscord, entrouGrupoWhatsapp, acessouPlataforma, etc.
  - `AulaAssistida` (UUID `idAulaAssistida`) — registro de aula concluída no Memberkit
  - `ProgressoWakander` (referenciado)
- **EventListeners:**
  - `ProgressoOnboardConsumer` responde a `CadastroCompletoEvent`:
    - Atualiza `OnboardingWakander.cadastroConfirmado`
    - Envia checklist via WhatsApp
    - Adiciona ao grupo WhatsApp
    - Convida para Discord
    - Cadastra no Memberkit (publica SNS topic `memberkit-requests`)
    - Notifica grupo de líderes
    - **Cria novo `ProgressoWakander`** (gamificação)
- **Consumers (SQS):**
  - `JornadaWakanderConsumerSqs` ← Various topics

### BC5: Autenticação

- **Pacote:** `academy.wakanda.wakanda_ai.autenticacao`
- **Agregados:**
  - Tokens (embarcados no Wakander ou UsuarioAdm)
  - `UsuarioAdm` (para dashboard)
- **Responsabilidades:**
  - Gera tokens para Wakanders (cadastro, dados complementares)
  - Autentica usuários ADM
  - Valida tokens

### BC6: Financeiro

- **Pacote:** `academy.wakanda.wakanda_ai.financeiro`
- **Agregados:**
  - `Assinatura` (referenciada por Wakander via UUID `idAssinatura`)
  - `Cobranca` → registra pagamentos/falhas Asaas
- **Integração Asaas:**
  - Webhook `POST /financeiro/assinaturas` → processa eventos (create, update, cancel)
  - Webhook `POST /financeiro/cobranca/processa-evento` → pagamento, falha, etc.
  - Consulta cliente/assinatura via `AsaasClient`
- **Fluxos:**
  - Novo cadastro Asaas → publica SNS topic `asaas-requests` → sincroniza dados
  - Cancelamento de assinatura → `AssinaturaCanceladaEvent` → listeners processam

### BC7: Comunicação

- **Pacote:** `academy.wakanda.wakanda_ai.comunicacao`
- **Responsabilidades:**
  - **WhatsApp (Z-API):** envia mensagens, adiciona grupos, remove de grupos
  - **Discord:** convida para servidor, atualiza cargos
  - **Clint CRM:** envia contatos
- **SNS Topics Consumidos e Publicados:**
  - Publica: `zapi-requests` (mensagens WhatsApp)
  - Publica: `discord-request` (envio para Discord)
  - Publica: `clint-requests` (contatos Clint)
  - Consumer SQS: `ComunicacaoConsumerSqs` processa respostas/webhooks

---

## §5 — Fluxos End-to-End

### Fluxo 1: Onboarding Completo (Novo Cadastro Asaas → Jornada Wakander)

```
[Asaas Webhook] → /financeiro/assinaturas (POST)
  ↓
AssinaturaAsaasApplicationService.processaAssinaturaPorEvento()
  ↓ (cria Wakander com status INCOMPLETO, jornada ONBOARD)
Wakander persisted
  ↓
[Memberkit Webhook] → /memberkit (POST) [aula assistida]
  ↓
AulaAssistidaApplicationService.processaEventoAulaAssistida()
  ↓
Registra AulaAssistida, publica ProgressoWakanderEventDto
  ↓ (SNS topic: progresso-wakander-requests)
ProgressoWakanderConsumer processa
  ↓
[Formulário] → FormularioApi /formulario/cadastro/{token}
  ↓
WakanderApplicationService.completaCadastroWakander()
  ↓
Publica CadastroCompletoEvent (applicationEventPublisher)
  ↓
ProgressoOnboardConsumer responde (6 @EventListeners):
  1. Atualiza OnboardingWakander.cadastroConfirmado
  2. Envia checklist WhatsApp (SNS topic: zapi-requests)
  3. Adiciona grupo WhatsApp (SNS)
  4. Convida Discord (SNS topic: discord-request)
  5. Cadastra no Memberkit (SNS topic: memberkit-requests)
  6. Notifica líderes (SNS)
  7. **Cria ProgressoWakander** (BC Gamificação)
  ↓
Wakander: status COMPLETO, jornada JORNADA_CONHECIMENTO
OnboardingWakander: etapas preenchidas conforme eventos
ProgressoWakander: novo registro com jornadas iniciais
```

### Fluxo 2: Assistir Aula → XP → Progresso de Missão → Promoção de Classe

```
[Memberkit Webhook] → /memberkit (POST) [aula concluída]
  ↓
AulaAssistidaApplicationService.processaEventoAulaAssistida()
  ↓ (registra AulaAssistida, atualiza Wakander.ultimaAulaAssistida)
Publica ProgressoWakanderEventDto (SNS topic: progresso-wakander-requests)
  ↓
ProgressoWakanderConsumer processa
  ↓ (identifica tipo: MISSAO_PROGRESSO)
MissaoProgressoApplicationService.concluiMissao()
  ↓
MissaoProgresso.concluiMissao() + publica XpWakanderEventDTO
  ↓ (SNS topic: xp-wakander-requests)
XpWakanderConsumer.consumeXpWakanderQueueMessage()
  ↓
XpWakanderApplicationService.processaXP()
  ↓
XpWakander.adicionarXpEAtualizarNivel()
  ↓
Publica XpPromocaoClasseEvent
  ↓
XpWakanderConsumer.@EventListener promoveClasseXpWakander()
  ↓
XpWakanderApplicationService.processaPromocaoClasse()
  ↓ (valida se ganhou XP suficiente + concluiu missões obrigatórias)
HistoricoClasseWakander.concluiClasseAtualAndIniciaProximaClasse()
  ↓
Wakander promovido, MissaoProgressoService.reavaliaMissoesDisponiveis()
  ↓ (libera novas missões da classe nova)
MissaoProgressoRepository cria novos MissaoProgresso registros
  ↓
[Opcional webhook] → notifica Discord/WhatsApp da promoção
```

### Fluxo 3: Conclusão de Missão via API (Game Master Manual)

```
[Dashboard] → /gameficacao/missao-progresso/{idMissaoProgresso}/conclui (PATCH)
  ↓
MissaoProgressoApi.concluiMissaoManualmente()
  ↓
MissaoProgressoApplicationService.concluiMissaoManualmente()
  ↓
(mesmo flow do Fluxo 2 a partir de finalizaMissaoAtualEPublicaXp)
```

### Fluxo 4: Cancelamento de Assinatura (Wakander Request)

```
[API] → WakanderApplicationService.solicitaCancelamentoWakander()
  ↓
Wakander.mudaStatusFinanceiro(CANCELAMENTO_SOLICITADO)
  ↓
Envia formulário de cancelamento via WhatsApp (SNS topic: zapi-requests)
  ↓
[Formulário] → FormularioApi /formulario/cancelamento-assinatura/{idWakander}
  ↓
WakanderApplicationService.cancelaAssinatura()
  ↓
Wakander.cancelaAssinatura() → status CANCELADO
  ↓
Publica AssinaturaCanceladaEvent
  ↓
AssinaturaCanceladaConsumer responde
  ↓ (não mapeado no código atual)
[Optional] Remove do grupo WhatsApp, notifica Discord, etc.
```

### Fluxo 5: Atualização de Dados Fiador (Formulário com Token)

```
[Formulário] → FormularioApi /formulario/atualiza-fiador/{token}
  ↓
WakanderApplicationService.atualizaFiador()
  ↓
Wakander.atualizaFiador(FiadorDTO)
  ↓
AssinaturaAsaasApplicationService.atualizaDadosFiador()
  ↓
AsaasClient.atualizaDadosFiador(idAsaas)
  ↓
Fiador atualizado em ambos os sistemas
  ↓
Marca token como utilizado
```

### Fluxo 6: Cobrança Asaas → Atualização de Status Wakander

```
[Asaas Webhook] → /financeiro/cobranca/processa-evento (POST)
  ↓
CobrancaAsaasApplicationService.processaEvento()
  ↓ (usa strategy para tipo de cobrança)
CobrancaProcessadorAsaas.processa() → `Cobranca` entity
  ↓
CobrancaRepository.salvaCobranca()
  ↓
[Opcional] Se pagamento falho: notifica Wakander
[Opcional] Se pagamento atrasado: WakanderApplicationService.regularizaWakander()
```

### Fluxo 7: Envio de Formulário Dados Complementares (Batch)

```
[Scheduler] → WakanderApplicationService.solicitaEnvioDeFormularioDadosComplementares()
  ↓
Busca Wakanders com dados pessoais incompletos
  ↓
Para cada: gera token com tempo de expiração (dados-complementares)
  ↓
Envia URL via WhatsApp com delay (SQS) (SNS topic: zapi-requests)
  ↓
[Formulário] → FormularioApi /formulario/dados-complementares/{token}
  ↓
WakanderApplicationService.atualizaDadosWakander()
  ↓
Wakander completa status, publica DiscordEventRequest (SNS topic: discord-request)
```

---

## Mapeamento de Integrações Externas

| Sistema Externo | Tipo | Endpoint/Topic | Fluxo |
|-----------------|------|----------------|-------|
| **Memberkit** | Webhook | `POST /memberkit` | Aula assistida, conclusão de conteúdo → missão progresso |
| **Memberkit** | Importação | `POST /memberkit/import` | Admin carrega cursos como catálogo de missões |
| **Asaas** | Webhook | `POST /financeiro/assinaturas` | Novo pedido, atualização, cancelamento assinatura |
| **Asaas** | Webhook | `POST /financeiro/cobranca/processa-evento` | Pagamento, falha, atraso cobrança |
| **Asaas** | API Call | `AsaasClient` | Consulta cliente, assinatura, atualiza dados |
| **Z-API (WhatsApp)** | SNS Publisher | topic: `zapi-requests` | Envia mensagens, adiciona/remove grupos |
| **Z-API** | SQS Consumer | queue: `comunicacao-consumer` | Recebe webhooks de status, erros |
| **Discord** | API Call | `DiscordClient` | Cria convite, atualiza cargos |
| **Discord** | SNS Publisher | topic: `discord-request` | Requisições de ação no Discord |
| **Clint CRM** | API Call | `ClintCRM` | Envia contatos de Wakanders |
| **Clint** | SNS Publisher | topic: `clint-requests` | Requisições Clint |
| **N8N** | Não mapeado | — | (Presumido por contexto, não encontrado no código) |

---

## Arquitetura de Eventos (SNS/SQS)

### SNS Topics (Publicadores)

| Topic | Publisher | Payload Type | Consumidores |
|-------|-----------|-------------|--------------|
| `zapi-requests` | `PublicadorNotificacaoSns` | `ZApiEventDto` | SQS + Z-API externa |
| `memberkit-requests` | `PublicadorNotificacaoSns` | `MemberKitMessageEnvelope` | SQS + Memberkit externa |
| `asaas-requests` | `PublicadorNotificacaoSns` | `Wakander` | SQS (sincronização dados) |
| `clint-requests` | `PublicadorNotificacaoSns` | `ClintContatoRequest` | SQS + Clint externa |
| `discord-request` | `PublicadorNotificacaoSns` | `DiscordEventRequest` | SQS + Discord externa |
| `progresso-wakander-requests` | `PublicadorNotificacaoSns` | `ProgressoWakanderEventDto` | SQS (ProgressoWakanderConsumer) |
| `xp-wakander-requests` | `PublicadorNotificacaoSns` | `XpWakanderEventDTO` | SQS (XpWakanderConsumer) |
| `teste` | `PublicadorNotificacaoSns` | Various | Para testes |

### SQS Queues (Consumidores)

| Queue (config) | Consumer Component | Processa | Ação |
|----------------|-------------------|----------|------|
| `progresso-wakander-requests` | `ProgressoWakanderConsumer` | `ProgressoWakanderEventDto` | Identifica tipo progresso, delega ao processor |
| `xp-wakander-requests` | `XpWakanderConsumer` | `XpWakanderEventDTO` | Processa XP, publica XpPromocaoClasseEvent |
| `comunicacao-consumer` | `ComunicacaoConsumerSqs` | Various | Processa respostas WhatsApp/Discord |
| `jornada-wakander-requests` | `JornadaWakanderConsumerSqs` | Various | Processa eventos de jornada |

### Application Events (Local EventListener)

| Event Class | Publisher | Listeners |
|-------------|-----------|-----------|
| `CadastroCompletoEvent` | `WakanderApplicationService` | `ProgressoOnboardConsumer` (6 métodos) |
| `AssinaturaCanceladaEvent` | `WakanderApplicationService` | `AssinaturaCanceladaConsumer` (não mapeado) |
| `XpPromocaoClasseEvent` | `XpWakanderApplicationService` | `XpWakanderConsumer.promoveClasseXpWakander()` |

---

## Padrões de Design Observados

1. **Strategy Pattern:** `MemberkitProcessor`, `AulaAssistidaProcessador`, `ProgressoWakanderProcessor`, `CobrancaProcessadorAsaas`, `AssinaturaProcessorAsaas`
2. **Repository Pattern:** Implementações duplas (InfraRepository + SpringDataJpaRepository)
3. **Event-Driven:** SNS/SQS para cross-domain communication, `ApplicationEventPublisher` para eventos locais
4. **DTO Layering:** Request/Response DTOs, Event DTOs, API DTOs
5. **Embedded Value Objects:** `WakanderContato`, `WakanderFinanceiro`, `WakanderFiador` embarcados em `Wakander`

---

## Observações e Divergências com Design Esperado

1. **Falta de Consumer para `AssinaturaCanceladaEvent`:** O evento é publicado mas não há listeners implementados. Esperava-se: remoção de grupos WhatsApp, notificação Discord, etc.
2. **Processamento Asaas/Memberkit direto no Controller:** Webhooks externos são processados diretamente no ApplicationService (sem camada de adaptador intermediária de webhook validation)
3. **Transactional limitado:** Apenas `XpWakanderApplicationService` tem `@Transactional` na classe. Outros services dependem implicitamente de transações por método
4. **Falta de versionamento de API:** Não há `/v1/`, `/v2/` nos endpoints
5. **Dashboard e Formulários acoplados:** Frontend usa Thymeleaf renderizado no backend (não separação clara de camadas)

