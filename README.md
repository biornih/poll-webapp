# ics-wtp-polls

Poll management web application for the Web Technology Project (International Computer Science), Summer semester 2026.

## Description

A web application that allows users to create polls with multiple question types, invite other users to participate, and view aggregate results.

## Architecture

- **Backend:** Spring Boot 4, Java 25, MariaDB
- **Frontend:** React with Vite, Bootstrap (to be added)
- **Deployment:** Docker Compose (to be added)

## Running the development environment

### 1. Start the database

\`\`\`bash
cd polls-backend/db
docker compose up -d
\`\`\`

The MariaDB instance runs on port 3306. Adminer (DB admin UI) runs on port 7070.

### 2. Run the backend

Open the project in IntelliJ and run \`PollsBackendApplication\`. The backend runs on http://localhost:8080. Swagger UI is available at http://localhost:8080/swagger-ui/index.html.

## AI usage

This project uses Claude (Anthropic) as a learning assistant during development. All conversations and prompts are documented. Generated code does not exceed 25% of the total codebase.

