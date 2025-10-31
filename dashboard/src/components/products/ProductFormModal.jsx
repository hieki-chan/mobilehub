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
        className="absolute right-0 top-0 h-full w-full bg-gray-900 text-gray-100 border-l border-gray-700 flex flex-col"
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        {/* ===== HEADER ===== */}
        <div className="flex-shrink-0 flex justify-between items-center px-6 py-4 bg-gray-900 border-b border-gray-700 z-10">
          <h2 className="text-xl font-semibold text-white">🛒 {title}</h2>

          <div className='justify-between items-center flex gap-3'>
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
        </div>

        {/* ===== BODY (scrollable only inside its zone) ===== */}
        <div className="flex-1 overflow-y-auto px-6 py-6 space-y-10">
          <ProductTabs newProduct={newProduct} setNewProduct={setNewProduct} />
          <div className="h-4"></div>
        </div>

        {/* ===== FOOTER ===== */}
        <div className="flex-shrink-0 bottom-100 bg-gray-900 border-t border-gray-700 flex justify-end gap-3 px-6 py-4 z-10">
          <div className="h-8"></div>
        </div>
      </motion.div>
    </div>
  )
}

export default ProductFormModal
