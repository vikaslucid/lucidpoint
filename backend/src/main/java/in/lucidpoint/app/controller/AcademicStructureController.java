package in.lucidpoint.app.controller;

import in.lucidpoint.app.entity.SchoolClass;
import in.lucidpoint.app.entity.Section;
import in.lucidpoint.app.entity.Subject;
import in.lucidpoint.app.service.AcademicStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
public class AcademicStructureController {

    private final AcademicStructureService academicStructureService;

    @PostMapping("/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public SchoolClass createClass(@RequestBody Map<String, String> body) {
        return academicStructureService.createClass(body.get("name"));
    }

    @GetMapping("/classes")
    public List<SchoolClass> listClasses() {
        return academicStructureService.listClasses();
    }

    @PostMapping("/classes/{classId}/sections")
    @PreAuthorize("hasRole('ADMIN')")
    public Section createSection(@PathVariable Long classId, @RequestBody Map<String, String> body) {
        return academicStructureService.createSection(classId, body.get("name"));
    }

    @GetMapping("/classes/{classId}/sections")
    public List<Section> listSections(@PathVariable Long classId) {
        return academicStructureService.listSectionsForClass(classId);
    }

    @PostMapping("/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public Subject createSubject(@RequestBody Map<String, String> body) {
        return academicStructureService.createSubject(body.get("name"), body.get("code"));
    }

    @GetMapping("/subjects")
    public List<Subject> listSubjects() {
        return academicStructureService.listSubjects();
    }
}
