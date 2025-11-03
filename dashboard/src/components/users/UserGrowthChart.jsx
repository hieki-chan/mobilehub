import React from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { motion } from "framer-motion";

// Dữ liệu tăng trưởng người dùng theo tháng
const DuLieuTangTruongNguoiDung = [
  { name: "Tháng 8", Users: 800 },
  { name: "Tháng 9", Users: 1500 },
  { name: "Tháng 10", Users: 1600 },
  { name: "Tháng 11", Users: 1950 },
  { name: "Tháng 12", Users: 3300 },
  { name: "Tháng 1", Users: 4000 },
];

const UserGrowthChart = () => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 1.3, delay: 0.7 }}
    >
      <h2 className="text-xl font-semibold mb-4 text-gray-100">
        Biểu đồ tăng trưởng người dùng
      </h2>

      <div className="h-80">
        <ResponsiveContainer width={"100%"} height={"100%"}>
          <LineChart data={DuLieuTangTruongNguoiDung}>
            <CartesianGrid strokeDasharray={"3 3"} stroke="#374151" />
            <XAxis dataKey={"name"} stroke="#9ca3af" />
            <YAxis stroke="#9ca3af" />
            <Tooltip
              formatter={(value) => [`${value} người dùng`, "Số lượng"]}
              labelFormatter={(label) => `Tháng: ${label}`}
              contentStyle={{
                backgroundColor: "rgba(31, 45, 55, 0.8)",
                borderColor: "#4b5563",
              }}
              itemStyle={{ color: "#e5e7eb" }}
            />
            <Line
              type="linear"
              dataKey="Users"
              stroke="#8b5cf6"
              strokeWidth={2}
              dot={{ fill: "#8b5cf6", strokeWidth: 2, r: 4 }}
              activeDot={{ r: 8 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
};

export default UserGrowthChart;
