import React, { useEffect, useState } from "react";
import {
  BarChart2,
  DollarSign,
  Settings,
  ShoppingBag,
  ShoppingCart,
  TrendingUp,
  Users,
  Menu,
} from "lucide-react";
import { AnimatePresence, motion } from "framer-motion";

//sua
import { Link, useLocation, useNavigate } from "react-router-dom";

const SIDEBAR_ITEMS = [
  { name: "Tổng quan", icon: BarChart2, color: "#6366f1", href: "/" },
  { name: "Sản phẩm", icon: ShoppingBag, color: "#8B5CF6", href: "/products" },
  { name: "Người dùng", icon: Users, color: "#EC4899", href: "/users" },
  { name: "Doanh số", icon: DollarSign, color: "#10B981", href: "/sales" },
  { name: "Đơn hàng", icon: ShoppingCart, color: "#F59E0B", href: "/orders" },
  { name: "Phân tích", icon: TrendingUp, color: "#3B82F6", href: "/analytics" },
  { name: "Cài đặt", icon: Settings, color: "#6EE7B7", href: "/settings" },
];

const Sidebar = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [isMobile, setIsMobile] = useState(false);
  const location = useLocation(); // Get current route
  //them
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("isLoggedIn");
    navigate("/login");
  };

  useEffect(() => {
    const mediaQuery = window.matchMedia("(max-width: 768px)");

    const handleMediaQueryChange = (e) => {
      setIsMobile(e.matches);
      setIsSidebarOpen(!e.matches);
    };

    handleMediaQueryChange(mediaQuery);
    mediaQuery.addEventListener("change", handleMediaQueryChange);

    return () =>
      mediaQuery.removeEventListener("change", handleMediaQueryChange);
  }, []);

  return (
    <>
      <motion.div
        className={`relative z-10 transition-all duration-200 ease-in-out flex-shrink-0 ${
          isSidebarOpen ? "w-64" : "w-20"
        }`}
        animate={{ width: isSidebarOpen ? 220 : 80 }}
        transition={{ duration: 0.1 }}
      >
        <div className="h-full bg-gray-800 bg-opacity-50 backdrop-blur-md p-4 flex flex-col border-r border-gray-700">
          <motion.button
            whileHover={{ scale: 1.2 }}
            whileTap={{ scale: 0.8 }}
            onClick={() => setIsSidebarOpen(!isSidebarOpen)}
            className="p-2 rounded-full hover:bg-gray-700 transition-colors max-w-fit"
            disabled={isMobile} // Disable button on mobile devices
          >
            <Menu size={26} />
          </motion.button>

          <nav className="mt-8 flex-grow">
            {SIDEBAR_ITEMS.map((item) => {
              const isActive = location.pathname === item.href;
              return (
                <Link key={item.href} to={item.href}>
                  <motion.div
                    className={`flex items-center font-medium p-4 mb-2 text-sm rounded-lg transition-colors
                                            ${
                                              isActive
                                                ? "bg-gray-700 text-white shadow-lg border border-gray-600"
                                                : "hover:bg-gray-700/50"
                                            }`}
                  >
                    <item.icon
                      size={20}
                      style={{
                        color: isActive ? "#fff" : item.color,
                        minWidth: "20px",
                      }}
                    />
                    <AnimatePresence>
                      {isSidebarOpen && (
                        <motion.span
                          className="ml-4 whitespace-nowrap"
                          initial={{ opacity: 0, width: 0 }}
                          animate={{ opacity: 1, width: "auto" }}
                          exit={{ opacity: 0, width: 0 }}
                          transition={{ duration: 0.05 }}
                        >
                          {item.name}
                        </motion.span>
                      )}
                    </AnimatePresence>
                  </motion.div>
                </Link>
              );
            })}
          </nav>
          {/* 🟢 Nút Đăng xuất */}
          <button
            onClick={handleLogout}
            className="mt-auto flex items-center justify-center w-full p-3 text-sm font-medium rounded-lg bg-red-600 hover:bg-red-700 text-white transition"
          >
            Đăng xuất
          </button>
        </div>
      </motion.div>
    </>
  );
};

export default Sidebar;
