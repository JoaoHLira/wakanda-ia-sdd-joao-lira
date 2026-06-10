# Project Configuration
# Only contains overrides. Properties not listed use framework defaults.
# See defaults in: sdd-kit/framework/standards/coding-standards.md

## Backend Conventions

architecture:
  pattern: ddd            # Clean Architecture + DDD + Event-Driven (ports/adapters, domain logic in entities)

## Quality Gates

coverage:
  min_coverage: 95        # Override: team requires 95% (project JaCoCo gate is >= 80%, team raised it)

## Language

language:
  specs: pt               # Specifications written in Portuguese
