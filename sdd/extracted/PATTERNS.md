# PATTERNS.md — WakandaAI

> Padrões estabelecidos extraídos do código (evidência ≥ 2 locais). Repo médio → máx. 20 padrões.
> Gerado em 2026-06-10 via `/sdd.reverse-eng`.

## Repository Pattern (Ports & Adapters duplo)

**Category**: Database

**Evidence**: aplicado em 100% das entidades, ex.:
- `gameficacao/progresso/missaoprogresso/application/service/MissaoProgressoRepository.java` (port)
- `gameficacao/progresso/missaoprogresso/infra/MissaoProgressoInfraRepository.java` (adapter)
- `*SpringDataJpaRepository` usado dentro do adapter

**Example**:
```java
// port (application/service/) — retorna domain objects, sem @Entity na assinatura
public interface MissaoProgressoRepository {
    MissaoProgresso buscaMissaoProgresso(UUID idMissao, UUID idProgressoWakander);
    void salvaProgressoMissao(MissaoProgresso missaoProgresso);
}
// adapter (infra/) — @Repository, delega ao Spring Data
@Repository
public class MissaoProgressoInfraRepository implements MissaoProgressoRepository { /* ... */ }
```

**When to use**: toda nova entidade. ApplicationServices **nunca** importam `*SpringDataJpaRepository`.

## Strategy Pattern para Processadores

**Category**:  Services / Application

**Evidence**: 5 chains —
- `comunicacao/application/service/whatsapp/processadores/`
- `gameficacao/catalogo/memberkit/application/service/processadores/`
- `gameficacao/progresso/progressowakander/application/service/progresso/processadores/`
- `jornadawakander/application/service/discord/processadores/`
- `jornadawakander/application/service/memberkit/`

**Example**:
```java
public interface XxxProcessor {
    boolean validaSeProcessa(TipoEvento tipo);
    void processaEvento(EventoDto evento);
}
// orquestrador injeta List<XxxProcessor> e seleciona:
processors.stream().filter(p -> p.validaSeProcessa(evento.getTipo())).findFirst().orElseThrow();
```

**When to use**: qualquer "tipo" com comportamento próprio. Novo tipo = nova `@Component`, zero modificação no existente.

## Domínio Rico (lógica na entidade)

**Category**: Domain

**Evidence**:
- `gameficacao/xp/xpwakander/domain/XpWakander.java` (`adicionarXpEAtualizarNivel`)
- `wakander/domain/Wakander.java` (15+ métodos comportamentais)
- `gameficacao/catalogo/missaowakanda/domain/MissaoWakanda.java`

**Example**:
```java
public void adicionarXpEAtualizarNivel(Integer xpObtido, Sabedorias sabedorias) {
    adicionaXp(xpObtido);
    atualizaNivel();               // while(podeSubirDeNivel())
    atualizaSabedoriasDoWakander(sabedorias);
}
```

**When to use**: cálculos e invariantes ficam no domínio, não no service.

## Value Objects com factory validadora

**Category**: Domain

**Evidence**: `OrdemMissao.criar(int)` (valida `ordem >= 0`); VOs embarcados em `Wakander`
(`WakanderContato`, `WakanderFinanceiro`, `WakanderFiador`, `WakanderAulaAssistida`).

**When to use**: encapsular invariantes de valor; usar `@Embedded` para compor a entidade raiz.

## Agregação por UUID (sem associações JPA)

**Category**: Database

**Evidence**: ausência de `@OneToMany`/`@ManyToOne` entre agregados; navegação por busca explícita
no repositório (documentado em `arquitetura-gameficacao.md` §2).

**When to use**: cruzar agregados → guardar UUID e buscar no port. Reduz acoplamento de carga.

## Eventos assíncronos SNS → SQS FIFO

**Category**: Messaging

**Evidence**: `comunicacao/infra/PublicadorNotificacaoInfraSns.java`; consumers `XpWakanderConsumer`,
`ProgressoWakanderConsumer`, `JornadaWakanderConsumerSqs`, `ComunicacaoConsumerSqs`.

**Example**:
```java
SnsNotification<T> notification = SnsNotification.<T>builder(payload)
        .deduplicationId(UUID.randomUUID().toString())  // ⚠️ idempotência fica no consumer
        .groupId(groupId).build();
snsOperations.sendNotification(topic, notification);
```

**When to use**: comunicação entre módulos. Sempre `*.fifo` + DLQ. Checar estado no handler para idempotência.

## Tratamento de erros via APIException

**Category**: Error Handling

**Evidence**: `handler/APIException.java` + `handler/RestResponseEntityExceptionHandler.java`;
uso difundido no domínio (`Wakander.java` lança `APIException.build(HttpStatus..., msg)`).

**Example**:
```java
throw APIException.build(HttpStatus.BAD_REQUEST, "Wakander não está regularizado!");
```

**When to use**: validações de negócio que devem virar resposta HTTP — não lançar exceptions genéricas.

## Naming convention por papel

**Category**: Application

**Evidence**: `*Api` (controllers), `*ApplicationService implements *Service`, `*Repository` (port),
`*InfraRepository` (adapter), `*SpringDataJpaRepository`, `*Consumer/*ConsumerSqs`, `*Client/*InfraClient`,
`*Processor/*Processador`.

**When to use**: nomear novos arquivos seguindo o papel para manter navegabilidade e DI consistente.

---
**Last Updated**: 2026-06-10 (reverse-engineering inicial)
