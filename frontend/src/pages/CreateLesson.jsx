import { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

const BLANK_QUESTION = { prompt: "", diagramSvg: "", difficulty: "BEGINNER", options: "", correctAnswer: "", hint: "", explanation: "" };

export default function CreateLesson() {
  const [title, setTitle] = useState("");
  const [summary, setSummary] = useState("");
  const [concept, setConcept] = useState("");
  const [grade, setGrade] = useState("");
  const [subject, setSubject] = useState("");
  const [sourceYear, setSourceYear] = useState("");
  const [questions, setQuestions] = useState([{ ...BLANK_QUESTION }]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  function updateQuestion(index, field, value) {
    setQuestions((qs) => qs.map((q, i) => (i === index ? { ...q, [field]: value } : q)));
  }

  function addQuestion() {
    setQuestions((qs) => [...qs, { ...BLANK_QUESTION }]);
  }

  function removeQuestion(index) {
    setQuestions((qs) => qs.filter((_, i) => i !== index));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await apiClient.post("/content/lessons", {
        title,
        summary,
        concept,
        grade: grade ? Number(grade) : null,
        subject: subject || null,
        sourceYear: sourceYear ? Number(sourceYear) : null,
        questions: questions.map((q) => ({
          prompt: q.prompt,
          diagramSvg: q.diagramSvg || null,
          difficulty: q.difficulty,
          options: q.options ? q.options.split("\n").map((o) => o.trim()).filter(Boolean) : [],
          correctAnswer: q.correctAnswer,
          hint: q.hint || null,
          explanation: q.explanation || null,
        })),
      });
      navigate("/lessons");
    } catch (err) {
      setError(err.response?.data?.error || "Could not create the lesson.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2>New Lesson</h2>
        <p className="hint">A short concept explanation, plus a handful of guided practice questions. Saved as a draft — submit it for review once you're happy with it.</p>

        <form className="form-card" onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}

          <label>
            Title
            <input value={title} onChange={(e) => setTitle(e.target.value)} required />
          </label>

          <label>
            Summary
            <input value={summary} onChange={(e) => setSummary(e.target.value)} required maxLength={255} />
          </label>

          <label>
            Concept explanation
            <textarea rows={6} value={concept} onChange={(e) => setConcept(e.target.value)} required
              placeholder="Explain the idea in plain language before the practice questions." />
          </label>

          <div className="form-row">
            <label>
              Grade (optional)
              <input type="number" min="1" max="12" value={grade} onChange={(e) => setGrade(e.target.value)} />
            </label>
            <label>
              Subject (optional)
              <input value={subject} onChange={(e) => setSubject(e.target.value)} placeholder="e.g. Mathematics" />
            </label>
            <label>
              Source year (optional)
              <input type="number" value={sourceYear} onChange={(e) => setSourceYear(e.target.value)} placeholder="e.g. 2019" />
            </label>
          </div>

          <h3>Practice questions</h3>
          {questions.map((q, i) => (
            <div key={i} className="lesson-question-editor">
              <div className="page-header">
                <strong>Question {i + 1}</strong>
                {questions.length > 1 && (
                  <button type="button" className="link-button" onClick={() => removeQuestion(i)}>Remove</button>
                )}
              </div>
              <label>
                Prompt
                <textarea rows={2} value={q.prompt} onChange={(e) => updateQuestion(i, "prompt", e.target.value)} required />
              </label>
              <label>
                Difficulty
                <select value={q.difficulty} onChange={(e) => updateQuestion(i, "difficulty", e.target.value)}>
                  <option value="BEGINNER">Beginner</option>
                  <option value="INTERMEDIATE">Intermediate</option>
                  <option value="ADVANCED">Advanced</option>
                </select>
              </label>
              <label>
                Diagram (optional — inline SVG markup, shown above the prompt)
                <textarea rows={3} value={q.diagramSvg} onChange={(e) => updateQuestion(i, "diagramSvg", e.target.value)}
                  placeholder="<svg viewBox='0 0 200 100'>...</svg>" />
              </label>
              <label>
                Options (one per line — leave blank for a free-response question)
                <textarea rows={3} value={q.options} onChange={(e) => updateQuestion(i, "options", e.target.value)} />
              </label>
              <label>
                Correct answer (must exactly match one option, if any)
                <input value={q.correctAnswer} onChange={(e) => updateQuestion(i, "correctAnswer", e.target.value)} required />
              </label>
              <label>
                Hint (optional)
                <input value={q.hint} onChange={(e) => updateQuestion(i, "hint", e.target.value)} />
              </label>
              <label>
                Explanation (optional, shown after answering)
                <textarea rows={2} value={q.explanation} onChange={(e) => updateQuestion(i, "explanation", e.target.value)} />
              </label>
            </div>
          ))}
          <button type="button" onClick={addQuestion}>+ Add another question</button>

          <button type="submit" disabled={loading}>
            {loading ? "Saving..." : "Save draft"}
          </button>
        </form>
      </div>
    </>
  );
}
