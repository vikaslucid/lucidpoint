import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";
import ResourceCard from "../components/ResourceCard";

export default function MyResources() {
  const [resources, setResources] = useState(null);
  const [error, setError] = useState("");

  function load() {
    apiClient
      .get("/content/resources/mine")
      .then((res) => setResources(res.data))
      .catch(() => setError("Could not load your resources."));
  }

  useEffect(load, []);

  async function submit(id) {
    try {
      await apiClient.post(`/content/resources/${id}/submit`);
      load();
    } catch (err) {
      setError(err.response?.data?.error || "Could not submit for review.");
    }
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <div className="page-header">
          <h2>My Resources</h2>
          <Link to="/resources/new" className="button-link">+ New Resource</Link>
        </div>

        {error && <div className="error-banner">{error}</div>}
        {!resources && <p>Loading...</p>}
        {resources && resources.length === 0 && <p className="hint">You haven't created any resources yet.</p>}

        <div className="resource-grid">
          {resources && resources.map((r) => (
            <ResourceCard
              key={r.id}
              resource={r}
              extra={
                (r.status === "DRAFT" || r.status === "REJECTED") && (
                  <button onClick={() => submit(r.id)}>Submit for review</button>
                )
              }
            />
          ))}
        </div>
      </div>
    </>
  );
}
