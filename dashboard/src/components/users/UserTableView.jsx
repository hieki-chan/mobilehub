import React, { useState } from "react";
import {
  UserPlus,
  Edit,
  Trash2,
  ArrowUp,
  ArrowDown,
  X,
  CheckCircle,
  Calendar,
  Mail,
  UserCircle,
} from "lucide-react";
import { motion } from "framer-motion";


const UserTableView = ({ users = [], onDelete }) => {
  const [selectedIds, setSelectedIds] = useState([]);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });

  // === SELECT ===
  const handleToggle = (id) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const handleSelectAll = () => {
    if (selectedIds.length === users.length) setSelectedIds([]);
    else setSelectedIds(users.map((u) => u.id));
  };

  // === SORT ===
  const handleSort = (key) => {
    setSortConfig((prev) => {
      if (prev.key === key && prev.direction === "asc")
        return { key, direction: "desc" };
      else return { key, direction: "asc" };
    });
  };

  const sortedUsers = [...users].sort((a, b) => {
    if (!sortConfig.key) return 0;
    let aVal = a[sortConfig.key];
    let bVal = b[sortConfig.key];

    if (sortConfig.key === "createdDate") {
      aVal = new Date(aVal);
      bVal = new Date(bVal);
    } else {
      aVal = aVal.toString().toLowerCase();
      bVal = bVal.toString().toLowerCase();
    }

    if (aVal < bVal) return sortConfig.direction === "asc" ? -1 : 1;
    if (aVal > bVal) return sortConfig.direction === "asc" ? 1 : -1;
    return 0;
  });

  const renderSortIcon = (key) => {
    const isActive = sortConfig.key === key;
    if (!isActive) return null;

    const isAsc = sortConfig.direction === "asc";
    const Icon = isAsc ? ArrowUp : ArrowDown;

    return (
      <motion.div
        key={`${key}-${sortConfig.direction}`}
        initial={{ rotate: 0 }}
        animate={{ rotate: 180 }}
        transition={{ duration: 0.1, ease: "easeInOut" }}
        className="ml-1 inline-block"
      >
        <Icon size={14} className="text-orange-500" />
      </motion.div>
    );
  };

  // === BULK DELETE ===
  const handleDeleteSelected = () => {
    if (
      selectedIds.length > 0 &&
      window.confirm(`Xóa ${selectedIds.length} người dùng đã chọn?`)
    ) {
      selectedIds.forEach((id) => onDelete(id));
      setSelectedIds([]);
    }
  };

  const handleClearSelection = () => setSelectedIds([]);

  return (
    <div className="overflow-x-auto">
      {/* 🧡 Bulk Action Bar */}
      {selectedIds.length > 0 && (
        <div className="flex items-center justify-between bg-orange-50 border-b border-orange-200 px-4 py-2">
          <div className="flex items-center gap-2 text-orange-700 text-sm font-medium">
            <CheckCircle size={16} />
            Đã chọn {selectedIds.length} người dùng
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handleDeleteSelected}
              className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600"
            >
              Xóa tất cả
            </button>
            <button
              onClick={handleClearSelection}
              className="p-1 text-gray-500 hover:bg-gray-100 rounded"
              title="Bỏ chọn"
            >
              <X size={16} />
            </button>
          </div>
        </div>
      )}

      {/* 🧩 Table */}
      <table className="w-full">
        <thead className="bg-gray-50 border-b border-gray-200">
          <tr className="text-xs text-gray-700 select-none font-semibold">
            <th className="py-3 px-4 text-left">
              <input
                type="checkbox"
                checked={selectedIds.length === users.length && users.length > 0}
                onChange={handleSelectAll}
                className="w-4 h-4 accent-gray-700 cursor-pointer"
              />
            </th>

            <th
              className="text-left py-3 px-4 cursor-pointer hover:bg-gray-100 transition"
              onClick={() => handleSort("id")}
            >
              <div className="flex items-center gap-2">
                Mã {renderSortIcon("id")}
              </div>
            </th>

            <th
              className="text-left py-3 px-4 cursor-pointer hover:bg-gray-100 transition"
              onClick={() => handleSort("name")}
            >
              <div className="flex items-center gap-2">
                <UserCircle size={16} className="text-black" />
                Tên {renderSortIcon("name")}
              </div>
            </th>

            <th
              className="text-left py-3 px-4 cursor-pointer hover:bg-gray-100 transition"
              onClick={() => handleSort("email")}
            >
              <div className="flex items-center gap-2">
                <Mail size={15} className="text-black" />
                Email {renderSortIcon("email")}
              </div>
            </th>

            <th
              className="text-left py-3 px-4 cursor-pointer hover:bg-gray-100 transition"
              onClick={() => handleSort("role")}
            >
              <div className="flex items-center gap-2">
                <UserPlus size={15} className="text-black" />
                Vai trò {renderSortIcon("role")}
              </div>
            </th>

            <th
              className="text-left py-3 px-4 cursor-pointer hover:bg-gray-100 transition"
              onClick={() => handleSort("status")}
            >
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-green-500"></div>
                Trạng thái {renderSortIcon("status")}
              </div>
            </th>

            <th
              className="text-left py-3 px-4 cursor-pointer hover:bg-gray-100 transition"
              onClick={() => handleSort("createdDate")}
            >
              <div className="flex items-center gap-2">
                <Calendar size={15} className="text-black" />
                Ngày tạo {renderSortIcon("createdDate")}
              </div>
            </th>

            <th className="text-left py-3 px-4 font-medium">Hành động</th>
          </tr>
        </thead>

        <tbody className="divide-y divide-gray-100">
          {sortedUsers.map((user) => (
            <tr
              key={user.id}
              className={`hover:bg-gray-50 transition ${selectedIds.includes(user.id) ? "bg-gray-100" : ""
                }`}
            >
              <td className="py-3 px-4">
                <input
                  type="checkbox"
                  checked={selectedIds.includes(user.id)}
                  onChange={() => handleToggle(user.id)}
                  className="w-4 h-4 accent-orange-500 cursor-pointer"
                />
              </td>

              <td className="py-3 px-4 text-sm text-gray-700">{user.id}</td>


              <td className="py-3 px-4">
                <div className="flex items-center gap-3">
                  <div
                    className={`w-8 h-8 rounded-full ${user.color} flex items-center justify-center text-white text-xs font-medium`}
                  >
                    {user.avatar}
                  </div>
                  <span className="text-sm font-medium text-gray-900">
                    {user.name}
                  </span>
                </div>
              </td>

              <td className="py-3 px-4">
                <a
                  href={`mailto:${user.email}`}
                  className="text-sm text-blue-600 hover:underline"
                >
                  {user.email}
                </a>
              </td>

              <td className="py-3 px-4 text-sm text-gray-700">{user.role}</td>

              <td className="py-3 px-4">
                <span
                  className={`inline-flex items-center gap-1.5 px-2 py-1 rounded-full text-xs font-medium ${user.status === "Active"
                    ? "bg-green-100 text-green-700"
                    : "bg-gray-100 text-gray-600"
                    }`}
                >
                  <div
                    className={`w-1.5 h-1.5 rounded-full ${user.status === "Active" ? "bg-green-500" : "bg-gray-400"
                      }`}
                  ></div>
                  {user.status}
                </span>
              </td>

              <td className="py-3 px-4 text-sm text-gray-600">
                {user.createdDate}
              </td>

              <td className="py-3 px-4">
                <div className="flex items-center gap-2">
                  <button className="p-1 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded">
                    <Edit size={16} />
                  </button>
                  <button
                    onClick={() => onDelete(user.id)}
                    className="p-1 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </td>
            </tr>
          ))}

          {users.length === 0 && (
            <tr>
              <td colSpan="7" className="text-center py-6 text-gray-500 text-sm">
                Không có người dùng nào.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default UserTableView;
