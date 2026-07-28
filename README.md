# LucidPoint — Academic Intelligence Platform (Phase 1 MVP)

An AI-oriented EdTech platform where teachers record assessments and attendance,
and the system turns that raw data into performance analytics for students, teachers,
and parents.

## Stack

| Layer     | Technology                                    |
|-----------|------------------------------------------------|
| Frontend  | React 18 (Vite), React Router, Axios, Recharts |
| Backend   | Java 17, Spring Boot 3.3, Spring Security, JWT |
| Database  | PostgreSQL                                     |
| Auth      | Email/password + JWT (stateless)               |

See `ARCHITECTURE.md` for the full system design and a file-by-file explanation.

## Running it locally

### 1. Database
Install PostgreSQL and create a database:
```bash
createdb lucidpoint
```

### 2. Backend
```bash
cd backend
# Set these however you prefer (env vars, IDE run config, or a .env loader)
export DB_HOST=localhost DB_PORT=5432 DB_NAME=lucidpoint DB_USER=postgres DB_PASSWORD=postgres
export JWT_SECRET=replace-this-with-a-long-random-string
export ANTHROPIC_API_KEY=sk-ant-...   # optional — omit it and AI endpoints return a clean 503 instead of crashing
mvn spring-boot:run
```
The API starts on `http://localhost:8080`. Hibernate will auto-create the tables
on first run (`ddl-auto: update` in `application.yml`).

> Note: this backend was written and organized here, but not compiled in this
> environment (no Maven Central access in this sandbox). Run `mvn compile` locally
> to catch any typos before your first real run — Spring Boot errors are usually
> very clear about what's missing.

### 3. Frontend
```bash
cd frontend
npm install
npm run dev
```
Opens on `http://localhost:5173`. It talks to the backend at
`http://localhost:8080/api` by default — override with a `.env` file containing
`VITE_API_URL=http://localhost:8080/api` if needed.

### 4. Try it
1. Go to `/register`, create an ADMIN account.
2. Use the Academic Structure API (`POST /api/academic/classes`, `.../sections`,
   `.../subjects`) to set up a class, section, and subject — no UI for this yet,
   use curl/Postman.
3. Enroll a student via `POST /api/students`.
4. Create an exam and enter a mark via `POST /api/exams` and `POST /api/exams/marks`.
5. Log in as that student (or just visit `/performance/<studentId>` as admin) to
   see the analytics dashboard render.

## What's built vs. what's next

See `ROADMAP.md` for the full picture — the mission expanded partway through
from a school-ops tool into an AI-powered knowledge ecosystem, and that doc
explains what changed and why.

**Built (Phase 1 — school ops):** Auth, Student/Class/Section/Subject
management, Exam & Marks entry, Attendance, Performance Analytics
(subject-wise averages, attendance %).

**Built (Phase 2 — knowledge ecosystem foundation):** `LEARNER` role for
platform-wide users with no school affiliation; a public `Resource` content
domain (articles/videos/problem sets/courses, readable with no login);
a `SubscriptionTier` (FREE/PREMIUM) entitlement scaffold; an AI service layer
(`POST /api/ai/problem-solving/hint`) giving hints/next-steps rather than
final answers, with FREE users capped at a few free hints/day.

**Not built yet (Phase 3+):** personalization/recommendations, creator
publishing workflow + revenue share, additional premium AI tools (study
planner, career guidance), real billing. `AnalyticsService` is designed as
the template personalization builds on, not something it replaces.
