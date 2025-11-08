import { useState } from "react";
import ProductInfoTab from "./ProductInfoTab";
import ProductDiscountTab from "./ProductDiscountTab";
import ProductInventoryTab from "./ProductInventoryTab";
import ProductVariantTab from "./ProductVariantTab";

const ProductTabs = ({ newProduct, setNewProduct }) => {
  const [activeTab, setActiveTab] = useState("info");

  return (
    <div className="flex flex-col gap-6">
      {/* ===== TAB HEADER ===== */}
      <div className="flex gap-2 border-b border-gray-200">
        {[
          { id: "info", label: "Thông tin sản phẩm" },
          { id: "variants", label: "Phiên bản" },
          { id: "discount", label: "Giá & khuyến mãi" },
          { id: "inventory", label: "Kho hàng" },
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 text-sm font-medium rounded-t-md transition-all ${activeTab === tab.id
                ? "bg-gray-900 text-white hover:bg-gray-800 shadow-sm"
                : "text-gray-700 hover:bg-gray-100"
              }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* ===== TAB CONTENT ===== */}
      <div className="p-4 bg-white border border-gray-200 rounded-md">
        {activeTab === "info" && (
          <ProductInfoTab newProduct={newProduct} setNewProduct={setNewProduct} />
        )}
        {activeTab === "variants" && <ProductVariantTab newProduct={newProduct} setNewProduct={setNewProduct} />}
        {activeTab === "discount" && (
          <ProductDiscountTab newProduct={newProduct} setNewProduct={setNewProduct} />
        )}
        {activeTab === "inventory" && (
          <ProductInventoryTab newProduct={newProduct} setNewProduct={setNewProduct} />
        )}
      </div>
    </div>
  );
};

export default ProductTabs;
