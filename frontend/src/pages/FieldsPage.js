import React, { useEffect, useState } from "react";
import axiosClient from "../axiosClient";

export default function FieldsPage() {
  const [fields, setFields] = useState([]);
  const [farmers, setFarmers] = useState([]);
  const [farmersById, setFarmersById] = useState({});
  const [fieldName, setFieldName] = useState("");
  const [area, setArea] = useState("");
  const [farmerId, setFarmerId] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchFarmers = async () => {
    const res = await axiosClient.get("/farmers");
    const farmers = res.data?.data ?? [];
    setFarmers(farmers);

    const map = {};
    farmers.forEach((f) => {
      map[f.id] = f.name;
    });

    setFarmersById(map);
  };

  const fetchFields = async () => {
    const res = await axiosClient.get("/fields");
    // Backend wraps responses in ApiResponse<T>: { message, data, status }
    setFields(res.data?.data ?? []);
  };

  useEffect(() => {
    const loadAll = async () => {
      setLoading(true);
      setError("");
      try {
        await Promise.all([fetchFarmers(), fetchFields()]);
      } catch (err) {
        const message =
          err?.response?.data?.message ||
          err?.response?.data?.error?.message ||
          err?.message ||
          "Failed to load fields";
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    loadAll();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const createField = async () => {
    setError("");
    const parsedArea = Number(area);
    const parsedFarmerId = Number(farmerId);

    if (!fieldName.trim()) {
      setError("Field name is required.");
      return;
    }
    if (!Number.isFinite(parsedArea) || parsedArea < 0) {
      setError("Area must be a valid non-negative number.");
      return;
    }
    if (!Number.isInteger(parsedFarmerId) || parsedFarmerId <= 0) {
      setError("Please select a valid farmer.");
      return;
    }

    try {
      await axiosClient.post(
        "/fields",
        {
          name: fieldName,
          area: parsedArea,
          farmerId: parsedFarmerId,
        },
        {}
      );

      // Clear inputs after successful POST
      setFieldName("");
      setArea("");
      setFarmerId("");

      // Refresh list without reloading the page
      setLoading(true);
      await fetchFields();
      setLoading(false);
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error?.message ||
        err?.message ||
        "Failed to create field";
      setError(message);
    }
  };

  return (
    <div>
      <div className="card">
        <h2 className="cardTitle">Fields</h2>
        {loading ? <p>Loading...</p> : null}
        {error ? <p className="errorText">{error}</p> : null}

        <div className="formRow" style={{ gridTemplateColumns: "1fr 1fr 1fr auto" }}>
          <input
            className="input"
            placeholder="Field Name"
            value={fieldName}
            onChange={(e) => {
              setError("");
              setFieldName(e.target.value);
            }}
          />
          <input
            className="input"
            type="number"
            min="0"
            step="0.1"
            placeholder="Area"
            value={area}
            onChange={(e) => {
              setError("");
              setArea(e.target.value);
            }}
          />
          <select
            className="input"
            value={farmerId}
            onChange={(e) => {
              setError("");
              setFarmerId(e.target.value);
            }}
          >
            <option value="">Select Farmer</option>
            {farmers.map((f) => (
              <option key={f.id} value={f.id}>
                {f.name}
              </option>
            ))}
          </select>

          <button
            className="btn"
            onClick={createField}
            disabled={!fieldName.trim() || !area.trim() || !farmerId}
          >
            Add Field
          </button>
        </div>

        <table className="table">
        <thead>
          <tr>
            <th>Field Name</th>
            <th>Area</th>
            <th>Farmer Name</th>
          </tr>
        </thead>
        <tbody>
          {fields.map((f) => (
            <tr key={f.id}>
              <td>{f.name}</td>
              <td>{f.area}</td>
              <td>{farmersById[f.farmerId] ?? "N/A"}</td>
            </tr>
          ))}
        </tbody>
        </table>
      </div>
    </div>
  );
}

