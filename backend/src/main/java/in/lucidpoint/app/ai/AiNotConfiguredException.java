package in.lucidpoint.app.ai;

/** No API key configured — a deployment/config problem, not a user error. Maps to 503. */
public class AiNotConfiguredException extends RuntimeException {
    public AiNotConfiguredException(String message) {
        super(message);
    }
}
