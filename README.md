# Bloquinho API

Bloquinho helps people find trusted local professionals. This repository owns business rules, authorization, persistence, catalog data, tracking, service requests, and API-generated public identifiers.

Stack: Java 21, Spring Boot 4.1.x, Maven, PostgreSQL, Flyway, Spring Security, and OpenAPI. It is a modular monolith on port 8080 and is the only component allowed to access the database.

Copy the local values from `.env.example` into your shell and use the committed Maven Wrapper for all API commands. Docker must be available because the verification suite uses Testcontainers. Run `./mvnw --batch-mode clean verify`, start PostgreSQL with `docker compose up -d postgres`, and then run `./mvnw spring-boot:run`. V2 adds category reference data and V3 adds ten entirely fictional development professionals with category links. `GET /api/v1/public/categories` lists active categories; `GET /api/v1/public/categories/{slug}` returns an active category with active professionals. Internal IDs, private contact fields, active flags, and timestamps are never returned. WhatsApp and Instagram links use demo data and have no tracking. CRUD, authentication, standalone professional details, tracking, and requests remain roadmap work. Start with `docs/PRODUCT_VISION.md` and `docs/SCOPE_AND_REQUIREMENTS.md`; see also `docs/API_CONTRACT.md`, `docs/LOCAL_DEVELOPMENT.md`, and `docs/ROADMAP.md`.

Legacy origin commit: `7a34b97217cb008930d9a20934d8729e3af72d2a`.
