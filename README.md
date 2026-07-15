# Bloquinho API

Bloquinho helps people find trusted local professionals. This repository owns business rules, authorization, persistence, catalog data, tracking, service requests, and API-generated public identifiers.

Stack: Java 21, Spring Boot 4.1.x, Maven, PostgreSQL, Flyway, Spring Security, and OpenAPI. It is a modular monolith on port 8080 and is the only component allowed to access the database.

Copy the local values from `.env.example` into your environment, start PostgreSQL, then plan to run `mvn spring-boot:run`. Foundation status: schema migration, security/CORS baseline, error handling, public ID generator, and `GET /api/v1/public/status` are prepared. CRUD, authentication, tracking, and requests remain roadmap work. See `docs/LOCAL_DEVELOPMENT.md` and `docs/ROADMAP.md`.

Legacy origin commit: `7a34b97217cb008930d9a20934d8729e3af72d2a`.
