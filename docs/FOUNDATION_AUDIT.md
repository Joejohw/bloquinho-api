# Foundation audit

## Scope

The `bloquinho-api`, `bloquinho-admin`, and `bloquinho-web` repositories were reviewed at commits `721b064`, `6d03063`, and `7657f45`. All were on `main`, clean at the start, and had no configured remotes.

## Findings and corrections

- Expanded compressed Java, SQL, and Angular foundation files into maintainable structures.
- Split each Angular placeholder page into its own feature file and updated lazy routes.
- Centralized browser API access on the relative `/api/v1` path so local proxying and same-origin deployments behave consistently.
- Kept admin endpoints denied until authentication exists, explicitly allowed CORS preflight requests, and retained the two configured concrete origins.
- Made `PublicIdGenerator` injectable while retaining deterministic construction for unit tests; expanded its length, alphabet, uniqueness, and repeated-call assertions.
- Normalized the initial PostgreSQL migration, named relationship constraints, retained timezone-aware timestamps, and removed indexes duplicated by unique constraints.
- Confirmed Docker uses multi-stage builds, Java 21 for the API, Node 24 for Angular, Nginx SPA fallback, and no embedded secrets. The Angular output paths match the configured project names but remain unverified without a build.
- Confirmed CI performs verification only and contains no deploy or image-push steps.

## Compatibility items not verified

- Spring Boot 4.1.0, springdoc 3.0.1, Testcontainers 2.0.2, Angular 22, TypeScript 6, Node 24, and their exact transitive compatibility were not resolved or compiled locally.
- Docker image tags and Angular output directories were reviewed statically only.

## Validation

JSON, YAML, XML structure, SQL, TypeScript imports, route targets, dictionary keys, generated artifacts, environment files, and common secret patterns were inspected statically. `git diff --check` and repository status checks were run in all repositories.

The intentionally excluded commands include dependency installation, builds, tests, application startup, migrations, Docker builds, Compose startup, pushes, and deployment.

## Recommended next slice

After dependency resolution and compilation are allowed, verify the foundation in CI and then implement the read-only public category catalog end to end as the first vertical slice.
