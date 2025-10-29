import React, { useState } from "react";
import { motion } from "framer-motion";
import {
  ChevronLeft,
  ChevronRight,
  Edit,
  Search,
  Trash2,
  UserPlus,
  X,
} from "lucide-react";

const DuLieuNguoiDung = [
  {
    id: 1,
    name: "Mudassar",
    email: "mudassar@gmail.com",
    role: "Quản trị viên",
    status: "Hoạt động",
  },
  {
    id: 2,
    name: "Ustad g",
    email: "john.smith@gmail.com",
    role: "Khách hàng",
    status: "Hoạt động",
  },
  {
    id: 3,
    name: "Wahab",
    email: "wahab.noor@gmail.com",
    role: "Khách hàng",
    status: "Ngừng hoạt động",
  },
  {
    id: 4,
    name: "Danish",
    email: "danish.joe@gmail.com",
    role: "Điều hành viên",
    status: "Hoạt động",
  },
  {
    id: 5,
    name: "Usama",
    email: "usama.glasses@gmail.com",
    role: "Khách hàng",
    status: "Hoạt động",
  },
  {
    id: 6,
    name: "Ayesha",
    email: "ayesha.khan@gmail.com",
    role: "Quản trị viên",
    status: "Ngừng hoạt động",
  },
  {
    id: 7,
    name: "Hassan",
    email: "hassan.ali@gmail.com",
    role: "Khách hàng",
    status: "Hoạt động",
  },
  {
    id: 8,
    name: "Sarah",
    email: "sarah.jones@gmail.com",
    role: "Quản trị viên",
    status: "Hoạt động",
  },
  {
    id: 9,
    name: "Ali",
    email: "ali.baba@gmail.com",
    role: "Khách hàng",
    status: "Ngừng hoạt động",
  },
  {
    id: 10,
    name: "Fahad",
    email: "fahad.king@gmail.com",
    role: "Điều hành viên",
    status: "Hoạt động",
  },
  {
    id: 11,
    name: "Zainab",
    email: "zainab.queen@gmail.com",
    role: "Khách hàng",
    status: "Hoạt động",
  },
  {
    id: 12,
    name: "Bilal",
    email: "bilal.smart@gmail.com",
    role: "Khách hàng",
    status: "Ngừng hoạt động",
  },
  {
    id: 13,
    name: "Rizwan",
    email: "rizwan.shah@gmail.com",
    role: "Điều hành viên",
    status: "Hoạt động",
  },
  {
    id: 14,
    name: "Kiran",
    email: "kiran.doe@gmail.com",
    role: "Khách hàng",
    status: "Ngừng hoạt động",
  },
  {
    id: 15,
    name: "Yasir",
    email: "yasir.ace@gmail.com",
    role: "Quản trị viên",
    status: "Hoạt động",
  },
];

const UsersPageTable = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredUsers, setFilteredUsers] = useState(DuLieuNguoiDung);
  const [isAddModalOpen, setAddModalOpen] = useState(false);
  const [newUser, setNewUser] = useState({
    name: "",
    email: "",
    role: "",
    status: "",
  });
  const [isEditModalOpen, setEditModalOpen] = useState(false);
  const [editUser, setEditUser] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const totalPages = Math.ceil(filteredUsers.length / itemsPerPage);

  // Tìm kiếm người dùng
  const handleSearch = (e) => {
    const term = e.target.value.toLowerCase();
    setSearchTerm(term);
    const filtered = DuLieuNguoiDung.filter(
      (user) =>
        user.name.toLowerCase().includes(term) ||
        user.email.toLowerCase().includes(term)
    );
    setFilteredUsers(filtered);
    setCurrentPage(1);
  };

  // Thêm người dùng
  const handleAdd = () => {
    const newId =
      filteredUsers.length > 0
        ? Math.max(...filteredUsers.map((u) => u.id)) + 1
        : 1;
    const userToAdd = { ...newUser, id: newId };
    setFilteredUsers([userToAdd, ...filteredUsers]);
    setAddModalOpen(false);
    setNewUser({ name: "", email: "", role: "", status: "" });
  };

  // Chỉnh sửa
  const handleEdit = (user) => {
    setEditUser(user);
    setEditModalOpen(true);
  };

  // Xóa
  const handleDelete = (id) => {
    const updated = filteredUsers.filter((u) => u.id !== id);
    setFilteredUsers(updated);
  };

  // Lưu chỉnh sửa
  const handleSave = () => {
    const updated = filteredUsers.map((u) =>
      u.id === editUser.id ? editUser : u
    );
    setFilteredUsers(updated);
    setEditModalOpen(false);
  };

  const paginate = (page) => setCurrentPage(page);
  const getCurrentPageUsers = () => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredUsers.slice(start, start + itemsPerPage);
  };

  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 border border-gray-700 mb-6 relative z-10"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3, delay: 0.3 }}
    >
      {/* Tiêu đề và tìm kiếm */}
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold text-gray-100">
          Danh sách người dùng
        </h2>
        <div className="relative flex items-center">
          <Search
            className="absolute left-3 text-gray-400 sm:left-2.5 top-2.5"
            size={20}
          />
          <input
            type="text"
            placeholder="Tìm kiếm người dùng..."
            className="bg-gray-700 text-white placeholder-gray-400 rounded-lg pl-10 pr-4 py-2 w-full sm:w-auto focus:outline-none focus:ring-2 focus:ring-blue-500"
            onChange={handleSearch}
            value={searchTerm}
          />
        </div>
      </div>

      {/* Bảng */}
      <div className="overflow-x-auto" style={{ minHeight: "400px" }}>
        <table className="min-w-full divide-y divide-gray-400">
          <thead>
            <tr>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Tên
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Email
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Vai trò
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Trạng thái
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Hành động
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-500">
            {getCurrentPageUsers().map((user) => (
              <motion.tr
                key={user.id}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 1 }}
              >
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="flex items-center">
                    <div className="h-10 w-10 rounded-full bg-gradient-to-r from-purple-400 to-blue-500 flex items-center justify-center text-white font-semibold">
                      {user.name.charAt(0)}
                    </div>
                    <div className="ml-4 text-sm font-semibold text-gray-100">
                      {user.name}
                    </div>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-gray-300">
                  {user.email}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className="px-3 py-1 rounded-full text-xs bg-gray-200 text-gray-800 font-semibold">
                    {user.role}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span
                    className={`px-3 py-1 rounded-full text-xs font-semibold 
                                        ${
                                          user.status === "Hoạt động"
                                            ? "bg-green-700 text-green-100"
                                            : "bg-red-700 text-red-100"
                                        }`}
                  >
                    {user.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <button
                    onClick={() => setAddModalOpen(true)}
                    className="text-green-500 hover:text-green-600 mr-2"
                  >
                    <UserPlus size={18} />
                  </button>
                  <button
                    onClick={() => handleEdit(user)}
                    className="text-indigo-400 hover:text-indigo-300 mr-2"
                  >
                    <Edit size={18} />
                  </button>
                  <button
                    onClick={() => handleDelete(user.id)}
                    className="text-red-400 hover:text-red-300"
                  >
                    <Trash2 size={18} />
                  </button>
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* PHÂN TRANG */}
      <div className="flex flex-col md:flex-row justify-between mt-4 space-x-2 items-center">
        <div className="flex items-center">
          <button
            onClick={() => paginate(currentPage - 1)}
            disabled={currentPage === 1}
            className={`text-sm px-3 py-1 border rounded-md ${
              currentPage === 1
                ? "text-gray-400 border-gray-600"
                : "text-gray-100 border-gray-300 hover:bg-gray-300 hover:text-gray-800"
            }`}
          >
            <ChevronLeft size={18} />
          </button>
          <span className="mx-2 text-sm font-medium text-gray-100">
            Trang {currentPage} / {totalPages}
          </span>
          <button
            onClick={() => paginate(currentPage + 1)}
            disabled={currentPage === totalPages}
            className={`text-sm px-3 py-1 border rounded-md ${
              currentPage === totalPages
                ? "text-gray-400 border-gray-600"
                : "text-gray-100 border-gray-300 hover:bg-gray-300 hover:text-gray-800"
            }`}
          >
            <ChevronRight size={18} />
          </button>
        </div>

        <div className="text-sm font-medium text-gray-300 mt-5 md:mt-0">
          Tổng số người dùng: {filteredUsers.length}
        </div>
      </div>

      {/* Popup chỉnh sửa */}
      {isEditModalOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <motion.div
            className="bg-gray-800 rounded-lg shadow-2xl p-6 w-full max-w-xl"
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
          >
            <h1 className="text-2xl font-semibold text-gray-100 mb-3 underline tracking-wider">
              Chỉnh sửa người dùng
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col space-y-1">
                <label className="text-sm text-gray-300">Tên người dùng</label>
                <input
                  type="text"
                  value={editUser.name}
                  onChange={(e) =>
                    setEditUser({ ...editUser, name: e.target.value })
                  }
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded-md"
                />
              </div>

              <div className="flex flex-col space-y-1">
                <label className="text-sm text-gray-300">Email</label>
                <input
                  type="email"
                  value={editUser.email}
                  onChange={(e) =>
                    setEditUser({ ...editUser, email: e.target.value })
                  }
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded-md"
                />
              </div>

              <div className="flex flex-col space-y-1">
                <label className="text-sm text-gray-300">Vai trò</label>
                <input
                  type="text"
                  value={editUser.role}
                  onChange={(e) =>
                    setEditUser({ ...editUser, role: e.target.value })
                  }
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded-md"
                />
              </div>

              <div className="flex flex-col space-y-1">
                <label className="text-sm text-gray-300">Trạng thái</label>
                <input
                  type="text"
                  value={editUser.status}
                  onChange={(e) =>
                    setEditUser({ ...editUser, status: e.target.value })
                  }
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded-md"
                />
              </div>
            </div>

            <div className="flex justify-end mt-5 space-x-2">
              <button
                onClick={() => setEditModalOpen(false)}
                className="bg-gray-600 hover:bg-red-500 text-gray-100 px-4 py-2 rounded-md"
              >
                <X size={22} />
              </button>
              <button
                onClick={handleSave}
                className="bg-blue-600 hover:bg-blue-800 text-white px-4 py-2 rounded-md w-24"
              >
                Lưu
              </button>
            </div>
          </motion.div>
        </div>
      )}

      {/* Popup thêm người dùng */}
      {isAddModalOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <motion.div
            className="bg-gray-800 rounded-lg shadow-lg p-6 max-w-xl w-full"
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
          >
            <h1 className="text-2xl font-semibold text-gray-100 mb-6 underline tracking-wider">
              Thêm người dùng mới
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col space-y-1">
                <label className="text-sm text-gray-300">Tên người dùng</label>
                <input
                  type="text"
                  value={newUser.name}
                  onChange={(e) =>
                    setNewUser({ ...newUser, name: e.target.value })
                  }
                  placeholder="Nhập tên"
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded-md"
                />
              </div>

              <div className="flex flex-col space-y-1">
                <label className="text-sm text-gray-300">Email</label>
                <input
                  type="email"
                  value={newUser.email}
                  onChange={(e) =>
                    setNewUser({ ...newUser, email: e.target.value })
                  }
                  placeholder="Nhập email"
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded-md"
                />
              </div>

              <div className="flex flex-col space-y-1">
                <label className="text-sm text-gray-300">Vai trò</label>
                <input
                  type="text"
                  value={newUser.role}
                  onChange={(e) =>
                    setNewUser({ ...newUser, role: e.target.value })
                  }
                  placeholder="Nhập vai trò"
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded-md"
                />
              </div>

              <div className="flex flex-col space-y-1">
                <label className="text-sm text-gray-300">Trạng thái</label>
                <input
                  type="text"
                  value={newUser.status}
                  onChange={(e) =>
                    setNewUser({ ...newUser, status: e.target.value })
                  }
                  placeholder="Nhập trạng thái"
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded-md"
                />
              </div>
            </div>

            <div className="flex justify-end mt-6 space-x-2">
              <button
                onClick={() => setAddModalOpen(false)}
                className="bg-gray-600 hover:bg-red-500 text-gray-100 px-4 py-2 rounded-md"
              >
                <X size={22} />
              </button>
              <button
                onClick={handleAdd}
                className="bg-blue-600 hover:bg-blue-800 text-white px-4 py-3 rounded-md w-28"
              >
                Thêm
              </button>
            </div>
          </motion.div>
        </div>
      )}
    </motion.div>
  );
};

export default UsersPageTable;
