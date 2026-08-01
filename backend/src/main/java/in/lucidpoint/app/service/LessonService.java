package in.lucidpoint.app.service;

import in.lucidpoint.app.dto.LessonQuestionRequest;
import in.lucidpoint.app.dto.LessonRequest;
import in.lucidpoint.app.entity.Lesson;
import in.lucidpoint.app.entity.LessonQuestion;
import in.lucidpoint.app.entity.ResourceStatus;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.repository.LessonRepository;
import in.lucidpoint.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                    .prompt(q.getPrompt())
                    .options(q.getOptions() == null ? List.of() : q.getOptions())
                    .correctAnswer(q.getCorrectAnswer())
                    .hint(q.getHint())
                    .explanation(q.getExplanation())
                    .build());
        }
        return questions;
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
