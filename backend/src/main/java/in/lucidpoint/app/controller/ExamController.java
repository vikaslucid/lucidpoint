package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.MarkEntryRequest;
import in.lucidpoint.app.entity.Exam;
import in.lucidpoint.app.entity.Mark;
import in.lucidpoint.app.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Exam createExam(@RequestBody Map<String, String> body) {
        return examService.createExam(
                body.get("name"),
                Long.valueOf(body.get("schoolClassId")),
                Long.valueOf(body.get("subjectId")),
                LocalDate.parse(body.get("examDate")),
                Double.valueOf(body.get("maxMarks"))
        );
    }

    @GetMapping("/class/{classId}")
    public List<Exam> listForClass(@PathVariable Long classId) {
        return examService.listExamsForClass(classId);
    }

    @PostMapping("/marks")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Mark recordMark(@Valid @RequestBody MarkEntryRequest request) {
        return examService.recordMark(request);
    }

    @GetMapping("/marks/student/{studentId}")
    public List<Mark> marksForStudent(@PathVariable Long studentId) {
        return examService.listMarksForStudent(studentId);
    }
}
