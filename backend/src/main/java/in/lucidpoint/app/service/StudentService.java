package in.lucidpoint.app.service;

import in.lucidpoint.app.entity.Role;
import in.lucidpoint.app.entity.Section;
import in.lucidpoint.app.entity.Student;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.repository.SectionRepository;
import in.lucidpoint.app.repository.StudentRepository;
import in.lucidpoint.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Enrolling a student creates both a login-capable User (role=STUDENT) and
     * the Student profile row in one transaction-worthy step. An admin typically
     * calls this once during onboarding; the student changes their password later.
     */
    public Student enrollStudent(String fullName, String email, String tempPassword,
                                  String admissionNumber, Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));

        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .role(Role.STUDENT)
                .enabled(true)
                .build();
        userRepository.save(user);

        Student student = Student.builder()
                .user(user)
                .admissionNumber(admissionNumber)
                .section(section)
                .build();

        return studentRepository.save(student);
    }

    public List<Student> listStudentsInSection(Long sectionId) {
        return studentRepository.findBySectionId(sectionId);
    }

    public Student getById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
    }
}
