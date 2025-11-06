import React, { useState, useEffect } from "react";
import { Edit, Trash2, ChevronDown, CheckCircle, X } from "lucide-react";

const ProductGridView = ({ products = [], onDelete, onEdit }) => {
  const [selectedIds, setSelectedIds] = useState([]);
  const [sortKey, setSortKey] = useState("default");
  const [sortDir, setSortDir] = useState("asc");
  const [sortedProducts, setSortedProducts] = useState([]);

  // === Sắp xếp ===
  useEffect(() => {
    let sorted = [...products];
    if (sortKey !== "default") {
      sorted.sort((a, b) => {
        let aVal = a[sortKey];
        let bVal = b[sortKey];
        if (typeof aVal === "string") aVal = aVal.toLowerCase();
        if (typeof bVal === "string") bVal = bVal.toLowerCase();

        if (aVal < bVal) return sortDir === "asc" ? -1 : 1;
        if (aVal > bVal) return sortDir === "asc" ? 1 : -1;
        return 0;
      });
    }
    setSortedProducts(sorted);
  }, [products, sortKey, sortDir]);

  // === Chọn tất cả ===
  const handleSelectAll = () => {
    if (selectedIds.length === sortedProducts.length) setSelectedIds([]);
    else setSelectedIds(sortedProducts.map((p) => p.id));
  };

  const handleToggleSelect = (id) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  // === Xóa hàng loạt ===
  const handleBulkDelete = () => {
    if (selectedIds.length === 0) return;
    if (window.confirm(`Xóa ${selectedIds.length} sản phẩm đã chọn?`)) {
      selectedIds.forEach((id) => onDelete(id));
      setSelectedIds([]);
    }
  };

  const handleClearSelection = () => setSelectedIds([]);

  return (
    <div className="p-4">
      {/* 🧡 Bulk Action Bar */}
      {selectedIds.length > 0 && (
        <div className="flex items-center justify-between bg-orange-50 border-b border-orange-200 px-4 py-2 mb-4 rounded-md">
          <div className="flex items-center gap-2 text-orange-700 text-sm font-medium">
            <CheckCircle size={16} />
            Đã chọn {selectedIds.length} sản phẩm
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handleBulkDelete}
              className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600 transition"
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

      {/* 🧭 Thanh điều khiển (Sort + Select All) */}
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3 mb-4">
        {/* Chọn tất cả */}
        <div className="flex items-center gap-2">
          <input
            type="checkbox"
            checked={
              sortedProducts.length > 0 &&
              selectedIds.length === sortedProducts.length
            }
            onChange={handleSelectAll}
            className="w-4 h-4 accent-orange-500 cursor-pointer"
          />
          <span className="text-sm text-gray-700">
            {selectedIds.length > 0
              ? `Đã chọn ${selectedIds.length}`
              : "Chọn tất cả"}
          </span>
        </div>

        {/* Sort dropdowns */}
        <div className="flex flex-wrap items-center gap-2">
          <div className="relative">
            <select
              value={sortKey}
              onChange={(e) => setSortKey(e.target.value)}
              className="appearance-none pl-3 pr-8 py-2 border border-gray-300 rounded-md text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-orange-500"
            >
              <option value="default">Sắp xếp theo...</option>
              <option value="name">Tên sản phẩm</option>
              <option value="price">Giá</option>
              <option value="stock">Tồn kho</option>
              <option value="category">Danh mục</option>
            </select>
            <ChevronDown
              size={16}
              className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 pointer-events-none"
            />
          </div>

          <div className="relative">
            <select
              value={sortDir}
              onChange={(e) => setSortDir(e.target.value)}
              className="appearance-none pl-3 pr-8 py-2 border border-gray-300 rounded-md text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-orange-500"
            >
              <option value="asc">Tăng dần</option>
              <option value="desc">Giảm dần</option>
            </select>
            <ChevronDown
              size={16}
              className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 pointer-events-none"
            />
          </div>
        </div>
      </div>

      {/* 🧱 Grid sản phẩm */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {sortedProducts.map((p) => (
          <div
            key={p.id}
            className={`relative bg-white border rounded-lg p-4 shadow-sm hover:shadow-md transition ${
              selectedIds.includes(p.id)
                ? "border-orange-400 ring-2 ring-orange-200"
                : "border-gray-200"
            }`}
          >
            {/* Checkbox overlay trên ảnh */}
            <div className="relative mb-3">
              <img
                src={p.imageUrl}
                alt={p.name}
                className="w-full h-48 object-cover rounded-md"
              />
              <input
                type="checkbox"
                checked={selectedIds.includes(p.id)}
                onChange={() => handleToggleSelect(p.id)}
                className="absolute top-2 left-2 w-4 h-4 accent-orange-500 bg-white/90 rounded shadow-sm cursor-pointer hover:scale-110 transition-transform"
              />
            </div>

            <h3 className="text-sm font-semibold text-gray-900">{p.name}</h3>
            <p className="text-xs text-gray-500 mb-1">{p.category}</p>
            <p className="text-orange-600 font-bold text-sm mb-1">
              {p.price.toLocaleString("vi-VN")}₫
            </p>
            <p className="text-xs text-gray-500 mb-2">Kho: {p.stock}</p>

            <span
              className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${
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

            <div className="flex justify-end gap-2 mt-3">
              <button
                onClick={() => onEdit(p)}
                className="p-1.5 text-gray-400 hover:text-orange-500 hover:bg-orange-50 rounded"
              >
                <Edit size={16} />
              </button>
              <button
                onClick={() => onDelete(p.id)}
                className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded"
              >
                <Trash2 size={16} />
              </button>
            </div>
          </div>
        ))}

        {sortedProducts.length === 0 && (
          <div className="col-span-full text-center text-gray-500 text-sm py-8">
            Không có sản phẩm nào.
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductGridView;
