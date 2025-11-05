import React, { useState } from "react";
import SettingSection from "./SettingSection";
import { User, X } from "lucide-react";
import { motion } from "framer-motion";
import Image from "/src/assests/Mudassar Nazir.png";

const Profile = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [name, setName] = useState("Mudassar Nazir");
  const [email, setEmail] = useState("mudassarnazir137@gmail.com");
  const [tempName, setTempName] = useState(name);
  const [tempEmail, setTempEmail] = useState(email);

  // Mở modal
  const openModal = () => {
    setTempName(name);
    setTempEmail(email);
    setIsModalOpen(true);
  };
  // Đóng modal
  const closeModal = () => setIsModalOpen(false);

  // Lưu thay đổi
  const handleSave = () => {
    setName(tempName);
    setEmail(tempEmail);
    closeModal();
  };

  return (
    <SettingSection icon={User} title="Hồ sơ cá nhân">
      <div className="flex flex-col sm:flex-row items-center mb-6">
        <img
          src={Image}
          alt="Ảnh đại diện"
          className="rounded-full size-28 object-cover mr-4 mb-4 sm:mb-0"
        />
        <div>
          <h3 className="text-xl font-semibold text-black mb-1">{name}</h3>
          <p className="text-gray-900">{email}</p>
        </div>
      </div>

      <button
        onClick={openModal}
        className="bg-indigo-600 hover:bg-indigo-800 text-black font-semibold py-2 px-6 rounded transition duration-300 w-full sm:w-auto"
      >
        Chỉnh sửa hồ sơ
      </button>

      {/* Hộp thoại chỉnh sửa hồ sơ */}
      {isModalOpen && (
        <motion.div
          className="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50"
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          exit={{ scale: 0, duration: 0.5 }}
        >
          <div className="bg-gray-800 p-6 rounded shadow-lg w-11/12 sm:w-2/4 relative">
            <button
              onClick={closeModal}
              className="text-gray-300 hover:text-gray-100 absolute top-4 right-4"
            >
              <X />
            </button>
            <h2 className="text-xl font-semibold text-gray-100 mb-4">
              Chỉnh sửa hồ sơ
            </h2>
            <input
              type="text"
              value={tempName}
              onChange={(e) => setTempName(e.target.value)}
              className="w-full mb-3 p-2 rounded bg-gray-700 text-white outline-none"
              placeholder="Nhập tên"
            />
            <input
              type="email"
              value={tempEmail}
              onChange={(e) => setTempEmail(e.target.value)}
              className="w-full mb-3 p-2 rounded bg-gray-700 text-white outline-none"
              placeholder="Nhập email"
            />
            <button
              onClick={handleSave}
              className="bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-4 rounded transition duration-300 w-full"
            >
              Lưu thay đổi
            </button>
          </div>
        </motion.div>
      )}
    </SettingSection>
  );
};

export default Profile;
