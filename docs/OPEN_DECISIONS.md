# Open decisions

Every item is `DECISAO_PENDENTE`; recommendations are technical proposals, not approvals.

| Decision | Context and alternatives | Recommendation | Impact |
|---|---|---|---|
| Final product name | Keep Bloquinho or select another brand after validation. | Keep Bloquinho as working name until domain/trademark checks. | URLs, UI, OpenAPI metadata, communication. |
| Public domain | Dedicated domain/subdomain and ownership are unknown. | Use separate public and admin subdomains with TLS. | CORS, cookies/tokens, deployment. |
| Service request | A: funnel ends at tracked click. B: visitor submits structured request in Bloquinho. | Start with A unless product validation explicitly needs lead qualification; B adds domain, privacy, statuses, administration, and time. | MVP scope and conversion definition. |
| Photos in MVP | No media schema exists; alternatives are no photos, external URL, or managed upload. | Defer managed uploads; decide whether one validated external image URL is essential. | Storage, moderation, public DTOs. |
| Public channels | Current schema has WhatsApp and Instagram; other channels are possible. | Restrict MVP to explicitly approved, validated channels. | Schema, click taxonomy, UI. |
| Reusable referral link | Links may be reusable campaigns or single-purpose shares. | Reusable links with lifecycle and optional expiration. | Uniqueness, analytics interpretation. |
| Expiration | Mandatory, optional, or no expiration. | Optional expiration with explicit active flag. | Availability rules and admin UI. |
| Categories per link | All active categories or an allowlist. | Explicit allowlist; empty-set semantics must be decided. | Join entity and contextual catalog query. |
| Tracking failure | Blocking guarantees recording but harms navigation; tolerant preserves UX but may lose events. | Tolerant contact navigation with observable asynchronous/retry-capable recording, subject to architecture review. | API contract, reliability, metrics accuracy. |
| IP storage | Full IP, truncated/hashed IP, or none. | Do not persist full IP; document a concrete abuse-control need before any derived form. | LGPD, security, retention. |
| Anonymization | Raw identifiers, pseudonymous session, aggregate-only. | Pseudonymous short-lived visitor/session identifier with no cross-context profiling. | Event model and privacy. |
| Retention | Indefinite or bounded by data class. | Define short raw-event retention and longer aggregate retention with deletion jobs. | Storage and LGPD operations. |
| Consent | Strictly necessary measurement, consent banner, or no nonessential tracking. | Obtain legal/product review before implementation; default to minimum necessary events. | Frontend UX and lawful basis. |
| Initial administrator | Migration, startup bootstrap, CLI, or one-time invitation. | One-time controlled bootstrap using external secret; never a fixed production credential. | Deployment and audit. |
| Password recovery | Admin-assisted reset or email self-service. | Admin-assisted/operational reset for one-admin pilot; revisit before expansion. | Email infrastructure and token model. |
| Administrative roles | Single ADMIN or multiple roles. | Begin with one least-privilege administrator role but keep authorization explicit. | Security model and audit. |
| Professional verification | Informal curation, status field, or documented workflow. | Define minimum verification evidence before exposing a “verified” claim. | Trust, liability, domain fields. |
| Public pagination | Unpaged small catalog or bounded pagination. | Keep current category response for pilot; require bounded pagination before scale. | Backward compatibility and frontend. |
| Manual ordering | Alphabetical only or explicit positions. | Add explicit integer positions for categories and category-professional associations if product approves curated ordering. | Schema migration and admin workflow. |

## Service-request alternatives

### Alternative A — outside the MVP

The final funnel result is a tracked click. Advantages: smaller scope, faster implementation, and earlier validation of catalog demand. Disadvantages: it does not prove that a service was requested and continuity happens in WhatsApp/Instagram.

### Alternative B — included in the MVP

The visitor submits a structured request. Advantages: structured data, better conversion follow-up, and richer metrics. Disadvantages: a new domain, personal-data handling, statuses, administration, notifications, and increased delivery time.

**Technical recommendation:** Alternative A for the first pilot. `ServiceRequest` remains `DECISAO_PENDENTE` and is excluded from acceptance until the product owner approves B.
