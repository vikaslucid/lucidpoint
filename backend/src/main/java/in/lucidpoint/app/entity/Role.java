package in.lucidpoint.app.entity;

/**
 * The four user types in the system. Every endpoint's access rules are
 * ultimately decided by which of these a logged-in user holds.
 */
public enum Role {
    ADMIN,
    TEACHER,
    STUDENT,
    PARENT
}
