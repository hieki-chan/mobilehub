import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { login } from "../api/AuthApi";
import { Mail, Lock } from "lucide-react";

const LoginPage = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [remember, setRemember] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const loggedIn = localStorage.getItem("isLoggedIn") === "true";
    const role = localStorage.getItem("role");
    if (loggedIn && role === "ADMIN") navigate("/");

    const savedEmail = localStorage.getItem("rememberedEmail");
    if (savedEmail) {
      setEmail(savedEmail);
      setRemember(true);
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const data = await login(email, password);

      if (remember) localStorage.setItem("rememberedEmail", email);
      else localStorage.removeItem("rememberedEmail");

      navigate("/");
    } catch (err) {
      console.error("Login error:", err);
      if (!err.response)
        return setError("Không thể kết nối đến máy chủ. Kiểm tra mạng hoặc thử lại sau.");

      if (err.response.status === 401) setError("Sai email hoặc mật khẩu!");
      else if (err.response.status === 403)
        setError("Tài khoản này không có quyền truy cập.");
      else if (err.response.status >= 500)
        setError("Lỗi máy chủ. Vui lòng thử lại sau.");
      else setError("Đăng nhập thất bại. Vui lòng thử lại.");
    }
  };

  return (
    <div className="w-screen h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 via-white to-orange-100 relative overflow-hidden">
      {/* ☀️ Hiệu ứng nền sáng màu cam nhạt */}
      <div className="absolute w-[600px] h-[600px] bg-orange-200/40 rounded-full blur-3xl top-[-200px] left-[-200px]" />
      <div className="absolute w-[500px] h-[500px] bg-amber-200/40 rounded-full blur-3xl bottom-[-200px] right-[-150px]" />

      {/* 🧱 Form container */}
      <div className="relative z-10 bg-white/80 backdrop-blur-xl border border-gray-200 shadow-2xl rounded-2xl p-8 w-[90%] max-w-md mx-auto">
        {/* Logo / Title */}
        <div className="flex flex-col items-center mb-6">
          <div className="w-14 h-14 bg-orange-500 rounded-full flex items-center justify-center text-2xl font-bold text-white shadow-md">
            M
          </div>
          <h2 className="mt-3 text-2xl font-bold text-gray-800">
            MobileHub Admin
          </h2>
          <p className="text-gray-500 text-sm mt-1">Đăng nhập để tiếp tục</p>
        </div>

        {error && (
          <div className="bg-red-100 text-red-600 border border-red-300 p-2 rounded-lg mb-4 text-sm text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          {/* Email */}
          <div>
            <label className="block mb-1 text-gray-700 text-sm">Email</label>
            <div className="relative">
              <Mail className="absolute left-3 top-2.5 text-gray-400" size={18} />
              <input
                type="email"
                placeholder="admin@example.com"
                className="w-full pl-10 pr-3 py-2 rounded-lg bg-white border border-gray-300 text-gray-800 focus:outline-none focus:ring-2 focus:ring-orange-400 focus:border-orange-400 transition"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          {/* Password */}
          <div>
            <label className="block mb-1 text-gray-700 text-sm">Mật khẩu</label>
            <div className="relative">
              <Lock className="absolute left-3 top-2.5 text-gray-400" size={18} />
              <input
                type="password"
                placeholder="••••••••"
                className="w-full pl-10 pr-3 py-2 rounded-lg bg-white border border-gray-300 text-gray-800 focus:outline-none focus:ring-2 focus:ring-orange-400 focus:border-orange-400 transition"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          {/* Remember me */}
          <div className="flex items-center justify-between text-sm">
            <label className="flex items-center space-x-2 text-gray-600">
              <input
                type="checkbox"
                checked={remember}
                onChange={(e) => setRemember(e.target.checked)}
                className="h-4 w-4 text-gray-500 rounded focus:ring-orange-400 border-gray-300"
              />
              <span>Ghi nhớ tài khoản này</span>
            </label>

            <Link
              to="/forgot-password"
              className="text-orange-500 hover:text-gray-600"
            >
              Quên mật khẩu?
            </Link>
          </div>

          {/* Submit */}
          <button
            type="submit"
            className="w-full py-2 rounded-lg bg-gray-900 hover:bg-orange-600 text-white font-medium transition-all duration-200 shadow-md hover:shadow-orange-300/40"
          >
            Đăng nhập
          </button>
        </form>

        {/* Footer */}
        <div className="text-center text-sm text-gray-500 mt-4">
          Chưa có tài khoản?{" "}
          <Link to="/register" className="text-orange-500 hover:text-gray-600">
            Đăng ký ngay
          </Link>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
