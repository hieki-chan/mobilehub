import React from "react";
import { motion } from "framer-motion";

const TheThongKe = ({ ten, bieuTuong: BieuTuong, giaTri, mau }) => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 backdrop-blur-md overflow-hidden shadow-lg rounded-lg border border-gray-700"
      whileHover={{
        y: -6,
        boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.5)",
        transition: { duration: 0.25, ease: "easeOut" },
      }}
    >
      <div className="px-4 py-5 sm:p-6">
        <span className="flex items-center text-sm font-medium text-gray-200">
          {BieuTuong && (
            <BieuTuong size={22} className="mr-2" style={{ color: mau }} />
          )}
          {ten}
        </span>
        <p className="mt-2 text-gray-100 font-semibold text-[27px]">{giaTri}</p>
      </div>
    </motion.div>
  );
};

export default TheThongKe;
