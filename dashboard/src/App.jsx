import React from "react";
import { Routes, Route, useLocation } from "react-router-dom";

import Sidebar from "./components/common_components/Sidebar";
import Header from "./components/common_components/Header";
import PrivateRoute from "./components/common_components/PrivateRoute";

import OverviewPage from "./pages/OverviewPage";

import ProductsPage from "./pages/ProductsPage";
import ProductRatingPage from "./pages/ProductRatingPage";

import UsersPage from "./pages/UsersPage";
import SalesPage from "./pages/SalesPage";
import OrdersPage from "./pages/OrdersPage";
import AnalyticsPage from "./pages/AnalyticsPage";
import SettingsPage from "./pages/SettingsPage";

import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";

const App = () => {
  const location = useLocation();
  const noSidebarRoutes = ["/login", "/forgot-password", "/register"];
  const hideSidebar = noSidebarRoutes.includes(location.pathname);

  return (
    <div className="flex h-screen bg-gray-50 text-gray-900 overflow-hidden">
      {/* BACKGROUND — Light, Clean */}
      <div className="fixed inset-0 z-0">
        <div className="absolute inset-0 bg-gradient-to-br from-white via-gray-50 to-gray-100 opacity-100" />
        <div className="absolute inset-0 backdrop-blur-xl" />
      </div>

      {/* Sidebar */}
      {!hideSidebar && <Sidebar />}

      {/* Content */}
      <Routes>
            {/* Public routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />

            {/* Private routes */}
            <Route element={<PrivateRoute />}>
              <Route path="/" element={<OverviewPage />} />
              <Route path="/products" element={<ProductsPage />} />
              <Route path="/products/:id/reviews" element={<ProductRatingPage />} />
              <Route path="/users" element={<UsersPage />} />
              <Route path="/sales" element={<SalesPage />} />
              <Route path="/orders" element={<OrdersPage />} />
              <Route path="/analytics" element={<AnalyticsPage />} />
              <Route path="/settings" element={<SettingsPage />} />
            </Route>
          </Routes>
    </div>
  );
};

export default App;
