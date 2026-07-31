import { useState } from "react";
import { useNavigate, useSearchParams, Link } from "react-router-dom";
import apiClient from "../api/client";

export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token") || "";
  const [newPassword, setNewPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [done, setDone] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await apiClient.post("/auth/reset-password", { token, newPassword });
      setDone(true);
    } catch (err) {
      setError(err.response?.data?.error || "Could not reset your password.");
    } finally {
      setLoading(false);
    }
  }

  if (!token) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <h1>LucidPoint</h1>
          <p className="subtitle">Reset your password</p>
          <div className="error-banner">
            This reset link is missing its token — check the link from your email, or request a new one.
          </div>
          <p className="switch-link">
            <Link to="/forgot-password">Request a new link</Link>
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>LucidPoint</h1>
        <p className="subtitle">Choose a new password</p>

        {done ? (
          <>
            <p>Your password has been reset.</p>
            <button onClick={() => navigate("/login")}>Sign in</button>
          </>
        ) : (
          <form onSubmit={handleSubmit}>
            {error && <div className="error-banner">{error}</div>}
            <label>
              New password
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
                minLength={6}
              />
            </label>
            <button type="submit" disabled={loading}>
              {loading ? "Resetting..." : "Reset password"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
