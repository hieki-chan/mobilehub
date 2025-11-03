import React from "react";
import { motion } from "framer-motion";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

const User_Activity_Data = [
  {
    name: "Thứ 2",
    "0-4h": 20,
    "4-8h": 40,
    "8-12h": 60,
    "12-16h": 80,
    "16-20h": 100,
    "20-24h": 30,
  },
  {
    name: "Thứ 3",
    "0-4h": 30,
    "4-8h": 50,
    "8-12h": 70,
    "12-16h": 90,
    "16-20h": 110,
    "20-24h": 40,
  },
  {
    name: "Thứ 4",
    "0-4h": 40,
    "4-8h": 60,
    "8-12h": 80,
    "12-16h": 100,
    "16-20h": 120,
    "20-24h": 50,
  },
  {
    name: "Thứ 5",
    "0-4h": 50,
    "4-8h": 70,
    "8-12h": 90,
    "12-16h": 110,
    "16-20h": 130,
    "20-24h": 60,
  },
  {
    name: "Thứ 6",
    "0-4h": 60,
    "4-8h": 80,
    "8-12h": 100,
    "12-16h": 120,
    "16-20h": 140,
    "20-24h": 70,
  },
  {
    name: "Thứ 7",
    "0-4h": 70,
    "4-8h": 90,
    "8-12h": 110,
    "12-16h": 130,
    "16-20h": 150,
    "20-24h": 80,
  },
  {
    name: "Chủ nhật",
    "0-4h": 80,
    "4-8h": 100,
    "8-12h": 120,
    "12-16h": 140,
    "16-20h": 160,
    "20-24h": 90,
  },
];

const UserActivityHeatMap = () => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 1.3, delay: 0.7 }}
    >
      <h2 className="text-xl font-semibold mb-4 text-gray-100">
        Biểu đồ hoạt động người dùng theo thời gian
      </h2>

      <div style={{ width: "100%", height: 320 }}>
        <ResponsiveContainer>
          <BarChart data={User_Activity_Data}>
            <CartesianGrid strokeDasharray="3, 3" stroke="#374151" />
            <XAxis dataKey="name" stroke="#9ca3af" />
            <YAxis stroke="#9ca3af" />

            <Tooltip
              contentStyle={{
                backgroundColor: "rgba(31, 45, 55, 0.85)",
                borderColor: "#4b5563",
                borderRadius: 8,
              }}
              itemStyle={{ color: "#e5e7eb" }}
              labelStyle={{ color: "#93c5fd" }}
            />

            <Legend wrapperStyle={{ color: "#e5e7eb" }} />

            <Bar dataKey="0-4h" stackId="a" fill="#5B82F7" />
            <Bar dataKey="4-8h" stackId="a" fill="#8B5CF6" />
            <Bar dataKey="8-12h" stackId="a" fill="#EC4899" />
            <Bar dataKey="12-16h" stackId="a" fill="#10B981" />
            <Bar dataKey="16-20h" stackId="a" fill="#F59E0B" />
            <Bar dataKey="20-24h" stackId="a" fill="#6EE7B7" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
};

export default UserActivityHeatMap;
