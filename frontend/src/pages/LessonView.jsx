import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

const TIERS = [
  { key: "BEGINNER", label: "Beginner" },
  { key: "INTERMEDIATE", label: "Intermediate" },
  { key: "ADVANCED", label: "Advanced" },
];

/**
 * The "guiding medium, not overwhelming" reading experience: show the concept explanation
 * first, then let the student pick a difficulty tier and work through that tier's questions
 * one at a time (not a scrollable wall of fifty questions like a Resource PROBLEM_SET).
 *
 * Each answer is graded instantly client-side for immediate feedback, and also submitted to
 * the server (POST .../attempt) which independently re-grades it, logs it to the student's
 * activity history, and awards points — the server never trusts a client-reported "was I
 * right" flag for anything that affects the points total (see LessonService.recordAttempt).
 */
export default function LessonView() {
  const { id } = useParams();
  const [lesson, setLesson] = useState(null);
  const [error, setError] = useState("");
  const [tier, setTier] = useState(null);
  const [step, setStep] = useState(0);
  const [showHint, setShowHint] = useState(false);
  const [selected, setSelected] = useState(null);
  const [answered, setAnswered] = useState(false);
  const [correctCount, setCorrectCount] = useState(0);
  const [sessionPoints, setSessionPoints] = useState(0);
  const [lastAwarded, setLastAwarded] = useState(null); // points from the most recent attempt, or null while pending

  useEffect(() => {
    apiClient
      .get(`/content/lessons/${id}`)
      .then((res) => setLesson(res.data))
      .catch(() => setError("This lesson doesn't exist or isn't published."));
  }, [id]);

  if (error) return (<><Navbar /><div className="page"><p className="error-banner">{error}</p></div></>);
  if (!lesson) return (<><Navbar /><div className="page">Loading...</div></>);

  const questionsByTier = Object.fromEntries(
    TIERS.map((t) => [t.key, lesson.questions.filter((q) => q.difficulty === t.key)])
  );
  const questions = tier ? questionsByTier[tier] : [];
  const question = questions[step];
  const isLast = step === questions.length - 1;
  const done = tier && step >= questions.length;

  function startTier(key) {
    setTier(key);
    setStep(0);
    setCorrectCount(0);
    setSessionPoints(0);
    resetQuestionState();
  }

  function resetQuestionState() {
    setShowHint(false);
    setSelected(null);
    setAnswered(false);
    setLastAwarded(null);
  }

  function checkAnswer(value) {
    setSelected(value);
    setAnswered(true);
    const correct = value.trim().toLowerCase() === question.correctAnswer.trim().toLowerCase();
    if (correct) setCorrectCount((c) => c + 1);

    apiClient
      .post(`/content/lessons/${id}/questions/${question.id}/attempt`, { selectedAnswer: value })
      .then((res) => {
        setLastAwarded(res.data.pointsAwarded);
        setSessionPoints((p) => p + res.data.pointsAwarded);
      })
      .catch(() => setLastAwarded(0)); // non-fatal — the instant client-side feedback above already stands
  }

  function next() {
    resetQuestionState();
    setStep((s) => s + 1);
  }

  const isCorrect = answered && selected.trim().toLowerCase() === question?.correctAnswer?.trim().toLowerCase();

  return (
    <>
      <Navbar />
      <div className="page lesson-page">
        <Link to="/lessons" className="back-link">&larr; Back to Lessons</Link>

        {lesson.grade && <span className="badge badge-meta">Grade {lesson.grade}</span>}
        {lesson.subject && <span className="badge badge-meta">{lesson.subject}</span>}
        <h2>{lesson.title}</h2>
        <p className="resource-author">by {lesson.author.fullName}</p>

        {!tier && (
          <>
            <div className="lesson-concept">{lesson.concept}</div>
            <div className="lesson-tier-picker">
              {TIERS.map((t) => (
                <button
                  key={t.key}
                  disabled={questionsByTier[t.key].length === 0}
                  onClick={() => startTier(t.key)}
                >
                  {t.label} ({questionsByTier[t.key].length} question{questionsByTier[t.key].length === 1 ? "" : "s"})
                </button>
              ))}
            </div>
          </>
        )}

        {tier && !done && question && (
          <div className="lesson-question-card">
            <p className="lesson-progress">
              {TIERS.find((t) => t.key === tier).label} — Question {step + 1} of {questions.length}
            </p>
            <p className="lesson-prompt">{question.prompt}</p>

            {question.hint && !answered && (
              showHint
                ? <p className="lesson-hint">Hint: {question.hint}</p>
                : <button className="link-button" onClick={() => setShowHint(true)}>Show hint</button>
            )}

            {question.options && question.options.length > 0 ? (
              <div className="lesson-options">
                {question.options.map((opt) => (
                  <button
                    key={opt}
                    disabled={answered}
                    className={
                      "lesson-option" +
                      (answered && opt === question.correctAnswer ? " lesson-option-correct" : "") +
                      (answered && opt === selected && !isCorrect ? " lesson-option-incorrect" : "")
                    }
                    onClick={() => checkAnswer(opt)}
                  >
                    {opt}
                  </button>
                ))}
              </div>
            ) : (
              !answered && (
                <form
                  onSubmit={(e) => {
                    e.preventDefault();
                    checkAnswer(new FormData(e.target).get("answer") || "");
                  }}
                >
                  <input name="answer" placeholder="Type your answer" autoFocus />
                  <button type="submit">Check</button>
                </form>
              )
            )}

            {answered && (
              <div className={"lesson-feedback " + (isCorrect ? "lesson-feedback-correct" : "lesson-feedback-incorrect")}>
                <p>
                  {isCorrect ? "Correct!" : `Not quite — the correct answer is "${question.correctAnswer}".`}
                  {isCorrect && lastAwarded != null && <span className="lesson-points-earned"> +{lastAwarded} points</span>}
                </p>
                {question.explanation && <p className="lesson-explanation">{question.explanation}</p>}
                <button onClick={next}>{isLast ? "See results" : "Next question"}</button>
              </div>
            )}
          </div>
        )}

        {done && (
          <div className="lesson-question-card">
            <p className="lesson-progress">Done with {TIERS.find((t) => t.key === tier).label}!</p>
            <p className="lesson-prompt">
              You got {correctCount} out of {questions.length} right, earning {sessionPoints} point{sessionPoints === 1 ? "" : "s"}.
            </p>
            <button onClick={() => setTier(null)}>Try another difficulty</button>
          </div>
        )}
      </div>
    </>
  );
}
