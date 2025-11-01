import React from 'react'
import { useNavigate } from "react-router-dom"

function formatPrice(v) {
    if (v === null || v === undefined || v === '') return ''
    try { return new Intl.NumberFormat('vi-VN').format(Number(v)) + '₫' }
    catch (e) { return String(v) + '₫' }
}

export default function ProductCard({ p, onQuickView }) {
    const navigate = useNavigate()
    return (
        <article className="product-card" role="article" aria-label={p.name}>
            <div className="img-wrap">
                <img loading="lazy" src={p.image || '/no-image.png'} alt={p.name}
                    onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = '/no-image.png' }} />
                {(p.tags || []).map((t, idx) => (
                    <span key={idx} className={`badge ${t}`}>{t === 'sale' ? 'Giảm giá' : (t === 'new' ? 'Mới' : t)}</span>
                ))}
            </div>
            <div className="product-info">
                <h4 className="product-title">{p.name}</h4>
                <div className="price-row">
                    {p.price ? <div className="price">{formatPrice(p.price)}</div> : (p.status === 'coming_soon' ? <div className="muted">Sắp mở bán</div> : null)}
                    {p.oldPrice ? <div className="old">{formatPrice(p.oldPrice)}</div> : null}
                </div>
                {p.status === 'coming_soon' ? (
                    <button className="btn" disabled>Sắp mở bán</button>
                ) : (
                    <>
                        <button className="btn btn-primary" onClick={() => navigate(`/product/${p.id}`)}>Mua ngay</button>
                        <button className="btn btn-secondary quick-view" onClick={() => onQuickView(p.id)}>Xem nhanh</button>
                    </>
                )}
            </div>
        </article>
    )
}
