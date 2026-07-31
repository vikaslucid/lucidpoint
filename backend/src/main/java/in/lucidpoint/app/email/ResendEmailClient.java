package in.lucidpoint.app.email;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

/** The only class that knows about Resend's wire format. Everything else talks to EmailClient. */
@Component
public class ResendEmailClient implements EmailClient {

    private final RestClient restClient = RestClient.create("https://api.resend.com");

    private final String apiKey;
    private final String fromAddress;

    public ResendEmailClient(
            @Value("${app.email.resend-api-key:}") String apiKey,
            @Value("${app.email.from-address}") String fromAddress) {
        this.apiKey = apiKey;
        this.fromAddress = fromAddress;
    }

    @Override
    public void send(String to, String subject, String htmlBody) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new EmailNotConfiguredException(
                    "Email isn't configured on this server yet — set the RESEND_API_KEY environment variable.");
        }

        try {
            restClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", fromAddress,
                            "to", List.of(to),
                            "subject", subject,
                            "html", htmlBody
                    ))
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException ex) {
            throw new EmailProviderException("The email provider returned an error: " + ex.getStatusCode(), ex);
        }
    }
}
