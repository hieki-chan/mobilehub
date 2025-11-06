import React from "react";
import { Edit, Trash2, Shield } from "lucide-react";
import RoleBadge from "../common_components/badges/RoleBadge";
import UserStatusBadge from "../common_components/badges/UserStatusBadge";

const UserGridView = ({ users = [], onDelete }) => {
  const formatDate = (dateString) => {
    if (!dateString) return "—";
    try {
      const d = new Date(dateString);
      return d.toLocaleString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch {
      return dateString;
    }
  };


  return (
    <div className="p-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
      {users.map((user, index) => (
        <div
          key={user.id ?? `user-${index}`}
          className="bg-white border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition-all"
        >
          {/* Header */}
          <div className="flex items-center gap-3 mb-3">
            <div
              className={`w-10 h-10 rounded-full ${user.color} flex items-center justify-center text-white font-semibold`}
            >
              {user.avatar}
            </div>
            <div>
              <h3 className="text-sm font-semibold text-gray-900 leading-tight">
                {user.username}
              </h3>
              <p className="text-xs text-gray-500 break-all">{user.email}</p>
            </div>
          </div>

          {/* Role */}
          <div className="mb-2">
            <RoleBadge role={user.role} size="sm" />
          </div>

          {/* Status */}
          <div className="mb-2">
            <UserStatusBadge status={user.status} />,
          </div>

          {/* Created Date */}
          <p className="text-xs text-gray-500 mb-3">
            Ngày tham gia: {formatDate(user.createdDate)}
          </p>

          {/* Actions */}
          <div className="flex justify-end gap-2 pt-2 border-t border-gray-100">
            <button className="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded">
              <Edit size={16} />
            </button>
            <button
              onClick={() => onDelete(user.id)}
              className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded"
            >
              <Trash2 size={16} />
            </button>
          </div>
        </div>
      ))}

      {users.length === 0 && (
        <div className="col-span-full text-center text-gray-500 text-sm py-8">
          Không có người dùng nào.
        </div>
      )}
    </div>
  );
};

export default UserGridView;
