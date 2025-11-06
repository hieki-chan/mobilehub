import React from "react";
import { motion } from "framer-motion";
import { ArrowUpRight, ArrowDownRight } from "lucide-react";

const StatCards = ({ name, icon: Icon, value, color, bg, change }) => {
  const hasChange = change !== undefined && change !== null && change !== 0;
  const isPositive = hasChange && change > 0;

  return (
    <motion.div
      initial={{ opacity: 0, y: 6 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.08 }}
      whileHover={{
        y: -2,
        boxShadow: "0 10px 25px -5px rgba(0,0,0,0.1)",
      }}
      className={`bg-white ${bg || ""} overflow-hidden shadow-sm rounded-lg border border-gray-200`}
    >
      <div className="px-4 py-5 sm:p-6">
        <span className="flex items-center text-sm font-medium text-gray-600">
          <Icon size={22} className="mr-2" style={{ color }} />
          {name}
        </span>

        <div className="mt-2 flex items-end justify-between">
          <p className="text-gray-900 font-semibold text-[27px]">{value}</p>

          {hasChange ? (
            <div
              className={`flex items-center text-sm font-medium ${
                isPositive ? "text-green-600" : "text-red-500"
              }`}
            >
              {isPositive ? (
                <ArrowUpRight size={16} className="mr-1" />
              ) : (
                <ArrowDownRight size={16} className="mr-1" />
              )}
              {Math.abs(change)}%
            </div>
          ) : (
            <span className="text-sm text-gray-500 italic">Không có thay đổi</span>
          )}
        </div>
      </div>
    </motion.div>
  );
};

export default StatCards;
