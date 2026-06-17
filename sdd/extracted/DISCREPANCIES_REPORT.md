# Discrepancies Report

**Generated**: 2026-06-17

## Source Priority
1. **CODE** — Source of truth
2. **Existing docs** — May be stale

## Discrepancies Found

| # | Severity | Item | Code vs Docs | Details |
|---|----------|------|-------------|---------|
| 1 | INFO | OpenAPI spec | Missing in docs | No standalone OpenAPI file exists — SpringDoc generates at runtime |
| 2 | INFO | Migration filename spacing | Trailing spaces | Some migration files have trailing spaces in names |

## Verification Notes

- All 17 controllers verified in code
- All 72+ endpoints verified in code
- All 18 entities verified in code
- All 68 migrations verified in filesystem
- All 15 SQS consumers verified in code
- All external integrations verified against HTTP client implementations
