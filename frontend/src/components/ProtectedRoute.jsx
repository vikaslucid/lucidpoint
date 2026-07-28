import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * Wrap any page element with this to require login: <ProtectedRoute><Dashboard /></ProtectedRoute>
 * Pass allowedRoles to also restrict by role, e.g. allowedRoles={["ADMIN", "TEACHER"]}.
 */
export default function ProtectedRoute({ children, allowedRoles }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
