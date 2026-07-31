import { useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await apiClient.post("/auth/forgot-password", { email });
      // The backend always returns success here regardless of whether the email matches
      // an account — same message either way, so this page can't be used to check who's
      // registered.
      setSubmitted(true);
    } catch (err) {
      setError(err.response?.data?.error || "Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>LucidPoint</h1>
        <p className="subtitle">Reset your password</p>

        {submitted ? (
          <p>If an account exists for that email, a reset link is on its way — check your inbox.</p>
        ) : (
          <form onSubmit={handleSubmit}>
            {error && <div className="error-banner">{error}</div>}
            <label>
              Email
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </label>
            <button type="submit" disabled={loading}>
              {loading ? "Sending..." : "Send reset link"}
            </button>
          </form>
        )}

        <p className="switch-link">
          <Link to="/login">Back to sign in</Link>
        </p>
      </div>
    </div>
  );
}
