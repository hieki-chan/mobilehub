import React, { useState } from "react";
import { motion } from "framer-motion";
import {
  AreaChart,
  Area,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
  Legend,
} from "recharts";

const DuLieuDoanhThu = [
  { month: "Tháng 1", DoanhThu: 4200, MucTieu: 5000 },
  { month: "Tháng 2", DoanhThu: 3000, MucTieu: 3200 },
  { month: "Tháng 3", DoanhThu: 5500, MucTieu: 4500 },
  { month: "Tháng 4", DoanhThu: 4500, MucTieu: 4200 },
  { month: "Tháng 5", DoanhThu: 5500, MucTieu: 6000 },
  { month: "Tháng 6", DoanhThu: 4500, MucTieu: 4800 },
  { month: "Tháng 7", DoanhThu: 7000, MucTieu: 6500 },
];

const BieuDoDoanhThu = () => {
  const [KhoangThoiGian, setKhoangThoiGian] = useState("Quý này");

  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 text-center lg:col-span-2 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.2, delay: 0.2 }}
    >
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold mb-4 text-gray-100">
          Doanh thu so với Mục tiêu
        </h2>

        <select
          className="bg-gray-700 text-white rounded-md px-3 py-1 focus:outline-none focus:ring-2 focus:ring-blue-600"
          value={KhoangThoiGian}
          onChange={(e) => setKhoangThoiGian(e.target.value)}
        >
          <option>Tuần này</option>
          <option>Tháng này</option>
          <option>Quý này</option>
          <option>Năm nay</option>
        </select>
      </div>

      <div className="w-full h-80">
        <ResponsiveContainer>
          <AreaChart data={DuLieuDoanhThu}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis dataKey="month" stroke="#9ca3af" />
            <YAxis stroke="#9ca3af" />
            <Tooltip
              contentStyle={{
                backgroundColor: "rgba(31, 41, 55, 0.8)",
                borderColor: "#4b5563",
              }}
              itemStyle={{ color: "#e5e7eb" }}
              formatter={(value, name) =>
                name === "DoanhThu"
                  ? [`${value} USD`, "Doanh thu"]
                  : [`${value} USD`, "Mục tiêu"]
              }
            />
            <Legend
              formatter={(value) =>
                value === "DoanhThu" ? "Doanh thu" : "Mục tiêu"
              }
            />
            <Area
              type="monotone"
              dataKey="DoanhThu"
              stroke="#8b5cf6"
              fill="#8b5cf6"
              fillOpacity={0.3}
            />
            <Area
              type="monotone"
              dataKey="MucTieu"
              stroke="#10b981"
              fill="#10b981"
              fillOpacity={0.3}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
};

export default BieuDoDoanhThu;
