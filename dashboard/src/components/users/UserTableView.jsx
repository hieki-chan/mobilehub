import React from "react";
import { UserCircle, Mail, Shield, Calendar } from "lucide-react";
import GenericTableView from "../common_components/GenericTableView";
import RoleBadge from "../common_components/badges/RoleBadge";
import UserStatusBadge from "../common_components/badges/UserStatusBadge";

const UserTableView = ({ users = [], onDelete, onEdit }) => (
  <GenericTableView
    data={users}
    entityName="người dùng"
    onDelete={onDelete}
    onEdit={onEdit}
    columns={[
      { key: "id", label: "Mã" },

      {
        key: "username",
        label: "Tên",
        icon: UserCircle,
        render: (u) => (
          <div className="flex items-center gap-3">
            <div
              className={`w-8 h-8 rounded-full ${u.color} flex items-center justify-center text-white text-xs font-medium`}
            >
              {u.avatar}
            </div>
            <span className="text-sm font-medium text-gray-900">{u.username}</span>
          </div>
        ),
      },

      {
        key: "email",
        label: "Email",
        icon: Mail,
        render: (u) => (
          <a href={`mailto:${u.email}`} className="text-blue-600 hover:underline">
            {u.email}
          </a>
        ),
      },

      {
        key: "createdDate",
        label: "Ngày tạo",
        icon: Calendar,
        render: (u) =>
          new Date(u.createdDate).toLocaleString("vi-VN", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
          }),
      },

      {
        key: "role",
        label: "Vai trò",
        icon: Shield,
        render: (u) => <RoleBadge role={u.role} />,
      },

      {
        key: "status",
        label: "Trạng thái",
        icon: Shield,
        render: (u) => <UserStatusBadge status={u.status} />,
      },
    ]}
  />
);

export default UserTableView;
