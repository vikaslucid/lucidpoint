package in.lucidpoint.app.ai;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

/**
 * The only class in the app that knows about Anthropic's wire format. Everything else talks
 * to AiClient. No SDK dependency — this is a small enough surface (one endpoint, one shape)
 * that a raw RestClient call is more transparent than pulling in a library for it.
 */
@Component
public class AnthropicAiClient implements AiClient {

    private final RestClient restClient = RestClient.create("https://api.anthropic.com");

    private final String apiKey;
    private final String model;
    private final int maxTokens;

    public AnthropicAiClient(
            @Value("${app.ai.anthropic-api-key:}") String apiKey,
            @Value("${app.ai.model}") String model,
            @Value("${app.ai.max-tokens}") int maxTokens) {
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiNotConfiguredException(
                    "AI features aren't configured on this server yet — set the ANTHROPIC_API_KEY environment variable.");
        }

        try {
            JsonNode response = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "model", model,
                            "max_tokens", maxTokens,
                            "system", systemPrompt,
                            "messages", List.of(Map.of("role", "user", "content", userPrompt))
                    ))
                    .retrieve()
                    .body(JsonNode.class);

            // Content blocks aren't always [text] — a "thinking" block can precede the text
            // block (seen with extended thinking enabled), so find the text block by type
            // rather than assuming index 0. Blindly reading content[0] silently returned ""
            // whenever thinking came first, since a thinking block has no "text" field.
            for (JsonNode block : response.path("content")) {
                if ("text".equals(block.path("type").asText())) {
                    return block.path("text").asText();
                }
            }
            throw new AiProviderException("The AI provider returned no text content", null);
        } catch (RestClientResponseException ex) {
            throw new AiProviderException("The AI provider returned an error: " + ex.getStatusCode(), ex);
        }
    }
}
