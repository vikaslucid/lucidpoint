import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";
import ResourceCard from "../components/ResourceCard";
import { useAuth } from "../context/AuthContext";

/**
 * The free knowledge layer's landing page (ROADMAP.md §3.2). Deliberately public — not
 * wrapped in ProtectedRoute (see App.jsx) — since "free, high-quality resources accessible
 * to anyone" should be true in the UI, not just true of the API underneath it.
 */
export default function Resources() {
  const { user } = useAuth();
  const [resources, setResources] = useState(null);
  const [recommended, setRecommended] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    apiClient
      .get("/content/resources")
      .then((res) => setResources(res.data))
      .catch(() => setError("Could not load resources."));

    if (user) {
      apiClient
        .get("/content/resources/recommended")
        .then((res) => setRecommended(res.data))
        .catch(() => setRecommended([])); // non-fatal — recommendations are a bonus, not required
    }
  }, [user]);

  return (
    <>
      <Navbar />
      <div className="page">
        <div className="page-header">
          <h2>Free Resources</h2>
          {user && (user.role === "ADMIN" || user.role === "TEACHER") && (
            <Link to="/resources/new" className="button-link">+ New Resource</Link>
          )}
        </div>

        {error && <div className="error-banner">{error}</div>}

        {recommended && recommended.length > 0 && (
          <section>
            <h3>Recommended for you</h3>
            <div className="resource-grid">
              {recommended.map((r) => (
                <ResourceCard key={r.id} resource={r} />
              ))}
            </div>
          </section>
        )}

        <section>
          {recommended && recommended.length > 0 && <h3>All Resources</h3>}
          {!resources && <p>Loading...</p>}
          {resources && resources.length === 0 && <p className="hint">No published resources yet.</p>}
          <div className="resource-grid">
            {resources && resources.map((r) => (
              <ResourceCard key={r.id} resource={r} />
            ))}
          </div>
        </section>
      </div>
    </>
  );
}
