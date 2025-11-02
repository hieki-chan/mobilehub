import axios from "axios";

const API_BASE_URL = "http://localhost:8080/auth"; // ⚙️ chỉnh theo backend

export const login = async (email, password) => {
    try {
        const res = await axios.post(`${API_BASE_URL}/authenticate`, {
            email,
            password,
        });

        const data = res.data;

        if (data.user.role !== "ADMIN") {
            throw error("Login Failed");
        }

        // ✅ Lưu token + role
        localStorage.setItem("token", data.accessToken);
        localStorage.setItem("role", data.user.role);
        localStorage.setItem("email", data.user.email);
        localStorage.setItem("isLoggedIn", "true");

        return data;
    } catch (err) {
        console.error("Login failed:", err);
        throw err;
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
