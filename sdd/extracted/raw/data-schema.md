# Data Schema - Wakanda-AI-Dev PostgreSQL

## § 1 — Tabelas Postgres (Inventory)

### Core Tables

#### Table: `wakander`
**Entidade JPA:** [Wakander](src/main/java/academy/wakanda/wakanda_ai/wakander/domain/Wakander.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_wakander` | UUID | PK | AUTO GEN |
| `nome` | VARCHAR(255) | NOT NULL | |
| `cpf` | VARCHAR(14) | NOT NULL, UNIQUE | Validado @CPF |
| `id_member_kit` | VARCHAR(255) | UNIQUE | Nullable até preenchimento |
| `id_discord` | VARCHAR(255) | | Nullable |
| `user_discord` | VARCHAR(255) | | Nullable |
| `data_nascimento` | DATE | | Nullable |
| `whatsapp` | VARCHAR(20) | | Embedded: WakanderContato |
| `email` | VARCHAR(255) | | Embedded: WakanderContato |
| `status_financeiro` | VARCHAR(50) | NOT NULL, DEFAULT 'REGULAR' | ENUM: REGULAR/CANCELAMENTO_SOLICITADO/CANCELADO |
| `ultima_atualizacao_financeiro` | TIMESTAMP | | Embedded: WakanderFinanceiro |
| `motivo_cancelamento` | VARCHAR(255) | | Embedded: WakanderFinanceiro |
| `status_cadastro` | VARCHAR(50) | | ENUM: COMPLETO/INCOMPLETO |
| `jornada_atual` | VARCHAR(50) | | ENUM: JornadaWakanda |
| `id_asaas` | VARCHAR(255) | | Embedded: WakanderFiador |
| `id_assinatura` | VARCHAR(255) | UNIQUE | Embedded: WakanderFiador |
| `nome_fiador` | VARCHAR(255) | | Embedded: WakanderFiador |
| `cpf_fiador` | VARCHAR(14) | | Embedded: WakanderFiador |
| `telefone_fiador` | VARCHAR(20) | | Embedded: WakanderFiador |
| `ultima_aula_assistida_datetime` | TIMESTAMP | | Embedded: WakanderAulaAssistida |
| `id_ultima_aula_assistida` | UUID | | Embedded: WakanderAulaAssistida |

**Unique Constraints:** cpf, id_member_kit  
**Foreign Keys:** None (root aggregate)  
**Indexes:** cpf (UNIQUE), id_member_kit (UNIQUE), id_assinatura (UNIQUE)

---

#### Table: `cobranca`
**Entidade JPA:** [Cobranca](src/main/java/academy/wakanda/wakanda_ai/financeiro/domain/cobranca/Cobranca.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_cobranca` | UUID | PK | AUTO GEN |
| `id_wakander` | UUID | FK → wakander | Nullable antes de migration |
| `id_payment_asaas` | VARCHAR(255) | NOT NULL, UNIQUE | Payment ID from Asaas |
| `valor` | DECIMAL(19,2) | NOT NULL | Total value |
| `valor_liquido` | DECIMAL(19,2) | | Net value |
| `data_criacao` | DATE | NOT NULL | |
| `data_vencimento` | DATE | NOT NULL | |
| `data_pagamento` | DATE | | Nullable until paid |
| `status` | VARCHAR(50) | NOT NULL | ENUM: PENDENTE/PAGAMENTO_CONFIRMADO/NEGATIVADO/PAGAMENTO_VENCIDO |

**Unique Constraints:** id_cobranca, id_payment_asaas  
**Foreign Keys:** id_wakander → wakander.id_wakander

---

#### Table: `autenticacao`
**Entidade JPA:** [Autenticacao](src/main/java/academy/wakanda/wakanda_ai/autenticacao/domain/Autenticacao.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `token` | VARCHAR(64) | PK | String token as primary key |
| `id_wakander` | UUID | NOT NULL, FK | |
| `data_expiracao` | TIMESTAMP | NOT NULL | Truncated to seconds |
| `status_token` | VARCHAR(10) | NOT NULL | ENUM: VALIDO/EXPIRADO/UTILIZADO |
| `data_utilizacao` | TIMESTAMP | | Dropped in V20250611 migration |

**Foreign Keys:** id_wakander → wakander.id_wakander

---

#### Table: `usuario_admin`
**Entidade JPA:** [UsuarioAdm](src/main/java/academy/wakanda/wakanda_ai/autenticacao/domain/UsuarioAdm.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | UUID | PK | AUTO GEN |
| `nome` | VARCHAR(100) | NOT NULL | |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | Lowercase |
| `senha` | VARCHAR(255) | NOT NULL | Encoded |
| `tentativa_login` | INTEGER | NOT NULL, DEFAULT 0 | Increments on failed login |
| `status_usuario` | VARCHAR(20) | NOT NULL | ENUM: ATIVO/BLOQUEADO |
| `perfil` | VARCHAR(50) | NOT NULL | ENUM: NAO_VERIFICADO/DEV/LIDERANCA |
| `criado_em` | TIMESTAMP | NOT NULL | |

**Unique Constraints:** username

---

### Gamificação: Catálogo Tables

#### Table: `trilha_wakanda`
**Entidade JPA:** [TrilhaWakanda](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/trilhawakanda/domain/TrilhaWakanda.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_trilha` | UUID | PK | |
| `nome` | VARCHAR(255) | NOT NULL, UNIQUE | |
| `descricao` | TEXT | | |
| `xp_total` | INT | | Sum of jornadas |

**Foreign Keys:** None

---

#### Table: `tipo_missao`
**Entidade JPA:** TipoMissao

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_tipo_missao` | UUID | PK | |
| `descricao` | VARCHAR(255) | NOT NULL | |

---

#### Table: `jornada_wakanda`
**Entidade JPA:** [JornadaWakanda](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/jornadawakanda/domain/JornadaWakanda.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_jornada` | UUID | PK | |
| `titulo` | VARCHAR(255) | NOT NULL | |
| `descricao` | TEXT | NOT NULL | |
| `xp_total` | INT | NOT NULL | Sum of missoes |
| `id_trilha_wakanda` | UUID | NOT NULL, FK | |
| `status_jornada` | VARCHAR(50) | NOT NULL | ENUM: ATIVA/INATIVA |
| `xp_bonus` | INT | NOT NULL, DEFAULT 0 | Bonus on completion |
| `ordem_jornada` | INT | NOT NULL | Position in trilha |

**Foreign Keys:** id_trilha_wakanda → trilha_wakanda.id_trilha

---

#### Table: `missao_wakanda`
**Entidade JPA:** [MissaoWakanda](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/domain/MissaoWakanda.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_missao` | UUID | PK | |
| `titulo` | VARCHAR(255) | NOT NULL, UNIQUE | |
| `descricao` | TEXT | NOT NULL | |
| `xp_base` | INT | NOT NULL | > 0 |
| `id_tipo_missao` | UUID | NOT NULL, FK | |
| `id_jornada` | UUID | NOT NULL, FK | |
| `missao_status` | VARCHAR(50) | | ENUM: ATIVA/INATIVA |
| `ordem_missao` | INT | NOT NULL | Position in jornada |
| `sab_teorico` | INT | NOT NULL | Embedded: Sabedorias |
| `sab_processo` | INT | NOT NULL | Embedded: Sabedorias |
| `sab_know_how` | INT | NOT NULL | Embedded: Sabedorias |
| `sab_comportamental` | INT | NOT NULL | Embedded: Sabedorias |
| `sab_criativo` | INT | NOT NULL | Embedded: Sabedorias |
| `id_missao_externa` | VARCHAR(255) | NOT NULL | External system ID |
| `id_missao_pai` | UUID | | FK self-reference (subtasks) |
| `id_classe_minima` | UUID | | FK → classe_wakanda |
| `conteudo_url` | VARCHAR(255) | | Content link |
| `processamento_status` | VARCHAR(50) | NOT NULL, DEFAULT 'EM_PROCESSO' | ENUM: EM_PROCESSO/COMPLETO |

**Unique Constraints:** titulo, id_missao_externa (if filled)  
**Foreign Keys:** 
- id_tipo_missao → tipo_missao.id_tipo_missao
- id_jornada → jornada_wakanda.id_jornada
- id_missao_pai → missao_wakanda.id_missao (self)
- id_classe_minima → classe_wakanda.id_classe

---

#### Table: `classe_wakanda`
**Entidade JPA:** [ClasseWakanda](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/classewakanda/domain/ClasseWakanda.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_classe` | UUID | PK | |
| `nome` | VARCHAR(255) | NOT NULL, UNIQUE | Bronze, Silver, Gold, etc |
| `descricao` | TEXT | NOT NULL, UNIQUE | Description |
| `nivel_necessario` | INT | NOT NULL | Min XP level |
| `ordem_classe` | INT | NOT NULL | Hierarchy order (1,2,3...) |
| `sab_teorico` | INT | NOT NULL | Min wisdoms required |
| `sab_processo` | INT | NOT NULL | |
| `sab_know_how` | INT | NOT NULL | |
| `sab_comportamental` | INT | NOT NULL | |
| `sab_criativo` | INT | NOT NULL | |
| `status` | VARCHAR(50) | NOT NULL | ENUM: ATIVA/INATIVA |

**Unique Constraints:** nome, descricao  
**Foreign Keys:** None in main table  
**Note:** Migrate V20260224 renamed `xp_necessario` → `nivel_necessario`

#### Table: `classe_wakanda_missoes` (ElementCollection)
**No Entity:** Maps ClasseWakanda.missoesNecessarias

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_classe` | UUID | PK+FK | → classe_wakanda.id_classe |
| `id_missao` | UUID | PK+FK | → missao_wakanda.id_missao |

**Note:** N:N relationship, one row per required mission

---

### Gamificação: Progresso Tables

#### Table: `progresso_wakander`
**Entidade JPA:** [ProgressoWakander](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/progressowakander/domain/ProgressoWakander.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_progresso_wakander` | UUID | PK | DEFAULT gen_random_uuid() |
| `id_wakander` | UUID | NOT NULL, FK, UNIQUE | 1:1 relationship |
| `data_criacao` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | |

**Dropped Columns (V20250901):**
- `nivel_atual` (VARCHAR(50))
- `classe_atual` (VARCHAR(100))

**Foreign Keys:** id_wakander → wakander.id_wakander

---

#### Table: `jornada_progresso`
**Entidade JPA:** [JornadaProgresso](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/jornadaprogresso/domain/JornadaProgresso.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_jornada_progresso` | UUID | PK | |
| `id_jornada_wakanda` | UUID | NOT NULL, FK | |
| `id_progresso_wakander` | UUID | NOT NULL, FK | |
| `status` | VARCHAR(50) | | ENUM: EM_ANDAMENTO/CONCLUIDA |
| `xp_obtido` | INT | NOT NULL | Bonus XP from jornada |
| `data_inicio` | TIMESTAMP | | |
| `ultima_atualizacao` | TIMESTAMP | | |

**Foreign Keys:** 
- id_jornada_wakanda → jornada_wakanda.id_jornada
- id_progresso_wakander → progresso_wakander.id_progresso_wakander

---

#### Table: `missao_progresso`
**Entidade JPA:** [MissaoProgresso](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/domain/MissaoProgresso.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_missao_progresso` | UUID | PK | |
| `id_missao_wakanda` | UUID | NOT NULL, FK | |
| `id_progresso_wakander` | UUID | NOT NULL, FK | |
| `status` | VARCHAR(50) | | ENUM: EM_ANDAMENTO/CONCLUIDA |
| `xp_obtido` | INT | NOT NULL | XP from completing missao |
| `tentativas` | INT | NOT NULL | Attempt counter |
| `ultima_atualizacao` | TIMESTAMP | | |
| `data_conclusao` | TIMESTAMP | | When completed |
| `sab_teorico` | INT | NOT NULL | Embedded: SabedoriasMissaoProgresso (V20260513) |
| `sab_processo` | INT | NOT NULL | |
| `sab_know_how` | INT | NOT NULL | |
| `sab_comportamental` | INT | NOT NULL | |
| `sab_criativo` | INT | NOT NULL | |

**Unique Constraints:** (id_missao_wakanda, id_progresso_wakander)  
**Foreign Keys:**
- id_missao_wakanda → missao_wakanda.id_missao
- id_progresso_wakander → progresso_wakander.id_progresso_wakander

---

### Gamificação: XP Tables

#### Table: `xp_wakander`
**Entidade JPA:** [XpWakander](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/domain/XpWakander.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_xp_wakander` | UUID | PK | |
| `id_progresso_wakander` | UUID | NOT NULL, FK | |
| `xp_total` | INT | NOT NULL | Cumulative XP |
| `nivel_atual` | INT | NOT NULL | Current level (1-based) |
| `xp_proximo_nivel` | INT | | XP needed for next level (Fibonacci) |
| `sab_teorico` | INT | | Embedded: Sabedorias (V20260225) |
| `sab_processo` | INT | | |
| `sab_know_how` | INT | | |
| `sab_comportamental` | INT | | |
| `sab_criativo` | INT | | |
| `ultima_atualizacao` | TIMESTAMP | | |

**Dropped Columns (V20250901, V20260521):**
- `classe_atual` (VARCHAR(100))

**Foreign Keys:** id_progresso_wakander → progresso_wakander.id_progresso_wakander

---

#### Table: `historico_classe_wakander`
**Entidade JPA:** [HistoricoClasseWakander](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/historicoclasse/domain/HistoricoClasseWakander.java)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_historico_classe` | UUID | PK | |
| `id_classe` | UUID | NOT NULL, FK | |
| `id_xp_wakander` | UUID | NOT NULL, FK | |
| `id_progresso_wakander` | UUID | NOT NULL, FK | |
| `id_wakander` | UUID | NOT NULL, FK | |
| `status` | VARCHAR(50) | NOT NULL | ENUM: EM_ANDAMENTO/CONCLUIDA |
| `data_inicio` | TIMESTAMP | NOT NULL | |
| `data_fim` | TIMESTAMP | | When concluded |

**Foreign Keys:**
- id_classe → classe_wakanda.id_classe
- id_xp_wakander → xp_wakander.id_xp_wakander
- id_progresso_wakander → progresso_wakander.id_progresso_wakander
- id_wakander → wakander.id_wakander

---

### Legado / Suporte Tables

#### Table: `onboarding_wakander`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id_onboarding` | UUID | PK | |
| `id_wakander` | UUID | | FK |
| `id_discord` | VARCHAR(255) | | Discord ID (dropped V20260318) |
| `user_discord` | VARCHAR(255) | | Discord username (dropped V20260318) |

**Note:** Merged into wakander table (V20260318 migrations)

#### Table: `jornada_wakander`
Tracks historical jornadas (different from `jornada_wakanda` catalog)

#### Table: `aula_assistida` / `historico_relatorio`
Created per migrations but entities not found in current code

---

## § 2 — Migrations Flyway — Sequência Histórica

Total: **67 migrations** (verified: `ls | wc -l` = 67)

### Fase 1: Core Wakander (Oct 2024 - Jan 2025)

| Migration | Type | Purpose |
|-----------|------|---------|
| V20241023164800__create_table_wakander.sql | CREATE | Initial wakander table |
| V20241023173900__add_constraint_unique_cpf_wakander.sql | ALTER | CPF unique constraint |
| V20241114151700__add_column_ultima_atualizacao_financeiro.sql | ALTER | Track last financial update |
| V20241126160900__add_constraint_unique_id_member_kit_wakander.sql | ALTER | Unique memberkit ID |
| V20241126170400__create_table_aula_assistida.sql | CREATE | Track class attendance |
| V20250116184600__alter_column_cpf_null_wakander.sql | ALTER | Make CPF nullable (interim) |
| V20250120122000__alter_table_wakander_unique.sql | ALTER | Unique constraint adjustments |

### Fase 2: Cobrança/Financeiro (Feb 2025)

| Migration | Type | Purpose |
|-----------|------|---------|
| V20250206160000__create_table_cobranca.sql | CREATE | Billing table |
| V20250206170000__alter_table_wakander_id_asaas.sql | ALTER | Add Asaas customer ID |
| V20250210165700__alter_table_wakander_fiador.sql | ALTER | Add guarantor info |
| V20250211155000__alter_table_wakander_id_asaas.sql | ALTER | Modify Asaas field |
| V20250212114600__alter_table_cobranca_id_payment_asaas.sql | ALTER | Add Asaas payment ID |
| V20250213163000__create_table_historico_relatorio.sql | CREATE | Report history |
| V20250314120000__alter_column_data_vencimento_null_cobranca.sql | ALTER | Make due date nullable |
| V20250315181100__alter_table_wakander_drop_not_null.sql | ALTER | Relax constraints |
| V20250316194100__alter_table_wakander_add_column.sql | ALTER | Add column (unclear name) |
| V20250318214000__add_column_wakander.sql | ALTER | Discord fields |
| V20250328172500__alter_table_cobranca_id_cobranca.sql | ALTER | Cobranca ID constraints |
| V20250328173100__alter_table_cobranca_drop_null_id_wakander.sql | ALTER | Drop null constraint |
| V20250328173400__add_columm_valor_liquido_cobranca.sql | ALTER | Net value field |
| V20250411163400__add_columm_data_nascimento_wakander.sql | ALTER | Birth date field |

### Fase 3: Onboarding/Auth (May - Jun 2025)

| Migration | Type | Purpose |
|-----------|------|---------|
| V20250513210010__create_table_onboarding_wakander.sql | CREATE | Onboarding flow table |
| V20250526204500__add_column_user_discord_to_wakander.sql | ALTER | Discord username to wakander |
| V20250609150600__create_table_autenticacao.sql | CREATE | Authentication tokens |
| V20250609160100__alter_table_autenticacao_tamanho_token.sql | ALTER | Enlarge token column |
| V20250611162200__alter_table_autenticacao_deleta_data_utilizacao.sql | ALTER | Drop usage timestamp |
| V20250613151600__create_table_usuario_lideranca.sql | CREATE | Leadership users (admin) |
| V20250623170200__add_columm_id_discord_onboarding.sql | ALTER | Discord ID to onboarding |
| V20250630174500__add_columm_user_discord_onboarding.sql | ALTER | Discord user to onboarding |
| V20250630174900__drop_columm_user_discord_wakander.sql | ALTER | Remove Discord user from wakander |
| V20250702203200__insert_wakanders_sem_registro_onboarding.sql | INSERT | Seed data for orphaned wakanders |

### Fase 4: Gamificação Catalogo (Jul - Sep 2025)

| Migration | Type | Purpose |
|-----------|------|---------|
| V20250722154700__create_table_trilha_wakanda.sql | CREATE | Learning paths |
| V20250722164800__create_table_jornada_wakanda.sql | CREATE | Jornadas within paths |
| V20250722164900__create_table_tipo_missao.sql | CREATE | Mission types |
| V20250722165000__create_table_missao_wakanda.sql | CREATE | Individual missions |
| V20250729125900__add_constraint_unique_nome_trilha-wakanda.sql | ALTER | Unique trilha name |
| V20250729180000__create_table_progresso_wakander\ .sql | CREATE | Progress root aggregate |
| V20250729181000__create_table_jornada_progresso_wakander\ .sql | CREATE | Journey progress tracking |
| V20250729181100__create_table_missao_progresso\ .sql | CREATE | Mission progress tracking |
| V20250730180400__alter_progresso_wakander_id_wakander_constraints.sql | ALTER | Add constraints to progresso |
| V20250731204239__add_column_status_jornada_jornada_wakanda.sql | ALTER | Add jornada status |
| V20250804101600__alter_table_missao_add_column_status.sql | ALTER | Add missao status |
| V20250804111200__alter_table_missao_add_column_type.sql | ALTER | Add missao type |
| V20250901113800__alter_table_progresso_wakander_remove_column_classe.sql | ALTER | Remove level (moved to xp_wakander) |
| V20250901113900__alter_table_progresso_wakander_remove_column_nivel.sql | ALTER | Remove nivel field |
| V20250904203903__drop_table_gameficacao.sql | DROP | Remove old gamification schema |
| V20250904210200__create_tables_gameficacao.sql | CREATE | **Recreate all 8 gamification tables** |
| V20250911145800__add_column-data-conclusao_missao-progesso.sql | ALTER | Track mission completion date |
| V20250912195100__add_column-id-missao-externa-missao-wakanda.sql | ALTER | External mission ID |
| V20250917100120__alter_table_xp_wakander_add_column.sql | ALTER | Add XP field |
| V20251006205400__add_constraint_unique_id_missao_and_id_progresso_missao_progresso.sql | ALTER | Unique (missao, progresso) pair |
| V20251012165400__add_column-id_missao_pai-missao-wakanda.sql | ALTER | Parent mission for subtasks |
| V20251128082424__add_constraint_unique_descricao.sql | ALTER | Unique descriptions |

### Fase 5: Classes & XP (Feb - May 2026)

| Migration | Type | Purpose |
|-----------|------|---------|
| V20260202120000__create_table_classe_wakanda.sql | CREATE | Class hierarchy |
| V20260202120100__alter_table_missao_wakanda_add_column_id_classe_minima.sql | ALTER | Min class req for mission |
| V20260208180808__add_column_conteudo_url.sql | ALTER | Content URL field |
| V20260210210207__add_column_processamento_status.sql | ALTER | IA processing status |
| V20260224210000__alter_table_classe_wakanda_rename_xp_necessario_to_nivel_necessario.sql | ALTER | **Schema rename:** xp_necessario → nivel_necessario |
| V20260225183000__add_columns_sabedorias_to_xp_wakander.sql | ALTER | Add wisdom fields to XP |
| V20260225183100__create_table_historico_classe_wakander.sql | CREATE | Class promotion history |
| V20260318080000__add_columns_discord_wakander.sql | ALTER | Discord fields to wakander |
| V20260318080100__migrate_discord_onboarding_to_wakander.sql | INSERT | Migrate Discord data |
| V20260318080200__drop_columns_discord_onboarding_wakander.sql | ALTER | Drop Discord from onboarding |
| V20260513120000__add_columns_sabedorias_to_missao_progresso.sql | ALTER | Add wisdom tracking to mission progress |
| V20260521171700__alter_table_xp_wakander_drop_column_classe_atual.sql | ALTER | Remove clase_atual (dropped earlier too) |

---

## § 3 — JSONB Usage

**Current Status:** NO JSONB columns found in current migrations or entity mappings.

**Search Result:** 
```bash
grep -r "@JdbcTypeCode(SqlTypes.JSON)" src/main/java/ → NO RESULTS
grep -r "columnDefinition.*jsonb" src/main/resources/db/migration/ → NO RESULTS
```

**Implication:** 
- All data is normalized (columns)
- No JSONB metadata storage
- Each attribute is a dedicated column

---

## § 4 — Constraints e Indexes

### Primary Keys

| Table | PK Column | Type |
|-------|-----------|------|
| wakander | id_wakander | UUID |
| cobranca | id_cobranca | UUID |
| autenticacao | token | VARCHAR(64) |
| usuario_admin | id | UUID |
| trilha_wakanda | id_trilha | UUID |
| jornada_wakanda | id_jornada | UUID |
| tipo_missao | id_tipo_missao | UUID |
| missao_wakanda | id_missao | UUID |
| classe_wakanda | id_classe | UUID |
| classe_wakanda_missoes | (id_classe, id_missao) | UUID, UUID (Composite) |
| progresso_wakander | id_progresso_wakander | UUID |
| jornada_progresso | id_jornada_progresso | UUID |
| missao_progresso | id_missao_progresso | UUID |
| xp_wakander | id_xp_wakander | UUID |
| historico_classe_wakander | id_historico_classe | UUID |

### Unique Constraints

| Table | Columns | Notes |
|-------|---------|-------|
| wakander | cpf, id_member_kit | Via @UniqueConstraint(columnNames=...) |
| cobranca | id_cobranca, id_payment_asaas | Via @UniqueConstraint |
| autenticacao | token | PK enforces uniqueness |
| usuario_admin | username | Via @Column(unique=true) |
| trilha_wakanda | nome | Via @Column(unique=true) |
| missao_wakanda | titulo | Via @Column(unique=true) |
| classe_wakanda | nome, descricao | Both UNIQUE |
| missao_progresso | (id_missao_wakanda, id_progresso_wakander) | Composite UNIQUE |
| progresso_wakander | id_wakander | 1:1 relationship |
| wakander.id_assinatura | (embedded fiador) | UNIQUE constraint in column |

### Foreign Key Constraints

| Constraint Name | Parent → Child | Notes |
|-----------------|---|---|
| fk_jornada_trilha | jornada_wakanda.id_trilha_wakanda → trilha_wakanda.id_trilha | Cascade rules? |
| fk_missao_tipo | missao_wakanda.id_tipo_missao → tipo_missao.id_tipo_missao | |
| fk_missao_jornada | missao_wakanda.id_jornada → jornada_wakanda.id_jornada | |
| fk_classe_wakanda (missoes) | classe_wakanda_missoes.id_classe → classe_wakanda.id_classe | |
| fk_missao_wakanda (missoes) | classe_wakanda_missoes.id_missao → missao_wakanda.id_missao | |
| fk_progresso_wakander | progresso_wakander.id_wakander → wakander.id_wakander | |
| fk_jornada_progresso_jornada | jornada_progresso.id_jornada_wakanda → jornada_wakanda.id_jornada | |
| fk_jornada_progresso_progresso | jornada_progresso.id_progresso_wakander → progresso_wakander.id_progresso_wakander | |
| fk_missao_progresso_missao | missao_progresso.id_missao_wakanda → missao_wakanda.id_missao | |
| fk_missao_progresso_progresso | missao_progresso.id_progresso_wakander → progresso_wakander.id_progresso_wakander | |
| fk_xp_progresso | xp_wakander.id_progresso_wakander → progresso_wakander.id_progresso_wakander | |
| fk_wakander (cobranca) | cobranca.id_wakander → wakander.id_wakander | Nullable initially, not null after |
| fk_token_wakander | autenticacao.id_wakander → wakander.id_wakander | |

### Check Constraints

**Status:** None explicitly defined in migrations.

**Could be Added:**
- `CHECK (xp_base > 0)` on missao_wakanda
- `CHECK (nivel_necessario > 0)` on classe_wakanda
- `CHECK (xp_total >= 0)` on trilha_wakanda, jornada_wakanda

### Indexes

**Implicit (via PRIMARY KEY & UNIQUE):**
- wakander(cpf)
- wakander(id_member_kit)
- cobranca(id_payment_asaas)
- usuario_admin(username)
- trilha_wakanda(nome)
- missao_wakanda(titulo)
- classe_wakanda(nome), classe_wakanda(descricao)
- progresso_wakander(id_wakander)

**Could Benefit from Explicit Indexes (Performance):**
- cobranca(id_wakander, status) — For status queries
- missao_progresso(id_progresso_wakander) — For progress lookups
- jornada_progresso(id_progresso_wakander) — For journey progress
- xp_wakander(id_progresso_wakander) — Already FK indexed implicitly
- autenticacao(id_wakander) — For token lookups by user

---

## § 5 — Repositórios e Seus Métodos Query

### Core Repositories

#### [WakanderSpringDataJpaRepository](src/main/java/academy/wakanda/wakanda_ai/wakander/infra/WakanderSpringDataJpaRepository.java)
**Extends:** JpaRepository<Wakander, UUID>, JpaSpecificationExecutor<Wakander>  
**Entidade:** Wakander

| Method | Query Type | Purpose |
|--------|-----------|---------|
| findByIdMemberKit(String) | Named method | Find by memberkit ID |
| buscaWakandersSemEstudar(...) | @Query | Find wakanders who haven't studied before date + status/jornada filters |
| findByIdAssinatura(String) | @Query | Find by Asaas subscription ID |
| buscaWakanderComDadosAsaasIncompleto() | @Query | Find wakanders with missing guarantor data |
| buscaPorQueryCpfOuTelefone(Pageable, query, bool) | @Query | Search by CPF/phone/name with pagination + include canceled option |
| findByStatusCadastro(Pageable, status, bool) | @Query | Paged filter by registration status |
| buscaWakanderRegularesPaginado(Pageable, status) | @Query | Paginated list of regular wakanders |
| buscaWakanderRegulares(status) | @Query | All regular wakanders (no pagination) |
| findByEmail(String) | @Query | Find by email (embedded field) |
| buscarWakandersComDadosIncompletos() | @Query | Missing personal data |
| buscaWakandersRegularesSemProgresso() | @Query | Regular but no progress record |
| buscaWakandersIrregulares() | @Query | Non-REGULAR status |
| buscaWakandersRegularesComProgresso() | @Query | Regular with progress record |

**Query Complexity:** MEDIUM (JPA path expressions, subqueries, pagination)

---

#### [CobrancaSpringDataJPARepository](src/main/java/academy/wakanda/wakanda_ai/financeiro/infra/CobrancaSpringDataJPARepository.java)
**Extends:** JpaRepository<Cobranca, UUID>  
**Entidade:** Cobranca

| Method | Query Type | Purpose |
|--------|-----------|---------|
| findByIdPaymentAsaas(String) | Named method | Find by Asaas payment ID |
| findAllByIdWakanderAndStatus(UUID, CobrancaStatus) | Named method | Get charges for wakander filtered by status |

**Query Complexity:** LOW (simple predicates)

---

#### [MissaoWakandaSpringDataJPARepository](src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/infra/MissaoWakandaSpringDataJPARepository.java)
**Extends:** JpaRepository<MissaoWakanda, UUID>  
**Entidade:** MissaoWakanda

| Method | Query Type | Purpose |
|-----------|-----------|---------|
| existsByTitulo(String) | Named method | Check existence |
| findAllByIdJornada(UUID) | Named method | All missions in journey (unordered) |
| findAllByIdJornadaOrderByOrdemMissaoAsc(UUID) | Named method | Ordered missions |
| findFirstByIdJornadaAndOrdemMissao(UUID, OrdemMissao) | Named method | Get next mission |
| findByIdMissaoExterna(String) | Named method | Find by external ID |
| findByIdJornadaAndMissaoStatus(UUID, MissaoStatus) | Named method | Missions with specific status |
| countByIdJornadaAndMissaoStatus(UUID, MissaoStatus) | Named method | Count by status |
| findByIdJornadaAndMissaoStatusOrderByOrdemMissaoAsc(UUID, status) | Named method | Ordered by status |
| findByIdJornadaAndMissaoStatusAndProcessamentoStatus(...) | Named method | Paged filter by 2 enums |
| findByIdMissaoIn(...) | @Query | Projection query for status check |

**Query Complexity:** MEDIUM (uses projections, enum filtering)

---

#### [ClasseWakandaSpringDataJpaRepository](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/classewakanda/infra/ClasseWakandaSpringDataJpaRepository.java)
**Extends:** JpaRepository<ClasseWakanda, UUID>  
**Entidade:** ClasseWakanda

| Method | Query Type | Purpose |
|--------|-----------|---------|
| existsByNome(String) | Named method | Check name uniqueness |
| existsByDescricao(String) | Named method | Check description uniqueness |
| existsByNivelNecessario(Integer) | Named method | Check level uniqueness |
| findMaxOrdemClasse() | @Query | Get highest order for ordering |
| findByStatus(ClasseWakandaStatus) | Named method | Active/Inactive filter |
| findProximaClassePorOrdem(Integer) | @Query + LIMIT | Find next class in hierarchy |

**Query Complexity:** LOW to MEDIUM (single table, order-based logic)

---

#### [XpWakanderSpringDataRepository](src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/infra/XpWakanderSpringDataRepository.java)
**Extends:** JpaRepository<XpWakander, UUID>  
**Entidade:** XpWakander

| Method | Query Type | Purpose |
|--------|-----------|---------|
| findByIdProgressoWakander(UUID) | Named method | Get XP record for progress |

**Query Complexity:** LOW (1:1 relationship)

---

#### [JornadaProgressoSpringDataRepository](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/jornadaprogresso/infra/JornadaProgressoSpringDataRepository.java)
**Extends:** JpaRepository<JornadaProgresso, UUID>

| Method | Query Type | Purpose |
|--------|-----------|---------|
| (inferred from infra) | - | Likely findByIdProgressoWakander, etc |

---

#### [MissaoProgressoSpringDataJpaRepository](src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/infra/MissaoProgressoSpringDataJpaRepository.java)
**Extends:** JpaRepository<MissaoProgresso, UUID>

| Method | Query Type | Purpose |
|--------|-----------|---------|
| (inferred) | - | Likely CRUD + findByIdProgressoWakander |

---

#### [AutenticacaoSpringDataJPARepository](src/main/java/academy/wakanda/wakanda_ai/autenticacao/infra/AutenticacaoSpringDataJPARepository.java)
**Extends:** JpaRepository<Autenticacao, String>  
**Note:** Uses String (token) as ID instead of UUID

| Method | Query Type | Purpose |
|--------|-----------|---------|
| (core CRUD) | - | findById(token), save(), delete() |

---

### Summary: Repository Pattern Usage

- **Total Repositories Found:** 20+ across contexts
- **Query Complexity:** LOW to MEDIUM (no native SQL found, all JPA/Hibernate)
- **Pagination:** Used in Wakander and Missao repositories
- **Custom Queries:** @Query annotations for complex filters (Wakander, Missao)
- **Projections:** MissaoWakandaProjection for partial data retrieval
- **Named Methods:** Heavy use of Spring Data naming convention (findBy*, existsBy*, countBy*)

---

## § 6 — Migration Quality Assessment

| Aspect | Status | Notes |
|--------|--------|-------|
| **Naming Convention** | ✓ GOOD | Consistent V{DATE}T{TIME}__ format |
| **Idempotence** | ✓ GOOD | Migrations are one-way transforms |
| **Rollback Support** | ✗ MISSING | No explicit DOWN scripts (Flyway limitation, not issue) |
| **Data Safety** | ⚠ CAUTION | Some ALTER COLUMN to nullable without defaults |
| **Constraint Coverage** | ⚠ PARTIAL | No CHECK constraints, some FKs have no explicit cascade rules |
| **Index Coverage** | ⚠ PARTIAL | Relies on implicit UNIQUE indexes; no explicit performance indexes |
| **Consistency** | ✗ ISSUE | V20250904 drops + recreates entire gamification schema (risky if data present) |
| **Documentation** | ✗ MISSING | No inline comments explaining migration purpose |
| **Test Coverage** | ? UNKNOWN | No flyway test migrations found |

---

## § 7 — Data Model Anomalies & Risks

### 1. **JornadaWakander Table vs JornadaWakanda Enum**
   - **Conflict:** Two separate concepts with similar names
   - `jornada_wakander` table: Tracks user journey progress
   - `jornada_wakanda` table + JornadaWakanda enum: Catalog of journey definitions
   - **Risk:** Developer confusion, potential data misalignment

### 2. **Sabedorias Duplication**
   - Sabedorias (catalogo/missaowakanda)
   - SabedoriasMissaoProgresso (progresso/missaoprogresso)
   - Identical structure (5 integer fields)
   - **Risk:** Maintenance burden, potential inconsistency

### 3. **Missing Check Constraints**
   - xpBase > 0 not enforced at DB level
   - Level > 0 not enforced
   - **Risk:** Invalid data possible via direct SQL updates

### 4. **Nullable Foreign Keys**
   - `cobranca.id_wakander` was initially nullable
   - **Risk:** Orphaned charges if not handled carefully

### 5. **Class Hierarchy Ordering**
   - `classe_wakanda.ordem_classe` is manually ordered (1, 2, 3...)
   - No database enforcement of sequential/unique ordering
   - **Risk:** If manually edited, could create gaps or duplicates

### 6. **ProcessamentoStatus Never Validated at DB Level**
   - Only checked in application code
   - Can stay EM_PROCESSO indefinitely if IA fails
   - **Risk:** Stale "in progress" missions

### 7. **Token PK as VARCHAR**
   - `autenticacao.token` is primary key
   - 64 character limit
   - **Risk:** If token generation changes to longer strings, migration required

### 8. **Large Unused Fields**
   - `conteudo_url` in missao_wakanda (could be moved to content service)
   - Multiple Discord fields scattered across wakander, onboarding
   - **Risk:** Schema bloat

---


