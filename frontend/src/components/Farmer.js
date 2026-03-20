import React, { useState, useEffect } from "react";
import axios from "axios";

function Farmer() {
  const [farmers, setFarmers] = useState([]);
  const [name, setName] = useState("");
  const [region, setRegion] = useState("");
  const [experienceYears, setExperienceYears] = useState("");

  const fetchFarmers = async () => {
    const res = await axios.get("http://localhost:8080/farmers");
    setFarmers(res.data);
  };

  useEffect(() => {
    fetchFarmers();
  }, []);

  const addFarmer = async () => {
    await axios.post("http://localhost:8080/farmers", {
      name,
      region,
      experienceYears: Number(experienceYears)
    });

    fetchFarmers();
  };

  return (
    <div>
      <h2>Farmers</h2>

      <input placeholder="Name" onChange={(e) => setName(e.target.value)} />
      <input placeholder="Region" onChange={(e) => setRegion(e.target.value)} />
      <input placeholder="Experience" onChange={(e) => setExperienceYears(e.target.value)} />

      <button onClick={addFarmer}>Add Farmer</button>

      <ul>
        {farmers.map((f) => (
          <li key={f.id}>
            {f.name} - {f.region} ({f.experienceYears} yrs)
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Farmer;