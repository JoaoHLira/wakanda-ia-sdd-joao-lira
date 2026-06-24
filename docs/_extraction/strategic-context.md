# Strategic Context — WakandaAI (extração consolidada)

> Material colado pelo Vis em 2026-05-23 + extração do `docs/Wakanda-AI.drawio` (8 páginas, 2024-09 a 2025-09) + referências aos arquivos já destilados em [`game-theory/dominios/wakanda/contexto/`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/).
>
> Este arquivo é **fonte intermediária** — alimenta `architecture-haiku.md` e `product-vision-board.md`. Sem prosa solta, sem duplicação com o que já existe em game-theory.

---

## 1. Product Vision Board v1 (gerado em sessão Pichler — material colado pelo Vis)

> Esta é a versão que o Vis trouxe pronta de uma sessão prévia (Roman Pichler + equipe simulada). Nota consolidada **8.8/10**. **Aceitar como baseline da v1** do Vision Board no projeto.

### Visão
Ajudar **aspirantes a programadores e programadores em início de carreira** a alcançar empregos bem remunerados em grandes empresas ou multinacionais, desenvolvendo competências práticas e know-how empresarial, e proporcionando suporte em processos seletivos por meio de uma plataforma integrada e inteligente.

### Grupo-alvo
- **Programadores atualmente empregados ganhando menos de R$ 4.000**, que sentem falta de oportunidades de crescimento.
- **Aspirantes a programadores** buscando ingressar no mercado de tecnologia com empregos que pagam bem.

### Necessidades
| # | Necessidade | Detalhe |
|---|-------------|---------|
| 1 | Entendimento do mercado | Compreender as demandas atuais do mercado de trabalho |
| 2 | Competência prática | Formações acadêmicas tradicionais não preparam para desafios reais |
| 3 | Experiência prática | Quebrar o paradoxo "sem emprego pq sem competência; sem competência pq sem emprego" |
| 4 | Posicionamento no mercado | Técnicas de entrevista + perfil LinkedIn que se destaca |
| 5 | Orientação e motivação | Suporte contínuo + motivação na jornada de aprendizado |

### Produto — 5 diferenciais
1. **Integração WhatsApp** — interação em tempo real; parabenização e incentivo via notificações automatizadas baseadas em desempenho
2. **Ambiente corporativo simulado** — vivências práticas em startups simuladas; dia-a-dia de engenheiro de software
3. **Automação dos processos de vivência** — escalabilidade na preparação e condução; tutoria por líderes treinados
4. **Condução prática via IA** — orientação automatizada da jornada de aprendizado, prática e posicionamento
5. **Suporte em processos seletivos** — análise/otimização de perfil LinkedIn + recomendações pra recrutadores + performance em entrevistas

### Metas de negócio (priorizadas)
1. **Aumentar receita** — escalar entrega a custo reduzido via automação
2. **Posicionar Wakanda como referência** — líder em formação prática para mercado de alto nível
3. **Adquirir conhecimento valioso** — dados de performance para melhorar continuamente
4. **Desenvolver a marca** — presença + comunidade engajada
5. **Expandir mercado** — atrair e reter programadores em início de carreira

### KPIs (já decididos pelo Vis)
- Taxa de conclusão do curso
- Taxa de sucesso em entrevistas (feedback dos alunos)
- Aumento salarial dos alunos pós-formação
- Número de conexões LinkedIn com recrutadores/empregadores
- Engajamento e feedback via WhatsApp

### Decisões técnicas declaradas na sessão Pichler
| Tema | Decisão |
|------|---------|
| Integrações externas | APIs robustas + DevOps + testes recorrentes de integração |
| IA robusta e escalável | ML treinado com dados de interações; NLP; cloud computing; monitoramento contínuo |
| Personalização WhatsApp | Dados comportamentais + recomendação + multimodal (texto/vídeo/áudio) + timing |
| Marketing | Anúncios segmentados LinkedIn/Facebook; influenciadores tech; conteúdo (blog/webinar/podcast); indicação |

### Notas da equipe (round 2)
| Persona | Nota | Ponto principal |
|---------|:---:|-----------------|
| Product Software Manager | 9 | Métricas robustas |
| Product Owner | 9 | Métricas específicas pra medir impacto |
| Tech Lead Especialista | 8 | Validar modelos de IA com experimentação prática |
| Arquiteto de Software | 9 | Manutenção de integrações com externos é clara |
| UX/UI Writer | 9 | Personalização via WA aumenta engajamento |
| Marketing/Growth | 9 | Estratégia ancorada em métodos comprovados |
| **Roman Pichler (consolidado)** | **8.8** | — |

### Perguntas abertas que ficaram da sessão Pichler (a responder no nosso loop)
1. Que testes e experimentações validarão IA antes da implementação completa?
2. Como garantir que integrações continuam evoluindo com mudanças de API/políticas externas?
3. Quais KPIs específicos para campanhas de marketing e engajamento?
4. O que mais pode incentivar networking dentro da plataforma?
5. Como abordar segurança e privacidade especialmente na integração LinkedIn?

---

## 2. Modelagem Estratégica Fase 5 (Conceitual) — gerada em sessão DDD prévia

> O Vis trouxe o output completo. Cobre os 3 subdomínios principais. **Aceitar como tese arquitetural-alvo**, a comparar contra o código atual (que é Fase 1/MVP).

### Subdomínio 1 — ✨ Catálogo Wakanda (Suporte)
> Estrutura oficial do jogo: trilhas, jornadas, missões e tipos. Define pré-requisitos.

| Agregado (raiz) | Entidades | VOs | Eventos |
|-----------------|-----------|-----|---------|
| **TrilhaWakanda** | Lista de JornadaWakanda | Nome, Descrição, XP total | TrilhaCriada, JornadaAdicionada, TrilhaPublicada |
| **JornadaWakanda** | Lista de MissaoWakanda | OrdemDasMissoes, Requisitos | JornadaCriada, MissaoAdicionada, JornadaAtivada |
| **MissaoWakanda** | TipoDeMissao | XPBase, SabedoriasEnvolvidas | TipoDefinido, MissaoDesativada, XPAtualizado |
| **TipoDeMissao** | — | Nome, Categoria, SabedoriasEnvolvidas | TipoCriado, SabedoriasDefinidas, XPBaseDefinido |

**Serviços de domínio:**
- `GerenciadorDeJornadas` — controla início, liberação e publicação
- `AtribuidorDeMissao` — define quais missões estão disponíveis com base nas regras do catálogo

### Subdomínio 2 — 🔹 Progresso Wakander (Core)
> Avanço do Wakander sobre os elementos do Catálogo. Histórico e estado.

| Agregado (raiz) | Entidades | VOs | Eventos |
|-----------------|-----------|-----|---------|
| **WakanderProgress** | JornadaDoWakander, MissaoProgress | XPDistribuido, ClasseAtual, NivelAtual | ProgressoRegistrado, XPDistribuido, NivelSubido, ClasseVerificada |
| **JornadaDoWakander** | MissaoProgress[] | Status, OrdemDasMissoes | JornadaIniciada, ProximaMissaoLiberada, JornadaConcluida |
| **MissaoProgress** | — | Status, XPObtido, Tentativas | MissaoIniciada, MissaoConcluida, MissaoReexecutada |

**Serviços de domínio:**
- `DistribuidorDeXP` — aplica XP após conclusão de MissaoProgress
- `ValidadorDeMissaoEspontanea` — avalia missões externas e converte em MissaoProgress válidas

### Subdomínio 3 — 🌟 XP Wakander (Core)
> Evolução de experiência, regras de promoção de classe, liberação de conteúdo. Escuta eventos de XPObtido.

| Agregado (raiz) | Entidades | VOs | Eventos |
|-----------------|-----------|-----|---------|
| **ClasseWakander** | RequisitosClasse | SabedoriasNecessarias, MissoesNecessarias | ClasseVerificada, ClasseSubida |
| **LiberacaoMissao** | RegrasDeAcesso | ClasseMinima, JornadaObrigatoria, XPMinimoPorTipo | MissaoLiberada, AcessoValidado, MissaoBloqueada |
| **SabedoriaXP** | XPPorSabedoria | TotalPorTipoDeSabedoria | XPObtido, SabedoriaAtualizada, XPRegistrado |

**Serviços de domínio:**
- `RegrasDeClasseService` — verifica subida de classe, liberação de missão
- `GerenciadorDeSabedoriaXP` — consome eventos de XPObtido, atualiza sabedoria, avalia promoção

### Observações estratégicas (declaradas)
- Catálogo é mantido por **Game Masters**, pode evoluir com base em uso real
- Missões Espontâneas, depois de validadas, **viram MissaoProgress ligadas a TipoDeMissao reutilizável**
- Subida de classe depende de: XP por tipo de sabedoria + missões concluídas + jornadas completas
- Cada **TipoDeMissao tem serviço orquestrador próprio**, com **fallback genérico** para tipos não previstos

---

## 3. Decisões adicionais respondidas pelo Vis (Bloco DDD)

### Catálogo vs WakanderProgress
- **Catálogo é gerenciado pelos Game Masters** — input pra inserção não é crítico, mas o "espaço de gestão" precisa estar visível
- **TipoDeMissao evolui** — tipos podem ser cadastrados e desabilitados ao longo do tempo. Ex: `Tipo: AulaTeorica` → `Missão concreta: "Aula Java Introdução Lógica"`
- **Missões espontâneas podem NÃO ter vínculo inicial com catálogo**, mas **depois de validadas viram MissaoProgress ligada a um TipoDeMissao** (regra: toda missão validada se reconecta ao catálogo)

### Agregados por subdomínio
- **JornadaDoWakander tem lista de referências a MissaoProgress.** Não faz sentido salvar MissaoProgress fora do contexto de WakanderProgress como um todo — provavelmente são **agregados aninhados**, não independentes (mas Vis não tem certeza; aplicar princípios DDD)
- **Múltiplas JornadasAtivas simultâneas são permitidas** — ex: Wakander na "Jornada da Conquista" (entrevista, processo seletivo) + simultaneamente na "Jornada de Clã" (design+dev em sprint)

### Serviços por tipo de missão
- **Cada TipoDeMissao tem serviço de domínio dedicado**
- **Serviço genérico** funciona como fallback para tipos não previstos — todos os serviços específicos **implementam uma interface comum**
- **Missão espontânea validada → vira MissaoProgress normal** (ligada a um TipoDeMissao do catálogo)

---

## 4. Arquitetura proposta na época — extraída de `docs/Wakanda-AI.drawio` (8 páginas)

Diagramas C4 + DDD criados entre **set/2024 e set/2025** (data drawio: 5 set 2025, 182 KB).

### Página 1 — Wakanda-AI - Domain
Domínio inicial focado em 3 contextos:
- **Contexto Financeiro Wakander** — agregados Cobrança + Wakander
  - `Wakander` (entity): idWakander, nome, cpf, wakanderContact, wakanderFinanceiro, ultimaAulaAssistida, idMemberkit. Métodos: matricula, negativaFinanceiro, regularizaFinanceiro
  - `Cobrança` (entity): idCobranca, valorCobranca, dataVencimento, dataPagamento, statusCobranca. Métodos: criaCobranca, pagaCobranca
  - `WakanderContact` (VO): whatsapp, email + updateContact
  - `WakanderFinanceiro` (VO): statusFinanceiro:enum + isRegular/isNegativado
  - `UltimaAulaAssistida` (VO): dataTimeUltimaAulaAssistida, idAulaAssistida
- **Contexto Jornada Wakander**
  - `AulaAssistida` (entity): idAulaAssistida, idAula, idCurso, idWakander, dataTimeConclusao, statusAula + registraAula
- **Contexto Wakander Comunicação**
  - `MensagemEnviada` (entity): idMensagemEnviada, idWakander, whatsapp, dataTimeSolicitacaoEnvio, mensagem + criaSolicitacaoMensagem

**Lista de problemas declarados nesta página:** matrícula, ativação/negativação financeira por pagamento, registro de aula assistida, checagem 1ª aula do dia, parabenizar quem estudou, checar quem não estudou, enviar mensagem motivacional.

### Página 2 — Wakanda-AI - Game - Domain
Mesma estrutura da página 1 — provavelmente snapshot intermediário. Sem entidades de gamificação ainda (XP, Classe, Trilha, Jornada).

> **GAP arquitetural detectado:** O drawio NÃO contém modelagem das entidades de gamificação (TrilhaWakanda, JornadaWakanda, MissaoWakanda, XpWakander, ClasseWakander). A Modelagem Fase 5 (seção 2 deste doc) ficou só em texto, não chegou ao drawio. **Isso indica que a Fase 5 é tese, não implementação.**

### Página 3 — L2 Containers (sistema completo)
- **Atores:** Wakander, Wakanda-AI Time (mentores)
- **Componentes externos:** Whatsapp, Z-API, Make, MemberKit, Asaas
- **Sistema:** `wakanda-ai-ms` (Microservice Java Spring Boot) + `wakanda-ai-db` (PostgreSQL RDS AWS) + `Whatsapp - Mensagens` (SNS/SQS)
- **Fluxos:** Mentores gerenciam Wakanders → Wakanda-AI Time → wakanda-ai-ms; Wakander assiste aulas no MemberKit → notifica wakanda-ai-ms; MemberKit/Asaas integram via JSON/HTTP

### Página 4 — L2 "Não estudou" (fluxo específico)
Mesma topologia da pág 3 + componente "Wakanders Que Não Estudaram" → "Notifica Quem Não Estudou" → "Consome Wakander Que Não Estudou"

### Página 5 — L3 "Não estudou" (componentes detalhados)
Mostra a microarquitetura interna do wakanda-ai-ms:
- `WakanderRepository` (Spring Repository)
- `WakanderIncentivosService` (Spring Service)
- `WakanderDesincentivados Scheduller` (todo dia 08h dispara fluxo)
- `WakanderInatividadePublisher` → publica `wakander-inatividade` SNS topic (`JSON/SNS`)
- `WakanderInatividadeConsumer` → consome
- `WhatsappMenssagemPublisher` + `WhatsappMenssagemConsumer` (SQS)
- `ZApiIntegrator` (Spring Component — JSON/HTTP)

Payload exemplo: `{"idWakander": "b342a2c1-...", "dataUltimaAula": "2024-10-21T10:35:00", "dataConsulta": "2024-10-23T08:00:00"}`

### Página 6 — Wakanda AI - MVP - L2 Containers
Versão simplificada do MVP — sem o fluxo "não estudou". Topologia core: Wakander + Time + wakanda-ai-ms + wakanda-ai-db + Whatsapp/Z-API + MemberKit.

### Páginas 7-8 — Financeiro 2025-01 (L2 + L3)
- **L2:** Wakanda-AI Time → wakanda-ai-ms ← Asaas (`JSON/HTTP`). Asaas notifica status de pagamento de Wakanders
- **L3 Components:**
  - `CobrancaAsaasAPI` (Spring Rest Controller)
  - `CobrancaAsaasStrategy` (Spring Component) — "Decide quem irá tratar evento de cobrança dado as informações no evento"
  - `NegativadorWakander` + `PagadorCobrança` (Spring Components)
  - `CobrançaService` (Spring Domain Service)
  - `CobrançaRepository` + `WakanderRepository` (Spring Repository)
  - Faz integração com `Make` (automation tool)

**Conclusões da arquitetura drawio:**
- **Stack era declarada Java Spring + PostgreSQL desde o início.** A "decisão MongoDB" mencionada nas memórias de mar/2025 (após o drawio começar) **foi tese reversa que não chegou na arquitetura visualizada** → consolidação Postgres é decisão de fato.
- **Strategy Pattern já estava no design** (`CobrancaAsaasStrategy`) → o padrão Processador existente no código é evolução natural.
- **SNS/SQS já era escolha desde 2024** → fundação de mensageria não vai mudar.

---

## 5. Documentos do Google Drive identificados (a buscar quando relevante)

> Vis compartilhou screenshots da pasta `Compartilhados comigo > Wakanda > Wakanda-AI` e subpasta `Up Stream`. Datas mostram histórico desde set/2024.

### Pasta `Wakanda > Wakanda-AI`
| Documento | Tipo | Última mod | Observação |
|-----------|------|------------|------------|
| **Wakanda-AI - Vision Board** | Google Docs | **25 set 2024** | Origem do Vision Board ⚠️ buscar conteúdo |
| Tutorial: Importação em Massa de Wakanders para SQL | Docs | 16 jan 2025 | Operacional — provável onboarding manual |
| Wakanda-AI - Squad Conhecimento | Docs | 12 mar 2025 | Estrutura organizacional interna |
| Wakanda-AI - Squad Staff | Docs | 3 abr 2025 | Estrutura organizacional interna |
| Narrador - Wakanda AI | Docs | 12 abr 2025 | Possível tom/narrativa de produto |
| Wakanda AI | Sheet | 15 abr 2025 | Planilha — KPIs? Tracking? |

### Pasta `Wakanda > Wakanda-AI > Up Stream`
| Documento | Tipo | Última mod | Observação |
|-----------|------|------------|------------|
| **Wakanda-AI.drawio** | drawio | 5 set 2025 | **Já em `docs/`** — extraído seção 4 |
| **Modelagem Wakanda - AI - Game** | Docs | **22 jul 2025** | Provável fonte da Modelagem Fase 5 ⚠️ buscar |
| Linkedin - WakaTech | Docs | 19 mai 2025 | Talvez perfil pra rebranding ou pessoa |
| João Lira - Linkedin - WakaTech | Docs | 19 mai 2025 | João Lira (mencionado nos players) — perfil pessoal |

### Subpasta `CRM`
- Modificado em 3 nov 2024 — pasta operacional (Clint integration?)

**Recomendação:** Vis pode baixar os 2 marcados ⚠️ (Vision Board original + Modelagem Game) e colocar em `docs/_extraction/external/` pra incorporarmos ao contexto sem depender do Drive.

---

## 6. Cruzamento com `game-theory/dominios/wakanda/contexto/` (não duplicar)

Material que **já está bem destilado** nas memórias do game-theory — referenciar, não copiar:

| Tema | Onde está | O que está coberto |
|------|-----------|---------------------|
| 5 Sabedorias (Fleury & Fleury) | [`contexto/12-gamificacao.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/12-gamificacao.md) | Taxonomia completa + mecânica de XP parcial + hierarquia Wakandana |
| Jornadas (Clareza/Conhecimento/Habilidade/Conquista) | [`contexto/07-10-jornada-*.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/) | 4 jornadas operacionais detalhadas |
| Players internos (Jefferson, Esténio, Lua, João Lira, Jusci) | [`contexto/14-players-internos.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/14-players-internos.md) | Papéis + capacidade |
| Plano de Automação AI 2026 | [`contexto/13-plano-automacao-ai.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/13-plano-automacao-ai.md) | Filosofia + estrutura técnica + riscos LGPD |
| Síntese Wakanda 2025 (3 fases produto) | [`contexto/AUTOMACAO-AI-PLANO.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/AUTOMACAO-AI-PLANO.md) | Tabela manual→AI |
| Posicionamento "não nos levamos tão a sério" | [`contexto/INDEX.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/contexto/INDEX.md) §"Resumo Executivo" | Tom + narrativa Wakanda |
| Laboratório Fleury+AI (pivot abr/2026) | [`jogos/laboratorio-fleury-ai/laboratorio-fleury-ai.md`](/Users/visreis/workspace/pessoal/game-theory/dominios/wakanda/jogos/laboratorio-fleury-ai/) | Estado atual do jogo principal |

---

## 7. Tese arquitetural vs Realidade do código — gap explícito

Comparação direta entre o que foi planejado (seções 2 + 4) e o que está implementado hoje (`docs/arquitetura-gameficacao.md`):

| Elemento da tese | Status no código | Veredito |
|-----------------|-------------------|----------|
| `TrilhaWakanda` agregado raiz com lista de Jornadas | Implementado (`gameficacao/catalogo/trilhawakanda/`) | ✅ |
| `JornadaWakanda` com ordem + requisitos | Implementado, mas pré-requisitos limitados | ⚠️ parcial |
| `MissaoWakanda` com xpBase + sabedorias | Implementado | ✅ |
| **`TipoDeMissao` com serviço dedicado por tipo + fallback** | Implementado como **tabela-enum sem polimorfismo** | ❌ **GAP CENTRAL** |
| `GerenciadorDeJornadas` (controle de início/liberação/publicação) | Lógica espalhada em `MissaoProgressoApplicationService` | ⚠️ parcial |
| `AtribuidorDeMissao` (disponibilidade) | `MissaoDisponibilidadeService` existe | ✅ |
| `WakanderProgress` agregado raiz contendo Jornadas + Missões | Implementado como agregados separados conectados por UUID | ⚠️ "separados por opção", não pela tese |
| **Múltiplas JornadasAtivas simultâneas** | A confirmar no código (Wakander tem `jornadaAtual` singular?) | ⚠️ **investigar** |
| `MissaoProgress` com tentativas + XP parcial | Status existe; XP parcial não validado | ⚠️ parcial |
| `DistribuidorDeXP` (após MissaoProgress conclusa) | Implementado via SNS xp-wakander | ✅ |
| `ValidadorDeMissaoEspontanea` (espontânea → catálogo) | **Não implementado** | ❌ Fase 2+ |
| `ClasseWakander` com `RequisitosClasse` (Sabedorias + Missões) | `ClasseWakanda.missoesNecessarias` existe; validação Sabedorias parcial | ⚠️ parcial |
| `LiberacaoMissao` com `RegrasDeAcesso` (ClasseMinima + XPMinimoPorTipo) | `idClasseMinima` existe; `XPMinimoPorTipo` não | ⚠️ parcial |
| `SabedoriaXP` com total por tipo de sabedoria | `Sabedorias` VO existe; total por tipo no `XpWakander` | ✅ |
| `RegrasDeClasseService` (subida de classe, liberação) | `XpPromocaoClasseService` existe | ✅ |
| `GerenciadorDeSabedoriaXP` (consome XPObtido, atualiza, avalia promoção) | `XpWakanderApplicationService.processaXP` + Event listener | ✅ |
| **Game Masters interface** (gerenciar catálogo) | Tem APIs CRUD mas sem UI dedicada Game Master | ⚠️ painel administrativo? |

**Top 3 gaps centrais (em ordem de impacto):**
1. **`TipoDeMissao` sem polimorfismo** — bloqueia "cada tipo tem serviço dedicado + fallback"
2. **Sem `ValidadorDeMissaoEspontanea`** — bloqueia missões espontâneas (Clã + jornadas livres)
3. **`XPMinimoPorTipo` (LiberacaoMissao)** — bloqueia validação multidimensional pra promoção de classe

---

## 8. Mapping para Haiku + Vision Board

| Dimensão Haiku/VB | Cobertura agora |
|-------------------|-----------------|
| Visão long-term | ✅ Seção 1 (Vision Board) + game-theory/manutencao.md (Fleury+AI lab) |
| Grupo-alvo | ✅ Seção 1 + crescente mulheres/LGBTQIA+ (game-theory) |
| Necessidades JTBD | ✅ Seção 1 (5 necessidades validadas) |
| Diferenciais (3-5) | ✅ Seção 1 (5 diferenciais) |
| Metas negócio priorizadas | ✅ Seção 1 (5 metas) |
| KPIs | ✅ Seção 1 (5 KPIs) |
| Objetivos negócio | ✅ Combinação seção 1 + game-theory/INDEX |
| Restrições | ⚠️ Parcial — capacidade Vis (5h/sem) ok; **limites técnicos a fechar** |
| Atributos qualidade priorizados | ❌ **Não decidido ainda** — pergunta crítica do Haiku v0.1 |
| Decisões arquiteturais (com reversões) | ✅ Seção 2 + 4 + arquitetura-gameficacao.md |
| "Alma Wakanda" (não-negociáveis) | ⚠️ Princípio declarado; **operacionalização pendente** (onde mentor é intocável) |
| Modelo de monetização / unit economics | ❌ **NÃO COBERTO** — sem dados sobre preço, ticket, CAC, LTV |
| Anti-fraude / LGPD operacional | ⚠️ Mencionado como risco; **políticas concretas pendentes** |

---

## 9. Lacunas explícitas para o Vis preencher

Pra fechar Haiku + Vision Board sem chutar, preciso de respostas em 3 lacunas:

### L1 — Atributos de Qualidade priorizados
Vis confirma a hipótese da v0.1 do Haiku?
**Extensibilidade > Mantenibilidade > Disponibilidade > Observabilidade > Segurança > Performance > Escalabilidade**
Ou inverte algo? (Ex: Segurança sobe pra top-3 pela LGPD? Disponibilidade desce porque mentor humano cobre indisponibilidade?)

### L2 — Modelo de monetização e unit economics
- Wakanda cobra mensalidade? Pacote fechado? Híbrido?
- Ticket médio?
- CAC vs LTV (mesmo aproximado)?
- Receita anual atual (faixa)?
- Esse tópico **define se "reduzir custo via AI" é existencial ou tático**.

### L3 — Linha vermelha "alma Wakanda"
Quais touchpoints do Wakander **NÃO podem** ser automatizados nem em 2027?
- Jornada da Clareza (coaching inicial)?
- Aprovação de promoção de classe?
- Conversa quando Wakander está travado emocionalmente?
- Reunião 1:1 do mentor?
- Outros?

(Essas viram restrições explícitas no Haiku.)

---

## 10. Próximos passos derivados

1. **Vis responde L1, L2, L3** acima (em qualquer formato) → desbloqueia v0.2 do Haiku
2. **Vis confirma Vision Board v1** (seção 1) como baseline aceitável pro projeto, ou pede ajustes
3. Quando ambos validados:
   - `docs/architecture-haiku.md` v0.2 gerado
   - `docs/product-vision-board.md` v1 gerado (importando seção 1)
   - `docs/arquitetura-gameficacao.md` § 11 atualizado com mapeamento gap → atributo de qualidade
   - `CLAUDE.md` ganha nova seção "Norte Estratégico" linkando ambos
4. **Opcional:** Vis baixa "Modelagem Wakanda - AI - Game" e "Vision Board original 2024-09" do Drive pra confirmar se há mais detalhe não capturado aqui

---

## Apêndice — Inventário JSON ChatGPT (status)

⚠️ Tentativa anterior de inventariar `chatgpt_pessoal_full_export_2026-04-08.json` (4.6MB) + `chatgpt_enterprise_COMPLETE_2026-04-08.json` (43MB) falhou por API timeout. **Provavelmente desnecessário rodar de novo** — o material que o Vis colou + drawio + game-theory/contexto cobrem ~90% das dimensões.

**Quando rodar:** se a v0.2 do Haiku tiver lacuna específica que provavelmente está só no JSON (ex: conversa específica sobre LGPD, ou modelo de monetização detalhado).

Estratégia revisada para reativar inventário:
- Não tentar carregar JSON inteiro
- Usar `jq --stream` ou grep direto por keywords muito específicas
- Filtrar primeiro por timeframe set/2024-abr/2026
- Limitar a Top 20 conversas (não tabelar todas)
