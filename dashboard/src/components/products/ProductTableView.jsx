import React from "react";
import { useNavigate } from "react-router-dom";
import { Package, Layers, Tag, Warehouse, Percent, Star } from "lucide-react";
import GenericTableView from "../common_components/GenericTableView";

const ProductTableView = ({ products = [], onDelete, onEdit, onView }) => {
  const navigate = useNavigate();

  return (
    <GenericTableView
      data={products}
      entityName="sản phẩm"
      onDelete={onDelete}
      onEdit={onEdit}
      onView={onView}
      columns={[
        { key: "id", label: "Mã" },
        {
          key: "name",
          label: "Sản phẩm",
          icon: Package,
          render: (p) => (
            <div className="flex items-center gap-3">
              <img
                src={p.imageUrl}
                alt={p.name}
                className="w-10 h-10 rounded-md object-cover border"
              />

              <div className="flex flex-col">
                <span className="text-sm font-medium text-gray-900 flex items-center gap-2">
                  {p.name}
                </span>

                <span
                  className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium border border-gray-300 bg-gray-50"
                  title={p.color_label}
                >
                  <span
                    className="inline-block w-3 h-3 rounded-full border"
                    style={{ backgroundColor: p.color_hex || "#ccc" }}
                  />
                  <span className="text-gray-700">
                    {p.color_label}
                  </span>
                </span>

              </div>
            </div>

          ),
        },
        { key: "category", label: "Danh mục", icon: Layers },
        {
          key: "price",
          label: "Giá tiền",
          icon: Tag,
          render: (p) => (
            <span className="text-blue-600 font-semibold">
              {p.price?.toLocaleString("vi-VN")}₫
            </span>
          ),
        },
        { key: "sales", label: "KM (%)", icon: Percent },
        { key: "stock", label: "Kho", icon: Warehouse },

        {
          key: "rating",
          label: "Đánh giá",
          icon: Star,
          render: (p) => (
            <button
              onClick={() => navigate(`/products/${p.id}/reviews`)}
              className="flex items-center gap-1 text-yellow-500 hover:text-yellow-600 transition-colors"
            >
              <Star
                size={14}
                className={
                  p.rating > 0 ? "fill-yellow-400" : "text-gray-300"
                }
              />
              <span className="text-sm font-medium">
                {p.rating ? p.rating.toFixed(1) : "Xem"}
              </span>
            </button>
          ),
        },

        {
          key: "status",
          label: "Trạng thái",
          render: (p) => (
            <span
              className={`inline-flex items-center gap-1.5 px-2 py-1 rounded-full text-xs font-medium ${p.status === "ACTIVE"
                ? "bg-green-100 text-green-700"
                : "bg-gray-100 text-gray-600"
                }`}
            >
              <div
                className={`w-1.5 h-1.5 rounded-full ${p.status === "ACTIVE" ? "bg-green-500" : "bg-gray-400"
                  }`}
              ></div>
              {p.status === "ACTIVE" ? "Hoạt động" : "Ngừng"}
            </span>
          ),
        },
      ]}
    />
  );
};

export default ProductTableView;
