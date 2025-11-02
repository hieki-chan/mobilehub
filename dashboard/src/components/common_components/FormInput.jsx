// src/components/common_components/FormInputs.jsx
import React from "react";

/**
 * Input component — hỗ trợ nested key (vd: spec.os, discount.startDate)
 * và tự động xử lý format ngày (YYYY-MM-DD)
 */
export const Input = ({
  label,
  keyName,
  type = "text",
  newProduct,
  setNewProduct,
}) => {
  // === Helper: lấy giá trị từ nested object theo dot-notation ===
  const getValue = (obj, path) =>
    path.split(".").reduce((acc, key) => acc?.[key], obj) ?? "";

  // === Helper: cập nhật nested value ===
  const setValue = (obj, path, value) => {
    const keys = path.split(".");
    const lastKey = keys.pop();
    const newObj = structuredClone(obj);
    let current = newObj;
    keys.forEach((k) => {
      if (!current[k]) current[k] = {};
      current = current[k];
    });
    current[lastKey] = value;
    return newObj;
  };

  // === Nếu là date, cắt phần "T..." để hiển thị đúng trong input date ===
  let rawValue = getValue(newProduct, keyName);
  if (type === "date" && typeof rawValue === "string") {
    rawValue = rawValue.split("T")[0];
  }

  return (
    <div>
      <label className="text-sm text-gray-700 block mb-1">{label} *</label>
      <input
        type={type}
        className="w-full px-3 py-2 rounded-md border border-gray-300 bg-white text-gray-900 focus:border-gray-900 focus:ring-2 focus:ring-gray-900 outline-none transition"
        value={rawValue}
        onChange={(e) =>
          setNewProduct(setValue(newProduct, keyName, e.target.value))
        }
      />
    </div>
  );
};

/**
 * Textarea component — hỗ trợ nested key (vd: spec.features)
 */
export const Textarea = ({
  label,
  keyName,
  rows,
  newProduct,
  setNewProduct,
}) => {
  const getValue = (obj, path) =>
    path.split(".").reduce((acc, key) => acc?.[key], obj) ?? "";

  const setValue = (obj, path, value) => {
    const keys = path.split(".");
    const lastKey = keys.pop();
    const newObj = structuredClone(obj);
    let current = newObj;
    keys.forEach((k) => {
      if (!current[k]) current[k] = {};
      current = current[k];
    });
    current[lastKey] = value;
    return newObj;
  };

  return (
    <div className="col-span-2">
      <label className="text-sm text-gray-700 block mb-1">{label} *</label>
      <textarea
        rows={rows}
        className="w-full px-3 py-2 rounded-md border border-gray-300 bg-white text-gray-900 focus:border-gray-900 focus:ring-2 focus:ring-gray-900 outline-none transition"
        value={getValue(newProduct, keyName)}
        onChange={(e) =>
          setNewProduct(setValue(newProduct, keyName, e.target.value))
        }
      ></textarea>
    </div>
  );
};
