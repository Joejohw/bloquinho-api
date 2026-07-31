# Security requirements and current posture

## Current posture

| Capability | State | Evidence/behavior |
|---|---|---|
| Default deny | `IMPLEMENTADO` | `SecurityConfig.anyRequest().denyAll()`. |
| Public API allowlist | `IMPLEMENTADO` | `/api/v1/public/**` is permitted. |
| Admin protection | `PARCIALMENTE_IMPLEMENTADO` | `/api/v1/admin/**` is completely `denyAll()`, not authenticated access. |
| CORS | `IMPLEMENTADO` | Two configured exact origins, explicit methods/headers; OPTIONS permitted. |
| CSRF | `IMPLEMENTADO` | Disabled; token/session design must reassess this choice. |
| User store | `PARCIALMENTE_IMPLEMENTADO` | Empty `InMemoryUserDetailsManager`; no user credential exists. |
| Login/JWT/refresh/logout | `PLANEJADO_MVP` | No endpoint or implementation exists. |
| Swagger | `IMPLEMENTADO` | GET Swagger/OpenAPI is currently public. |
| Actuator | `IMPLEMENTADO` | Only health is exposed and public for GET. |
| Safe errors | `PARCIALMENTE_IMPLEMENTADO` | Problem Details handlers exist, but framework/security/path validation are not fully standardized. |

There is no real authentication, administrator, login endpoint, JWT/Basic/OAuth flow, or default password. Admin routes are unavailable to everyone.

## MVP security controls

The normative controls are RNF-SEC-001–012 in [Scope and requirements](SCOPE_AND_REQUIREMENTS.md):

- active-user authentication and explicit authorization;
- adaptive password hashing;
- short-lived access credentials and securely stored, rotated, revocable refresh tokens;
- login/endpoint abuse protection;
- explicit CORS;
- production restriction of Swagger and Actuator;
- external secrets and no default production credential;
- input validation and standardized safe errors;
- no secrets, tokens, personal data, SQL, or stack traces in logs/responses.

## Privacy and tracking

Before real tracking, approve lawful basis/consent, purpose, minimized fields, IP/anonymization approach, raw/aggregate retention, and deletion/anonymization. See RNF-PRIV-001–008 and [Open decisions](OPEN_DECISIONS.md). Tracking must not be implemented by silently accepting full IPs or persistent cross-context identifiers.

## Required evidence

`PLANEJADO_MVP`: unit tests for token/password rules; controller tests for 400/401/403; full filter-chain tests for public/admin/Swagger/Actuator/CORS/CSRF policy; PostgreSQL integration for refresh revocation and audit; secret/configuration checks; brute-force controls; log/error disclosure checks.

Production readiness also requires a production profile, external secrets, TLS/reverse-proxy policy, backup/restore, deployment, observability, and incident ownership.
