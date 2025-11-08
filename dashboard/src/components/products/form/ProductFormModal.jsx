import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import ProductTabs from "./ProductTabs";
import {
  createAdminProduct,
  getAdminProductDetail,
  updateAdminProduct,
} from "../../../api/ProductApi";

const ProductFormModal = ({ productId, isOpen, onClose, onSubmitSuccess }) => {
  const [newProduct, setNewProduct] = useState({});
  const [loading, setLoading] = useState(false);

  const mode = productId ? "edit" : "add";
  const title = mode === "edit" ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới";

  useEffect(() => {
    document.body.style.overflow = isOpen ? "hidden" : "auto";
    return () => (document.body.style.overflow = "auto");
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) {
      setNewProduct({});
      return;
    }

    if (productId) {
      setLoading(true);
      getAdminProductDetail(productId)
        .then((data) => {
          const variants = data.variants || [];
          const allUrls = [];
          const imageMap = [];

          variants.forEach((v) => {
            const subImages = Array.isArray(v.subImageUrls) ? v.subImageUrls : [];
            const variantImages = [v.imageUrl, ...subImages].filter(Boolean);
            const startIdx = allUrls.length;
            const indexes = Array.from({ length: variantImages.length }, (_, j) => startIdx + j);
            allUrls.push(...variantImages);
            imageMap.push(indexes);
          });

          const mappedProduct = {
            ...data,
            images: [],
            imagePreviews: allUrls,
            imageMap,
            variants: variants.map((v) => ({
              ...v,
              images: [],
              imageUrl: v.imageUrl || null,
              subImageUrls: Array.isArray(v.subImageUrls) ? v.subImageUrls : [],
            })),
          };

          setNewProduct(mappedProduct);
        })
        .catch(() => alert("Không thể tải thông tin sản phẩm!"))
        .finally(() => setLoading(false));
    } else {
      setNewProduct({});
    }
  }, [isOpen, productId]);

  const handleSubmit = async () => {
    try {
      if (mode === "edit") await updateAdminProduct(productId, newProduct);
      else await createAdminProduct(newProduct);
      onSubmitSuccess?.();
      onClose();
    } catch {
      alert("Không thể lưu sản phẩm!");
    }
  };

  if (!isOpen) return null;

  return (
    <div className="absolute top-0 left-0 w-full h-screen z-50 flex bg-black/40 backdrop-blur-sm">
      <motion.div
        className="absolute right-0 top-0 h-full w-full bg-white text-gray-900 flex flex-col shadow-xl"
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <div className="flex-shrink-0 flex justify-between items-center px-6 py-4 bg-gray-50 border-b border-gray-200 z-10">
          <h2 className="text-xl font-semibold text-gray-800">🛒 {title}</h2>
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={onClose}
              className="bg-gray-900 hover:bg-gray-800 px-5 py-2 rounded-md text-white transition"
            >
              Hủy
            </button>
            <button
              type="button"
              onClick={handleSubmit}
              className="bg-orange-500 hover:bg-orange-600 px-6 py-2 rounded-md text-white font-medium transition"
            >
              {mode === "edit" ? "Cập nhật" : "Lưu"}
            </button>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto px-6 py-6 space-y-10 bg-gray-50">
          {loading ? (
            <p className="text-gray-500 italic">Đang tải dữ liệu sản phẩm...</p>
          ) : (
            <ProductTabs
              key={newProduct.id || "new"}
              newProduct={newProduct}
              setNewProduct={setNewProduct}
            />
          )}
        </div>

        <div className="flex-shrink-0 bg-gray-50 border-t border-gray-200 px-6 py-4 z-10">
          <div className="h-6" />
        </div>
      </motion.div>
    </div>
  );
};

export default ProductFormModal;
