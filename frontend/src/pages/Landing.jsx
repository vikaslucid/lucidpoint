import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";

/**
 * Public entry point at "/". Deliberately breaks out of the app's narrow `.page`
 * container (used by every internal screen) — a marketing page that's boxed into
 * the same 960px column as a dashboard reads as an app screen, not a front door.
 * The hero's comparison demo uses the actual wording the AI hint endpoint returns
 * (verified against a real Anthropic call), not invented copy — the whole pitch is
 * that this is a real product behavior, not a slogan.
 */
export default function Landing() {
  return (
    <>
      <Navbar />

      <section className="landing-hero">
        <div className="landing-hero-inner">
          <div>
            <span className="landing-eyebrow">Free. No school sign-up required.</span>
            <h1>Don't just get the answer. Learn to think.</h1>
            <p className="hero-subtitle">
              LucidPoint is a free knowledge platform with AI tools built to make you a
              better problem solver — not a faster answer-copier.
            </p>
            <div className="hero-cta">
              <Link to="/register" className="button-link">Get Started Free</Link>
              <Link to="/resources" className="button-link button-link-outline">Browse Free Resources</Link>
            </div>
            <p className="landing-trust">No credit card. No school sign-up. Just start.</p>
          </div>

          <div className="landing-demo">
            <div className="landing-demo-problem">
              A student asks:
              <strong>"Solve for x: 2x + 3 = 11"</strong>
            </div>
            <div className="landing-demo-row">
              <div className="landing-demo-bubble generic">
                <span className="landing-demo-label">Generic AI chatbot</span>
                x = 4
              </div>
              <div className="landing-demo-bubble lucid">
                <span className="landing-demo-label">LucidPoint</span>
                "What's the first step you'd take to isolate the term with x? Think
                about what operation would undo the '+3'."
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="landing-mission">
        <div className="landing-mission-inner">
          <h2>Most EdTech is a paywall. Most AI is a shortcut.</h2>
          <p>
            Neither actually helps you learn. Real understanding isn't handed to
            you — it's built by wrestling with a problem until it clicks. So our
            free content stays free, and our AI is designed to ask you the next
            question instead of skipping straight to the answer.
          </p>
        </div>
      </section>

      <section className="landing-pillars">
        <h2>What you actually get</h2>
        <div className="feature-grid">
          <div className="feature-card">
            <span className="landing-pillar-index">01</span>
            <h3>Free Resources, No Catch</h3>
            <p>Articles, problem sets, and courses — readable by anyone, no account, no paywall.</p>
          </div>
          <div className="feature-card">
            <span className="landing-pillar-index">02</span>
            <h3>AI That Teaches, Not Tells</h3>
            <p>Guiding hints on your own attempt instead of a final answer — you build the skill, not just get the solution.</p>
          </div>
          <div className="feature-card">
            <span className="landing-pillar-index">03</span>
            <h3>A Real Study Planner</h3>
            <p>Turn your subjects and available time into a realistic weekly schedule built around active recall. Premium.</p>
          </div>
          <div className="feature-card">
            <span className="landing-pillar-index">04</span>
            <h3>Built for Schools Too</h3>
            <p>Teachers track exams and attendance; students and parents see real subject-wise performance analytics.</p>
          </div>
        </div>
      </section>

      <section className="landing-cta-band">
        <h2>Start thinking better today.</h2>
        <p>It's free, it takes 30 seconds, and you don't need to belong to a school.</p>
        <div className="hero-cta">
          <Link to="/register" className="button-link">Get Started Free</Link>
        </div>
      </section>

      <p className="landing-signin">
        Already have an account? <Link to="/login">Sign in</Link>
      </p>
    </>
  );
}
