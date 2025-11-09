import React, { useState } from 'react'
import '../styles/components/product-gallery.css'

export default function ProductGallery({ images = [] }) {
  const imgs = images.length ? images : ['/no-image.png']
  const [mainIndex, setMainIndex] = useState(0)
  const [fade, setFade] = useState(false)

  const main = imgs[mainIndex]

  const handleChange = (index) => {
    setFade(true)
    setTimeout(() => {
      setMainIndex(index)
      setFade(false)
    }, 200)
  }

  const prevImage = () => {
    handleChange((mainIndex - 1 + imgs.length) % imgs.length)
  }

  const nextImage = () => {
    handleChange((mainIndex + 1) % imgs.length)
  }

  return (
    <section className="gallery" aria-label="Hình ảnh sản phẩm">
      <div className="breadcrumb muted" style={{ marginBottom: 10 }}>
        <a href="/">Trang chủ</a> / <a href="/#">Điện thoại</a> / <span>{/* title filled outside */}</span>
      </div>

      <div className={`main-image ${fade ? 'fade' : ''}`} id="mainImage">
        <button className="nav prev" onClick={prevImage} aria-label="Ảnh trước">&lt;</button>
        <img src={main} alt="" />
        <button className="nav next" onClick={nextImage} aria-label="Ảnh tiếp">&gt;</button>
      </div>

      <div className="thumbs" id="thumbs">
        {imgs.map((src, i) => (
          <button key={i} onClick={() => handleChange(i)} aria-label={`Ảnh ${i+1}`}>
            <img src={src} alt="" className={i === mainIndex ? 'active-thumb' : ''} />
          </button>
        ))}
      </div>
    </section>
  )
}
