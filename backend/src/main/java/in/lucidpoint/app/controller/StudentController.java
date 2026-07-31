package in.lucidpoint.app.controller;

import in.lucidpoint.app.entity.Student;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.security.UserPrincipal;
import in.lucidpoint.app.service.StudentService;
import in.lucidpoint.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final UserService userService;

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

    // Open to any authenticated user, not gated by @PreAuthorize like enroll()/listInSection()
    // above — StudentService.getById enforces per-request that only staff (ADMIN/TEACHER) or
    // the student themself/their linked parent can actually see the result.
    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getById(principal.getId());
        return studentService.getById(id, requester);
    }
}
