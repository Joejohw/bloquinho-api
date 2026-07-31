# MVP roadmap

States follow [Scope and requirements](SCOPE_AND_REQUIREMENTS.md). Sequencing is dependency-driven; it is not a delivery-date commitment.

## Epic 0 — Foundation

- **State:** `PARCIALMENTE_IMPLEMENTADO`.
- **Objective:** reproducible, safe base for vertical delivery.
- **Dependencies:** none.
- **Delivered:** Java/Spring modular monolith; Maven Wrapper; PostgreSQL/Flyway V1–V3; CI `clean verify`; Testcontainers; public DTOs; public ID generator; default-deny/CORS base; health and OpenAPI.
- **Delivered:** malformed category slug returns standardized 400 Problem Details; missing/inactive category remains 404; public status/category access, admin denial, and configured CORS preflight have security tests.
- **Delivered:** the public category repository has PostgreSQL integration coverage for active filtering, inactive lookup, name ordering, slug lookup, mapping, and Flyway V1–V3.
- **Delivered:** real-filter-chain coverage for public/default/admin routes, CORS, OpenAPI/Swagger and health-only Actuator exposure.
- **Delivered:** unmapped public MVC routes return safe 404 Problem Details and unsupported methods return 405 Problem Details with `Allow`, instead of unexpected 500.
- **Delivered:** individual active-professional profile with minimized fields, active categories ordered by name, public ID validation, and PostgreSQL/security/OpenAPI coverage.
- **Remaining:** error standardization for future non-public surfaces; demo-data separation; production profiles.
- **Acceptance:** baseline green; current public behavior secured/tested; no accidental 500 for client validation; production data/config strategy approved.
- **Risks:** demo migrations in every environment; incomplete error/security coverage.

## Epic 1 — Close the public API

- **State:** `CONCLUÍDO`.
- **Objective:** complete and harden the direct public catalog before adding write flows.
- **Dependencies:** Epic 0.
- **Deliveries:** current public status, active-category list/detail, active professionals by category, individual active-professional profile, minimized DTOs, standardized 400/404/405 errors, filter-chain, OpenAPI/Swagger, Actuator, CORS and PostgreSQL/Flyway coverage.
- **Acceptance:** RF-CAT-001/002, RF-PRO-001/008/009 and RF-PUB-001–006 pass application/controller/error/security/OpenAPI/PostgreSQL tests; final audit is recorded in `EPIC_1_AUDIT.md`.
- **Non-blocking backlog:** the status response remains a stable map; the public security matcher remains prefix-based; public pagination/photos and production hardening remain separate decisions/epics.

## Epic 2 — Administrative identity

- **State:** `PLANEJADO_MVP`.
- **Objective:** allow Marcos to access protected operations safely.
- **Dependencies:** Epic 0 security/error base; initial-admin decision.
- **Deliveries:** AppUser persistence mapping; controlled bootstrap; login; active-user enforcement; access credential; refresh rotation; logout/revocation; `/me`; route protection; security audit events.
- **Acceptance:** RF-IDN-001–008 and RNF-SEC identity controls pass security/PostgreSQL tests; no default credential.
- **Risks:** token transport/CSRF choice, brute force, secret/bootstrap operations.

## Epic 3 — Administration

- **State:** `PLANEJADO_MVP`.
- **Objective:** maintain catalog content and relationships without database access.
- **Dependencies:** Epic 2; ordering/photo/verification decisions as applicable.
- **Deliveries:** category and professional list/detail/create/patch; activate/deactivate; association replacement/removal; optional approved positions; audit events; published-catalog preview.
- **Acceptance:** RF-CAT-003–010, RF-PRO-002–007/010, RF-ASC-001–005, RF-AUD-001–005; public filtering remains correct.
- **Risks:** manual-ordering schema, history semantics, validation/data quality.

## Epic 4 — Referrals

- **State:** `PLANEJADO_MVP`.
- **Objective:** create shareable, contextual, lifecycle-controlled referral links.
- **Dependencies:** Epics 1–3; expiration/category-scope decisions.
- **Deliveries:** referral link domain/admin CRUD; category allowlist; public link access; active/expiration enforcement; origin propagation.
- **Acceptance:** RF-REF-001–007 with admin/public/security/time-boundary/PostgreSQL tests.
- **Risks:** empty allowlist semantics, expiration, link enumeration, reuse policy.

## Epic 5 — Tracking and analytics

- **State:** `PLANEJADO_MVP`.
- **Objective:** measure visits, contact intent, and conversion by approved dimensions.
- **Dependencies:** Epic 4; tracking failure, IP, anonymization, retention, and consent decisions.
- **Deliveries:** minimized visits/clicks; channel/origin validation; period analytics; results by link/category/professional/channel; conversion; observability of tracking failure.
- **Acceptance:** RF-TRK-001–006 and RF-ANL-001–007 reconcile with source events; privacy/security controls approved and tested.
- **Risks:** LGPD, event loss/duplication, bot traffic, metric interpretation.

## Epic 6 — Production readiness

- **State:** `PLANEJADO_MVP`.
- **Objective:** safely operate the approved MVP with real data.
- **Dependencies:** Epics 0–5 and product/privacy decisions.
- **Deliveries:** real-data onboarding; demo-data isolation; production profile and external secrets; Swagger/Actuator hardening; backup/restore; deploy/migration/rollback procedure; structured logs, correlation and metrics; security review.
- **Acceptance:** complete [MVP acceptance checklist](SCOPE_AND_REQUIREMENTS.md#mvp-acceptance-checklist), restore rehearsal, repeatable deployment, operational ownership.
- **Risks:** data migration, secret leakage, recovery gaps, unobserved failures.

## After MVP

`FUTURO`: only evidence-driven evolution after the pilot. `FORA_DO_ESCOPO` items listed in the product vision do not enter these epics without a new scope decision. `ServiceRequest` is added only if Alternative B changes from `DECISAO_PENDENTE` to approved.
