# Epic 1 final audit

> **Documento histórico.** Esta auditoria registra o fechamento do Épico 1 a partir de `c416d69` mais alterações locais posteriormente consolidadas no commit `5ccd280`. Consulte [CURRENT_STATE.md](CURRENT_STATE.md) para o estado canônico atual.

## 1. Objective and audited state

This audit closes Epic 1 — public API by comparing controllers, application use cases, domain ports, persistence, validation, security, HTTP contracts, OpenAPI, tests, and documentation.

- Audited branch: `main`.
- Audited commit: `c416d69` (`feat: standardize error handling for unmapped public routes and unsupported methods`).
- Audited state: that commit plus the preserved, uncommitted RF-PRO-008 implementation already present when this audit began.
- Runtime baseline: Java 21, Spring Boot 4.1, PostgreSQL 18, Flyway, Spring Data JPA, Spring Security, Springdoc OpenAPI, Testcontainers 2.0.2, and Maven Wrapper 3.8.7.

No existing local change was reverted, overwritten, committed, or pushed.

## 2. Scope

The audit covers the four current public operations, related technical endpoints, the default-deny boundary, public error handling, PostgreSQL repositories, Flyway V1–V3, and their tests and documentation.

Authentication, administrative CRUD, referral links, tracking, analytics, service requests, production profiles, and real-data onboarding are explicitly outside this epic.

## 3. Endpoint matrix

| Method and route | Controller | Application use case | 200 contract | Validation and errors | Security | Evidence | Final state |
|---|---|---|---|---|---|---|---|
| `GET /api/v1/public/status` | `PublicStatusController` | Fixed status; no domain use case | `data.application`, `data.status` | 405 for unsupported method | Public | Filter-chain and OpenAPI tests; `API_CONTRACT.md` | Accepted |
| `GET /api/v1/public/categories` | `PublicCategoryController` | `ListPublicCategoriesUseCase` | `data` list of minimized categories | Empty list allowed; 405 | Public | Application, MVC, security, OpenAPI, PostgreSQL | Accepted |
| `GET /api/v1/public/categories/{slug}` | `PublicCategoryController` | `GetPublicCategoryDetailsUseCase` and `ListProfessionalsByCategoryUseCase` | Minimized category and active professionals | Slug pattern; 400, indistinguishable missing/inactive 404, 405 | Public | Application, MVC, error, security, OpenAPI, PostgreSQL | Accepted |
| `GET /api/v1/public/professionals/{publicId}` | `PublicProfessionalController` | `GetPublicProfessionalDetailsUseCase` | Minimized active profile and active categories | Shared 21-character pattern; 400, indistinguishable missing/inactive 404, 405 | Public | Application, MVC, security, OpenAPI, PostgreSQL | Accepted |
| `GET /actuator/health` | Actuator | Actuator health contributors | Actuator health document | Other Actuator endpoints unavailable | Public GET only | Full-context technical endpoint test | Accepted for local/current scope |
| `GET /v3/api-docs` | Springdoc | Generated document | OpenAPI document | Non-GET denied | Public GET only | Full-context structural assertions | Accepted for local/current scope |
| `GET /swagger-ui/index.html` | Springdoc UI | Static UI | Swagger UI | Non-GET denied | Public GET only | Full-context security test | Accepted for local/current scope |
| `/api/v1/admin/**` | No admin controller | None | None | Always 403 at filter chain | `denyAll()` | Real filter-chain GET/POST/unknown-route tests | Accepted |

## 4. Implemented requirements

The accepted current public API covers RF-PUB-001–006, RF-CAT-001/002, RF-PRO-001/008/009, and the public-read portion supported by RF-PRO-010/RF-ASC-003.

- Category list and lookup return active records only.
- Category professionals are active and ordered by name.
- Individual profiles return active professionals only.
- Profile categories are active and ordered by name.
- Empty category/professional collections remain valid 200 responses.
- Explicit response records prevent JPA entities and private fields from being serialized.
- Internal IDs, `active`, private phone, email, password/hash, timestamps, and administrative fields are absent.
- Success resources use the `data` envelope. Status intentionally uses the same envelope with a fixed map.

## 5. Persistence findings

Category persistence uses derived Spring Data queries with `active = true` and name ordering. Category lookup filters active records by slug.

Professional category listing uses one join query that filters active category and professional rows and orders professionals by name. Individual profile loading uses one active-professional query followed by one ordered active-category projection query. It does not introduce an `EAGER` association or N+1 behavior, and the second query is not executed when the first returns empty.

Integration tests use PostgreSQL from Testcontainers, the real adapters, Hibernate schema validation, Flyway, and migrations V1, V2, and V3. They do not use H2 or an external local database. Transactional mutations isolate inactive and relationship scenarios from test order.

## 6. Error and security findings

`ApiExceptionHandler` gives deterministic Problem Details for validation/constraint violations (400), missing domain resources and MVC handlers/resources (404), unsupported methods (405 with preserved `Allow`), and unexpected failures (500). Responses use `application/problem+json`, `about:blank`, safe titles/details, and contain no stack trace, internal exception name, or local path.

Security is default deny. Current public paths are permitted, administration is `denyAll()`, unknown non-public routes are denied, technical documentation is GET-only, and Actuator exposes health only. There is no functional login, Basic Auth, form login, JWT, refresh token, OAuth flow, or default user/password. The empty user manager suppresses Spring Boot's generated credential.

CSRF is disabled and the default session policy remains `IF_REQUIRED`; current public flows do not authenticate or intentionally create sessions. CORS uses two exact local origins, no wildcard, and no credentials. Unknown origins are rejected.

## 7. Test coverage

Evidence is classified as follows:

- Application: category list/detail, professional list/profile, missing and inactive behavior, ordering and empty collections.
- Controller/MVC: exact success fields, private-field omission, 400/404/405, Problem Details, and `Allow`.
- Error handler: constraint violations, missing MVC resources, and unsupported methods.
- Security/CORS: public access, administration/default denial, empty user store, exact/unknown origins, preflight behavior, technical GET-only access.
- OpenAPI: all four paths, GET methods, summaries, parameter examples/patterns, expected response codes, public schemas, and absence of JPA/admin exposure.
- PostgreSQL repositories: active filters, ordering, slug/public ID lookup, mapping, inactive/missing records, empty relationships, and N:N behavior.
- Migrations: successful V1, V2, and V3 history against PostgreSQL 18.

No test relies on execution order or a previous database. No duplicated test was added solely to raise the count.

## 8. Findings and corrections

### Blocking findings corrected

1. README and product vision still classified the individual public profile as absent. They now reflect RF-PRO-008.
2. Security documentation still claimed unmapped/unsupported public MVC requests became 500. It now reflects the implemented 404/405 behavior.
3. OpenAPI metadata did not explicitly describe status and omitted 400/405 responses on category operations. Minimal annotations and structural OpenAPI assertions now cover the actual contract.
4. Roadmap and traceability did not yet record formal Epic 1 acceptance. They now point to this audit and the completed evidence.

### Non-blocking findings

- `PublicStatusController` returns a fixed typed `Map`. Its shape is stable, documented, tested, and enveloped consistently; creating a DTO would be aesthetic rather than corrective.
- `/api/v1/public/**` permits every method to reach MVC. Today, unmapped methods/routes remain safe and tested. The prefix may automatically expose a future controller and therefore requires review when adding public routes.
- The default `IF_REQUIRED` session policy and disabled CSRF are coherent with the unauthenticated current API but must be redesigned with administrative identity.
- Swagger/Actuator production restriction, demo-data separation, production configuration, pagination, and photos remain later decisions.

### Open blocking findings

None within Epic 1.

## 9. Explicitly outside Epic 1

- Administrative authentication, JWT, refresh and logout.
- Category/professional/association CRUD.
- Referral links and contextual referral access.
- Visits, contact-click tracking, analytics, and administrative audit.
- Service requests.
- Production profile, real data, secret management, backup/restore, deployment, and observability.

## 10. Validation and acceptance

The final Maven and Git validation results are recorded after the single final `clean verify` execution. Epic 1 is accepted only with all tests passing, PostgreSQL/Flyway evidence present, no blocking finding open, and `git diff --check` clean.

**Decision:** `ÉPICO 1 CONCLUÍDO`.
