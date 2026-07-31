# Bloquinho API

Backend do Bloquinho para publicação de um catálogo de profissionais locais. Este repositório concentra a API HTTP, as regras de leitura pública, a segurança, a persistência PostgreSQL e a documentação dos módulos planejados.

> Estado canônico: consulte [docs/CURRENT_STATE.md](docs/CURRENT_STATE.md). O documento representa o código confirmado no commit `5ccd280`.

## Estado atual

O Épico 1 — API pública está `CONCLUÍDO`. Estão implementados:

- `GET /api/v1/public/status`;
- `GET /api/v1/public/categories`;
- `GET /api/v1/public/categories/{slug}`;
- `GET /api/v1/public/professionals/{publicId}`;
- `GET /actuator/health`;
- OpenAPI em `/v3/api-docs` e Swagger UI;
- validações e respostas Problem Details para 400, 404, 405 e 500;
- filtro de categorias e profissionais ativos;
- PostgreSQL, Flyway V1–V3 e testes de integração com Testcontainers;
- segurança default-deny, CORS explícito e bloqueio integral de `/api/v1/admin/**`.

Ainda não estão implementados autenticação, CRUD administrativo, links de indicação, tracking, analytics, solicitações de serviço nem preparação para produção. O próximo épico é o Épico 2 — autenticação administrativa.

## Stack

Java 21, Spring Boot 4.1.0, Maven Wrapper 3.8.7, PostgreSQL 18, Flyway, Spring Data JPA, Spring Security, Springdoc OpenAPI 3.0.1 e Testcontainers 2.0.2.

## Execução local

Exporte no shell os valores de `.env.example`; o projeto não carrega automaticamente um arquivo `.env`.

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

A API usa a porta 8080 e o PostgreSQL local usa a porta 5432 por padrão. Docker é necessário para os testes de integração com Testcontainers.

```bash
./mvnw --batch-mode clean verify
```

## Dados de demonstração

`V2__seed_initial_professional_categories.sql` cria as categorias de referência. `V3__seed_demo_professionals.sql` insere dez profissionais inteiramente fictícios e suas associações.

A V3 pertence à cadeia normal do Flyway e, no estado atual, seria aplicada em qualquer ambiente com Flyway habilitado. Isso é um risco explícito para produção: o projeto ainda não possui separação de dados demo nem profile de produção.

## Documentação

- [Estado atual canônico](docs/CURRENT_STATE.md)
- [Contrato da API](docs/API_CONTRACT.md)
- [Escopo e requisitos](docs/SCOPE_AND_REQUIREMENTS.md)
- [Arquitetura](docs/ARCHITECTURE.md)
- [Modelo de domínio](docs/DOMAIN_MODEL.md)
- [Segurança](docs/SECURITY.md)
- [Roadmap](docs/ROADMAP.md)
- [Decisões abertas](docs/OPEN_DECISIONS.md)
- [Desenvolvimento local](docs/LOCAL_DEVELOPMENT.md)
- [Repositórios da solução](docs/REPOSITORIES.md)

Os documentos `FOUNDATION_AUDIT.md` e `EPIC_1_AUDIT.md` são registros históricos das condições observadas em auditorias anteriores. Eles não substituem `CURRENT_STATE.md`.

Commit de origem legado referenciado: `7a34b97217cb008930d9a20934d8729e3af72d2a`.
