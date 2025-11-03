import React from "react";
import { motion } from "framer-motion";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

// 📊 Dữ liệu đơn hàng theo ngày
const DuLieu_DonHang = [
  { ngay: "07/01", DonHang: 48 },
  { ngay: "07/02", DonHang: 42 },
  { ngay: "07/03", DonHang: 49 },
  { ngay: "07/04", DonHang: 62 },
  { ngay: "07/05", DonHang: 55 },
  { ngay: "07/06", DonHang: 52 },
  { ngay: "07/07", DonHang: 62 },
];

const BieuDoDonHangHangNgay = () => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.2 }}
    >
      <h2 className="text-xl font-semibold mb-4 text-gray-100">
        Đơn hàng hàng ngày
      </h2>

      <div className="h-80">
        <ResponsiveContainer width={"100%"} height={"100%"}>
          <LineChart data={DuLieu_DonHang}>
            <CartesianGrid strokeDasharray={"3 3"} stroke="#4b5563" />
            <XAxis
              dataKey={"ngay"}
              stroke="#9ca3af"
              label={{
                value: "Ngày",
                position: "insideBottom",
                offset: -5,
                fill: "#9ca3af",
              }}
            />
            <YAxis
              stroke="#9ca3af"
              label={{
                value: "Số đơn",
                angle: -90,
                position: "insideLeft",
                fill: "#9ca3af",
              }}
            />
            <Tooltip
              contentStyle={{
                backgroundColor: "rgba(31, 45, 55, 0.8)",
                borderColor: "#4b5563",
              }}
              itemStyle={{ color: "#e5e7eb" }}
              labelStyle={{ color: "#a78bfa" }}
              formatter={(value) => [`${value} đơn`, "Số lượng"]}
              labelFormatter={(label) => `Ngày ${label}`}
            />
            <Line
              type="monotone"
              dataKey="DonHang"
              stroke="#6366f1"
              strokeWidth={3}
              dot={{ fill: "#6366f1", strokeWidth: 2, r: 5 }}
              activeDot={{ r: 8, strokeWidth: 2 }}
            />
            <Legend formatter={() => "Số lượng đơn hàng"} />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
};

export default BieuDoDonHangHangNgay;
