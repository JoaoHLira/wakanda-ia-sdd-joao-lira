# Architecture Overview

## Package Structure
```
academy.wakanda.wakanda_ai/
├── WakandaAiApplication.java      # @SpringBootApplication, @EnableAsync, @EnableScheduling
├── autenticacao/                    # Auth (JWT)
├── comunicacao/                     # WhatsApp, Discord, Clint
├── config/                          # Security, AWS, Discord, Swagger
├── constants/                       # Topic names, messages
├── financeiro/                      # Asaas payments
├── frontend/                        # Thymeleaf views
├── gameficacao/                     # XP, classes, missions
├── jornadawakander/                 # Onboarding, lessons
├── wakander/                        # Core student entity
├── handler/                         # Global exception handler
└── utils/                           # Utilities
```

## Architecture Pattern
Clean Architecture + DDD + Event-Driven:
- Controllers (application/api/) → ApplicationServices → Domain → Repository Port → Infra Adapter
- Async via SNS → SQS FIFO
- Sync events via Spring @TransactionalEventListener

## Key Patterns
- Ports & Adapters (dual repository pattern)
- Strategy Pattern (5 processor chains)
- Event-Driven (SNS → SQS FIFO)
- Domain Events (Spring events)
- Scheduled tasks (@Scheduled)
