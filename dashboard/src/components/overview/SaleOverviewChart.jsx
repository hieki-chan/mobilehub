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

// Dữ liệu doanh thu theo tháng
const Sales_Data = [
  { name: "Tháng 8", Sales: 3000 },
  { name: "Tháng 9", Sales: 3700 },
  { name: "Tháng 10", Sales: 5200 },
  { name: "Tháng 11", Sales: 4600 },
  { name: "Tháng 12", Sales: 5400 },
  { name: "Tháng 1", Sales: 7300 },
  { name: "Tháng 2", Sales: 6100 },
  { name: "Tháng 3", Sales: 5600 },
  { name: "Tháng 4", Sales: 6600 },
  { name: "Tháng 5", Sales: 6200 },
  { name: "Tháng 6", Sales: 7100 },
  { name: "Tháng 7", Sales: 7700 },
];

const SaleOverviewChart = () => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.2 }}
    >
      <h2 className="text-lg font-medium mb-4 text-gray-100">
        Tổng quan doanh thu
      </h2>

      <div className="h-80">
        <ResponsiveContainer width={"100%"} height={"100%"}>
          <LineChart data={Sales_Data}>
            <CartesianGrid strokeDasharray={"3 3"} stroke="#4b5563" />
            <XAxis dataKey={"name"} stroke="#9ca3af" />
            <YAxis stroke="#9ca3af" />
            <Tooltip
              formatter={(value) => [
                `${value.toLocaleString()} ₫`,
                "Doanh thu",
              ]}
              labelFormatter={(label) => `${label}`}
              contentStyle={{
                backgroundColor: "rgba(31, 45, 55, 0.8)",
                borderColor: "#4b5563",
              }}
              itemStyle={{ color: "#e5e7eb" }}
            />
            <Line
              type="monotone"
              dataKey="Sales"
              stroke="#6366f1"
              strokeWidth={3}
              dot={{ fill: "#6366f1", strokeWidth: 2, r: 5 }}
              activeDot={{ r: 8, strokeWidth: 2 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
};

export default SaleOverviewChart;
