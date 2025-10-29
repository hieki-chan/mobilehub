import React from "react";
import { motion } from "framer-motion";
import {
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
} from "recharts";

// Màu sắc biểu đồ
const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#ff8042", "#0088fe"];

// Dữ liệu nhân khẩu học người dùng
const DuLieuNhanKhauHoc = [
  { name: "18-24 tuổi", value: 20 },
  { name: "25-34 tuổi", value: 30 },
  { name: "35-44 tuổi", value: 25 },
  { name: "45-54 tuổi", value: 15 },
  { name: "55 tuổi trở lên", value: 10 },
];

const UserDemographicChart = () => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 lg:col-span-2 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 1.8, delay: 1.2 }}
    >
      <h2 className="text-xl font-semibold mb-4 text-gray-100">
        Nhân khẩu học người dùng
      </h2>

      <div style={{ width: "100%", height: 300 }}>
        <ResponsiveContainer>
          <PieChart>
            <Pie
              data={DuLieuNhanKhauHoc}
              cx={"50%"}
              cy={"50%"}
              outerRadius={100}
              fill="#8884d8"
              labelLine={false}
              dataKey="value"
              label={({ name, percent }) =>
                `${name} ${(percent * 100).toFixed(0)}%`
              }
            >
              {DuLieuNhanKhauHoc.map((items, index) => (
                <Cell
                  key={`cell-${index}`}
                  fill={COLORS[index % COLORS.length]}
                />
              ))}
            </Pie>

            <Tooltip
              contentStyle={{
                backgroundColor: "rgba(31, 45, 55, 0.8)",
                borderColor: "#4b5563",
              }}
              itemStyle={{ color: "#e5e7eb" }}
              formatter={(value, name) => [`${value}%`, `${name}`]}
              labelFormatter={(label) => `Nhóm tuổi: ${label}`}
            />
            <Legend
              formatter={(value) => (
                <span className="text-gray-300">{value}</span>
              )}
            />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
};

export default UserDemographicChart;
