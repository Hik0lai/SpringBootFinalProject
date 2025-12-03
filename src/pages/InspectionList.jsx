import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import jsPDF from "jspdf";

export default function InspectionList() {
  const [inspections, setInspections] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchInspections = () => {
    setLoading(true);
    axios.get("http://localhost:8080/api/inspections", {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    }).then(res => setInspections(res.data)).finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchInspections();
  }, []);

  const handleDelete = async (inspectionId, hiveName, date) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete the inspection for "${hiveName}" on ${date}?\n\nThis action cannot be undone.`
    );

    if (!confirmed) {
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      await axios.delete(`http://localhost:8080/api/inspections/${inspectionId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchInspections(); // Refresh list after deletion
    } catch (err) {
      console.error("Error deleting inspection:", err);
      alert("Failed to delete inspection. Please try again.");
    }
  };

  const handleGeneratePDF = async (inspection) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("You must be logged in to generate PDF.");
        return;
      }

      // Fetch full inspection details including hive information
      const response = await axios.get(
        `http://localhost:8080/api/inspections/${inspection.id}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      const inspectionData = response.data;
      
      // Format date for filename (replace slashes/dashes with underscores)
      const dateStr = inspectionData.date.replace(/-/g, "_");
      
      // Format hive name for filename (remove special characters, replace spaces with underscores)
      const hiveNameForFile = inspectionData.hiveName
        .replace(/[^a-zA-Z0-9]/g, "_")
        .replace(/\s+/g, "_");
      
      // Create filename: "HiveName_YYYY_MM_DD.pdf"
      const filename = `${hiveNameForFile}_${dateStr}.pdf`;

      // Create PDF document
      const doc = new jsPDF();
      
      // Set colors
      const primaryColor = [255, 165, 0]; // Yellow/Orange
      const textColor = [51, 51, 51]; // Dark gray
      
      // Title
      doc.setFillColor(...primaryColor);
      doc.rect(10, 10, 190, 15, "F");
      doc.setTextColor(255, 255, 255);
      doc.setFontSize(18);
      doc.setFont("helvetica", "bold");
      doc.text("Hive Inspection Report", 105, 21, { align: "center" });
      
      // Reset text color
      doc.setTextColor(...textColor);
      
      // Content starting position
      let yPos = 35;
      
      // Add spacing
      const lineHeight = 7;
      
      // Hive Information Section
      doc.setFontSize(14);
      doc.setFont("helvetica", "bold");
      doc.text("Hive Information", 10, yPos);
      yPos += lineHeight;
      
      doc.setFontSize(11);
      doc.setFont("helvetica", "normal");
      doc.text(`Hive Name: ${inspectionData.hiveName}`, 10, yPos);
      yPos += lineHeight;
      
      // Inspection Details Section
      yPos += 5; // Add spacing
      doc.setFontSize(14);
      doc.setFont("helvetica", "bold");
      doc.text("Inspection Details", 10, yPos);
      yPos += lineHeight;
      
      doc.setFontSize(11);
      doc.setFont("helvetica", "normal");
      
      // Format date for display
      const displayDate = new Date(inspectionData.date).toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
      
      doc.text(`Date: ${displayDate}`, 10, yPos);
      yPos += lineHeight;
      
      doc.text(`Inspector: ${inspectionData.inspector || "Not specified"}`, 10, yPos);
      yPos += lineHeight;
      
      // Notes Section
      yPos += 5; // Add spacing
      doc.setFontSize(14);
      doc.setFont("helvetica", "bold");
      doc.text("Notes", 10, yPos);
      yPos += lineHeight;
      
      doc.setFontSize(11);
      doc.setFont("helvetica", "normal");
      
      // Handle notes text - split into multiple lines if too long
      const notes = inspectionData.notes || "No notes recorded.";
      const splitNotes = doc.splitTextToSize(notes, 180);
      
      // Check if notes would overflow the page
      const pageHeight = doc.internal.pageSize.height;
      const marginBottom = 20;
      const remainingSpace = pageHeight - yPos - marginBottom;
      const estimatedHeight = splitNotes.length * lineHeight;
      
      // If notes would overflow, add a new page
      if (yPos + estimatedHeight > pageHeight - marginBottom) {
        doc.addPage();
        yPos = 20;
        doc.setFontSize(14);
        doc.setFont("helvetica", "bold");
        doc.text("Notes (continued)", 10, yPos);
        yPos += lineHeight;
        doc.setFontSize(11);
        doc.setFont("helvetica", "normal");
      }
      
      doc.text(splitNotes, 10, yPos);
      
      // Add footer
      doc.setFontSize(8);
      doc.setTextColor(128, 128, 128);
      doc.text(
        `Generated on ${new Date().toLocaleDateString("en-US", {
          year: "numeric",
          month: "long",
          day: "numeric",
          hour: "2-digit",
          minute: "2-digit",
        })}`,
        105,
        pageHeight - 10,
        { align: "center" }
      );
      
      // Save PDF
      doc.save(filename);
    } catch (err) {
      console.error("Error generating PDF:", err);
      alert("Failed to generate PDF. Please try again.");
    }
  };

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-2xl font-bold text-yellow-700">Hive Inspections</h1>
        <Link to="/add-inspection" className="bg-yellow-500 px-3 py-1 rounded text-white hover:bg-yellow-600">+ New</Link>
      </div>
      {loading ? (
        <div>Loading...</div>
      ) : inspections.length === 0 ? (
        <div>No inspections recorded yet.</div>
      ) : (
        <div className="bg-white rounded-lg shadow border border-yellow-100">
          <table className="w-full text-left">
            <thead>
              <tr className="text-yellow-700">
                <th className="p-2">Hive</th>
                <th className="p-2">Inspector</th>
                <th className="p-2">Date</th>
                <th className="p-2">Notes</th>
                <th className="p-2"></th>
              </tr>
            </thead>
            <tbody>
              {inspections.map((insp) => (
                <tr key={insp.id} className="border-t">
                  <td className="p-2">{insp.hiveName}</td>
                  <td className="p-2">{insp.inspector || "-"}</td>
                  <td className="p-2">{insp.date}</td>
                  <td className="p-2 max-w-xs truncate">{insp.notes}</td>
                  <td className="p-2">
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleGeneratePDF(insp)}
                        className="text-green-600 hover:text-green-800 hover:underline text-sm"
                        title="Generate PDF"
                      >
                        PDF
                      </button>
                      <Link
                        to={`/edit-inspection/${insp.id}`}
                        className="text-blue-600 hover:text-blue-800 hover:underline text-sm"
                      >
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(insp.id, insp.hiveName, insp.date)}
                        className="text-red-600 hover:text-red-800 hover:underline text-sm"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}