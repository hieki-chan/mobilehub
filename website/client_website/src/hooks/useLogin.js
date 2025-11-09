import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/AuthApi";

export default function useLogin() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleLogin = async ({ email, password, remember = true }) => {
    setError("");
    setLoading(true);

    try {
      const data = await login(email, password);

      if (remember) localStorage.setItem("email", email);
      else localStorage.removeItem("email");

      localStorage.setItem("token", data.accessToken);
      localStorage.setItem("user", JSON.stringify(data.user));

      navigate("/");
      window.dispatchEvent(new Event("user-changed"));
    } catch (err) {
      if (!err.response)
        setError("Không thể kết nối đến máy chủ. Kiểm tra mạng hoặc thử lại sau.");
      else if (err.response.status === 401)
        setError("Sai email hoặc mật khẩu!");
      else if (err.response.status === 403)
        setError("Tài khoản này không có quyền truy cập.");
      else if (err.response.status >= 500)
        setError("Lỗi máy chủ. Vui lòng thử lại sau.");
      else setError("Đăng nhập thất bại. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  return { handleLogin, loading, error };
}
