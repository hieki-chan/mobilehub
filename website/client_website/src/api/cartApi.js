import api from "./api";

const BASE_URL = "/carts";

export const CartApi = {
  getCart: async (userId) => {
    const res = await api.get(`${BASE_URL}/${userId}`);
    return res.data.result;
  },

  addItem: async (userId, request) => {
    console.log(request);
    const res = await api.post(`${BASE_URL}/${userId}/items`, request);
    return res.data.result;
  },

  updateItem: async (userId, request) => {
    const res = await api.put(`${BASE_URL}/${userId}/update`, request);
    return res.data.result;
  },

  removeItem: async (userId, itemId) => {
    const res = await api.delete(`${BASE_URL}/item/${itemId}`, { params: { userId } });
    return res.data;
  },

  clearCart: async (userId) => {
    const res = await api.delete(`${BASE_URL}/clear`, { params: { userId } });
    return res.data;
  },

  getTotal: async (userId) => {
    const res = await api.get(`${BASE_URL}/total`, { params: { userId } });
    return res.data.result;
  },
};
