import { useState } from "react";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

export default function StudyPlanner() {
  const [subjects, setSubjects] = useState("");
  const [hoursPerWeek, setHoursPerWeek] = useState(5);
  const [targetDate, setTargetDate] = useState("");
  const [notes, setNotes] = useState("");
  const [plan, setPlan] = useState("");
  const [error, setError] = useState("");
  const [needsUpgrade, setNeedsUpgrade] = useState(false);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setNeedsUpgrade(false);
    setPlan("");
    setLoading(true);
    try {
      const { data } = await apiClient.post("/ai/study-planner/plan", {
        subjects,
        hoursPerWeek: Number(hoursPerWeek),
        targetDate: targetDate || null,
        notes: notes || null,
      });
      setPlan(data.plan);
    } catch (err) {
      if (err.response?.status === 403) {
        setNeedsUpgrade(true);
      } else {
        setError(err.response?.data?.error || "Could not generate a study plan right now.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2>Study Planner</h2>
        <p className="hint">
          A Premium AI tool — describe what you're studying and how much time you have, and get
          a realistic weekly schedule built around active recall, not just re-reading notes.
        </p>

        <form className="form-card" onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}
          {needsUpgrade && (
            <div className="upgrade-banner">
              This is a Premium feature. Upgrade your account to generate study plans.
            </div>
          )}

          <label>
            Subjects / topics
            <input
              value={subjects}
              onChange={(e) => setSubjects(e.target.value)}
              placeholder="e.g. Algebra, Physics - mechanics, English essay writing"
              required
            />
          </label>

          <label>
            Hours available per week
            <input
              type="number"
              min={1}
              value={hoursPerWeek}
              onChange={(e) => setHoursPerWeek(e.target.value)}
              required
            />
          </label>

          <label>
            Target date (optional)
            <input type="date" value={targetDate} onChange={(e) => setTargetDate(e.target.value)} />
          </label>

          <label>
            Notes (optional)
            <textarea
              rows={3}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="Current level, known weak areas, constraints..."
            />
          </label>

          <button type="submit" disabled={loading}>
            {loading ? "Building your plan..." : "Generate plan"}
          </button>
        </form>

        {plan && (
          <div className="ai-response">
            <h3>Your Study Plan</h3>
            <p className="plan-text">{plan}</p>
          </div>
        )}
      </div>
    </>
  );
}
