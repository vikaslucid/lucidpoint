package in.lucidpoint.app.entity;

/**
 * The user types in the system. Every endpoint's access rules are ultimately
 * decided by which of these a logged-in user holds.
 *
 * ADMIN/TEACHER/STUDENT/PARENT are school-specific — STUDENT and TEACHER each
 * imply a domain profile (see Student/Teacher entities) tying them to a
 * SchoolClass/Section. LEARNER is deliberately the odd one out: a platform-wide
 * user with no school affiliation at all (see ROADMAP.md §3.1) — self-registration
 * already creates a bare User with no forced Student/Section link (AuthService
 * .register() never touches Student), so LEARNER just names that path correctly
 * instead of forcing "lifelong learner" users to misuse STUDENT.
 */
public enum Role {
    ADMIN,
    TEACHER,
    STUDENT,
    PARENT,
    LEARNER
}
