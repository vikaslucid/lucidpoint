package in.lucidpoint.app.service;

import in.lucidpoint.app.dto.AttemptRequest;
import in.lucidpoint.app.dto.AttemptResponse;
import in.lucidpoint.app.dto.LessonQuestionRequest;
import in.lucidpoint.app.dto.LessonRequest;
import in.lucidpoint.app.entity.Lesson;
import in.lucidpoint.app.entity.LessonAttempt;
import in.lucidpoint.app.entity.LessonQuestion;
import in.lucidpoint.app.entity.QuestionDifficulty;
import in.lucidpoint.app.entity.ResourceStatus;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.repository.LessonAttemptRepository;
import in.lucidpoint.app.repository.LessonQuestionRepository;
import in.lucidpoint.app.repository.LessonRepository;
import in.lucidpoint.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Same DRAFT -> PENDING_REVIEW -> PUBLISHED/REJECTED workflow as ResourceService, applied to
 * Lesson instead. See Lesson for why this is a separate content type from Resource.
 */
@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final LessonQuestionRepository lessonQuestionRepository;
    private final LessonAttemptRepository lessonAttemptRepository;

    // Points per correct answer, scaled by difficulty — the whole point of tiering questions
    // is that advanced ones should be worth more than beginner ones.
    private static int pointsFor(QuestionDifficulty difficulty) {
        return switch (difficulty) {
            case BEGINNER -> 1;
            case INTERMEDIATE -> 2;
            case ADVANCED -> 3;
        };
    }

    public Lesson create(LessonRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + authorId));

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .concept(request.getConcept())
                .grade(request.getGrade())
                .subject(request.getSubject())
                .sourceYear(request.getSourceYear())
                .author(author)
                .build(); // status defaults to DRAFT

        List<LessonQuestion> questions = toQuestions(request.getQuestions(), lesson);
        lesson.setQuestions(questions);

        return lessonRepository.save(lesson);
    }

    private List<LessonQuestion> toQuestions(List<LessonQuestionRequest> requests, Lesson lesson) {
        List<LessonQuestion> questions = new java.util.ArrayList<>();
        int position = 0;
        for (LessonQuestionRequest q : requests) {
            questions.add(LessonQuestion.builder()
                    .lesson(lesson)
                    .position(position++)
                    .difficulty(parseDifficulty(q.getDifficulty()))
                    .prompt(q.getPrompt())
                    .diagramSvg(q.getDiagramSvg())
                    .options(q.getOptions() == null ? List.of() : q.getOptions())
                    .correctAnswer(q.getCorrectAnswer())
                    .hint(q.getHint())
                    .explanation(q.getExplanation())
                    .build());
        }
        return questions;
    }

    private QuestionDifficulty parseDifficulty(String raw) {
        if (raw == null || raw.isBlank()) {
            return QuestionDifficulty.BEGINNER;
        }
        return QuestionDifficulty.valueOf(raw.trim().toUpperCase());
    }

    // Server-side grading — the frontend already shows instant feedback client-side (it has the
    // correct answer in the payload anyway, see LessonQuestion), but points/log integrity comes
    // from checking correctness here rather than trusting a client-reported "was I right" flag.
    @Transactional
    public AttemptResponse recordAttempt(Long lessonId, Long questionId, AttemptRequest request, Long userId) {
        Lesson lesson = getPublishedById(lessonId);
        LessonQuestion question = lessonQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        if (!question.getLesson().getId().equals(lesson.getId())) {
            throw new IllegalArgumentException("Question does not belong to this lesson");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        String given = request.getSelectedAnswer() == null ? "" : request.getSelectedAnswer().trim();
        boolean correct = given.equalsIgnoreCase(question.getCorrectAnswer().trim());
        int points = correct ? pointsFor(question.getDifficulty()) : 0;

        lessonAttemptRepository.save(LessonAttempt.builder()
                .user(user)
                .lesson(lesson)
                .question(question)
                .selectedAnswer(request.getSelectedAnswer())
                .correct(correct)
                .pointsAwarded(points)
                .build());

        if (points > 0) {
            user.setPoints(user.getPoints() + points);
            userRepository.save(user);
        }

        return new AttemptResponse(correct, points, user.getPoints(), question.getCorrectAnswer(), question.getExplanation());
    }

    public List<LessonAttempt> listMyAttempts(Long userId) {
        return lessonAttemptRepository.findByUserIdOrderByAttemptedAtDesc(userId);
    }

    // Appends more questions to an already-existing lesson (e.g. going from 5 questions to 30
    // across three difficulty tiers) without touching the existing ones — deliberately additive
    // rather than a full replace, since replacing/removing questions that already have
    // LessonAttempt rows pointing at them would violate the FK on lesson_attempts.
    @Transactional
    public Lesson appendQuestions(Long lessonId, List<LessonQuestionRequest> newQuestions, Long requesterId) {
        Lesson lesson = getById(lessonId);
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + requesterId));
        boolean isAuthor = lesson.getAuthor().getId().equals(requesterId);
        if (!isAuthor && requester.getRole() != in.lucidpoint.app.entity.Role.ADMIN) {
            throw new IllegalArgumentException("Only the author or an admin can add questions to this lesson");
        }

        int nextPosition = lesson.getQuestions().stream()
                .mapToInt(LessonQuestion::getPosition)
                .max()
                .orElse(-1) + 1;

        for (LessonQuestionRequest q : newQuestions) {
            lessonQuestionRepository.save(LessonQuestion.builder()
                    .lesson(lesson)
                    .position(nextPosition++)
                    .difficulty(parseDifficulty(q.getDifficulty()))
                    .prompt(q.getPrompt())
                    .diagramSvg(q.getDiagramSvg())
                    .options(q.getOptions() == null ? List.of() : q.getOptions())
                    .correctAnswer(q.getCorrectAnswer())
                    .hint(q.getHint())
                    .explanation(q.getExplanation())
                    .build());
        }

        return getById(lessonId);
    }

    public Lesson submitForReview(Long id, Long requesterId) {
        Lesson lesson = getById(id);
        if (!lesson.getAuthor().getId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the author can submit this lesson for review");
        }
        if (lesson.getStatus() != ResourceStatus.DRAFT && lesson.getStatus() != ResourceStatus.REJECTED) {
            throw new IllegalArgumentException("Only a draft or rejected lesson can be submitted for review");
        }

        lesson.setStatus(ResourceStatus.PENDING_REVIEW);
        lesson.setReviewNote(null);
        return lessonRepository.save(lesson);
    }

    public Lesson approve(Long id) {
        Lesson lesson = requirePendingReview(id);
        lesson.setStatus(ResourceStatus.PUBLISHED);
        return lessonRepository.save(lesson);
    }

    public Lesson reject(Long id, String reviewNote) {
        Lesson lesson = requirePendingReview(id);
        lesson.setStatus(ResourceStatus.REJECTED);
        lesson.setReviewNote(reviewNote);
        return lessonRepository.save(lesson);
    }

    public List<Lesson> listPublished(Integer grade, String subject) {
        if (grade != null && subject != null) {
            return lessonRepository.findByStatusAndGradeAndSubject(ResourceStatus.PUBLISHED, grade, subject);
        }
        if (grade != null) {
            return lessonRepository.findByStatusAndGrade(ResourceStatus.PUBLISHED, grade);
        }
        if (subject != null) {
            return lessonRepository.findByStatusAndSubject(ResourceStatus.PUBLISHED, subject);
        }
        return lessonRepository.findByStatus(ResourceStatus.PUBLISHED);
    }

    public List<Lesson> listPending() {
        return lessonRepository.findByStatus(ResourceStatus.PENDING_REVIEW);
    }

    public List<Lesson> listMine(Long authorId) {
        return lessonRepository.findByAuthorId(authorId);
    }

    // Same "not found rather than 403" reasoning as ResourceService.getPublishedById — an
    // unpublished lesson's existence shouldn't be visible to a reader who can't see it.
    public Lesson getPublishedById(Long id) {
        Lesson lesson = getById(id);
        if (lesson.getStatus() != ResourceStatus.PUBLISHED) {
            throw new IllegalArgumentException("Lesson not found: " + id);
        }
        return lesson;
    }

    private Lesson getById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + id));
    }

    private Lesson requirePendingReview(Long id) {
        Lesson lesson = getById(id);
        if (lesson.getStatus() != ResourceStatus.PENDING_REVIEW) {
            throw new IllegalArgumentException("Lesson is not pending review");
        }
        return lesson;
    }
}
