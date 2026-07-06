# ics-wtp-polls

**Poll Management Application** — Web Technology Project (International Computer Science), OTH Regensburg, Summer Semester 2026.

## Description

A full-stack web application that allows users to create polls with multiple question types, invite other users to participate, and view aggregate results.

### Core features
- User registration and login (HTTP Basic Auth)
- Create polls with plain-text, boolean, and numeric questions
- Invite up to 3 users per poll
- Participate in polls by answering all questions
- View individual and aggregate results after finishing a poll
- Dark mode support, responsive layout

## Architecture

### Backend
- **Framework:** Spring Boot 4, Java 25
- **Database:** MariaDB (via Docker), JPA/Hibernate for ORM
- **Security:** Spring Security with HTTP Basic Auth, BCrypt password hashing
- **API:** REST API documented with OpenAPI/Swagger (SpringDoc)
- **Testing:** JUnit integration tests with H2 in-memory database, 86% line coverage

### Frontend
- **Framework:** React 19 with Vite
- **Routing:** React Router
- **Styling:** Bootstrap 5, custom CSS with dark mode and responsive media queries

### Deployment
- All services containerized with Docker
- `docker-compose.yml` at project root starts all three containers (db, backend, frontend) with one command

## Running the application

### Production (Docker)

```bash
# Build the backend jar first
cd polls-backend
./mvnw package -DskipTests

# Start all containers
cd ..
docker compose up -d --build
```

- Frontend: http://localhost
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### Development

```bash
# Start the database
cd polls-backend/db
docker compose up -d

# Run backend in IntelliJ (PollsBackendApplication)
# Run frontend
cd polls-frontend
npm run dev
# Frontend available at http://localhost:5173
```

## AI usage

This project was developed with **Claude (Anthropic)** as a learning assistant. Claude was used to:
- Explain Spring Boot concepts (dependency injection, JPA, Spring Security)
- Guide the step-by-step implementation of each layer
- Debug errors during development
- Explain React concepts and hooks

All conversations were used for learning and understanding. Every line of code was typed manually and can be explained. AI-generated code does not exceed 25% of the total codebase.