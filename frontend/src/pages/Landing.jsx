import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";

/**
 * Public entry point at "/". Unauthenticated visitors used to land straight on
 * the login form (via ProtectedRoute's redirect) with no explanation of what
 * LucidPoint is — this replaces that with an actual landing page. Logged-in
 * users are redirected past this to /dashboard (see App.jsx), so this only
 * ever renders for a first-time or signed-out visitor.
 */
export default function Landing() {
  return (
    <>
      <Navbar />
      <div className="page">
        <section className="hero">
          <h1>Think better. Solve problems. Grow.</h1>
          <p className="hero-subtitle">
            LucidPoint is a free knowledge platform with AI tools that help you learn
            by doing the thinking yourself — not by handing you the answer.
          </p>
          <div className="hero-cta">
            <Link to="/resources" className="button-link">Browse Free Resources</Link>
            <Link to="/register" className="button-link button-link-outline">Get Started Free</Link>
          </div>
        </section>

        <section className="feature-grid">
          <div className="feature-card">
            <h3>Free Resources</h3>
            <p>Articles, problem sets, and courses — readable by anyone, no account required.</p>
          </div>
          <div className="feature-card">
            <h3>AI Problem-Solving Companion</h3>
            <p>Get guiding hints on your own attempt instead of a final answer, so you build the skill, not just the solution.</p>
          </div>
          <div className="feature-card">
            <h3>AI Study Planner</h3>
            <p>Turn your subjects and available time into a realistic weekly study schedule. Premium.</p>
          </div>
          <div className="feature-card">
            <h3>School Tools</h3>
            <p>Schools can track exams, attendance, and subject-wise performance analytics for every student.</p>
          </div>
        </section>

        <p className="hint">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </>
  );
}
