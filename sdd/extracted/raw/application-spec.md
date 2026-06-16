# Functional Specification - Wakanda-AI-Dev

**Data de Extração:** 2026-05-27  
**Escopo:** Especificação funcional baseada em análise de código  
**Público-alvo:** Wakanders (alunos), Game Masters (operação), integradores externos

---

## §1 — Atores

| Ator | Tipo | Descrição |
|------|------|-----------|
| **Wakander (Aluno)** | Pessoa | Aluno/aprendiz que se matricula, estuda, progride em jornadas e ganha XP. Tem um fiador responsável financeiramente. |
| **Fiador** | Pessoa | Responsável pela assinatura e pagamento do Wakander. Pode ser o próprio Wakander ou terceira pessoa. Dados armazenados em Asaas. |
| **Game Master / Mentor** | Pessoa | Operador do sistema via dashboard Thymeleaf. Marca manualmente conclusão de missões, sincroniza dados, gerencia catálogo. |
| **Sistema Wakanda-AI** | Sistema Automático | Eventos scheduler, consumers SQS, event listeners locais. Orquestra fluxos de onboarding, XP, promoção. |
| **Asaas (Integrador)** | Sistema Externo | Plataforma de pagamento. Gerencia assinaturas, cobranças, dados de clientes. Envia webhooks de status. |
| **Memberkit (Integrador)** | Sistema Externo | LMS (Learning Management System). Hospeda cursos/aulas. Envia webhooks quando aluno assiste aula. |
| **Z-API (WhatsApp)** | Sistema Externo | Gateway de WhatsApp. Envia/recebe mensagens, gerencia grupos, retorna status. |
| **Discord** | Sistema Externo | Comunidade online. Servidor com canais, convites, cargos por classe. |
| **Clint CRM** | Sistema Externo | Sistema de CRM. Recebe contatos de Wakanders para gestão de relacionamento. |

---

## §2 — Casos de Uso

### UC-WAKANDER-001: Cadastro Inicial via Asaas

**ID:** UC-WAKANDER-001  
**Título:** Cadastro novo Wakander (Fiador + Assinatura)  
**Ator Principal:** Sistema Asaas  
**Trigger:** Webhook `POST /financeiro/assinaturas` (evento type = "subscription.created" ou similar)  
**Fluxo Principal:**
1. Asaas envia webhook com dados de nova assinatura (cliente, assinatura ID, plano)
2. Sistema cria novo `Wakander` com status `INCOMPLETO`, jornada `ONBOARD`
3. Sistema persiste dados do Fiador (nome, CPF, telefone do Asaas)
4. Sistema retorna HTTP 204 (sucesso)

**Regras de Negócio:**
- Wakander começa em jornada `ONBOARD` (não acessa conteúdo principal)
- Status cadastro = `INCOMPLETO` até formulário de cadastro ser preenchido
- Fiador é obrigatório (dados vindos do Asaas)

**Pré-condições:**
- Assinatura válida no Asaas
- Webhook configurado corretamente em Asaas

**Pós-condições:**
- Wakander criado no banco com UUID único
- Fiador vinculado

---

### UC-WAKANDER-002: Preenchimento de Cadastro (Formulário)

**ID:** UC-WAKANDER-002  
**Título:** Completar cadastro com dados pessoais  
**Ator Principal:** Wakander  
**Trigger:** Acesso ao link `/formulario/cadastro/{token}` → submit do formulário  
**Fluxo Principal:**
1. Wakander recebe link (token via WhatsApp gerado por sistema)
2. Abre formulário Thymeleaf `/formulario/cadastro/{token}`
3. Preenche: nome, CPF, data de nascimento, email
4. Submit → `POST /formulario/cadastro/{token}` com `WakanderCadastroCompleto`
5. Sistema valida dados (CPF único, etc.)
6. Marca token como utilizado
7. **Publica `CadastroCompletoEvent`** → dispara 6+ event listeners:
   - Atualiza `OnboardingWakander.cadastroConfirmado = true`
   - Envia checklist de onboarding via WhatsApp
   - Adiciona ao grupo WhatsApp de turma
   - Cria link de convite Discord, envia via WhatsApp
   - Cadastra no Memberkit (publica SNS)
   - Notifica grupo de líderes no WhatsApp
   - **Cria novo `ProgressoWakander`** (inicia rastreamento de XP/classes)

**Regras de Negócio:**
- CPF deve ser único na plataforma
- Dados pessoais são imutáveis após cadastro (apenas fiador pode atualizar dados complementares)
- Jornada muda de `ONBOARD` para `JORNADA_CONHECIMENTO` após conclusão
- Token expira em 24h

**Pré-condições:**
- Wakander criado via UC-WAKANDER-001
- Token válido e não expirado

**Pós-condições:**
- Status cadastro = `COMPLETO`
- OnboardingWakander com etapas atualizadas
- ProgressoWakander criado
- Mensagens WhatsApp enviadas

---

### UC-WAKANDER-003: Atualizar Dados do Fiador

**ID:** UC-WAKANDER-003  
**Título:** Atualizar dados pessoais do fiador (via formulário com token)  
**Ator Principal:** Wakander / Fiador  
**Trigger:** Link `/formulario/atualiza-fiador/{token}` → submit formulário  
**Fluxo Principal:**
1. Wakander/Fiador abre formulário de atualização
2. Preenche: nome, CPF, telefone, etc.
3. Submit → valida dados
4. Chama `AsaasClient.atualizaDadosFiador(idAsaas)` → atualiza em Asaas
5. Se sucesso, atualiza `Wakander.fiador` localmente
6. Marca token como utilizado

**Regras de Negócio:**
- Dados enviados devem ser válidos (CPF, telefone formato)
- Atualização é síncrona com Asaas
- Pode ser feito múltiplas vezes (diferente de UC-WAKANDER-002)

**Pré-condições:**
- Token gerado por `geraLinkAtualizacaoFiador()`
- Wakander já cadastrado

**Pós-condições:**
- `Wakander.fiador` atualizado
- Asaas cliente atualizado

---

### UC-MEMBERKIT-001: Webhook Aula Assistida

**ID:** UC-MEMBERKIT-001  
**Título:** Processar evento "aula assistida" do Memberkit  
**Ator Principal:** Memberkit (Integrador)  
**Trigger:** Webhook `POST /memberkit` (type = "lesson.completed" ou similar)  
**Fluxo Principal:**
1. Memberkit envia webhook com dados: user ID (Memberkit), aula ID, data conclusão, duração
2. Sistema busca Wakander por `idMemberKit`
3. Registra `AulaAssistida` (data, ID aula, duração)
4. Atualiza `Wakander.ultimaAulaAssistida`
5. Valida: aula corresponde a alguma `MissaoWakanda` ativa?
6. Se sim, publica `ProgressoWakanderEventDto` → SQS (topic: `progresso-wakander-requests`)
7. Consumer processa e chama `MissaoProgressoApplicationService.concluiMissao()` → cascata XP

**Regras de Negócio:**
- Uma aula só dispara progresso de missão se estiver mapeada no catálogo
- Não há limite de tentativas de aula (reasistir não gera XP duplo se já concluída)
- Duração mínima de aula para contar: não mapeado (assumir: qualquer duração)

**Pré-condições:**
- Aula deve estar cadastrada no catálogo Memberkit de Wakanda
- Wakander deve estar ativo (não cancelado)

**Pós-condições:**
- `AulaAssistida` registrada
- `ProgressoWakanderEventDto` publicada em SQS
- Missão progredida (se aplicável)

---

### UC-GAMEFICACAO-001: Concluir Missão e Ganhar XP

**ID:** UC-GAMEFICACAO-001  
**Título:** Conclusão de missão → XP → potencial promoção de classe  
**Ator Principal:** Sistema (via webhook Memberkit ou Game Master)  
**Trigger:** UC-MEMBERKIT-001 (aula) OU Game Master marca manual via API  
**Fluxo Principal:**
1. `MissaoProgressoApplicationService.concluiMissao()` chamado
2. Busca `MissaoWakanda` (XP base, Sabedorias vinculadas)
3. Marca `MissaoProgresso` como `CONCLUÍDA`
4. **Publica `XpWakanderEventDTO`** → SQS (topic: `xp-wakander-requests`)
5. `XpWakanderConsumer` processa:
   - Chama `XpWakanderApplicationService.processaXP()`
   - Atualiza `XpWakander.xpTotal += xpBase`
   - Recalcula nível baseado em XP
6. **Publica `XpPromocaoClasseEvent`**
7. `XpWakanderConsumer.@EventListener` processa:
   - Chama `processaPromocaoClasse()`
   - Valida: XP >= limite classe atual AND (missões obrigatórias concluídas OU não há obrigatórias)?
   - Se sim: cria `HistoricoClasseWakander`, promove para próxima classe
   - Reavalia missões disponíveis (libera novas)
8. Processa próxima missão da jornada (sequencial)

**Regras de Negócio:**
- XP é acumulativo, nunca decresce
- Cada classe tem XP mínimo requerido
- Algumas classes têm missões obrigatórias (pré-requisitos)
- Uma jornada tem múltiplas missões em sequência
- Missões são liberadas conforme classe do Wakander
- Última classe é a final (não há próxima)

**Pré-condições:**
- Wakander tem `ProgressoWakander` criado
- `MissaoWakanda` existe com XP e Sabedorias

**Pós-condições:**
- `MissaoProgresso.status = CONCLUÍDA`
- `XpWakander.xpTotal` aumentado
- Possível novo `HistoricoClasseWakander` criado
- Novas missões desbloqueadas

---

### UC-GAMEFICACAO-002: Listar Missões Disponíveis

**ID:** UC-GAMEFICACAO-002  
**Título:** Consultar missões disponíveis para Wakander em uma jornada  
**Ator Principal:** Wakander / Frontend  
**Trigger:** `GET /gameficacao/missao-progresso/wakander/{idWakander}/jornada/{idJornada}`  
**Fluxo Principal:**
1. System busca classe atual do Wakander
2. Busca todas as missões da jornada (ordenadas por ordem)
3. Para cada missão, calcula disponibilidade:
   - Classe mínima requerida <= classe atual? → DISPONÍVEL
   - Classe mínima > classe atual? → BLOQUEADA
   - Já concluída? → CONCLUÍDA
4. Retorna `List<MissaoDisponibilidadeResponse>` com status de cada uma

**Regras de Negócio:**
- Ordem de missão é respeitada (sequencial)
- Não é possível pular missão
- Bloqueio é por classe, não por pré-requisito (apenas validação, não bloqueio soft)

**Pré-condições:**
- Jornada existe
- Wakander existe e tem progresso

**Pós-condições:**
- Lista retornada com status cada missão

---

### UC-JORNADA-001: Onboarding Automático (Cadastro → Integração com Discord/WhatsApp/Memberkit)

**ID:** UC-JORNADA-001  
**Título:** Orquestração automática de onboarding pós-cadastro  
**Ator Principal:** Sistema (event listeners)  
**Trigger:** Conclusão de UC-WAKANDER-002 (publica `CadastroCompletoEvent`)  
**Fluxo Principal:**
1. `ProgressoOnboardConsumer` responde com 6+ métodos @EventListener:
   - **atualizaStatusCadastroOnboarding():**
     - Atualiza `OnboardingWakander.cadastroConfirmado = true`
     - Envia checklist via WhatsApp
   - **adicionaWakanderAoGrupo():**
     - Envia requisição Z-API para adicionar ao grupo WhatsApp
     - Atualiza `OnboardingWakander.entrouGrupoWhatsapp = true`
   - **convidaWakanderParaDiscord():**
     - Cria link de convite Discord
     - Envia via WhatsApp
     - Atualiza? (checklistProgressoOnboarding)
   - **cadastraWakanderNoMemberkit():**
     - Publica SNS (topic: `memberkit-requests`) com dados Wakander
     - Memberkit cria usuário correspondente
   - **enviaMensagemAoGrupoDeLideres():**
     - Notifica grupo WhatsApp de líderes sobre novo Wakander
   - **criaNovoProgressoWakander():**
     - Chama `ProgressoWakanderApplicationService.novoProgresso(idWakander)`
     - Cria `ProgressoWakander` e jornadas iniciais

2. Todo processamento é síncrono (no mesmo request/evento)

**Regras de Negócio:**
- Etapas são executadas em ordem
- Se uma falhar, as subsequentes podem não executar (transação local)
- Checklist tem 6 etapas pré-definidas

**Pré-condições:**
- `CadastroCompletoEvent` publicado
- Discord convite disponível
- Grupo WhatsApp existe

**Pós-condições:**
- `OnboardingWakander` com múltiplas etapas preenchidas
- `ProgressoWakander` criado
- Wakander adicionado a múltiplos sistemas externos

---

### UC-JORNADA-002: Associação Manual de Discord

**ID:** UC-JORNADA-002  
**Título:** Wakander associa sua conta Discord à plataforma  
**Ator Principal:** Wakander / Discord  
**Trigger:** Link Discord com callback → `POST /wakander/jornada/associar-discord` (DiscordRequest com email + idDiscord)  
**Fluxo Principal:**
1. Wakander recebe link Discord de convite via WhatsApp
2. Aceita convite, é adicionado ao servidor
3. Bot Discord redireciona para callback (form + email + idDiscord)
4. Submit → POST /wakander/jornada/associar-discord
5. Sistema:
   - Busca Wakander por email
   - Atualiza `Wakander.idDiscord` e `Wakander.userDiscord`
   - Atualiza `OnboardingWakander.entrouDiscord = true`
   - Chama `DiscordService.atualizaCargoParaWakander()` → atribui cargo baseado em classe
6. Retorna página de sucesso

**Regras de Negócio:**
- Uma conta Discord só pode ser associada a um Wakander
- Cargo no Discord é baseado em classe atual
- Associação é imutável

**Pré-condições:**
- Wakander cadastrado
- Discord bot tem permissão para atribuir cargos
- Email no form deve corresponder a Wakander existente

**Pós-condições:**
- `Wakander.idDiscord` preenchido
- `OnboardingWakander.entrouDiscord = true`
- Cargo Discord atribuído

---

### UC-FINANCEIRO-001: Webhook Assinatura (Criação/Atualização/Cancelamento)

**ID:** UC-FINANCEIRO-001  
**Título:** Processar webhook de assinatura do Asaas  
**Ator Principal:** Asaas  
**Trigger:** Webhook `POST /financeiro/assinaturas` (type = "subscription.created", "subscription.updated", "subscription.deleted")  
**Fluxo Principal:**
1. Asaas envia webhook com tipo de evento
2. Sistema deserializa `AssinaturaAsaasDto`
3. Strategy pattern: `AssinaturaProcessorAsaas.processaEvento()`:
   - **Criação:** Cria novo Wakander (UC-WAKANDER-001)
   - **Atualização:** Atualiza dados de assinatura em Wakander
   - **Cancelamento:** Marca Wakander como status CANCELADO (não muda em tempo real, é futura)
4. Retorna HTTP 204

**Regras de Negócio:**
- Tipo deve ser reconhecido (3+ processadores)
- Se tipo desconhecido, retorna HTTP 400
- Assinatura é única por cliente Asaas

**Pré-condições:**
- Webhook autenticado (não mapeado no código)

**Pós-condições:**
- Wakander ou Assinatura atualizada
- Status de Wakander pode mudar

---

### UC-FINANCEIRO-002: Webhook Cobrança (Pagamento/Falha/Atraso)

**ID:** UC-FINANCEIRO-002  
**Título:** Processar webhook de cobrança do Asaas  
**Ator Principal:** Asaas  
**Trigger:** Webhook `POST /financeiro/cobranca/processa-evento` (type = "payment.confirmed", "payment.failed", etc.)  
**Fluxo Principal:**
1. Asaas envia webhook com dados de cobrança (ID fatura, status, data)
2. Sistema deserializa `CobrancaAsaasDto`
3. Strategy pattern: `CobrancaProcessadorAsaas.processa()`:
   - **Pagamento confirmado:** Registra `Cobranca` com status PAGO
   - **Pagamento falho:** Registra com status FALHO, notifica Wakander
   - **Pagamento em atraso:** Registra com status ATRASADO
4. Persiste em `CobrancaRepository`
5. Retorna HTTP 204

**Regras de Negócio:**
- Uma cobrança pode ter múltiplas tentativas
- Falha não cancela assinatura automaticamente (esperar múltiplas falhas ou webhook cancel)

**Pré-condições:**
- Cobrança corresponde a assinatura existente

**Pós-condições:**
- `Cobranca` registrada com status apropriado
- Wakander potencialmente notificado

---

### UC-COMUNICACAO-001: Enviar Mensagem WhatsApp

**ID:** UC-COMUNICACAO-001  
**Título:** Enviar mensagem via WhatsApp (Z-API)  
**Ator Principal:** Sistema (internamente via SNS)  
**Trigger:** `PublicadorNotificacaoSns.enviaNotificacaoSns(..., topic: zapi-requests)`  
**Fluxo Principal:**
1. Sistema publica mensagem em SNS (topic: `zapi-requests`) com `ZApiEventDto`
2. SQS consumer recebe: `ZApiEventDto` com telefone, tipo (NORMAL_MESSAGE, ADD_TO_GROUP, REMOVE_TO_GROUP)
3. `ComunicacaoApplicationService.enviaMensagemWhatsapp()`:
   - Chama `ZApiClient.enviaMensagemWhatsApp(payload)`
   - Z-API envia para WhatsApp
4. Retorna sucesso/falha

**Regras de Negócio:**
- Mensagem pode ter múltiplas tentativas
- Número deve estar no formato válido (com código país)
- Falha de envio deve ser registrada (não implementado no código)

**Pré-condições:**
- Número WhatsApp válido

**Pós-condições:**
- Mensagem enviada (ou fila para retry)

---

### UC-COMUNICACAO-002: Adicionar Wakander a Grupo WhatsApp

**ID:** UC-COMUNICACAO-002  
**Título:** Adicionar Wakander a grupo WhatsApp (turma)  
**Ator Principal:** Sistema  
**Trigger:** `PublicadorNotificacaoSns.enviaNotificacaoSns(..., DiscordEventRequest, topic: zapi-requests)` com type `ADD_TO_GROUP`  
**Fluxo Principal:**
1. Sistema publica requisição com type `ADD_TO_GROUP`
2. Consumer chamaa `ComunicacaoApplicationService.adicionaWakanderAoGrupo()`
3. Chama `ZApiClient.processaRequisicaoGrupoWhatsapp(payload, ADD_TO_GROUP)`
4. Z-API adiciona contato ao grupo
5. Atualiza `OnboardingWakander.entrouGrupoWhatsapp = true` (talvez via listener)

**Regras de Negócio:**
- Grupo deve existir previamente
- Número deve estar registrado em Z-API

**Pré-condições:**
- Grupo criado em WhatsApp
- Z-API conhece grupo ID

**Pós-condições:**
- Wakander adicionado ao grupo
- Recebe mensagens do grupo

---

### UC-COMUNICACAO-003: Convidar para Discord

**ID:** UC-COMUNICACAO-003  
**Título:** Criar link de convite Discord e enviar via WhatsApp  
**Ator Principal:** Sistema  
**Trigger:** `ProgressoOnboardConsumer.convidaWakanderParaDiscord(CadastroCompletoEvent)`  
**Fluxo Principal:**
1. System chama `ComunicacaoService.convidaParaCanalDiscord(DiscordConviteRequest)`
2. `DiscordClient.criaConviteDoCanalParaWakander()`
   - Usa Discord API para criar invite
   - Retorna `DiscordConviteResponse` com código
3. Monta URL: `https://discord.gg/{codigo}`
4. Envia via WhatsApp: mensagem com link

**Regras de Negócio:**
- Invite expira em 24h (não configurado no código)
- Canal é pré-definido (não parameterizado)

**Pré-condições:**
- Discord bot tem permissão para criar invites
- Canal existe no servidor

**Pós-condições:**
- Invite criado
- URL enviada via WhatsApp

---

### UC-AUTENTICACAO-001: Login ADM via Dashboard

**ID:** UC-AUTENTICACAO-001  
**Título:** Autenticação de usuário ADM (Game Master)  
**Ator Principal:** Game Master  
**Trigger:** `POST /autenticacao/login` (UsuarioAdmLoginDto)  
**Fluxo Principal:**
1. ADM acessa dashboard Thymeleaf
2. Submete credenciais (email/username + senha)
3. `AutenticacaoService.authenticate()`:
   - Valida usuário no banco
   - Valida senha (encrypted)
   - Gera JWT token
4. Retorna `AuthenticationResponseDto` com token
5. Token é usado em requests subsequentes (header Authorization)

**Regras de Negócio:**
- Senha é obrigatória e criptografada
- Token tem tempo de expiração
- Role-based access (se implementado)

**Pré-condições:**
- Usuário ADM cadastrado via UC-AUTENTICACAO-002

**Pós-condições:**
- JWT token gerado
- ADM autenticado para dashboard

---

### UC-AUTENTICACAO-002: Cadastro de Usuário ADM

**ID:** UC-AUTENTICACAO-002  
**Título:** Cadastro novo usuário ADM (Game Master)  
**Ator Principal:** Admin raiz / Sistema  
**Trigger:** `POST /autenticacao/cadastro` (UsuarioAdmCadastroRequest)  
**Fluxo Principal:**
1. Admin raiz cria novo usuário via form backend
2. Submete: email, nome, senha
3. `UsuarioAdmApplicationService.cadastrar()`:
   - Valida email único
   - Criptografa senha
   - Persiste UsuarioAdm
4. Retorna sucesso (201)

**Regras de Negócio:**
- Email deve ser único
- Senha mínimo de caracteres (não especificado no código)

**Pré-condições:**
- Admin raiz autenticado

**Pós-condições:**
- Usuário ADM criado
- Pronto para login (UC-AUTENTICACAO-001)

---

### UC-CANCELAMENTO-001: Solicitação de Cancelamento

**ID:** UC-CANCELAMENTO-001  
**Título:** Wakander solicita cancelamento de assinatura  
**Ator Principal:** Wakander  
**Trigger:** API ou formulário → `WakanderApplicationService.solicitaCancelamentoWakander()`  
**Fluxo Principal:**
1. Wakander inicia processo de cancelamento
2. Sistema:
   - Muda status para `CANCELAMENTO_SOLICITADO`
   - Gera link para formulário de confirmação (token com expiração)
   - Envia via WhatsApp: "Confirme cancelamento aqui: [link]"
3. Wakander pode:
   - Clicar no link → confirmar cancelamento (UC-CANCELAMENTO-002)
   - Ignorar → cancelamento não ocorre (requer ação Asaas depois)

**Regras de Negócio:**
- Status intermediário: `CANCELAMENTO_SOLICITADO` (não é ainda cancelado)
- Link expira em 24h
- Wakander pode revert (se implementado)

**Pré-condições:**
- Wakander status `REGULAR` ou similar

**Pós-condições:**
- Status muda para `CANCELAMENTO_SOLICITADO`
- Link enviado via WhatsApp

---

### UC-CANCELAMENTO-002: Confirmação de Cancelamento

**ID:** UC-CANCELAMENTO-002  
**Título:** Wakander confirma cancelamento de assinatura  
**Ator Principal:** Wakander  
**Trigger:** Acesso a link `/formulario/cancelamento-assinatura/{idWakander}` → submit formulário  
**Fluxo Principal:**
1. Wakander abre formulário de cancelamento (Thymeleaf)
2. Vê motivo, oferece campo para feedback
3. Confirma: "Sim, desejo cancelar"
4. Submit → `WakanderApplicationService.cancelaAssinatura()`
5. Sistema:
   - Muda status para `CANCELADO`
   - **Publica `AssinaturaCanceladaEvent`** (não há listeners implementados)
   - Esperado: Assinatura também é cancelada em Asaas via webhook (fora de escopo aqui)

**Regras de Negócio:**
- Cancelamento é irreversível via UI (requer Asaas para reverter)
- Feedback é opcional

**Pré-condições:**
- Status `CANCELAMENTO_SOLICITADO`

**Pós-condições:**
- Status muda para `CANCELADO`
- Acesso à plataforma pode ser bloqueado (não mapeado)

---

### UC-DASHBOARD-001: Gerenciar Wakanders (Game Master)

**ID:** UC-DASHBOARD-001  
**Título:** ADM visualiza e gerencia Wakanders no dashboard  
**Ator Principal:** Game Master  
**Trigger:** Acesso a dashboard `/formulario/...` (Thymeleaf)  
**Fluxo Principal:**
1. ADM autentica (UC-AUTENTICACAO-001)
2. Acessa dashboard Thymeleaf
3. Visualiza lista de Wakanders:
   - Paginada
   - Filtrável por nome/CPF/telefone
   - Ordená vel por nome
   - Pode incluir/excluir cancelados
4. Ações possíveis:
   - Ver detalhes (nome, classe, XP, jornada)
   - Iniciar onboarding manual (gera token)
   - Marcar missão como concluída
   - Ver progresso (XP, classes, histórico)

**Regras de Negócio:**
- Paginação obrigatória (max 50 por página)
- Busca por CPF/telefone é case-insensitive

**Pré-condições:**
- ADM autenticado

**Pós-condições:**
- Dashboard renderizado com dados atualizados

---

### UC-DASHBOARD-002: Marcar Missão Concluída Manualmente

**ID:** UC-DASHBOARD-002  
**Título:** ADM marca missão como concluída (sem aula Memberkit)  
**Ator Principal:** Game Master  
**Trigger:** Dashboard → click "Marcar Concluída" em missão  
**Fluxo Principal:**
1. ADM visualiza missão em progresso
2. Clica em "Marcar Concluída"
3. Submete: `PATCH /gameficacao/missao-progresso/{idMissaoProgresso}/conclui`
4. Sistema:
   - Marca `MissaoProgresso` como `CONCLUÍDA`
   - Publica XP (mesmo flow do UC-GAMEFICACAO-001)
   - Processa próxima missão
5. Retorna sucesso (204)

**Regras de Negócio:**
- ADM pode marcar qualquer missão
- Não há validação de quem pode marcar (assumir autorização via JWT role)

**Pré-condições:**
- ADM autenticado
- Missão existe e está em progresso

**Pós-condições:**
- Missão marcada concluída
- XP publicado

---

### UC-IMPORTACAO-001: Importar Catálogo Memberkit

**ID:** UC-IMPORTACAO-001  
**Título:** Importar cursos do Memberkit como missões  
**Ator Principal:** Game Master  
**Trigger:** Dashboard → `POST /memberkit/import?idTipoMissao={uuid}&idJornada={uuid}`  
**Fluxo Principal:**
1. ADM navega dashboard
2. Seleciona: tipo de missão (categoria) + jornada
3. Clica "Importar Memberkit"
4. Sistema:
   - Chama `MemberkitIntegrationImporter.buscarCursosDoMemberkit()`
   - Para cada curso: cria `MissaoWakanda` com:
     - Título = nome curso
     - XP base = 100 (padrão, não parameterizado)
     - Sabedorias = ? (não mapeado)
     - OrdemMissao = sequencial
   - Valida duplicatas (mesmos títulos em múltiplos cursos)
   - Exibe relatório: "Importadas 42 missões, 3 duplicatas encontradas"
5. Retorna sucesso (204)

**Regras de Negócio:**
- Cursos sem título são ignorados
- Duplicatas são reportadas, não bloqueiam import
- XP base é fixo (não configurável via UI)

**Pré-condições:**
- Tipo missão existe
- Jornada existe
- Memberkit API está acessível

**Pós-condições:**
- Múltiplas `MissaoWakanda` criadas
- Relatório de import exibido

---

## §3 — Domínios e Atribuições

### Domínio: Wakander

**O que faz:** Gerencia a entidade central (aluno), cadastro inicial, dados pessoais, status financeiro, jornada atual.

**Funcionalidades:**
- Criar Wakander (via Asaas webhook)
- Completar cadastro pessoal
- Atualizar dados fiador
- Solicitar/confirmar cancelamento
- Buscar dados (individual ou lista com filtros)
- Atualizar status financeiro (regularizar, cancelar)
- Gerar formulários com tokens

**Casos de Uso:** UC-WAKANDER-001, UC-WAKANDER-002, UC-WAKANDER-003, UC-CANCELAMENTO-001, UC-CANCELAMENTO-002

---

### Domínio: Gamificação - Catálogo

**O que faz:** Define estrutura de aprendizado (jornadas, trilhas, missões, classes).

**Funcionalidades:**
- Criar/editar jornadas
- Criar/editar trilhas
- Criar/editar missões com XP e Sabedorias
- Definir classes (níveis)
- Importar cursos do Memberkit como missões
- Validar disponibilidade de missão por classe

**Casos de Uso:** UC-IMPORTACAO-001, UC-GAMEFICACAO-002 (leitura)

---

### Domínio: Gamificação - Progresso

**O que faz:** Rastreia evolução do Wakander (XP, classes, missões concluídas).

**Funcionalidades:**
- Criar novo `ProgressoWakander` quando Wakander se cadastra
- Concluir missão e disparar XP
- Processar XP e atualizar nível
- Promover classe quando critérios atingidos
- Listar missões disponíveis
- Gerar ranking de Wakanders
- Sincronizar dados de Wakanders legacy

**Casos de Uso:** UC-GAMEFICACAO-001, UC-GAMEFICACAO-002

---

### Domínio: Jornada Wakander

**O que faz:** Orquestra onboarding e integração com sistemas externos.

**Funcionalidades:**
- Registrar aulas assistidas (Memberkit)
- Processar webhook Memberkit (aula concluída)
- Gerenciar etapas de onboarding
- Associar Discord
- Publicar eventos cascata (pós-cadastro)

**Casos de Uso:** UC-MEMBERKIT-001, UC-JORNADA-001, UC-JORNADA-002

---

### Domínio: Autenticação

**O que faz:** Gerencia identidade e autorização.

**Funcionalidades:**
- Gerar tokens para Wakanders (cadastro, dados complementares)
- Autenticar ADMs (JWT)
- Validar tokens
- Reativar tokens expirados

**Casos de Uso:** UC-AUTENTICACAO-001, UC-AUTENTICACAO-002, UC-WAKANDER-002 (implícito)

---

### Domínio: Financeiro

**O que faz:** Gerencia assinaturas e cobranças (integração Asaas).

**Funcionalidades:**
- Processar webhooks de assinatura (create, update, cancel)
- Processar webhooks de cobrança (pagamento, falha, atraso)
- Atualizar dados de fiador em Asaas
- Sincronizar dados de cliente/assinatura do Asaas

**Casos de Uso:** UC-FINANCEIRO-001, UC-FINANCEIRO-002, UC-WAKANDER-003

---

### Domínio: Comunicação

**O que faz:** Notifica e integra com sistemas de comunicação.

**Funcionalidades:**
- Enviar mensagens WhatsApp (Z-API)
- Adicionar/remover grupo WhatsApp
- Convidar para Discord
- Enviar contatos para Clint CRM
- Processar webhooks de resposta WhatsApp/Discord

**Casos de Uso:** UC-COMUNICACAO-001, UC-COMUNICACAO-002, UC-COMUNICACAO-003, UC-JORNADA-001 (implícito)

---

## §4 — Pontos de Integração Externa (Visão Funcional)

| Sistema | Tipo | Trigger Wakanda | Payload Esperado | Comportamento Sistema | Notificação Wakander |
|---------|------|-----------------|------------------|----------------------|----------------------|
| **Asaas** | Webhook | Novo pedido, atualização, cancelamento | `AssinaturaAsaasDto` {tipo, cliente, assinatura ID, plano} | Cria Wakander ou atualiza status | Não (automático) |
| **Asaas** | Webhook | Pagamento, falha, atraso | `CobrancaAsaasDto` {fatura ID, status, valor, data} | Registra cobrança, pode notificar | Sim (se falha/atraso) |
| **Asaas** | API Query | Atualizar fiador | `FiadorDTO` {nome, CPF, telefone} | Sincroniza dados Asaas | Não |
| **Memberkit** | Webhook | Aula concluída | `AulaMemberKitDTO` {user ID, aula ID, duração, data} | Registra aula, dispara missão progresso | Sim (checklist via WhatsApp) |
| **Memberkit** | API Query | Importar catálogo | (GET cursos) | Cria múltiplas missões no catálogo | Não (ADM vê relatório) |
| **Z-API** | API Call | Enviar notificação | `ZApiEventDto` {telefone, mensagem, tipo} | Envia via WhatsApp | Sim (recebe mensagem) |
| **Z-API** | Webhook | Entrega, leitura, erro | (status message) | Registra status entrega | Não |
| **Discord** | API Call | Convidar / Atribuir cargo | `DiscordConviteRequest` ou cargo | Cria invite, atualiza permissões | Sim (recebe mensagem com link) |
| **Discord** | Webhook | Callback de associação | `DiscordRequest` {idDiscord, email} | Asocia Discord ID ao Wakander | Não |
| **Clint CRM** | API Call | Enviar contato | `ClintContatoRequest` {nome, email, telefone, data nasc} | Registra contato no CRM | Não |

---

## §5 — Casos de Uso por Domínio

| UC | Domínio(s) | Tipo |
|----|-----------|------|
| UC-WAKANDER-001 | Wakander, Financeiro | Criação (async webhook) |
| UC-WAKANDER-002 | Wakander, Jornada, Autenticação | Cadastro (sincrônico) |
| UC-WAKANDER-003 | Wakander, Financeiro | Atualização (sincrônico) |
| UC-MEMBERKIT-001 | Jornada, Gamificação | Webhook (async SQS) |
| UC-GAMEFICACAO-001 | Gamificação | Processamento (async SQS) |
| UC-GAMEFICACAO-002 | Gamificação | Consulta (sincrônico) |
| UC-JORNADA-001 | Jornada, Wakander, Comunicação | Orquestração (event listeners) |
| UC-JORNADA-002 | Jornada, Comunicação | Associação (sincrônico) |
| UC-FINANCEIRO-001 | Financeiro, Wakander | Webhook (sincrônico) |
| UC-FINANCEIRO-002 | Financeiro | Webhook (sincrônico) |
| UC-COMUNICACAO-001 | Comunicação | Notificação (async SNS) |
| UC-COMUNICACAO-002 | Comunicação | Ação (async SNS) |
| UC-COMUNICACAO-003 | Comunicação | Ação (event listener) |
| UC-AUTENTICACAO-001 | Autenticação | Login (sincrônico) |
| UC-AUTENTICACAO-002 | Autenticação | Cadastro (sincrônico) |
| UC-CANCELAMENTO-001 | Wakander, Comunicação | Requisição (sincrônico) |
| UC-CANCELAMENTO-002 | Wakander | Confirmação (sincrônico) |
| UC-DASHBOARD-001 | Wakander, Autenticação | Consulta (sincrônico) |
| UC-DASHBOARD-002 | Gamificação, Autenticação | Ação (sincrônico) |
| UC-IMPORTACAO-001 | Gamificação, Autenticação | Ação (sincrônico) |

---

## Observações Funcionais

1. **Fluxo Crítico: Onboarding** — depende de múltiplos sistemas (Asaas, Memberkit, WhatsApp, Discord). Falha em qualquer etapa pode deixar Wakander sem acesso.

2. **Assincronia via SNS/SQS** — garante escalabilidade, mas dificulta debugging (eventual consistency).

3. **Falta de Handlers para Cancelamento** — `AssinaturaCanceladaEvent` é publicado mas não processado. Esperava-se: remover de grupos, notificar Discord, etc.

4. **Importação Memberkit é Manual** — depende de ADM executar. Pode haver cursos novos não importados.

5. **Validações de Disponibilidade Missão** — baseadas em classe, não em pré-requisitos. Modelo é linear, não arbóreo.

6. **Transações Locais** — event listeners não garantem ACID (cada listener é uma transação separada). Se listener 3 falha, listeners 1-2 já executaram.

