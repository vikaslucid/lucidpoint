import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";

const ROLE_COPY = {
  ADMIN: "Set up classes, sections, subjects, and enroll students from the Academic Structure and Students APIs.",
  TEACHER: "Create exams, enter marks, and record attendance for your classes.",
  STUDENT: "View your exam results, attendance, and subject-wise performance.",
  PARENT: "Track your child's academic performance and attendance.",
  LEARNER: "No school needed — browse free resources and use the AI tools below whenever you want to learn something.",
};

/** Quick-action cards, filtered per role below. Every href is a real route. */
const ACTIONS = [
  { icon: "\u{1F4DA}", title: "Browse Resources", desc: "Free articles, videos, and practice sets.", to: "/resources", roles: ["ADMIN", "TEACHER", "STUDENT", "PARENT", "LEARNER"] },
  { icon: "\u{270F}\u{FE0F}", title: "New Resource", desc: "Draft an article, video, or problem set.", to: "/resources/new", roles: ["ADMIN", "TEACHER"] },
  { icon: "\u{1F4C4}", title: "My Resources", desc: "Drafts and submissions you've authored.", to: "/resources/mine", roles: ["ADMIN", "TEACHER"] },
  { icon: "✅", title: "Review Queue", desc: "Approve or reject submitted content.", to: "/resources/pending", roles: ["ADMIN"] },
  { icon: "\u{1F9E9}", title: "Problem-Solving Companion", desc: "Get guided help working through a problem.", to: "/ai/problem-solving", roles: ["ADMIN", "TEACHER", "STUDENT", "PARENT", "LEARNER"] },
  { icon: "\u{1F5D3}\u{FE0F}", title: "Study Planner", desc: "AI-generated study schedules.", to: "/ai/study-planner", premium: true, roles: ["ADMIN", "TEACHER", "STUDENT", "PARENT", "LEARNER"] },
];

/**
 * A single Dashboard component that renders different content per role,
 * rather than three near-duplicate page components. As each role's feature
 * set grows (teacher marks entry, parent PTM reports, etc.) these branches
 * can be split into their own components/routes.
 */
export default function Dashboard() {
  const { user } = useAuth();
  const [publishedCount, setPublishedCount] = useState(null);
  const [mineCount, setMineCount] = useState(null);
  const [pendingCount, setPendingCount] = useState(null);

  useEffect(() => {
    apiClient
      .get("/content/resources")
      .then((res) => setPublishedCount(res.data.length))
      .catch(() => {}); // stats are a bonus, not required

    if (user.role === "ADMIN" || user.role === "TEACHER") {
      apiClient
        .get("/content/resources/mine")
        .then((res) => setMineCount(res.data.length))
        .catch(() => {});
    }

    if (user.role === "ADMIN") {
      apiClient
        .get("/content/resources/pending")
        .then((res) => setPendingCount(res.data.length))
        .catch(() => {});
    }
  }, [user.role]);

  const actions = ACTIONS.filter((a) => a.roles.includes(user.role));

  return (
    <>
      <Navbar />
      <div className="page">
        <div className="dash-greeting">
          <div>
            <h2>Welcome back, {user.fullName.split(" ")[0]}</h2>
            <p className="hint" style={{ margin: "4px 0 0" }}>{ROLE_COPY[user.role]}</p>
          </div>
          <span className="badge badge-type">{user.role}</span>
        </div>

        <div className="stat-cards">
          <div className="stat-card">
            <span className="stat-label">Published resources</span>
            <span className="stat-value">{publishedCount ?? "–"}</span>
          </div>
          {mineCount !== null && (
            <div className="stat-card">
              <span className="stat-label">Your resources</span>
              <span className="stat-value">{mineCount}</span>
            </div>
          )}
          {pendingCount !== null && (
            <div className="stat-card">
              <span className="stat-label">Awaiting review</span>
              <span className="stat-value">{pendingCount}</span>
            </div>
          )}
        </div>

        <div className="feature-grid dash-actions">
          {actions.map((a) => (
            <Link key={a.to} to={a.to} className="feature-card dash-action-card">
              <span className="dash-action-icon">{a.icon}</span>
              <h3>
                {a.title}
                {a.premium && <span className="badge badge-meta dash-premium-badge">Premium</span>}
              </h3>
              <p>{a.desc}</p>
            </Link>
          ))}
        </div>

        {user.role !== "LEARNER" && (
          <p className="hint">
            Tip: visit <code>/performance/&lt;studentId&gt;</code> to see the analytics dashboard for a specific student.
          </p>
        )}
      </div>
    </>
  );
}
