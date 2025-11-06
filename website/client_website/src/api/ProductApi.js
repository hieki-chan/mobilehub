import api from "./api";

const BASE_URL = "/products";

export const fetchProducts = async (page = 0, size = 8) => {
  const res = await api.get(`${BASE_URL}?page=${page}&size=${size}`);
  return res.data;
};