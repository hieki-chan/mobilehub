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
          className="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
        >
          <motion.div
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.9, opacity: 0 }}
            transition={{ duration: 0.15 }}
            className="bg-white border border-gray-200 p-6 rounded-2xl shadow-2xl max-w-sm w-[90%] relative"
          >
            {/* Nút đóng */}
            <button
              onClick={() => handleClose(false)}
              className="absolute top-3 right-3 text-gray-400 hover:text-gray-700"
            >
              <X size={20} />
            </button>

            {/* Nội dung */}
            <h2 className="text-xl font-semibold text-gray-900 mb-3">
              {title}
            </h2>
            <p className="text-gray-600 text-sm mb-6 leading-relaxed">
              {message}
            </p>

            {/* Hành động */}
            <div className="flex justify-end gap-3">
              <button
                onClick={() => handleClose(false)}
                className="bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-md transition"
              >
                Hủy
              </button>
              <button
                onClick={() => handleClose(true)}
                className="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-md transition"
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
