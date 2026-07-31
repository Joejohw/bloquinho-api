# Scope and requirements

## Reading guide

Allowed states: `IMPLEMENTADO`, `PARCIALMENTE_IMPLEMENTADO`, `PLANEJADO_MVP`, `FUTURO`, `DECISAO_PENDENTE`, `FORA_DO_ESCOPO`.

Priorities: P0 is required for a safe MVP; P1 is required to complete the intended MVP funnel; P2 is desirable but may follow after explicit scope review.

Each requirement row is traceable and contains: actor; precondition (`Pre`); main flow (`Main`); alternate flow (`Alt`); expected result (`Result`); related rules/endpoint; acceptance criteria (`AC`); and expected tests (`Tests`). Planned endpoint details are in [API contract](API_CONTRACT.md).

## Product scope

### Current baseline

`IMPLEMENTADO`: public status; active category list; active category details by slug; active professionals per category; individual active-professional profile with active categories; public DTOs; non-sequential 21-character ID generator; many-to-many category-professional schema; PostgreSQL/Flyway; explicit CORS; default-deny Spring Security; admin `denyAll()`; health/OpenAPI; Testcontainers integration.

`PARCIALMENTE_IMPLEMENTADO`: category/professional administration has schema but no CRUD; contacts are exposed but not mediated/tracked; `app_users` has no identity flow. The current public API contract and its MVC errors are standardized; future surfaces must extend that policy.

### MVP

`PLANEJADO_MVP`: administrative identity/session, category/professional/association management, curated ordering subject to decision, referral links, minimum visits/contact clicks, analytics, minimum audit, production security and operations.

### Explicitly outside the first MVP

`FORA_DO_ESCOPO`: client account, complete professional account, native mobile app, chat, proposals, quotes, documents, electronic signatures, payments, payment split, automatic commission, reviews, ranking, calendar, real-time geolocation, multitenancy, white-label, plans, recurring billing, microservices, Kafka, RabbitMQ, Redis, and Kubernetes.

Service requests are `DECISAO_PENDENTE`; see [Open decisions](OPEN_DECISIONS.md).

## Functional requirements

### Identity — 8 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-IDN-001 Authenticate administrator | `PLANEJADO_MVP` / P0 / Administrator | Pre: active user and valid secret. Main: submit credentials; verify hash; issue short access and refresh credentials. Alt: invalid input/credentials. Result: authenticated session without password disclosure. | RN-016, RN-018; `POST /admin/auth/login` | AC: 200 with public user/session data; no secret in response/log. Tests: unit hash/service, controller 200/400/401, PostgreSQL integration, security. |
| RF-IDN-002 Reject invalid credentials | `PLANEJADO_MVP` / P0 / System | Pre: login attempt. Main: compare safely. Alt: unknown email and wrong password return equivalent error. Result: 401 without enumeration. | RN-018; login | AC: same observable response for both failures. Tests: controller/security timing-safe behavior boundary. |
| RF-IDN-003 Block inactive user | `PLANEJADO_MVP` / P0 / System | Pre: matching inactive user. Main: refuse authentication. Alt: already-issued credentials follow revocation decision. Result: no new session. | RN-018; login | AC: inactive user receives 401/403 policy response and no tokens. Tests: unit, controller, integration. |
| RF-IDN-004 Read authenticated user | `PLANEJADO_MVP` / P0 / Administrator | Pre: valid access credential. Main: resolve principal and public user. Alt: missing/expired/revoked token. Result: current-user DTO. | RN-001, RN-016; `GET /admin/auth/me` | AC: 200 for valid token; 401 otherwise; no internal ID/hash. Tests: controller/security. |
| RF-IDN-005 Renew session | `PLANEJADO_MVP` / P0 / Administrator | Pre: valid unrevoked refresh token. Main: rotate/renew credentials. Alt: expired, reused, or revoked token. Result: new session credentials. | RN-016; `POST /admin/auth/refresh` | AC: valid token succeeds once under rotation policy; invalid token 401. Tests: unit, controller, persistence/concurrency. |
| RF-IDN-006 Logout | `PLANEJADO_MVP` / P0 / Administrator | Pre: refresh/session context. Main: revoke session token. Alt: already revoked token remains harmless. Result: session cannot renew. | RN-016; `POST /admin/auth/logout` | AC: idempotent policy documented; refresh fails afterward. Tests: service, controller, integration. |
| RF-IDN-007 Revoke refresh token | `PLANEJADO_MVP` / P0 / System | Pre: stored token/session. Main: mark revoked with timestamp/reason. Alt: absent token. Result: verifiable revocation history. | RN-016; refresh/logout | AC: revoked/expired tokens never issue access tokens. Tests: repository and race/concurrency cases. |
| RF-IDN-008 No default production credential | `PLANEJADO_MVP` / P0 / System | Pre: production startup. Main: require controlled bootstrap/external secret. Alt: missing bootstrap configuration fails safely. Result: no fixed user/password. | RN-018; none | AC: repository/image/config contains no production credential. Tests: configuration/security inspection and startup profile test. |

### Categories — 10 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-CAT-001 List public categories | `IMPLEMENTADO` / P0 / Visitor | Pre: API available. Main: query active categories ordered by name. Alt: none found. Result: 200 `data` list. | RN-004; `GET /public/categories` | AC: only active, no internal fields, empty list allowed. Tests: use-case/controller/security and PostgreSQL active filtering/order. |
| RF-CAT-002 Read public category by slug | `IMPLEMENTADO` / P0 / Visitor | Pre: syntactically valid slug. Main: find active category and active professionals. Alt: missing/inactive → 404; malformed → 400. Result: public details. | RN-003–RN-005; `GET /public/categories/{slug}` | AC: 200/400/404 and empty professional list. Tests: unit/controller/security plus PostgreSQL active/inactive/missing slug and public-field mapping. |
| RF-CAT-003 List administrative categories | `PLANEJADO_MVP` / P0 / Administrator | Pre: authenticated. Main: list active/inactive with bounded pagination/order. Alt: invalid page. Result: admin summaries. | RN-016; `GET /admin/categories` | AC: authorization, pagination metadata, no internal ID. Tests: controller/security/repository. |
| RF-CAT-004 Create category | `PLANEJADO_MVP` / P0 / Administrator | Pre: authenticated, valid unique name/slug. Main: validate, generate public ID, persist, audit. Alt: duplicate/invalid → 409/400. Result: 201 category. | RN-002, RN-003, RN-019; `POST /admin/categories` | AC: unique slug/public ID and audit event. Tests: unit/controller/JPA/security. |
| RF-CAT-005 Edit category | `PLANEJADO_MVP` / P0 / Administrator | Pre: existing public ID. Main: patch allowed fields and audit. Alt: absent 404; slug conflict 409. Result: updated category. | RN-003, RN-019; `PATCH /admin/categories/{publicId}` | AC: validation and immutable internal ID/history. Tests: unit/controller/integration. |
| RF-CAT-006 Activate category | `PLANEJADO_MVP` / P0 / Administrator | Pre: existing inactive category. Main: activate and audit. Alt: already active follows idempotent patch semantics. Result: public eligibility restored. | RN-004, RN-008, RN-019; category patch | AC: active category can be returned when otherwise valid. Tests: service/public integration/audit. |
| RF-CAT-007 Deactivate category | `PLANEJADO_MVP` / P0 / Administrator | Pre: existing active category. Main: deactivate without deleting and audit. Alt: already inactive. Result: unavailable publicly. | RN-008, RN-019; category patch | AC: immediately absent from list/details; history retained. Tests: service/public integration/audit. |
| RF-CAT-008 Order categories | `DECISAO_PENDENTE` / P1 / Administrator | Pre: manual ordering approved. Main: assign stable positions. Alt: duplicates/gaps normalized by policy. Result: curated order. | RN-025; category admin endpoint TBD | AC/tests depend on ordering decision; otherwise alphabetical remains. |
| RF-CAT-009 Enforce unique slug | `PARCIALMENTE_IMPLEMENTADO` / P0 / System | Pre: category write. Main: normalize/validate and enforce DB uniqueness. Alt: collision. Result: deterministic public address. | RN-003; category create/patch | AC: DB already unique; application conflict behavior remains planned. Tests: integration conflict/concurrency. |
| RF-CAT-010 Read administrative category | `PLANEJADO_MVP` / P0 / Administrator | Pre: authenticated and valid category public ID. Main: return administrative detail and associations. Alt: malformed ID 400; absent resource 404. Result: admin category DTO. | RN-001, RN-016; `GET /admin/categories/{publicId}` | AC: active/inactive detail without internal ID. Tests: controller/security/repository. |

### Professionals — 10 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-PRO-001 List by active category | `IMPLEMENTADO` / P0 / Visitor | Pre: active category slug. Main: join associations and active professionals ordered by name. Alt: none. Result: embedded public list. | RN-004–RN-006; category details | AC: inactive category/professional excluded. Tests: existing integration/controller. |
| RF-PRO-002 List administratively | `PLANEJADO_MVP` / P0 / Administrator | Pre: authenticated. Main: bounded list/filter. Alt: invalid page/filter. Result: admin page. | RN-016; `GET /admin/professionals` | AC: active/inactive visible to authorized admin only. Tests: controller/security/repository. |
| RF-PRO-003 Read administratively | `PLANEJADO_MVP` / P0 / Administrator | Pre: authenticated and public ID. Main: load admin detail. Alt: 404. Result: full permitted admin DTO. | RN-001, RN-016; `GET /admin/professionals/{publicId}` | AC: internal ID never exposed. Tests: controller/security. |
| RF-PRO-004 Create professional | `PLANEJADO_MVP` / P0 / Administrator | Pre: valid fields. Main: generate public ID, persist, audit. Alt: invalid/contact conflict policy. Result: 201. | RN-002, RN-019; `POST /admin/professionals` | AC: validated contacts and audit. Tests: unit/controller/JPA. |
| RF-PRO-005 Edit professional | `PLANEJADO_MVP` / P0 / Administrator | Pre: existing public ID. Main: patch allowed data and audit. Alt: 404/400. Result: updated resource. | RN-019; `PATCH /admin/professionals/{publicId}` | AC: partial update cannot expose/change internal ID. Tests: unit/controller/integration. |
| RF-PRO-006 Activate professional | `PLANEJADO_MVP` / P0 / Administrator | Pre: inactive professional. Main: activate and audit. Alt: already active. Result: eligible in active categories. | RN-005, RN-019; professional patch | AC: public visibility follows associations/categories. Tests: public integration/audit. |
| RF-PRO-007 Deactivate professional | `PLANEJADO_MVP` / P0 / Administrator | Pre: active professional. Main: deactivate without delete and audit. Alt: already inactive. Result: hidden everywhere. | RN-005, RN-009, RN-019; professional patch | AC: absent from every public category; history retained. Tests: integration/audit. |
| RF-PRO-008 Read individual public profile | `IMPLEMENTADO` / P1 / Visitor | Pre: valid active professional public ID. Main: return minimized profile and active categories ordered by name. Alt: malformed ID 400; missing/inactive 404; no active category returns an empty list; unsupported method 405. Result: public DTO. | RN-001, RN-005, RN-014; `GET /public/professionals/{publicId}` | AC: no private/admin fields or inactive categories; missing/inactive are indistinguishable. Tests: use case/controller/PostgreSQL/security/OpenAPI. |
| RF-PRO-009 Expose only public contacts | `IMPLEMENTADO` / P0 / System | Pre: category response. Main: map WhatsApp/Instagram; omit phone/email. Alt: optional values null. Result: minimized DTO. | RN-014; public category/profile | AC: existing controller proves omissions; extend profile tests. |
| RF-PRO-010 Support multiple categories | `IMPLEMENTADO` / P0 / Administrator/System | Pre: professional/category records. Main: persist N:N associations. Alt: duplicate rejected. Result: one professional in many categories. | RN-006, RN-007; association endpoint | AC: composite PK exists; management flow planned. Tests: migration/current query plus management integration. |

### Associations — 5 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-ASC-001 Associate professional/category | `PLANEJADO_MVP` / P0 / Administrator | Pre: both exist. Main: create association and audit. Alt: missing 404; duplicate 409/idempotent policy. Result: membership. | RN-006, RN-007, RN-019; `PUT /admin/professionals/{publicId}/categories` | AC: allowed set persisted atomically. Tests: service/JPA/controller/audit. |
| RF-ASC-002 Remove association | `PLANEJADO_MVP` / P0 / Administrator | Pre: association exists. Main: remove/deactivate according to history design and audit. Alt: absent association. Result: professional removed from category. | RN-010, RN-019; association PUT | AC: public query no longer returns membership. Tests: integration/audit. |
| RF-ASC-003 Prevent duplicate association | `IMPLEMENTADO` / P0 / System | Pre: same pair. Main: composite PK enforces uniqueness. Alt: write conflict. Result: one association per pair. | RN-007; association PUT | AC: schema constraint exists; application error mapping planned. Tests: migration/JPA conflict. |
| RF-ASC-004 Order within category | `DECISAO_PENDENTE` / P1 / Administrator | Pre: manual ordering approved. Main: assign per-category position. Alt: conflicts normalized. Result: curated list. | RN-025; association PUT | AC/tests depend on decision; current order is professional name ASC. |
| RF-ASC-005 Preserve association history | `PLANEJADO_MVP` / P1 / System | Pre: relevant change. Main: retain audit/history while public membership changes. Alt: privacy deletion obligation. Result: traceability without stale public display. | RN-010, RN-019; association PUT | AC: audit identifies actor/pair/action/time. Tests: audit integration. |

### Referral links — 7 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-REF-001 Create referral link | `PLANEJADO_MVP` / P1 / Administrator | Pre: authenticated and valid category selection. Main: generate public ID, persist lifecycle/categories, audit. Alt: invalid selection. Result: 201 link. | RN-002, RN-011–RN-013, RN-019; `POST /admin/referral-links` | AC: unpredictable ID and valid allowlist. Tests: unit/controller/JPA/security. |
| RF-REF-002 Edit referral link | `PLANEJADO_MVP` / P1 / Administrator | Pre: existing link. Main: patch label/categories/expiration and audit. Alt: 404/400. Result: updated link. | RN-012, RN-013, RN-019; `PATCH /admin/referral-links/{publicId}` | AC: updates are atomic and audited. Tests: service/controller/integration. |
| RF-REF-003 Activate/deactivate link | `PLANEJADO_MVP` / P1 / Administrator | Pre: existing link. Main: change active state and audit. Alt: same state. Result: public availability changes. | RN-011, RN-019; referral patch | AC: inactive link cannot open/track. Tests: public/security/integration. |
| RF-REF-004 Apply expiration | `DECISAO_PENDENTE` / P1 / Administrator/System | Pre: expiration policy and timestamp. Main: reject expired access. Alt: no expiration when optional. Result: lifecycle enforcement. | RN-012, RN-022; referral patch/public GET | AC: boundary/timezone tests after policy approval. |
| RF-REF-005 Select allowed categories | `PLANEJADO_MVP` / P1 / Administrator | Pre: active/existing categories. Main: replace allowlist. Alt: inactive/unknown category; empty semantics pending. Result: scoped link. | RN-013; referral create/patch | AC: public link exposes only allowed categories. Tests: transactional integration. |
| RF-REF-006 Open contextual catalog | `PLANEJADO_MVP` / P1 / Visitor | Pre: valid active unexpired link. Main: return link context and permitted active catalog. Alt: missing/inactive/expired 404/410 policy. Result: contextual DTO. | RN-004, RN-005, RN-011–RN-013; `GET /public/referrals/{publicId}` | AC: no unauthorized category/private field. Tests: controller/JPA/security/time. |
| RF-REF-007 Identify origin | `PLANEJADO_MVP` / P1 / System | Pre: referral access/event. Main: propagate referral public context to event. Alt: direct catalog has explicit “direct” origin policy. Result: attributable metrics. | RN-015; public referral/tracking | AC: every tracked referral event resolves origin. Tests: service/integration. |

### Tracking — 6 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-TRK-001 Register visit | `PLANEJADO_MVP` / P1 / System | Pre: valid referral. Main: validate context, minimize data, timestamp event. Alt: duplicate/retry policy. Result: visit. | RN-015, RN-020, RN-023; `POST /public/referrals/{publicId}/visits` | AC: valid event linked to referral and time. Tests: controller/JPA/privacy. |
| RF-TRK-002 Register contact click | `PLANEJADO_MVP` / P1 / System | Pre: valid referral/category/professional/channel. Main: validate and timestamp. Alt: invalid relation/channel. Result: click event. | RN-015, RN-020; `POST /public/referrals/{publicId}/contact-clicks` | AC: event links all applicable dimensions. Tests: unit/controller/integration. |
| RF-TRK-003 Identify channel | `PLANEJADO_MVP` / P1 / System | Pre: click. Main: accept supported enum (WhatsApp/Instagram initially). Alt: unsupported value 400. Result: normalized channel. | RN-015; contact-clicks | AC: analytics cannot contain arbitrary channel values. Tests: validation/controller. |
| RF-TRK-004 Relate event origin | `PLANEJADO_MVP` / P1 / System | Pre: event. Main: resolve link and optional category/professional. Alt: stale/inconsistent reference rejected. Result: referentially valid event. | RN-015; tracking endpoints | AC: foreign/context invariants hold. Tests: JPA/integration. |
| RF-TRK-005 Record event time | `PLANEJADO_MVP` / P1 / System | Pre: accepted event. Main: generate server-side UTC-aware timestamp. Alt: client timestamp treated only as metadata if approved. Result: reliable period queries. | RN-015; tracking endpoints | AC: server timestamp mandatory and timezone-safe. Tests: persistence/time boundary. |
| RF-TRK-006 Define failure behavior/minimization | `DECISAO_PENDENTE` / P0 / System | Pre: storage unavailable or personal metadata available. Main: apply approved blocking/tolerant policy and minimization. Alt: retry/deduplication. Result: known UX/data guarantee. | RN-020, RN-021, RN-023, RN-024; tracking endpoints | AC/tests depend on privacy/reliability decisions; must be approved before implementation. |

### Analytics — 7 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-ANL-001 Overview | `PLANEJADO_MVP` / P1 / Administrator | Pre: authenticated, valid period. Main: aggregate visits, clicks, conversion. Alt: empty period. Result: zero-safe metrics. | RN-016, RN-017; `GET /admin/analytics/overview` | AC: totals reconcile with source events. Tests: query integration/security. |
| RF-ANL-002 Filter by period | `PLANEJADO_MVP` / P1 / Administrator | Pre: valid inclusive/exclusive date policy. Main: apply timezone-safe range. Alt: inverted/oversized range 400. Result: bounded metrics. | RN-017; analytics GETs | AC: boundary behavior documented. Tests: controller/query/timezone. |
| RF-ANL-003 Metrics by referral link | `PLANEJADO_MVP` / P1 / Administrator | Pre: link public ID. Main: aggregate its events. Alt: 404/empty. Result: link result. | RN-017; `/analytics/referral-links/{publicId}` | AC: only selected link contributes. Tests: JPA query/security. |
| RF-ANL-004 Metrics by category | `PLANEJADO_MVP` / P1 / Administrator | Pre: period. Main: group related events by category. Alt: deleted/deactivated category retained historically. Result: category rows. | RN-010, RN-017; overview/detail | AC: historical labels/IDs remain interpretable. Tests: history aggregation. |
| RF-ANL-005 Metrics by professional | `PLANEJADO_MVP` / P1 / Administrator | Pre: period. Main: group clicks by professional. Alt: inactive professional still appears historically. Result: professional rows. | RN-010, RN-017; overview/detail | AC: deactivation does not erase metrics. Tests: integration. |
| RF-ANL-006 Metrics by channel | `PLANEJADO_MVP` / P1 / Administrator | Pre: period. Main: group clicks by normalized channel. Alt: no clicks. Result: channel totals. | RN-015, RN-017; overview/detail | AC: channel sum equals click total. Tests: query integration. |
| RF-ANL-007 Conversion rate | `PLANEJADO_MVP` / P1 / Administrator | Pre: visit/click totals. Main: calculate clicks ÷ visits under documented distinct-event policy. Alt: zero visits → zero/null policy. Result: stable rate. | RN-017; overview/detail | AC: formula and zero behavior explicit. Tests: unit/query. |

### Audit — 6 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-AUD-001 Audit creation | `PLANEJADO_MVP` / P0 / System | Pre: successful admin create. Main: record actor/action/resource/time. Alt: transaction fails. Result: consistent audit. | RN-019; admin writes | AC: category/professional/link creation audited. Tests: transactional integration. |
| RF-AUD-002 Audit edition | `PLANEJADO_MVP` / P0 / System | Pre: successful change. Main: record changed resource and safe metadata. Alt: no-op patch policy. Result: traceability. | RN-019; admin PATCH | AC: no passwords/tokens/private payload dumps. Tests: integration/security. |
| RF-AUD-003 Audit activation | `PLANEJADO_MVP` / P0 / System | Pre: state transition. Main: record old/new state. Alt: no-op. Result: lifecycle history. | RN-019; admin PATCH | AC: activation is attributable. Tests: service/integration. |
| RF-AUD-004 Audit deactivation | `PLANEJADO_MVP` / P0 / System | Pre: state transition. Main: record actor/resource/time. Alt: no-op. Result: retained history. | RN-008–RN-010, RN-019 | AC: deactivation never destroys audit. Tests: integration. |
| RF-AUD-005 Audit association | `PLANEJADO_MVP` / P0 / System | Pre: association change. Main: record professional/category/action. Alt: failed transaction creates no success event. Result: membership history. | RN-019; association PUT | AC: add/remove attributable. Tests: transactional integration. |
| RF-AUD-006 Audit relevant login | `PLANEJADO_MVP` / P0 / System | Pre: login/security event. Main: record success and policy-approved failures without secrets. Alt: rate-limit event. Result: security evidence. | RN-018, RN-019; login | AC: actor/reference, outcome, time; no credentials. Tests: security/integration. |

### Public foundation — 6 requirements

| ID / title | State / priority / actor | Behavior | Rules / endpoint | Acceptance and expected tests |
|---|---|---|---|---|
| RF-PUB-001 Public application status | `IMPLEMENTADO` / P2 / Visitor/System | Pre: application handles request. Main: return fixed application/status DTO-like map. Alt: none. Result: 200. | `GET /public/status` | AC: current response contract. Tests: controller and real filter-chain security. |
| RF-PUB-002 Health check | `IMPLEMENTADO` / P0 / Operations | Pre: Actuator/application context. Main: evaluate health contributors. Alt: dependency down. Result: health status. | `GET /actuator/health` | AC: health is the only exposed Actuator endpoint; production access remains pending. Tests: full-context health and sensitive endpoint denial. |
| RF-PUB-003 OpenAPI/Swagger | `IMPLEMENTADO` / P1 / Developer | Pre: application running. Main: expose generated API documentation/UI. Alt: production restriction planned. Result: discoverable implemented operations. | `/v3/api-docs`, `/swagger-ui/**` | AC: GET endpoints are public and docs contain only implemented controllers; production restriction remains pending. Tests: full-context document/UI/method security. |
| RF-PUB-004 Standard public category 400/404 errors | `IMPLEMENTADO` / P0 / All | Pre: malformed slug or missing/inactive category. Main: return Problem Details. Alt: active valid category preserves success contract. Result: malformed slug 400; missing/inactive category 404; no accidental 500. | RN-026; public category details | AC: path validation returns useful 400, missing/inactive returns 404, use case is not called for malformed input. Tests: handler, controller, real MVC validation/security. |
| RF-PUB-005 Protect public representation | `IMPLEMENTADO` / P0 / System | Pre: public response. Main: map domain to explicit DTO. Alt: optional nulls. Result: no internal/admin fields. | RN-001, RN-014; all public GETs | AC: current controller tests prove key omissions. Tests: extend to every future DTO. |
| RF-PUB-006 Standardize MVC routing errors | `IMPLEMENTADO` / P0 / All | Pre: request passes security but has no MVC resource/handler or uses an unsupported method. Main: return safe Problem Details. Alt: supported mapped request continues normally. Result: missing route 404; unsupported method 405 with `Allow`; neither becomes 500. | Public MVC boundary; global error handler | AC: `about:blank`, deterministic title/detail, no internal exception or trace. Tests: handler, isolated MVC/security, full Spring Boot MVC. |

**Functional requirement count: 65.**

## Business rules

| Rule | State | Mandatory rule |
|---|---|---|
| RN-001 | `IMPLEMENTADO` | Internal database IDs are never exposed by public or administrative contracts. |
| RN-002 | `PARCIALMENTE_IMPLEMENTADO` | Public IDs are non-predictable, URL-safe, 21 characters, and unique; generation exists but creation flows do not. |
| RN-003 | `IMPLEMENTADO` | Category slug is unique in the database. |
| RN-004 | `IMPLEMENTADO` | Only active categories appear publicly; inactive categories are unavailable by slug. |
| RN-005 | `IMPLEMENTADO` | Only active professionals appear publicly. |
| RN-006 | `IMPLEMENTADO` | A professional may belong to multiple categories. |
| RN-007 | `IMPLEMENTADO` | A duplicate category-professional association is prohibited by composite PK. |
| RN-008 | `PLANEJADO_MVP` | Deactivating a category must not delete history and makes it publicly unavailable. |
| RN-009 | `PLANEJADO_MVP` | Deactivating a professional hides it from every public category without deleting history. |
| RN-010 | `PLANEJADO_MVP` | Administrative deactivation/removal preserves audit and analytics history when legally permitted. |
| RN-011 | `PLANEJADO_MVP` | An inactive referral link is publicly unavailable and cannot accept new events. |
| RN-012 | `DECISAO_PENDENTE` | An expired link is unavailable; whether expiration is mandatory or optional is open. |
| RN-013 | `PLANEJADO_MVP` | A referral link exposes only its authorized active categories. |
| RN-014 | `IMPLEMENTADO` | Private/admin fields do not appear in public API responses. |
| RN-015 | `PLANEJADO_MVP` | A contact click records referral origin and applicable category, professional, channel, and server time. |
| RN-016 | `PLANEJADO_MVP` | Administrative operations require an authenticated, active, authorized user. |
| RN-017 | `PLANEJADO_MVP` | Analytics use a documented period, timezone, dimensions, and conversion formula. |
| RN-018 | `PLANEJADO_MVP` | Production has no default credential; authentication must resist enumeration and brute force. |
| RN-019 | `PLANEJADO_MVP` | Relevant admin writes and security events record actor, action, resource, outcome, and time. |
| RN-020 | `DECISAO_PENDENTE` | Tracking is blocking or failure-tolerant only after an explicit reliability decision. |
| RN-021 | `DECISAO_PENDENTE` | IP storage, derivation, and anonymization require purpose and privacy approval. |
| RN-022 | `DECISAO_PENDENTE` | Link expiration is mandatory or optional only after product approval. |
| RN-023 | `DECISAO_PENDENTE` | Tracking retention and deletion/anonymization schedules require approval. |
| RN-024 | `DECISAO_PENDENTE` | Consent and lawful basis for tracking require product/legal approval. |
| RN-025 | `DECISAO_PENDENTE` | Manual category/professional ordering and its conflict semantics require approval. |
| RN-026 | `IMPLEMENTADO` | The current public category endpoint returns standardized 400 for malformed slug and 404 for missing/inactive category without sensitive details; future public endpoints must preserve the rule. |
| RN-027 | `DECISAO_PENDENTE` | Photos and professional verification are included only after product/data-quality decisions. |
| RN-028 | `DECISAO_PENDENTE` | Service requests enter the MVP only through explicit approval of Alternative B. |
| RN-029 | `PLANEJADO_MVP` | Demonstration data must not be used in production. |
| RN-030 | `IMPLEMENTADO` | Applied Flyway migrations are immutable; changes require a new migration. |

**Business rule count: 30.**

## Non-functional requirements

### Security — 12

| ID | State | Requirement |
|---|---|---|
| RNF-SEC-001 | `IMPLEMENTADO` | Deny access by default and explicitly allow public operations. |
| RNF-SEC-002 | `PLANEJADO_MVP` | Authenticate and authorize every administrative operation. |
| RNF-SEC-003 | `PLANEJADO_MVP` | Store passwords only with an approved adaptive one-way password hash. |
| RNF-SEC-004 | `PLANEJADO_MVP` | Use short-lived access credentials. |
| RNF-SEC-005 | `PLANEJADO_MVP` | Persist refresh-token state securely and support rotation/revocation. |
| RNF-SEC-006 | `PLANEJADO_MVP` | Rate-limit/protect login and sensitive public event endpoints against abuse. |
| RNF-SEC-007 | `IMPLEMENTADO` | Use explicit configured CORS origins; production values must be external configuration. |
| RNF-SEC-008 | `PLANEJADO_MVP` | Restrict or disable Swagger UI/API docs in production. |
| RNF-SEC-009 | `PLANEJADO_MVP` | Restrict Actuator in production while retaining orchestrator health access. |
| RNF-SEC-010 | `PLANEJADO_MVP` | Supply production secrets only through a secret mechanism/environment, never repository defaults. |
| RNF-SEC-011 | `PARCIALMENTE_IMPLEMENTADO` | Validate all input and standardize safe 400/404/security errors. |
| RNF-SEC-012 | `PLANEJADO_MVP` | Exclude credentials, tokens, personal data, SQL, and stack traces from logs/errors. |

### Privacy — 8

| ID | State | Requirement |
|---|---|---|
| RNF-PRIV-001 | `PLANEJADO_MVP` | Document LGPD roles, lawful bases, data subjects, and operational responsibilities. |
| RNF-PRIV-002 | `PLANEJADO_MVP` | Collect only data necessary for catalog, contact attribution, security, and approved analytics. |
| RNF-PRIV-003 | `PLANEJADO_MVP` | Document purpose for every professional contact and tracking field. |
| RNF-PRIV-004 | `PARCIALMENTE_IMPLEMENTADO` | Public DTOs expose only approved contact fields; production data approval remains. |
| RNF-PRIV-005 | `DECISAO_PENDENTE` | Define tracking identifiers, IP policy, anonymization/pseudonymization, and consent. |
| RNF-PRIV-006 | `DECISAO_PENDENTE` | Define raw-event and aggregate retention periods. |
| RNF-PRIV-007 | `PLANEJADO_MVP` | Support deletion or irreversible anonymization where legally applicable without corrupting required aggregates/audit. |
| RNF-PRIV-008 | `PLANEJADO_MVP` | Publish required privacy notice/consent controls before real visitor tracking. |

### Performance — 6

| ID | State | Requirement |
|---|---|---|
| RNF-PERF-001 | `PLANEJADO_MVP` | Query public/administrative views without N+1 behavior. |
| RNF-PERF-002 | `PARCIALMENTE_IMPLEMENTADO` | Maintain indexes for active/catalog joins and add evidence-based tracking/analytics indexes. |
| RNF-PERF-003 | `PLANEJADO_MVP` | Paginate administrative collections. |
| RNF-PERF-004 | `PLANEJADO_MVP` | Enforce a configured maximum page size. |
| RNF-PERF-005 | `PLANEJADO_MVP` | Define and measure an MVP public API latency objective under representative pilot load. |
| RNF-PERF-006 | `PLANEJADO_MVP` | Introduce caching only after measured need and with explicit invalidation. |

### Maintainability — 9

| ID | State | Requirement |
|---|---|---|
| RNF-MAN-001 | `IMPLEMENTADO` | Remain one deployable modular monolith for the MVP. |
| RNF-MAN-002 | `IMPLEMENTADO` | Organize backend code by functional module. |
| RNF-MAN-003 | `IMPLEMENTADO` | Keep API/application/domain/infrastructure responsibilities separated where applicable. |
| RNF-MAN-004 | `IMPLEMENTADO` | Never rewrite an applied migration; add a new version. |
| RNF-MAN-005 | `IMPLEMENTADO` | Version HTTP contracts under `/api/v1`. |
| RNF-MAN-006 | `IMPLEMENTADO` | Exercise persistence-critical behavior against real PostgreSQL through Testcontainers. |
| RNF-MAN-007 | `IMPLEMENTADO` | Use the committed Maven Wrapper locally and in CI. |
| RNF-MAN-008 | `IMPLEMENTADO` | CI runs one reproducible `clean verify`. |
| RNF-MAN-009 | `PARCIALMENTE_IMPLEMENTADO` | Keep OpenAPI aligned with implemented contracts and clearly separate planned documentation. |

### Availability — 6

| ID | State | Requirement |
|---|---|---|
| RNF-AVL-001 | `IMPLEMENTADO` | Expose application/dependency health through Actuator. |
| RNF-AVL-002 | `PLANEJADO_MVP` | Define automated PostgreSQL backup frequency, retention, encryption, and ownership. |
| RNF-AVL-003 | `PLANEJADO_MVP` | Test and document restoration with measurable recovery objectives. |
| RNF-AVL-004 | `PLANEJADO_MVP` | Define application/database rollback and forward-fix procedures. |
| RNF-AVL-005 | `PLANEJADO_MVP` | Define repeatable production deployment and migration sequencing. |
| RNF-AVL-006 | `PLANEJADO_MVP` | Keep API instances stateless where possible; persist session state needed for revocation. |

### Observability — 5

| ID | State | Requirement |
|---|---|---|
| RNF-OBS-001 | `PLANEJADO_MVP` | Emit structured logs appropriate to the runtime platform. |
| RNF-OBS-002 | `PLANEJADO_MVP` | Propagate/generate a correlation ID across request logs and errors. |
| RNF-OBS-003 | `PLANEJADO_MVP` | Expose basic request, error, latency, database-pool, and tracking-failure metrics. |
| RNF-OBS-004 | `PLANEJADO_MVP` | Provide searchable administrative audit events separate from operational logs. |
| RNF-OBS-005 | `FUTURO` | Add actionable alerts after deployment targets and service objectives are approved. |

**Non-functional requirement count: 46.**

## MVP acceptance checklist

The MVP is complete only when every applicable item is evidenced:

- [ ] Administrative authentication, active-user enforcement, short access credential, refresh rotation, logout, and revocation.
- [ ] Category, professional, and association management with activation/deactivation and approved ordering.
- [x] Existing public catalog plus standardized 400/404/405 and individual public professional profile.
- [ ] Referral-link creation, lifecycle, category scope, and contextual public access.
- [ ] Visit and WhatsApp/Instagram click recording under approved privacy/reliability policies.
- [ ] Period-filtered overview and metrics by link, category, professional, and channel, including conversion.
- [ ] Minimum administrative/security audit.
- [x] Current public API security/CORS/input/error behavior covered by tests and OpenAPI aligned.
- [ ] Unit, controller, security, and PostgreSQL integration tests pass through `clean verify`.
- [ ] Real production-approved data and an explicit strategy preventing demo data in production.
- [ ] Production profile/configuration, external secrets, Swagger/Actuator hardening.
- [ ] Tested backup and restore, repeatable deployment, rollback/forward-fix procedure.
- [ ] Minimum structured logs, correlation, metrics, and operational ownership.
- [ ] All P0/P1 decisions affecting implementation are approved and recorded.

`ServiceRequest` is added only if Alternative B is explicitly approved.
