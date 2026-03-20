import React, { useEffect, useState } from "react";
import axiosClient from "../axiosClient";

export default function FieldsPage() {
  const [fields, setFields] = useState([]);
  const [farmersById, setFarmersById] = useState({});
  const [fieldName, setFieldName] = useState("");
  const [area, setArea] = useState("");
  const [farmerId, setFarmerId] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchFarmers = async () => {
    const res = await axiosClient.get("/farmers");
    const farmers = res.data?.data ?? [];

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
        console.error("Failed to load fields/farmers:", err);
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
    try {
      await axiosClient.post(
        "/fields",
        {
          name: fieldName,
          area: Number(area),
          farmerId: Number(farmerId),
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
      console.error("Failed to create field:", err);
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
            onChange={(e) => setFieldName(e.target.value)}
          />
          <input className="input" placeholder="Area" value={area} onChange={(e) => setArea(e.target.value)} />
          <input
            className="input"
            placeholder="Farmer ID"
            value={farmerId}
            onChange={(e) => setFarmerId(e.target.value)}
          />

          <button className="btn" onClick={createField}>
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

