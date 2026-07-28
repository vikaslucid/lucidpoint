import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import StudentPerformance from "./pages/StudentPerformance";
import Resources from "./pages/Resources";
import ResourceDetail from "./pages/ResourceDetail";
import CreateResource from "./pages/CreateResource";
import MyResources from "./pages/MyResources";
import PendingReview from "./pages/PendingReview";
import ProblemSolvingCompanion from "./pages/ProblemSolvingCompanion";
import StudyPlanner from "./pages/StudyPlanner";
import "./App.css";

/**
 * Route map for the whole app. AuthProvider wraps everything so any page can
 * call useAuth(). ProtectedRoute redirects to /login if there's no logged-in user.
 */
export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />

          <Route
            path="/performance/:studentId"
            element={
              <ProtectedRoute>
                <StudentPerformance />
              </ProtectedRoute>
            }
          />

          {/* Free knowledge layer (ROADMAP.md §3.2) — public, matching the API underneath it */}
          <Route path="/resources" element={<Resources />} />
          <Route path="/resources/:id" element={<ResourceDetail />} />

          <Route
            path="/resources/new"
            element={
              <ProtectedRoute allowedRoles={["ADMIN", "TEACHER"]}>
                <CreateResource />
              </ProtectedRoute>
            }
          />
          <Route
            path="/resources/mine"
            element={
              <ProtectedRoute allowedRoles={["ADMIN", "TEACHER"]}>
                <MyResources />
              </ProtectedRoute>
            }
          />
          <Route
            path="/resources/pending"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <PendingReview />
              </ProtectedRoute>
            }
          />

          <Route
            path="/ai/problem-solving"
            element={
              <ProtectedRoute>
                <ProblemSolvingCompanion />
              </ProtectedRoute>
            }
          />
          <Route
            path="/ai/study-planner"
            element={
              <ProtectedRoute>
                <StudyPlanner />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
