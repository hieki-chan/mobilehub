import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Mail, Lock, Loader2 } from "lucide-react";
import useLogin from "../hooks/useLogin";

const LoginPage = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [remember, setRemember] = useState(false);
  const { handleLogin, loading, error } = useLogin();
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const loggedIn = localStorage.getItem("isLoggedIn") === "true";

    if (token && loggedIn && role === "ADMIN") {
      navigate("/", { replace: true });
      return;
    }

    const savedEmail = localStorage.getItem("email");
    if (savedEmail) {
      setEmail(savedEmail);
      setRemember(true);
    }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    handleLogin({ email: email.trim(), password, remember });
  };

  return (
    <div className="w-screen h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 via-white to-orange-100 relative overflow-hidden">
      <div className="absolute w-[600px] h-[600px] bg-orange-200/40 rounded-full blur-3xl top-[-200px] left-[-200px]" />
      <div className="absolute w-[500px] h-[500px] bg-amber-200/40 rounded-full blur-3xl bottom-[-200px] right-[-150px]" />

      <div className="relative z-10 bg-white/80 backdrop-blur-xl border border-gray-200 shadow-2xl rounded-2xl p-8 w-[90%] max-w-md mx-auto">
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
                disabled={loading}
              />
            </div>
          </div>

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
                disabled={loading}
              />
            </div>
          </div>

          <div className="flex items-center justify-between text-sm">
            <label className="flex items-center space-x-2 text-gray-600">
              <input
                type="checkbox"
                checked={remember}
                onChange={(e) => setRemember(e.target.checked)}
                className="h-4 w-4 text-gray-500 rounded focus:ring-orange-400 border-gray-300"
                disabled={loading}
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

          <button
            type="submit"
            disabled={loading}
            className={`w-full py-2 rounded-lg text-white font-medium transition-all duration-200 shadow-md flex items-center justify-center ${loading
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-gray-900 hover:bg-orange-600 hover:shadow-orange-300/40"
              }`}
          >
            {loading ? (
              <>
                <Loader2 className="animate-spin mr-2" size={18} />
                Đang đăng nhập...
              </>
            ) : (
              "Đăng nhập"
            )}
          </button>
        </form>

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
