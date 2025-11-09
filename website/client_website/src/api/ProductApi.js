import api from "./api";

const BASE_URL = "/products";

export const fetchProducts = async (page = 0, size = 8) => {
  const res = await api.get(`${BASE_URL}?page=${page}&size=${size}`);
  console.log(res);
  return res.data;
};

export const getProductDetails = async (productId) => {
  const res = await api.get(`${BASE_URL}/${productId}/details`);
  console.log(res);
  return res.data;
};