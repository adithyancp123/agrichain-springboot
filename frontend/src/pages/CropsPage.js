import React, { useEffect, useState } from "react";
import axiosClient from "../axiosClient";

export default function CropsPage() {
  const [crops, setCrops] = useState([]);
  const [fieldsById, setFieldsById] = useState({});
  const [name, setName] = useState("");
  const [type, setType] = useState("");
  const [fieldId, setFieldId] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchFields = async () => {
    const res = await axiosClient.get("/fields");
    const fields = res.data?.data ?? [];

    const map = {};
    fields.forEach((f) => {
      map[f.id] = f.name;
    });

    setFieldsById(map);
  };

  const fetchCrops = async () => {
    const res = await axiosClient.get("/crops");
    setCrops(res.data?.data ?? []);
  };

  useEffect(() => {
    const loadAll = async () => {
      setLoading(true);
      setError("");
      try {
        await Promise.all([fetchFields(), fetchCrops()]);
      } catch (err) {
        console.error("Failed to load crops/fields:", err);
        const message =
          err?.response?.data?.message ||
          err?.response?.data?.error?.message ||
          err?.message ||
          "Failed to load crops";
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    loadAll();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const createCrop = async () => {
    setError("");
    try {
      await axiosClient.post(
        "/crops",
        {
          name,
          type,
          fieldId: Number(fieldId),
        },
        {}
      );

      // Clear inputs after successful POST
      setName("");
      setType("");
      setFieldId("");

      // Refresh list without reloading
      setLoading(true);
      await fetchCrops();
      setLoading(false);
    } catch (err) {
      console.error("Failed to create crop:", err);
      const message =
        err?.response?.data?.message ||
        err?.message ||
        "Failed to create crop";
      setError(message);
    }
  };

  return (
    <div className="card">
      <h2 className="cardTitle">Crops</h2>
      {loading ? <p>Loading...</p> : null}
      {error ? <p className="errorText">{error}</p> : null}

      <div className="formRow" style={{ gridTemplateColumns: "1fr 1fr 1fr auto" }}>
        <input className="input" placeholder="Crop Name" value={name} onChange={(e) => setName(e.target.value)} />
        <input className="input" placeholder="Type" value={type} onChange={(e) => setType(e.target.value)} />
        <input
          className="input"
          placeholder="Field ID"
          value={fieldId}
          onChange={(e) => setFieldId(e.target.value)}
        />

        <button className="btn" onClick={createCrop}>
          Add Crop
        </button>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Crop Name</th>
            <th>Type</th>
            <th>Field Name</th>
          </tr>
        </thead>
        <tbody>
          {crops.map((c) => (
            <tr key={c.id}>
              <td>{c.name}</td>
              <td>{c.type}</td>
              <td>{fieldsById[c.fieldId] ?? "N/A"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

