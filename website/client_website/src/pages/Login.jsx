import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import GoogleLogin from "../components/GoogleLogin";
import "../styles/pages/login.css";
import useLogin from "../hooks/useLogin";

export async function mockSignIn({ email, password }) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!email || !email.includes("@")) {
        return reject(new Error("Email không hợp lệ"));
      }
      if (!password || password.length < 6) {
        return reject(new Error("Mật khẩu phải ≥ 6 ký tự"));
      }

      const name = email.split("@")[0].replace(/[^\w]/g, "");
      resolve({
        user: {
          id: "u_" + Date.now(),
          name: name.charAt(0).toUpperCase() + name.slice(1),
          email,
        },
        token: "demo-token-" + Math.random().toString(36).slice(2, 10),
      });
    }, 600);
  });
}

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [remember, setRemember] = useState(true);
  const { handleLogin, loading, error } = useLogin();

  const onLoginSuccess = ({ user, token }) => {
    //if (remember) localStorage.setItem("token", token);
    //navigate("/");
    window.dispatchEvent(new Event("user-changed"));
  };

  const submitEmail = async (e) => {
    e.preventDefault();
    handleLogin({ email: email.trim(), password, remember });
  };

  useEffect(() => {
    const savedEmail = localStorage.getItem("email");
    if (savedEmail) {
      setEmail(savedEmail);
      setRemember(true);
    }
  }, []);

  return (
    <main className="login-page">
      <div className="login-container">
        {/* Left Side - Illustration */}
        <div className="login-illustration">
          <div className="illustration-content">
            <svg width="400" height="400" viewBox="0 0 400 400" fill="none" xmlns="http://www.w3.org/2000/svg">
              {/* Phone/Device */}
              <rect x="150" y="80" width="180" height="280" rx="20" fill="#f0f9ff" stroke="#0ea5e9" strokeWidth="3"/>
              <rect x="165" y="100" width="150" height="240" rx="8" fill="#e0f2fe"/>
              
              {/* Screen items - Order list */}
              <rect x="180" y="120" width="120" height="30" rx="6" fill="#fef3f3"/>
              <rect x="185" y="125" width="20" height="20" rx="4" fill="#dc2626"/>
              <rect x="210" y="127" width="70" height="8" rx="2" fill="#f87171"/>
              <rect x="210" y="137" width="50" height="6" rx="2" fill="#fca5a5"/>
              
              <rect x="180" y="160" width="120" height="30" rx="6" fill="#fef3f3"/>
              <rect x="185" y="165" width="20" height="20" rx="4" fill="#dc2626"/>
              <rect x="210" y="167" width="70" height="8" rx="2" fill="#f87171"/>
              <rect x="210" y="177" width="50" height="6" rx="2" fill="#fca5a5"/>
              
              <rect x="180" y="200" width="120" height="30" rx="6" fill="#fef3f3"/>
              <rect x="185" y="205" width="20" height="20" rx="4" fill="#dc2626"/>
              <rect x="210" y="207" width="70" height="8" rx="2" fill="#f87171"/>
              <rect x="210" y="217" width="50" height="6" rx="2" fill="#fca5a5"/>
              
              {/* Credit Card */}
              <g transform="translate(320, 240) rotate(15)">
                <rect x="0" y="0" width="100" height="65" rx="8" fill="#fbbf24"/>
                <rect x="0" y="15" width="100" height="12" fill="#92400e"/>
                <rect x="8" y="35" width="25" height="18" rx="3" fill="#fef3c7"/>
                <circle cx="75" cy="44" r="8" fill="#f59e0b" opacity="0.6"/>
                <circle cx="85" cy="44" r="8" fill="#f59e0b" opacity="0.6"/>
              </g>
              
              {/* Decorative elements */}
              <circle cx="80" cy="120" r="25" fill="#0ea5e9" opacity="0.1"/>
              <circle cx="360" cy="300" r="30" fill="#dc2626" opacity="0.1"/>
              <circle cx="100" cy="280" r="20" fill="#fbbf24" opacity="0.15"/>
              
              {/* Abstract shapes */}
              <path d="M 50 150 Q 70 130, 90 150" stroke="#0ea5e9" strokeWidth="3" fill="none" opacity="0.3"/>
              <path d="M 340 100 Q 360 80, 380 100" stroke="#dc2626" strokeWidth="3" fill="none" opacity="0.3"/>
            </svg>
          </div>
        </div>

        {/* Right Side - Login Form */}
        <div className="login-card">
          <h2 className="login-title">Đăng nhập tài khoản</h2>
          <p className="muted">Chào mừng quay lại với MobileHub 👋</p>

          {error && (
            <div className="form-error" role="alert">
              {error}
            </div>
          )}

          <form onSubmit={submitEmail} className="login-form" noValidate>
            <label className="field">
              <div className="label">Email</div>
              <input
                type="email"
                placeholder="example@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoComplete="email"
                disabled={loading}
              />
            </label>

            <label className="field">
              <div className="label">Mật khẩu</div>
              <input
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                autoComplete="current-password"
                disabled={loading}
              />
            </label>

            <div className="form-row">
              <label className="checkbox">
                <input
                  type="checkbox"
                  checked={remember}
                  onChange={(e) => setRemember(e.target.checked)}
                  disabled={loading}
                />
                <span>Ghi nhớ đăng nhập</span>
              </label>
            </div>

            <button
              type="submit"
              className="btn btn-primary full"
              disabled={loading}
            >
              {loading ? "Đang đăng nhập..." : "Đăng nhập"}
            </button>

            <a
              className="forgot-password"
              onClick={() => navigate("/forgot-password")}
              style={{ cursor: "pointer" }}
            >
              Quên mật khẩu?
            </a>
          </form>

          <div className="divider">Hoặc đăng nhập bằng</div>

          <GoogleLogin
            clientId="REPLACE_WITH_GOOGLE_CLIENT_ID"
            onSuccess={onLoginSuccess}
          />

          <p className="register">
            Bạn chưa có tài khoản?{" "}
            <a
              className="register-link"
              onClick={() => navigate("/register")}
              style={{ cursor: "pointer" }}
            >
              Đăng ký ngay!
            </a>
          </p>
        </div>
      </div>
    </main>
  );
}