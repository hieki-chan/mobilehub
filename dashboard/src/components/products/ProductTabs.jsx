import { useState } from 'react'
import ProductInfoTab from './ProductInfoTab'
import ProductDiscountTab from './ProductDiscountTab'

const ProductTabs = ({ newProduct, setNewProduct }) => {
  const [activeTab, setActiveTab] = useState('info')

  return (
    <div className="flex flex-col gap-6">
      {/* ===== TAB HEADER ===== */}
      <div className="flex gap-2 border-b border-gray-700">
        {[
          { id: 'info', label: '📄 Thông tin sản phẩm' },
          { id: 'discount', label: '💸 Giá & khuyến mãi' },
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 text-sm font-medium rounded-t-md transition-all ${
              activeTab === tab.id
                ? 'bg-blue-600 text-white shadow'
                : 'text-gray-300 hover:bg-gray-800'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* ===== TAB CONTENT ===== */}
      <div className="p-4 bg-gray-900 border border-gray-700 rounded-md">
        {activeTab === 'info' && (
          <ProductInfoTab newProduct={newProduct} setNewProduct={setNewProduct} />
        )}
        {activeTab === 'discount' && (
          <ProductDiscountTab newProduct={newProduct} setNewProduct={setNewProduct} />
        )}
      </div>
    </div>
  )
}

export default ProductTabs
