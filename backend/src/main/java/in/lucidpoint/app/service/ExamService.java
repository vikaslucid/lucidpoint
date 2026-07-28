package in.lucidpoint.app.service;

import in.lucidpoint.app.dto.MarkEntryRequest;
import in.lucidpoint.app.entity.*;
import in.lucidpoint.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final MarkRepository markRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    public Exam createExam(String name, Long schoolClassId, Long subjectId, LocalDate examDate, Double maxMarks) {
        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + schoolClassId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));

        Exam exam = Exam.builder()
                .name(name)
                .schoolClass(schoolClass)
                .subject(subject)
                .examDate(examDate)
                .maxMarks(maxMarks)
                .build();

        return examRepository.save(exam);
    }

    public List<Exam> listExamsForClass(Long schoolClassId) {
        return examRepository.findBySchoolClassId(schoolClassId);
    }

    /**
     * Records or updates one student's mark for an exam. Upsert semantics: teachers
     * re-submitting a corrected score shouldn't create duplicate rows.
     */
    public Mark recordMark(MarkEntryRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + request.getExamId()));
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + request.getStudentId()));

        if (request.getMarksObtained() > exam.getMaxMarks()) {
            throw new IllegalArgumentException("Marks obtained cannot exceed max marks for this exam");
        }

        Mark mark = markRepository.findByExamIdAndStudentId(exam.getId(), student.getId())
                .orElse(Mark.builder().exam(exam).student(student).build());
        mark.setMarksObtained(request.getMarksObtained());

        return markRepository.save(mark);
    }

    public List<Mark> listMarksForStudent(Long studentId) {
        return markRepository.findByStudentId(studentId);
    }
}
