package in.lucidpoint.app.service;

import in.lucidpoint.app.dto.StudentPerformanceResponse;
import in.lucidpoint.app.dto.StudentPerformanceResponse.SubjectScore;
import in.lucidpoint.app.entity.Attendance;
import in.lucidpoint.app.entity.Mark;
import in.lucidpoint.app.entity.Student;
import in.lucidpoint.app.repository.AttendanceRepository;
import in.lucidpoint.app.repository.MarkRepository;
import in.lucidpoint.app.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the "AI/analytics" core of LucidPoint's MVP: it turns raw Mark and
 * Attendance rows into the aggregated numbers the Teacher/Parent/Student
 * dashboards actually display (subject-wise average %, overall average %,
 * attendance %). Later phases (learning-gap prediction, AI feedback text)
 * build on top of these same aggregates rather than replacing them.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MarkRepository markRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public StudentPerformanceResponse getStudentPerformance(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        List<Mark> marks = markRepository.findByStudentId(studentId);

        // Group marks by subject, then average each subject's percentage (marksObtained / maxMarks * 100)
        Map<String, List<Mark>> bySubject = marks.stream()
                .collect(Collectors.groupingBy(m -> m.getExam().getSubject().getName()));

        List<SubjectScore> subjectScores = bySubject.entrySet().stream()
                .map(entry -> new SubjectScore(
                        entry.getKey(),
                        average(entry.getValue())
                ))
                .collect(Collectors.toList());

        Double overallAverage = marks.isEmpty() ? null : average(marks);

        List<Attendance> attendance = attendanceRepository.findByStudentId(studentId);
        Double attendancePercentage = attendance.isEmpty() ? null : computeAttendancePercentage(attendance);

        return new StudentPerformanceResponse(
                student.getId(),
                student.getUser().getFullName(),
                overallAverage,
                attendancePercentage,
                subjectScores
        );
    }

    private double average(List<Mark> marks) {
        return marks.stream()
                .mapToDouble(m -> (m.getMarksObtained() / m.getExam().getMaxMarks()) * 100.0)
                .average()
                .orElse(0.0);
    }

    private double computeAttendancePercentage(List<Attendance> records) {
        long present = records.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT)
                .count();
        return (present * 100.0) / records.size();
    }
}
