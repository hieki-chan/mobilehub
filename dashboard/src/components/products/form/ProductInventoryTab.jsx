import { useState } from "react";
import { AlertTriangle, Plus, Edit3 } from "lucide-react";

const ProductInventoryTab = ({ newProduct, setNewProduct }) => {
  const [quantityToAdd, setQuantityToAdd] = useState("");
  const [showWarning, setShowWarning] = useState(false);

  const currentQty = newProduct?.inventory?.quantity || 0;
  const importPrice = newProduct?.inventory?.import_price || "";

  // === Nhập thêm hàng ===
  const handleAddStock = () => {
    if (!quantityToAdd || isNaN(quantityToAdd)) return;
    const updatedQty = currentQty + parseInt(quantityToAdd);
    setNewProduct((prev) => ({
      ...prev,
      inventory: {
        ...prev.inventory,
        quantity: updatedQty,
      },
    }));
    setQuantityToAdd("");
  };

  // === Sửa số lượng trực tiếp ===
  const handleQuantityChange = (e) => {
    setShowWarning(true);
    const value = e.target.value;
    setNewProduct((prev) => ({
      ...prev,
      inventory: {
        ...prev.inventory,
        quantity: Number(value),
      },
    }));
  };

  return (
    <div className="space-y-10">
      {/* --- Tổng quan tồn kho --- */}
      <section>
        <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
          📦 Quản lý kho hàng
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <label className="text-sm font-medium text-gray-700 block mb-1">
              Giá nhập hàng (VNĐ)
            </label>
            <input
              type="number"
              value={importPrice}
              onChange={(e) =>
                setNewProduct((prev) => ({
                  ...prev,
                  inventory: {
                    ...prev.inventory,
                    import_price: Number(e.target.value),
                  },
                }))
              }
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="Nhập giá nhập..."
            />
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700 block mb-1">
              Số lượng hiện có
            </label>
            <div className="flex items-center gap-2">
              <input
                type="number"
                value={currentQty}
                onChange={handleQuantityChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md"
              />
              <Edit3 className="text-gray-500" size={18} />
            </div>
          </div>
        </div>

        {showWarning && (
          <div className="mt-3 p-3 bg-yellow-100 border border-yellow-300 rounded-md flex items-center gap-2 text-yellow-800">
            <AlertTriangle size={18} />
            <p className="text-sm">
              ⚠️ Việc chỉnh sửa số lượng thủ công có thể gây sai lệch dữ liệu kho. Hãy xác nhận thay đổi trước khi lưu.
            </p>
          </div>
        )}
      </section>

      {/* --- Nhập thêm hàng --- */}
      <section>
        <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
          ➕ Nhập thêm hàng
        </h3>
        <div className="flex gap-3 items-end">
          <div className="flex-1">
            <label className="text-sm font-medium text-gray-700 block mb-1">
              Số lượng nhập thêm
            </label>
            <input
              type="number"
              value={quantityToAdd}
              onChange={(e) => setQuantityToAdd(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="VD: 10"
            />
          </div>
          <button
            onClick={handleAddStock}
            className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition"
          >
            <Plus size={18} /> Nhập hàng
          </button>
        </div>
      </section>
    </div>
  );
};

export default ProductInventoryTab;
