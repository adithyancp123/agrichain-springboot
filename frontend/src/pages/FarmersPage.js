import React, { useEffect, useRef, useState } from "react";
import { addFarmer, deleteFarmer, getFarmers } from "../api";

export default function FarmersPage() {
  const [farmers, setFarmers] = useState([]);
  const [nameQuery, setNameQuery] = useState("");
  const [regionFilter, setRegionFilter] = useState("");
  const [name, setName] = useState("");
  const [region, setRegion] = useState("");
  const [experienceYears, setExperienceYears] = useState("");
  const [loading, setLoading] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const successTimeoutRef = useRef(null);

  const fetchFarmers = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getFarmers();
      setFarmers(data);
    } catch (err) {
      console.error("Failed to fetch farmers:", err);
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error?.message ||
        err?.message ||
        "Failed to load farmers";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  const showSuccess = (message) => {
    setSuccessMessage(message);
    if (successTimeoutRef.current) {
      clearTimeout(successTimeoutRef.current);
    }
    successTimeoutRef.current = setTimeout(() => setSuccessMessage(""), 3000);
  };

  useEffect(() => {
    fetchFarmers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    return () => {
      if (successTimeoutRef.current) {
        clearTimeout(successTimeoutRef.current);
      }
    };
  }, []);

  const createFarmer = async () => {
    // Prevent double submits on fast clicks.
    if (isCreating || loading) return;

    try {
      setError("");
      setIsCreating(true);

      await addFarmer({
        name,
        region,
        experienceYears: Number(experienceYears),
      });

      // Clear inputs after successful POST
      setName("");
      setRegion("");
      setExperienceYears("");

      // Refresh list without reloading the page
      await fetchFarmers();

      showSuccess("Farmer added successfully.");
    } catch (err) {
      console.error("Failed to create farmer:", err);
      const message =
        err?.response?.data?.message ||
        err?.message ||
        "Failed to add farmer";
      setError(message);
    } finally {
      setIsCreating(false);
    }
  };

  const handleDelete = async (farmer) => {
    try {
      if (deletingId != null) return;
      const id = farmer?.id;
      if (id == null) return;

      const ok = window.confirm(
        `Are you sure you want to delete "${farmer?.name ?? "this farmer"}"?`
      );
      if (!ok) return;

      setError("");
      setDeletingId(id);
      await deleteFarmer(id);
      setFarmers((prev) => prev.filter((f) => f.id !== id));
      showSuccess("Farmer deleted successfully.");
    } catch (err) {
      console.error("Failed to delete farmer:", err);
      const message =
        err?.response?.data?.message ||
        err?.message ||
        "Failed to delete farmer";
      setError(message);
    } finally {
      setDeletingId(null);
    }
  };

  const filteredFarmers = farmers.filter((f) => {
    const q = nameQuery.trim().toLowerCase();
    const r = regionFilter.trim().toLowerCase();

    const matchesName = !q
      ? true
      : (f.name ?? "").toLowerCase().includes(q);

    const matchesRegion = !r
      ? true
      : (f.region ?? "").toLowerCase().includes(r);

    return matchesName && matchesRegion;
  });

  const canAddFarmer =
    !isCreating &&
    !loading &&
    deletingId == null &&
    name.trim().length > 0 &&
    region.trim().length > 0 &&
    experienceYears.trim().length > 0;

  return (
    <div className="card">
      <h2 className="cardTitle">Farmers</h2>
      {(loading || isCreating || deletingId != null) ? (
        <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 8 }}>
          <span className="spinner" aria-label="Loading" />
          <span>Loading...</span>
        </div>
      ) : null}
      {error ? <p className="errorText">{error}</p> : null}
      {successMessage ? <p className="successText">{successMessage}</p> : null}

      <div style={{ display: "flex", gap: 12, marginBottom: 16 }}>
        <input
          className="input"
          placeholder="Search by name"
          value={nameQuery}
          onChange={(e) => setNameQuery(e.target.value)}
        />
        <input
          className="input"
          placeholder="Filter by region"
          value={regionFilter}
          onChange={(e) => setRegionFilter(e.target.value)}
        />
      </div>

      <div className="formRow">
        {/* Keep existing input fields */}
        <input className="input" placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} />
        <input className="input" placeholder="Region" value={region} onChange={(e) => setRegion(e.target.value)} />
        <input
          className="input"
          placeholder="Experience"
          value={experienceYears}
          onChange={(e) => setExperienceYears(e.target.value)}
        />
        <button className="btn" onClick={createFarmer} disabled={!canAddFarmer}>
          {isCreating ? "Adding..." : "Add Farmer"}
        </button>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Region</th>
            <th>Experience</th>
            <th>Delete</th>
          </tr>
        </thead>
        <tbody>
          {filteredFarmers.map((f) => (
            <tr key={f.id}>
              <td>{f.name}</td>
              <td>{f.region}</td>
              <td>{f.experienceYears} yrs</td>
              <td>
                <button
                  className="btnSecondary"
                  onClick={() => handleDelete(f)}
                  disabled={deletingId === f.id || deletingId != null || loading}
                >
                  {deletingId === f.id ? "Deleting..." : "Delete"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

