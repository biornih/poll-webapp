# Polls Web App

<p align="center">
  <img src="PollsExample1.PNG" width="200" alt="Log in">
  <img src="PollsExample2.PNG" width="200" alt="Polls">
  <img src="PollsExample3.PNG" width="200" alt="Create">
  <img src="PollsExample4.PNG" width="200" alt="Profile">
</p>

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

### Extra Features

- **Expired polls** — polls past their due date show as "Expired" and block new answers
- **Profile page** — user statistics (polls created, pending, answered) with clickable navigation
- **Profile editing** — username can be changed anytime; password change limited to once per week
- **Uninvite with undo** — poll creators can uninvite users within 10 seconds of inviting them
- **Dark mode** — full dark theme following system preference (macOS/Windows appearance setting)
- **Animated UI** — moving gradient background, button wave shimmer, radar pulse on pending indicator
- **Printing** — printing all your polls by clicking on "Print"

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
