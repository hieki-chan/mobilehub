import React, { useEffect, useRef, useState } from 'react'
import ProductCard from '../components/ProductCard'
import { useNavigate } from 'react-router-dom'
import { formatPrice } from '../utils/formatPrice'
import { products as mockProducts} from '../data/products'

import '../styles/pages/home.css'


export default function Home() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [modalProduct, setModalProduct] = useState(null)

  const trackRef = useRef(null)
  const [current, setCurrent] = useState(0)
  const slidesCount = 3

  useEffect(() => {
    setTimeout(() => {
      setProducts(mockProducts)
      setLoading(false)
    }, 200)
  }, [])

  useEffect(() => {
    const timer = setInterval(() => setCurrent((c) => (c + 1) % slidesCount), 4000)
    return () => clearInterval(timer)
  }, [])

  useEffect(() => {
    const track = trackRef.current
    if (track) track.style.transform = `translateX(-${current * 100}%)`
  }, [current])

  const openQuickView = (id) => {
    const p = mockProducts.find(x => String(x.id) === String(id))
    if (!p) return alert('Không tìm thấy sản phẩm')
    setModalProduct(p)
    setModalOpen(true)
    document.body.style.overflow = 'hidden'
  }

  const closeQuickView = () => {
    setModalOpen(false)
    setModalProduct(null)
    document.body.style.overflow = ''
  }

  const navigateToSearch = (term) => {
    navigate(`/search?q=${encodeURIComponent(term)}`)
  }
  const popularTerms = [
    'Vphone Pro 6',
    'Vphone X',
    'iPhone 17',
    'Samsung S25',
    'Pixel 9',
    'Điện thoại chơi game',
    'Camera phone',
    'Pin dự phòng',
    'Ốp lưng',
    'Sạc 65W',
  ];

  // --- START: Dữ liệu cho mục mới ---
  // Giả lập "Gợi ý": Lấy các sản phẩm giá rẻ (dưới 20 triệu)
  const suggestedProducts = products.filter(p => p.price < 20000000 && p.status === 'available').slice(0, 4)
  // Giả lập "Bán chạy": Lấy các sản phẩm đang giảm giá (có oldPrice)
  const bestSellerProducts = products.filter(p => p.oldPrice && p.status === 'available').slice(0, 4)
  // --- END: Dữ liệu cho mục mới ---

  return (
    <div>
      {/* Hero */}
      <section className="hero" aria-label="Banner chính">
        <div className="carousel" id="carousel" aria-roledescription="carousel">
          <div className="carousel-track" ref={trackRef}>
            <div className="slide" style={{ backgroundImage: 'linear-gradient(135deg, #06b6d4, #0ea5e9)' }}>
              <div className="slide-content">
                <h2>Khuyến mãi điện thoại — Giá tốt mỗi ngày</h2>
                <p className="muted">Chọn model yêu thích, giao nhanh toàn quốc.</p>
              </div>
            </div>
            <div className="slide" style={{ backgroundImage: 'linear-gradient(135deg, #06b6d4, #0891b2)' }}>
              <div className="slide-content">
                <h2>Vphone Pro Series</h2>
                <p className="muted">Hiệu năng mạnh — Camera chuyên nghiệp.</p>
              </div>
            </div>
            <div className="slide" style={{ backgroundImage: 'linear-gradient(135deg, #0ea5e9, #06b6d4)' }}>
              <div className="slide-content">
                <h2>Flash Sale — Số lượng có hạn</h2>
                <p className="muted">Đừng bỏ lỡ giá rẻ trong ngày.</p>
              </div>
            </div>
          </div>

          <button className="carousel-arrow prev" onClick={() => setCurrent((c) => (c - 1 + slidesCount) % slidesCount)} aria-label="Slide trước">
            <i className="fa fa-chevron-left"></i>
          </button>
          <button className="carousel-arrow next" onClick={() => setCurrent((c) => (c + 1) % slidesCount)} aria-label="Slide sau">
            <i className="fa fa-chevron-right"></i>
          </button>

          <div className="carousel-dots" aria-label="Chọn slide">
            {[0, 1, 2].map(i => (
              <button key={i} aria-label={`Slide ${i + 1}`} className={i === current ? 'active' : ''} onClick={() => setCurrent(i)}></button>
            ))}
          </div>
        </div>
      </section>

      {/* Product grid */}
      <main className="container main-content" id="mainContent">
        <h3 className="section-title">Sản phẩm</h3>
        <section className="products-grid" id="productGrid" aria-label="Danh sách sản phẩm">
          {loading ? (
            Array.from({ length: 8 }).map((_, i) => (
              <div className="skeleton-card" key={i} aria-hidden="true">
                <div className="skel-rect skel-img"></div>
                <div className="skel-rect skel-line"></div>
                <div className="skel-rect skel-line short"></div>
                <div style={{ flex: 1 }}></div>
                <div className="skel-rect skel-btn"></div>
              </div>
            ))
          ) : (!products.length ? (
            <div className="loading" style={{ padding: 18, textAlign: 'center' }}>Không có sản phẩm nào.</div>
          ) : (
            products.map(p => <ProductCard key={p.id} p={p} onQuickView={openQuickView} />)
          ))}
        </section>

        {/* === START: Mục Gợi ý cho bạn === */}
        {suggestedProducts.length > 0 && (
          <>
            <h3 className="section-title" style={{ marginTop: '24px' }}>Gợi ý cho bạn</h3>
            <section className="products-grid" aria-label="Sản phẩm gợi ý">
              {loading ? (
                Array.from({ length: suggestedProducts.length }).map((_, i) => (
                  <div className="skeleton-card" key={i} aria-hidden="true">
                    <div className="skel-rect skel-img"></div>
                    <div className="skel-rect skel-line"></div>
                    <div className="skel-rect skel-btn"></div>
                  </div>
                ))
              ) : (
                suggestedProducts.map(p => <ProductCard key={p.id} p={p} onQuickView={openQuickView} />)
              )}
            </section>
          </>
        )}
        {/* === END: Mục Gợi ý cho bạn === */}


        {/* === START: Mục Bán chạy === */}
        {bestSellerProducts.length > 0 && (
          <>
            <h3 className="section-title" style={{ marginTop: '24px' }}>Bán chạy</h3>
            <section className="products-grid" aria-label="Sản phẩm bán chạy">
              {loading ? (
                Array.from({ length: bestSellerProducts.length }).map((_, i) => (
                  <div className="skeleton-card" key={i} aria-hidden="true">
                    <div className="skel-rect skel-img"></div>
                    <div className="skel-rect skel-line"></div>
                    <div className="skel-rect skel-btn"></div>
                  </div>
                ))
              ) : (
                bestSellerProducts.map(p => <ProductCard key={p.id} p={p} onQuickView={openQuickView} />)
              )}
            </section>
          </>
        )}
        {/* === END: Mục Bán chạy === */}

        {/* Mọi người cũng tìm kiếm */}

        <section className="popular-searches container" aria-label="Mọi người cũng tìm kiếm">
          <h4 className="popular-title">Mọi người cũng tìm kiếm</h4>
          <div className="tags-wrap">
            {popularTerms.map((term, index) => (
              <a
                key={index} 
                onClick={() => navigateToSearch(term)}
                className="tag"
                href={`/search?q=${encodeURIComponent(term)}`} 
              >
                {term}
              </a>
            ))}
          </div>
        </section>
      </main>

      {/* Modal */}
      {modalOpen && modalProduct && (
        <div id="quickViewModal" className={`modal-overlay open`} onClick={(e) => { if (e.target.id === 'quickViewModal') closeQuickView() }}>
          <div className="modal" role="dialog" aria-modal="true" aria-labelledby="qvTitle">
            <button className="modal-close" aria-label="Đóng" onClick={closeQuickView}>&times;</button>
            <div className="modal-body">
              <div className="modal-img"><img src={modalProduct.image} alt={modalProduct.name} /></div>
              <div className="modal-info">
                <h3 id="qvTitle">{modalProduct.name}</h3>
                <div className="price">{formatPrice(modalProduct.price)}</div>
                <div className="muted" style={{ marginTop: 8 }}>{modalProduct.desc}</div>
                <div className="modal-actions">
                  <button className="btn btn-primary" id="qvBuy" onClick={() => { if (modalProduct.status !== 'coming_soon') window.location.href = `product/${encodeURIComponent(modalProduct.id)}` }} disabled={modalProduct.status === 'coming_soon'}>{modalProduct.status === 'coming_soon' ? 'Sắp mở bán' : 'Mua ngay'}</button>
                  <button className="btn btn-secondary" id="qvClose" onClick={closeQuickView}>Đóng</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
