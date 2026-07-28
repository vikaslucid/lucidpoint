import { useState } from "react";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

/**
 * The flagship AI feature (ROADMAP.md §3.3). Deliberately doesn't render the response as
 * "the answer" — the backend's system prompt already refuses to give one; the UI just needs
 * to surface whatever hint/guiding question comes back, plus the two AI-specific failure
 * modes a FREE user can actually hit: 429 (daily limit) and 503 (not configured server-side).
 */
export default function ProblemSolvingCompanion() {
  const [problem, setProblem] = useState("");
  const [studentAttempt, setStudentAttempt] = useState("");
  const [hint, setHint] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setHint("");
    setLoading(true);
    try {
      const { data } = await apiClient.post("/ai/problem-solving/hint", { problem, studentAttempt });
      setHint(data.hint);
    } catch (err) {
      setError(err.response?.data?.error || "Could not get a hint right now.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2>Problem-Solving Companion</h2>
        <p className="hint">
          Describe a problem you're stuck on. You'll get a hint or a guiding question — not the
          final answer — so you build the skill of solving it yourself. Free accounts get a few
          hints per day; Premium is unlimited.
        </p>

        <form className="form-card" onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}

          <label>
            Problem
            <textarea
              rows={3}
              value={problem}
              onChange={(e) => setProblem(e.target.value)}
              placeholder="e.g. Solve for x: 2x + 3 = 11"
              required
            />
          </label>

          <label>
            What have you tried so far? (optional)
            <textarea
              rows={3}
              value={studentAttempt}
              onChange={(e) => setStudentAttempt(e.target.value)}
              placeholder="Share your attempt for more specific feedback"
            />
          </label>

          <button type="submit" disabled={loading}>
            {loading ? "Thinking..." : "Get a hint"}
          </button>
        </form>

        {hint && (
          <div className="ai-response">
            <h3>Hint</h3>
            <p>{hint}</p>
          </div>
        )}
      </div>
    </>
  );
}
