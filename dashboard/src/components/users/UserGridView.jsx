import React from "react";
import { Edit, Trash2 } from "lucide-react";

const UserGridView = ({ users = [], onDelete }) => {
  return (
    <div className="p-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
      {users.map((user) => (
        <div
          key={user.id}
          className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm hover:shadow-md transition"
        >
          <div className="flex items-center gap-3 mb-3">
            <div
              className={`w-10 h-10 rounded-full ${user.color} flex items-center justify-center text-white font-medium`}
            >
              {user.avatar}
            </div>
            <div>
              <h3 className="text-sm font-semibold text-gray-900">
                {user.name}
              </h3>
              <p className="text-xs text-gray-500">{user.email}</p>
            </div>
          </div>

          <div className="text-sm text-gray-700 mb-2">
            <span className="font-medium">Vai trò:</span> {user.role}
          </div>
          <div className="mb-2">
            <span
              className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${
                user.status === "Active"
                  ? "bg-green-100 text-green-700"
                  : "bg-gray-100 text-gray-600"
              }`}
            >
              <div
                className={`w-1.5 h-1.5 rounded-full ${
                  user.status === "Active" ? "bg-green-500" : "bg-gray-400"
                }`}
              ></div>
              {user.status}
            </span>
          </div>
          <p className="text-xs text-gray-500 mb-3">
            Ngày tham gia: {user.createdDate}
          </p>

          <div className="flex justify-end gap-2">
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
