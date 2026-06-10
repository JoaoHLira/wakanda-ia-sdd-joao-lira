# Detection Report

**Generated**: 2026-06-10
**Repository**: wakanda-ai (WakandaAI)

## Extraction Scope

**Mode**: FULL
**Focus Component**: Full Repository

## Detected Frameworks

| Framework | Confidence | Files Found |
|-----------|------------|-------------|
| Claude Code | 🟡 Medium | `CLAUDE.md` (rich project instructions) |
| Plain Docs | 🟡 Medium | `docs/arquitetura-gameficacao.md` (deep architecture analysis of gamification) |
| SDD Kit | 🟢 High | `sdd-kit/` framework installed (no prior specs in `sdd/specs/`) |

No OpenAPI/Swagger spec file, no `.fury`, no OpenSpec/Kiro/Tessl/Cursor artifacts found.
MeliSystemMCP /  not available in this environment → actor discovery via code + docs only.

## Selected Strategy

**Strategy**: ASSISTED
**Rationale**: Plain Docs (`arquitetura-gameficacao.md`) + Claude Code (`CLAUDE.md`) provide
high-quality architectural hints. These were used to corroborate code findings, but **code
remains the source of truth** per the Anti-Invention Protocol.

## Detected Specs Summary

| Spec Type | Location | Notes |
|-----------|----------|-------|
| Architecture doc | `docs/arquitetura-gameficacao.md` | Gamification module only — used as VERIFIED corroboration |
| Project conventions | `CLAUDE.md` | Stack, layering, naming, known debt |

## Extraction History

| Date | Mode | Focus | Summary |
|------|------|-------|---------|
| 2026-06-10 | FULL | - | Initial extraction: 17 controllers, 18 entities, 4 SQS consumers, ~22 processors, 67 migrations |

## Recommendations

- Generate an OpenAPI export (`/v3/api-docs`) and commit it to enable API_ANCHORED re-extraction.
- Address the gamification debt documented in `arquitetura-gameficacao.md` §9–10 before adding
  polymorphic mission types.
