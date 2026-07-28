import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

export default function ResourceDetail() {
  const { id } = useParams();
  const [resource, setResource] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    apiClient
      .get(`/content/resources/${id}`)
      .then((res) => setResource(res.data))
      .catch(() => setError("This resource doesn't exist or isn't published."));
  }, [id]);

  if (error) return (<><Navbar /><div className="page"><p className="error-banner">{error}</p></div></>);
  if (!resource) return (<><Navbar /><div className="page">Loading...</div></>);

  return (
    <>
      <Navbar />
      <div className="page">
        <Link to="/resources" className="back-link">&larr; Back to Resources</Link>
        <span className="badge badge-type">{resource.type.replace("_", " ")}</span>
        <h2>{resource.title}</h2>
        <p className="resource-author">by {resource.author.fullName}</p>
        <p className="resource-summary">{resource.summary}</p>
        <div className="resource-body">{resource.body}</div>
        {resource.externalUrl && (
          <p>
            <a href={resource.externalUrl} target="_blank" rel="noreferrer">Open external link &rarr;</a>
          </p>
        )}
      </div>
    </>
  );
}
