package in.lucidpoint.app.entity;

/**
 * The creator publishing workflow (ROADMAP.md §3.5). A resource starts as DRAFT (visible only
 * to its author), moves to PENDING_REVIEW when the creator submits it, and an admin reviewer
 * moves it to PUBLISHED (publicly visible) or REJECTED (author can revise and resubmit).
 */
public enum ResourceStatus {
    DRAFT,
    PENDING_REVIEW,
    PUBLISHED,
    REJECTED
}
