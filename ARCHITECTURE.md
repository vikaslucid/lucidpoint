# LucidPoint — Architecture

This document explains *why* the project is structured the way it is, and what
every file does. Read this alongside the code — it's written so you can explain
the design in an interview, not just point at working code.

---

## 1. High-level architecture

```
┌─────────────────┐        HTTPS/JSON        ┌──────────────────────┐        SQL       ┌──────────────┐
│  React Frontend │  ───────────────────────▶ │  Spring Boot Backend │ ───────────────▶ │  PostgreSQL  │
│  (Vite, :5173)  │  ◀─────────────────────── │  (REST API, :8080)   │ ◀─────────────── │              │
└─────────────────┘      JWT in header         └──────────────────────┘                  └──────────────┘
```

This is a classic **three-tier architecture**:
- **Presentation tier** — React SPA. Owns nothing about business rules; it just
  renders state and calls the API.
- **Application tier** — Spring Boot REST API. Owns all business logic, validation,
  and authorization. This is the only thing that talks to the database.
- **Data tier** — PostgreSQL. Just storage; no logic lives here (no stored procedures).

Why split it this way instead of one monolith (e.g. server-rendered Java pages)?
Two independently deployable pieces, a clean API boundary you can point a mobile
app at later, and it's the pattern most job postings for both "frontend" and
"backend" roles expect you to know.

### Request flow example (a teacher enters a mark)
1. React's `MarkEntry` form (not yet built — see README) calls `apiClient.post("/exams/marks", {...})`.
2. Axios's request interceptor (`api/client.js`) attaches `Authorization: Bearer <jwt>`.
3. Request hits Spring's `JwtAuthenticationFilter` — validates the token, loads the
   user, and puts them in `SecurityContextHolder` so the rest of the request "knows"
   who's calling.
4. `SecurityConfig`'s rules + `ExamController`'s `@PreAuthorize("hasAnyRole('ADMIN','TEACHER')")`
   check the user is allowed to hit this endpoint.
5. `ExamController.recordMark()` delegates to `ExamService.recordMark()`, which
   validates the exam/student exist and the mark doesn't exceed max marks, then
   saves via `MarkRepository`.
6. Hibernate translates that save into a SQL `INSERT`/`UPDATE` against `marks`.
7. Response flows back as JSON; if anything threw an exception,
   `GlobalExceptionHandler` turns it into a clean JSON error instead of a stack trace.

---

## 2. Backend — package-by-package

The backend follows a standard **layered architecture**: Controller → Service → Repository → Entity.
Each layer has one job, which makes the codebase testable (you can test a Service
without spinning up HTTP) and lets you swap layers independently later (e.g. add
caching in front of a Repository without touching Controllers).

```
in.lucidpoint.app
├── LucidpointApplication.java     Entry point — boots the whole app
├── config/          Framework wiring (security rules, CORS, beans)
├── controller/       HTTP layer — REST endpoints, request/response mapping
├── dto/              Data Transfer Objects — the shapes of JSON going in/out
├── entity/            JPA entities — the shapes of tables in the database
├── repository/       Spring Data interfaces — auto-generated DB queries
├── security/          JWT creation/validation, Spring Security integration
├── service/           Business logic — validation, orchestration, rules
└── exception/          Turns thrown exceptions into clean JSON error responses
```

### Why DTOs are separate from Entities
`RegisterRequest` (a DTO) and `User` (an entity) both have `fullName`/`email`, but
they're deliberately different classes. If a controller accepted a `User` directly
from the client, a malicious request could set `enabled=true` or `role=ADMIN`
directly. DTOs are a whitelist: only the fields you explicitly declare can come
in from the client, and only the fields you explicitly declare go back out.

### `entity/` — the data model
- **User** — the login identity for *any* human (admin/teacher/student/parent).
  Holds email, hashed password, role.
- **Student** / **Teacher** — domain profiles, each with a one-to-one link back to
  a `User`. Kept separate from `User` on purpose: "can this person log in" (User)
  is a different concern from "what class is this student in" (Student). If you
  later add SSO login, only `User`/security code changes — `Student` is untouched.
- **SchoolClass** → **Section** — a class (e.g. "Grade 10") has many sections
  (e.g. "A", "B"). Students belong to a Section.
- **Subject**, **Exam** — an Exam is one assessment event for one class+subject
  (e.g. "Mid-Term 2026, Grade 10 Maths, max marks 100").
- **Mark** — one student's score on one exam. Unique on `(exam_id, student_id)` —
  a student can't have two mark rows for the same exam (enforced at the DB level,
  not just in application code).
- **Attendance** — one status (PRESENT/ABSENT/LATE) per student per day. Unique on
  `(student_id, date)` for the same reason.

Entity relationship diagram (conceptual):
```
User ──1:1── Student ──N:1── Section ──N:1── SchoolClass
User ──1:1── Teacher                              │
                                                    │
Exam ──N:1── SchoolClass          Exam ──N:1── Subject
  │
  └──1:N── Mark ──N:1── Student

Student ──1:N── Attendance
```

### `repository/` — the data-access layer
These are plain interfaces extending `JpaRepository<Entity, IdType>`. Spring Data
JPA generates the implementation (and the SQL) at runtime just from the method
name — e.g. `findByEmail(String email)` becomes `SELECT * FROM users WHERE email = ?`.
No SQL is hand-written for simple lookups; this is the standard Spring pattern and
saves a lot of boilerplate.

### `security/` — authentication mechanics
- **JwtUtil** — creates a signed JWT at login (`generateToken`) and parses/validates
  one on every later request (`extractEmail`, `isTokenValid`). Signed with HMAC-SHA256
  using a secret from `application.yml` (which itself comes from an environment
  variable — never hardcoded).
- **JwtAuthenticationFilter** — a `OncePerRequestFilter` that runs before every
  controller. Reads the `Authorization` header, and if the token is valid, tells
  Spring Security "this request is from this user" by populating
  `SecurityContextHolder`. If there's no token, the request just continues
  unauthenticated — it's `SecurityConfig`'s job to then reject it if the endpoint
  requires auth.
- **UserPrincipal** — adapts our `User` entity to Spring Security's `UserDetails`
  interface, which is what the rest of Spring Security's machinery expects to work with.
- **CustomUserDetailsService** — the hook Spring Security calls during login to
  fetch "the account matching this email" so it can check the password.

### `config/SecurityConfig.java` — the access-control rulebook
This is the single place that decides:
- Sessions are **stateless** (`SessionCreationPolicy.STATELESS`) — no server-side
  session, no cookies. Every request must carry its own JWT. This is what makes
  the API horizontally scalable (any server instance can handle any request,
  since no session state is pinned to one machine).
- CSRF protection is **disabled** — CSRF attacks exploit cookie-based sessions;
  since we use bearer tokens in headers (which browsers don't attach automatically
  the way they do cookies), CSRF isn't a relevant threat model here.
- `/api/auth/**` is public; everything else requires a valid JWT.
- `@EnableMethodSecurity` turns on `@PreAuthorize("hasRole('ADMIN')")` annotations
  on controller methods, for fine-grained per-endpoint role checks.

### `service/` — where the actual business rules live
Controllers stay thin (map HTTP → method call → HTTP response); all the "what does
this actually mean" logic lives here. E.g. `ExamService.recordMark()`:
- Looks up the exam and student (throwing a clean error if either doesn't exist)
- Rejects a mark greater than the exam's max marks (a business rule, not a DB constraint)
- **Upserts** — if the student already has a mark for this exam, it updates instead
  of creating a duplicate row, so a teacher correcting a typo doesn't create ghost records.

`AnalyticsService` is the most "interesting" file in the backend — it's the seed
of the "AI/analytics" part of the product. It takes raw `Mark` and `Attendance` rows
and aggregates them into percentages: subject-wise average, overall average,
attendance %. Phase 2 features (learning-gap prediction, AI-generated feedback text)
are designed to build on top of *these same aggregates* rather than recomputing
from scratch — e.g. "learning gap" could later be "subjects where this student's
average is >15% below their overall average," using data this service already computes.

### `exception/GlobalExceptionHandler.java`
Without this, an unhandled exception (bad ID, failed validation) would return
Spring's default HTML error page — useless for a JSON API. This intercepts
exceptions globally and returns consistent `{ timestamp, status, error }` JSON,
which the frontend can display directly.

---

## 3. Frontend — folder-by-folder

```
frontend/src
├── main.jsx              React entry point — mounts <App /> into the DOM
├── App.jsx                Route definitions
├── App.css / index.css    Styling
├── api/client.js           Axios instance — attaches JWT, handles 401s globally
├── context/AuthContext.jsx  Global login state (who's logged in, login/logout functions)
├── components/
│   ├── ProtectedRoute.jsx    Redirects to /login if not authenticated (or wrong role)
│   └── Navbar.jsx            Shared top bar
└── pages/
    ├── Login.jsx
    ├── Register.jsx
    ├── Dashboard.jsx          Role-aware landing page
    └── StudentPerformance.jsx  Charts, driven by GET /api/analytics/student/:id
```

### Why a `context/AuthContext.jsx` instead of prop-drilling
Login state (who's logged in, what's their role) is needed by almost every page —
the Navbar, every protected route, every role-conditional UI branch. Passing that
down as props through every intermediate component would be painful. React's
Context API lets any component call `useAuth()` and get `{ user, login, logout }`
directly, no matter how deep it is in the tree.

### Why `api/client.js` centralizes the Axios setup
Two cross-cutting concerns apply to *every* API call: attaching the JWT, and
reacting to an expired/invalid token (401) by logging the user out. Putting this
in one shared Axios instance (via interceptors) means no individual page ever has
to remember to do either — they just `import apiClient from "../api/client"` and
call `apiClient.get(...)`.

### Why `ProtectedRoute.jsx` wraps pages instead of checking auth in each page
Same reasoning as AuthContext: centralizing the "is this user allowed here" check
in one reusable wrapper component means each page component only has to worry
about its own content, not re-implementing an auth check.

### `pages/StudentPerformance.jsx`
Calls `GET /api/analytics/student/:id`, then renders the response with Recharts —
a stat-card summary (overall average, attendance %) plus a bar chart of subject-wise
averages. This is the page that visually demonstrates the "data-driven" part of
the product pitch.

---

## 4. Cross-cutting decisions worth being able to explain in an interview

- **Why JWT instead of session cookies?** Stateless auth scales horizontally
  without shared session storage, and works cleanly for a decoupled SPA + API
  (no CORS/cookie complications). Trade-off: tokens can't be "revoked" server-side
  as easily as a session — mitigated here by a relatively short 24h expiry.
- **Why PostgreSQL over MongoDB?** The data is inherently relational — students
  belong to sections, marks reference both an exam and a student, uniqueness
  constraints matter (one mark per student per exam). A relational DB enforces
  those integrity rules at the database level, not just in application code.
- **Why upsert semantics for Marks/Attendance?** Real-world usage: a teacher will
  fix a typo'd mark. Without upserts, that creates duplicate rows and every
  average calculation becomes ambiguous ("which mark row is correct?").
- **Why is business logic in Services, not Controllers?** Testability (you can
  unit-test `ExamService.recordMark()` without spinning up HTTP or a servlet
  container) and reuse (if you later add a bulk-CSV-import feature, it can call
  the same `ExamService` instead of duplicating the validation logic).

---

## 5. What's deliberately not built yet

Phase 1 stops at CRUD + analytics aggregation. Deliberately excluded from this
build (see README for the full list): AI-generated feedback text, PTM report
PDF generation, learning-gap prediction, and the various "Future Features".
The service-layer boundaries (`AnalyticsService` especially) are structured so
these can be added as new methods/services that consume existing data, rather
than requiring a rewrite.
