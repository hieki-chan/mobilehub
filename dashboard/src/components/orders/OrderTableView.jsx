import React from "react";
import { Package, User, Mail, DollarSign, Calendar, Shield } from "lucide-react";
import GenericTableView from "../common_components/GenericTableView";
import OrderStatusBagde from "../common_components/badges/OrderStatusBagde";

const OrderTableView = ({ orders = [], onDelete }) => (
  <GenericTableView
    data={orders}
    entityName="đơn hàng"
    onDelete={onDelete}
    columns={[
      { key: "id", label: "Mã" },
      { key: "code", label: "Mã đơn", icon: Package },
      {
        key: "customerName",
        label: "Khách hàng",
        icon: User,
      },
      {
        key: "email",
        label: "Email",
        icon: Mail,
        render: (o) => (
          <a href={`mailto:${o.email}`} className="text-blue-600 hover:underline">
            {o.email}
          </a>
        ),
      },
      {
        key: "total",
        label: "Tổng tiền",
        icon: DollarSign,
        render: (o) => o.total?.toLocaleString("vi-VN") + " ₫",
      },
      {
        key: "payment",
        label: "Thanh toán",
        icon: Shield,
      },
      {
        key: "status",
        label: "Trạng thái",
        icon: Shield,
        render: (o) => <OrderStatusBagde status={o.status} />,
      },
      {
        key: "createdDate",
        label: "Ngày tạo",
        icon: Calendar,
        render: (o) =>
          new Date(o.createdDate).toLocaleString("vi-VN", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
          }),
      },
    ]}
  />
);

export default OrderTableView;
