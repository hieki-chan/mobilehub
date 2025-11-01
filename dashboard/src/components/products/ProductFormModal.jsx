import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { X } from "lucide-react";
import ProductTabs from "./ProductTabs";
import {
  createAdminProduct,
  getAdminProductDetail,
  updateAdminProduct,
} from "../../api/ProductApi";

const ProductFormModal = ({
  productId,          // 🆕 chỉ truyền ID
  isOpen,
  onClose,
  onSubmitSuccess,
}) => {
  const [newProduct, setNewProduct] = useState({});
  const [loading, setLoading] = useState(false);

  const mode = productId ? "edit" : "add";
  const title =
    mode === "edit" ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới";

  // ===== Scroll lock khi mở modal =====
  useEffect(() => {
    if (isOpen) document.body.style.overflow = "hidden";
    else document.body.style.overflow = "auto";
    return () => (document.body.style.overflow = "auto");
  }, [isOpen]);

  // ===== Khi mở modal edit => load chi tiết sản phẩm =====
  useEffect(() => {
    if (isOpen && productId) {
      setLoading(true);

      getAdminProductDetail(productId)
        .then((data) => {
          setNewProduct({
            ...data,
            images: [],
            imagePreviews: data.otherImageUrls || [],
            mainImage: data.mainImageUrl || (data.otherImageUrls?.[0] ?? null),
          });

          console.log("🟢 Product loaded:", {
            ...data,
            images: [],
            imagePreviews: data.otherImageUrls || [],
            mainImage: data.mainImageUrl || (data.otherImageUrls?.[0] ?? null),
          });
        })
        .catch((err) => {
          console.error("❌ Lỗi tải chi tiết sản phẩm:", err);
          alert("Không thể tải thông tin sản phẩm!");
        })
        .finally(() => setLoading(false));
    } else if (!isOpen) {
      setNewProduct({});
    }
  }, [isOpen, productId]);



  // ===== Gửi form (create / update) =====
  const handleSubmit = async () => {
    try {
      if (mode === "edit") {
        await updateAdminProduct(productId, newProduct);
      } else {
        await createAdminProduct(newProduct);
      }
      onSubmitSuccess?.();
      onClose();
    } catch (error) {
      console.error("🚨 Lưu sản phẩm thất bại:", error);
      alert("Không thể lưu sản phẩm!");
    }
  };

  if (!isOpen) return null;

  return (
    <div className="absolute top-0 left-0 w-full h-screen z-50 flex flex-col bg-black bg-opacity-60 backdrop-blur-sm">
      <motion.div
        className="absolute right-0 top-0 h-full w-full bg-gray-900 text-gray-100 border-l border-gray-700 flex flex-col"
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        {/* ===== HEADER ===== */}
        <div className="flex-shrink-0 flex justify-between items-center px-6 py-4 bg-gray-900 border-b border-gray-700 z-10">
          <h2 className="text-xl font-semibold text-white">🛒 {title}</h2>

          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={onClose}
              className="bg-gray-600 hover:bg-red-500 px-5 py-2 rounded-md text-white transition"
            >
              Huỷ
            </button>
            <button
              type="button"
              onClick={handleSubmit}
              className="bg-blue-600 hover:bg-blue-800 px-6 py-2 rounded-md text-white transition"
            >
              {mode === "edit" ? "Cập nhật" : "Lưu"}
            </button>
          </div>
        </div>

        {/* ===== BODY ===== */}
        <div className="flex-1 overflow-y-auto px-6 py-6 space-y-10">
          {loading ? (
            <p className="text-gray-400 italic">Đang tải dữ liệu sản phẩm...</p>
          ) : (
            <ProductTabs
              key={newProduct.id || "new"}
              newProduct={newProduct}
              setNewProduct={setNewProduct}
            />
          )}
        </div>

        {/* ===== FOOTER ===== */}
        <div className="flex-shrink-0 bg-gray-900 border-t border-gray-700 flex justify-end gap-3 px-6 py-4 z-10">
          <div className="h-8"></div>
        </div>
      </motion.div>
    </div>
  );
};

export default ProductFormModal;
