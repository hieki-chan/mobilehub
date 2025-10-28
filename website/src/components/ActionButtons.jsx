// src/components/ActionButtons.jsx
import React from 'react'
import { formatPrice } from '../utils/formatPrice'

export default function ActionButtons({ product, qty, capacity, color, onAddCart, onBuyNow }) {
  // test trả góp
  const instalment = () => {
    const months = Number(prompt('Chọn số tháng trả góp (3,6,9,12):', '6')) || 6
    const fee = 0.02
    const principal = product.price || 0
    const monthly = Math.ceil((principal * (1 + fee)) / months)
    alert(`Trả góp ${months} tháng — khoảng ${formatPrice(monthly)} / tháng (demo).`)
  }

  const share = () => {
    if (navigator.share) { navigator.share({ title: product.name, text: product.desc, url: location.href }).catch(()=>{}) }
    else { navigator.clipboard?.writeText(location.href).then(()=> alert('Đã sao chép liên kết')) }
  }

  return (
    <div className="buy-section">
      <div style={{ display: 'flex', gap: 12 }}>
        <div style={{ display: 'flex', alignItems:'center', gap: 8 }}>
          <div style={{ fontWeight:700, marginRight:8 }}>{/* qty controlled outside */}</div>
        </div>
      </div>
      <div className="action-row" style={{ marginTop: 10 }}>
        <button className="btn btn-primary btn-lg" id="addCart" onClick={() => onAddCart({ id: product.id, name: product.name, price: product.price, qty, capacity, color })}>Thêm vào giỏ</button>
        <button className="btn btn-primary btn-lg" id="buyNow" onClick={() => onBuyNow({ id: product.id, name: product.name, price: product.price, qty, capacity, color })}>Mua ngay</button>
        <button className="btn btn-secondary btn-lg" id="instalment" onClick={instalment}>Mua trả góp</button>
        <button className="icon-btn" onClick={share} title="Chia sẻ"><i className="fa fa-share-nodes"></i></button>
      </div>
    </div>
  )
}
