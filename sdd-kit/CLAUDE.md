# SDD Kit — Spec-Driven Development

You are assisting a developer who is using the **SDD Kit** framework for Spec-Driven Development.

## What is SDD Kit?

SDD Kit is a methodology framework that helps teams build software predictably using AI coding assistants. It enforces a structured workflow:

1. **Functional Spec** (WHAT) — user experience, user stories, acceptance criteria
2. **Technical Spec** (HOW) — architecture, APIs, data models, design decisions
3. **Tasks** — granular implementation tasks with dependencies (`tasks.json`)
4. **Implementation** — code generation with mandatory quality gates

## Your Role

- Guide the developer through the SDD workflow using the `sdd.*` commands
- Always check `sdd/wip/` for active features before starting new work
- Read `sdd/wip/<feature>/meta.md` to understand current phase before acting
- Respect the framework structure — never skip phases

## Available Commands

| Command | Purpose |
|---------|---------|
| `/sdd.go` | **Express mode** — orchestrates the full workflow automatically |
| `/sdd.start` | Initialize a new feature |
| `/sdd.spec` | Create functional and/or technical specs |
| `/sdd.plan` | Generate implementation tasks from approved specs |
| `/sdd.build` | Implement tasks with quality gates |
| `/sdd.finish` | Validate and archive completed feature |
| `/sdd.check` | View feature status and consistency |
| `/sdd.fix` | Fix errors across spec layers |
| `/sdd.list` | List all features |
| `/sdd.backlog` | Manage TODO/DEBT/IDEA backlog |
| `/sdd.hub` | Coordinate multi-app features |
| `/sdd.import` | Import existing specs |
| `/sdd.reverse-eng` | Document existing codebase |
| `/sdd.project` | View/edit PROJECT.md |
| `/sdd.cancel` | Cancel current feature |
| `/sdd.rollback` | Rollback to previous phase |
| `/sdd.doctor` | Diagnose configuration issues |
| `/sdd.help` | Framework help |

## Working Directory Structure

```
sdd/
├── wip/                    # Features in progress
│   └── YYYYMMDD-feature/
│       ├── meta.md         # Feature metadata + phase tracking
│       ├── functional-spec.md
│       ├── technical-spec.md
│       ├── tasks.json
│       └── progress.md
└── features/               # Completed/archived features
```

## Core Principles

- **Specs before code** — never implement without an approved technical spec
- **Small, focused tasks** — each task has clear acceptance criteria
- **Quality gates** — code review and tests before marking tasks done
- **Language awareness** — respond in the same language the developer uses

## Skills Available

- `sdd-kit-expert` — framework knowledge and guidance
- `sdd-code-reviewer` — code review quality gate
- `sdd-validator` — build and test validation
- `sdd-performance-expert` — performance analysis
- `context-guardian` — context budget monitoring
- `java-spring-expert` — Java + Spring Boot patterns
- `python-expert` — Python (FastAPI / Flask / Django) patterns

## Agents Available

Specialized subagents are invoked automatically during `sdd.build` and `sdd.spec`:
- `sdd-implementer`, `sdd-system-designer`, `sdd-explorer`
- `sdd-small-test-writer`, `sdd-large-test-writer`
- `sdd-debugger`, `sdd-backlog-manager`, `sdd-layer-analyzer`
- `sdd-project-wizard`, `sdd-validator-runner`
