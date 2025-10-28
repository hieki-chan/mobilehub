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
import { Link } from "react-router-dom";

const DANH_MUC_THANH_BEN = [
  { name: "Tổng quan", icon: BarChart2, color: "#6366f1", href: "/" },
  { name: "Sản phẩm", icon: ShoppingBag, color: "#8B5CF6", href: "/products" },
  { name: "Người dùng", icon: Users, color: "#EC4899", href: "/users" },
  { name: "Doanh số", icon: DollarSign, color: "#10B981", href: "/sales" },
  { name: "Đơn hàng", icon: ShoppingCart, color: "#F59E0B", href: "/orders" },
  { name: "Phân tích", icon: TrendingUp, color: "#3B82F6", href: "/analytics" },
  { name: "Cài đặt", icon: Settings, color: "#6EE7B7", href: "/settings" },
];

const ThanhBen = () => {
  const [moThanhBen, setMoThanhBen] = useState(true);
  const [laMobile, setLaMobile] = useState(false);

  useEffect(() => {
    const mediaQuery = window.matchMedia("(max-width: 768px)");

    const handleMediaQueryChange = (e) => {
      setLaMobile(e.matches);
      setMoThanhBen(!e.matches);
    };

    handleMediaQueryChange(mediaQuery);
    mediaQuery.addEventListener("change", handleMediaQueryChange);

    return () =>
      mediaQuery.removeEventListener("change", handleMediaQueryChange);
  }, []);

  return (
    <>
      <motion.div
        className={`relative z-10 transition-all duration-300 ease-in-out flex-shrink-0 ${
          moThanhBen ? "w-64" : "w-20"
        }`}
        animate={{ width: moThanhBen ? 220 : 80 }}
      >
        <div className="h-full bg-gray-800 bg-opacity-50 backdrop-blur-md p-4 flex flex-col border-r border-gray-700">
          <motion.button
            whileHover={{ scale: 1.2 }}
            whileTap={{ scale: 0.8 }}
            onClick={() => setMoThanhBen(!moThanhBen)}
            className="p-2 rounded-full hover:bg-gray-700 transition-colors max-w-fit"
            disabled={laMobile}
          >
            <Menu size={26} />
          </motion.button>

          <nav className="mt-8 flex-grow">
            {DANH_MUC_THANH_BEN.map((item) => (
              <Link key={item.href} to={item.href}>
                <motion.div className="flex items-center font-medium p-4 mb-2 text-sm rounded-lg hover:bg-gray-700 transition-colors">
                  <item.icon
                    size={20}
                    style={{ color: item.color, minWidth: "20px" }}
                  />
                  <AnimatePresence>
                    {moThanhBen && (
                      <motion.span
                        className="ml-4 whitespace-nowrap"
                        initial={{ opacity: 0, width: 0 }}
                        animate={{ opacity: 1, width: "auto" }}
                        exit={{ opacity: 0, width: 0 }}
                        transition={{ duration: 0.2, delay: 0.3 }}
                      >
                        {item.name}
                      </motion.span>
                    )}
                  </AnimatePresence>
                </motion.div>
              </Link>
            ))}
          </nav>
        </div>
      </motion.div>
    </>
  );
};

export default ThanhBen;
