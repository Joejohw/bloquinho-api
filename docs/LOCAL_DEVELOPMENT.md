# Local development

Requirements are Java 21, Docker with Compose, and Node.js 24 for the Angular applications. Use the committed `./mvnw` script so local development and CI run the configured Maven 3.8.7 distribution. Export the values documented in `.env.example` in your shell; do not commit a local `.env`.

Docker must be available for the Testcontainers integration tests. Run the complete project validation with `./mvnw --batch-mode clean verify`. Start PostgreSQL with `docker compose up -d postgres`, confirm it with `docker compose ps`, and run the API with `./mvnw spring-boot:run`. The validated endpoint is `curl http://localhost:8080/api/v1/public/status`. Stop PostgreSQL with `docker compose down` without `-v` to preserve its data.

The ignored `target/` directory contains disposable Maven build output and must never be committed. Do not mount the repository into build containers that write local artifacts as a different user. If a container creates `target/` with an invalid owner, remove only that generated directory with `sudo rm -rf -- /home/joejohw/projetos/bloquinho-api/target`, then rerun the wrapper command as the normal user. Never use broad permission changes such as `chmod -R 777`.

In each Angular repository, run `npm ci`, `npm run typecheck`, `npm test`, and `npm run build`. Start Admin with `npm start` on 4200 and Web with `npm start` on 4300. Their `/api` proxies target the API on 8080.

The foundation was validated using Java 21.0.11, Spring Boot 4.1.0, PostgreSQL 18.4, Node 24.18.0, and npm 11.16.0. Local port conflicts required temporary ports 5433 and 8081 during validation; the documented defaults remain 5432 and 8080.
