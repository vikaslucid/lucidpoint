-- Seeds one demo class/section/subjects/teacher/student with exams, marks, and
-- attendance, so /performance/{studentId} and the academic-structure APIs have
-- something to show instead of an empty dashboard. See V2__seed_public_resources.sql
-- for why this is deliberately careful about login accounts: this migration runs
-- against every environment the app deploys to, production included, so the
-- teacher and student rows below are disabled placeholders (enabled = false,
-- random undistributed password hashes) — not accounts anyone can sign in as.
--
-- Note for whoever picks this up next: /api/students/{id} and
-- /api/analytics/student/{studentId} currently have no ownership check — any
-- authenticated user can view any student's record by ID (see StudentController
-- / AnalyticsController — neither has @PreAuthorize or a role/ownership guard).
-- That's a pre-existing gap unrelated to this seed data, but it's exactly why a
-- demo *can* work with zero new login-capable accounts: once you register any
-- real account and log in, visiting /performance/1 shows this seeded student's
-- analytics without needing to authenticate as them.

INSERT INTO public.school_classes (name) VALUES ('Grade 9');

INSERT INTO public.sections (name, school_class_id)
VALUES ('A', (SELECT id FROM public.school_classes WHERE name = 'Grade 9'));

INSERT INTO public.subjects (name, code) VALUES
    ('Mathematics', 'MATH101'),
    ('Science', 'SCI101'),
    ('English', 'ENG101');

INSERT INTO public.users (email, password, full_name, role, subscription_tier, enabled, created_at)
VALUES (
    'demo.teacher@lucidpoint.in',
    '$2b$10$X8jxE.q1bG8koaghyDXv9e9H3L0ZvfHkmIHJDMQZFVY6l4.EDWr8a',
    'Rohan Mehta',
    'TEACHER',
    'FREE',
    false,
    CURRENT_TIMESTAMP
);

INSERT INTO public.users (email, password, full_name, role, subscription_tier, enabled, created_at)
VALUES (
    'demo.student@lucidpoint.in',
    '$2b$10$FxCWyDqoAAxN7Zz92cJqz.rqN4.PfZz2rm31dpe..kqYZJ7tUZ43W',
    'Aditi Rao',
    'STUDENT',
    'FREE',
    false,
    CURRENT_TIMESTAMP
);

INSERT INTO public.teachers (employee_code, user_id)
VALUES ('DEMO-T001', (SELECT id FROM public.users WHERE email = 'demo.teacher@lucidpoint.in'));

INSERT INTO public.students (admission_number, section_id, user_id, parent_user_id)
VALUES (
    'DEMO-S001',
    (SELECT id FROM public.sections WHERE name = 'A'),
    (SELECT id FROM public.users WHERE email = 'demo.student@lucidpoint.in'),
    NULL
);

-- Exam names are unique per subject so later inserts can look up exam_id by name alone.
INSERT INTO public.exams (name, exam_date, max_marks, school_class_id, subject_id) VALUES
    ('Mid-Term Exam — Mathematics', '2026-06-15', 100,
        (SELECT id FROM public.school_classes WHERE name = 'Grade 9'),
        (SELECT id FROM public.subjects WHERE code = 'MATH101')),
    ('Final Exam — Mathematics', '2026-07-20', 100,
        (SELECT id FROM public.school_classes WHERE name = 'Grade 9'),
        (SELECT id FROM public.subjects WHERE code = 'MATH101')),
    ('Mid-Term Exam — Science', '2026-06-15', 100,
        (SELECT id FROM public.school_classes WHERE name = 'Grade 9'),
        (SELECT id FROM public.subjects WHERE code = 'SCI101')),
    ('Final Exam — Science', '2026-07-20', 100,
        (SELECT id FROM public.school_classes WHERE name = 'Grade 9'),
        (SELECT id FROM public.subjects WHERE code = 'SCI101')),
    ('Mid-Term Exam — English', '2026-06-15', 100,
        (SELECT id FROM public.school_classes WHERE name = 'Grade 9'),
        (SELECT id FROM public.subjects WHERE code = 'ENG101')),
    ('Final Exam — English', '2026-07-20', 100,
        (SELECT id FROM public.school_classes WHERE name = 'Grade 9'),
        (SELECT id FROM public.subjects WHERE code = 'ENG101'));

-- Deliberately uneven scores across subjects (strong in English, weak in Science)
-- so the subject-wise breakdown on the analytics dashboard has something to show,
-- not a flat line.
INSERT INTO public.marks (marks_obtained, exam_id, student_id) VALUES
    (78, (SELECT id FROM public.exams WHERE name = 'Mid-Term Exam — Mathematics'),
        (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    (85, (SELECT id FROM public.exams WHERE name = 'Final Exam — Mathematics'),
        (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    (58, (SELECT id FROM public.exams WHERE name = 'Mid-Term Exam — Science'),
        (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    (64, (SELECT id FROM public.exams WHERE name = 'Final Exam — Science'),
        (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    (88, (SELECT id FROM public.exams WHERE name = 'Mid-Term Exam — English'),
        (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    (91, (SELECT id FROM public.exams WHERE name = 'Final Exam — English'),
        (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001'));

INSERT INTO public.attendance (date, status, student_id) VALUES
    ('2026-07-13', 'PRESENT', (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-14', 'PRESENT', (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-15', 'ABSENT',  (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-16', 'PRESENT', (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-17', 'LATE',    (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-20', 'PRESENT', (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-21', 'PRESENT', (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-22', 'PRESENT', (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-23', 'PRESENT', (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001')),
    ('2026-07-24', 'PRESENT', (SELECT id FROM public.students WHERE admission_number = 'DEMO-S001'));
