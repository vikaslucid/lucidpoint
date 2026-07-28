package in.lucidpoint.app.service;

import in.lucidpoint.app.ai.AiClient;
import in.lucidpoint.app.dto.StudyPlanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The second AI feature (ROADMAP.md §5 Phase 3) — a premium productivity tool, unlike the
 * problem-solving companion's free-tier-with-limit design. Access control is entirely
 * @PreAuthorize("hasAuthority('TIER_PREMIUM')") on the controller; there's no usage log here
 * because PREMIUM has no cap to enforce. Reuses AiClient as-is — the payoff of putting a
 * provider boundary in front of Anthropic earlier is that a second feature costs nothing
 * beyond a new system prompt.
 */
@Service
@RequiredArgsConstructor
public class StudyPlannerService {

    private static final String SYSTEM_PROMPT = """
            You are an expert academic study planner. Given a list of subjects/topics, a
            weekly time budget, and an optional target date, produce a realistic weekly study
            schedule. Prioritize active recall and practice problems over passive reading.
            Spread topics across the week rather than cramming one subject per session. If a
            target date is given, pace the plan to be ready by then. Keep the plan concise and
            scannable - a short list of sessions (subject, focus, suggested duration), not
            paragraphs.
            """;

    private final AiClient aiClient;

    public String createPlan(StudyPlanRequest request) {
        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append("Subjects/topics: ").append(request.getSubjects()).append('\n');
        userPrompt.append("Available time: ").append(request.getHoursPerWeek()).append(" hours/week\n");
        if (request.getTargetDate() != null) {
            userPrompt.append("Target date: ").append(request.getTargetDate()).append('\n');
        }
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            userPrompt.append("Additional context: ").append(request.getNotes()).append('\n');
        }

        return aiClient.complete(SYSTEM_PROMPT, userPrompt.toString());
    }
}
