// src/components/ActionButtons.jsx
import React from 'react'
import { useNavigate } from 'react-router-dom'
import { formatPrice } from '../utils/formatPrice'

export default function ActionButtons({ product, qty, capacity, color, onAddCart, onBuyNow }) {
  const navigate = useNavigate()

  const handleInstallment = () => {
    navigate(`/installment?productId=${product.id}`)
  }

  const share = () => {
    if (navigator.share) { 
      navigator.share({ title: product.name, text: product.desc, url: location.href }).catch(()=>{}) 
    } else { 
      navigator.clipboard?.writeText(location.href).then(()=> alert('Đã sao chép liên kết')) 
    }
  }

  return (
    <div className="buy-section">
      <div style={{ display: 'flex', gap: 12 }}>
        <div style={{ display: 'flex', alignItems:'center', gap: 8 }}>
          <div style={{ fontWeight:700, marginRight:8 }}>{/* qty controlled outside */}</div>
        </div>
      </div>
      <div className="action-buttons-row" style={{ marginTop: 10 }}>
        <button className="btn btn-primary btn-action" id="addCart" onClick={() => onAddCart({ id: product.id, name: product.name, price: product.price, qty, capacity, color })}>Thêm vào giỏ</button>
        <button className="btn btn-primary btn-action" id="buyNow" onClick={() => onBuyNow({ id: product.id, name: product.name, price: product.price, qty, capacity, color })}>Mua ngay</button>
      </div>
      <button className="btn btn-secondary btn-full" id="instalment" onClick={handleInstallment}>
        <i className="fa fa-credit-card"></i> Mua trả góp 0%
      </button>
    </div>

  )
}