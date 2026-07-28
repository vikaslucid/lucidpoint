package in.lucidpoint.app.service;

import in.lucidpoint.app.dto.AttendanceEntryRequest;
import in.lucidpoint.app.entity.Attendance;
import in.lucidpoint.app.entity.Student;
import in.lucidpoint.app.repository.AttendanceRepository;
import in.lucidpoint.app.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    /** Upsert: marking the same student+date twice corrects the earlier entry instead of duplicating it. */
    public Attendance markAttendance(AttendanceEntryRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + request.getStudentId()));

        Attendance attendance = attendanceRepository.findByStudentId(student.getId()).stream()
                .filter(a -> a.getDate().equals(request.getDate()))
                .findFirst()
                .orElse(Attendance.builder().student(student).date(request.getDate()).build());

        attendance.setStatus(request.getStatus());
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> historyForStudent(Long studentId, LocalDate from, LocalDate to) {
        return attendanceRepository.findByStudentIdAndDateBetween(studentId, from, to);
    }

    public List<Attendance> allForStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }
}
