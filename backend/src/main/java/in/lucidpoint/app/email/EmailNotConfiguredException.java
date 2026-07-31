package in.lucidpoint.app.email;

/** No email provider API key configured — a deployment/config problem, not a user error. */
public class EmailNotConfiguredException extends RuntimeException {
    public EmailNotConfiguredException(String message) {
        super(message);
    }
}
