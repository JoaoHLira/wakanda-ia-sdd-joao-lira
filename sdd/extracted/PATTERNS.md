# Project Patterns — WakandaAI

**Generated**: 2026-06-17
**Total Patterns**: 12

---

## HTTP/API

### Pattern 1: Dual Repository (Ports/Adapters)

**Category**: HTTP/API

**Evidence**: Used across ALL entities:
- `WakanderService` (port interface) + `WakanderInfraRepository` (adapter) + `WakanderSpringDataJpaRepository` (Spring Data)
- `CobrancaService` + `CobrancaInfraRepository` + `CobrancaSpringDataJpaRepository`
- Same pattern in: MissaoWakanda, JornadaWakanda, TrilhaWakanda, ClasseWakanda, etc.

**When to use**: Always create three layers for data access: Port interface in `application/service/`, Spring Data JPA in `infra/`, Adapter implementing the port in `infra/`.

---

### Pattern 2: Controller Naming Convention

**Category**: HTTP/API

**Evidence**: 
- REST: `*Api.java` suffix (`WakanderAPI.java`, `CobrancaAPI.java`, `MissaoWakandaAPI.java`)
- Thymeleaf: `*ViewController.java` suffix (`GameficacaoViewController.java`)
- Paths: lowercase plural (`/wakander`, `/missoes`, `/classes`, `/trilhas`)

**When to use**: Follow `*Api` for REST controllers, `*ViewController` for Thymeleaf.

---

## Messaging

### Pattern 3: SNS → SQS FIFO with DLQ

**Category**: Messaging

**Evidence**: 
- `zapi-requests-topic.fifo` + `zapi-requests` queue + `zapi-requests-dlq` DLQ
- Same pattern for all 8 topics: `memberkit`, `asaas`, `discord`, `progresso-wakander`, `xp-wakander`, `clint`, `clint-contato`

**When to use**: Every async flow uses SNS → SQS FIFO with a corresponding DLQ. Topics named `*-requests-topic.fifo`, queues named after the topic.

---

### Pattern 4: Single SQS Consumer Class

**Category**: Messaging

**Evidence**: `ComunicacaoConsumerSqs.java` handles 12 out of 15 SQS queues (zapi, memberkit-dlq, asaas, clint, discord, etc.)

**When to use**: Route multiple related queues through a single consumer class rather than creating one per queue.

---

## Error Handling

### Pattern 5: Global Exception Handler

**Category**: Error Handling

**Evidence**: `handler/RestResponseEntityExceptionHandler.java` — centralized `@RestControllerAdvice`

**When to use**: Use a single `@RestControllerAdvice` for all REST exception handling.

---

## Strategy

### Pattern 6: Processor Chain (Strategy Pattern)

**Category**: Error Handling

**Evidence**: 5 chains following identical pattern:
```java
public interface XxxProcessor {
    boolean validaSeProcessa(TipoEvento tipo);
    void processaEvento(EventoDto evento);
}
```
- WhatsApp: `ComunicacaoWhatsappApplicationService` + 4 processors
- Memberkit (gamificação): `MemberkitApplicationService` + 4 processors
- Memberkit (jornada): `JornadaWakanderService` + 4 processors
- Discord: `DiscordApplicationService` + 2 processors
- Cobranca: `CobrancaAsaasApplicationService` + 4 processors

**When to use**: For any event type with variant behavior, create an interface with `validaSeProcessa()` + `processaEvento()` and inject `List<XxxProcessor>`. Select via `.filter(p -> p.validaSeProcessa(tipo)).findFirst()`.

---

## Domain

### Pattern 7: Value Objects with Static Factory

**Category**: Error Handling

**Evidence**: 
- `OrdemMissao.criar(int ordem)` — validates `ordem >= 0`
- `Sabedorias` — 5-dimension knowledge VO used across MissaoWakanda, XpWakander, ClasseWakanda, MissaoProgresso

**When to use**: Create static factory methods (`criar`, `of`, `from`) that validate invariants instead of public constructors.

---

### Pattern 8: Aggregation by UUID

**Category**: Error Handling

**Evidence**: No `@OneToMany`/`@ManyToOne` between aggregate entities. All cross-entity references use UUID fields. Navigation requires explicit repository lookup:
- `Wakander.idWakander` referenced in `Cobranca.idWakander`, `ProgressoWakander.idWakander`, etc.
- `MissaoWakanda.idJornada` references `JornadaWakanda.idJornada`

**When to use**: Between aggregate roots, use UUIDs as foreign keys, not JPA relationships.

---

## Transaction

### Pattern 9: @TransactionalEventListener(AFTER_COMMIT)

**Category**: Error Handling

**Evidence**: 
- `AssinaturaCanceladaConsumer` — listens for cancellation events after commit
- `ProgressoOnboardConsumer` — listens for onboarding progress events

**When to use**: For domain events that should not rollback if the listener fails, use `@TransactionalEventListener(phase = AFTER_COMMIT)`.

---

## Security

### Pattern 10: SecurityFilter with JWT

**Category**: Security

**Evidence**: `config/security/SecurityFilter.java` — `OncePerRequestFilter` that:
1. Extracts `Authorization: Bearer <token>` header
2. Validates JWT via `TokenService`
3. Sets `SecurityContextHolder` with `UsernamePasswordAuthenticationToken`

**When to use**: For JWT-based stateless auth, extend `OncePerRequestFilter` and add before `UsernamePasswordAuthenticationFilter`.

---

## Testing

### Pattern 11: WireMock for External Services

**Category**: Testing

**Evidence**: WireMock stubs in `test/resources/stub/` for:
- `z-api/` — WhatsApp API
- `asaas/` — Asaas payment API
- `memberkit/` — Memberkit LMS
- `discord/` — Discord API

**When to use**: Always use WireMock for HTTP external integrations in integration tests. Never mock the HTTP layer at the client level.

---

### Pattern 12: FixtureFactory for Test Data

**Category**: Testing

**Evidence**: FixtureFactory used across test classes for generating test entities with realistic data.

**When to use**: Use FixtureFactory for domain entity creation in tests. Avoid manual instance creation.
