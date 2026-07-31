package in.lucidpoint.app.email;

/** Internal boundary in front of whichever email provider we actually send through. */
public interface EmailClient {
    void send(String to, String subject, String htmlBody);
}
