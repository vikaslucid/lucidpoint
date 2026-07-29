-- Seeds the public "free knowledge layer" (ROADMAP.md §3.2) so /api/content/resources
-- and the /resources page aren't empty on a fresh deploy. This migration runs against
-- every environment this app is deployed to, production included — there is no
-- separate seed/dev-only migration path in this repo — so it deliberately seeds
-- content only, not demo login accounts:
--
--   * The author row below is a disabled placeholder (enabled = false), not a usable
--     account. Its password is a bcrypt hash of a random value nobody was given —
--     irrelevant anyway, since a disabled user can't authenticate regardless of
--     password (see CustomUserDetailsService / UserPrincipal.isEnabled()).
--   * No fake students/teachers/marks/attendance are seeded here, to avoid putting
--     any additional login-capable account — demo or otherwise — on the live site.
--
-- An admin can reassign these resources to a real account later (update author_id),
-- or this row can stay as a permanent "LucidPoint Team" byline.

INSERT INTO public.users (email, password, full_name, role, subscription_tier, enabled, created_at)
VALUES (
    'content@lucidpoint.in',
    '$2b$10$kd5VlEgxuHLEWtOf.M1qEeDJEFwaTx0mZ18efJ.Rs1ZH0KZsZxPNC',
    'LucidPoint Content Team',
    'ADMIN',
    'FREE',
    false,
    CURRENT_TIMESTAMP
);

INSERT INTO public.resources (title, type, summary, body, author_id, status, created_at)
VALUES (
    'How to Build a Study Habit That Actually Sticks',
    'ARTICLE',
    'Five evidence-based habits for consistent, low-willpower studying.',
    E'Motivation is unreliable — it shows up when it wants to and disappears right before a deadline. A study habit that survives a bad week is built on structure, not willpower. Here are five habits worth stealing:\n\n1. Same time, same place. Studying at 7pm at your desk every day removes the daily decision of "when should I start?" — the decision itself is often the biggest source of procrastination.\n\n2. Start with two minutes. Commit to just opening the book and doing two minutes of work. Almost every session that starts this way keeps going well past two minutes, because starting is the hard part, not continuing.\n\n3. Study in short, focused blocks. 25-40 minutes of full attention beats two unfocused hours. Take a real break between blocks — walk around, don''t scroll.\n\n4. Review before you learn something new. Spend the first five minutes of a session recalling what you covered last time, without looking at your notes. This single habit (active recall) does more for long-term retention than rereading ever will.\n\n5. Track streaks, not hours. "I studied 14 days in a row" is a more motivating number than "I studied 40 hours" — streaks reward consistency, which is the actual goal.\n\nNone of these require more willpower than you have today. They just remove the moments where willpower is required in the first place.',
    (SELECT id FROM public.users WHERE email = 'content@lucidpoint.in'),
    'PUBLISHED',
    CURRENT_TIMESTAMP
);

INSERT INTO public.resources (title, type, summary, body, author_id, status, created_at)
VALUES (
    'Quadratic Equations: A Guided Problem Set',
    'PROBLEM_SET',
    'Ten practice problems on solving quadratics, from factoring to the quadratic formula — work them before checking the approach notes.',
    E'Work through these in order — they build from factoring to the general formula. Try each one yourself before reading the approach note beneath it.\n\n1. x^2 - 5x + 6 = 0\nApproach: Factor into two binomials whose constants multiply to 6 and add to -5.\n\n2. x^2 + 2x - 8 = 0\nApproach: Same factoring approach — look for two numbers multiplying to -8, adding to 2.\n\n3. 2x^2 - 8 = 0\nApproach: No middle term — isolate x^2 and take the square root of both sides.\n\n4. x^2 - 9x = 0\nApproach: Factor out x first; one root is always 0 in this pattern.\n\n5. x^2 + 4x + 4 = 0\nApproach: This is a perfect square trinomial — check if it factors to (x + a)^2.\n\n6. x^2 - 3x - 1 = 0\nApproach: Doesn''t factor with integers — use the quadratic formula, x = (-b +/- sqrt(b^2 - 4ac)) / 2a.\n\n7. 3x^2 + 5x - 2 = 0\nApproach: Leading coefficient isn''t 1 — try factoring by grouping, or go straight to the formula.\n\n8. x^2 - 6x + 9 = 0\nApproach: Another perfect square — one repeated root.\n\n9. 2x^2 + 3x + 5 = 0\nApproach: Compute the discriminant (b^2 - 4ac) first — if it''s negative, there''s no real solution.\n\n10. x^2 - 2x - 15 = 0\nApproach: Factor into two binomials whose constants multiply to -15, add to -2.\n\nIf you got stuck on any of these, the AI Problem-Solving Companion can walk through your attempt with you — it won''t give you the final answer, but it will help you find the next step.',
    (SELECT id FROM public.users WHERE email = 'content@lucidpoint.in'),
    'PUBLISHED',
    CURRENT_TIMESTAMP
);

INSERT INTO public.resources (title, type, summary, body, author_id, status, created_at)
VALUES (
    'Newton''s Three Laws of Motion, Explained Simply',
    'ARTICLE',
    'A plain-language walkthrough of inertia, force, and reaction pairs with everyday examples.',
    E'Newton''s three laws describe how objects move, and why — using only three ideas.\n\nFirst law (inertia): an object at rest stays at rest, and an object in motion stays in motion at a constant velocity, unless acted on by an outside force. This is why you lurch forward when a bus stops suddenly — your body was moving with the bus, and it keeps trying to move even after the bus stops.\n\nSecond law (F = ma): the force needed to accelerate an object depends on its mass. Push an empty shopping cart and a full one with the same force — the empty one speeds up faster, because acceleration = force / mass. Heavier objects need more force to get the same acceleration.\n\nThird law (action-reaction): every force has an equal and opposite reaction force. When you jump, you push down on the ground, and the ground pushes back up on you with equal force — that reaction is what launches you upward. A rocket works the same way: it pushes exhaust gas backward, and the gas pushes the rocket forward.\n\nTogether these three laws explain everything from why seatbelts matter (first law) to how rockets reach orbit (third law) without needing anything more advanced than arithmetic.',
    (SELECT id FROM public.users WHERE email = 'content@lucidpoint.in'),
    'PUBLISHED',
    CURRENT_TIMESTAMP
);

INSERT INTO public.resources (title, type, summary, body, author_id, status, created_at)
VALUES (
    'The Cornell Method: Taking Notes You''ll Actually Reuse',
    'ARTICLE',
    'A simple note-taking layout that turns lecture notes into a built-in study guide.',
    E'Most notes get written once and never opened again — because rereading a wall of unstructured text is unpleasant, so nobody does it. The Cornell method fixes this by building review into the page layout itself.\n\nSplit each page into three sections:\n\n- A narrow left column (about a third of the page width) — left blank while you take notes.\n- A wide right column — where your actual notes go during class, in whatever shorthand works for you.\n- A few lines at the bottom of the page — left blank until after class.\n\nRight after class (same day, ideally within a few hours), go back and fill in the left column with keywords or questions that each corresponding chunk of notes answers. Then write a 2-3 sentence summary of the whole page in the bottom section, in your own words.\n\nThe payoff comes at review time: cover the right column, look only at your left-column keywords/questions, and try to recall the content from memory. This turns every review session into active recall practice instead of passive rereading — and the bottom summary gives you a 30-second refresher before an exam without rereading the whole page.',
    (SELECT id FROM public.users WHERE email = 'content@lucidpoint.in'),
    'PUBLISHED',
    CURRENT_TIMESTAMP
);

INSERT INTO public.resources (title, type, summary, body, author_id, status, created_at)
VALUES (
    'Foundations of Algebra: A 4-Week Self-Study Path',
    'COURSE',
    'A self-paced four-week outline covering the algebra fundamentals most other math depends on.',
    E'This is a suggested pace, not a strict schedule — slow down on any week that needs it.\n\nWeek 1 — Expressions and equations: combining like terms, the distributive property, solving one- and two-step linear equations. Goal by the end of the week: solve any one-variable linear equation without help.\n\nWeek 2 — Linear equations and graphing: slope, the slope-intercept form (y = mx + b), graphing a line from an equation and writing an equation from a graph. Goal: given any two points, write the line''s equation.\n\nWeek 3 — Systems of equations: solving two linear equations together by substitution and elimination, and what it means graphically when lines intersect, are parallel, or overlap. Goal: solve a system both algebraically and by reasoning about the graph.\n\nWeek 4 — Quadratics: factoring, the quadratic formula, and graphing parabolas (vertex, axis of symmetry). Goal: solve a quadratic equation by at least two different methods and check they agree.\n\nPair this path with the Quadratic Equations problem set on this platform once you reach week 4, and use the AI Problem-Solving Companion any time you''re stuck on a specific step rather than jumping to a solved-example video.',
    (SELECT id FROM public.users WHERE email = 'content@lucidpoint.in'),
    'PUBLISHED',
    CURRENT_TIMESTAMP
);

-- Left PENDING_REVIEW (not PUBLISHED) so the admin review queue at /resources/pending
-- also has something in it to demonstrate the publishing workflow, rather than only
-- ever showing an empty state.
INSERT INTO public.resources (title, type, summary, body, author_id, status, created_at)
VALUES (
    'Time Management for Students Juggling School and Extracurriculars',
    'ARTICLE',
    'A practical approach to weekly planning when school, sports/clubs, and everything else compete for the same hours.',
    E'When every hour is already spoken for, the fix isn''t finding more hours — it''s deciding, once a week, where the ones you have actually go.\n\nStart each week with 15 minutes of planning, not daily. Write down every fixed commitment first (classes, practice, jobs), then block study time into what''s left, in the same way you''d block off a meeting. Unscheduled time reliably gets eaten by whatever feels urgent in the moment, which is rarely the thing that matters most.\n\nProtect one buffer block per week for whatever ran over — something always does. And review Sunday night, not Friday: five minutes looking at the week ahead prevents most Monday-morning scrambles.',
    (SELECT id FROM public.users WHERE email = 'content@lucidpoint.in'),
    'PENDING_REVIEW',
    CURRENT_TIMESTAMP
);
