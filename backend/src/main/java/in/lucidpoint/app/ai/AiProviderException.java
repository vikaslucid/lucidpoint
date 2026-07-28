package in.lucidpoint.app.ai;

/** The LLM provider itself returned an error (rate limit, outage, bad request). Maps to 502. */
public class AiProviderException extends RuntimeException {
    public AiProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
