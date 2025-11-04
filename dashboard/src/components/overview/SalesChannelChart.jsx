import React from "react";
import { motion } from "framer-motion";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
  Cell,
} from "recharts";

const Sales_Channel_Data = [
  { name: "Website", Value: 53000 },
  { name: "Ứng dụng di động", Value: 38200 },
  { name: "Sàn thương mại điện tử", Value: 17000 },
  { name: "Mạng xã hội", Value: 28700 },
  { name: "Tiếp thị qua Email", Value: 50000 },
  { name: "Tiếp thị liên kết", Value: 35000 },
  { name: "Bán hàng trực tiếp", Value: 25000 },
];

const COLORS = [
  "#6366f1",
  "#8B5CF6",
  "#EC4899",
  "#10B981",
  "#F59E0B",
  "#3B82F6",
  "#6EE7B7",
];

const SalesChannelChart = () => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 lg:col-span-2 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6, delay: 0.5 }}
    >
      <h2 className="text-lg font-medium mb-4 text-gray-100">
        Doanh thu theo kênh bán hàng
      </h2>

      <div className="h-80">
        <ResponsiveContainer>
          <BarChart data={Sales_Channel_Data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#4b5563" />
            <XAxis dataKey="name" stroke="#9ca3af" />
            <YAxis stroke="#9ca3af" />
            <Tooltip
              formatter={(value) => [
                `${value.toLocaleString()} ₫`,
                "Doanh thu",
              ]}
              contentStyle={{
                backgroundColor: "rgba(31, 41, 55, 0.8)",
                borderColor: "#4b5563",
              }}
              itemStyle={{ color: "#e5e7eb" }}
            />
            <Legend
              formatter={() => "Doanh thu"}
              wrapperStyle={{ color: "#e5e7eb" }}
            />
            <Bar dataKey="Value">
              {Sales_Channel_Data.map((item, index) => (
                <Cell
                  key={`cell-${index}`}
                  fill={COLORS[index % COLORS.length]}
                />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
};

export default SalesChannelChart;
