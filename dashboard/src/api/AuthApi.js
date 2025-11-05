import api from "./api";

const API_BASE_URL = "/auth"; 

export const login = async (email, password) => {
  try {
    const res = await api.post(`${API_BASE_URL}/authenticate`, { email, password });
    const data = res.data;

    if (!data?.user || data.user.role !== "ADMIN") {
      throw new Error("Tài khoản này không có quyền admin!");
    }

    localStorage.setItem("token", data.accessToken);
    localStorage.setItem("role", data.user.role);
    localStorage.setItem("email", data.user.email);
    localStorage.setItem("isLoggedIn", "true");

    return data;
  } catch (err) {
    console.error("❌ Login failed:", err);
    throw err;
  }
};

export const verifyToken = async (token) => {
  try {
    const res = await api.get(`${API_BASE_URL}/validate`, { params: { token } });
    return res.data === "Valid";
  } catch (err) {
    console.warn("⚠️ Token verify failed:", err);
    return false;
  }
};

export const logout = () => {
  clearAccountData();
};

export const clearAccountData = () => {
  localStorage.removeItem("isLoggedIn");
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  localStorage.removeItem("email");
  localStorage.removeItem("username");
};
