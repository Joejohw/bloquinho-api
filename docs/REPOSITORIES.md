# Repositories

The solution is split into private repositories named `bloquinho-api`, `bloquinho-admin`, and `bloquinho-web`. No remote is configured by this foundation task.

Future commands, after selecting the GitHub owner and repository URLs:

```bash
gh repo create bloquinho-api --private
gh repo create bloquinho-admin --private
gh repo create bloquinho-web --private
git remote add origin <repository-url>
git push -u origin main
```
