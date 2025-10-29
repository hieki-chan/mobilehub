import { createRoot } from "react-dom/client";
import { motion, AnimatePresence } from "framer-motion";
import { X } from "lucide-react";
import React from "react";

export function showPopupConfirm(title, message) {
  return new Promise((resolve) => {
    const container = document.createElement("div");
    document.body.appendChild(container);
    const root = createRoot(container);

    const handleClose = (result) => {
      root.unmount();
      container.remove();
      resolve(result);
    };

    const Popup = () => (
      <AnimatePresence>
        <motion.div
          className="fixed inset-0 bg-black/60 flex items-center justify-center z-[9999]"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
        >
          <motion.div
            initial={{ scale: 0.85, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.85, opacity: 0 }}
            transition={{ duration: 0.1 }}
            className="bg-gray-800 border border-gray-700 p-6 rounded-2xl shadow-2xl max-w-sm w-[90%] relative"
          >
            {/* Nút đóng */}
            <button
              onClick={() => handleClose(false)}
              className="absolute top-3 right-3 text-gray-400 hover:text-gray-200"
            >
              <X size={20} />
            </button>

            {/* Nội dung */}
            <h2 className="text-xl font-semibold text-white mb-3">
              {title}
            </h2>
            <p className="text-gray-300 text-sm mb-6 leading-relaxed">
              {message}
            </p>

            {/* Hành động */}
            <div className="flex justify-end gap-3">
              <button
                onClick={() => handleClose(false)}
                className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-md"
              >
                Hủy
              </button>
              <button
                onClick={() => handleClose(true)}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md"
              >
                OK
              </button>
            </div>
          </motion.div>
        </motion.div>
      </AnimatePresence>
    );

    root.render(<Popup />);
  });
}
