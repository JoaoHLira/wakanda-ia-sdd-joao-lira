# Complete Endpoint Reference (17 Controllers, 72+ Endpoints)

## 1. WakanderAPI — `/wakander`
Base path: `/wakander`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/wakander/novo-wakander` | 201 | permitAll |
| PATCH | `/wakander/{idWakander}/regulariza` | 204 | permitAll |
| PATCH | `/wakander/{idWakander}/progresso/jornada-conhecimento` | 204 | permitAll |
| PATCH | `/wakander/{idWakander}/progresso/jornada-habilidade` | 204 | permitAll |
| PATCH | `/wakander/{idWakander}/progresso/jornada-conquista` | 204 | permitAll |
| PATCH | `/wakander/{idWakander}/progresso/vibraniun` | 204 | permitAll |
| PATCH | `/wakander/{idWakander}/edita-wakander` | 204 | permitAll |
| GET | `/wakander/{idWakander}` | 200 | permitAll |
| GET | `/wakander/dado-oculto/{token}` | 200 | permitAll |
| GET | `/wakander/busca-wakanders` | 200 | ROLE_DEV |
| GET | `/wakander/busca-wakanders/{statusCadastro}` | 200 | ROLE_DEV |
| PATCH | `/wakander/dados-wakander` | 200 | permitAll |
| PATCH | `/wakander/envia-formularios-wakanders` | 200 | permitAll |
| PATCH | `/wakander/atualiza-status-cadastro` | 200 | ROLE_LIDERANCA |
| GET | `/wakander/estatistica-wakanders` | 200 | ROLE_DEV |
| PATCH | `/wakander/atualiza-dados-asaas` | 200 | ROLE_LIDERANCA |
| PATCH | `/wakander/cadastro/{token}` | 200 | permitAll |
| PATCH | `/wakander/{idWakander}/cancela-assinatura` | 204 | permitAll |
| PATCH | `/wakander/{idWakander}/reverte-cancelamento` | 204 | permitAll |
| POST | `/wakander/{idWakander}/fiador/atualizacao-link` | 201 | ROLE_LIDERANCA |
| PATCH | `/wakander/{idWakander}/inicia-onboarding-manual` | 204 | ROLE_LIDERANCA |
| GET | `/wakander` | 200 | permitAll |

## 2. AutenticacaoApi — `/autenticacao`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/autenticacao/cadastro` | 201 | permitAll |
| POST | `/autenticacao/login` | 200 | permitAll |
| GET | `/autenticacao/token-teste` | 200 | ROLE_DEV |
| PATCH | `/autenticacao/reativa-token/{token}` | 200 | permitAll |

## 3. CobrancaAPI — `/financeiro/cobranca`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/financeiro/cobranca/processa-evento` | 204 | permitAll |

## 4. AssinaturaAPI — `/financeiro/assinaturas`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/financeiro/assinaturas` | 204 | permitAll |
| PATCH | `/financeiro/assinaturas/fiador/{token}` | 200 | permitAll |

## 5. ComunicacaoApi — `/whatsapp-message`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/whatsapp-message/envia` | 204 | permitAll |
| POST | `/whatsapp-message/convida` | 200 | permitAll |
| POST | `/whatsapp-message/publica-notificacao` | 200 | permitAll |

## 6. JornadaWakanderApi — `/wakander/jornada`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/wakander/jornada/associar-discord` | 200 | permitAll |

## 7. DashboardApi — `/painel-dados`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| GET | `/painel-dados/dashboard` | 200 | permitAll (Thymeleaf) |
| GET | `/painel-dados/login` | 200 | permitAll (Thymeleaf) |

## 8. FormularioApi — `/formulario`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| GET | `/formulario/cadastro/{token}` | 200 | permitAll (Thymeleaf) |
| GET | `/formulario/cancelamento-assinatura/{idWakander}` | 200 | permitAll (Thymeleaf) |
| GET | `/formulario/dados-complementares/{token}` | 200 | permitAll (Thymeleaf) |
| GET | `/formulario/{username}/{idDiscord}/associar-discord` | 200 | permitAll (Thymeleaf) |
| GET | `/formulario/resposta-discord` | 200 | permitAll (Thymeleaf) |
| GET | `/formulario/atualiza-fiador/{token}` | 200 | permitAll (Thymeleaf) |
| GET | `/formulario/resposta-atualiza-fiador` | 200 | permitAll (Thymeleaf) |

## 9. MissaoWakandaAPI — `/missoes`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/missoes` | 201 | ROLE_DEV |
| PATCH | `/missoes/{idMissao}/atualiza-missao-com-ia` | 204 | ROLE_DEV |
| GET | `/missoes/{idMissao}` | 200 | ROLE_DEV |
| PATCH | `/missoes/{idMissao}/status/desativar` | 200 | ROLE_DEV |
| PATCH | `/missoes/{idMissao}/pontuacao` | 204 | ROLE_DEV |
| PATCH | `/missoes/{idMissao}/ordem/{posicao}` | 204 | ROLE_DEV |
| GET | `/missoes/{idJornada}/missoes` | 200 | ROLE_DEV |

## 10. TrilhaWakandaAPI — `/trilhas`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/trilhas` | 201 | ROLE_DEV |
| GET | `/trilhas/{idTrilha}` | 200 | ROLE_DEV |
| GET | `/trilhas` | 200 | ROLE_DEV |

## 11. JornadaWakandaAPI — `/jornadas`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/jornadas` | 201 | ROLE_DEV |
| GET | `/jornadas/{idTrilha}/lista-jornadas` | 200 | ROLE_DEV |
| GET | `/jornadas/{idJornada}` | 200 | ROLE_DEV |
| GET | `/jornadas` | 200 | ROLE_DEV |

## 12. TipoMissaoAPI — `/tipos-missao`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/tipos-missao` | 201 | ROLE_DEV |

## 13. MemberkitController — `/memberkit`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/memberkit` | 200 | permitAll (webhook) |
| POST | `/memberkit/import` | 204 | ROLE_DEV |

## 14. ProgressoWakanderApi — `/gameficacao/progresso`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/gameficacao/progresso/{idWakander}` | 201 | permitAll |
| GET | `/gameficacao/progresso/ranking-wakanders` | 200 | permitAll |
| POST | `/gameficacao/progresso/sincroniza-antigos` | 200 | ROLE_DEV |
| POST | `/gameficacao/progresso/sincroniza-antigo/{idWakander}` | 200 | ROLE_DEV |
| GET | `/gameficacao/progresso/{idWakander}` | 200 | permitAll |

## 15. MissaoProgressoApi — `/gameficacao/missao-progresso`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/gameficacao/missao-progresso` | 201 | permitAll |
| PATCH | `/gameficacao/missao-progresso/{idMissaoProgresso}/conclui` | 204 | permitAll |
| GET | `/gameficacao/missao-progresso/wakander/{idWakander}/jornada/{idJornada}` | 200 | permitAll |
| GET | `/gameficacao/missao-progresso/progresso/{idProgressoWakander}/missoes-concluidas` | 200 | permitAll |

## 16. ClasseWakandaAPI — `/classes`
| Method | Path | Status | Auth |
|--------|------|--------|------|
| POST | `/classes` | 201 | ROLE_DEV |
| GET | `/classes/{idClasse}` | 200 | ROLE_DEV |
| GET | `/classes/ativas` | 200 | ROLE_DEV |
| PATCH | `/classes/{idClasse}` | 204 | ROLE_DEV |

## 17. GameficacaoViewController (Thymeleaf)
| Method | Path | Auth |
|--------|------|------|
| GET | `/gameficacao/home` | permitAll |
| GET | `/gameficacao/catalogo/missoes` | permitAll |
| GET | `/gameficacao/missoes/{idMissao}` | permitAll |
| GET | `/gameficacao/ProgressoWakanders` | permitAll |
| GET | `/gameficacao/progresso` | permitAll |

## Swagger
| Method | Path |
|--------|------|
| GET | `/swagger-ui/index.html` | permitAll |
| GET | `/v3/api-docs` | permitAll |
