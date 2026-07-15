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

`GET /api/v1/public/categories/{slug}`

Returns one active category and its active professionals ordered by name. Unknown or inactive categories return HTTP 404. A category without professionals returns HTTP 200 with an empty `professionals` array.

```json
{
  "data": {
    "publicId": "Ctg000000000000000001",
    "name": "Elétrica",
    "slug": "eletrica",
    "description": "Instalações, reparos e manutenção elétrica.",
    "professionals": [
      {
        "publicId": "Pro000000000000000001",
        "name": "Carlos Elétrica Residencial",
        "businessName": "Carlos Elétrica Demo",
        "description": "Serviços fictícios de instalações e reparos elétricos residenciais.",
        "whatsapp": "5500000000001",
        "instagram": "https://instagram.com/bloquinho_demo_eletrica",
        "city": "Campinas",
        "state": "SP"
      }
    ]
  }
}
```

Internal IDs, active flags, timestamps, email, and alternate phone fields are not public. Demo contact values come exclusively from migration V3 and no contact tracking is performed.

## Planned

Standalone professional details, tracking sessions/events, and service requests are roadmap endpoints. Their detailed contracts are deliberately deferred until implemented.
