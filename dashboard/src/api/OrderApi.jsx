import api from "./api";

export const fetchAdminOrdersPaged = async (page = 0, size = 20) =>
  fetch(`/api/v1/admin/orders?page=${page}&size=${size}`).then((r) => r.json());

export const deleteAdminOrder = async (id) =>
  fetch(`/api/v1/admin/orders/${id}`, { method: "DELETE" });
