# Local development

Requirements are Java 21, Maven, PostgreSQL, and Node.js 24 for the Angular applications. Copy `.env.example` values into your shell or a private local environment file. Start PostgreSQL, then run the API on 8080, admin on 4200, and public web on 4300. These setup commands are planned and were not run during foundation creation.

The Angular proxy forwards `/api` to port 8080 for same-origin local calls. The configured `apiUrl` supports direct API calls; choose one convention per environment.
