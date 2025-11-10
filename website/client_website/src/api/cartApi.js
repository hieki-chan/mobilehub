import api from "./api";

const BASE_URL = "/carts";

export const CartApi = {
  getCart: async (userId) => {
    const res = await api.get(`${BASE_URL}/${userId}`);
    return res.data.result;
  },

  addItem: async (userId, request) => {
    const res = await api.post(`${BASE_URL}/${userId}/items`, request);
    return res.data.result;
  },

  updateItemQuantity: async (userId, itemId, quantity) => {
    const res = await api.put(`${BASE_URL}/${userId}/items/${itemId}/quantity`, { quantity });
    return res.data.result;
  },

  updateItemVariant: async (userId, itemId, variantId) => {
    const res = await api.put(`${BASE_URL}/${userId}/items/${itemId}/variant`, { variantId });
    return res.data.result;
  },

  removeItem: async (userId, itemId) => {
    const res = await api.delete(`${BASE_URL}/items/${itemId}`, { params: { userId } });
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
