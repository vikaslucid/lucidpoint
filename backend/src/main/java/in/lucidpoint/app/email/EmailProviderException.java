package in.lucidpoint.app.email;

/** The email provider itself returned an error. */
public class EmailProviderException extends RuntimeException {
    public EmailProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
