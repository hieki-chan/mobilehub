import React from "react";
import { motion } from "framer-motion";

const StatCards = ({ name, icon: Icon, value, color }) => {
  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 backdrop-blur-md shadow-lg rounded-xl p-5 border border-gray-700"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6 }}
    >
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-gray-400 text-sm font-medium">{name}</h3>
          <p className="text-2xl font-semibold text-gray-100 mt-1">{value}</p>
        </div>
        <div
          className="p-3 rounded-full"
          style={{ backgroundColor: color + "33" }}
        >
          <Icon color={color} size={28} />
        </div>
      </div>
    </motion.div>
  );
};

export default StatCards;
