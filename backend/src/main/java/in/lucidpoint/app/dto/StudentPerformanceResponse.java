package in.lucidpoint.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Aggregated analytics for one student — the payload the "Performance Analytics"
 * frontend page renders as charts.
 */
@Getter
@AllArgsConstructor
public class StudentPerformanceResponse {
    private Long studentId;
    private String studentName;
    private Double averagePercentage;
    private Double attendancePercentage;
    private List<SubjectScore> subjectScores;

    @Getter
    @AllArgsConstructor
    public static class SubjectScore {
        private String subjectName;
        private Double averagePercentage;
    }
}
