import { Link } from "react-router-dom";

const STATUS_LABELS = {
  DRAFT: "Draft",
  PENDING_REVIEW: "Pending review",
  PUBLISHED: "Published",
  REJECTED: "Rejected",
};

export function StatusBadge({ status }) {
  return <span className={`badge badge-${status.toLowerCase()}`}>{STATUS_LABELS[status] || status}</span>;
}

/**
 * Shared across Resources (public browse), MyResources, and PendingReview so a resource
 * looks the same everywhere it appears. `extra` lets each page slot in page-specific actions
 * (a Submit button, Approve/Reject controls) without this component knowing about any of them.
 */
export default function ResourceCard({ resource, extra }) {
  return (
    <div className="resource-card">
      <div className="resource-card-header">
        <span className="badge badge-type">{resource.type.replace("_", " ")}</span>
        {resource.grade && <span className="badge badge-meta">Grade {resource.grade}</span>}
        {resource.subject && <span className="badge badge-meta">{resource.subject}</span>}
        {resource.sourceYear && <span className="badge badge-meta">{resource.sourceYear}</span>}
        {resource.status && resource.status !== "PUBLISHED" && <StatusBadge status={resource.status} />}
      </div>
      <h4>
        <Link to={`/resources/${resource.id}`}>{resource.title}</Link>
      </h4>
      <p className="resource-summary">{resource.summary}</p>
      {resource.author && <p className="resource-author">by {resource.author.fullName}</p>}
      {resource.reviewNote && (
        <p className="review-note"><strong>Reviewer note:</strong> {resource.reviewNote}</p>
      )}
      {extra}
    </div>
  );
}
