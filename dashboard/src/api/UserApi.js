import api from "./api";

export const createAdminUser = async (userData) => {
  const request = {
    email: userData.email,
    username: userData.username,
    password: userData.password,
    role: userData.role,
    status: userData.status,
  };

  const res = await api.post("/admin/users", request);
  return res.data;
};

export const fetchAdminUsersPaged = async (
  page = 0,
  size = 10
) => {
  const res = await api.get("/admin/users", {
    params: { page, size },
  });
  return res.data;
};

export const updateAdminUser = async (id, userData) => {
  const request = {
    email: userData.email,
    username: userData.username,
    password: userData.password,
    role: userData.role,
    status: userData.status,
  };

  const res = await api.put(`/admin/users/${id}`, request);
  return res.data;
};

export const deleteAdminUser = async (userId) => {
  const res = await api.delete(`/admin/users/${userId}`);
  return res.data;
};