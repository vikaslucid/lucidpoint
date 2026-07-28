import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";
import apiClient from "../api/client";
import Navbar from "../components/Navbar";

/**
 * Renders GET /api/analytics/student/{id} — the AnalyticsService aggregation —
 * as a scorecard plus a subject-wise bar chart. This is the page a teacher or
 * parent lands on to see "how is this student actually doing".
 */
export default function StudentPerformance() {
  const { studentId } = useParams();
  const [data, setData] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    apiClient
      .get(`/analytics/student/${studentId}`)
      .then((res) => setData(res.data))
      .catch(() => setError("Could not load performance data for this student."));
  }, [studentId]);

  if (error) return (<><Navbar /><div className="page"><p className="error-banner">{error}</p></div></>);
  if (!data) return (<><Navbar /><div className="page">Loading...</div></>);

  const chartData = data.subjectScores.map((s) => ({
    subject: s.subjectName,
    average: Math.round(s.averagePercentage * 10) / 10,
  }));

  return (
    <>
      <Navbar />
      <div className="page">
        <h2>{data.studentName}'s Performance</h2>

        <div className="stat-cards">
          <div className="stat-card">
            <span className="stat-label">Overall Average</span>
            <span className="stat-value">
              {data.averagePercentage != null ? `${data.averagePercentage.toFixed(1)}%` : "No marks yet"}
            </span>
          </div>
          <div className="stat-card">
            <span className="stat-label">Attendance</span>
            <span className="stat-value">
              {data.attendancePercentage != null ? `${data.attendancePercentage.toFixed(1)}%` : "No records yet"}
            </span>
          </div>
        </div>

        {chartData.length > 0 && (
          <div className="chart-container">
            <h3>Subject-wise Average</h3>
            <ResponsiveContainer width="100%" height={320}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="subject" />
                <YAxis domain={[0, 100]} />
                <Tooltip />
                <Bar dataKey="average" fill="#4f46e5" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>
    </>
  );
}
