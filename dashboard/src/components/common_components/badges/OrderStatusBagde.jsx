import React from "react";

const OrderStatusBadge = ({ status }) => {
  const isActive = status?.toLowerCase() === "active";
  return (
    <span
      className={`px-2 py-1 rounded-full text-xs font-medium ${
        isActive
          ? "bg-green-100 text-green-700"
          : "bg-gray-200 text-gray-600"
      }`}
    >
      {status == "ACTIVE" ? "Hoạt động" : "Ngừng"}
    </span>
  );
};

export default OrderStatusBadge;
