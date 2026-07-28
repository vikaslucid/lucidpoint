package in.lucidpoint.app.ai;

/**
 * Internal boundary in front of whichever LLM provider we actually call (ROADMAP.md §3.3).
 * Nothing outside this package should know or care that it's currently Anthropic — swapping
 * or adding a provider later means implementing this interface again, not touching feature
 * code (ProblemSolvingService and anything built after it depends on this, not on Anthropic).
 */
public interface AiClient {
    String complete(String systemPrompt, String userPrompt);
}
