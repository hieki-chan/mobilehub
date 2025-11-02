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

  // 🧠 Nếu đã đăng nhập thì vào thẳng dashboard
  useEffect(() => {
    const loggedIn = localStorage.getItem("isLoggedIn") === "true";
    const role = localStorage.getItem("role");
    if (loggedIn && role === "ADMIN") {
      navigate("/");
    }

    // Nếu có email được "ghi nhớ" thì điền sẵn vào
    const savedEmail = localStorage.getItem("rememberedEmail");
    if (savedEmail) {
      setEmail(savedEmail);
      setRemember(true);
    }
  }, [navigate]);

  // 🧾 Submit form
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = await login(email, password);

      // Nếu tick "Ghi nhớ"
      if (remember) {
        localStorage.setItem("rememberedEmail", email);
      } else {
        localStorage.removeItem("rememberedEmail");
      }

      navigate("/");
    } catch {
      setError("Sai email hoặc mật khẩu!");
    }
  };

  return (
    <div className="w-screen h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 relative overflow-hidden">
      {/* 🟣 Hiệu ứng glow background */}
      <div className="absolute w-[600px] h-[600px] bg-indigo-500/20 rounded-full blur-3xl top-[-200px] left-[-200px]" />
      <div className="absolute w-[500px] h-[500px] bg-purple-500/20 rounded-full blur-3xl bottom-[-200px] right-[-150px]" />

      {/* 🧱 Form container */}
      <div className="relative z-10 bg-gray-800/80 backdrop-blur-xl border border-gray-700 shadow-2xl rounded-2xl p-8 w-[90%] max-w-md mx-auto">
        {/* Logo / Title */}
        <div className="flex flex-col items-center mb-6">
          <div className="w-14 h-14 bg-indigo-600 rounded-full flex items-center justify-center text-2xl font-bold text-white shadow-md">
            M
          </div>
          <h2 className="mt-3 text-2xl font-bold text-white">MobileHub Admin</h2>
          <p className="text-gray-400 text-sm mt-1">Đăng nhập để tiếp tục</p>
        </div>

        {error && (
          <div className="bg-red-500/10 text-red-400 border border-red-400/30 p-2 rounded-lg mb-4 text-sm text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          {/* Email */}
          <div>
            <label className="block mb-1 text-gray-300 text-sm">Email</label>
            <div className="relative">
              <Mail className="absolute left-3 top-2.5 text-gray-400" size={18} />
              <input
                type="email"
                placeholder="admin@example.com"
                className="w-full pl-10 pr-3 py-2 rounded-lg bg-gray-700/60 border border-gray-600 text-gray-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          {/* Password */}
          <div>
            <label className="block mb-1 text-gray-300 text-sm">Mật khẩu</label>
            <div className="relative">
              <Lock className="absolute left-3 top-2.5 text-gray-400" size={18} />
              <input
                type="password"
                placeholder="••••••••"
                className="w-full pl-10 pr-3 py-2 rounded-lg bg-gray-700/60 border border-gray-600 text-gray-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          {/* Remember me */}
          <div className="flex items-center justify-between text-sm">
            <label className="flex items-center space-x-2 text-gray-300">
              <input
                type="checkbox"
                checked={remember}
                onChange={(e) => setRemember(e.target.checked)}
                className="h-4 w-4 text-indigo-500 rounded focus:ring-indigo-500 border-gray-600 bg-gray-700"
              />
              <span>Ghi nhớ tài khoản này</span>
            </label>

            <Link
              to="/forgot-password"
              className="text-indigo-400 hover:text-indigo-300"
            >
              Quên mật khẩu?
            </Link>
          </div>

          {/* Submit */}
          <button
            type="submit"
            className="w-full py-2 rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white font-medium transition-all duration-200 shadow-md hover:shadow-indigo-500/30"
          >
            Đăng nhập
          </button>
        </form>

        {/* Footer */}
        <div className="text-center text-sm text-gray-400 mt-4">
          Chưa có tài khoản?{" "}
          <Link to="/register" className="text-indigo-400 hover:text-indigo-300">
            Đăng ký ngay
          </Link>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
