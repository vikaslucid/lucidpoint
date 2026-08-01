import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";
import { useAuth } from "../context/AuthContext";

/**
 * Browse page for Lesson content — a short concept explanation plus a handful of guided
 * practice questions, worked one at a time (see LessonView). Deliberately separate from
 * Resources: this is the "guiding medium" content type, not a full reference paper/problem set.
 */
export default function Lessons() {
  const { user } = useAuth();
  const [lessons, setLessons] = useState(null);
  const [error, setError] = useState("");
  const [gradeFilter, setGradeFilter] = useState("");
  const [subjectFilter, setSubjectFilter] = useState("");

  useEffect(() => {
    const params = {};
    if (gradeFilter) params.grade = gradeFilter;
    if (subjectFilter) params.subject = subjectFilter;

    apiClient
      .get("/content/lessons", { params })
      .then((res) => setLessons(res.data))
      .catch(() => setError("Could not load lessons."));
  }, [gradeFilter, subjectFilter]);

  return (
    <>
      <Navbar />
      <div className="page">
        <div className="page-header">
          <h2>Lessons</h2>
          {user && (user.role === "ADMIN" || user.role === "TEACHER") && (
            <Link to="/lessons/new" className="button-link">+ New Lesson</Link>
          )}
        </div>
        <p className="hint">A short explanation, then a few guided practice questions — one at a time.</p>

        {error && <div className="error-banner">{error}</div>}

        <div className="resource-filters">
          <label>
            Grade
            <select value={gradeFilter} onChange={(e) => setGradeFilter(e.target.value)}>
              <option value="">All</option>
              {Array.from({ length: 12 }, (_, i) => i + 1).map((g) => (
                <option key={g} value={g}>{g}</option>
              ))}
            </select>
          </label>
          <label>
            Subject
            <input
              value={subjectFilter}
              onChange={(e) => setSubjectFilter(e.target.value)}
              placeholder="e.g. Mathematics"
            />
          </label>
        </div>

        {!lessons && <p>Loading...</p>}
        {lessons && lessons.length === 0 && <p className="hint">No published lessons yet.</p>}
        <div className="resource-grid">
          {lessons && lessons.map((lesson) => (
            <div key={lesson.id} className="resource-card">
              <div className="resource-card-header">
                {lesson.grade && <span className="badge badge-meta">Grade {lesson.grade}</span>}
                {lesson.subject && <span className="badge badge-meta">{lesson.subject}</span>}
                <span className="badge badge-meta">{lesson.questions.length} question{lesson.questions.length === 1 ? "" : "s"}</span>
              </div>
              <h4>
                <Link to={`/lessons/${lesson.id}`}>{lesson.title}</Link>
              </h4>
              <p className="resource-summary">{lesson.summary}</p>
              <p className="resource-author">by {lesson.author.fullName}</p>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}
