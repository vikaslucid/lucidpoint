import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <nav className="navbar">
      <div className="nav-left">
        <Link to="/" className="brand">LucidPoint</Link>
        <Link to="/resources">Resources</Link>
        {user && (
          <>
            <Link to="/ai/problem-solving">AI Hint</Link>
            <Link to="/ai/study-planner">Study Planner</Link>
            {(user.role === "ADMIN" || user.role === "TEACHER") && (
              <Link to="/resources/mine">My Resources</Link>
            )}
            {user.role === "ADMIN" && <Link to="/resources/pending">Review Queue</Link>}
          </>
        )}
      </div>
      <div className="nav-right">
        {user ? (
          <>
            <span className="nav-user">{user.fullName} · {user.role}</span>
            <button onClick={handleLogout} className="link-button">Log out</button>
          </>
        ) : (
          <Link to="/login">Sign in</Link>
        )}
      </div>
    </nav>
  );
}
