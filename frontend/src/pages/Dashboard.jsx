import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";

/**
 * A single Dashboard component that renders different content per role,
 * rather than three near-duplicate page components. As each role's feature
 * set grows (teacher marks entry, parent PTM reports, etc.) these branches
 * can be split into their own components/routes.
 */
export default function Dashboard() {
  const { user } = useAuth();

  return (
    <>
      <Navbar />
      <div className="page">
        <h2>Welcome, {user.fullName}</h2>

        {user.role === "ADMIN" && (
          <p>Set up classes, sections, subjects, and enroll students from the Academic Structure and Students APIs.</p>
        )}
        {user.role === "TEACHER" && (
          <p>Create exams, enter marks, and record attendance for your classes.</p>
        )}
        {user.role === "STUDENT" && (
          <p>View your exam results, attendance, and subject-wise performance.</p>
        )}
        {user.role === "PARENT" && (
          <p>Track your child's academic performance and attendance.</p>
        )}
        {user.role === "LEARNER" && (
          <p>No school needed — browse free resources and use the AI tools below whenever you want to learn something.</p>
        )}

        <div className="dashboard-links">
          <Link to="/resources">Browse Resources &rarr;</Link>
          <Link to="/ai/problem-solving">Problem-Solving Companion &rarr;</Link>
          <Link to="/ai/study-planner">Study Planner (Premium) &rarr;</Link>
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
