# API contract

Only operations marked `IMPLEMENTADO` exist in code/OpenAPI today. Planned contracts are design targets subject to review; they are not controllers.

## Conventions

- Base path: `/api/v1`; JSON names use `camelCase`.
- Successful resource payloads use `{"data": ...}`; planned paged responses use `data` plus `page`.
- Resources use 21-character `publicId`; internal IDs are never serialized.
- Planned admin endpoints require an active authorized administrator.
- Errors use Problem Details: 400 validation, 401 unauthenticated, 403 unauthorized, 404 unavailable/not found, 405 unsupported method, 409 conflict, and 500 safe unexpected error.
- GET is safe/idempotent. PUT is idempotent. PATCH/POST tracking idempotency is stated per endpoint.

## Implemented

### `GET /api/v1/public/status` — `IMPLEMENTADO` / RF-PUB-001

Public; no request parameters. Returns 200:

```json
{"data":{"application":"bloquinho-api","status":"UP"}}
```

### `GET /api/v1/public/categories` — `IMPLEMENTADO` / RF-CAT-001

Public, unpaged, active categories ordered by name. Returns 200 with an empty list when necessary:

```json
{"data":[{"publicId":"Ctg000000000000000001","name":"Elétrica","slug":"eletrica","description":"Instalações e reparos."}]}
```

### `GET /api/v1/public/categories/{slug}` — `IMPLEMENTADO` / RF-CAT-002, RF-PRO-001

Public. `slug` matches lowercase alphanumeric hyphenated format. Returns an active category and active professionals ordered by name:

```json
{
  "data": {
    "publicId": "Ctg000000000000000001",
    "name": "Elétrica",
    "slug": "eletrica",
    "description": "Instalações e reparos.",
    "professionals": [{
      "publicId": "Pro000000000000000001",
      "name": "Carlos Elétrica Residencial",
      "businessName": "Carlos Elétrica Demo",
      "description": "Serviços fictícios.",
      "whatsapp": "5500000000001",
      "instagram": "https://instagram.com/bloquinho_demo_eletrica",
      "city": "Campinas",
      "state": "SP"
    }]
  }
}
```

Returns 404 for unknown/inactive category and 200 with empty `professionals`. A malformed slug returns 400 Problem Details without invoking the use case:

```json
{"type":"about:blank","title":"Invalid request","status":400,"detail":"Slug inválido."}
```

Internal ID, active, timestamps, email, and alternate phone are omitted.

### `GET /actuator/health` — `IMPLEMENTADO` / RF-PUB-002

Public GET under current security; reports Actuator health. It is the only Actuator endpoint exposed by configuration. Requests such as `/actuator/env`, `/actuator/beans`, and `/actuator/configprops` are not exposed and are intercepted by default-deny as 403 without returning their data. Production access is `PLANEJADO_MVP`.

### OpenAPI/Swagger — `IMPLEMENTADO` / RF-PUB-003

Current GET access: `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`; non-GET requests are denied. `/v3/api-docs` documents the implemented public status and category operations and no absent administrative controller. Production restriction is `PLANEJADO_MVP`.

The current security allowlist uses the broad `/api/v1/public/**` prefix for all methods. MVC failures that pass this boundary are standardized:

- an unmapped public route returns 404 with `{"type":"about:blank","title":"Resource not found","status":404,"detail":"The requested resource was not found."}`;
- an unsupported method on an existing public resource returns 405 with `{"type":"about:blank","title":"Method not allowed","status":405,"detail":"The HTTP method is not supported for this resource."}` and preserves the MVC `Allow` header.

Neither case is converted to an unexpected 500 or masked as an administrative 403.

## Planned identity

| Endpoint | Req. / state | Request and validation | Response / codes | Pagination / idempotency |
|---|---|---|---|---|
| `POST /api/v1/admin/auth/login` | RF-IDN-001–003 / `PLANEJADO_MVP` | Public; JSON email/password; normalized valid email, bounded password; rate limited. | 200 user + short access credential + refresh mechanism; 400, 401; no enumeration. | No pagination; non-idempotent session creation. |
| `POST /api/v1/admin/auth/refresh` | RF-IDN-005,007 / `PLANEJADO_MVP` | Public transport endpoint; valid unexpired unrevoked refresh credential; rotation/reuse checks. | 200 rotated session; 400/401. | No pagination; deliberately non-idempotent under rotation. |
| `POST /api/v1/admin/auth/logout` | RF-IDN-006,007 / `PLANEJADO_MVP` | Auth/session context plus refresh credential according to approved transport. | 204; 400/401. Already-revoked behavior must be safe. | No pagination; intended idempotent revocation. |
| `GET /api/v1/admin/auth/me` | RF-IDN-004 / `PLANEJADO_MVP` | Authenticated active administrator. | 200 `{data:{publicId,name,email,role}}`; 401/403. | No pagination; safe/idempotent. |

No password hash, raw refresh secret, internal ID, or security metadata may be returned.

## Planned category administration

| Endpoint | Req. / state | Request and validation | Response / codes | Pagination / idempotency |
|---|---|---|---|---|
| `GET /api/v1/admin/categories` | RF-CAT-003 / `PLANEJADO_MVP` | Authorized admin; page/size/sort/active filters; bounded size and allowlisted sort. | 200 page of admin category summaries; 400/401/403. | Required; safe/idempotent. |
| `POST /api/v1/admin/categories` | RF-CAT-004,009 / `PLANEJADO_MVP` | `{name,slug,description,active?,position?}`; required/bounded fields, slug format/unique; position only if approved. | 201 + Location/resource; 400/401/403/409. | No pagination; non-idempotent, DB uniqueness protects conflicts. |
| `GET /api/v1/admin/categories/{publicId}` | RF-CAT-010 / `PLANEJADO_MVP` | Authorized admin; valid 21-character public ID. | 200 admin detail; 400/401/403/404. | Safe/idempotent. |
| `PATCH /api/v1/admin/categories/{publicId}` | RF-CAT-005–008 / `PLANEJADO_MVP` | Partial name/slug/description/active/approved position; at least one field; unique slug. | 200 updated detail; 400/401/403/404/409. | Idempotent for the same desired field values. |

## Planned professional administration/public profile

| Endpoint | Req. / state | Request and validation | Response / codes | Pagination / idempotency |
|---|---|---|---|---|
| `GET /api/v1/admin/professionals` | RF-PRO-002 / `PLANEJADO_MVP` | Authorized admin; bounded page, active/category/search filters, allowlisted sort. | 200 paged summaries; 400/401/403. | Required; safe/idempotent. |
| `POST /api/v1/admin/professionals` | RF-PRO-004 / `PLANEJADO_MVP` | Valid name, optional bounded business/description/city; UF format; validated approved contacts. | 201 + Location/detail; 400/401/403/409 as applicable. | Non-idempotent; generated public ID unique. |
| `GET /api/v1/admin/professionals/{publicId}` | RF-PRO-003 / `PLANEJADO_MVP` | Authorized admin; valid public ID. | 200 admin detail/associations; 400/401/403/404. | Safe/idempotent. |
| `PATCH /api/v1/admin/professionals/{publicId}` | RF-PRO-005–007 / `PLANEJADO_MVP` | Partial permitted fields/active; contact and length validation. | 200 updated detail; 400/401/403/404/409. | Idempotent for same desired values. |
| `PUT /api/v1/admin/professionals/{publicId}/categories` | RF-ASC-001–005 / `PLANEJADO_MVP` | `{categoryPublicIds:[...], ordering?:[...]}`; unique existing categories; ordering only if approved. | 200 resulting associations; 400/401/403/404/409. | Atomic replacement and idempotent. |
| `GET /api/v1/public/professionals/{publicId}` | RF-PRO-008,009 / `PLANEJADO_MVP` | Public; valid public ID; professional must be active. | 200 minimized profile and active categories; 400/404. | Unpaged; safe/idempotent. |

The public profile may expose name, business name, description, approved WhatsApp/Instagram, city/state, and active categories. Phone/email/internal/admin fields remain excluded.

## Planned referral links

| Endpoint | Req. / state | Request and validation | Response / codes | Pagination / idempotency |
|---|---|---|---|---|
| `GET /api/v1/admin/referral-links` | RF-REF-001–005 / `PLANEJADO_MVP` | Authorized admin; bounded page, active/expired/search filters. | 200 paged summaries; 400/401/403. | Required; safe/idempotent. |
| `POST /api/v1/admin/referral-links` | RF-REF-001,005 / `PLANEJADO_MVP` | `{label,active?,expiresAt?,categoryPublicIds}`; valid distinct existing categories and approved expiration policy. | 201 + Location/detail; 400/401/403/409. | Non-idempotent; public ID unique. |
| `GET /api/v1/admin/referral-links/{publicId}` | RF-REF-002–005 / `PLANEJADO_MVP` | Authorized admin; valid public ID. | 200 lifecycle/categories/summary; 400/401/403/404. | Safe/idempotent. |
| `PATCH /api/v1/admin/referral-links/{publicId}` | RF-REF-002–005 / `PLANEJADO_MVP` | Partial label/active/expiresAt/category allowlist; coherent lifecycle and distinct categories. | 200 updated detail; 400/401/403/404/409. | Idempotent for same desired values. |
| `GET /api/v1/public/referrals/{publicId}` | RF-REF-006,007 / `PLANEJADO_MVP` | Public; valid active link, expiration policy, permitted active categories. | 200 contextual catalog; 400; unavailable link uses approved non-enumerating 404/410 policy. | Unpaged initially; safe/idempotent. |

## Planned tracking

| Endpoint | Req. / state | Request and validation | Response / codes | Pagination / idempotency |
|---|---|---|---|---|
| `POST /api/v1/public/referrals/{publicId}/visits` | RF-TRK-001,004–006 / `PLANEJADO_MVP` | Public; valid available link; minimized optional session/origin fields only after privacy approval. Server supplies time. | 202/204 or 201 according to approved reliability contract; 400/404/429/503 policy. | No pagination; retry/idempotency key or deduplication is `DECISAO_PENDENTE`. |
| `POST /api/v1/public/referrals/{publicId}/contact-clicks` | RF-TRK-002–006 / `PLANEJADO_MVP` | Public; `{categoryPublicId,professionalPublicId,channel}`; channel enum and link/category/professional consistency. | 202/204 or 201; 400/404/409/429/503 policy. | No pagination; retry/deduplication and blocking behavior are `DECISAO_PENDENTE`. |

Tracking requests must not accept internal IDs or an authoritative client timestamp. IP, consent, anonymization, retention, and failure behavior remain open and must be resolved before implementation.

## Planned analytics

| Endpoint | Req. / state | Request and validation | Response / codes | Pagination / idempotency |
|---|---|---|---|---|
| `GET /api/v1/admin/analytics/overview` | RF-ANL-001,002,004–007 / `PLANEJADO_MVP` | Authorized admin; required/defaulted `from`/`to`, timezone policy, bounded range; optional dimensions. | 200 totals, conversion and breakdowns; 400/401/403. | Breakdowns may be bounded/paged; safe/idempotent. |
| `GET /api/v1/admin/analytics/referral-links/{publicId}` | RF-ANL-002–007 / `PLANEJADO_MVP` | Authorized admin; valid link public ID and period. | 200 link totals/breakdowns; 400/401/403/404. | Event details are not returned; safe/idempotent. |

## Service requests

No endpoint is reserved. `ServiceRequest` is `DECISAO_PENDENTE`; a separate contract is required only if Alternative B is approved.
