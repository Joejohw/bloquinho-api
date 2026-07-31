# Architecture

## Classification and legend

Bloquinho API is one Maven-built, Spring Boot deployable: a feature-oriented modular monolith with layered/hexagonal influence. It uses one PostgreSQL database owned exclusively by the API. Package boundaries are conventions, not separate Maven modules.

- Green / `IMPLEMENTADO`: exists in the current repository.
- Blue / `PLANEJADO_MVP`: specified for the MVP but absent from code.
- Gray / `FUTURO`: explicitly after MVP.

## System context

```mermaid
flowchart LR
    Admin[Administrator]:::planned
    Visitor[Visitor]:::implemented
    Professional[Partner professional]:::implemented
    System[Bloquinho platform]:::implemented
    Contact[WhatsApp / Instagram]:::implemented
    Admin -->|manage and analyze| System
    Visitor -->|browse public catalog| System
    System -->|public contact destination| Contact
    Professional -->|data curated by admin| System
    classDef implemented fill:#dcfce7,stroke:#15803d,color:#14532d
    classDef planned fill:#dbeafe,stroke:#1d4ed8,color:#1e3a8a
    classDef future fill:#e5e7eb,stroke:#6b7280,color:#374151
```

Administrator workflows are planned; visitor catalog access and contact values are implemented. Partner professionals have no account in the MVP.

## Containers

```mermaid
flowchart LR
    Admin[Angular Admin]:::planned
    Web[Angular Public]:::implemented
    API[Spring Boot API\nsingle Maven artifact]:::implemented
    DB[(PostgreSQL)]:::implemented
    Ops[Deployment / monitoring]:::planned
    Admin -->|REST /api/v1/admin| API
    Web -->|REST /api/v1/public| API
    API -->|JPA / SQL / Flyway| DB
    Ops --> API
    classDef implemented fill:#dcfce7,stroke:#15803d,color:#14532d
    classDef planned fill:#dbeafe,stroke:#1d4ed8,color:#1e3a8a
    classDef future fill:#e5e7eb,stroke:#6b7280,color:#374151
```

The frontend repositories are separate by ADR-003. Their runtime state is outside this backend repository. Local infrastructure provides PostgreSQL through Compose; CI uses Java 21, the Maven Wrapper, and Testcontainers. Production deployment/observability are planned.

## Backend modules and components

Current modules:

- `catalog` — `IMPLEMENTADO`: fixed public application status controller.
- `category` — `IMPLEMENTADO`: API, application use cases, domain record/port, JPA entity/repository/adapter.
- `professional` — `IMPLEMENTADO`: public DTO, category-scoped use case, domain record/port, JPA entity/repository/adapter.
- `shared` — `IMPLEMENTADO`: security/CORS, Problem Details advice, public ID generator.

Planned modules:

- `identity` — `PLANEJADO_MVP`: administrator/session/refresh.
- `administration` — `PLANEJADO_MVP`: secured management orchestration; feature writes should remain with owning modules.
- `referral` — `PLANEJADO_MVP`: link lifecycle and category scope.
- `tracking` — `PLANEJADO_MVP`: visits and contact clicks.
- `analytics` — `PLANEJADO_MVP`: read-side aggregates.
- `audit` — `PLANEJADO_MVP`: administrative/security evidence.

```mermaid
flowchart TB
    subgraph Current[Current modular monolith]
      Catalog[catalog]:::implemented
      Category[category]:::implemented
      Professional[professional]:::implemented
      Shared[shared]:::implemented
      Category --> Professional
      Catalog --> Shared
      Category --> Shared
      Professional --> Shared
    end
    subgraph Planned[MVP modules]
      Identity[identity]:::planned
      Admin[administration]:::planned
      Referral[referral]:::planned
      Tracking[tracking]:::planned
      Analytics[analytics]:::planned
      Audit[audit]:::planned
      Admin --> Identity
      Admin --> Audit
      Referral --> Tracking
      Tracking --> Analytics
    end
    DB[(PostgreSQL)]:::implemented
    Current --> DB
    Planned --> DB
    classDef implemented fill:#dcfce7,stroke:#15803d,color:#14532d
    classDef planned fill:#dbeafe,stroke:#1d4ed8,color:#1e3a8a
    classDef future fill:#e5e7eb,stroke:#6b7280,color:#374151
```

## Internal dependency pattern

```text
Web/API → Application → Domain ← Infrastructure
```

Controllers map HTTP to application services. Application services orchestrate domain ports. Domain packages hold records and repository interfaces. Infrastructure implements those ports through Spring Data JPA. Spring annotations in application code and direct `category.application → professional.application` coupling mean this is not strict ports-and-adapters.

Flyway owns schema evolution; Hibernate uses `ddl-auto=validate` and `open-in-view=false`. Existing migrations are immutable. Testcontainers exercises persistence against PostgreSQL 18.

## Existing public flow

```mermaid
sequenceDiagram
    actor V as Visitor
    participant C as PublicCategoryController
    participant A as Category application
    participant R as JPA adapters
    participant D as PostgreSQL
    V->>C: GET /public/categories
    C->>A: list active categories
    A->>R: findAllActiveOrderByName
    R->>D: active categories query
    D-->>V: public category DTO list
    V->>C: GET /public/categories/{slug}
    C->>A: get active category + professionals
    A->>R: category and professional queries
    R->>D: active joins ordered by name
    D-->>V: minimized category detail
```

This flow is `IMPLEMENTADO`. It exposes contact values but does not create a referral context or track an event.

## Planned referral flow

```mermaid
sequenceDiagram
    actor V as Visitor
    participant P as Public referral API
    participant R as Referral application
    participant T as Tracking application
    participant D as PostgreSQL
    V->>P: GET /public/referrals/{publicId}
    P->>R: validate active/expiration/scope
    R->>D: load link and active allowed catalog
    D-->>V: contextual catalog
    V->>T: POST visit
    T->>D: minimized visit event
    V->>T: POST contact-click
    T->>D: link/category/professional/channel/time
```

All participants except visitor/database are `PLANEJADO_MVP`; tracking failure behavior and privacy details remain `DECISAO_PENDENTE`.

## Planned administrative flow

```mermaid
sequenceDiagram
    actor A as Administrator
    participant I as Identity API
    participant M as Admin feature APIs
    participant U as Audit
    participant D as PostgreSQL
    A->>I: POST login
    I->>D: verify active user / create session
    D-->>A: access + refresh session
    A->>M: authorized category/professional/link write
    M->>D: transactional domain change
    M->>U: actor/action/resource/outcome
    U->>D: audit event
    M-->>A: public-ID admin DTO
```

This flow is `PLANEJADO_MVP`. Current `/api/v1/admin/**` is `denyAll()` and has no controller.

## Domain overview

```mermaid
flowchart LR
    User[AppUser\nschema exists]:::implemented --> Refresh[RefreshToken]:::planned
    User --> Audit[AuditEvent]:::planned
    Category[ProfessionalCategory]:::implemented --> LinkCP[ProfessionalCategoryLink]:::implemented
    Professional[Professional]:::implemented --> LinkCP
    Referral[ReferralLink]:::planned --> LinkRC[ReferralLinkCategory]:::planned
    Category --> LinkRC
    Referral --> Visit[ReferralVisit]:::planned
    Referral --> Click[ContactClick]:::planned
    Category --> Click
    Professional --> Click
    Request[ServiceRequest]:::future
    Referral -. decision pending .-> Request
    classDef implemented fill:#dcfce7,stroke:#15803d,color:#14532d
    classDef planned fill:#dbeafe,stroke:#1d4ed8,color:#1e3a8a
    classDef future fill:#e5e7eb,stroke:#6b7280,color:#374151
```

The detailed entity/invariant/retention model is in [Domain model](DOMAIN_MODEL.md).

## Security, infrastructure, and observability

Spring Security currently provides explicit public allowlists, CORS, OPTIONS, and default/admin denial; it does not authenticate. Identity and production hardening are planned in [Security](SECURITY.md).

The current chain disables CSRF, keeps Spring Security's default `IF_REQUIRED` session policy, has an empty in-memory user manager, and configures neither HTTP Basic, form login nor JWT. GET documentation and health are public; only health is exposed through Actuator. Full-context tests exercise these technical endpoints with the real filter chain and PostgreSQL Testcontainers.

The default `local` profile supplies the runtime baseline. Although `application-test.yml` exists, Maven does not activate it automatically; integration tests override the datasource dynamically and run Flyway under the default profile. No production profile exists yet.

Operational baseline:

- `IMPLEMENTADO`: Java 21, Spring Boot 4.1, Maven Wrapper 3.8.7, PostgreSQL 18, Flyway, Docker build, Compose database, CI `clean verify`, Testcontainers, Actuator health, Springdoc.
- `PLANEJADO_MVP`: production profile/secrets, deploy/rollback, backup/restore, structured logs, correlation ID, metrics, audit, production Swagger/OpenAPI restriction and health access hardening.
- `FUTURO`: evidence-based alerting and any architecture expansion; microservices and messaging infrastructure are outside the first MVP.
