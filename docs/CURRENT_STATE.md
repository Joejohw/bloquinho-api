# Estado atual do Bloquinho API

## Fonte canônica

Este documento é a fonte canônica do estado implementado no `bloquinho-api`. Ele representa a branch `main` no commit `5ccd280` (`feat: enhance public API for professionals and categories`).

Em caso de divergência:

1. o código e as migrations definem o comportamento executável;
2. este documento resume o estado confirmado;
3. `API_CONTRACT.md`, `SCOPE_AND_REQUIREMENTS.md`, `TRACEABILITY.md` e `ROADMAP.md` detalham contratos e planejamento;
4. auditorias registram fotografias históricas e não devem ser interpretadas como estado corrente.

## Papel do repositório

O `bloquinho-api` é o backend do Bloquinho. É um monólito modular Spring Boot responsável pela API HTTP, regras de leitura pública, segurança, persistência PostgreSQL, migrations e documentação dos contratos atuais e planejados.

Os frontends administrativo e público vivem em repositórios separados. Consulte [REPOSITORIES.md](REPOSITORIES.md).

## Stack confirmada

- Java 21;
- Spring Boot 4.1.0;
- Maven 3.8.7 pelo Wrapper;
- PostgreSQL 18;
- Flyway;
- Spring Data JPA;
- Spring Security;
- Spring Boot Actuator;
- Springdoc OpenAPI 3.0.1;
- Testcontainers 2.0.2;
- Docker e GitHub Actions.

## Funcionalidades implementadas

### API pública — Épico 1 concluído

| Método | Rota | Comportamento |
|---|---|---|
| GET | `/api/v1/public/status` | Retorna o status fixo da aplicação no envelope `data` |
| GET | `/api/v1/public/categories` | Lista categorias ativas ordenadas por nome |
| GET | `/api/v1/public/categories/{slug}` | Retorna categoria ativa e profissionais ativos ordenados por nome |
| GET | `/api/v1/public/professionals/{publicId}` | Retorna perfil ativo com campos públicos e categorias ativas ordenadas |

Regras confirmadas:

- recursos de sucesso usam envelope `data`;
- slugs inválidos e IDs públicos inválidos retornam 400;
- recursos inexistentes e inativos retornam 404;
- métodos HTTP não suportados retornam 405;
- falhas inesperadas retornam 500 com mensagem segura;
- erros usam Problem Details e não expõem stack trace;
- IDs internos, `active`, telefone privado, e-mail e timestamps não são expostos;
- um profissional sem categorias e uma categoria sem profissionais retornam listas vazias;
- o `publicId` gerado possui 21 caracteres do alfabeto URL-safe configurado.

### Persistência

- Flyway controla o schema por V1, V2 e V3;
- Hibernate usa `ddl-auto=validate`;
- `open-in-view` está desabilitado;
- categorias são consultadas com filtro de ativo;
- profissionais são consultados com filtro de profissional e categoria ativos;
- o perfil individual usa uma consulta para o profissional e uma consulta para suas categorias, sem associação `EAGER`;
- os testes de repository usam PostgreSQL via Testcontainers, sem H2.

### Segurança e endpoints técnicos

- `/api/v1/public/**` é público;
- `/api/v1/admin/**` usa `denyAll()`;
- qualquer outra rota é negada por padrão;
- não existe usuário padrão;
- não existe login, Basic Auth, form login, JWT, refresh token ou OAuth;
- CSRF está desabilitado e a sessão mantém a política padrão `IF_REQUIRED`;
- CORS aceita somente as duas origens locais configuradas, sem wildcard e sem credenciais;
- `GET /actuator/health` está disponível;
- somente health é exposto pelo Actuator;
- OpenAPI e Swagger UI estão disponíveis apenas por GET segundo a filter chain.

### Qualidade

A suíte contém testes de aplicação, MVC, segurança, erros, OpenAPI e repositories PostgreSQL. A auditoria de fechamento do Épico 1 registrou 68 testes aprovados, sem falhas, erros ou testes ignorados.

Esse número é evidência do estado auditado; resultados futuros devem ser obtidos por uma nova execução da suíte.

## Funcionalidades parcialmente implementadas

- `app_users`: tabela existente, sem entidade JPA, repository ou casos de uso;
- administração: prefixo protegido, mas nenhum endpoint;
- categorias e profissionais: leitura pública pronta, sem escrita administrativa;
- associações profissional/categoria: schema e consultas prontos, sem gestão;
- contatos: WhatsApp e Instagram públicos, sem tracking;
- IDs públicos: gerador pronto, sem fluxo de criação que o consuma;
- operação: desenvolvimento local e CI existem, mas não há ambiente de produção definido.

## Funcionalidades planejadas, não implementadas

- autenticação administrativa, acesso e refresh token, logout e `/me`;
- CRUD administrativo de categorias e profissionais;
- gestão de associações;
- links de indicação;
- tracking de visitas e cliques;
- analytics;
- auditoria administrativa;
- profile e segurança de produção;
- backup, restauração, deployment e observabilidade;
- solicitações de serviço, ainda dependentes de decisão de produto.

Contratos planejados em [API_CONTRACT.md](API_CONTRACT.md) não representam controllers existentes.

## Banco e dados de demonstração

### Migrations

- `V1__create_initial_schema.sql`: usuários, categorias, profissionais, vínculos, constraints e índices;
- `V2__seed_initial_professional_categories.sql`: dez categorias de referência;
- `V3__seed_demo_professionals.sql`: dez profissionais fictícios e suas associações.

### Risco para produção

`V3__seed_demo_professionals.sql` faz parte da cadeia normal do Flyway. Como ainda não há profile de produção nem separação de seeds, a V3 seria executada também em um ambiente de produção que aplicasse as migrations atuais.

Esse risco não está resolvido. A migration não deve ser alterada retroativamente; a estratégia de separação ou neutralização dos dados demo precisa ser definida antes de qualquer implantação produtiva.

As credenciais padrão do PostgreSQL e as origens CORS padrão também são exclusivamente adequadas ao desenvolvimento local.

## Estado dos épicos

| Épico | Estado | Observação |
|---|---|---|
| 0 — Fundação | `PARCIALMENTE_IMPLEMENTADO` | Base local/CI pronta; produção e separação de dados demo pendentes |
| 1 — API pública | `CONCLUÍDO` | Quatro endpoints públicos, segurança, erros, OpenAPI e persistência cobertos |
| 2 — Autenticação administrativa | `PLANEJADO_MVP` | Próximo épico |
| 3 — Administração | `PLANEJADO_MVP` | Depende do Épico 2 |
| 4 — Indicações | `PLANEJADO_MVP` | Depende de administração e decisões de ciclo de vida |
| 5 — Tracking e analytics | `PLANEJADO_MVP` | Depende de decisões de privacidade e confiabilidade |
| 6 — Produção | `PLANEJADO_MVP` | Inclui dados reais, secrets, deploy e operação |

## Próximo épico

O próximo épico é o Épico 2 — autenticação administrativa. Antes de implementar CRUD, precisam ser definidos e entregues:

- persistência e leitura de `AppUser`;
- bootstrap controlado do primeiro administrador;
- verificação segura de senha;
- login e identidade atual;
- access token;
- refresh, rotação e revogação;
- revisão da política de sessão e CSRF;
- proteção autenticada das futuras rotas administrativas.

As decisões de transporte de token, bootstrap e recuperação de senha permanecem abertas.

## Decisões abertas

Continuam sem decisão final:

- nome comercial e domínio público;
- transporte de token e política CSRF/sessão;
- bootstrap e recuperação do administrador;
- papéis administrativos;
- fotos;
- paginação e ordenação manual;
- ciclo de vida e expiração de links;
- falha, deduplicação e privacidade do tracking;
- IP, anonimização, retenção e consentimento;
- inclusão ou exclusão definitiva de solicitações de serviço.

Consulte [OPEN_DECISIONS.md](OPEN_DECISIONS.md) para o catálogo detalhado.

## Documentos históricos

- [FOUNDATION_AUDIT.md](FOUNDATION_AUDIT.md): fotografia da fundação antes da implementação do catálogo público;
- [EPIC_1_AUDIT.md](EPIC_1_AUDIT.md): auditoria que fundamentou o fechamento do Épico 1 antes de sua consolidação no commit `5ccd280`;
- [LEGACY_REFERENCE.md](LEGACY_REFERENCE.md): referência superficial ao sistema legado.

Esses documentos são preservados como evidência histórica. Recomendações, remotes e estados neles descritos podem ter sido superados.
