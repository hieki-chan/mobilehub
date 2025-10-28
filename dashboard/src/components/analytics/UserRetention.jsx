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
import { motion } from "framer-motion";

const duLieuGiuChanNguoiDung = [
  { name: "Tuần 1", GiuChan: 100 },
  { name: "Tuần 2", GiuChan: 76 },
  { name: "Tuần 3", GiuChan: 60 },
  { name: "Tuần 4", GiuChan: 50 },
  { name: "Tuần 5", GiuChan: 45 },
  { name: "Tuần 6", GiuChan: 40 },
  { name: "Tuần 7", GiuChan: 35 },
  { name: "Tuần 8", GiuChan: 30 },
];

const GiuChanNguoiDung = () => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 backdrop-filter backdrop-blur-lg shadow-lg rounded-xl p-6 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 1, duration: 0.5 }}
    >
      <h2 className="text-xl font-semibold text-gray-100 mb-4">
        Tỷ lệ giữ chân người dùng
      </h2>

      <div style={{ width: "100%", height: 300 }}>
        <ResponsiveContainer>
          <LineChart data={duLieuGiuChanNguoiDung}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis dataKey="name" stroke="#9CA3AF" />
            <YAxis stroke="#9CA3AF" />
            <Tooltip
              contentStyle={{
                backgroundColor: "rgba(31, 41, 55, 0.8)",
                borderColor: "#4B5563",
              }}
              itemStyle={{ color: "#E5E7EB" }}
              formatter={(value) => [`${value}%`, "Tỷ lệ giữ chân"]}
            />
            <Legend formatter={() => "Tỷ lệ giữ chân"} />
            <Line
              type="monotone"
              dataKey="GiuChan"
              stroke="#8B5CF6"
              strokeWidth={2}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
};

export default GiuChanNguoiDung;
