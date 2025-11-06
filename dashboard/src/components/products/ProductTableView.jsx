import React, { useState } from "react";
import {
  Edit,
  Trash2,
  ArrowUp,
  ArrowDown,
  CheckCircle,
  X,
  Package,
  Layers,
  Tag,
  Warehouse,
  Percent,
} from "lucide-react";
import { motion } from "framer-motion";

const ProductTableView = ({ products = [], onDelete, onEdit }) => {
  const [selectedIds, setSelectedIds] = useState([]);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });

  // === SELECT ===
  const handleToggle = (id) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const handleSelectAll = () => {
    if (selectedIds.length === products.length) setSelectedIds([]);
    else setSelectedIds(products.map((p) => p.id));
  };

  // === SORT ===
  const handleSort = (key) => {
    setSortConfig((prev) => {
      if (prev.key === key && prev.direction === "asc")
        return { key, direction: "desc" };
      else return { key, direction: "asc" };
    });
  };

  const sortedProducts = [...products].sort((a, b) => {
    if (!sortConfig.key) return 0;
    let aVal = a[sortConfig.key];
    let bVal = b[sortConfig.key];

    if (typeof aVal === "string") aVal = aVal.toLowerCase();
    if (typeof bVal === "string") bVal = bVal.toLowerCase();

    if (aVal < bVal) return sortConfig.direction === "asc" ? -1 : 1;
    if (aVal > bVal) return sortConfig.direction === "asc" ? 1 : -1;
    return 0;
  });

  const renderSortIcon = (key) => {
    const isActive = sortConfig.key === key;
    if (!isActive) return null;
    const Icon = sortConfig.direction === "asc" ? ArrowUp : ArrowDown;
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
      window.confirm(`Xóa ${selectedIds.length} sản phẩm đã chọn?`)
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
            Đã chọn {selectedIds.length} sản phẩm
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
        <thead className="bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-700 select-none">
          <tr>
            <th className="py-3 px-4 text-left">
              <input
                type="checkbox"
                checked={selectedIds.length === products.length && products.length > 0}
                onChange={handleSelectAll}
                className="w-4 h-4 accent-gray-700 cursor-pointer"
              />
            </th>

            {[
              { key: "id", label: "Mã" },
              { key: "name", label: "Tên sản phẩm", icon: Package },
              { key: "category", label: "Danh mục", icon: Layers },
              { key: "price", label: "Giá tiền", icon: Tag },
              { key: "stock", label: "Kho", icon: Warehouse },
              { key: "sales", label: "KM (%)", icon: Percent },
              { key: "status", label: "Trạng thái" },
            ].map(({ key, label, icon: Icon }) => (
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
          {sortedProducts.map((p) => (
            <tr
              key={p.id}
              className={`hover:bg-gray-50 transition ${
                selectedIds.includes(p.id) ? "bg-gray-100" : ""
              }`}
            >
              <td className="py-3 px-4">
                <input
                  type="checkbox"
                  checked={selectedIds.includes(p.id)}
                  onChange={() => handleToggle(p.id)}
                  className="w-4 h-4 accent-orange-500 cursor-pointer"
                />
              </td>

              <td className="py-3 px-4 text-sm text-gray-700">{p.id}</td>

              <td className="py-3 px-4">
                <div className="flex items-center gap-3">
                  <img
                    src={p.imageUrl}
                    alt={p.name}
                    className="w-10 h-10 rounded-md object-cover"
                  />
                  <span className="text-sm font-medium text-gray-900">
                    {p.name}
                  </span>
                </div>
              </td>

              <td className="py-3 px-4 text-sm text-gray-700">{p.category}</td>
              <td className="py-3 px-4 text-sm text-blue-600 font-semibold">
                {p.price.toLocaleString("vi-VN")}₫
              </td>
              <td className="py-3 px-4 text-sm text-gray-700">{p.stock}</td>
              <td className="py-3 px-4 text-sm text-gray-700">{p.sales}</td>

              <td className="py-3 px-4">
                <span
                  className={`inline-flex items-center gap-1.5 px-2 py-1 rounded-full text-xs font-medium ${
                    p.status === "ACTIVE"
                      ? "bg-green-100 text-green-700"
                      : "bg-gray-100 text-gray-600"
                  }`}
                >
                  <div
                    className={`w-1.5 h-1.5 rounded-full ${
                      p.status === "ACTIVE" ? "bg-green-500" : "bg-gray-400"
                    }`}
                  ></div>
                  {p.status === "ACTIVE" ? "Hoạt động" : "Ngừng"}
                </span>
              </td>

              <td className="py-3 px-4">
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => onEdit(p)}
                    className="p-1 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded"
                  >
                    <Edit size={16} />
                  </button>
                  <button
                    onClick={() => onDelete(p.id)}
                    className="p-1 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </td>
            </tr>
          ))}

          {products.length === 0 && (
            <tr>
              <td
                colSpan="8"
                className="text-center py-6 text-gray-500 text-sm"
              >
                Không có sản phẩm nào.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ProductTableView;
