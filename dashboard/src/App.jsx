import React from "react";
import { Routes, Route } from "react-router-dom";

import Sidebar from "./components/common_components/Sidebar";

import OverviewPage from "./pages/OverviewPage";
import ProductsPage from "./pages/ProductsPage";
import UsersPage from "./pages/UsersPage";
import SalesPage from "./pages/SalesPage";
import OrdersPage from "./pages/OrdersPage";
import AnalyticsPage from "./pages/AnalyticsPage";
import SettingsPage from "./pages/SettingsPage";

const App = () => {
  return (
    <div className="flex h-screen bg-gray-900 text-gray-100 overflow-hidden">
      {/* NỀN (HIỆU ỨNG BACKGROUND) */}
      <div className="fixed inset-0 z-0">
        <div className="absolute inset-0 bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 opacity-80" />
        <div className="absolute inset-0 backdrop-blur-3xl" />
      </div>

      {/* THANH ĐIỀU HƯỚNG BÊN (SIDEBAR) */}
      <Sidebar />

      {/* CÁC TUYẾN TRANG (ROUTES) */}
      <Routes>
        <Route path="/" element={<OverviewPage />} /> {/* Tổng quan */}
        <Route path="/products" element={<ProductsPage />} /> {/* Sản phẩm */}
        <Route path="/users" element={<UsersPage />} /> {/* Người dùng */}
        <Route path="/sales" element={<SalesPage />} /> {/* Doanh số */}
        <Route path="/orders" element={<OrdersPage />} /> {/* Đơn hàng */}
        <Route path="/analytics" element={<AnalyticsPage />} />{" "}
        {/* Phân tích */}
        <Route path="/settings" element={<SettingsPage />} /> {/* Cài đặt */}
      </Routes>
    </div>
  );
};

export default App;
