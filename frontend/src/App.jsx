import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import Dashboard from "./pages/Dashboard";
import StudentPerformance from "./pages/StudentPerformance";
import Resources from "./pages/Resources";
import ResourceDetail from "./pages/ResourceDetail";
import CreateResource from "./pages/CreateResource";
import Lessons from "./pages/Lessons";
import LessonView from "./pages/LessonView";
import CreateLesson from "./pages/CreateLesson";
import MyActivity from "./pages/MyActivity";
import MyResources from "./pages/MyResources";
import PendingReview from "./pages/PendingReview";
import ProblemSolvingCompanion from "./pages/ProblemSolvingCompanion";
import StudyPlanner from "./pages/StudyPlanner";
import "./App.css";

/**
 * "/" is the public landing page. A logged-in user hitting it is sent straight
 * to /dashboard instead of seeing marketing copy for a product they're already
 * inside of; everyone else sees Landing.
 */
function Home() {
  const { user } = useAuth();
  return user ? <Navigate to="/dashboard" replace /> : <Landing />;
}

/**
 * Route map for the whole app. AuthProvider wraps everything so any page can
 * call useAuth(). ProtectedRoute redirects to /login if there's no logged-in user.
 */
export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />

          <Route
            path="/dashboard"
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

          {/* Lessons: concept + a few guided questions, walked through one at a time */}
          <Route path="/lessons" element={<Lessons />} />
          <Route path="/lessons/:id" element={<LessonView />} />
          <Route
            path="/lessons/new"
            element={
              <ProtectedRoute allowedRoles={["ADMIN", "TEACHER"]}>
                <CreateLesson />
              </ProtectedRoute>
            }
          />
          <Route
            path="/activity"
            element={
              <ProtectedRoute>
                <MyActivity />
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
