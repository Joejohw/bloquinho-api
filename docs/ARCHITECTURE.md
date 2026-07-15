# Architecture

```text
Angular Admin ────┐
                  ├── Spring Boot API ── PostgreSQL
Angular Public ───┘
```

Only the central API accesses PostgreSQL. A modular monolith keeps deployment and transactions simple while feature packages preserve boundaries. Separate Angular applications isolate public UX and administrative security concerns while sharing one technology ecosystem. Java 21 and Spring Boot provide a mature server platform; PostgreSQL provides relational integrity. Non-sequential public identifiers avoid exposing database sequences.

The initial modules are authentication, users, professionals, categories, catalog, tracking, and service requests. Only foundation code and the public status endpoint are implemented.
