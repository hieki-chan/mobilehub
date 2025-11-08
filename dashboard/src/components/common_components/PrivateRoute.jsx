import React, { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { verifyToken, clearAccountData } from "../../api/AuthApi";

const PrivateRoute = () => {
  const [isValid, setIsValid] = useState(null);

  useEffect(() => {
    const checkToken = async () => {
      const token = localStorage.getItem("token");
      const role = localStorage.getItem("role");

      if (!token || role !== "ADMIN") {
        setIsValid(false);
        return;
      }

      const valid = await verifyToken(token);
      if (!valid) {
        clearAccountData();
        setIsValid(false);
      } else {
        setIsValid(true);
      }
    };

    checkToken();
  }, []);

  if (isValid === null)
    return (
      <div className="text-center text-gray-300 w-full h-screen flex items-center justify-center bg-gray-900">
        🔄 Đang kiểm tra phiên đăng nhập...
      </div>
    );

  return isValid ? <Outlet /> : <Navigate to="/login" replace />;
};

export default PrivateRoute;
