# Bloquinho API

Bloquinho helps people find trusted local professionals. This repository owns business rules, authorization, persistence, catalog data, tracking, service requests, and API-generated public identifiers.

Stack: Java 21, Spring Boot 4.1.x, Maven, PostgreSQL, Flyway, Spring Security, and OpenAPI. It is a modular monolith on port 8080 and is the only component allowed to access the database.

Copy the local values from `.env.example` into your shell, run `docker compose up -d postgres`, then run `mvn test`, `mvn package`, and `mvn spring-boot:run`. The foundation includes security/CORS, error handling, public ID generation, and Flyway migrations. V2 adds initial category reference data, exposed as active categories ordered by name through `GET /api/v1/public/categories`. Internal IDs are never returned. CRUD, authentication, professionals, tracking, and requests remain roadmap work. See `docs/API_CONTRACT.md`, `docs/LOCAL_DEVELOPMENT.md`, and `docs/ROADMAP.md`.

Legacy origin commit: `7a34b97217cb008930d9a20934d8729e3af72d2a`.
