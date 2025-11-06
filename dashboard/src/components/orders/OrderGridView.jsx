import React from "react";
import { Trash2 } from "lucide-react";
import OrderStatusBagde from "../common_components/badges/OrderStatusBagde";

const OrderGridView = ({ orders = [], onDelete }) => (
  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
    {orders.map((o) => (
      <div
        key={o.id}
        className="bg-white shadow-sm border border-gray-200 rounded-xl p-4 hover:shadow-md transition"
      >
        <div className="flex justify-between items-center mb-2">
          <span className="font-semibold text-gray-900">{o.code}</span>
          <button
            onClick={() => onDelete(o.id)}
            className="text-red-500 hover:text-red-700"
          >
            <Trash2 size={16} />
          </button>
        </div>
        <p className="text-sm text-gray-700 mb-1">
          <strong>Khách hàng:</strong> {o.customerName}
        </p>
        <p className="text-sm text-gray-700 mb-1">
          <strong>Email:</strong> {o.email}
        </p>
        <p className="text-sm text-gray-700 mb-1">
          <strong>Tổng tiền:</strong> {o.total?.toLocaleString("vi-VN")} ₫
        </p>
        <p className="text-sm text-gray-700 mb-1">
          <strong>Thanh toán:</strong> {o.payment}
        </p>
        <p className="text-sm text-gray-700 mb-1 flex items-center gap-2">
          <strong>Trạng thái:</strong> <OrderStatusBagde status={o.status} />
        </p>
        <p className="text-xs text-gray-500 mt-2">
          {new Date(o.createdDate).toLocaleString("vi-VN")}
        </p>
      </div>
    ))}
  </div>
);

export default OrderGridView;
