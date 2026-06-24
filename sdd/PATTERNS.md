# Code Patterns — WakandaAI

> **Versão:** v2 (review anki-coach — AI Agent pattern 2026-06-01) · **Data:** 2026-06-01
> **Status:** Padrões existentes extraídos do código real + padrões a canonizar (alinhados com Vis) + convenções de código executáveis + P21 (AI Agent) e P22 (APIException no domínio) adicionados após o review de design do anki-coach
> **Como usar:** Toda nova feature deve seguir as 22 seções de "Padrões Obrigatórios" e evitar as 12 de "Anti-patterns". Exemplos canônicos abaixo foram extraídos diretamente do código atual.

---

## Sumário

1. [Padrões Obrigatórios](#padrões-obrigatórios)
   - **Arquiteturais (P1–P11)**
   - P1. Repository Port/Adapter (Hexagonal)
   - P2. Strategy Pattern (Processador)
   - P3. Event-driven SNS/SQS FIFO
   - P4. Domínio Rico
   - P5. Construção via construtor (factory como exceção)
   - P6. Agregação por UUID
   - P7. **Idempotência em Consumers SQS** *(a canonizar)*
   - P8. **Observabilidade estruturada** *(a canonizar)*
   - P9. **Error Handling + Retry** *(a canonizar)*
   - P10. **Estrutura de Testes (pirâmide)** *(a canonizar)*
   - P11. **`@Transactional` no nível da classe em ApplicationServices** *(a canonizar — hoje inconsistente)*
   - **Convenções de código executáveis (P12–P20) — adicionadas 2026-05-28**
   - P12. Logs estruturados `[start]`/`[finish]` em todo método público
   - P13. Tamanho de método — meta 5 linhas de lógica, hard-cap 10
   - P14. Documentação Swagger via `*APIDocs.@interface` (não inline)
   - P15. Validação extraída em método privado nomeado
   - P16. Resposta HTTP via `@ResponseStatus` (não `ResponseEntity`)
   - P17. `@WebMvcTest` obrigatório em controllers Thymeleaf/REST (cobertura mínima 99%)
   - P18. Logging LGPD — metadata-only (hash, tamanho, ID) para conteúdo PII
   - P19. Defense-in-depth em in-memory repositories (cap + TTL + scheduler + remoção imediata)
   - P20. `@Retryable`/`@Recover` testáveis só via integration test (Spring AOP)
   - **AI / LLM (P21–P22) — adicionadas 2026-06-01**
   - P21. AI Agent Port/Adapter (port `*AIAgent` por propósito + adapter na infra do subdomínio)
   - P22. `APIException` no domínio é permitida (decisão deliberada)
2. [Anti-patterns (NÃO FAÇA)](#anti-patterns-não-faça)
   - **Arquiteturais (A1–A6)**
   - A1. Cascata síncrona em `concluiMissao()`
   - A2. Spring `@EventListener` síncrono dentro da TX
   - A3. `@NotBlank`/`@NotNull` em entidades de domínio
   - A4. DTO externo vazando para application layer
   - A5. Cache manual com `HashMap`
   - A6. `deduplicationId = UUID.randomUUID()` sem idempotência no handler
   - **De código (A7–A12) — adicionadas 2026-05-28**
   - A7. `UnsupportedOperationException` (ou exceptions Java padrão) em adapters
   - A8. `var` em métodos de application/domain/api
   - A9. `ResponseEntity` em controllers
   - A10. Emoji em mensagens server-side (exceptions, logs, response bodies)
   - A11. Comentários inline `//` explicando passos
   - A12. Instanciação seguida de mutação externa (`new Entity(); entity.attr = xpto;`)
3. [Padrões de Extensão](#padrões-de-extensão)
4. [Referências](#referências)

---

## Padrões Obrigatórios

### P1. Repository Port/Adapter (Hexagonal)

**Quando usar:** sempre que precisar persistir ou consultar dados de uma entidade/agregado.

**Forma canônica:** **3 arquivos** por agregado:

1. **Port** (interface) em `application/service/` — retorna **domain objects**, sem JPA na assinatura
2. **Adapter** (`@Repository`) em `infra/` — implementa o port, faz log, mapeia exceptions
3. **Spring Data JPA** (interface) em `infra/` — usado **dentro** do adapter

**Exemplo canônico — `MissaoProgresso`:**

```java
// 1. PORT — application/service/MissaoProgressoRepository.java
public interface MissaoProgressoRepository {
    MissaoProgresso buscaMissaoProgresso(UUID idMissao, UUID idProgressoWakander);
    Optional<MissaoProgresso> buscaOptionalMissaoProgresso(UUID idMissao, UUID idProgressoWakander);
    MissaoProgresso salvaProgressoMissao(MissaoProgresso missaoProgresso);
    boolean existeMissaoProgresso(UUID idMissao, UUID idProgressoWakander);
    // ... NUNCA importa JpaRepository, Page, etc na assinatura
}

// 2. ADAPTER — infra/MissaoProgressoInfraRepository.java
@Repository
@RequiredArgsConstructor
@Log4j2
public class MissaoProgressoInfraRepository implements MissaoProgressoRepository {

    private final MissaoProgressoSpringDataJpaRepository missaoProgressoSpringDataJpaRepository;

    @Override
    public MissaoProgresso salvaProgressoMissao(MissaoProgresso missaoProgresso) {
        log.info("[start] MissaoProgressoInfraRepository - salvaMissao");
        try {
            missaoProgressoSpringDataJpaRepository.save(missaoProgresso);
        } catch (DataIntegrityViolationException ex) {
            throw APIException.build(HttpStatus.CONFLICT, "Missão Progresso já existe!");
        }
        log.debug("[finish] MissaoProgressoInfraRepository - salvaMissao");
        return missaoProgresso;
    }
    // ...
}

// 3. SPRING DATA — infra/MissaoProgressoSpringDataJpaRepository.java
public interface MissaoProgressoSpringDataJpaRepository extends JpaRepository<MissaoProgresso, UUID> {
    Optional<MissaoProgresso> findByMissaoAndProgressoWakander(UUID idMissao, UUID idProgressoWakander);
    // ... queries específicas
}
```

**Regras:**
- ApplicationServices injetam **somente** o port (`MissaoProgressoRepository`), nunca o `*SpringDataJpaRepository`
- Adapter encapsula tradução de exceptions JPA → exceptions de domínio (`DataIntegrityViolationException` → `APIException`)
- Logs no nível do adapter, não do domain ou do Spring Data

**Anti-pattern relacionado:** ver [A4](#a4-dto-externo-vazando-para-application-layer).

---

### P2. Strategy Pattern (Processador)

**Quando usar:** sempre que houver "N tipos de evento/comportamento" que precisam ser tratados de forma diferente, com previsão de novos tipos no futuro.

**Forma canônica:** interface `*Processor` ou `*Processador` + N implementações `@Component`. Service orquestrador injeta `List<XxxProcessor>` e seleciona com `.filter(...).findFirst()`.

**Assinatura padrão:**

```java
public interface XxxProcessor {
    boolean validaSeProcessa<Algo>(<TipoEvento> tipo);
    void processa<Algo>(<DtoEvento> dto);
}
```

**Exemplo canônico — `ComunicacaoProcessorWhatsapp`:**

```java
// INTERFACE — application/service/whatsapp/processadores/ComunicacaoProcessorWhatsapp.java
public interface ComunicacaoProcessorWhatsapp {
    boolean validaSeProcessaMensagem(ZApiEventype whatsappMessageDto);
    void processaEnvioDaMensagem(ZApiEventDto ZApiEventDto);
}

// IMPLEMENTAÇÃO — application/service/whatsapp/processadores/ComunicacaoProcessorNormal.java
@Log4j2
@Component
@RequiredArgsConstructor
public class ComunicacaoProcessorNormal implements ComunicacaoProcessorWhatsapp {

    private final ComunicacaoService comunicacaoService;

    @Override
    public boolean validaSeProcessaMensagem(ZApiEventype whatsappMessageDto) {
        return whatsappMessageDto.equals(ZApiEventype.NORMAL_MESSAGE);
    }

    @Override
    public void processaEnvioDaMensagem(ZApiEventDto event) {
        log.info("[start] ComunicacaoProcessorNormal - processaEnvioDaMensagem");
        comunicacaoService.enviaMensagemWhatsapp(
            new MensagemRequest(event.getWhatsapp(), event.getMensagem())
        );
        log.debug("[finish] ComunicacaoProcessorNormal - processaEnvioDaMensagem");
    }
}
```

**Service orquestrador:**

```java
@Service
@RequiredArgsConstructor
public class ComunicacaoWhatsappApplicationService {
    private final List<ComunicacaoProcessorWhatsapp> processadores;  // ← Spring injeta TODOS

    public void processaEvento(ZApiEventDto event) {
        processadores.stream()
            .filter(p -> p.validaSeProcessaMensagem(event.getType()))
            .findFirst()
            .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Tipo não suportado"))
            .processaEnvioDaMensagem(event);
    }
}
```

**Chains existentes no projeto (5):**
- WhatsApp: `ComunicacaoProcessorWhatsapp` (5 impls)
- Memberkit catálogo: `MemberKitCadastraNovoMembroProcessador` + outros
- Memberkit jornada: `AulaAssistidaProcessadorJornadaConhecimento`
- Discord: `ComunicacaoProcessorDiscord` (em `jornadawakander/`)
- Progresso: chains em `gameficacao/progresso/`

**Adicionar tipo novo = nova classe `@Component implements XxxProcessor`. Zero modificação em código existente.**

---

### P3. Event-driven SNS/SQS FIFO

**Quando usar:** comunicação entre módulos/contextos que NÃO devem rodar na mesma transação (ex: missão concluída → XP → classe → WhatsApp).

**Forma canônica:**
- Tópicos SNS: `*.fifo` com nome `<dominio>-requests-topic.fifo`
- Filas SQS: `*.fifo` com DLQ correspondente
- Consumer: `@SqsListener` em classe `*Consumer` ou `*ConsumerSqs` no `infra/`

**Exemplo canônico — `XpWakanderConsumer`:**

```java
@Log4j2
@RequiredArgsConstructor
@Component
public class XpWakanderConsumer {

    private final ObjectMapper objectMapper;
    private final XpWakanderService xpWakanderService;

    @SqsListener("${aws.queue.xp-wakander-requests}")
    public void consumeXpWakanderQueueMessage(SqsMessageDto sqsMessageDto) {
        log.info("[start] XpWakanderConsumer - consumeXpWakanderQueueMessage");
        log.debug("[received] Mensagem: {}", sqsMessageDto.getMessage());
        try {
            XpWakanderEventDTO xpWakanda = deserializesqsMessageContent(sqsMessageDto, XpWakanderEventDTO.class);
            xpWakanderService.processaXP(xpWakanda);
            // ⚠️ TODO: adicionar verificação de idempotência aqui (ver P7)
        } catch (JsonProcessingException e) {
            log.info("[error] Não foi possível mapear o json!");
            // ⚠️ TODO: tratamento adequado de erro (ver P9)
        }
        log.debug("[finish] XpWakanderConsumer - consumeXpWakanderQueueMessage");
    }
}
```

**Tópicos atuais (do CLAUDE.md):**
- `zapi-requests-topic.fifo`
- `memberkit-requests-topic.fifo`
- `asaas-requests-topic.fifo`
- `discord-requests-topic.fifo`
- `progresso-wakander-requests-topic.fifo`
- `xp-wakander-requests-topic.fifo`

---

### P4. Domínio Rico

**Quando usar:** SEMPRE. Lógica de negócio vive nas entidades, não nos services.

**Princípio:** services orquestram (chamam repositórios, despacham eventos, transacionam). Domain calcula, valida, decide.

**Exemplo canônico — `XpWakander`:**

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)  // ← @NoArgsConstructor PRIVATE força uso da factory neste caso (exceção P5: defaults complexos com fibonacci)
@AllArgsConstructor
@Entity
@Table(name = "xp_wakander")
public class XpWakander {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idXpWakander;
    private UUID idProgressoWakander;
    private int xpTotal;
    private int nivelAtual;
    private int xpProximoNivel;
    @Embedded
    private Sabedorias sabedorias;
    private LocalDateTime ultimaAtualizacao;

    public static XpWakander novoComDefaults(UUID idProgressoWakander) {  // ← factory
        XpWakander xp = new XpWakander();
        xp.idProgressoWakander = idProgressoWakander;
        xp.xpTotal = 0;
        xp.nivelAtual = 1;
        xp.xpProximoNivel = xp.fibonacciCalculaXpParaNivel(2);
        xp.sabedorias = new Sabedorias(0, 0, 0, 0, 0);
        xp.ultimaAtualizacao = LocalDateTime.now();
        return xp;
    }

    // ✅ LÓGICA DE NEGÓCIO no domínio
    public void adicionarXpEAtualizarNivel(int xpObtido, Sabedorias sabedorias) {
        adicionaXp(xpObtido);
        atualizaNivel();
        atualizaSabedoriasDoWakander(sabedorias);
    }

    public boolean validaXpSabedoriasProximaClasseAtingido(ClasseWakanda proximaClasse) {
        return this.sabedorias.getTeorico() >= proximaClasse.getSabedorias().getTeorico() &&
               this.sabedorias.getProcesso() >= proximaClasse.getSabedorias().getProcesso() &&
               this.sabedorias.getKnowHow() >= proximaClasse.getSabedorias().getKnowHow() &&
               this.sabedorias.getComportamental() >= proximaClasse.getSabedorias().getComportamental() &&
               this.sabedorias.getCriativo() >= proximaClasse.getSabedorias().getCriativo();
    }

    // ✅ helpers privados que protegem invariantes
    private boolean podeSubirDeNivel() { return xpTotal >= xpProximoNivel; }
    private void subirNivel() { /* mutação controlada */ }
}
```

**Regras:**
- Construtor padrão: **público** para agregados raiz, **package-private** para entidades/VOs internos (ver P5)
- Construtor `private` (`@NoArgsConstructor(access = PRIVATE)`) apenas quando exceção P5 aplica (factory `novoComDefaults`/`criar` justificada)
- Setters APENAS via métodos comportamentais (`adicionarXpEAtualizarNivel`), nunca `@Setter`
- Validação de invariante: regras de equilíbrio das 5 Sabedorias vivem em `validaXpSabedoriasProximaClasseAtingido`, não em `XpService`
- **Nunca** `new Entity(); entity.attr = xpto` em consumer — ver [A12](#a12-instanciação-seguida-de-mutação-externa-new-entity-entityattr--xpto)

**Anti-pattern relacionado:** lógica em service ([A3](#a3-notblanknotnull-em-entidades-de-domínio) e [A12](#a12-instanciação-seguida-de-mutação-externa-new-entity-entityattr--xpto)).

---

### P5. Construção via construtor (factory como exceção)

> **Reescrito em 2026-05-28 após auditoria empírica.** Investigação em 54 classes mostrou que 75% do projeto usa **construtor público direto**, não factory static. Factory aparece em ~5 classes onde se justifica (XpWakander, MissaoProgresso, OrdemMissao, JornadaProgresso, UsuarioAdm). P5 anterior dizia "Value Object com Factory Validadora" — inverso do real. Versão atual reflete o padrão dominante + critérios objetivos para a exceção.

**Quando usar:** sempre que precisar instanciar entidade ou Value Object com invariantes a proteger.

**Hierarquia de construtores (regra dominante):**

| Tipo | Construtor | Por quê |
|------|-----------|---------|
| **Agregado raiz** (ex: `Wakander`, `XpWakander`, `MissaoWakanda`, `Cobranca`) | **`public`** | Consumer (ApplicationService) precisa instanciar |
| **Entidade interna ao agregado** | **`package-private`** (default, sem modifier) | Só o agregado raiz cria, do mesmo pacote — inversão de controle |
| **VO interno ao agregado** | **`package-private`** | Idem |
| **VO standalone** (compartilhado entre agregados, ex: `Sabedorias`) | **`public`** | Múltiplos consumers em pacotes diferentes |
| **JPA `@NoArgsConstructor`** | sempre presente, `access = AccessLevel.PRIVATE` ou default | Hibernate precisa de construtor sem args via reflexão |

**Validação:** dentro do construtor, chamadas a métodos privados nomeados (`validaTextoNaoVazio`, `validaXyz`) — alinha com [P15](#p15-validação-extraída-em-método-privado-nomeado). NUNCA `@NotBlank`/`@NotNull` em entidade de domínio (ver [A3](#a3-notblanknotnull-em-entidades-de-domínio)).

**Exemplo canônico — agregado raiz com construtor público sobrecarregado:**

```java
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)  // ← JPA only
@Table(name = "wakander")
public class Wakander {

    @Id
    private UUID idWakander;
    private String nome;
    private String cpf;
    // ... outros campos

    public Wakander(WakanderNovoRequest wakanderNovo) {
        this.nome = wakanderNovo.getNome();
        this.cpf = wakanderNovo.getCpf();
        this.statusCadastro = StatusCadastro.COMPLETO;
        this.jornadaAtual = JornadaWakanda.JORNADA_CONHECIMENTO;
        this.contato = new WakanderContato(wakanderNovo);
        this.financeiro = new WakanderFinanceiro();
    }

    public Wakander(AssinaturaEvento assinaturaEvento, ClienteAsaasDto fiadorDto) {
        this.statusCadastro = StatusCadastro.INCOMPLETO;
        this.jornadaAtual = JornadaWakanda.ONBOARD;
        // ...
    }
}
```

→ Wakander tem 2 construtores públicos por contexto de criação (cadastro completo vs onboarding via assinatura). Cada construtor inicializa estado consistente. Internos (`WakanderContato`, `WakanderFinanceiro`) criados via `new` dentro do agregado.

**Exemplo canônico — agregado raiz com inicialização determinística:**

```java
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "jornada_progresso")
public class JornadaProgresso {

    @Id private UUID idJornadaProgresso;
    private UUID idJornadaWakanda;
    private UUID idProgressoWakander;
    private JornadaProgressoStatus status;
    private LocalDateTime dataInicio;

    public JornadaProgresso(UUID idJornadaWakanda, UUID idProgresso) {
        this.idJornadaWakanda = idJornadaWakanda;
        this.idProgressoWakander = idProgresso;
        this.status = JornadaProgressoStatus.EM_ANDAMENTO;
        this.dataInicio = LocalDateTime.now();
        this.ultimaAtualizacao = LocalDateTime.now();
    }
}
```

→ Construtor recebe os 2 IDs externos; campos derivados (`status`, `dataInicio`) inicializados internamente. **Sem setters externos**; consumer chama `new JornadaProgresso(idJornada, idProgresso)`.

**Factory como EXCEÇÃO — 3 critérios objetivos:**

| Critério | Quando aplica | Exemplo canônico |
|----------|---------------|-------------------|
| **(a) Defaults múltiplos complexos** | Construtor receberia muitos params calculados internamente | [XpWakander.novoComDefaults](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/domain/XpWakander.java#L35) — calcula `xpProximoNivel` via fibonacci, inicializa `Sabedorias(0,0,0,0,0)`, `xpTotal=0`, `nivelAtual=1` |
| **(b) Estado inicial determinístico** | Factory força status/estado de domínio que esconde regra | [MissaoProgresso.criarEmAndamento](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/domain/MissaoProgresso.java) — força `status = EM_ANDAMENTO` |
| **(c) Validação sobre tipo primitivo** | VO simples com regra de domínio sobre primitivo | [OrdemMissao.criar(int)](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/domain/OrdemMissao.java#L26) — valida `ordem >= 0` |

**Exemplo de factory exceção (OrdemMissao):**

```java
@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)  // ← força uso de criar() (exceção P5: validação sobre primitivo)
public class OrdemMissao {

    @Column(name = "ordem_missao", nullable = false)
    private int ordem;

    public static OrdemMissao criar(int ordem) {
        validaOrdemNaoNegativa(ordem);
        return new OrdemMissao(ordem);
    }

    private static void validaOrdemNaoNegativa(int ordem) {
        if (ordem < 0) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "A ordem da missão não pode ser negativa");
        }
    }
}
```

**Outros VOs no projeto:**
- `Sabedorias(int teorico, processos, empirico, social, cognitivo)` — VO standalone embedded em `XpWakander`. **Construtor público** (`@AllArgsConstructor`) — 5 inteiros sem regra adicional além de tipos
- `WakanderContato`, `WakanderFinanceiro`, `WakanderFiador`, `WakanderAulaAssistida` — VOs internos do `Wakander`. **Construtores públicos sobrecarregados** (recebem DTO no `Wakander`)

**Records package-private como entidades internas de agregado (variante encapsulada):**

Quando uma entidade interna ao agregado é simples o suficiente para ser `record`, declare **sem modifier de acesso** (= package-private). Resultado: o construtor canônico é package-private — só classes no mesmo pacote `<feature>/domain/` instanciam.

```java
// ✅ CERTO — record package-private; só o agregado raiz do mesmo pacote instancia
package academy.wakanda.wakanda_ai.<feature>.domain;

record ItemInterno(TipoItem tipo, String valor, LocalDateTime momento) {
}

// Consumer (ApplicationService) NUNCA faz:
// ItemInterno i = new ItemInterno(...);  ❌ compile error fora do pacote

// Consumer faz:
agregado.adicionaItem(valor);  // ✅ agregado raiz orquestra a criação do interno
```

**Anti-pattern evitado**: vazamento do tipo interno pro ApplicationService (que quebra encapsulamento DDD). Combina com P4 (Domínio Rico) e A12 (mutação externa).

> **Nota (2026-06-01):** o exemplo canônico anterior (`MensagemSessao`, interno de `SessaoIterativa`) foi **dissolvido** na refatoração do AI Agent ([P21](#p21-ai-agent-portadapter)) — `SessaoIterativa` passou a reusar `ChatHistory`/`MensagemChat` do kernel `ai/domain` em vez de um tipo interno duplicado. O **conceito** acima permanece válido para entidades internas genuinamente locais a um agregado.

**Regra absoluta:**
- **NÃO use `@NotBlank`/`@NotNull` em VOs/entidades** — valide no construtor (ou no método `criar()` quando exceção P5 aplica). Ver [A3](#a3-notblanknotnull-em-entidades-de-domínio)
- **NÃO faça** `new Entity(); entity.attr = xpto;` — sempre construtor recebendo tudo OU método comportamental do agregado. Ver [A12](#a12-instanciação-seguida-de-mutação-externa-new-entity-entityattr--xpto)
- **Agregado orquestra criação de internos**: ApplicationService chama `wakander.atualizaProgresso(jornada)`, não `wakander.setProgresso(jornada)`

---

### P6. Agregação por UUID

**Quando usar:** SEMPRE entre agregados diferentes. Nunca use `@OneToMany`/`@ManyToOne` cruzando bounded contexts.

**Forma canônica:** chave estrangeira é um `UUID` simples (não uma referência JPA).

**Exemplo:**

```java
@Entity
public class XpWakander {
    @Id
    private UUID idXpWakander;

    // ✅ referência a outro agregado é UUID, não @ManyToOne
    private UUID idProgressoWakander;

    // ❌ NÃO faça:
    // @ManyToOne(fetch = FetchType.LAZY)
    // private ProgressoWakander progressoWakander;
}
```

**Justificativa:**
- Permite que cada agregado evolua independentemente (esp. quando virarem microsserviços)
- Evita N+1 queries acidentais
- Força navegação explícita via repository (`progressoWakanderRepository.buscaPorId(idProgressoWakander)`)
- Compatível com Outbox Pattern e mensageria

**Único caso permitido:** `@OneToMany`/`@Embedded` **dentro do mesmo agregado** (ex: `Wakander` com VOs `Contato`, `Financeiro`).

---

### P7. Idempotência em Consumers SQS *(a canonizar — hoje inconsistente)*

**Problema atual:** consumers SQS críticos não verificam estado antes de processar. Se a mesma mensagem chega 2× (DLQ retry, redelivery), o efeito é duplicado (XP em dobro, classe promovida 2×).

**Forma canônica:**

```java
@SqsListener("${aws.queue.xp-wakander-requests}")
public void consumeXpWakanderQueueMessage(SqsMessageDto sqsMessageDto) {
    log.info("[start] XpWakanderConsumer - consumeXpWakanderQueueMessage");

    XpWakanderEventDTO event = deserialize(sqsMessageDto, XpWakanderEventDTO.class);

    // ✅ 1. PRIMEIRA COISA: idempotência por chave de negócio (não por messageId aleatório)
    if (xpWakanderService.jaProcessouEvento(event.getIdMissaoProgresso())) {
        log.info("[skip] Evento já processado para missaoProgresso={}", event.getIdMissaoProgresso());
        return;  // ✅ ack mensagem sem reprocessar
    }

    // ✅ 2. processa
    xpWakanderService.processaXP(event);

    // ✅ 3. registra processamento (ou usa flag no agregado: missaoProgresso.isXpAtribuido())
    log.debug("[finish] XpWakanderConsumer - consumeXpWakanderQueueMessage");
}
```

**Estratégias de idempotência (em ordem de preferência):**

1. **Flag no próprio agregado** — `missaoProgresso.isXpAtribuido()`, `cobranca.isProcessada()`. Mais simples, sem tabela extra.
2. **Tabela `processed_events(event_id PK, processed_at)`** — quando não couber flag no agregado.
3. **deduplicationId determinístico** no SNS publisher (`deduplicationId = idMissaoProgresso + "-XP"`). Combinado com `MessageGroupId = idWakander` para garantir ordem.

**❌ Anti-pattern:** `deduplicationId = UUID.randomUUID()` no publisher — anula a garantia FIFO. Ver [A6](#a6-deduplicationid--uuidrandomuuid-sem-idempotência-no-handler).

---

### P8. Observabilidade estruturada *(a canonizar — hoje só log básico + actuator)*

**Forma canônica:** 4 pilares.

**1. Logs estruturados JSON + MDC:**

```yaml
# logback-spring.xml
logging:
  pattern:
    console: '{"ts":"%d","level":"%p","correlationId":"%X{correlationId}","wakanderId":"%X{wakanderId}","msg":"%m"}%n'
```

```java
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        String correlationId = Optional.ofNullable(req.getHeader("X-Correlation-Id"))
            .orElse(UUID.randomUUID().toString());
        MDC.put("correlationId", correlationId);
        try { chain.doFilter(req, res); }
        finally { MDC.clear(); }
    }
}
```

**Propagar MDC em SQS consumers** — extrair `correlationId` do header da mensagem SQS.

**2. Métricas Micrometer:**

```java
@Service
@RequiredArgsConstructor
public class XpWakanderService {
    private final MeterRegistry meterRegistry;

    public void processaXP(XpWakanderEventDTO event) {
        // ...
        meterRegistry.counter("wakanda.xp.atribuido",
            "tipo_missao", event.getTipoMissao()).increment(event.getXpObtido());
    }
}
```

**Métricas-chave a publicar:**
- `wakanda.xp.atribuido` (counter, tag `tipo_missao`)
- `wakanda.classe.promovida` (counter, tag `classe_destino`)
- `wakanda.missao.concluida` (counter, tag `tipo_missao`)
- `wakanda.sqs.consumer.latency` (timer, tag `queue`)
- `wakanda.integracao.<provider>.latency` (timer, tag `provider=z-api|asaas|memberkit|discord`)

**3. Distributed tracing** (OpenTelemetry — a implementar):

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% em prod até estabilizar
  otlp:
    tracing:
      endpoint: ${OTLP_ENDPOINT}
```

Fluxo crítico a rastrear: `missão→XP→classe→WhatsApp` (4 hops).

**4. Health checks específicos:**

```java
@Component
public class AsaasHealthIndicator implements HealthIndicator {
    private final AsaasInfraClient asaasClient;

    @Override
    public Health health() {
        try {
            asaasClient.ping();
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
```

---

### P9. Error Handling + Retry *(a canonizar — hoje cada domínio resolve diferente)*

**Forma canônica:**

**1. Global `@RestControllerAdvice` com `ProblemDetail` (RFC 7807):**

```java
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ProblemDetail> handleApi(APIException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        pd.setProperty("correlationId", MDC.get("correlationId"));
        log.warn("[api-exception] {} - {}", ex.getStatus(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        // ...
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {
        log.error("[unexpected] erro não tratado", ex);
        return ResponseEntity.internalServerError()
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno"));
    }
}
```

**2. Retry com backoff em integrações externas (Resilience4j ou Spring Retry):**

```java
@Component
@RequiredArgsConstructor
public class ZApiInfraClient {

    private final RestClient restClient;

    @Retry(name = "zApi", fallbackMethod = "enviarMensagemFallback")
    @CircuitBreaker(name = "zApi", fallbackMethod = "enviarMensagemFallback")
    public void enviarMensagem(String numero, String texto) {
        // ...
    }

    private void enviarMensagemFallback(String numero, String texto, Exception ex) {
        log.error("[fallback] Z-API indisponível, enfileirando para retry. wakanderTel={}", numero);
        // empurra para DLQ ou tabela de retry diferida
    }
}
```

```yaml
# application.yml
resilience4j:
  retry:
    instances:
      zApi:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
  circuitbreaker:
    instances:
      zApi:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
```

**3. Hierarquia de exceptions de domínio:**

```java
public abstract class WakandaException extends RuntimeException {
    protected final HttpStatus status;
    // ...
}

public class APIException extends WakandaException { /* já existe — ver handler/APIException.java */ }
public class DominioInvariantException extends WakandaException { /* invariante violado */ }
public class IntegracaoExternaException extends WakandaException { /* Z-API, Asaas, etc */ }
```

---

### P10. Estrutura de Testes (pirâmide) *(a canonizar)*

**Forma canônica — pirâmide:**

```
       ▲
      ╱ ╲  Contract  (WireMock stubs verificáveis vs real provider)
     ╱───╲
    ╱     ╲ Integration (@SpringBootTest + LocalStack + WireMock)
   ╱───────╲
  ╱         ╲ Unit (Mockito, FixtureFactory)
 ─────────────
```

**Quando usar cada nível:**

| Nível | Ferramenta | Quando |
|-------|-----------|--------|
| **Unit** | JUnit5 + Mockito + FixtureFactory | Lógica de domínio pura (`XpWakander.adicionarXpEAtualizarNivel`), services com mocks de repository |
| **Integration** | `@SpringBootTest` + LocalStack + WireMock | Fluxo SQS end-to-end, fluxo HTTP webhook (Memberkit/Asaas/Z-API), persistência real Postgres |
| **Contract** | WireMock com verify() | Garantir que cliente HTTP envia o payload esperado para provider externo |

**Convenções:**

- `@MockBean` — quando precisar substituir COMPLETAMENTE um bean (ex: bloquear chamada real ao Z-API)
- `@SpyBean` — quando precisar **verificar** chamadas mas deixar lógica real rodar
- `Testcontainers` — quando comportamento JPA específico precisa de Postgres real (índices, triggers, JSONB)
- `LocalStack` — para SQS/SNS — já configurado em `docker-compose.dev.yml`
- `WireMock` — para HTTP externos — stubs em `src/test/resources/stub/<provider>/`

**Convenção obrigatória de testes (canonizada na revisão anki-coach 2026-06-08):**

- Testes unit com injeção de dependência **devem** usar `@ExtendWith(MockitoExtension.class)` + `@Mock` (e `@InjectMocks` quando **todos** os colaboradores são mocks) — sem `mock()` manual em `setup()`. Quando o teste mistura mock (a fronteira: agente/LLM, client externo) com colaboradores reais baratos (`SimpleMeterRegistry`, repositório in-memory, formatter), use `@Mock` no colaborador-fronteira e construa o SUT num `setup()` mínimo.
- **Todo** método de teste leva `@DisplayName` descritivo em português.
- Exemplo canônico: [`AnkiCoachInstantaneoApplicationServiceTest`](../src/test/java/academy/wakanda/wakanda_ai/anki/application/service/AnkiCoachInstantaneoApplicationServiceTest.java) (`@ExtendWith(MockitoExtension.class)` + `@Mock GeradorDeckAIAgent` + `@DisplayName`); integration com WireMock embarcado em porta dinâmica: [`OpenAiGeradorDeckAIAgentIntegrationTest`](../src/test/java/academy/wakanda/wakanda_ai/anki/infra/openai/OpenAiGeradorDeckAIAgentIntegrationTest.java).

**Exemplo unit canônico:**

```java
class XpWakanderTest {

    @Test
    void deveSubirDeNivelQuandoXpAcumuladoAtingirProximoNivel() {
        // given
        XpWakander xp = XpWakander.novoComDefaults(UUID.randomUUID());

        // when
        xp.adicionarXpEAtualizarNivel(35, new Sabedorias(10, 0, 0, 0, 0));

        // then
        assertThat(xp.getNivelAtual()).isEqualTo(2);
        assertThat(xp.getSabedorias().getTeorico()).isEqualTo(10);
    }

    @Test
    void naoDevePromoverClasseSeSabedoriasNaoEquilibradas() {
        XpWakander xp = XpWakander.novoComDefaults(UUID.randomUUID());
        xp.adicionarXpEAtualizarNivel(1000, new Sabedorias(1000, 0, 0, 0, 0));

        ClasseWakanda guerreiro = ClasseWakandaFixture.guerreiro();  // exige 100 em CADA sabedoria

        assertThat(xp.validaXpSabedoriasProximaClasseAtingido(guerreiro)).isFalse();
    }
}
```

**Metas:**
- Cobertura mínima 80% (JaCoCo) — atual ~21%, alvo gradual
- Critical paths (XP, promoção de classe, cobrança Asaas) → 95%+
- Testes E2E (Large) com LTP → fora do escopo atual, futuro

---

### P11. `@Transactional` no nível da classe em ApplicationServices *(a canonizar — hoje inconsistente)*

**Problema atual:** alguns `*ApplicationService` têm `@Transactional` no método, outros na classe, outros não têm — comportamento de transação varia, dificulta manutenção. (Gap #7 de `arquitetura-gameficacao.md §11`.)

**Forma canônica:**

```java
// ✅ CERTO — @Transactional no NÍVEL DA CLASSE para ApplicationServices que MUTAM estado
@Service
@Transactional                              // ← padrão (rollback em RuntimeException)
@RequiredArgsConstructor
public class XpWakanderApplicationService implements XpWakanderService {

    private final XpWakanderRepository xpWakanderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void processaXP(XpWakanderEventDTO event) {
        XpWakander xp = xpWakanderRepository.buscaPorProgresso(event.getIdProgressoWakander());
        xp.adicionarXpEAtualizarNivel(event.getXpObtido(), event.getSabedorias());
        xpWakanderRepository.salva(xp);
        // commit aqui ao final do método
    }

    @Transactional(readOnly = true)         // ← override apenas em métodos de leitura
    @Override
    public XpWakanderResponse busca(UUID idWakander) {
        return XpWakanderResponse.from(xpWakanderRepository.buscaPorWakander(idWakander));
    }
}
```

**Regras:**
- `@Transactional` na classe = todos os métodos public são transacionais por padrão
- `@Transactional(readOnly = true)` override em métodos de busca/listagem (otimização Hibernate flush)
- **NUNCA** `@Transactional` em domain entity ou em controller
- **NUNCA** `@Transactional` em método **`private`** (Spring AOP não intercepta — não funciona)
- Controllers NÃO têm `@Transactional` — application service é o boundary transacional
- Para reações pós-commit, use `@TransactionalEventListener(phase = AFTER_COMMIT)` em vez de `@EventListener` simples (ver A2)

**Cuidado:** mudar para `@Transactional` na classe pode QUEBRAR métodos que hoje rodam fora de TX por acidente (ex: chamada a integração externa que demora 30s segurando conexão DB). Migração deve ser feita ApplicationService por ApplicationService, com revisão de cada método.

---

### P12. Logs estruturados `[start]`/`[finish]` em todo método público

**Quando usar:** SEMPRE em métodos públicos de Controllers (`*Api`), ApplicationServices, Schedulers, Consumers, Adapters de integração externa. Em métodos privados que valem rastro (ex: chamada cara, branch crítico, evento publicado), também aplicar.

**Forma canônica:**

```java
@Log4j2
@Service
public class XpWakanderApplicationService {

    @Override
    public void processaXP(XpWakanderEventDTO xpWakander) {
        log.info("[start] XpWakanderApplicationService - processaXP");
        ProgressoWakander progressoWakander = progressoGameficacaoRepository.buscaProgressoPorId(xpWakander.getIdProgressoWakander());
        XpWakander wakander = obterOuCriarXpWakander(progressoWakander);
        wakander.adicionarXpEAtualizarNivel(xpWakander.getXpObtido(), xpWakander.getSabedorias());
        xpWakanderRepository.salva(wakander);
        publicaEventoPromocaoClasse(wakander);
        log.debug("[finish] XpWakanderApplicationService - processaXP");
    }
}
```

**Vocabulário de marcadores:**

| Marcador | Nível | Quando |
|----------|-------|--------|
| `[start]` | `info` | Primeira linha de método público (ou privado relevante) |
| `[finish]` | `debug` | Última linha antes do `return` ou fim do método |
| `[info]` | `info` | Evento de negócio relevante a operadores (ex: "Criando histórico inicial") |
| `[sending]` | `debug` | Antes de publicar evento ou chamar integração externa |
| `[received]` | `debug` | Após receber payload SQS/HTTP |
| `[skip]` | `info` | Branch que decidiu não processar (idempotência, validação) |
| `[error]` | `error` | Exception capturada e tratada (não relançada) |
| `[fallback]` | `error` | Circuit breaker / retry esgotado / fallback ativado |

**Formato fixo:** `"[marcador] NomeDaClasse - nomeDoMetodo"` (separador hífen, espaço entre nome e marcador). NÃO usar lambda, MDC manual ou interpolação inline além do que cabe — o marcador é a primeira coisa após o nível.

**Regras:**
- `log.info` no `[start]`, `log.debug` no `[finish]` — start em info pra ser visível em prod; finish em debug pra reduzir ruído
- Mesmo padrão em **todas as camadas**: API, Service, Scheduler, Adapter
- Em SQS Consumers, **propagar `correlationId`** vindo do header da mensagem (ver P8)
- **NUNCA logar PII em conteúdo** — ID, tamanho, hash sim; texto cru não

**Exemplos canônicos no código:**
- [XpWakanderApplicationService.java:44,51](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/application/service/XpWakanderApplicationService.java) (Service)
- [MissaoProgressoApi.java:30,32](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/application/api/MissaoProgressoApi.java) (Controller)
- [MissaoProgressoInfraRepository.java:69,75](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/infra/MissaoProgressoInfraRepository.java) (Adapter)

**Anti-pattern relacionado:** ver [A11](#a11-comentários-inline--explicando-passos) — logs descritivos substituem comentários inline.

---

### P13. Tamanho de método — meta 5 linhas de lógica, hard-cap 10

**Quando usar:** SEMPRE. Aplica em ApplicationServices, Adapters, Controllers, Domain methods comportamentais.

**Regra de contagem:**

```
linhas_de_lógica = total_de_linhas_do_corpo
                 - log.info("[start] ...")
                 - log.debug("[finish] ...")
                 - linha do return final
                 - linhas em branco
                 - chaves isoladas
```

- **Meta:** ≤ 5 linhas de lógica por método
- **Hard-cap:** ≤ 10 linhas de lógica — acima disso, **bloqueia code review**
- **Exceção tolerada:** orquestradores que apenas delegam para métodos privados nomeados (encadeando chamadas) podem chegar a 10 linhas se cada chamada for autocontida e descritiva

**Forma canônica — orquestrador delega:**

```java
@Override
public void processaPromocaoClasse(XpPromocaoClasseDTO dto) {
    log.info("[start] XpWakanderApplicationService - processaPromocaoClasse");
    HistoricoClasseWakander historicoAtual = historicoClasseWakanderRepository
            .buscaOptionalHistoricoClasseAtual(dto)
            .orElseGet(() -> criaHistoricoInicial(dto));
    ClasseWakanda classeAtual = classeWakandaRepository.buscaClassePorId(historicoAtual.getIdClasse());
    classeWakandaRepository.buscaOptionalProximaClassePorOrdem(classeAtual.getOrdemClasse())
            .ifPresentOrElse(
                proximaClasse -> validaEPromoveClasse(dto, historicoAtual, classeAtual, proximaClasse),
                () -> log.info("[info] Usuário já está na última classe, encerrando promoção.")
            );
    log.debug("[finish] XpWakanderApplicationService - processaPromocaoClasse");
}
```

→ 5 linhas de lógica (busca histórico, busca classe atual, branch promoção). Cada decisão complexa virou método privado nomeado.

**Refatoração quando passa de 10:** extrair para método privado com nome descritivo (`validaXyz`, `criaXyz`, `processaXyz`, `publicaXyz`). Nome do método substitui o comentário que ele teria. Aplicar P15 (validação extraída).

**Exemplos canônicos no código:**
- [XpWakanderApplicationService.java:43-52](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/application/service/XpWakanderApplicationService.java) — `processaXP`: 5 linhas de lógica
- [XpWakanderApplicationService.java:95-104](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/application/service/XpWakanderApplicationService.java) — `validaEPromoveClasse`: 5 linhas

**Como medir em code review:** contar visualmente; se duvida, é sinal de refatorar.

---

### P14. Documentação Swagger via `*APIDocs.@interface` (não inline)

**Quando usar:** SEMPRE em endpoints REST públicos ou internos que aparecem no Swagger UI.

**Forma canônica:** criar `src/main/java/academy/wakanda/wakanda_ai/docs/swagger/<Dominio>APIDocs.java` com `@interface` de nível superior contendo sub-`@interface` por método. Cada sub-`@interface` carrega `@Operation`, `@ApiResponses`, `@ExampleObject`. Controller decora endpoints com `@<Dominio>APIDocs.<NomeDoMetodo>`.

**Por quê:** mantém controller limpo e legível (foco em lógica HTTP + delegação), mantém documentação versionada em um único lugar por domínio, facilita revisão de contrato sem ler controller, padroniza ApiResponses entre métodos.

**Exemplo canônico — `MissaoProgressoAPIDocs` + `MissaoProgressoApi`:**

```java
// 1. DOCS — docs/swagger/MissaoProgressoAPIDocs.java
public @interface MissaoProgressoAPIDocs {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Criar progresso de missão",
               description = "Cria progresso quando missão liberada para classe atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Progresso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Missão não liberada",
                content = @Content(examples = @ExampleObject(value = "{ \"message\": \"Missão não liberada\" }"))),
            @ApiResponse(responseCode = "404", description = "Missão não encontrada"),
            @ApiResponse(responseCode = "409", description = "Progresso já existe"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @interface CriaMissaoProgresso { }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Concluir missão manualmente")
    @ApiResponses(value = { /* ... */ })
    @interface ConcluiMissaoManualmente { }
}

// 2. CONTROLLER — application/api/MissaoProgressoApi.java
@RestController
@RequestMapping("/gameficacao/missao-progresso")
@Tag(name = "MissaoProgressoApi", description = "Controle de progresso de missão")
public class MissaoProgressoApi {

    @MissaoProgressoAPIDocs.CriaMissaoProgresso              // ← uma anotação substitui ~10 linhas
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    MissaoProgressoResponse criaMissaoProgressoParaWakander(@RequestBody @Valid MissaoProgressoRequest request) {
        log.info("[start] MissaoProgressoApi - criaMissaoProgressoParaWakander");
        MissaoProgressoResponse resposta = missaoProgressoService.criaProgressoDeMissao(request);
        log.debug("[finish] MissaoProgressoApi - criaMissaoProgressoParaWakander");
        return resposta;
    }
}
```

**Regras:**
- Um `*APIDocs.java` por domínio (não por controller — domínio pode ter vários controllers)
- `@Tag` no controller, não no `*APIDocs`
- ApiResponses devem listar **todos** os status documentados — incluindo 4xx esperados de validação e 5xx genérico
- `@ExampleObject` realistas (não placeholders); facilita teste manual via Swagger UI

**Exemplos canônicos:**
- [MissaoProgressoAPIDocs.java:14-67](../src/main/java/academy/wakanda/wakanda_ai/docs/swagger/MissaoProgressoAPIDocs.java)
- [MissaoProgressoApi.java:26](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/application/api/MissaoProgressoApi.java) — uso da anotação

**Anti-pattern relacionado:** ver [A9](#a9-responseentity-em-controllers).

---

### P15. Validação extraída em método privado nomeado

**Quando usar:** SEMPRE que um construtor de domínio (ou factory `.criar()`/`.novoComDefaults()` quando exceção [P5](#p5-construção-via-construtor-factory-como-exceção) aplica) ou um ApplicationService precisar validar duas ou mais condições, ou uma condição que se repete.

**Forma canônica:** validação fica em método privado `private void valida<Algo>(<params>)` com nome descritivo, que lança `APIException.build(HttpStatus.X, "msg")` curta.

```java
public class MissaoProgresso {

    public void concluiMissao() {
        validaConclusaoMissao();
        this.status = MissaoStatus.CONCLUIDA;
        this.dataConclusao = LocalDateTime.now();
    }

    private void validaConclusaoMissao() {
        if (this.status == MissaoStatus.CONCLUIDA) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "A missão já está concluída.");
        }
    }
}
```

**Em VOs / construtor público (padrão dominante):**

```java
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transcricao {

    private static final int MAX_CHARS = 150_000;

    private String texto;
    private String hashSha256;

    public Transcricao(String texto) {
        validaTextoNaoVazio(texto);
        validaTamanhoMaximo(texto);
        this.texto = texto;
        this.hashSha256 = calcularHash(texto);
    }

    private static void validaTextoNaoVazio(String texto) {
        if (texto == null || texto.isBlank()) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Transcrição não pode ser vazia");
        }
    }

    private static void validaTamanhoMaximo(String texto) {
        if (texto.length() > MAX_CHARS) {
            throw APIException.build(HttpStatus.BAD_REQUEST,
                "Transcrição excede limite de " + MAX_CHARS + " caracteres");
        }
    }
}
```

**Regras:**
- Nome do método descreve **o que** é validado (`validaTextoNaoVazio`), não **como** (`validaTextoNaoNuloENaoBlank`)
- Uma validação = um método (Single Responsibility)
- Reutilização: validação que aparece em 2+ factories vira static util ou move para o próprio VO
- **Não usar `@NotBlank`/`@NotNull` em entidades de domínio** — ver [A3](#a3-notblanknotnull-em-entidades-de-domínio)

**Exemplos canônicos:**
- [MissaoProgresso.java:53-58](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/domain/MissaoProgresso.java) — `validaConclusaoMissao`
- [OrdemMissao.java:314-319](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/domain/OrdemMissao.java) — `validarNovaPosicao` delega a 3 privados

---

### P16. Resposta HTTP via `@ResponseStatus` (não `ResponseEntity`)

**Quando usar:** SEMPRE em controllers REST. **Banir `ResponseEntity`** do projeto.

**Forma canônica:** `@ResponseStatus(HttpStatus.X)` na assinatura do método + retorno direto do DTO (ou `void` para 204).

```java
@RestController
@RequestMapping("/gameficacao/missao-progresso")
public class MissaoProgressoApi {

    @MissaoProgressoAPIDocs.CriaMissaoProgresso
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)                            // ← status na assinatura
    MissaoProgressoResponse criaMissaoProgressoParaWakander(@RequestBody @Valid MissaoProgressoRequest request) {
        log.info("[start] MissaoProgressoApi - criaMissaoProgressoParaWakander");
        MissaoProgressoResponse resposta = missaoProgressoService.criaProgressoDeMissao(request);
        log.debug("[finish] MissaoProgressoApi - criaMissaoProgressoParaWakander");
        return resposta;                                            // ← DTO direto
    }

    @MissaoProgressoAPIDocs.ConcluiMissaoManualmente
    @PatchMapping("/{idMissaoProgresso}/conclui")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void concluiMissaoManualmente(@PathVariable UUID idMissaoProgresso) {
        log.info("[start] MissaoProgressoApi - concluiMissaoManualmente");
        missaoProgressoService.concluiMissaoManualmente(idMissaoProgresso);
        log.debug("[finish] MissaoProgressoApi - concluiMissaoManualmente");
    }
}
```

**Por quê:**
- Status fica na assinatura → grep por `@ResponseStatus(HttpStatus.CREATED)` mostra tudo que cria recursos
- Método retorna o DTO de domínio, não wrapper HTTP → testes assertem em payload direto
- Menos código por endpoint
- Erros viram `ProblemDetail` via `@RestControllerAdvice` global (ver P9), não inline

**Regra absoluta:** `ResponseEntity` em controller = **bloqueio em code review**. Exceção única: precisa de header HTTP customizado dinâmico (raro — geralmente cabe em `HttpServletResponse` injetado ou em filter).

**Exemplos canônicos:**
- [MissaoProgressoApi.java:28,38,47,59](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/application/api/MissaoProgressoApi.java) — 4 endpoints com `@ResponseStatus`
- [AutenticacaoApi.java:27-28](../src/main/java/academy/wakanda/wakanda_ai/autenticacao/application/api/AutenticacaoApi.java) — `@ResponseStatus(code = HttpStatus.CREATED)` + `void`

**Anti-pattern relacionado:** ver [A9](#a9-responseentity-em-controllers).

---

### P17. `@WebMvcTest` obrigatório em controllers (Thymeleaf e REST)

**Quando usar:** SEMPRE em controllers (`@Controller`, `@RestController`). Cobertura mínima de feature nova é **99%** — controllers entram nessa meta.

**Forma canônica:**

```java
@WebMvcTest(controllers = AnkiCoachApi.class)
@AutoConfigureMockMvc(addFilters = false)  // bypassa SecurityFilter global do projeto
class AnkiCoachApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private AnkiCoachInstantaneoService instantaneoService;
    @MockBean private TokenService tokenService;
    @MockBean private AutenticacaoRepository autenticacaoRepository;
    @MockBean private UsuarioAdmSpringDataJpaRepository usuarioAdmSpringDataJpaRepository;

    @Test
    void gerarInstantaneoDeveRetornar200ComCsv() throws Exception {
        UUID idRequisicao = UUID.randomUUID();
        when(instantaneoService.gerarDeck(any()))
            .thenReturn(new AnkiInstantaneoResponse("f;b\nf2;b2", 2, idRequisicao));

        String body = objectMapper.writeValueAsString(new AnkiInstantaneoRequest("aula"));

        mockMvc.perform(post("/anki/instantaneo/gerar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantidadeCards").value(2));
    }
}
```

**Regras:**

| Regra | Razão |
|-------|-------|
| `@WebMvcTest(controllers = X.class)` apontando o controller específico | Limita scope; reduz tempo de startup |
| `@AutoConfigureMockMvc(addFilters = false)` quando projeto tem SecurityFilter global | Evita 401 em rotas públicas; SecurityFilter precisa de `TokenService` que vira `@MockBean` |
| `@MockBean` (não `@MockitoBean`) em Spring Boot 3.3.x | `@MockitoBean` só existe Spring Boot 3.4+; usar `@MockBean` |
| Mockar dependências do filter global (`TokenService`, repositórios de autenticação) | Sem isso, `Failed to load ApplicationContext` por dependências não resolvidas |
| Para `@Controller` Thymeleaf, assertar `view().name("anki/instantaneo")` + `status().isOk()` | Valida que o GET retorna a view correta |
| Para `@RestController`, assertar `jsonPath`/`content().json(...)` por endpoint + status codes (200, 201, 400, 404, 410, 422, 503) | Cobertura completa do contrato HTTP |

**Exemplos canônicos:**
- [AnkiCoachWebControllerTest](../src/test/java/academy/wakanda/wakanda_ai/anki/application/web/AnkiCoachWebControllerTest.java) — Thymeleaf (3 testes, 3 GETs)
- [AnkiCoachApiTest](../src/test/java/academy/wakanda/wakanda_ai/anki/application/api/AnkiCoachApiTest.java) — REST (7 testes cobrindo 3 endpoints + cenários de erro)

**Anti-pattern evitado:** controllers sem teste passam por cobertura "fake" (via integration test indireto ou nenhum) — pode levar 200 → 500 em produção sem ninguém perceber.

---

### P18. Logging LGPD — metadata-only para conteúdo PII

**Quando usar:** SEMPRE que logar contexto envolvendo conteúdo de usuário (transcrição, mensagem de chat, dados pessoais, conteúdo de upload).

**Regra:** logs registram **apenas metadata** — `hash SHA-256`, `tamanho` em chars/bytes, `ID UUID`, `timestamp`, `tipo de operação`. **NUNCA** o conteúdo cru.

**Forma canônica:**

```java
// ✅ CERTO — só metadata
log.debug("Transcrição criada hash={} tamanho={}", transcricao.getHashSha256(), transcricao.tamanho());
log.info("[start] AnkiCoachInstantaneoApplicationService - gerarDeck");
log.info("[skip] Sessão já processada idSessao={}", sessao.getIdSessao());

// ✅ Métricas: tags com IDs/hashes, nunca emails/nomes/conteúdo
meterRegistry.counter("wakanda.anki.tokens.consumed",
    "variant", "instantaneo", "model", "gpt-4o-mini").increment(300);
```

**Anti-pattern (NÃO FAZER):**

```java
// ❌ ERRADO — vaza PII em log persistido (LGPD R7 violação)
log.debug("Transcrição recebida: {}", transcricao.getTexto());
log.info("Wakander {} respondeu: {}", wakander.getEmail(), mensagem);
log.error("Erro processando aula sobre {}", transcricao.getPrimeiros100Chars());
```

**Validação executável (grep automatizado):**

```bash
# Em code review (e idealmente em CI):
grep -rnE 'log\.\w+\([^)]*\.(getTexto|getConteudo|getMensagem|getEmail|getCpf|getNome)\(' src/main/java/
# Deve retornar VAZIO
```

**Exemplo canônico:** [AnkiCoachInstantaneoApplicationService.java:51](../src/main/java/academy/wakanda/wakanda_ai/anki/application/service/AnkiCoachInstantaneoApplicationService.java#L51) — `log.debug("Transcrição criada hash={} tamanho={}", ...)` (anki-coach T1.0)

**Regra correlata:** mensagens de `APIException` também não devem conter conteúdo do usuário — só descrições genéricas ("Transcrição inválida", "Sessão expirou"). Stack traces propagados ao cliente também precisam ser sanitizados.

**Exceção legítima:** logs **estruturados de auditoria** em sistemas com retenção controlada e cifragem em rest podem registrar conteúdo — mas não é o caso do projeto WakandaAI no MVP.

---

### P19. Defense-in-depth em in-memory repositories

**Quando usar:** SEMPRE que armazenar estado de domínio em estruturas in-memory (`ConcurrentHashMap`, `Caffeine cache`, `Map`, etc.) — especialmente para dados que sobrevivem entre requests (sessões, agregados temporários).

**Regra das 4 camadas (todas obrigatórias):**

```
┌─────────────────────────────────────────────────────────────────┐
│  1. CAP MÁXIMO    → fail-fast com 503 se exceder                │
│  2. TTL POR ITEM  → expira automaticamente após inatividade     │
│  3. CLEANUP       → @Scheduled remove expirados periodicamente  │
│  4. REMOÇÃO EAGER → libera memória pós-finalização do uso       │
└─────────────────────────────────────────────────────────────────┘
```

**Forma canônica:**

```java
@Log4j2
@Repository
public class SessaoIterativaInMemoryRepository implements SessaoIterativaRepository {

    private static final int MAX_SESSOES_ATIVAS = 100;  // ← CAMADA 1: cap

    private final ConcurrentHashMap<UUID, SessaoIterativa> sessoes = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;

    public SessaoIterativaInMemoryRepository(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("wakanda.anki.iterativo.sessoes.ativas", sessoes, ConcurrentHashMap::size)
            .register(meterRegistry);
    }

    @Override
    public SessaoIterativa salvar(SessaoIterativa sessao) {
        validaCapacidadeNaoExcedida(sessao);  // ← CAMADA 1: bloqueia 101ª sessão
        sessoes.put(sessao.getIdSessao(), sessao);
        return sessao;
    }

    private void validaCapacidadeNaoExcedida(SessaoIterativa sessao) {
        if (sessoes.containsKey(sessao.getIdSessao())) return;  // update não conta
        if (sessoes.size() >= MAX_SESSOES_ATIVAS) {
            throw APIException.build(HttpStatus.SERVICE_UNAVAILABLE, "Capacidade máxima de sessões atingida");
        }
    }
}

// Domínio: TTL por item — CAMADA 2
public class SessaoIterativa {
    private static final Duration TTL = Duration.ofMinutes(30);

    public boolean expirou() {
        return Duration.between(this.ultimaInteracao, LocalDateTime.now()).compareTo(TTL) > 0;
    }
}

// CAMADA 3: scheduler periódico
@Component
public class SessaoCleanupScheduler {
    @Scheduled(fixedDelay = 5 * 60 * 1000)  // a cada 5min
    public void limparSessoesExpiradas() {
        List<UUID> idsExpirados = sessaoRepository.idsExpirados();
        idsExpirados.forEach(sessaoRepository::remover);
    }
}

// CAMADA 4: remoção eager no application service
@Service
public class AnkiCoachIterativoApplicationService {
    private AnkiIterativoMensagemResponse finalizarSessaoComDeck(SessaoIterativa sessao, ...) {
        sessao.concluir();
        sessaoRepository.remover(sessao.getIdSessao());  // ← libera memória já
        // ...
    }
}
```

**Métricas obrigatórias** (Micrometer):
- Gauge `wakanda.<feature>.sessoes.ativas` — tamanho atual do map
- Counter `wakanda.<feature>.sessoes.expiradas` — incrementado pelo scheduler

**Por quê 4 camadas e não menos:**
- **Cap sem TTL** → sessões "esquecidas" enchem memória até bater no cap
- **TTL sem cleanup** → sessões expiradas continuam ocupando entrada no map
- **Cleanup sem remoção eager** → memória fica 5min ocupada após uso natural
- **Remoção eager sem cap** → bug em finalização e tudo lota

**Exemplo canônico:** [SessaoIterativaInMemoryRepository](../src/main/java/academy/wakanda/wakanda_ai/anki/infra/memoria/SessaoIterativaInMemoryRepository.java) + [SessaoCleanupScheduler](../src/main/java/academy/wakanda/wakanda_ai/anki/application/service/SessaoCleanupScheduler.java) + [`AnkiCoachIterativoApplicationService.finalizarSessaoComDeck`](../src/main/java/academy/wakanda/wakanda_ai/anki/application/service/AnkiCoachIterativoApplicationService.java) (anki-coach T1.0)

**Anti-pattern relacionado:** ver [A5](#a5-cache-manual-com-hashmap) — cache manual sem TTL/cap é exatamente o que P19 previne quando há justificativa pra não usar `@Cacheable`.

---

### P20. `@Retryable`/`@Recover` testáveis só via integration test (Spring AOP)

**Quando usar:** ao escrever testes para classes que usam **Spring Retry** (`@Retryable`, `@Recover`) ou **Resilience4j** (`@CircuitBreaker`, `@RateLimiter`, etc.).

**Regra crítica:** essas anotações **só funcionam via proxy Spring AOP**. Testes unit puros (`new Adapter(...)`) **NÃO** interceptam retry. O método é chamado direto, exception propaga na primeira tentativa.

**Forma canônica:**

```java
// ❌ ERRADO — não exercita retry
class OpenAiGeradorDeckAIAgentTest {
    @Test void deveLancar503AposRetryEsgotar() {
        // `new` bypassa Spring AOP → @Retryable/@Recover NÃO são interceptados.
        // Este teste esperaria APIException(SERVICE_UNAVAILABLE) do @Recover,
        // mas a RuntimeException propaga direto na primeira tentativa.
        assertThatThrownBy(() -> agent.gerar(new Transcricao("teste")))
            .isInstanceOf(APIException.class);  // ← nunca acontece
    }
}

// ✅ CERTO — integration test com WireMock (sem Spring)
class OpenAiGeradorDeckAIAgentIntegrationTest {
    @Test void gerarDevePropagaExceptionQuandoOpenAiFalha() {
        // Documenta limitação: retry só funciona via Spring AOP.
        // Validação real (3 attempts + APIException SERVICE_UNAVAILABLE)
        // requer @SpringBootTest ou smoke manual.
        wireMockServer.stubFor(post(urlEqualTo("/v1/chat/completions"))
            .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> agent.gerar(new Transcricao("teste")))
            .isInstanceOf(RuntimeException.class);
    }
}

// ✅ MELHOR (quando precisar) — @SpringBootTest
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class OpenAiGeradorDeckAIAgentRetrySpringTest {
    @Autowired GeradorDeckAIAgent agent;

    @Test void deveLancar503ComProblemDetailAposRetry() {
        // Spring AOP ATIVO — @Retryable + @Recover funcionam
        // Assertar 3 chamadas no WireMock + APIException(SERVICE_UNAVAILABLE)
    }
}
```

**Pirâmide de testes para adapters com retry:**

| Camada | Cobre | Custo |
|--------|-------|-------|
| **Unit** (mock direto) | Lógica do adapter (mapping ACL, métricas) sem retry | baixo |
| **Integration** (WireMock direto sem Spring) | Chamada HTTP real ao SDK; propagação de exception | médio |
| **`@SpringBootTest`** (Spring AOP ativo) | Retry 3× + `@Recover` + fallback completo | alto — usar só pra casos críticos ou smoke pré-deploy |

**Documentação obrigatória no teste:** quando o teste **conscientemente** não cobre o retry (por economia de complexidade), incluir comentário explicando a limitação — caso contrário leitor pensa que é cobertura real.

**Exemplo canônico:** [OpenAiGeradorDeckAIAgentIntegrationTest.java](../src/test/java/academy/wakanda/wakanda_ai/anki/infra/openai/OpenAiGeradorDeckAIAgentIntegrationTest.java) — WireMock direto + comentário explicando que retry/fallback ficam em `@SpringBootTest` ou smoke

**Implicação para coverage:** classes com `@Retryable` podem ficar abaixo de 99% em cobertura de linha porque o método `@Recover` raramente é exercitado em testes unit. Aceitável documentar como WARNING — não confundir com bug.

---

### P21. AI Agent Port/Adapter

> **Adicionado 2026-06-01** (review anki-coach). Canoniza como o projeto fala com LLM. Substitui o antigo `LLMProvider` genérico (removido) — ver histórico em [`DETECTION_REPORT.md`](DETECTION_REPORT.md).

**Quando usar:** sempre que um caso de uso precisar de uma resposta de um LLM (gerar conteúdo, conversar, classificar, etc.).

**Regra de ouro:**

> A aplicação fala **linguagem de domínio** (`gerar(transcricao)`, `responder(conversa)`) e recebe o **resultado pronto**. Todo o "como falar com a IA" — system prompt, escolha do LLM, temperatura, tokens, retry, conversão de tipos, parse do output — vive na **infra do subdomínio dono**.

É o paralelo direto do [P1 (Repository)](#p1-repository-portadapter-hexagonal): a aplicação diz `salvar(wakander)` e o `InfraRepository` sabe a query; aqui a aplicação diz `gerar(transcricao)` e o `*AIAgent` sabe o prompt e sobe o seu próprio LLM.

**Forma canônica — port por propósito + adapter na infra do subdomínio:**

```java
// 1. PORT — <subdominio>/application/service/<Proposito>AIAgent.java
//    Contrato em linguagem de domínio. SEM prompt, SEM temperatura, SEM tipos de SDK.
public interface GeradorDeckAIAgent {
    AnkiDeck gerar(Transcricao transcricao);
}

// 2. ADAPTER — <subdominio>/infra/openai/OpenAi<Proposito>AIAgent.java
@Log4j2
@Component
public class OpenAiGeradorDeckAIAgent implements GeradorDeckAIAgent {

    private final ChatLanguageModel model;                    // ← cada agente sobe o SEU builder
    private final LangChainMensagemMapper mapper;             // ← mecânica compartilhada (composição)
    private final AnkiRespostaJsonMapper jsonMapper;          // ← tradução do output (JSON→domínio, ACL)

    public OpenAiGeradorDeckAIAgent(
            @Value("${wakanda.ai.openai.api-key}") String apiKey,           // ← secret, nunca em DTO/log; sem default ':' (no yml)
            @Value("${wakanda.ai.openai.base-url}") String baseUrl,         // ← config no yml, não no @Value
            LangChainMensagemMapper mapper, AnkiRespostaJsonMapper jsonMapper, MeterRegistry meterRegistry) {
        this.model = construirModelo(apiKey, baseUrl);        // ← build encapsulado em método privado
        // ...
    }

    private ChatLanguageModel construirModelo(String apiKey, String baseUrl) {
        return OpenAiChatModel.builder()
            .apiKey(apiKey).baseUrl(baseUrl).modelName(MODELO)            // ← MODELO é constante DESTE agente
            .temperature(TEMPERATURA).maxTokens(MAX_TOKENS)
            .responseFormat("json_object").build();                      // ← JSON mode do provider
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, notRecoverable = APIException.class,
               maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public AnkiDeck gerar(Transcricao transcricao) {
        log.info("[start] OpenAiGeradorDeckAIAgent - gerar");
        ChatHistory conversa = montarConversa(transcricao);          // system prompt aqui
        RespostaIA resposta = chamarLlm(conversa);                   // mapper + métrica
        AnkiDeck deck = jsonMapper.paraDeck(jsonMapper.ler(resposta.texto())); // JSON→domínio
        log.debug("[finish] OpenAiGeradorDeckAIAgent - gerar");
        return deck;
    }

    @Recover
    AnkiDeck recuperarFalha(RuntimeException ex) {                    // sem param não usado; preserva a causa
        throw APIException.build(HttpStatus.SERVICE_UNAVAILABLE, "LLM indisponível", ex);
    }

    // constantes de config ao final da classe
    private static final double TEMPERATURA = 0.3;            // ← config DESTE agente
    private static final int MAX_TOKENS = 4000;
    private static final String MODELO = "gpt-4o-mini";       // ← modelo via constante, não @Value global
    private static final String SISTEMA = """ ... """;        // ← system prompt DESTE agente (pede JSON)
}

// 3. USE-CASE — <subdominio>/application/service/*ApplicationService.java
//    Só orquestra domínio e repassa o output. NÃO conhece prompt nem temperatura.
@Override
public AnkiInstantaneoResponse gerarDeck(AnkiInstantaneoRequest request) {
    Transcricao transcricao = new Transcricao(request.transcricao());
    AnkiDeck deck = geradorDeckAgent.gerar(transcricao);      // pega output pronto
    return construirResposta(deck);                           // repassa
}
```

**As 4 camadas:**

| Camada | Conteúdo | Exemplo |
|--------|----------|---------|
| `ai/domain` (kernel reutilizável) | VOs de conversa puros — sem Spring/HTTP/SDK | `MensagemChat`, `ChatRole`, `ChatHistory`, `RespostaIA` |
| `ai/infra` (mecânica compartilhada, **sem interface**) | Conversão de tipos + extração de resposta, reusada por **composição** | `LangChainMensagemMapper` |
| `<subdominio>/application/service` (port + use-case) | A **única** interface (inversão de dependência); use-case só repassa | `GeradorDeckAIAgent`, `GeradorDeckIterativoAIAgent` |
| `<subdominio>/infra/openai` (adapter) | system prompt + builder do LLM + parse do output + retry + métrica + DTOs JSON (ACL) | `OpenAiGeradorDeckAIAgent`, `OpenAiGeradorDeckIterativoAIAgent`, `AnkiRespostaJsonMapper` |

> **Naming do kernel (revisão 2026-06-08):** o VO de mensagem do kernel chama-se **`MensagemChat`** (não `ChatMessage`) — elimina a colisão com `dev.langchain4j.data.message.ChatMessage`, deixando o mapper importar o tipo do LangChain sem caminho completo.

**Interface vs Composição (regra crítica):**
- **Interface (port) — só uma:** `<Proposito>AIAgent`. É a inversão de dependência com a aplicação, como o port `*Repository`.
- **Composição — o resto:** o adapter **compõe** o mapper compartilhado + o builder do LangChain4j. O builder (`OpenAiChatModel.builder()...`) **já é** uma abstração limpa; não se embrulha numa segunda interface.
- **❌ Proibido:** criar um `LLMProvider`/`ChatModelClient` genérico com implementações por provider. Isso encapsula algo que já é abstração e nunca será trocado dinamicamente na prática. **Não forçar herança onde a composição + uma interface de port bastam.**

**Divisão de responsabilidade:**

| Concern | Onde vive | Por quê |
|---------|-----------|---------|
| Prompt (system + montagem) | adapter do agente | específico do propósito, como a query SQL |
| Escolha do LLM (modelo via **constante**) / temperatura / tokens (builder) | adapter do agente | cada agente sobe o seu; modelo é decisão do adapter, não property global; o builder já é a abstração |
| api-key + base-url | `@Value` no adapter (**sem default `:` — config no yml**) | property/secret; **nunca** logada, **nunca** em DTO |
| Conversão de tipos + extração de resposta | `LangChainMensagemMapper` (ai/infra) | mecânica genérica reusada por composição |
| Retry / resiliência | `@Retryable`/`@Recover` no adapter | Spring AOP; testado via integration test ([P20](#p20-retryablerecover-testáveis-só-via-integration-test-spring-aop)); `@Recover` preserva a causa original |
| Parse do output (**JSON estruturado → domínio** via DTO ACL) | adapter do agente (`*JsonMapper`) | tradução do que a IA devolve; ver padrão de saída JSON abaixo |
| Métrica de tokens/latência | adapter (infra) | observabilidade da chamada = infra |
| Métrica de negócio (decks, sessões) | ApplicationService | é domínio |

**Granularidade:** **um port por propósito** (`GeradorDeckAIAgent`, `GeradorDeckIterativoAIAgent`), não um port por subdomínio. 1 port = 1 responsabilidade, cada um com sua config no adapter.

**Nem todo agente é conversacional:** o kernel `ai/domain` oferece building blocks (`ChatHistory` para multi-turno); um agente single-shot usa um `ChatHistory` de 2 mensagens (system + user). Agentes que não usam chat (ex: embeddings) não dependem desses VOs.

**Exemplos canônicos:**
- [GeradorDeckAIAgent](../src/main/java/academy/wakanda/wakanda_ai/anki/application/service/GeradorDeckAIAgent.java) (port) + [OpenAiGeradorDeckAIAgent](../src/main/java/academy/wakanda/wakanda_ai/anki/infra/openai/OpenAiGeradorDeckAIAgent.java) (adapter)
- [GeradorDeckIterativoAIAgent](../src/main/java/academy/wakanda/wakanda_ai/anki/application/service/GeradorDeckIterativoAIAgent.java) (port multi-turno) + [OpenAiGeradorDeckIterativoAIAgent](../src/main/java/academy/wakanda/wakanda_ai/anki/infra/openai/OpenAiGeradorDeckIterativoAIAgent.java)
- [LangChainMensagemMapper](../src/main/java/academy/wakanda/wakanda_ai/ai/infra/langchain/LangChainMensagemMapper.java) (mecânica compartilhada por composição)
- Kernel: [ChatHistory](../src/main/java/academy/wakanda/wakanda_ai/ai/domain/ChatHistory.java), [MensagemChat](../src/main/java/academy/wakanda/wakanda_ai/ai/domain/MensagemChat.java), [RespostaIA](../src/main/java/academy/wakanda/wakanda_ai/ai/domain/RespostaIA.java)

**Padrão de saída do LLM — JSON estruturado > magic-string (revisão 2026-06-08):**

> O LLM devolve **JSON estruturado** (ligando o `responseFormat("json_object")` do provider quando suportado), desserializado por um **DTO ACL na infra** (`AnkiCardJson`/`AnkiRespostaJson`) e traduzido para o domínio por um mapper (`AnkiRespostaJsonMapper`). **Não** se usa sinal mágico em prosa (ex.: a antiga string `GERANDO CARDS...`) nem filtragem defensiva de texto. Para multi-turno, a presença de um campo no JSON (ex.: `cards`) decide o ramo (pergunta vs. deck pronto). O DTO externo vive na infra do subdomínio — o domínio nunca o conhece ([A4](#a4-dto-externo-vazando-para-application-layer)). Exemplos: [`AnkiRespostaJsonMapper`](../src/main/java/academy/wakanda/wakanda_ai/anki/infra/openai/AnkiRespostaJsonMapper.java), [`AnkiCardJson`](../src/main/java/academy/wakanda/wakanda_ai/anki/infra/openai/AnkiCardJson.java).

**Anti-pattern relacionado:** ver [A4](#a4-dto-externo-vazando-para-application-layer) (ACL) — os VOs do kernel `ai/domain` **não** são DTOs externos; são abstração interna pura (zero import de `dev.langchain4j`), por isso podem ser reusados por qualquer subdomínio.

---

### P22. `APIException` no domínio é permitida (decisão deliberada)

> **Adicionado 2026-06-01.** Esclarecimento para evitar que reverse-eng/specs futuras tratem isso como dívida.

**Regra:** VOs e entidades de domínio **podem** lançar `APIException` carregando `HttpStatus` (ex.: [`ChatHistory`](../src/main/java/academy/wakanda/wakanda_ai/ai/domain/ChatHistory.java), [`SessaoIterativa`](../src/main/java/academy/wakanda/wakanda_ai/anki/domain/SessaoIterativa.java), [`Transcricao`](../src/main/java/academy/wakanda/wakanda_ai/anki/domain/Transcricao.java)). Não é dívida nem violação — é decisão arquitetural do projeto.

**Trade-off (consciente):** há um leve acoplamento do domínio a um status HTTP. Aceito porque:
1. As aplicações deste projeto são **sempre web** — o status é sempre relevante.
2. Mantém o `@RestControllerAdvice` **enxuto** — ele não precisa conhecer/traduzir cada exceção de domínio (evita acoplamento e um arquivo "sempre-mexido").
3. Evita **espalhar a lógica de exceção em duas** (uma de domínio + conversão na aplicação/controller).
4. É **menos verboso**.

Se um dia o domínio for reusado fora de contexto web, `APIException` é capturável/extensível na borda. **Não "consertar" isso em specs futuras** — é o padrão estabelecido (`APIException.build(HttpStatus.X, "msg curta")`). Combina com [P9](#p9-error-handling--retry-a-canonizar) (o handler global depende de `APIException` para gerar `ProblemDetail`) e [P15](#p15-validação-extraída-em-método-privado-nomeado) (validação privada nomeada lança `APIException`).

**Atenção:** a mensagem da `APIException` segue [A10](#a10-emoji-em-mensagens-server-side-exceptions-logs-response-bodies) (sem emoji) e [P18](#p18-logging-lgpd--metadata-only-para-conteúdo-pii) (sem PII — descrições genéricas, nunca conteúdo do usuário).

---

## Anti-patterns (NÃO FAÇA)

### A1. Cascata síncrona em `concluiMissao()`

**O problema:** `MissaoProgressoApplicationService.concluiMissao()` executa, dentro da MESMA transação:
1. marca missão como concluída
2. recalcula XP
3. dispara `XpPromocaoClasseEvent`
4. atualiza classe
5. enfileira WhatsApp para Wakander

Se algum passo lança exception, **TUDO** faz rollback — inclusive a conclusão da missão (que era a operação principal).

**Por que é ruim:**
- Disponibilidade #3 comprometida — uma falha de Z-API derruba conclusão de missão
- Mantenibilidade #2 — service de 5 responsabilidades viola SRP

**Como fazer:**

```java
// ❌ ERRADO (cascata síncrona)
@Transactional
public void concluiMissao(...) {
    missao.concluir();
    xpService.atribuirXP(...);
    classeService.promoverSeAplicavel(...);
    whatsappService.notificarWakander(...);  // se falhar, missão volta a "em andamento"!
}

// ✅ CERTO (eventos AFTER_COMMIT + SQS)
@Transactional
public void concluiMissao(...) {
    missao.concluir();
    eventPublisher.publishEvent(new MissaoConcluidaEvent(missao));
    // commit aqui
}

@TransactionalEventListener(phase = AFTER_COMMIT)
public void onMissaoConcluida(MissaoConcluidaEvent event) {
    snsTemplate.send("xp-wakander-requests-topic.fifo", event);  // assíncrono
}
```

---

### A2. Spring `@EventListener` síncrono dentro da TX

**O problema:** `XpWakanderConsumer` tem 2 entradas:

```java
@SqsListener("${aws.queue.xp-wakander-requests}")
public void consumeXpWakanderQueueMessage(SqsMessageDto sqsMessageDto) { /* SQS */ }

@EventListener  // ⚠️ síncrono, dentro da TX que publicou
public void promoveClasseXpWakander(XpPromocaoClasseEvent evento) { /* Spring Event */ }
```

Quando `XpWakanderService.processaXP()` publica `XpPromocaoClasseEvent`, o `@EventListener` roda DENTRO da transação. Se falhar → rollback de tudo.

**Como fazer:**

```java
// ✅ CERTO
@TransactionalEventListener(phase = AFTER_COMMIT)
public void promoveClasseXpWakander(XpPromocaoClasseEvent evento) {
    log.info("[start] - promoveClasseXpWakander (AFTER_COMMIT)");
    // ...
}
```

**Quando usar Spring Event vs SQS:**
- Spring Event (`@TransactionalEventListener AFTER_COMMIT`) — reações DENTRO do mesmo processo, NÃO precisam sobreviver a restart
- SQS — comunicação entre módulos que devem sobreviver a restart + retry + DLQ

---

### A3. `@NotBlank`/`@NotNull` em entidades de domínio

**O problema:** Bean Validation acopla domínio ao framework. Domínio deveria validar no construtor (ou em método/factory quando exceção [P5](#p5-construção-via-construtor-factory-como-exceção) aplica), lançando exceptions de negócio.

```java
// ❌ ERRADO
@Entity
public class Wakander {
    @NotBlank
    @Email
    private String email;  // se vier null/inválido, Hibernate Validator joga ConstraintViolationException
}

// ✅ CERTO — construtor público com validação privada nomeada
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Wakander {

    @Embedded
    private WakanderContato contato;

    public Wakander(String email, String nome, ...) {
        validaEmail(email);
        validaNome(nome);
        this.contato = new WakanderContato(email);
        this.nome = nome;
    }

    private static void validaEmail(String email) {
        if (email == null || email.isBlank()) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Email obrigatório");
        }
    }
}
```

**Onde Bean Validation É bem-vinda:** DTOs de API (request/response) — eles têm que cumprir contrato HTTP.

```java
public record CriarWakanderRequest(
    @NotBlank @Email String email,
    @NotBlank String nome,
    @Min(0) int idade
) { }
```

---

### A4. DTO externo vazando para application layer

**O problema:** `ZApiEventDto`, `DiscordEventRequest`, `MemberkitEventDto` são DTOs **do PROVIDER externo** — eles podem mudar sem aviso. Hoje vivem em `application/service/whatsapp/ZApiEventDto.java` e application sabe da estrutura externa.

```java
// ❌ ERRADO (atual)
// application/service/whatsapp/ZApiEventDto.java
public class ZApiEventDto {
    private ZApiEventype type;  // ← enum específico do Z-API
    private String whatsapp;
    private String mensagem;
    private LocalDate dataEnvio;
}
```

Se Z-API renomear "whatsapp" para "phone_number", todos os processadores quebram.

**Como fazer (ACL — Anti-Corruption Layer):**

```java
// 1. DTO externo SÓ no infra
// infra/zapi/ZApiHttpRequest.java
public class ZApiHttpRequest {
    private String phoneNumber;  // contrato Z-API
    private String message;
}

// 2. ACL traduz para DTO interno do domínio
// infra/zapi/ZApiAdapter.java
@Component
public class ZApiAdapter {
    public MensagemRecebida toDomain(ZApiHttpRequest req) {
        return new MensagemRecebida(req.getPhoneNumber(), req.getMessage(), LocalDate.now());
    }
}

// 3. Application só conhece o DTO interno
// application/service/whatsapp/MensagemRecebida.java
public record MensagemRecebida(String numero, String texto, LocalDate dataEnvio) { }
```

**Regra absoluta:** toda integração externa nasce com ACL. **Sem exceção.**

---

### A5. Cache manual com `HashMap`

**O problema:** alguns services do projeto fazem cache manual:

```java
// ❌ ERRADO
private final Map<UUID, Wakander> cacheWakanders = new HashMap<>();

public Wakander busca(UUID id) {
    return cacheWakanders.computeIfAbsent(id, this::buscaDoBanco);
}
```

Problemas:
- Não tem TTL — cresce indefinidamente
- Não é thread-safe
- Não é distribuído (perde em restart)
- Não tem métricas (hit rate, evictions)

**Como fazer:**

```java
// ✅ CERTO
@Cacheable(value = "wakanders", key = "#id")
public Wakander busca(UUID id) {
    return wakanderRepository.buscaPorId(id);
}
```

```yaml
# application.yml
spring:
  cache:
    type: caffeine  # ou redis se distribuído
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=10m,recordStats
```

---

### A6. `deduplicationId = UUID.randomUUID()` sem idempotência no handler

**O problema:** publisher SNS gera `deduplicationId` aleatório → SNS/SQS FIFO não consegue deduplicar → handler recebe a mesma mensagem 2× → efeito duplicado.

```java
// ❌ ERRADO
snsTemplate.sendNotification(
    "xp-wakander-requests-topic.fifo",
    Notification.builder()
        .payload(event)
        .deduplicationId(UUID.randomUUID().toString())  // ← aleatório, anula FIFO dedup
        .build()
);
```

**Como fazer:**

```java
// ✅ CERTO
snsTemplate.sendNotification(
    "xp-wakander-requests-topic.fifo",
    Notification.builder()
        .payload(event)
        .deduplicationId(event.getIdMissaoProgresso() + "-XP")  // ← determinístico
        .groupId(event.getIdWakander().toString())              // ← ordena por wakander
        .build()
);

// E no handler: P7 — checa idempotência por chave de negócio
```

**Combinação correta:** `MessageGroupId` (FIFO ordering) + `MessageDeduplicationId` determinístico (FIFO dedup) + verificação de estado no handler (P7).

---

### A7. `UnsupportedOperationException` (ou exceptions Java padrão) em adapters

**O problema:** adapters de integração externa lançam `UnsupportedOperationException`, `IllegalStateException`, `RuntimeException` cru. Essas exceptions:
- Não carregam `HttpStatus` semântico → `@RestControllerAdvice` cai no handler genérico (500)
- Não viram `ProblemDetail` RFC 7807 com correlationId
- Frontend recebe mensagem confusa em vez de status acionável (503 vs 422)

```java
// ❌ ERRADO — adapter de agente cujo provider ainda não está pronto
@Component
public class AnthropicGeradorDeckAIAgent implements GeradorDeckAIAgent {
    @Override
    public AnkiDeck gerar(Transcricao transcricao) {
        throw new UnsupportedOperationException(
            "Anthropic adapter preparado mas não ativo no MVP. Ative via wakanda.ai.provider=anthropic + configure API key"
        );  // ← exception genérica + msg longa
    }
}
```

**Como fazer:**

```java
// ✅ CERTO — adapter sempre lança APIException com HttpStatus semântico
@Component
public class AnthropicGeradorDeckAIAgent implements GeradorDeckAIAgent {

    @Override
    public AnkiDeck gerar(Transcricao transcricao) {
        log.info("[start] AnthropicGeradorDeckAIAgent - gerar");
        throw APIException.build(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Anthropic provider não implementado no MVP"
        );
    }
}
```

Exemplo real no projeto: o `@Recover` dos adapters de agente OpenAI lança `APIException.build(HttpStatus.SERVICE_UNAVAILABLE, "LLM indisponível")` após o retry esgotar (ver [P21](#p21-ai-agent-portadapter) e [OpenAiGeradorDeckAIAgent](../src/main/java/academy/wakanda/wakanda_ai/anki/infra/openai/OpenAiGeradorDeckAIAgent.java)).

**Regras:**
- Adapter **sempre** usa `APIException.build(HttpStatus.X, "msg curta")`
- HttpStatus apropriado:
  - **503 SERVICE_UNAVAILABLE** — provider externo não acessível ou stub não implementado
  - **502 BAD_GATEWAY** — provider retornou resposta inválida
  - **504 GATEWAY_TIMEOUT** — timeout na chamada externa
  - **422 UNPROCESSABLE_ENTITY** — payload do provider não pôde ser parseado
- Mensagem curta (até ~80 chars) e acionável; detalhes vão para log
- **NUNCA** mensagens longas com instruções de configuração — isso é doc, não mensagem de erro

**Anti-pattern relacionado:** ver [P9](#p9-error-handling--retry-a-canonizar) — global handler depende de `APIException` para gerar `ProblemDetail` consistente.

---

### A8. `var` em métodos de application/domain/api

**O problema:** `var` esconde o tipo do leitor, especialmente em métodos que mexem com múltiplas camadas. Em projeto com naming explícito (`ProgressoWakander`, `MissaoProgresso`, `XpWakander`), tipo concreto é parte da documentação.

```java
// ❌ ERRADO
@Override
public void processaXP(XpWakanderEventDTO event) {
    var progresso = progressoRepository.buscaProgressoPorId(event.getIdProgressoWakander());
    var xp = obterOuCriarXpWakander(progresso);
    var sabedorias = event.getSabedorias();
    xp.adicionarXpEAtualizarNivel(event.getXpObtido(), sabedorias);
}
```

→ Leitor não sabe se `progresso` é `ProgressoWakander`, `ProgressoWakanderDto` ou `Optional<ProgressoWakander>` sem inspecionar a chamada.

**Como fazer:**

```java
// ✅ CERTO — tipo concreto explícito
@Override
public void processaXP(XpWakanderEventDTO event) {
    ProgressoWakander progressoWakander = progressoRepository.buscaProgressoPorId(event.getIdProgressoWakander());
    XpWakander xpWakander = obterOuCriarXpWakander(progressoWakander);
    xpWakander.adicionarXpEAtualizarNivel(event.getXpObtido(), event.getSabedorias());
}
```

**Regras:**
- `var` é **bloqueio em code review** em application/, domain/, infra/, api/
- Nomes de variáveis também devem ser descritivos: `progressoWakander` (não `p`), `xpWakander` (não `xp` quando ambíguo), `transcricao` (não `t`), `card` (não `c`)
- **Exceção tolerada:** `try-with-resources` óbvios (`try (var stream = ...) `), ou casos em que o tipo está no nome do método imediatamente à direita (raros). Mesmo assim, prefira concreto

**Exemplos canônicos (padrão real):**
- [XpWakanderApplicationService.java:45,47](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/application/service/XpWakanderApplicationService.java) — `ProgressoWakander progressoWakander = ...`, `XpWakander wakander = ...`

---

### A9. `ResponseEntity` em controllers

**O problema:** `ResponseEntity` no retorno acopla controller a wrapping HTTP, polui assinatura, dificulta teste, esconde status code (que fica enterrado em `.status(HttpStatus.X)`).

```java
// ❌ ERRADO
@PostMapping("/v1/generate")
public ResponseEntity<AnkiResponse> generate(@Valid @RequestBody AnkiRequest request) {
    AnkiResponse response = service.gerarDeck(request);
    return ResponseEntity.ok(response);
}
```

**Como fazer:** ver [P16](#p16-resposta-http-via-responsestatus-não-responseentity).

```java
// ✅ CERTO
@PostMapping("/v1/generate")
@ResponseStatus(HttpStatus.OK)
AnkiResponse generate(@Valid @RequestBody AnkiRequest request) {
    log.info("[start] AnkiCoachApi - generate");
    AnkiResponse response = service.gerarDeck(request);
    log.debug("[finish] AnkiCoachApi - generate");
    return response;
}
```

**Regra absoluta:** `ResponseEntity` no projeto = bloqueio.

---

### A10. Emoji em mensagens server-side (exceptions, logs, response bodies)

**O problema:** emojis em mensagens server-side criam três classes de bug:
1. **Logs** — viram caracteres `?` em terminais sem suporte UTF-8, quebram parsers de log estruturado, ocupam espaço em ProblemDetail JSON
2. **Exceptions** — vazam para frontend que pode estar em locale sem suporte (browsers antigos, leitores de tela)
3. **Response bodies** — quebram contrato semântico de mensagem de erro (RFC 7807 ProblemDetail). Emoji é decoração de UI, não conteúdo de erro

```java
// ❌ ERRADO (do spec preliminar do anki-coach)
throw APIException.build(HttpStatus.BAD_REQUEST, "Eita, transcrição vazia. Cola aí pra gente 😉");
log.info("Pronto! {} cards 🎉", cardCount);
return new AnkiResponse("Pronto, deck gerado! 🎉", csv, count, true);
```

**Como fazer:**

```java
// ✅ CERTO — server-side limpo
throw APIException.build(HttpStatus.BAD_REQUEST, "Transcrição não pode ser vazia");
log.info("[info] Deck gerado com {} cards", cardCount);
return new AnkiResponse("Deck gerado", csv, count, true);
```

**Onde emoji É permitido:**
- Templates Thymeleaf (`*.html`) — UI consumida só por browser
- JavaScript do frontend — UI client-side
- Documentação `.md` em `docs/` e `sdd/`
- Commit messages e PR descriptions

**Regra:** `find src/main/java -name '*.java' -exec grep -l '[😀-🙏✨🎉]' {} \;` deve retornar **vazio** sempre.

---

### A11. Comentários inline `//` explicando passos

**O problema:** comentários `// 1. Validar input`, `// 2. Buscar do banco`, `// 3. Chamar LLM` são indicação de que o método deveria ser quebrado em privados nomeados (P13). Comentários:
- Não rodam — código pode divergir do que comentário diz
- Substituem nome descritivo que daria mesma informação no menor escopo (do método privado)
- Geram ruído visual em diff, code review e leitura

```java
// ❌ ERRADO
public AnkiInstantaneoResponse gerarDeck(AnkiInstantaneoRequest request) {
    // 1. Validar e criar VO de domínio
    Transcricao transcricao = new Transcricao(request.transcricao());

    // 2. Gerar o deck via agente
    AnkiDeck deck = geradorDeckAgent.gerar(transcricao);

    // 3. Serializar e retornar
    return new AnkiInstantaneoResponse(csvFormatter.serialize(deck), deck.quantidadeCards(), UUID.randomUUID());
}
```

**Como fazer:** método quebrado em privados nomeados — o nome do método é o "comentário". O use-case só orquestra domínio e repassa o output do agente ([P21](#p21-ai-agent-portadapter)):

```java
// ✅ CERTO
public AnkiInstantaneoResponse gerarDeck(AnkiInstantaneoRequest request) {
    log.info("[start] AnkiCoachInstantaneoApplicationService - gerarDeck");
    Transcricao transcricao = new Transcricao(request.transcricao());
    AnkiDeck deck = geradorDeckAgent.gerar(transcricao);     // agente sabe prompt + LLM
    AnkiInstantaneoResponse resposta = construirResposta(deck);
    log.debug("[finish] AnkiCoachInstantaneoApplicationService - gerarDeck");
    return resposta;
}

private AnkiInstantaneoResponse construirResposta(AnkiDeck deck) { /* serializa + métrica de negócio */ }
```

**Onde comentário é OK:**
- **Javadoc** em interfaces públicas (`/** ... */` no port `GeradorDeckAIAgent`)
- **TODO** com referência a ticket (`// TODO WAI-XXX: ...`)
- **Workarounds** específicos que precisam de "porque assim" (sempre com link pro issue/bug do framework)
- **ADRs** em arquivos `.md`, não no Java

**Regra:** comentário inline `// passo N` ou `// faz X` ou `// agora Y` em diff = pedido de refatoração no code review.

---

### A12. Instanciação seguida de mutação externa (`new Entity(); entity.attr = xpto;`)

**O problema:** consumer instancia entidade e depois mexe em atributos diretamente. Isso vaza acoplamento — consumer precisa conhecer ordem de inicialização, invariantes internos, defaults derivados. Causa raiz: ou construtor incompleto, ou setters expostos via `@Setter`/`@Data`. Esse anti-pattern é particularmente perigoso porque é a forma mais sutil de furar [P4 (Domínio Rico)](#p4-domínio-rico) e [P5 (Construção via construtor)](#p5-construção-via-construtor-factory-como-exceção).

```java
// ❌ ERRADO — consumer monta estado interno do agregado
@Service
public class WakanderApplicationService {

    public Wakander cadastraWakander(WakanderNovoRequest request) {
        Wakander wakander = new Wakander();
        wakander.setNome(request.getNome());
        wakander.setCpf(request.getCpf());
        wakander.setStatusCadastro(StatusCadastro.COMPLETO);
        wakander.setJornadaAtual(JornadaWakanda.JORNADA_CONHECIMENTO);
        wakander.setContato(new WakanderContato(request));
        return wakanderRepository.salva(wakander);
    }
}
```

Problemas:
- Setters expostos quebram invariantes (alguém pode setar `statusCadastro = null`)
- Ordem de inicialização vira responsabilidade do consumer (se esquecer `setStatusCadastro`, fica `null`)
- Mudança no agregado força mudança em todo consumer
- Não passa por validação de domínio (quem garante que `cpf` é válido?)

**Como fazer — construtor recebe tudo necessário; agregado orquestra criação dos internos:**

```java
// ✅ CERTO — construtor encapsula montagem
public Wakander(WakanderNovoRequest wakanderNovo) {
    this.nome = wakanderNovo.getNome();
    this.cpf = wakanderNovo.getCpf();
    this.statusCadastro = StatusCadastro.COMPLETO;
    this.jornadaAtual = JornadaWakanda.JORNADA_CONHECIMENTO;
    this.contato = new WakanderContato(wakanderNovo);
    this.financeiro = new WakanderFinanceiro();
}

// Consumer fica trivial:
@Service
public class WakanderApplicationService {

    public Wakander cadastraWakander(WakanderNovoRequest request) {
        log.info("[start] WakanderApplicationService - cadastraWakander");
        Wakander wakander = new Wakander(request);
        Wakander salvo = wakanderRepository.salva(wakander);
        log.debug("[finish] WakanderApplicationService - cadastraWakander");
        return salvo;
    }
}
```

**Quando o estado precisa mudar após criação:** use **método comportamental nomeado** no agregado:

```java
// ✅ CERTO — método comportamental
public class Wakander {

    public void atualizaProgresso(JornadaWakanda jornadaWakanda) {
        validaJornadaAtual(jornadaWakanda);
        validaWakanderRegular();
        this.jornadaAtual = jornadaWakanda;
    }
}

// Consumer:
wakander.atualizaProgresso(JornadaWakanda.JORNADA_CODIFICACAO);  // não wakander.setJornadaAtual(...)
```

**Regras:**
- **Banir `@Setter` / `@Data`** em entidades e VOs (ver [PROJECT.md lombok_usage.forbidden](PROJECT.md#technology-preferences))
- **Banir** modificação direta de campos via `entity.field = X` em consumers (ApplicationService, Controllers)
- Mutações de estado expostas APENAS via métodos comportamentais nomeados (`atualizaProgresso`, `concluiMissao`, `registrarPergunta`)
- Internos do agregado (entidades/VOs internos com construtor package-private) só são criados pelo próprio agregado, nunca pelo consumer

**Exemplo canônico no projeto:**
- [Wakander.java:80,93](../src/main/java/academy/wakanda/wakanda_ai/wakander/domain/Wakander.java) — 2 construtores públicos por contexto
- [Wakander.java:102-116](../src/main/java/academy/wakanda/wakanda_ai/wakander/domain/Wakander.java) — `atualizaProgresso`, `mudaStatusFinanceiro`, `atualizaUltimaAulaAssistida` como métodos comportamentais

**Anti-pattern relacionado:** [A3](#a3-notblanknotnull-em-entidades-de-domínio) (Bean Validation em entidades) muitas vezes vem junto com A12 — código que usa `@Setter` + `@NotBlank` espera que framework faça o trabalho do domínio.

---

## Padrões de Extensão

| Para adicionar... | Como | Modificação em código existente |
|---|---|---|
| Tipo de evento WhatsApp/Memberkit/Discord | Nova classe `@Component implements XxxProcessor` em `processadores/` | **Zero** |
| Repositório de entidade nova | Port em `application/service/<dominio>/` + Adapter em `infra/<dominio>/` + Spring Data em `infra/<dominio>/` | **Zero** |
| Integração externa nova | Cliente HTTP em `infra/<provider>/` + ACL (Adapter) traduzindo DTO externo → DTO domínio + ConsumerSqs se receber webhook | **Zero** |
| Tipo de Value Object novo | Classe `@Embeddable` com `@AllArgsConstructor` + construtor público (ou factory quando exceção [P5](#p5-construção-via-construtor-factory-como-exceção) aplica) chamando validações privadas nomeadas + ajustar entidade owner | Toca só a entidade owner |
| Endpoint REST novo | Controller em `application/api/` retornando DTOs (não entidades) + service em `application/service/` + Bean Validation no request DTO | **Zero** |
| Consumer SQS novo | Classe `*Consumer` ou `*ConsumerSqs` em `infra/` com `@SqsListener` + idempotência por chave (P7) | **Zero** |
| Agente de IA novo (gerar conteúdo, conversar, classificar) | Port `<Proposito>AIAgent` em `<subdominio>/application/service/` + adapter `OpenAi<Proposito>AIAgent` em `<subdominio>/infra/<provider>/` que sobe seu builder e usa `LangChainMensagemMapper` por composição (ver [P21](#p21-ai-agent-portadapter)) | **Zero** |
| Provider de LLM alternativo para um agente existente (ex: Anthropic) | Novo adapter `Anthropic<Proposito>AIAgent` implementando o mesmo port `*AIAgent`, selecionado por `@Qualifier`/perfil | **Zero** |
| Tipo de missão com comportamento próprio | **REQUER refactor de `TipoMissao` para Strategy** (P0 do roadmap, owner João Lira ~jul/2026) | **Não-zero ainda** |
| Métrica de negócio nova | `meterRegistry.counter("wakanda.<dominio>.<metrica>", tag, value)` no service | Toca só o service |

---

## Referências

- 📘 [`docs/architecture-haiku.md`](../docs/architecture-haiku.md) §"Padrões de extensão (canônicos — replicar)" e §"Dívida arquitetural reconhecida"
- 📕 [`docs/arquitetura-gameficacao.md`](../docs/arquitetura-gameficacao.md) §11 — priorização de gaps derivada do Haiku
- 📙 [`CLAUDE.md`](../CLAUDE.md) §"Convenções de Código" e §"Dívida Arquitetural Conhecida (não replicar)"
- 📗 [`PROJECT.md`](PROJECT.md) §"Non-Negotiables" (princípios operacionais — trade-off triplo, ACL, anti-prescriptive)
- Código canônico citado:
  - [`MissaoProgressoRepository`](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/application/service/MissaoProgressoRepository.java) + [`MissaoProgressoInfraRepository`](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/missaoprogresso/infra/MissaoProgressoInfraRepository.java) (P1)
  - [`ComunicacaoProcessorWhatsapp`](../src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/service/whatsapp/processadores/ComunicacaoProcessorWhatsapp.java) + [`ComunicacaoProcessorNormal`](../src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/service/whatsapp/processadores/ComunicacaoProcessorNormal.java) (P2)
  - [`XpWakanderConsumer`](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/infra/XpWakanderConsumer.java) (P3, A2)
  - [`XpWakander`](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/domain/XpWakander.java) (P4)
  - [`Wakander`](../src/main/java/academy/wakanda/wakanda_ai/wakander/domain/Wakander.java) (P5 — construtor público; A12 — métodos comportamentais)
  - [`JornadaProgresso`](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/progresso/jornadaprogresso/domain/JornadaProgresso.java) (P5 — construtor público com inicialização determinística)
  - [`OrdemMissao`](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/catalogo/missaowakanda/domain/OrdemMissao.java) (P5 — exceção legítima: factory com validação sobre primitivo)
  - [`XpWakander`](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/domain/XpWakander.java) (P5 — exceção legítima: factory com defaults complexos fibonacci)
  - [`ZApiEventDto`](../src/main/java/academy/wakanda/wakanda_ai/comunicacao/application/service/whatsapp/ZApiEventDto.java) (A4 — contraexemplo atual)
  - [`XpPromocaoClasseEvent`](../src/main/java/academy/wakanda/wakanda_ai/gameficacao/xp/xpwakander/application/event/XpPromocaoClasseEvent.java) (A2 — DTO de Spring Event síncrono)
