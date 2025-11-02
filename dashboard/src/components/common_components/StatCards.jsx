import React from 'react'
import { motion } from 'framer-motion'

const StatCards = ({ name, icon: Icon, value, color, bg }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 6 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.08 }} // nhanh hơn khi init
      whileHover={{
        y: -4,
        boxShadow: "0 10px 25px -5px rgba(0,0,0,0.1)"
      }}
      className={`bg-white ${bg || ''} overflow-hidden shadow-sm rounded-lg border border-gray-200`}
    >
      <div className="px-4 py-5 sm:p-6">
        <span className="flex items-center text-sm font-medium text-gray-600">
          <Icon size={22} className="mr-2" style={{ color }} />
          {name}
        </span>
        <p className="mt-2 text-gray-900 font-semibold text-[27px]">
          {value}
        </p>
      </div>
    </motion.div>
  )
}

export default StatCards
