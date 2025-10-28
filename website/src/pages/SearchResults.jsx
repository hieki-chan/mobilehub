import React, { useMemo } from 'react'
import { useLocation } from 'react-router-dom'
import { products as mockProducts } from '../data/products'
import ProductCard from '../components/ProductCard'
import '../styles/home.css'

function useQuery() {
  return new URLSearchParams(useLocation().search)
}

export default function SearchResults() {
  const q = useQuery().get('q') || ''
  const term = q.trim().toLowerCase()

  const results = useMemo(() => {
    if (!term) return []
    return (mockProducts || []).filter(p => p.name.toLowerCase().includes(term))
  }, [term])

  return (
    <main className="container main-content" style={{ paddingTop: 18 }}>
      <h3 className="section-title">Kết quả tìm kiếm</h3>
      <p className="muted">Tìm cho: <strong>{q}</strong> · {results.length} kết quả</p>

      {results.length === 0 ? (
        <div style={{ marginTop: 24, textAlign: 'center', padding: 28, background:'#fff', borderRadius:12 }}>
          <div className="muted">Không tìm thấy sản phẩm phù hợp.</div>
        </div>
      ) : (
        <section className="products-grid" style={{ marginTop: 16 }}>
          {results.map(p => <ProductCard key={p.id} p={p} />)}
        </section>
      )}
    </main>
  )
}
