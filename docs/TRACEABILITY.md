# Traceability matrix

This matrix connects product objective → functional requirement → business rule → use case/endpoint → expected test → state. Detailed flows and acceptance criteria are in [Scope and requirements](SCOPE_AND_REQUIREMENTS.md); endpoint semantics are in [API contract](API_CONTRACT.md).

| Objective | Requirement | Rule | Use case / endpoint | Expected test | State |
|---|---|---|---|---|---|
| Show service readiness | RF-PUB-001 | — | `PublicStatusController`; `GET /public/status` | Controller + real filter-chain security | `IMPLEMENTADO` |
| Expose dependency health | RF-PUB-002 | — | Actuator health | Full-context health + sensitive endpoint denial; production hardening pending | `IMPLEMENTADO` |
| Discover implemented API | RF-PUB-003 | — | Springdoc/Swagger | Full-context paths, methods, response codes, validation patterns, public schemas, UI and method security; production restriction pending | `IMPLEMENTADO` |
| Standardize public category failures | RF-PUB-004 | RN-026 | `ApiExceptionHandler`; category details | Handler, controller, real MVC validation/security tests | `IMPLEMENTADO` |
| Protect representations | RF-PUB-005 | RN-001, RN-014 | Public response mappers | DTO omission tests for every public resource | `IMPLEMENTADO` |
| Standardize public MVC routing failures | RF-PUB-006 | — | `ApiExceptionHandler`; public MVC boundary | Handler + isolated/full MVC 404/405 and `Allow` tests | `IMPLEMENTADO` |
| Browse categories | RF-CAT-001 | RN-004 | `ListPublicCategoriesUseCase`; `GET /public/categories` | Unit/controller/security + PostgreSQL active filtering and name ordering | `IMPLEMENTADO` |
| Browse category details | RF-CAT-002, RF-PRO-001 | RN-003–RN-005 | `GetPublicCategoryDetailsUseCase`; `GET /public/categories/{slug}` | Unit/controller + PostgreSQL active/inactive/missing slug and mapping + professional integration + invalid slug | `IMPLEMENTADO` |
| Guarantee category address | RF-CAT-009 | RN-002, RN-003 | Future category writes | Unique/conflict/concurrency integration | `PARCIALMENTE_IMPLEMENTADO` |
| Manage categories | RF-CAT-003–007, RF-CAT-010 | RN-001, RN-004, RN-008, RN-016, RN-019 | Admin category endpoints | Unit/controller/security/JPA/audit | `PLANEJADO_MVP` |
| Curate category order | RF-CAT-008 | RN-025 | Endpoint/schema TBD | Ordering conflict/query tests | `DECISAO_PENDENTE` |
| Read/manage professionals | RF-PRO-002–007 | RN-005, RN-009, RN-016, RN-019 | Admin professional endpoints | Unit/controller/security/JPA/audit | `PLANEJADO_MVP` |
| Publish individual profile | RF-PRO-008,009 | RN-001, RN-005, RN-014 | `GetPublicProfessionalDetailsUseCase`; `GET /public/professionals/{publicId}` | Unit/controller/PostgreSQL/security/OpenAPI and DTO omission | `IMPLEMENTADO` |
| Support multiple categories | RF-PRO-010, RF-ASC-003 | RN-006, RN-007 | Current schema/query | Migration/JPA duplicate and join tests | `IMPLEMENTADO` |
| Manage associations | RF-ASC-001,002,005 | RN-006, RN-010, RN-019 | Association replacement endpoint | Transactional JPA/controller/audit | `PLANEJADO_MVP` |
| Curate professional order | RF-ASC-004 | RN-025 | Endpoint/schema TBD | Per-category ordering tests | `DECISAO_PENDENTE` |
| Authenticate administrator | RF-IDN-001–004 | RN-016, RN-018 | Login and `/me` | Password/service/controller/full security | `PLANEJADO_MVP` |
| Maintain revocable session | RF-IDN-005–007 | RN-016 | Refresh/logout/token store | Rotation/reuse/revocation/concurrency | `PLANEJADO_MVP` |
| Eliminate default credential | RF-IDN-008 | RN-018 | Controlled bootstrap/config | Production startup/configuration inspection | `PLANEJADO_MVP` |
| Create/manage referral links | RF-REF-001–003,005 | RN-002, RN-011, RN-013, RN-019 | Admin referral endpoints | Service/controller/security/JPA/audit | `PLANEJADO_MVP` |
| Apply expiration | RF-REF-004 | RN-012, RN-022 | Referral patch/public lookup | Clock/timezone boundary tests | `DECISAO_PENDENTE` |
| Open contextual referral | RF-REF-006,007 | RN-004, RN-005, RN-011–RN-015 | `GET /public/referrals/{publicId}` | Controller/JPA/security/scope | `PLANEJADO_MVP` |
| Record visit | RF-TRK-001,004,005 | RN-015, RN-020, RN-023 | Visits endpoint/application | Validation/JPA/time/privacy | `PLANEJADO_MVP` |
| Record contact intent | RF-TRK-002–005 | RN-015 | Contact-click endpoint/application | Relationship/channel/JPA tests | `PLANEJADO_MVP` |
| Resolve tracking policy | RF-TRK-006 | RN-020, RN-021, RN-023, RN-024 | Reliability/privacy design | Failure/retry/privacy tests after decision | `DECISAO_PENDENTE` |
| Show funnel overview | RF-ANL-001,002,007 | RN-017 | Analytics overview | Query reconciliation/period/zero tests | `PLANEJADO_MVP` |
| Analyze dimensions | RF-ANL-003–006 | RN-010, RN-015, RN-017 | Overview/link detail | Grouping/history/security integration | `PLANEJADO_MVP` |
| Audit admin lifecycle | RF-AUD-001–005 | RN-019 | Write use cases + `AuditEvent` | Transactional success/failure audit | `PLANEJADO_MVP` |
| Audit authentication | RF-AUD-006 | RN-018, RN-019 | Login/security events | Success/failure/no-secret tests | `PLANEJADO_MVP` |
| Decide service capture | — | RN-028 | `ServiceRequest`/endpoint TBD | Separate test plan only if approved | `DECISAO_PENDENTE` |
| Operate safely | RNF-SEC/PRIV/PERF/MAN/AVL/OBS families | RN-018–RN-030 as applicable | Cross-cutting platform | CI, security, load, restore, deploy, observability evidence | `PARCIALMENTE_IMPLEMENTADO` |
| Defer expanded marketplace | Explicit outside-MVP list | — | None | None in MVP | `FORA_DO_ESCOPO` |

## Status index

- `IMPLEMENTADO`: RF-PUB-001–006, RF-CAT-001/002, RF-PRO-001/008–010, RF-ASC-003.
- `PARCIALMENTE_IMPLEMENTADO`: RF-CAT-009 and cross-cutting production/privacy/error concerns beyond the completed current public API.
- `PLANEJADO_MVP`: all identity; admin category/professional/association; referral; most tracking/analytics/audit requirements.
- `DECISAO_PENDENTE`: RF-CAT-008, RF-ASC-004, RF-REF-004, RF-TRK-006, ServiceRequest, and decisions catalogued in `OPEN_DECISIONS.md`.
- `FUTURO`: post-MVP observability/evolution explicitly marked in the requirements and product vision.
- `FORA_DO_ESCOPO`: the first-MVP exclusions in `PRODUCT_VISION.md`.
