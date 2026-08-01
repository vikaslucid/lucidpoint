import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

/**
 * The "guiding medium, not overwhelming" reading experience: show the concept explanation
 * first, then walk through the lesson's questions one at a time (not a scrollable wall of
 * fifty questions like a Resource PROBLEM_SET). A student can reveal a hint before answering,
 * gets immediate feedback + explanation after answering, and only sees the next question once
 * they're done with the current one.
 */
export default function LessonView() {
  const { id } = useParams();
  const [lesson, setLesson] = useState(null);
  const [error, setError] = useState("");
  const [started, setStarted] = useState(false);
  const [step, setStep] = useState(0); // index into lesson.questions
  const [showHint, setShowHint] = useState(false);
  const [selected, setSelected] = useState(null); // chosen option / typed answer, pre-submit
  const [answered, setAnswered] = useState(false);
  const [correctCount, setCorrectCount] = useState(0);

  useEffect(() => {
    apiClient
      .get(`/content/lessons/${id}`)
      .then((res) => setLesson(res.data))
      .catch(() => setError("This lesson doesn't exist or isn't published."));
  }, [id]);

  if (error) return (<><Navbar /><div className="page"><p className="error-banner">{error}</p></div></>);
  if (!lesson) return (<><Navbar /><div className="page">Loading...</div></>);

  const questions = lesson.questions;
  const question = questions[step];
  const isLast = step === questions.length - 1;
  const done = started && step >= questions.length;

  function resetQuestionState() {
    setShowHint(false);
    setSelected(null);
    setAnswered(false);
  }

  function checkAnswer(value) {
    setSelected(value);
    setAnswered(true);
    if (value.trim().toLowerCase() === question.correctAnswer.trim().toLowerCase()) {
      setCorrectCount((c) => c + 1);
    }
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

        {!started && (
          <>
            <div className="lesson-concept">{lesson.concept}</div>
            <button onClick={() => setStarted(true)}>
              Start practice ({questions.length} question{questions.length === 1 ? "" : "s"})
            </button>
          </>
        )}

        {started && !done && question && (
          <div className="lesson-question-card">
            <p className="lesson-progress">Question {step + 1} of {questions.length}</p>
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
                <p>{isCorrect ? "Correct!" : `Not quite — the correct answer is "${question.correctAnswer}".`}</p>
                {question.explanation && <p className="lesson-explanation">{question.explanation}</p>}
                <button onClick={next}>{isLast ? "See results" : "Next question"}</button>
              </div>
            )}
          </div>
        )}

        {done && (
          <div className="lesson-question-card">
            <p className="lesson-progress">Done!</p>
            <p className="lesson-prompt">You got {correctCount} out of {questions.length} right.</p>
          </div>
        )}
      </div>
    </>
  );
}
