import React from "react";
import { Shield } from "lucide-react";
import clsx from "clsx"; 

const RoleBadge = ({ role, size = "sm" }) => {
  const getRoleStyle = (role) => {
    switch (role) {
      case "ADMIN":
        return "bg-red-100 text-red-700 border border-red-200";
      case "EMPLOYEE":
        return "bg-blue-100 text-blue-700 border border-blue-200";
      case "USER":
        return "bg-gray-100 text-gray-700 border border-gray-200";
      default:
        return "bg-gray-100 text-gray-600 border border-gray-200";
    }
  };

  const textSize =
    size === "sm"
      ? "text-xs px-2 py-1"
      : size === "md"
      ? "text-sm px-3 py-1.5"
      : "text-base px-3 py-2";

  return (
    <span
      className={clsx(
        "inline-flex items-center gap-1.5 rounded-full font-medium transition-colors duration-150",
        getRoleStyle(role),
        textSize
      )}
    >
      <Shield size={size === "sm" ? 12 : size === "md" ? 14 : 16} />
      <span>{role === "ADMIN" ? "Quản trị viên" : "Người dùng" || "Unknown"}</span>
    </span>
  );
};

export default RoleBadge;
