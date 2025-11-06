import React, { useMemo, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { products as mockProducts } from '../data/products'
import ProductCard from '../components/home/ProductCard'
import '../styles/pages/search-results.css'

function useQuery() {
  return new URLSearchParams(useLocation().search)
}

export default function SearchResults() {
  const q = useQuery().get('q') || ''
  const term = q.trim().toLowerCase()

  // State cho filters
  const [priceRange, setPriceRange] = useState('all')
  const [sortBy, setSortBy] = useState('default')
  const [showFilters, setShowFilters] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [modalProduct, setModalProduct] = useState(null)

  // Tìm kiếm sản phẩm
  const searchResults = useMemo(() => {
    if (!term) return []
    return (mockProducts || []).filter(p => 
      p.name.toLowerCase().includes(term) ||
      p.desc?.toLowerCase().includes(term)
    )
  }, [term])

  // Lọc theo giá
  const filteredResults = useMemo(() => {
    let filtered = [...searchResults]

    // Lọc theo khoảng giá
    if (priceRange !== 'all') {
      filtered = filtered.filter(p => {
        const price = p.price || 0
        switch (priceRange) {
          case 'under5':
            return price < 5000000
          case '5to10':
            return price >= 5000000 && price < 10000000
          case '10to20':
            return price >= 10000000 && price < 20000000
          case '20to30':
            return price >= 20000000 && price < 30000000
          case 'over30':
            return price >= 30000000
          default:
            return true
        }
      })
    }

    return filtered
  }, [searchResults, priceRange])

  // Sắp xếp
  const sortedResults = useMemo(() => {
    let sorted = [...filteredResults]

    switch (sortBy) {
      case 'price-asc':
        sorted.sort((a, b) => (a.price || 0) - (b.price || 0))
        break
      case 'price-desc':
        sorted.sort((a, b) => (b.price || 0) - (a.price || 0))
        break
      case 'name-asc':
        sorted.sort((a, b) => a.name.localeCompare(b.name))
        break
      case 'name-desc':
        sorted.sort((a, b) => b.name.localeCompare(a.name))
        break
      case 'default':
      default:
        // Giữ nguyên thứ tự mặc định
        break
    }

    return sorted
  }, [filteredResults, sortBy])

  const handleResetFilters = () => {
    setPriceRange('all')
    setSortBy('default')
  }

  const openQuickView = (id) => {
    const p = sortedResults.find(x => String(x.id) === String(id))
    if (!p) return
    setModalProduct(p)
    setModalOpen(true)
    document.body.style.overflow = 'hidden'
  }

  const closeQuickView = () => {
    setModalOpen(false)
    setModalProduct(null)
    document.body.style.overflow = ''
  }

  const formatPrice = (v) => {
    if (v == null || v === '') return ''
    try { return new Intl.NumberFormat('vi-VN').format(Number(v)) + '₫' }
    catch (e) { return String(v) + '₫' }
  }

  return (
    <main className="search-results-page">
      <div className="container">
        <div className="search-header">
          <div>
            <h3 className="section-title">Kết quả tìm kiếm</h3>
            <p className="muted">
              Tìm cho: <strong>"{q}"</strong> · {sortedResults.length} kết quả
            </p>
          </div>
          
          <button 
            className="btn btn-secondary toggle-filters-btn"
            onClick={() => setShowFilters(!showFilters)}
          >
            <i className={`fa fa-${showFilters ? 'times' : 'filter'}`}></i>
            {showFilters ? 'Ẩn bộ lọc' : 'Hiện bộ lọc'}
          </button>
        </div>

        <div className="search-content">
          {/* Sidebar Filters */}
          {showFilters && (
            <aside className="filters-sidebar">
              <div className="filter-section">
                <h4 className="filter-title">
                  <i className="fa fa-sort"></i>
                  Sắp xếp
                </h4>
                <div className="filter-options">
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="sort" 
                      value="default"
                      checked={sortBy === 'default'}
                      onChange={(e) => setSortBy(e.target.value)}
                    />
                    <span>Mặc định</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="sort" 
                      value="price-asc"
                      checked={sortBy === 'price-asc'}
                      onChange={(e) => setSortBy(e.target.value)}
                    />
                    <span>Giá thấp đến cao</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="sort" 
                      value="price-desc"
                      checked={sortBy === 'price-desc'}
                      onChange={(e) => setSortBy(e.target.value)}
                    />
                    <span>Giá cao đến thấp</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="sort" 
                      value="name-asc"
                      checked={sortBy === 'name-asc'}
                      onChange={(e) => setSortBy(e.target.value)}
                    />
                    <span>Tên A-Z</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="sort" 
                      value="name-desc"
                      checked={sortBy === 'name-desc'}
                      onChange={(e) => setSortBy(e.target.value)}
                    />
                    <span>Tên Z-A</span>
                  </label>
                </div>
              </div>

              <div className="filter-section">
                <h4 className="filter-title">
                  <i className="fa fa-dollar-sign"></i>
                  Khoảng giá
                </h4>
                <div className="filter-options">
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="price" 
                      value="all"
                      checked={priceRange === 'all'}
                      onChange={(e) => setPriceRange(e.target.value)}
                    />
                    <span>Tất cả</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="price" 
                      value="under5"
                      checked={priceRange === 'under5'}
                      onChange={(e) => setPriceRange(e.target.value)}
                    />
                    <span>Dưới 5 triệu</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="price" 
                      value="5to10"
                      checked={priceRange === '5to10'}
                      onChange={(e) => setPriceRange(e.target.value)}
                    />
                    <span>5 - 10 triệu</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="price" 
                      value="10to20"
                      checked={priceRange === '10to20'}
                      onChange={(e) => setPriceRange(e.target.value)}
                    />
                    <span>10 - 20 triệu</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="price" 
                      value="20to30"
                      checked={priceRange === '20to30'}
                      onChange={(e) => setPriceRange(e.target.value)}
                    />
                    <span>20 - 30 triệu</span>
                  </label>
                  <label className="filter-option">
                    <input 
                      type="radio" 
                      name="price" 
                      value="over30"
                      checked={priceRange === 'over30'}
                      onChange={(e) => setPriceRange(e.target.value)}
                    />
                    <span>Trên 30 triệu</span>
                  </label>
                </div>
              </div>

              {(priceRange !== 'all' || sortBy !== 'default') && (
                <button 
                  className="btn btn-secondary" 
                  style={{ width: '100%', marginTop: 12 }}
                  onClick={handleResetFilters}
                >
                  <i className="fa fa-refresh"></i>
                  Xóa bộ lọc
                </button>
              )}
            </aside>
          )}

          {/* Results Grid */}
          <div className="results-grid-container">
            {sortedResults.length === 0 ? (
              <div className="no-results">
                <i className="fa fa-search" style={{ fontSize: 48, marginBottom: 16, color: 'var(--muted)' }}></i>
                <h3>Không tìm thấy sản phẩm</h3>
                <p className="muted">Không có sản phẩm nào phù hợp với "{q}"</p>
                {(priceRange !== 'all' || sortBy !== 'default') && (
                  <button className="btn btn-primary" style={{ marginTop: 16 }} onClick={handleResetFilters}>
                    Xóa bộ lọc và thử lại
                  </button>
                )}
              </div>
            ) : (
              <section className="products-grid">
                {sortedResults.map(p => <ProductCard key={p.id} p={p} onQuickView={openQuickView} />)}
              </section>
            )}
          </div>
        </div>

        {/* Quick View Modal */}
        {modalOpen && modalProduct && (
          <div className="modal-overlay open" onClick={(e) => { if (e.target.className.includes('modal-overlay')) closeQuickView() }}>
            <div className="modal" role="dialog" aria-modal="true">
              <button className="modal-close" aria-label="Đóng" onClick={closeQuickView}>&times;</button>
              <div className="modal-body">
                <div className="modal-img">
                  <img src={modalProduct.image || modalProduct.images?.[0] || '/no-image.png'} alt={modalProduct.name} />
                </div>
                <div className="modal-info">
                  <h3>{modalProduct.name}</h3>
                  <div className="price-row">
                    <div className="price">{formatPrice(modalProduct.price)}</div>
                    {modalProduct.oldPrice && <div className="old">{formatPrice(modalProduct.oldPrice)}</div>}
                  </div>
                  <div className="muted" style={{ marginTop: 8, marginBottom: 12 }}>{modalProduct.desc}</div>
                  
                  {modalProduct.specs && (
                    <div style={{ marginBottom: 12, fontSize: 14 }}>
                      <div style={{ fontWeight: 700, marginBottom: 6 }}>Thông số nổi bật:</div>
                      {modalProduct.specs['Cấu hình & Bộ nhớ'] && (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                          {modalProduct.specs['Cấu hình & Bộ nhớ']['RAM'] && (
                            <div className="muted">• RAM: {modalProduct.specs['Cấu hình & Bộ nhớ']['RAM']}</div>
                          )}
                          {modalProduct.specs['Cấu hình & Bộ nhớ']['Dung lượng lưu trữ'] && (
                            <div className="muted">• Bộ nhớ: {modalProduct.specs['Cấu hình & Bộ nhớ']['Dung lượng lưu trữ']}</div>
                          )}
                        </div>
                      )}
                      {modalProduct.specs['Camera & Màn hình'] && (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                          {modalProduct.specs['Camera & Màn hình']['Camera chính'] && (
                            <div className="muted">• Camera: {modalProduct.specs['Camera & Màn hình']['Camera chính']}</div>
                          )}
                          {modalProduct.specs['Camera & Màn hình']['Màn hình'] && (
                            <div className="muted">• Màn hình: {modalProduct.specs['Camera & Màn hình']['Màn hình']}</div>
                          )}
                        </div>
                      )}
                      {modalProduct.specs['Pin & Sạc'] && modalProduct.specs['Pin & Sạc']['Dung lượng pin'] && (
                        <div className="muted">• Pin: {modalProduct.specs['Pin & Sạc']['Dung lượng pin']}</div>
                      )}
                    </div>
                  )}
                  
                  <div className="modal-actions">
                    <a href={`/product/${modalProduct.id}`} className="btn btn-primary">
                      Xem chi tiết
                    </a>
                    <button className="btn btn-secondary" onClick={closeQuickView}>Đóng</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </main>
  )
}