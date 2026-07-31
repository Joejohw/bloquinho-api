# Product vision

## Status vocabulary

This documentation uses only `IMPLEMENTADO`, `PARCIALMENTE_IMPLEMENTADO`, `PLANEJADO_MVP`, `FUTURO`, `DECISAO_PENDENTE`, and `FORA_DO_ESCOPO`.

## Product

**Working name:** Bloquinho (`DECISAO_PENDENTE` as the final commercial name).

Bloquinho is a platform for administering and sharing trusted referrals to professionals and suppliers. Today, referrals are commonly held in private contact lists and exchanged in unstructured messages. The recipient has little context, the person making the referral cannot maintain one source of truth, and neither side can understand whether a referral generated interest.

### Target audience

- **Administrator:** initially Marcos, who curates categories, partner professionals, referral links, and results.
- **Client or visitor:** accesses the public experience without an account, discovers professionals, and starts contact.
- **Partner professional:** is curated by the administrator, may appear in multiple categories, and does not require an account in the MVP.

### Value proposition

Provide one maintainable public catalog for trusted referrals, contextual links for sharing, and minimum evidence of visits and contact intent without forcing visitors or professionals to create accounts.

### Product hypothesis

If a curator can publish trusted professionals through contextual, measurable links, visitors will find the right contact faster and the curator will learn which referrals, categories, professionals, and channels create engagement.

## MVP objective

Deliver a secure end-to-end funnel in which Marcos can authenticate, manage categories and professionals, publish referral links, and view minimum visit-to-contact analytics, while visitors browse active content without authentication. Service requests remain `DECISAO_PENDENTE`.

## Actors and responsibilities

| Actor | State | Responsibilities |
|---|---|---|
| Administrator | `PLANEJADO_MVP` | Authenticate; manage categories, professionals, associations, referral links, and publication order; inspect the public catalog and analytics. |
| Client or visitor | `PARCIALMENTE_IMPLEMENTADO` | Access without an account; list categories; inspect a category and professionals; eventually open a referral and tracked contact channel. |
| Partner professional | `PARCIALMENTE_IMPLEMENTADO` | Be registered by the administrator; appear in multiple categories; have no required login or direct editing in the MVP. |
| System | `PARCIALMENTE_IMPLEMENTADO` | Validate rules, filter active content, protect administrative data, record future events and audit history, and never expose internal IDs. |

## Current evidence

`IMPLEMENTADO`: public status, active-category listing, active category lookup by slug, active professionals per category, individual active-professional profile, public DTOs, 21-character public ID generator, PostgreSQL/Flyway, default-deny security, explicit CORS, health/OpenAPI, and Testcontainers coverage. Evidence is in `src/main`, migrations V1–V3, and current tests.

`PARCIALMENTE_IMPLEMENTADO`: the public catalog exposes WhatsApp and Instagram values but has no referral link, redirect, visit, or click tracking. `app_users` exists only as a table; administrative routes are `denyAll()`.

## Expected outcomes and success metrics

| Outcome | MVP metric | Initial target |
|---|---|---|
| Curated supply is usable | Active categories and professionals published | At least one real professional in every published category |
| Referrals are consumed | Valid referral visits | Baseline established during pilot |
| Visitors show contact intent | WhatsApp/Instagram clicks | Baseline established during pilot |
| Funnel is measurable | Visit-to-click conversion | Correct, filterable calculation by period |
| Administration is sustainable | Time to update catalog/link | Product owner validates acceptable workflow |
| Operations are trustworthy | Critical security/data incidents | Zero during pilot |

Numeric growth targets remain `DECISAO_PENDENTE` until a real pilot produces a baseline.

## Constraints

- Java 21/Spring Boot modular monolith and one PostgreSQL database.
- Separate public and administrative Angular frontends.
- Visitors and partner professionals do not require accounts in the MVP.
- Internal database IDs and administrative fields remain private.
- Existing Flyway migrations are immutable.
- Demo data must not be used in production.
- Tracking must follow LGPD, minimization, purpose, retention, and consent decisions.

## Risks

- Trust and data quality depend on manual curation.
- Tracking decisions may materially change privacy and implementation.
- Demo seed data currently shares the normal migration path.
- Authentication, production profiles, backup, restore, deployment, and observability do not yet exist.
- Clicks measure intent, not a completed service or commercial outcome.

## Evolution

`FUTURO`: richer professional access and content may be considered after MVP evidence.

`FORA_DO_ESCOPO` for the first MVP: client account, complete professional account, native mobile app, chat, proposals, quotes, documents, electronic signatures, payments, payment split, automatic commission, reviews, ranking, calendar, real-time geolocation, multitenancy, white-label, plans, recurring billing, microservices, Kafka, RabbitMQ, Redis, and Kubernetes.

See [Scope and requirements](SCOPE_AND_REQUIREMENTS.md), [Open decisions](OPEN_DECISIONS.md), and [Roadmap](ROADMAP.md).
