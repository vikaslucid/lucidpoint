# LucidPoint — Roadmap: From School-Ops Tool to Knowledge Ecosystem

This document exists because the mission changed shape. Phase 1 (see `README.md`)
built a school-operations MVP: a teacher records marks and attendance, an admin
manages the academic structure, a student sees their own analytics. That's real,
working software — but it's a *tool for one school's back office*, not what the
mission below describes. This doc is the bridge: what the mission actually
requires, what of Phase 1 survives, and in what order to build the rest.

---

## 1. The mission, unpacked

> Build LucidPoint as an AI-powered knowledge ecosystem — not an EdTech website.
> Help millions of students, teachers, and lifelong learners think better, solve
> problems, create projects, and grow through free, high-quality educational
> resources, while generating sustainable revenue from premium AI productivity
> tools, personalization, and creator services. Every feature should reinforce
> the mission of making knowledge accessible and empowering users to become
> better problem solvers rather than merely consuming information.

Read literally, that sentence contains a product architecture. Four claims,
each with a direct technical consequence:

| Claim | What it rules out | What it requires |
|---|---|---|
| "millions of students, teachers, and lifelong learners" | A system scoped to one school's roster (current `Student`/`Teacher` tied 1:1 to a school-specific `Section`) | An identity model where a person can exist on the platform *without* belonging to any school |
| "free, high-quality educational resources" | Everything gated behind admin-created, school-specific data | A public content domain — articles, problem sets, courses — independent of the school-ops domain |
| "premium AI productivity tools, personalization, and creator services" (the revenue side) | A single flat `Role` enum with no monetization concept | An entitlement/subscription layer orthogonal to `Role`, plus an AI service layer, plus a content-authoring/payout path for creators |
| "become better problem solvers rather than merely consuming information" | A chatbot that just answers questions | AI features deliberately designed around *process* (hints, step-by-step reasoning, critique of the user's own attempt) rather than *answers* |

That last row matters most and is the easiest to get wrong: the fastest thing
to build is a Q&A chatbot, and it's also the thing that violates the mission
statement's explicit "not merely consuming information" clause. Every AI
feature in this roadmap is scoped to keep the user in the loop, not replace them.

---

## 2. What survives from Phase 1, and what it becomes

Nothing gets thrown away. The school-ops MVP becomes **one vertical** inside
the ecosystem — "LucidPoint for Schools" — rather than the whole product. Concretely:

- `SchoolClass` / `Section` / `Subject` / `Exam` / `Mark` / `Attendance` stay
  exactly as they are. They model a real, already-solved problem (a school's
  internal record-keeping) and nothing about the new mission changes that need.
- `AnalyticsService` stops being "the whole analytics story" and becomes the
  **first data source feeding personalization** — see §4. This was already the
  intent (`ARCHITECTURE.md` §2 calls it "the seed of the AI/analytics part"),
  it just now has siblings instead of being the only thing.
- `User` / `Role` (ADMIN/TEACHER/STUDENT/PARENT) stay for the school vertical,
  but a platform-wide learner who never touches a school (the actual majority
  of "millions of ... lifelong learners") needs a path that doesn't require
  an admin to have enrolled them into a `Section` first. This is the first
  real architectural fork — see §3.

The instinct to "rename things and reposition the dashboard" (the option you
didn't pick) would have papered over this fork rather than resolving it, which
is why the roadmap starts here instead.

---

## 3. New domains the mission requires

Four new backend domains, each independent enough to build and ship separately:

### 3.1 Identity, generalized
Today, every `User` implies a role in exactly one school's hierarchy. A
platform-wide learner needs to sign up and get value with zero school
involvement. Two viable shapes:
- **Minimal**: add `Role.LEARNER` (no `Student` profile required — `Student`
  stays specifically "enrolled in a school"), and make `Section` optional
  context rather than a mandatory foreign key for anyone using the free
  content layer or AI tools.
- Keep `User` as the single login identity either way — that boundary
  (`ARCHITECTURE.md` §2, "why DTOs are separate from entities") already
  generalizes fine; it's `Student`'s hard dependency on `Section` that doesn't.

### 3.2 Content — the free knowledge layer
A new domain, deliberately separate from the academic-structure entities:
`Resource` (article/video/problem-set/course), authored by a `User` acting as
a creator, publicly readable with no auth required for the free tier. This is
the most mission-critical new domain and the cheapest to prove: a working
`GET /api/content` with no login requirement is a bigger statement of "not an
EdTech website" than any amount of dashboard copy editing.

### 3.3 AI service layer
A dedicated `ai/` package wrapping an LLM provider (Claude, via the Anthropic
API) behind an internal interface — never call the provider directly from a
controller. Two things this boundary buys immediately: (1) usage metering per
user, which the entitlement system in §3.4 needs to enforce free-tier limits,
and (2) the ability to swap or add providers later without touching feature
code. First feature to build here (see §5): a **problem-solving companion**
that returns hints/next-steps against a rubric, not a final answer, matching
the mission's "solve problems" language exactly.

### 3.4 Entitlements — the monetization spine
A `SubscriptionTier` (FREE/PREMIUM) on `User`, checked via a Spring Security
`@PreAuthorize`-style guard the same way `hasRole('ADMIN')` already works —
same pattern, new axis. Build this scaffold *before* any specific premium
feature exists, even with fake/manual tier assignment at first. The reason:
every feature built afterward (AI tool, personalization, creator payouts)
needs to answer "is this user allowed to do this" from day one, and retrofitting
that check across N already-shipped features is far more expensive than
designing every new endpoint against a `@RequiresTier(PREMIUM)` check that
exists from the start, even before real billing is wired up.

### 3.5 Creator services
A publishing workflow on top of §3.2's `Resource` domain: a creator submits
content, it goes through a review/approval state, and — once §3.4 has a real
billing provider behind it — revenue share gets tracked per resource. This is
correctly last: it depends on both the content domain existing and the
entitlement/billing spine being real, not scaffolded.

---

## 4. How personalization actually gets built (not a new domain — a consumer of existing ones)

Personalization isn't a fifth domain; it's a service that reads from the ones
above. `AnalyticsService` already aggregates `Mark`/`Attendance` into
subject-wise performance for school users. The same shape generalizes to
platform-wide learners once §3.2 (Content) exists: track which `Resource`s a
`User` engages with and how they perform on embedded problem sets, and
recommend next content the same way `AnalyticsService` already identifies
"subjects where this student's average is >15% below their overall average"
(`ARCHITECTURE.md` §2). This is precisely why Phase 1's analytics work wasn't
wasted — it's the template, not a dead end.

---

## 5. Phased build order

**Phase 1 — done.** School-ops MVP: auth, academic structure, exams/marks,
attendance, `AnalyticsService`.

**Phase 2 — prove the mission (smallest slice that isn't a school tool).**
1. Generalize identity (§3.1) — `Role.LEARNER`, `Section` becomes optional.
2. Content domain (§3.2) — `Resource` entity, public read endpoint, minimal
   creator-authoring endpoint (admin/creator-only write, no review workflow yet).
3. Entitlement scaffold (§3.4) — `SubscriptionTier` on `User`, one `@RequiresTier`
   guard, no real billing yet (manually flip a user to PREMIUM for testing).
4. AI service layer (§3.3) + **one** flagship feature: a problem-solving
   companion scoped to hints/reasoning-steps, gated behind the entitlement
   check from step 3 (free users get N free uses/day; this is the first place
   free vs. premium becomes real to a user, not just to the codebase).

This phase is deliberately small — four building blocks, one visible feature —
because it's the phase that either validates or falsifies the pivot. Everything
in Phase 3 assumes Phase 2's four domains exist and just builds more on top of them.

**Phase 3 — personalization, creators, and the rest of the original "Future
Features" list** (now re-homed under this architecture instead of being a
vague backlog): recommendation engine (§4), creator review/publishing workflow
(§3.5), additional premium AI tools (study planner, note synthesis, career
guidance — these were already in README's Phase 2 list; they now have a real
place to live: `ai/` package, gated by `@RequiresTier(PREMIUM)`).

**Phase 4 — monetize and scale.** Real billing provider behind §3.4 (Stripe or
similar) + webhook handling, creator payouts, and — only once there's
actual load data to justify it — performance work (caching, read replicas,
CDN for content). Deliberately last: premature scaling work for "millions of
users" before there's a single paying user is effort spent on the wrong risk.

---

## 6. What to build first

Given the phases above, the concrete next unit of work is **Phase 2, step 2**:
the `Resource` content domain with a public, unauthenticated read endpoint.
It's the highest-leverage first move because:
- It's independently shippable (no dependency on §3.1/3.3/3.4 to be *visible*
  to a user, even though the identity generalization needs to land alongside it).
- It's the single fastest way to make "free, high-quality educational
  resources" true in the running system rather than true in a mission statement.
- Every later phase (personalization, AI features, creator services) is built
  *on top of* content existing — building anything else first means building
  on a domain that doesn't exist yet.
