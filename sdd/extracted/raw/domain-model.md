# Domain Model - Wakanda-AI-Dev

## § 1 — Entidades por Bounded Context

### 1.1 Bounded Context: **WAKANDER** (Identidade e Cadastro)

#### [Wakander](src/main/java/academy/wakanda/wakanda_ai/wakander/domain/Wakander.java)
- **Domínio:** Identidade pessoal e financeira do participante
- **Tabela Mapeada:** `wakander`
- **Agregado Raiz:** SIM
- **Campos Principais:**
  - `idWakander` (UUID, PK)
  - `nome` (String)
  - `cpf` (String, UNIQUE) — Validado com @CPF
  - `dataNascimento` (LocalDate)
  - `idMemberKit` (String, UNIQUE)
  - `idDiscord` (String)
  - `userDiscord` (String)
  - `statusCadastro` (@Enumerated) → StatusCadastro {INCOMPLETO, COMPLETO}
  - `jornadaAtual` (@Enumerated) → JornadaWakanda {ONBOARD, JORNADA_CONHECIMENTO, ...}
  - `contato` (@Embedded WakanderContato)
  - `financeiro` (@Embedded WakanderFinanceiro)
  - `fiador` (@Embedded WakanderFiador)
  - `ultimaAulaAssistida` (@Embedded WakanderAulaAssistida)

- **Lombok:** @Getter, @NoArgsConstructor(access=PRIVATE), @AllArgsConstructor

- **Métodos Comportamentais (Rich Domain Model):**
  - `mudaStatusFinanceiro(status, dataHora)` — Transição de status financeiro
  - `atualizaUltimaAulaAssistida(idAula, dataConclusao)` — Registra conclusão de aula
  - `atualizaProgresso(jornadaWakanda)` — Avança para próxima jornada com validações
  - `validaWakanderRegular()` — Invariante: Wakander deve estar REGULAR para continuar
  - `validaJornadaAtual(jornadaWakanda)` — Invariante: Não pode estar já na mesma jornada
  - `completaCadastro(dados)` — Finaliza onboarding
  - `cancelaAssinatura(motivo, data)` — Inicia cancelamento
  - `reverteCancelamentoParaRegular()` — Desfaz cancelamento
  - `atualizaStatusCadastro()` — Recalcula status (COMPLETO se todos dados preenchidos)
  - `associarDiscord(idDiscord, userDiscord)` — Vincula conta Discord

- **Factory Methods:**
  - `new Wakander(WakanderNovoRequest)` — Construtor principal [DEPRECATED]
  - `new Wakander(AssinaturaEvento, ClienteAsaasDto)` — Construtor via Assinatura Asaas

- **Invariantes Protegidos:**
  - CPF validado e único
  - idMemberKit único (quando preenchido)
  - Wakander regular = financeiro.status == REGULAR
  - Não pode estar em duas jornadas simultaneamente
  - Dados pessoais completos: nome, cpf, dataNascimento, contato (email + whatsapp)

- **Classe:** RICH (com lógica de negócio)

---

#### [WakanderContato](src/main/java/academy/wakanda/wakanda_ai/wakander/domain/WakanderContato.java)
- **Tipo:** Value Object (@Embeddable)
- **Tabela:** Colunas em `wakander`
- **Campos:**
  - `whatsapp` (String)
  - `email` (String, @Email)

- **Métodos:**
  - `editaContato(request)` — Atualiza ambos
  - `ocultaEmail()` — Máscara segurança: primeiros 3 chars + asteriscos
  - `ocultarWhatsapp()` — Máscara: últimos 4 dígitos + asteriscos
  - `preencheContatoIncompleto(email, whatsapp)` — Preenche campos nulos

- **Lombok:** @Getter, @NoArgsConstructor, @ToString

- **Factories:** Construtores diretos com validação via `ContatoUtils`

---

#### [WakanderFinanceiro](src/main/java/academy/wakanda/wakanda_ai/wakander/domain/WakanderFinanceiro.java)
- **Tipo:** Value Object (@Embeddable)
- **Tabela:** Colunas em `wakander` (prefixo `financeiro_`)
- **Campos:**
  - `status` (@Enumerated) → WakanderStatusFinanceiro {REGULAR, CANCELAMENTO_SOLICITADO, CANCELADO}
  - `ultimaAtualizacao` (LocalDateTime)
  - `motivoCancelamento` (String)

- **Métodos:**
  - `mudaStatus(novoStatus, dataHora)` — Transição com validação de conflito
  - `mudaStatusParaCancelado(motivo, dataHora)` — Cancela se status == CANCELAMENTO_SOLICITADO
  - `reverteParaRegular(dataDesistencia)` — Desfaz cancelamento
  - `validaSepodeCancelar()` — Invariante: Só cancela se status == CANCELAMENTO_SOLICITADO
  - `validaSePodeReverter()` — Invariante: Só reverte se status == CANCELAMENTO_SOLICITADO

- **Lombok:** @Getter, @AllArgsConstructor

- **Default:** status = REGULAR ao criar novo Wakander

---

#### [WakanderFiador](src/main/java/academy/wakanda/wakanda_ai/wakander/domain/WakanderFiador.java)
- **Tipo:** Value Object (@Embeddable)
- **Campos:**
  - `idAsaas` (String) — ID do cliente no Asaas
  - `idAssinatura` (String, UNIQUE) — ID da assinatura Asaas
  - `nome` (String)
  - `cpf` (String, @CPF validado)
  - `telefone` (String)

- **Métodos:**
  - `atualizaDados(ClienteAsaasDto)` — Sincroniza com Asaas
  - `atualizaAssinatura(AssinaturaListaAsaasDto)` — Atualiza assinatura ACTIVE
  - `verificaStatusPreenchimento()` → StatusDadosFiador {COMPLETO, INCOMPLETO}
  - `atualizaFiador(FiadorDTO)` — Atualiza nome, cpf, whatsapp

- **Lombok:** @Getter, @NoArgsConstructor(PRIVATE), @AllArgsConstructor

---

#### [WakanderAulaAssistida](src/main/java/academy/wakanda/wakanda_ai/wakander/domain/WakanderAulaAssistida.java)
- **Tipo:** Value Object (@Embeddable)
- **Campos:**
  - `dateTime` (LocalDateTime)
  - `idAulaAssistida` (UUID)

- **Métodos:** Construtor com valores específicos ou com defaults (LocalDateTime.now())

- **Lombok:** @Getter, @Embeddable

---

### 1.2 Bounded Context: **AUTENTICAÇÃO**

#### [Autenticacao](src/main/java/academy/wakanda/wakanda_ai/autenticacao/domain/Autenticacao.java)
- **Domínio:** Tokens de autenticação com expiração
- **Tabela:** `autenticacao`
- **Campos:**
  - `token` (String, PK)
  - `idWakander` (UUID, FK)
  - `dataExpiracao` (LocalDateTime)
  - `statusToken` (@Enumerated) → StatusToken {VALIDO, EXPIRADO, UTILIZADO}

- **Métodos:**
  - `verificaDataDeExpiracao()` — Valida se expirou
  - `mudaStatusTokenParaExpirado()` — Marca como EXPIRADO
  - `mudaStatusTokenParaUtilizado()` — Marca como UTILIZADO
  - `reativaTokenExpirado()` — Renova por 2 dias, volta a VALIDO

- **Invariante:** Token expira automaticamente baseado em dataExpiracao

- **Classe:** RICH

---

#### [UsuarioAdm](src/main/java/academy/wakanda/wakanda_ai/autenticacao/domain/UsuarioAdm.java)
- **Domínio:** Usuários administrativos
- **Tabela:** `usuario_admin`
- **Campos:**
  - `id` (UUID, PK)
  - `nome` (String, 100 chars)
  - `username` (String, UNIQUE, 50 chars)
  - `senha` (String)
  - `tentativaLogin` (Integer) — Contador de tentativas falhas
  - `statusUsuario` (@Enumerated) → StatusUsuario {ATIVO, BLOQUEADO}
  - `perfil` (@Enumerated) → PerfilUsuario {NAO_VERIFICADO, DEV, LIDERANCA}
  - `criadoEm` (LocalDateTime)

- **Implementa:** UserDetails (Spring Security)

- **Métodos:**
  - `getAuthorities()` — Retorna ROLE_DEV ou ROLE_LIDERANCA
  - `verificaTipoLogin(TipoLogin)` — Incrementa tentativas falhas, bloqueia se >= 5
  - `novoUsuarioNaoVerificado(nome, username, senhaCodificada)` — Factory

- **Invariante:** Bloqueia após 5 tentativas falhas

- **Classe:** RICH (gerencia segurança)

---

### 1.3 Bounded Context: **FINANCEIRO (Cobrança)**

#### [Cobranca](src/main/java/academy/wakanda/wakanda_ai/financeiro/domain/cobranca/Cobranca.java)
- **Domínio:** Faturas de cobrança via Asaas
- **Tabela:** `cobranca`
- **Campos:**
  - `idCobranca` (UUID, PK)
  - `idWakander` (UUID, FK)
  - `idPaymentAsaas` (String, UNIQUE) — ID do pagamento no Asaas
  - `valor` (BigDecimal)
  - `valorLiquido` (BigDecimal)
  - `dataCriacao` (LocalDate)
  - `dataVencimento` (LocalDate)
  - `dataPagamento` (LocalDate, nullable)
  - `status` (@Enumerated) → CobrancaStatus {PENDENTE, PAGAMENTO_CONFIRMADO, NEGATIVADO, PAGAMENTO_VENCIDO}

- **Métodos:**
  - `alteraStatusParaConfirmado(CobrancaEvento)` — Marca como pago
  - `alteraStatusParaNegativado()` — Marca como negativado
  - `atualizaStatusParaVencido()` — Marca como vencido

- **Invariante:** Status é único em cada momento (não pode ser o mesmo)

- **Classe:** RICH

---

### 1.4 Bounded Context: **JORNADA WAKANDER** (Legado)

#### [JornadaWakander](src/main/java/academy/wakanda/wakanda_ai/jornadawakander/domain/JornadaWakander.java)
- **Domínio:** Histórico de jornadas completadas
- **Tabela:** `jornada_wakander` (SEM @Table, nome inferido)
- **Campos:**
  - `idJornadaWakander` (UUID, PK)
  - `idWakander` (UUID, FK)
  - `jornadaAtual` (@Enumerated) → JornadaWakanda
  - `jornadaConcluida` (@Enumerated) → JornadaWakanda
  - `momentoAlteracao` (LocalDateTime)

- **Métodos:** Apenas getters, construtor
- **Classe:** ANÊMICA (só dados, sem lógica)

---

## § 2 — Gamificação: Três Sub-Contextos (Catalogo, Progresso, XP)

### 2.1 SUB-CONTEXTO: **CATALOGO** — Definição de Conteúdo

#### [TrilhaWakanda](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/trilhawakanda/domain/TrilhaWakanda.java)
- **Domínio:** Caminhos de aprendizado (coleções de jornadas)
- **Tabela:** `trilha_wakanda`
- **Campos:**
  - `idTrilha` (UUID, PK)
  - `nome` (String, UNIQUE)
  - `descricao` (String)
  - `xpTotal` (Integer) — Soma de XP das jornadas

- **Métodos:**
  - `atualizaXp(xpMissao)` — Subtrai XP quando missão é removida
  - `recalculaXpTrilha(tipo, xpAntigoJornada, xpNovoJornada)` — Recalcula se XP alterado ou missão adicionada

- **Invariante:** xpTotal >= 0

- **Lombok:** @Getter, @NoArgsConstructor(PRIVATE)

- **Classe:** RICH

---

#### [JornadaWakanda](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/jornadawakanda/domain/JornadaWakanda.java)
- **Domínio:** Sequência de missões dentro de uma trilha
- **Tabela:** `jornada_wakanda`
- **Campos:**
  - `idJornada` (UUID, PK)
  - `titulo` (String)
  - `descricao` (String)
  - `xpTotal` (Integer) — Soma de XP das missões
  - `idTrilhaWakanda` (UUID, FK)
  - `statusJornada` (@Enumerated) → StatusJornada {ATIVA, INATIVA}
  - `xpBonus` (Integer) — Bônus ao completar jornada
  - `ordemJornada` (Integer) — Posição na trilha

- **Métodos:**
  - `subtraiXpMissao(xpMissao)` — Atualiza total ao remover missão
  - `recalculaXpJornada(tipo, xpAntigo, xpNovo)` — Recalcula XP
  - `validaJornadaAtiva()` — Invariante: Precisa estar ATIVA para modificar

- **Classe:** RICH

---

#### [TipoMissao](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/tipomissao/domain/TipoMissao.java)
- **Domínio:** Categorização de tipos de missão (teórica, prática, desafio, etc)
- **Tabela:** `tipo_missao`
- **Campos:**
  - `idTipoMissao` (UUID, PK)
  - `descricao` (String)

- **Classe:** ANÊMICA

---

#### [MissaoWakanda](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/domain/MissaoWakanda.java)
- **Domínio:** Atividades individuais de aprendizado
- **Tabela:** `missao_wakanda`
- **Agregado Raiz:** SIM
- **Campos:**
  - `idMissao` (UUID, PK)
  - `titulo` (String, UNIQUE)
  - `descricao` (String)
  - `xpBase` (Integer) — XP base para conclusão
  - `idTipoMissao` (UUID, FK)
  - `idJornada` (UUID, FK)
  - `missaoStatus` (@Enumerated) → MissaoStatus {ATIVA, INATIVA}
  - `ordemMissao` (@Embedded OrdemMissao)
  - `sabedorias` (@Embedded Sabedorias) — 5 tipos de conhecimento
  - `idMissaoExterna` (String) — ID em sistema externo
  - `idMissaoPai` (UUID, FK, nullable) — Missão pai para subtarefas
  - `idClasseMinima` (UUID, FK, nullable) — Classe mínima para acessar
  - `conteudoUrl` (String) — URL do conteúdo
  - `processamentoStatus` (@Enumerated) → ProcessamentoStatus {EM_PROCESSO, COMPLETO}

- **Métodos Comportamentais:**
  - `desativaMissao()` — Marca como INATIVA se ainda ATIVA
  - `alteraXpBase(novoXp)` — Atualiza XP com validações
  - `atualizaMissaoComDadosIA(request)` — Atualiza via IA, muda processamentoStatus
  - `atualizaConteudoUrl(url)` — Atualiza URL do conteúdo
  - `validaSabedorias()` — Invariante: Precisa ter ao menos 1 sabedoria
  - `validaXPBase()` — Invariante: XP > 0
  - `estaAtiva()` → Boolean
  - `possuiClasseMinimaDefinida()` → Boolean

- **Invariantes:**
  - titulo UNIQUE
  - xpBase > 0
  - Ao menos 1 sabedoria definida
  - Se processamentoStatus == COMPLETO, não pode ser alterada via IA novamente

- **Classe:** RICH

---

#### [OrdemMissao](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/domain/OrdemMissao.java)
- **Tipo:** Value Object (@Embeddable)
- **Campos:**
  - `ordem` (Integer)

- **Métodos:**
  - `criar(ordem)` — Factory com validação (ordem >= 0)
  - `validarNovaPosicao(novaPosicao, total, atual, status)` — Static, valida reposicionamento
  - `ajustarPosicoesIntermediarias()` — Static, reorganiza outras missões
  - `reorganizarMissoesComInativasNoFinal()` — Static, move inativas para o fim

- **Invariante:** ordem >= 0

---

#### [Sabedorias](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/domain/Sabedorias.java)
- **Tipo:** Value Object (@Embeddable)
- **Campos:**
  - `teorico` (Integer)
  - `processo` (Integer)
  - `knowHow` (Integer)
  - `comportamental` (Integer)
  - `criativo` (Integer)

- **Métodos:**
  - `ofNullable(sabedorias)` — Factory que retorna zeros se null
  - `isEmpty()` → Boolean (todos zeros)
  - `atualizaSabedorias(novaSabedorias)` — Atualiza todos os campos

- **Classe:** RICH (validações de negócio)

---

#### [ClasseWakanda](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/classewakanda/domain/ClasseWakanda.java)
- **Domínio:** Categorização de progresso (bronze, prata, ouro, etc)
- **Tabela:** `classe_wakanda`
- **Campos:**
  - `idClasse` (UUID, PK)
  - `nome` (String, UNIQUE)
  - `descricao` (String, UNIQUE)
  - `nivelNecessario` (Integer) — Nível de XP mínimo
  - `ordemClasse` (Integer) — Hierarquia (1=bronze, 2=prata, ...)
  - `sabedorias` (@Embedded Sabedorias) — Requisitos mínimos
  - `missoesNecessarias` (List<UUID>, @ElementCollection) — Missões que devem ser completadas
  - `status` (@Enumerated) → ClasseWakandaStatus {ATIVA, INATIVA}

- **Métodos:**
  - `atualizaParcialmente(dto, validador)` — Atualiza campos com validação
  - `verificaSeIgualOuSuperior(outraClasse)` → Boolean

- **Invariantes:**
  - nivelNecessario > 0
  - Ao menos 1 sabedoria
  - Ao menos 1 missão necessária

- **Classe:** RICH

---

### 2.2 SUB-CONTEXTO: **PROGRESSO** — Acompanhamento do Usuário

#### [ProgressoWakander](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/progressowakander/domain/ProgressoWakander.java)
- **Domínio:** Registro raiz de progresso de um Wakander
- **Tabela:** `progresso_wakander`
- **Agregado Raiz:** SIM
- **Campos:**
  - `idProgressoWakander` (UUID, PK)
  - `idWakander` (UUID, FK, UNIQUE) — 1:1 com Wakander
  - `dataCriacao` (LocalDateTime)

- **Métodos:** Apenas construtor
- **Classe:** ANÊMICA (é um agregado raiz apenas para manter referência)

---

#### [JornadaProgresso](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/jornadaprogresso/domain/JornadaProgresso.java)
- **Domínio:** Progresso dentro de uma jornada
- **Tabela:** `jornada_progresso`
- **Campos:**
  - `idJornadaProgresso` (UUID, PK)
  - `idJornadaWakanda` (UUID, FK)
  - `idProgressoWakander` (UUID, FK)
  - `status` (@Enumerated) → JornadaProgressoStatus {EM_ANDAMENTO, CONCLUIDA}
  - `xpObtido` (Integer)
  - `dataInicio` (LocalDateTime)
  - `ultimaAtualizacao` (LocalDateTime)

- **Métodos:**
  - `concluiJornada(JornadaWakanda)` — Marca CONCLUIDA, adiciona xpBonus
  - `criarEmAndamento(idJornada, idProgresso)` — Factory static

- **Classe:** RICH

---

#### [MissaoProgresso](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/domain/MissaoProgresso.java)
- **Domínio:** Progresso dentro de uma missão
- **Tabela:** `missao_progresso` (UNIQUE: idMissaoWakanda + idProgressoWakander)
- **Campos:**
  - `idMissaoProgresso` (UUID, PK)
  - `idMissaoWakanda` (UUID, FK)
  - `idProgressoWakander` (UUID, FK)
  - `statusProgresso` (@Enumerated) → MissaoProgressoStatus {EM_ANDAMENTO, CONCLUIDA}
  - `xpObtido` (Integer)
  - `tentativas` (Integer)
  - `ultimaAtualizacao` (LocalDateTime)
  - `dataConclusao` (LocalDateTime, nullable)
  - `sabedorias` (@Embedded SabedoriasMissaoProgresso)

- **Métodos:**
  - `concluiMissao(xpBase, Sabedorias)` — Marca CONCLUIDA, registra dataConclusao
  - `criarEmAndamento(idMissao, idProgresso)` — Factory static

- **Invariante:** Uma missão não pode ser concluída duas vezes

- **Classe:** RICH

---

#### [SabedoriasMissaoProgresso](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/domain/SabedoriasMissaoProgresso.java)
- **Tipo:** Value Object (@Embeddable)
- **Estrutura:** Idêntica a Sabedorias, mas para progresso
- **Métodos:**
  - `fromCatalogo(Sabedorias)` — Converter da entidade catalogo
  - `toCatalogo()` → Sabedorias
  - `isEmpty()` → Boolean
  - `ofNullable()` → Factory

---

### 2.3 SUB-CONTEXTO: **XP** — Sistema de Experiência

#### [XpWakander](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/domain/XpWakander.java)
- **Domínio:** Acúmulo de experiência e cálculo de níveis
- **Tabela:** `xp_wakander`
- **Campos:**
  - `idXpWakander` (UUID, PK)
  - `idProgressoWakander` (UUID, FK)
  - `xpTotal` (Integer) — Acúmulo total
  - `nivelAtual` (Integer) — Nível atual (começa em 1)
  - `xpProximoNivel` (Integer) — XP necessário para next level
  - `sabedorias` (@Embedded Sabedorias) — Totais por tipo
  - `ultimaAtualizacao` (LocalDateTime)

- **Métodos Comportamentais:**
  - `novoComDefaults(idProgresso)` — Factory: cria com xpTotal=0, nivel=1, xpProx=35 (Fibonacci)
  - `adicionarXpEAtualizarNivel(xpObtido, sabedorias)` — Adiciona XP e atualiza level/wisdoms
  - `adicionaXp(xpGanho)` — Incrementa xpTotal
  - `atualizaNivel()` → Int (quantidade de níveis subidos)
  - `podeSubirDeNivel()` → Boolean
  - `subirNivel()` — Sobe 1 nível, recalcula xpProximoNivel via Fibonacci
  - `fibonacciCalculaXpParaNivel(nivel)` → Int (35 * fib(n))
  - `validaXpSabedoriasProximaClasseAtingido(proximaClasse)` → Boolean

- **Invariante:** Nível mínimo = 1, XP Fibonacci crescente

- **Classe:** RICH (cálculos complexos de nível)

---

#### [HistoricoClasseWakander](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/historicoclasse/domain/HistoricoClasseWakander.java)
- **Domínio:** Histórico de promoções entre classes
- **Tabela:** `historico_classe_wakander`
- **Campos:**
  - `idHistoricoClasse` (UUID, PK)
  - `idClasse` (UUID, FK)
  - `idXpWakander` (UUID, FK)
  - `idProgressoWakander` (UUID, FK)
  - `idWakander` (UUID, FK)
  - `status` (@Enumerated) → HistoricoClasseWakanderStatus {EM_ANDAMENTO, CONCLUIDA}
  - `dataInicio` (LocalDateTime)
  - `dataFim` (LocalDateTime, nullable)

- **Métodos:**
  - `concluiClasseAtual()` — Marca CONCLUIDA e registra dataFim

- **Classe:** RICH

---

## § 3 — Enums de Domínio

### Domínio: **WAKANDER**

| Enum | Valores | Uso | Tipo DB |
|------|---------|-----|---------|
| `StatusCadastro` | INCOMPLETO, COMPLETO | @Column(name="status_cadastro") em Wakander | ENUM STRING |
| `WakanderStatusFinanceiro` | REGULAR, CANCELAMENTO_SOLICITADO, CANCELADO | @Column(name="status_financeiro") em WakanderFinanceiro | ENUM STRING |
| `StatusDadosFiador` | COMPLETO, INCOMPLETO | Apenas em memória, retorno de método | - |

### Domínio: **AUTENTICACAO**

| Enum | Valores | Uso |
|------|---------|-----|
| `StatusToken` | VALIDO, EXPIRADO, UTILIZADO | @Column(name="status_token") em Autenticacao |
| `StatusUsuario` | ATIVO, BLOQUEADO | @Column(name="status_usuario") em UsuarioAdm |
| `PerfilUsuario` | NAO_VERIFICADO, DEV, LIDERANCA | @Column em UsuarioAdm |

### Domínio: **FINANCEIRO**

| Enum | Valores | Uso |
|------|---------|-----|
| `CobrancaStatus` | PENDENTE, PAGAMENTO_CONFIRMADO, NEGATIVADO, PAGAMENTO_VENCIDO | @Column(name="status") em Cobranca |

### Domínio: **GAMIFICACAO - Catalogo**

| Enum | Valores | Uso |
|------|---------|-----|
| `MissaoStatus` | ATIVA, INATIVA | @Column(name="missao_status") em MissaoWakanda |
| `ProcessamentoStatus` | EM_PROCESSO, COMPLETO | @Column(name="processamento_status") em MissaoWakanda |
| `StatusJornada` | ATIVA, INATIVA | @Column(name="status_jornada") em JornadaWakanda |
| `ClasseWakandaStatus` | ATIVA, INATIVA | @Column(name="status") em ClasseWakanda |
| `TipoRecalculo` | XP_ALTERADO, MISSAO_ADICIONADA | Apenas em memória, lógica de recálculo |

### Domínio: **GAMIFICACAO - Progresso**

| Enum | Valores | Uso |
|------|---------|-----|
| `JornadaProgressoStatus` | EM_ANDAMENTO, CONCLUIDA | @Column(name="status") em JornadaProgresso |
| `MissaoProgressoStatus` | EM_ANDAMENTO, CONCLUIDA | @Column(name="status") em MissaoProgresso |
| `HistoricoClasseWakanderStatus` | EM_ANDAMENTO, CONCLUIDA | @Column(name="status") em HistoricoClasseWakander |

### Domínio: **GAMIFICACAO - XP**

| Enum | Valores | Uso |
|------|---------|-----|
| `NivelWakander` | CONHECIMENTO | Marcador de domínio (aparenta não estar em uso ativo) |

---

## § 4 — Mapa de Contextos (Bounded Context Map)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        WAKANDA-AI-DEV CONTEXT MAP                       │
└─────────────────────────────────────────────────────────────────────────┘

    ┌──────────────────────────────────────────────────────────────────┐
    │                  WAKANDER (Identidade)                           │
    │  Agregado Raiz: Wakander                                        │
    │  - Informações pessoais (nome, CPF, dataNascimento)             │
    │  - Status financeiro (REGULAR/CANCELADO)                        │
    │  - Referência a jornada atual (enum JornadaWakanda)             │
    │  - Contato, Fiador, AulaAssistida (Value Objects)              │
    │                                                                  │
    │  Comunicação: SÍNCRONA                                          │
    │  - → Gamificação/Progresso (criar ProgressoWakander)            │
    │  - → Autenticação (validar login)                               │
    │  - → Financeiro (status financeiro)                             │
    └──────────────────────────────────────────────────────────────────┘
                           │                  │
              ┌────────────┴──────────┐        │
              ▼                       ▼        │
        ┌─────────────────────┐  ┌──────────────────────────┐
        │   AUTENTICACAO      │  │   FINANCEIRO (Cobrança)  │
        │                     │  │                          │
        │ Agregados:          │  │ Agregados:               │
        │ - Autenticacao      │  │ - Cobranca               │
        │ - UsuarioAdm        │  │                          │
        │                     │  │ Comunicação:             │
        │ Tokens com expiração│  │ - ASSÍNCRONA (Eventos    │
        │                     │  │   Asaas Webhooks)        │
        └─────────────────────┘  └──────────────────────────┘
                           
              ┌────────────────────────────────────────────┐
              │    GAMIFICAÇÃO (3 Sub-contextos)           │
              │                                            │
              │  2.1 CATALOGO                             │
              │  ├─ TrilhaWakanda (agregado raiz)         │
              │  ├─ JornadaWakanda (agregado raiz)        │
              │  ├─ TipoMissao                             │
              │  ├─ MissaoWakanda (agregado raiz)         │
              │  │  └─ OrdemMissao, Sabedorias (VO)        │
              │  │                                        │
              │  │  Comunicação: SÍNCRONA                 │
              │  │  - Recebe idTrilha, idJornada, etc     │
              │  │  - Valida XP, sabedorias              │
              │  │                                        │
              │  2.2 PROGRESSO                             │
              │  ├─ ProgressoWakander (agregado raiz)     │
              │  ├─ JornadaProgresso                      │
              │  ├─ MissaoProgresso (agregado raiz)       │
              │  │  └─ SabedoriasMissaoProgresso (VO)     │
              │  │                                        │
              │  │  Comunicação: SÍNCRONA COM:             │
              │  │  - XpWakander (quando conclui missão)  │
              │  │  - HistoricoClasseWakander             │
              │  │  - Eventos de conclusão de missão      │
              │  │                                        │
              │  2.3 XP                                   │
              │  ├─ XpWakander (agregado raiz)            │
              │  │  └─ Sabedorias (VO)                    │
              │  │  - Nível = Fibonacci(XP)              │
              │  │                                        │
              │  ├─ ClasseWakanda (agregado raiz)         │
              │  │  └─ Sabedorias req, Missões req        │
              │  │                                        │
              │  └─ HistoricoClasseWakander               │
              │     - Registra promoções de classe        │
              │                                            │
              │  Comunicação Intra-Gamificação:           │
              │  - Progresso → XP (acúmulo)              │
              │  - XP → Classe (promoção)                │
              │  - Catalogo → Progresso (validações)     │
              │                                            │
              └────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                  LINHAS DE COMUNICAÇÃO CROSS-CONTEXTS                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│ SÍNCRONA (REST/JPA Queries):                                           │
│ - Wakander → Gamificação: criar ProgressoWakander ao registrar novo    │
│ - Wakander → Autenticação: validar token antes de operações            │
│ - Financeiro → Wakander: atualizar status financeiro (Asaas webhook)   │
│ - Gamificação.Progresso ↔ Gamificação.XP: sincronizar XP ao completar │
│                                                                         │
│ ASSÍNCRONA (Eventos / SQS):                                            │
│ - Financeiro (Asaas): eventos de pagamento, vencimento, negativação    │
│ - Gamificação: eventos de conclusão de missão, promoção de classe      │
│ - Comunicação: whatsapp, discord (integração com Wakander)             │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## § 5 — Relações entre Agregados (Detalhado)

### Ligações por UUID (Referências de Identidade)

1. **Wakander → ProgressoWakander** (1:1, exclusiva)
   - Coluna: `id_wakander` (FK em progresso_wakander)
   - Semantics: Cada Wakander tem exatamente um registro de progresso

2. **ProgressoWakander → XpWakander** (1:1, exclusiva)
   - Coluna: `id_progresso_wakander` (FK em xp_wakander)
   - Semantics: Cada ProgressoWakander tem um perfil XP

3. **ProgressoWakander → JornadaProgresso** (1:N)
   - Coluna: `id_progresso_wakander` (FK em jornada_progresso)
   - Semantics: Um Wakander pode estar em múltiplas jornadas (sequencial ou paralelo)

4. **ProgressoWakander → MissaoProgresso** (1:N)
   - Coluna: `id_progresso_wakander` (FK em missao_progresso)
   - Semantics: Um Wakander tem múltiplas missões em progresso

5. **JornadaWakanda (Catalogo) → JornadaProgresso** (1:N)
   - Coluna: `id_jornada_wakanda` (FK em jornada_progresso)
   - Semantics: Uma jornada catalogada pode ter múltiplos progresso de wakanders

6. **MissaoWakanda (Catalogo) → MissaoProgresso** (1:N)
   - Coluna: `id_missao_wakanda` (FK em missao_progresso)
   - Semantics: Uma missão pode ser realizada por múltiplos wakanders

7. **TrilhaWakanda → JornadaWakanda** (1:N)
   - Coluna: `id_trilha_wakanda` (FK em jornada_wakanda)
   - Semantics: Uma trilha contém múltiplas jornadas

8. **JornadaWakanda → MissaoWakanda** (1:N)
   - Coluna: `id_jornada` (FK em missao_wakanda)
   - Semantics: Uma jornada contém múltiplas missões

9. **ClasseWakanda ← MissaoWakanda** (N:N, via tabela classe_wakanda_missoes)
   - Semantics: Uma classe requer múltiplas missões; uma missão pode ser requisito de múltiplas classes

10. **Cobranca → Wakander** (N:1)
    - Coluna: `id_wakander` (FK em cobranca)
    - Semantics: Múltiplas cobranças por Wakander

11. **Autenticacao → Wakander** (N:1)
    - Coluna: `id_wakander` (FK em autenticacao)
    - Semantics: Um Wakander pode ter múltiplos tokens válidos

---

## § 6 — Divergências e Anti-patterns Encontrados

### 1. **Falta de Constraint UNIQUE em ClasseWakanda.nivelNecessario**
   - **Esperado:** Cada classe deveria ter um nível único (bronze=10, prata=20, ouro=30)
   - **Atual:** Apenas existe `existsByNivelNecessario()` mas sem constraint UNIQUE no DDL
   - **Impacto:** Múltiplas classes podem ter o mesmo nível, causando ambiguidade
   - **Recomendação:** Adicionar UNIQUE constraint em migration

### 2. **UsuarioAdm é UserDetails mas sem @Column mapping**
   - **Issue:** Alguns getters de SecurityContext não mapeiam para `@Column`
   - **Classe:** Usa getPassword(), getUsername(), isEnabled() sem explicitação

### 3. **JornadaWakander (legado) sem @Table**
   - **Actual name:** Inferido como `jornada_wakander` pelo Hibernate
   - **Risco:** Pode diferir de migrations se mudar naming convention
   - **Recomendação:** Adicionar @Table(name="jornada_wakander") explícito

### 4. **Falta de HistoricoRelatorio, AulaAssistida como entidades**
   - **Encontradas:** Via grep nas migrations
   - **Não encontradas:** Seus domínios no codebase (podem estar ocultos ou deletados)
   - **Impacto:** Inconsistência entre schema (67 migrations) e entities

### 5. **SabedoriaMissaoProgresso é duplicação de Sabedorias**
   - **Current:** Ambas têm 5 campos idênticos (teorico, processo, knowHow, comportamental, criativo)
   - **Code Smell:** Violação DRY. Considerar usar Sabedorias diretamente com type conversions

### 6. **ProcessamentoStatus em MissaoWakanda não tem Constraint CHECK**
   - **Valores:** EM_PROCESSO, COMPLETO
   - **Uso:** Controla processamento por IA
   - **Risk:** Poderia ficar inconsistente se atualizado manualmente no BD

### 7. **Wakander.@Column(name="jornada_atual") é Enum mas refere JornadaWakanda (enum no jornadawakander package)**
   - **Confuso:** Enum JornadaWakanda está em `jornadawakander/domain/` mas é usada em Wakander core
   - **Não há relação FK:** Apenas storage de enum, não referência a JornadaWakanda table

---

