import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token && token !== "null" && token !== "undefined") {
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    delete config.headers.Authorization;
  }

  const isFormData = config.data instanceof FormData;
  if (!isFormData) {
    config.headers["Content-Type"] = "application/json";
  } else {
    delete config.headers["Content-Type"]; // để axios tự set boundary cho multipart
  }

  return config;
});

export default api;
