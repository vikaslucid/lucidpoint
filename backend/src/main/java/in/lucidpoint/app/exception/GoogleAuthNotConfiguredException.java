package in.lucidpoint.app.exception;

/** No GOOGLE_CLIENT_ID configured — a deployment/config problem, not a user error. */
public class GoogleAuthNotConfiguredException extends RuntimeException {
    public GoogleAuthNotConfiguredException(String message) {
        super(message);
    }
}
