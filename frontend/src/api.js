import axiosClient from "./axiosClient";

export const getFarmers = async () => {
  const res = await axiosClient.get("/farmers");

  // Backend wraps responses in ApiResponse<T>: { message, data, status }
  return res.data?.data ?? [];
};

export const addFarmer = async ({ name, region, experienceYears }) => {
  await axiosClient.post("/farmers", { name, region, experienceYears });
};

export const deleteFarmer = async (id) => {
  await axiosClient.delete(`/farmers/${id}`);
};

