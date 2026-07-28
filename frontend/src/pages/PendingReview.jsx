import { useEffect, useState } from "react";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";
import ResourceCard from "../components/ResourceCard";

export default function PendingReview() {
  const [resources, setResources] = useState(null);
  const [error, setError] = useState("");
  const [notes, setNotes] = useState({}); // resourceId -> draft rejection note

  function load() {
    apiClient
      .get("/content/resources/pending")
      .then((res) => setResources(res.data))
      .catch(() => setError("Could not load the review queue."));
  }

  useEffect(load, []);

  async function approve(id) {
    try {
      await apiClient.post(`/content/resources/${id}/approve`);
      load();
    } catch (err) {
      setError(err.response?.data?.error || "Could not approve this resource.");
    }
  }

  async function reject(id) {
    try {
      await apiClient.post(`/content/resources/${id}/reject`, { reviewNote: notes[id] || null });
      load();
    } catch (err) {
      setError(err.response?.data?.error || "Could not reject this resource.");
    }
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2>Review Queue</h2>
        <p className="hint">Content submitted by creators, waiting to go live.</p>

        {error && <div className="error-banner">{error}</div>}
        {!resources && <p>Loading...</p>}
        {resources && resources.length === 0 && <p className="hint">Nothing pending review.</p>}

        <div className="resource-grid">
          {resources && resources.map((r) => (
            <ResourceCard
              key={r.id}
              resource={r}
              extra={
                <div className="review-actions">
                  <input
                    placeholder="Rejection reason (optional)"
                    value={notes[r.id] || ""}
                    onChange={(e) => setNotes({ ...notes, [r.id]: e.target.value })}
                  />
                  <div className="review-buttons">
                    <button onClick={() => approve(r.id)}>Approve</button>
                    <button className="button-danger" onClick={() => reject(r.id)}>Reject</button>
                  </div>
                </div>
              }
            />
          ))}
        </div>
      </div>
    </>
  );
}
