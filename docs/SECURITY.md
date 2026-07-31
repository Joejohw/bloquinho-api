# Security requirements and current posture

## Current posture

| Capability | State | Evidence/behavior |
|---|---|---|
| Default deny | `IMPLEMENTADO` | `SecurityConfig.anyRequest().denyAll()`. |
| Public API allowlist | `IMPLEMENTADO` | `/api/v1/public/**` is permitted. |
| Admin protection | `PARCIALMENTE_IMPLEMENTADO` | `/api/v1/admin/**` is completely `denyAll()`, not authenticated access. |
| CORS | `IMPLEMENTADO` | Two configured exact origins, explicit methods/headers; OPTIONS permitted. |
| CSRF | `IMPLEMENTADO` | Disabled; token/session design must reassess this choice. |
| Session | `PARCIALMENTE_IMPLEMENTADO` | No custom policy; Spring Security's default `IF_REQUIRED` applies, but current flows do not authenticate or intentionally create sessions. |
| User store | `PARCIALMENTE_IMPLEMENTADO` | Empty `InMemoryUserDetailsManager`; no user credential exists. |
| Login/JWT/refresh/logout | `PLANEJADO_MVP` | No endpoint or implementation exists. |
| Swagger | `IMPLEMENTADO` | GET `/v3/api-docs/**`, `/swagger-ui/**`, and `/swagger-ui.html` is public; other methods are denied. |
| Actuator | `IMPLEMENTADO` | Only health is exposed and public for GET; other paths are also denied by the filter chain. |
| Safe errors | `IMPLEMENTADO` for the current public API | Validation, missing/inactive resources, unmapped MVC routes, unsupported methods, and unexpected failures have safe Problem Details handling; future endpoints must retain the same policy. |

There is no real authentication, administrator, login endpoint, JWT, HTTP Basic, form login, OAuth flow, or default password. Admin routes are unavailable to everyone. The empty user manager deliberately suppresses Spring Boot's generated default credential.

`/api/v1/public/**` is a prefix allowlist for every HTTP method. It does not create a controller, so an unmapped future path is not administrative, but any future controller under that prefix becomes security-public automatically. Unsupported and unmapped public requests reach MVC and are converted to deterministic 405 and 404 Problem Details respectively. The broad prefix is a non-blocking governance risk: every future controller placed below it becomes public automatically and must receive an explicit security review.

## CORS behavior

- exact origins: `${ADMIN_FRONTEND_URL:http://localhost:4200}` and `${PUBLIC_FRONTEND_URL:http://localhost:4300}`;
- methods: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`;
- headers: `Content-Type`, `Authorization`, `Accept`;
- credentials are not enabled and wildcards are not used;
- `OPTIONS /**` is security-permitted, while Spring CORS still rejects a preflight from an unlisted origin;
- a request carrying an unlisted `Origin` is rejected without `Access-Control-Allow-Origin`.

## Profiles

- `local` is the default. It uses the base PostgreSQL configuration (localhost defaults), Flyway V1–V3 including demo seeds, public Swagger/OpenAPI, health-only Actuator exposure, and the two configurable CORS origins. Its only profile-specific override disables JPA SQL display.
- `test` only sets `spring.flyway.enabled=false`. It is not automatically activated by the current Maven suite. Full integration tests run with the default `local` profile, replace datasource properties with Testcontainers, and therefore execute Flyway.
- no production profile exists. Production must disable or protect Swagger UI, restrict OpenAPI, retain only required health access, require CORS values without localhost defaults, externalize database/secrets, and prevent demo data. This remains `PLANEJADO_MVP`; no speculative profile was added.

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

Current filter-chain tests cover public/admin/default denial, empty users, Swagger/OpenAPI, Actuator and CORS. `PLANEJADO_MVP`: token/password rules; 401 behavior after authentication exists; PostgreSQL refresh revocation and audit; production profile and secret checks; brute-force controls; log/error disclosure checks.

Production readiness also requires a production profile, external secrets, TLS/reverse-proxy policy, backup/restore, deployment, observability, and incident ownership.
