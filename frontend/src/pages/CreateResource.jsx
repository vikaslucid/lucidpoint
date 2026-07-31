import { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

export default function CreateResource() {
  const [title, setTitle] = useState("");
  const [type, setType] = useState("ARTICLE");
  const [summary, setSummary] = useState("");
  const [body, setBody] = useState("");
  const [externalUrl, setExternalUrl] = useState("");
  const [grade, setGrade] = useState("");
  const [subject, setSubject] = useState("");
  const [sourceYear, setSourceYear] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await apiClient.post("/content/resources", {
        title,
        type,
        summary,
        body,
        externalUrl: externalUrl || null,
        grade: grade ? Number(grade) : null,
        subject: subject || null,
        sourceYear: sourceYear ? Number(sourceYear) : null,
      });
      navigate("/resources/mine");
    } catch (err) {
      setError(err.response?.data?.error || "Could not create the resource.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2>New Resource</h2>
        <p className="hint">Saved as a draft — submit it for review from "My Resources" once you're happy with it.</p>

        <form className="form-card" onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}

          <label>
            Title
            <input value={title} onChange={(e) => setTitle(e.target.value)} required />
          </label>

          <label>
            Type
            <select value={type} onChange={(e) => setType(e.target.value)}>
              <option value="ARTICLE">Article</option>
              <option value="VIDEO">Video</option>
              <option value="PROBLEM_SET">Problem Set</option>
              <option value="COURSE">Course</option>
            </select>
          </label>

          <label>
            Summary
            <input value={summary} onChange={(e) => setSummary(e.target.value)} required />
          </label>

          <label>
            Body
            <textarea rows={8} value={body} onChange={(e) => setBody(e.target.value)} required />
          </label>

          <label>
            External link (optional — for videos/hosted courses)
            <input value={externalUrl} onChange={(e) => setExternalUrl(e.target.value)} />
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

          <button type="submit" disabled={loading}>
            {loading ? "Saving..." : "Save draft"}
          </button>
        </form>
      </div>
    </>
  );
}
