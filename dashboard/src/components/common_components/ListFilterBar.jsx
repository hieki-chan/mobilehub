import React from "react";

const ListFilterBar = ({ filters = [] }) => {
  return (
    <div className="sticky top-[128px] z-30 p-4 border-b border-gray-200 bg-gray-50 flex flex-wrap items-center gap-3 sm:gap-4">
      {filters.map((f, i) => (
        <div
          key={i}
          className={`flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2 ${i > 0 ? "sm:ml-6" : ""}`}
        >
          <label className="text-sm font-medium text-gray-700">{f.label}:</label>
          <select
            value={f.value}
            onChange={(e) => f.onChange(e.target.value)}
            className="border border-gray-300 rounded-md px-3 py-1.5 text-sm text-gray-800 focus:ring-2 focus:ring-gray-900 focus:outline-none w-full sm:w-auto"
          >
            {f.options.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>
      ))}
    </div>
  );
};

export default ListFilterBar;
