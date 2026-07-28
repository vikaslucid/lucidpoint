package in.lucidpoint.app.service;

import in.lucidpoint.app.entity.SchoolClass;
import in.lucidpoint.app.entity.Section;
import in.lucidpoint.app.entity.Subject;
import in.lucidpoint.app.repository.SchoolClassRepository;
import in.lucidpoint.app.repository.SectionRepository;
import in.lucidpoint.app.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles the "school structure" building blocks: classes, sections, subjects.
 * These rarely change day-to-day (unlike marks/attendance), so they're grouped
 * in one service rather than three near-empty ones.
 */
@Service
@RequiredArgsConstructor
public class AcademicStructureService {

    private final SchoolClassRepository schoolClassRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;

    public SchoolClass createClass(String name) {
        return schoolClassRepository.save(SchoolClass.builder().name(name).build());
    }

    public List<SchoolClass> listClasses() {
        return schoolClassRepository.findAll();
    }

    public Section createSection(Long schoolClassId, String name) {
        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + schoolClassId));
        return sectionRepository.save(Section.builder().name(name).schoolClass(schoolClass).build());
    }

    public List<Section> listSectionsForClass(Long schoolClassId) {
        return sectionRepository.findBySchoolClassId(schoolClassId);
    }

    public Subject createSubject(String name, String code) {
        return subjectRepository.save(Subject.builder().name(name).code(code).build());
    }

    public List<Subject> listSubjects() {
        return subjectRepository.findAll();
    }
}
