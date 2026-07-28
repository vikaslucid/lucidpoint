package in.lucidpoint.app.service;

import in.lucidpoint.app.ai.AiClient;
import in.lucidpoint.app.ai.AiUsageLimitExceededException;
import in.lucidpoint.app.entity.AiUsageLog;
import in.lucidpoint.app.entity.SubscriptionTier;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.repository.AiUsageLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * The flagship AI feature (ROADMAP.md §3.3, §5 Phase 2 step 4). Deliberately scoped around
 * hints and next-steps, never a final answer — the mission's "become better problem solvers
 * rather than merely consuming information" is enforced here in the system prompt actually
 * sent to the model, not just described in a planning doc.
 */
@Service
public class ProblemSolvingService {

    private static final String SYSTEM_PROMPT = """
            You are a patient tutor helping a student build their own problem-solving skills.
            You must never give the final answer to the problem they describe. Instead:
            - If they haven't shown an attempt yet, ask a guiding question or point them to the
              relevant concept/first step, without solving it for them.
            - If they've shown an attempt, give specific feedback on it and hint at the next
              step - do not complete the solution for them.
            Keep responses to 2-4 sentences.
            """;

    private final AiClient aiClient;
    private final AiUsageLogRepository aiUsageLogRepository;
    private final int freeTierDailyLimit;

    public ProblemSolvingService(AiClient aiClient,
                                  AiUsageLogRepository aiUsageLogRepository,
                                  @Value("${app.ai.free-tier-daily-limit}") int freeTierDailyLimit) {
        this.aiClient = aiClient;
        this.aiUsageLogRepository = aiUsageLogRepository;
        this.freeTierDailyLimit = freeTierDailyLimit;
    }

    public String getHint(User user, String problem, String studentAttempt) {
        boolean metered = user.getSubscriptionTier() == SubscriptionTier.FREE;
        if (metered) {
            enforceDailyLimit(user);
        }

        String userPrompt = (studentAttempt == null || studentAttempt.isBlank())
                ? "Problem: " + problem
                : "Problem: " + problem + "\n\nMy attempt so far: " + studentAttempt;

        // Record usage only after a successful call — a config/provider failure (503/502)
        // isn't the user's fault and shouldn't burn into their free daily quota.
        String hint = aiClient.complete(SYSTEM_PROMPT, userPrompt);
        if (metered) {
            recordUsage(user);
        }
        return hint;
    }

    private void enforceDailyLimit(User user) {
        int usedToday = aiUsageLogRepository.findByUserIdAndDate(user.getId(), LocalDate.now())
                .map(AiUsageLog::getCount)
                .orElse(0);

        if (usedToday >= freeTierDailyLimit) {
            throw new AiUsageLimitExceededException(
                    "You've used your " + freeTierDailyLimit
                            + " free hints for today. Upgrade to Premium for unlimited access.");
        }
    }

    private void recordUsage(User user) {
        LocalDate today = LocalDate.now();
        AiUsageLog usage = aiUsageLogRepository.findByUserIdAndDate(user.getId(), today)
                .orElseGet(() -> AiUsageLog.builder().user(user).date(today).count(0).build());
        usage.setCount(usage.getCount() + 1);
        aiUsageLogRepository.save(usage);
    }
}
