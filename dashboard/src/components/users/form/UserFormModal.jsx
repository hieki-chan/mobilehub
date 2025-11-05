import React, { useState, useEffect } from "react";
import { X, ChevronDown } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { createAdminUser, updateAdminUser } from "../../../api/UserApi";

const roles = ["ADMIN", "EMPLOYEE", "USER"];
const statuses = ["ACTIVE", "INACTIVE"];

const UserFormModal = ({ isOpen, onClose, mode = "create", initialData = {}, onSuccess }) => {
  const [form, setForm] = useState({
    email: "",
    name: "",
    password: "",
    role: "USER",
    status: "Active",
  });

  const [showRoleDropdown, setShowRoleDropdown] = useState(false);
  const [showStatusDropdown, setShowStatusDropdown] = useState(false);

  useEffect(() => {
    if (mode === "edit" && initialData) {
      setForm({
        email: initialData.email || "",
        name: initialData.name || "",
        password: "",
        role: initialData.role || "USER",
        status: initialData.status || "Active",
      });
    }
  }, [mode, initialData]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (mode === "edit") {
        await updateAdminUser(initialData.id, form);
        alert("✅ Cập nhật người dùng thành công!");
      } else {
        await createAdminUser(form);
        alert("✅ Tạo người dùng thành công!");
      }
      onSuccess?.(); // reload list
      onClose();
    } catch (error) {
      console.error("❌ Lỗi khi lưu user:", error);
      alert("Không thể lưu người dùng!");
    }
  };

  return (
    <div className="absolute inset-0 flex justify-center items-center z-50 pointer-events-none">
      <AnimatePresence>
        {isOpen && (
          <motion.div
            key="userForm"
            initial={{ opacity: 0, scale: 0.97, y: 6 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.97, y: 6 }}
            transition={{ duration: 0.1, ease: "easeOut" }}
            className="pointer-events-auto bg-white rounded-xl shadow-xl border border-gray-200 w-full max-w-md p-6 relative"
          >
            {/* ===== Header ===== */}
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-lg font-semibold text-gray-800">
                {mode === "edit" ? "Chỉnh sửa người dùng" : "Thêm người dùng"}
              </h2>
              <button
                onClick={onClose}
                className="text-gray-500 hover:text-gray-700 p-1 rounded-full"
              >
                <X size={20} />
              </button>
            </div>

            {/* ===== Form ===== */}
            <form onSubmit={handleSubmit} className="space-y-3">
              {/* Email */}
              <div>
                <label className="text-sm font-medium text-gray-700">Email</label>
                <input
                  type="email"
                  name="email"
                  value={form.email}
                  onChange={handleChange}
                  required
                  placeholder="Nhập email"
                  disabled={mode === "edit"}
                  className="w-full mt-1 border border-gray-300 rounded-md px-3 py-1.5 text-sm focus:ring-2 focus:ring-gray-900 focus:outline-none disabled:bg-gray-100"
                />
              </div>

              {/* Name */}
              <div>
                <label className="text-sm font-medium text-gray-700">Tên</label>
                <input
                  type="text"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  required
                  placeholder="Nhập tên"
                  className="w-full mt-1 border border-gray-300 rounded-md px-3 py-1.5 text-sm focus:ring-2 focus:ring-gray-900 focus:outline-none"
                />
              </div>

              {/* Password */}
              <div>
                <label className="text-sm font-medium text-gray-700">Mật khẩu</label>
                <input
                  type="password"
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  required={mode === "create"}
                  placeholder={mode === "edit" ? "Để trống nếu không đổi" : "Nhập mật khẩu"}
                  className="w-full mt-1 border border-gray-300 rounded-md px-3 py-1.5 text-sm focus:ring-2 focus:ring-gray-900 focus:outline-none"
                />
              </div>

              {/* Role Dropdown */}
              <div className="relative">
                <label className="text-sm font-medium text-gray-700">Vai trò</label>
                <button
                  type="button"
                  onClick={() => setShowRoleDropdown((prev) => !prev)}
                  className="w-full flex justify-between items-center mt-1 border border-gray-300 rounded-md px-3 py-1.5 text-sm text-gray-800 hover:border-gray-400 focus:ring-2 focus:ring-gray-900 focus:outline-none"
                >
                  {form.role}
                  <ChevronDown
                    size={16}
                    className={`transition-transform duration-100 ${
                      showRoleDropdown ? "rotate-180" : ""
                    }`}
                  />
                </button>

                <AnimatePresence>
                  {showRoleDropdown && (
                    <motion.ul
                      initial={{ opacity: 0, y: -3 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -3 }}
                      transition={{ duration: 0.08, ease: "easeOut" }}
                      className="absolute z-10 mt-1 w-full bg-white border border-gray-200 rounded-md shadow-md overflow-hidden"
                    >
                      {roles.map((r) => (
                        <li
                          key={r}
                          onClick={() => {
                            setForm((prev) => ({ ...prev, role: r }));
                            setShowRoleDropdown(false);
                          }}
                          className={`px-3 py-1.5 text-sm cursor-pointer hover:bg-gray-100 ${
                            form.role === r ? "bg-gray-100 font-medium" : ""
                          }`}
                        >
                          {r}
                        </li>
                      ))}
                    </motion.ul>
                  )}
                </AnimatePresence>
              </div>

              {/* Status Dropdown */}
              <div className="relative">
                <label className="text-sm font-medium text-gray-700">Trạng thái</label>
                <button
                  type="button"
                  onClick={() => setShowStatusDropdown((prev) => !prev)}
                  className="w-full flex justify-between items-center mt-1 border border-gray-300 rounded-md px-3 py-1.5 text-sm text-gray-800 hover:border-gray-400 focus:ring-2 focus:ring-gray-900 focus:outline-none"
                >
                  {form.status}
                  <ChevronDown
                    size={16}
                    className={`transition-transform duration-100 ${
                      showStatusDropdown ? "rotate-180" : ""
                    }`}
                  />
                </button>

                <AnimatePresence>
                  {showStatusDropdown && (
                    <motion.ul
                      initial={{ opacity: 0, y: -3 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -3 }}
                      transition={{ duration: 0.08, ease: "easeOut" }}
                      className="absolute z-10 mt-1 w-full bg-white border border-gray-200 rounded-md shadow-md overflow-hidden"
                    >
                      {statuses.map((s) => (
                        <li
                          key={s}
                          onClick={() => {
                            setForm((prev) => ({ ...prev, status: s }));
                            setShowStatusDropdown(false);
                          }}
                          className={`px-3 py-1.5 text-sm cursor-pointer hover:bg-gray-100 ${
                            form.status === s ? "bg-gray-100 font-medium" : ""
                          }`}
                        >
                          {s}
                        </li>
                      ))}
                    </motion.ul>
                  )}
                </AnimatePresence>
              </div>

              {/* Buttons */}
              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-3 py-1.5 text-sm rounded border border-gray-300 text-gray-700 hover:bg-gray-100"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-3 py-1.5 text-sm rounded bg-orange-500 text-white hover:bg-orange-600"
                >
                  {mode === "edit" ? "Cập nhật" : "Lưu"}
                </button>
              </div>
            </form>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default UserFormModal;
