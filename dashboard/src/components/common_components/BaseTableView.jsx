import React, { useState } from "react";
import { ArrowUpDown, X, CheckCircle } from "lucide-react";

const BaseTableView = ({
  data = [],
  columns = [],
  onDelete,
  entityLabel = "mục",
}) => {
  const [selectedIds, setSelectedIds] = useState([]);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });

  // --- SELECT ---
  const handleToggle = (id) =>
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  const handleSelectAll = () =>
    setSelectedIds(
      selectedIds.length === data.length ? [] : data.map((item) => item.id)
    );

  // --- SORT ---
  const handleSort = (key) => {
    setSortConfig((prev) =>
      prev.key === key && prev.direction === "asc"
        ? { key, direction: "desc" }
        : { key, direction: "asc" }
    );
  };

  const sortedData = [...data].sort((a, b) => {
    if (!sortConfig.key) return 0;
    let aVal = a[sortConfig.key];
    let bVal = b[sortConfig.key];
    if (aVal == null || bVal == null) return 0;

    if (typeof aVal === "string") {
      aVal = aVal.toLowerCase();
      bVal = bVal.toLowerCase();
    }

    if (aVal < bVal) return sortConfig.direction === "asc" ? -1 : 1;
    if (aVal > bVal) return sortConfig.direction === "asc" ? 1 : -1;
    return 0;
  });

  const handleDeleteSelected = () => {
    if (
      selectedIds.length > 0 &&
      window.confirm(`Xóa ${selectedIds.length} ${entityLabel} đã chọn?`)
    ) {
      selectedIds.forEach((id) => onDelete(id));
      setSelectedIds([]);
    }
  };

  const renderSortIcon = (key) => (
    <ArrowUpDown
      size={14}
      className={`ml-1 ${
        sortConfig.key === key
          ? "text-orange-500"
          : "text-gray-400 group-hover:text-gray-600"
      }`}
    />
  );

  return (
    <div className="overflow-x-auto">
      {/* Bulk action bar */}
      {selectedIds.length > 0 && (
        <div className="flex items-center justify-between bg-orange-50 border-b border-orange-200 px-4 py-2">
          <div className="flex items-center gap-2 text-orange-700 text-sm font-medium">
            <CheckCircle size={16} />
            Đã chọn {selectedIds.length} {entityLabel}
          </div>
          <button
            onClick={handleDeleteSelected}
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
      )}

      {/* Table */}
      <table className="w-full">
        <thead className="bg-gray-50 border-b border-gray-200">
          <tr className="text-xs text-gray-600 select-none">
            <th className="py-3 px-4 text-left">
              <input
                type="checkbox"
                checked={selectedIds.length === data.length && data.length > 0}
                onChange={handleSelectAll}
                className="w-4 h-4 accent-gray-700 cursor-pointer"
              />
            </th>
            {columns.map((col) => (
              <th
                key={col.key}
                className="text-left py-3 px-4 font-medium cursor-pointer group"
                onClick={() => handleSort(col.key)}
              >
                <div className="flex items-center gap-2">
                  {col.icon && <col.icon size={14} />}
                  {col.label} {renderSortIcon(col.key)}
                </div>
              </th>
            ))}
          </tr>
        </thead>

        <tbody className="divide-y divide-gray-100">
          {sortedData.map((item) => (
            <tr
              key={item.id}
              className={`hover:bg-gray-50 ${
                selectedIds.includes(item.id) ? "bg-gray-100" : ""
              }`}
            >
              <td className="py-3 px-4">
                <input
                  type="checkbox"
                  checked={selectedIds.includes(item.id)}
                  onChange={() => handleToggle(item.id)}
                  className="w-4 h-4 accent-orange-500 cursor-pointer"
                />
              </td>
              {columns.map((col) => (
                <td key={col.key} className="py-3 px-4 text-sm text-gray-700">
                  {col.render ? col.render(item[col.key], item) : item[col.key]}
                </td>
              ))}
            </tr>
          ))}

          {data.length === 0 && (
            <tr>
              <td
                colSpan={columns.length + 1}
                className="text-center py-6 text-gray-500 text-sm"
              >
                Không có {entityLabel}.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default BaseTableView;
