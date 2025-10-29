import { motion } from 'framer-motion'
import { AlertTriangle, DollarSign, Package, TrendingUp } from 'lucide-react'
import React, { useState } from 'react'
import ProductFormModal from '../components/products/ProductFormModal'

import Header from '../components/common_components/Header'
import StatCards from '../components/common_components/StatCards'
import ProductTable from '../components/products/ProductTable'
import SalesTrendChart from "../components/products/SalesTrendChart"
import CategoryDistributionChart from '../components/overview/CategoryDistributionChart'

const ProductsPage = () => {

  const [isAddModalOpen, setAddModalOpen] = useState(false)
  const [newProduct, setNewProduct] = useState({})

  const handleAdd = () => {
    console.log('Tạo sản phẩm:', newProduct)
    setAddModalOpen(false)
    setNewProduct({})
  }

  const openAddModal = () => {
    setAddModalOpen(true)
    setNewProduct({})
  }

  const openEditModal = (product) => {
    setAddModalOpen(true)
    setNewProduct(product)
  }

  return (
    <div className={`flex-1 relative z-10 bg-gray-900 ${isAddModalOpen ? 'overflow-visible' : 'overflow-auto'}`}>
      <Header title="Sản phẩm " />


      {/* STAT DATA  */}
      <main className={`relative mx-auto py-6 px-4 lg:px-8 ${isAddModalOpen ? 'overflow-visible' : 'overflow-auto'}`}>
        <motion.div
          className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-7"
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
        >
          <StatCards name="Tổng sản phẩm" icon={Package} value="4,321" color="#6366f1" />
          <StatCards name="Top Selling" icon={TrendingUp} value="69" color="#10b981" />
          <StatCards name="Low Stock" icon={AlertTriangle} value="32" color="#f59e0b" />
          <StatCards name="Total Revenue" icon={DollarSign} value="$654,310" color="#ef4444" />
        </motion.div>


        {/* PRODUCT TABLE */}

        <ProductTable 
          onAddClick={() => openAddModal()} 
          onEditClick={(product) => openEditModal(product)}
          />


        {/* CHARTS */}

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
          <SalesTrendChart />
          <CategoryDistributionChart />
        </div>

        <ProductFormModal
          product={newProduct}
          isOpen={isAddModalOpen}
          onClose={() => setAddModalOpen(false)}
          onSubmit={handleAdd}
          newProduct={newProduct}
          setNewProduct={setNewProduct}
        />

      </main>
    </div>

  )
}

export default ProductsPage