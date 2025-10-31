import React from "react";
import { Navigate, Outlet } from "react-router-dom";

const PrivateRoute = () => {
  // kiểm tra trạng thái đăng nhập
  const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";

  // nếu chưa đăng nhập → chuyển sang trang login
  return isLoggedIn ? <Outlet /> : <Navigate to="/login" replace />;
};

export default PrivateRoute;
