# Security

Public endpoints and health/API documentation have an explicit allowlist. Administrative endpoints require authentication, whose implementation is pending. CORS accepts only configured admin and public origins. No fixed user, fake authentication, JWT, credentials, or production secrets are included. Problem responses must not disclose stack traces, SQL, class names, credentials, or database details.
