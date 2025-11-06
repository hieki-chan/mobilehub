import React, { useState } from "react";
import { motion } from "framer-motion";
import { ArrowUp, ArrowDown, CheckCircle, X, Edit, Trash2, Eye } from "lucide-react";

const GenericTableView = ({ data = [], columns = [], entityName = "", onEdit, onView, onDelete }) => {
  const [selectedIds, setSelectedIds] = useState([]);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });

  // === SELECT ===
  const handleToggle = (id) =>
    setSelectedIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));

  const handleSelectAll = () => {
    if (selectedIds.length === data.length) setSelectedIds([]);
    else setSelectedIds(data.map((item) => item.id));
  };

  // === SORT ===
  const handleSort = (key) => {
    setSortConfig((prev) => ({
      key,
      direction: prev.key === key && prev.direction === "asc" ? "desc" : "asc",
    }));
  };

  const sortedData = [...data].sort((a, b) => {
    if (!sortConfig.key) return 0;
    let aVal = a[sortConfig.key], bVal = b[sortConfig.key];
    if (typeof aVal === "string") aVal = aVal.toLowerCase();
    if (typeof bVal === "string") bVal = bVal.toLowerCase();
    if (aVal < bVal) return sortConfig.direction === "asc" ? -1 : 1;
    if (aVal > bVal) return sortConfig.direction === "asc" ? 1 : -1;
    return 0;
  });

  const renderSortIcon = (key) => {
    if (sortConfig.key !== key) return null;
    const Icon = sortConfig.direction === "asc" ? ArrowUp : ArrowDown;
    return (
      <motion.div
        key={`${key}-${sortConfig.direction}`}
        initial={{ rotate: 0 }}
        animate={{ rotate: 180 }}
        transition={{ duration: 0.1 }}
        className="ml-1 inline-block"
      >
        <Icon size={14} className="text-orange-500" />
      </motion.div>
    );
  };

  return (
    <div className="overflow-x-auto">
      {selectedIds.length > 0 && (
        <div className="flex items-center justify-between bg-orange-50 border-b border-orange-200 px-4 py-2">
          <div className="flex items-center gap-2 text-orange-700 text-sm font-medium">
            <CheckCircle size={16} />
            Đã chọn {selectedIds.length} {entityName}
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => {
                if (window.confirm(`Xóa ${selectedIds.length} ${entityName} đã chọn?`)) {
                  selectedIds.forEach((id) => onDelete(id));
                  setSelectedIds([]);
                }
              }}
              className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600"
            >
              Xóa tất cả
            </button>
            <button
              onClick={() => setSelectedIds([])}
              className="p-1 text-gray-500 hover:bg-gray-100 rounded"
              title="Bỏ chọn"
            >
              <X size={16} />
            </button>
          </div>
        </div>
      )}

      <table className="w-full">
        <thead className="bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-700 select-none">
          <tr>
            <th className="py-3 px-4 text-left">
              <input
                type="checkbox"
                checked={selectedIds.length === data.length && data.length > 0}
                onChange={handleSelectAll}
                className="w-4 h-4 accent-gray-700 cursor-pointer"
              />
            </th>
            {columns.map(({ key, label, icon: Icon }) => (
              <th
                key={key}
                onClick={() => handleSort(key)}
                className="text-left py-3 px-4 cursor-pointer hover:bg-gray-100 transition"
              >
                <div className="flex items-center gap-2">
                  {Icon && <Icon size={15} className="text-black" />}
                  {label}
                  {renderSortIcon(key)}
                </div>
              </th>
            ))}
            <th className="py-3 px-4 text-left font-medium">Hành động</th>
          </tr>
        </thead>

        <tbody className="divide-y divide-gray-100">
          {sortedData.map((item) => (
            <tr
              key={item.id}
              className={`hover:bg-gray-50 ${selectedIds.includes(item.id) ? "bg-gray-100" : ""}`}
            >
              <td className="py-3 px-4">
                <input
                  type="checkbox"
                  checked={selectedIds.includes(item.id)}
                  onChange={() => handleToggle(item.id)}
                  className="w-4 h-4 accent-orange-500 cursor-pointer"
                />
              </td>

              {columns.map(({ key, render }) => (
                <td key={key} className="py-3 px-4 text-sm text-gray-700">
                  {render ? render(item) : item[key]}
                </td>
              ))}

              <td className="py-3 px-4">
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => onEdit(item)}
                    className="p-1 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded"
                    title="Chỉnh sửa"
                  >
                    <Edit size={16} />
                  </button>
                  
                  <button
                    onClick={() => onView && onView(item)}
                    className="p-1 text-gray-500 hover:text-indigo-600 hover:bg-indigo-50 rounded"
                    title="Xem chi tiết"
                  >
                    <Eye size={16} />
                  </button>
                    
                  <button
                    onClick={() => onDelete(item.id)}
                    className="p-1 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded"
                    title="Xóa"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </td>
            </tr>
          ))}

          {data.length === 0 && (
            <tr>
              <td colSpan={columns.length + 2} className="text-center py-6 text-gray-500 text-sm">
                Không có {entityName} nào.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default GenericTableView;
