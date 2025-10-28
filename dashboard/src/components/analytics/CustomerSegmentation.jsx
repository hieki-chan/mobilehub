import React from "react";
import { motion } from "framer-motion";
import {
  ResponsiveContainer,
  Radar,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Legend,
  Tooltip,
} from "recharts";

const DuLieuPhanKhucKhachHang = [
  { subject: "Tương tác", NhomA: 120, NhomB: 110, fullMark: 150 },
  { subject: "Trung thành", NhomA: 98, NhomB: 130, fullMark: 150 },
  { subject: "Hài lòng", NhomA: 86, NhomB: 130, fullMark: 150 },
  { subject: "Chi tiêu", NhomA: 99, NhomB: 100, fullMark: 150 },
  { subject: "Tần suất mua", NhomA: 85, NhomB: 90, fullMark: 150 },
  { subject: "Thời gian quay lại", NhomA: 65, NhomB: 85, fullMark: 150 },
];

const PhanKhucKhachHang = () => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 backdrop-filter backdrop-blur-lg shadow-lg rounded-xl p-6 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 1, duration: 0.5 }}
    >
      <h2 className="text-xl font-semibold text-gray-100 mb-4">
        Phân khúc khách hàng
      </h2>

      <ResponsiveContainer width="100%" height={300}>
        <RadarChart
          data={DuLieuPhanKhucKhachHang}
          cx="50%"
          cy="50%"
          outerRadius="80%"
        >
          <PolarGrid stroke="#374151" />
          <PolarAngleAxis dataKey="subject" stroke="#7ca3af" />
          <PolarRadiusAxis angle={30} domain={[0, 150]} stroke="#9ca3af" />
          <Radar
            name="Nhóm A"
            dataKey="NhomA"
            stroke="#8b5cf6"
            fill="#8b5cf6"
            fillOpacity={0.6}
          />
          <Radar
            name="Nhóm B"
            dataKey="NhomB"
            stroke="#10b981"
            fill="#10b981"
            fillOpacity={0.6}
          />
          <Legend
            formatter={(value) => (value === "NhomA" ? "Nhóm A" : "Nhóm B")}
          />
          <Tooltip
            formatter={(value, name) => [
              value,
              name === "NhomA" ? "Nhóm A" : "Nhóm B",
            ]}
            contentStyle={{
              backgroundColor: "rgba(31, 41, 55, 0.8)",
              borderColor: "#4B5563",
            }}
            itemStyle={{ color: "#E5E7EB" }}
          />
        </RadarChart>
      </ResponsiveContainer>
    </motion.div>
  );
};

export default PhanKhucKhachHang;
