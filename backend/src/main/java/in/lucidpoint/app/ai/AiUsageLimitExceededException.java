package in.lucidpoint.app.ai;

/** A FREE-tier user hit their daily hint cap. Maps to 429 — this is a rate limit, not a bad request. */
public class AiUsageLimitExceededException extends RuntimeException {
    public AiUsageLimitExceededException(String message) {
        super(message);
    }
}
