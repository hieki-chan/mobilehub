// src/components/ProductGallery.jsx
import React, { useState } from 'react'

export default function ProductGallery({ images = [] }) {
  const imgs = images.length ? images : ['/no-image.png']
  const [main, setMain] = useState(imgs[0])
  return (
    <section className="gallery" aria-label="Hình ảnh sản phẩm">
      <div className="breadcrumb muted" style={{ marginBottom: 10 }}>
        <a href="/">Trang chủ</a> / <a href="/#">Điện thoại</a> / <span>{/* title filled outside */}</span>
      </div>
      <div className="main-image" id="mainImage"><img src={main} alt="" /></div>
      <div className="thumbs" id="thumbs">
        {imgs.map((src, i) => (
          <button key={i} onClick={() => setMain(src)} aria-label={`Ảnh ${i+1}`}><img src={src} alt="" /></button>
        ))}
      </div>
    </section>
  )
}
