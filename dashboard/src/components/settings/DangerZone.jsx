import React, { useState } from "react";
import { Trash2 } from "lucide-react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { Bounce, ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const DangerZone = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();

  const handleDeleteClick = () => {
    setIsModalOpen(true);
  };

  const handleConfirmDelete = () => {
    setIsModalOpen(false);

    // Hiển thị thông báo xóa tài khoản
    toast.success("Tài khoản của bạn đã được xóa thành công.", {
      position: "top-right",
      autoClose: 3000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      transition: Bounce,
      theme: "light",
    });

    // Chờ thông báo hiện xong rồi quay lại trang chủ
    setTimeout(() => {
      navigate("/");
    }, 3000);
  };

  const handleCancelDelete = () => {
    setIsModalOpen(false);
  };

  return (
    <>
      <motion.div
        className="bg-red-900 bg-opacity-50 backdrop-filter backdrop-blur-lg rounded-lg shadow-lg p-6 border border-red-700 mb-3"
        initial={{ opacity: 0, y: 25 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.6, duration: 0.5 }}
      >
        <div className="flex items-center mb-4">
          <Trash2 className="text-red-400 mr-3" size={24} />
          <h2 className="text-xl font-semibold text-gray-100">
            Khu vực nguy hiểm
          </h2>
        </div>

        <p className="text-gray-300 mb-4">
          Việc xóa tài khoản sẽ **vĩnh viễn** xóa toàn bộ dữ liệu và nội dung
          liên quan. Hãy xác nhận nếu bạn thực sự muốn tiếp tục — hành động này
          không thể hoàn tác.
        </p>
        <button
          className="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded transition duration-300"
          onClick={handleDeleteClick}
        >
          Xóa tài khoản
        </button>

        {/* Hộp thoại xác nhận */}
        {isModalOpen && (
          <motion.div
            className="fixed inset-0 bg-gray-800 bg-opacity-30 flex items-center justify-center z-50"
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            exit={{ scale: 0, duration: 0.5 }}
          >
            <div className="bg-gray-800 p-6 rounded-lg shadow-lg max-w-sm w-full text-center">
              <h3 className="text-lg font-semibold text-red-200 mb-4">
                Bạn có chắc chắn muốn xóa tài khoản không?
              </h3>
              <p className="text-red-300 mb-6">
                Hành động này **không thể hoàn tác** và sẽ xóa vĩnh viễn toàn bộ
                dữ liệu của bạn.
              </p>
              <div className="flex justify-center space-x-4">
                <button
                  className="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded"
                  onClick={handleConfirmDelete}
                >
                  Có, xóa ngay
                </button>
                <button
                  className="bg-gray-300 hover:bg-gray-400 text-red-800 font-semibold py-2 px-4 rounded"
                  onClick={handleCancelDelete}
                >
                  Hủy
                </button>
              </div>
            </div>
          </motion.div>
        )}
      </motion.div>

      {/* Hiển thị Toast */}
      <ToastContainer />
    </>
  );
};

export default DangerZone;
