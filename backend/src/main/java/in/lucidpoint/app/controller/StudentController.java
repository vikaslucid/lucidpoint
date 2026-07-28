package in.lucidpoint.app.controller;

import in.lucidpoint.app.entity.Student;
import in.lucidpoint.app.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Student enroll(@RequestBody Map<String, String> body) {
        return studentService.enrollStudent(
                body.get("fullName"),
                body.get("email"),
                body.get("tempPassword"),
                body.get("admissionNumber"),
                Long.valueOf(body.get("sectionId"))
        );
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public List<Student> listInSection(@PathVariable Long sectionId) {
        return studentService.listStudentsInSection(sectionId);
    }

    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return studentService.getById(id);
    }
}
