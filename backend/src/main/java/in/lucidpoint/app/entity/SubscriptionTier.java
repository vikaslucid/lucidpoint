package in.lucidpoint.app.entity;

/**
 * Orthogonal to Role (ROADMAP.md §3.4): an ADMIN, TEACHER, STUDENT, PARENT, or
 * LEARNER can each independently be FREE or PREMIUM. No billing provider wired
 * up yet — tier is set manually (see UserController) until Phase 4 adds real
 * payment processing. Existing premium features should gate on this now anyway,
 * so nothing has to be retrofitted later.
 */
public enum SubscriptionTier {
    FREE,
    PREMIUM
}
