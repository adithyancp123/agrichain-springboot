import React, { useEffect, useState } from "react";
import axiosClient from "../axiosClient";
import { Bar, Pie } from "react-chartjs-2";
import "chart.js/auto";

export default function DashboardPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [farmers, setFarmers] = useState([]);
  const [crops, setCrops] = useState([]);
  const [counts, setCounts] = useState({
    farmers: 0,
    fields: 0,
    crops: 0,
  });

  const loadCounts = async () => {
    setLoading(true);
    setError("");
    try {
      const [farmersRes, fieldsRes, cropsRes] = await Promise.all([
        axiosClient.get("/farmers"),
        axiosClient.get("/fields"),
        axiosClient.get("/crops"),
      ]);

      const farmersList = farmersRes.data?.data ?? [];
      const cropsList = cropsRes.data?.data ?? [];

      setCounts({
        farmers: farmersList.length,
        fields: fieldsRes.data?.data?.length ?? 0,
        crops: cropsList.length,
      });
      setFarmers(farmersList);
      setCrops(cropsList);
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error?.message ||
        err?.message ||
        "Failed to load dashboard";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCounts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const farmersByRegionChart = (() => {
    const regionMap = new Map();
    for (const f of farmers) {
      const key = (f.region ?? "").toString().trim() || "Unknown";
      regionMap.set(key, (regionMap.get(key) ?? 0) + 1);
    }

    const labels = Array.from(regionMap.keys());
    const values = labels.map((label) => regionMap.get(label) ?? 0);
    const palette = ["#2563eb", "#16a34a", "#f59e0b", "#ef4444", "#8b5cf6", "#06b6d4", "#64748b"];
    const backgroundColors = labels.map((_, idx) => palette[idx % palette.length]);

    return {
      labels,
      data: values,
      backgroundColors,
    };
  })();

  const cropsByTypeChart = (() => {
    const typeMap = new Map();
    for (const c of crops) {
      const key = (c.type ?? "").toString().trim() || "Unknown";
      typeMap.set(key, (typeMap.get(key) ?? 0) + 1);
    }

    const labels = Array.from(typeMap.keys());
    const values = labels.map((label) => typeMap.get(label) ?? 0);
    const palette = ["#10b981", "#3b82f6", "#f97316", "#ef4444", "#a855f7", "#14b8a6", "#64748b"];
    const backgroundColors = labels.map((_, idx) => palette[idx % palette.length]);

    return {
      labels,
      data: values,
      backgroundColors,
    };
  })();

  return (
    <div className="card">
      <h2 className="cardTitle">Dashboard</h2>
      {loading ? <p>Loading...</p> : null}
      {error ? <p className="errorText">{error}</p> : null}

      <div className="statsGrid">
        <div className="statCard">
          <div className="statLabel">Total Farmers</div>
          <div className="statValue">{counts.farmers}</div>
        </div>

        <div className="statCard">
          <div className="statLabel">Total Fields</div>
          <div className="statValue">{counts.fields}</div>
        </div>

        <div className="statCard">
          <div className="statLabel">Total Crops</div>
          <div className="statValue">{counts.crops}</div>
        </div>
      </div>

      <div className="chartGrid" style={{ marginTop: 16 }}>
        <div className="chartCard">
          <div className="chartSectionTitle">Farmers by Region</div>
          <div className="chartArea">
            {farmersByRegionChart.labels.length > 0 ? (
              <Pie
                data={{
                  labels: farmersByRegionChart.labels,
                  datasets: [
                    {
                      data: farmersByRegionChart.data,
                      backgroundColor: farmersByRegionChart.backgroundColors,
                    },
                  ],
                }}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: {
                    legend: { position: "right" },
                  },
                }}
              />
            ) : (
              <p>No farmer data available.</p>
            )}
          </div>
        </div>

        <div className="chartCard">
          <div className="chartSectionTitle">Crops by Type</div>
          <div className="chartArea">
            {cropsByTypeChart.labels.length > 0 ? (
              <Bar
                data={{
                  labels: cropsByTypeChart.labels,
                  datasets: [
                    {
                      label: "Crops",
                      data: cropsByTypeChart.data,
                      backgroundColor: cropsByTypeChart.backgroundColors,
                      borderWidth: 0,
                    },
                  ],
                }}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: {
                    legend: { display: false },
                  },
                  scales: {
                    y: {
                      beginAtZero: true,
                      ticks: { precision: 0 },
                    },
                  },
                }}
              />
            ) : (
              <p>No crop data available.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

