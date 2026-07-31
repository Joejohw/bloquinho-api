# Foundation audit

> **Documento histórico.** Esta auditoria registra a fundação observada nos commits citados abaixo e não representa o estado atual. O catálogo público mencionado como próximo incremento já foi implementado. Consulte [CURRENT_STATE.md](CURRENT_STATE.md) para o estado canônico no commit `5ccd280`.

## Scope

The `bloquinho-api`, `bloquinho-admin`, and `bloquinho-web` repositories were fully validated from commits `cffe339`, `8738b6e`, and `41fd415`. All were on `main`, clean at the start, and had no configured remotes.

## Findings and corrections

- Expanded compressed Java, SQL, and Angular foundation files into maintainable structures.
- Split each Angular placeholder page into its own feature file and updated lazy routes.
- Centralized browser API access on the relative `/api/v1` path so local proxying and same-origin deployments behave consistently.
- Kept admin endpoints denied until authentication exists, explicitly allowed CORS preflight requests, and retained the two configured concrete origins.
- Made `PublicIdGenerator` injectable while retaining deterministic construction for unit tests; expanded its length, alphabet, uniqueness, and repeated-call assertions.
- Normalized the initial PostgreSQL migration, named relationship constraints, retained timezone-aware timestamps, and removed indexes duplicated by unique constraints.
- Confirmed Docker uses multi-stage builds, Java 21 for the API, Node 24 for Angular, Nginx SPA fallback, and no embedded secrets. The Angular output paths match the configured project names but remain unverified without a build.
- Confirmed CI performs verification only and contains no deploy or image-push steps.
- Replaced the raw Flyway dependency with the Spring Boot 4 Flyway starter, which enabled migration auto-configuration.
- Prevented Spring Security from generating a default user and password, and explicitly connected the CORS source to the Security 7 filter chain.
- Corrected the PostgreSQL 18 volume mount from `/var/lib/postgresql/data` to `/var/lib/postgresql`.
- Added the Angular 22 test runner dependencies, development build configurations, and one root-component foundation test per application. Removed the invalid lint scripts because no lint builder is configured.

## Validated compatibility

- Java 21.0.11, Maven 3.9 in the official Temurin 21 image, Spring Boot 4.1.0, Spring Security 7.1.0, Flyway 12.4.0, PostgreSQL 18.4, and springdoc 3.0.1 resolved, compiled, and started together.
- Node 24.18.0 and npm 11.16.0 resolved Angular 22.0.x and TypeScript 6.0.x. Strict type-check, Vitest, production builds, development servers, and proxies passed for both applications.
- Real browser output directories are `dist/bloquinho-admin/browser` and `dist/bloquinho-web/browser`; both Dockerfiles already reference them.

## Validation

Maven dependency resolution, tests, package generation, PostgreSQL health, Flyway V1, schema constraints, public/health/OpenAPI/admin endpoints, CORS, Angular dependency installation, strict type-check, tests, builds, local servers, and proxies were validated. Port 8080 and PostgreSQL port 5432 were already occupied locally, so isolated validation used ports 8081 and 5433 without stopping unrelated services.

Three low-severity npm audit findings remain for review. No automatic audit fix was applied.

## Recommended next slice

Run the same commands in CI, then implement the read-only public category catalog end to end as the first vertical slice.
