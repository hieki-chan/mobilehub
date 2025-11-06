// src/components/RelatedProducts.jsx
import React from 'react'
import ProductCard from '../components/home/ProductCard' // reuse existing component
export default function RelatedProducts({ products = [], currentId }) {
  const related = products.filter(p => p.id !== currentId).slice(0,4)
  return (
    <div className="related">
      <h3 className="section-title">Sản phẩm liên quan</h3>
      <div className="related-grid" id="relatedGrid" style={{ display:'grid', gridTemplateColumns:'repeat(auto-fill,minmax(220px,1fr))', gap:18 }}>
        {related.map(r => <ProductCard key={r.id} p={r} />)}
      </div>
    </div>
  )
}
