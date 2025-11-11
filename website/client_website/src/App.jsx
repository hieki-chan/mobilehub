import React, { useEffect, useState } from "react";
import { Routes, Route } from "react-router-dom";
import Loading from "./pages/Loading";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import ProductDetail from "./pages/ProductDetail";
import Cart from "./pages/Cart";
import SearchResults from "./pages/SearchResults";
import Profile from "./pages/Profile";
import Checkout from "./pages/Checkout";
import Installment from "./pages/Installment";
import { verifyToken, logout } from "./api/AuthApi";

export default function App() {
  const [isChecking, setIsChecking] = useState(true);
  const [retryCount, setRetryCount] = useState(0);

  const hideFooterPaths = ["/product/", "/cart", "/search"];
  const currentPath = window.location.pathname;
  const hideFooter = hideFooterPaths.some((p) => currentPath.startsWith(p));

  useEffect(() => {
    const checkLogin = async () => {
      const token = localStorage.getItem("token");
      const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";

      if (!isLoggedIn || !token) {
        setIsChecking(false);
        return;
      }

      const maxRetries = 3;

      for (let i = 1; i <= maxRetries; i++) {
        try {
          const valid = await verifyToken(token);

          if (!valid) {
            console.warn("⚠️ Token invalid or expired. Logging out...");
            logout();
            setIsChecking(false)
            return;
          }

          setIsChecking(false);
          return;
        } catch (error) {
          console.warn(`⚠️ Không thể kết nối server (lần ${i}/${maxRetries})...`);
          setRetryCount(i);

          await new Promise((resolve) => setTimeout(resolve, 1000));
        }
      }

      console.warn("❌ Kết nối thất bại sau 3 lần. Tự động đăng xuất.");
      logout();
      setIsChecking(false);
    };
    checkLogin();
  }, []);

  if (isChecking) return <Loading retryCount={retryCount} />;

  return (
    <div style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
      <Header />
      <div style={{ flex: 1 }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/product/:id" element={<ProductDetail />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/search" element={<SearchResults />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/installment" element={<Installment />} />
        </Routes>
      </div>
      {!hideFooter && <Footer />}
    </div>
  );
}
