import { useEffect } from 'react'
import { motion } from 'framer-motion'
import { X } from 'lucide-react'
import ProductTabs from './ProductTabs'

const ProductFormModal = ({ product, isOpen, onClose, onSubmit, newProduct, setNewProduct }) => {
  const mode = newProduct && newProduct.id ? 'edit' : 'add'
  const title = mode === 'edit' ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới'

  useEffect(() => {
    if (isOpen) document.body.style.overflow = 'hidden'
    else document.body.style.overflow = 'auto'
    return () => (document.body.style.overflow = 'auto')
  }, [isOpen])

  if (!isOpen) return null

  return (
    <div className="absolute top-0 left-0 w-full h-screen z-50 flex flex-col bg-black bg-opacity-60 backdrop-blur-sm">
      <motion.div
        className="flex flex-col h-full bg-gray-900 text-gray-100 border-l border-gray-700"
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        {/* ===== HEADER ===== */}
        <div className="sticky top-0 flex justify-between items-center px-6 py-4 bg-gray-900 border-b border-gray-700 z-10">
          <h2 className="text-xl font-semibold text-white">🛒 {title}</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-red-500 transition-colors"
          >
            <X size={26} />
          </button>
        </div>

        {/* ===== BODY ===== */}
        <div className="flex-1 overflow-y-auto p-6 space-y-10">
          {/* Tabs ở đây */}
          <ProductTabs newProduct={newProduct} setNewProduct={setNewProduct} />
          <div className="h-16"></div>
        </div>

        {/* ===== FOOTER ===== */}
        <div className="sticky bottom-0 bg-gray-900 border-t border-gray-700 flex justify-end gap-3 px-6 py-4 z-10">
          <button
            type="button"
            onClick={onClose}
            className="bg-gray-600 hover:bg-red-500 px-5 py-2 rounded-md text-white transition"
          >
            Huỷ
          </button>
          <button
            type="button"
            onClick={onSubmit}
            className="bg-blue-600 hover:bg-blue-800 px-6 py-2 rounded-md text-white transition"
          >
            Lưu
          </button>
        </div>
      </motion.div>
    </div>
  )
}

export default ProductFormModal
