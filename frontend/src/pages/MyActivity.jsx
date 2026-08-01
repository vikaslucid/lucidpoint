import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

/**
 * The student-facing log of every lesson question they've attempted (see LessonAttempt /
 * LessonController.myAttempts) — right or wrong, when, and how many points it earned. Points
 * are only ever awarded server-side (see LessonService.recordAttempt), so this list and the
 * running total both reflect what the server actually recorded, not just client-side state.
 */
export default function MyActivity() {
  const [attempts, setAttempts] = useState(null);
  const [points, setPoints] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([
      apiClient.get("/content/lessons/attempts/mine"),
      apiClient.get("/users/me"),
    ])
      .then(([attemptsRes, meRes]) => {
        setAttempts(attemptsRes.data);
        setPoints(meRes.data.points);
      })
      .catch(() => setError("Could not load your activity."));
  }, []);

  return (
    <>
      <Navbar />
      <div className="page">
        <h2>My Activity</h2>
        <p className="hint">Every lesson question you've attempted, and the points you've earned.</p>

        {error && <div className="error-banner">{error}</div>}

        {points != null && (
          <div className="points-summary">{points} point{points === 1 ? "" : "s"} total</div>
        )}

        {!attempts && !error && <p>Loading...</p>}
        {attempts && attempts.length === 0 && <p className="hint">No attempts yet — head to Lessons to start practicing.</p>}

        {attempts && attempts.length > 0 && (
          <table className="activity-table">
            <thead>
              <tr>
                <th>Lesson</th>
                <th>Question</th>
                <th>Your answer</th>
                <th>Result</th>
                <th>Points</th>
                <th>When</th>
              </tr>
            </thead>
            <tbody>
              {attempts.map((a) => (
                <tr key={a.id}>
                  <td><Link to={`/lessons/${a.lesson.id}`}>{a.lesson.title}</Link></td>
                  <td>{a.question.prompt}</td>
                  <td>{a.selectedAnswer || <em>(no answer)</em>}</td>
                  <td className={a.correct ? "activity-correct" : "activity-incorrect"}>
                    {a.correct ? "Correct" : "Incorrect"}
                  </td>
                  <td>{a.pointsAwarded}</td>
                  <td>{new Date(a.attemptedAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
