# API contract

## Implemented

`GET /api/v1/public/status`

```json
{"data":{"application":"bloquinho-api","status":"UP"}}
```

`GET /api/v1/public/categories`

Returns active professional categories ordered by `name` ascending. Internal database IDs, active flags, and timestamps are not exposed. An empty catalog returns HTTP 200 with an empty `data` array.

```json
{
  "data": [
    {
      "publicId": "Ctg000000000000000001",
      "name": "Elétrica",
      "slug": "eletrica",
      "description": "Instalações, reparos e manutenção elétrica."
    }
  ]
}
```

## Planned

Public professionals, tracking sessions/events, and service requests are roadmap endpoints. Their detailed contracts are deliberately deferred until implemented.
