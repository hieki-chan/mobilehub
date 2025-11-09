// src/pages/Cart.jsx
import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import useCart from '../hooks/useCart'
import { formatPrice } from '../utils/formatPrice'
import { products as mockProducts } from '../data/products'

import '../styles/pages/cart.css'

export default function Cart() {
    const navigate = useNavigate()
    const userId = localStorage.getItem("userId")
    const { cart, remove, updateQty, clear, total, count, loading, error } = useCart(userId)

    useEffect(() => {
        document.title = 'Giỏ hàng | MobileHub'
    }, [])

    if (loading) {
        return (
            <main className="cart-container" style={{ padding: 28, textAlign: 'center' }}>
                <h2>Đang tải giỏ hàng...</h2>
            </main>
        )
    }

    if (error) {
        return (
            <main className="cart-container" style={{ padding: 28, textAlign: 'center' }}>
                <h2>Lỗi: {error}</h2>
                <button className="btn btn-primary" onClick={() => window.location.reload()}>Thử lại</button>
            </main>
        )
    }

    if (!cart || cart.length === 0) {
        return (
            <main className="cart-container" style={{ padding: 28, textAlign: 'center' }}>
                <h2>Giỏ hàng của bạn trống</h2>
                <p className="muted">Chưa có sản phẩm nào trong giỏ. Hãy thêm vài món bạn thích.</p>
                <div style={{ marginTop: 18, display: 'flex', justifyContent: 'center', gap: 12 }}>
                    <button className="btn btn-primary btn-lg" onClick={() => navigate('/')}>Quay về trang chủ</button>
                    <button className="btn" onClick={() => navigate(-1)}>Tiếp tục xem</button>
                </div>
            </main>
        )
    }

    const withMeta = cart.map(item => {
        const meta = (mockProducts || []).find(p => String(p.id) === String(item.id)) || {}
        return {
            ...item,
            image: item.image || meta.images?.[0] || meta.image || null,
            price: Number(item.price) || 0,
            qty: Number(item.qty) || 1
        }
    })

    const getItemTotal = (item) => (item.price || 0) * (item.qty || 1)
    const cartTotal = withMeta.reduce((sum, item) => sum + getItemTotal(item), 0)

    return (
        <main className="cart-container" style={{ padding: 20, position: 'relative' }}>
            <h3 className="section-title">Giỏ hàng ({count()} sản phẩm)</h3>

            <div className="cart-items">
                {withMeta.map((it, idx) => (
                    <div key={idx} className="cart-item">
                        <div className="cart-item-left">
                            <img
                                src={it.image || 'https://via.placeholder.com/80x80?text=No+Image'}
                                alt={it.name}
                                className="cart-item-image"
                                onError={(e) => e.target.src = 'https://via.placeholder.com/80x80?text=No+Image'}
                            />
                        </div>
                        <div className="cart-item-right">
                            <div className="cart-item-info">
                                <a href={`/product/${it.id}`} className="cart-item-info-name">{it.name}</a>
                                <div className="cart-item-info-price">
                                    {it.oldPrice && <div className="old-price">{formatPrice(it.oldPrice)}</div>}
                                    <div className="new-price">{formatPrice(it.price)}</div>
                                </div>
                            </div>
                            <div className="cart-item-info-other">
                                {it.capacity && <div className="cart-item-variant">Dung lượng: {it.capacity}</div>}
                                {it.color && <div className="cart-item-variant">Màu sắc: {it.color}</div>}
                            </div>
                            <div className="cart-item-quantity">
                                <button
                                    className="cart-item-quantity-remove"
                                    disabled={loading}
                                    onClick={() => {
                                        if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ?')) remove(it)
                                    }}
                                >
                                    Xóa
                                </button>
                                <div className="cart-item-quantity-btn">
                                    <button
                                        className="minus-btn"
                                        disabled={loading}
                                        aria-label="Giảm"
                                        onClick={() => {
                                            const newQty = (it.qty || 1) - 1
                                            if (newQty <= 0) {
                                                if (confirm('Bạn có chắc muốn xóa sản phẩm này?')) remove(it)
                                            } else {
                                                updateQty({ id: it.id, capacity: it.capacity, color: it.color }, newQty)
                                            }
                                        }}
                                    >
                                        -
                                    </button>
                                    <input
                                        type="text"
                                        inputMode="numeric"
                                        pattern="^[1-9][0-9]*$"
                                        className="no-spinners"
                                        value={it.qty || 1}
                                        readOnly
                                    />
                                    <button
                                        className="add-btn"
                                        disabled={loading}
                                        aria-label="Tăng"
                                        onClick={() => updateQty({ id: it.id, capacity: it.capacity, color: it.color }, (it.qty || 1) + 1)}
                                    >
                                        +
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}

                <div className="cart-summary-wrapper">
                    <div className="cart-summary">
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                            <div className="muted">Tạm tính</div>
                            <div>{formatPrice(cartTotal)}</div>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                            <div className="muted">Phí vận chuyển</div>
                            <div className="muted">Miễn phí</div>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 750, fontSize: 18 }}>
                            <div>Tổng cộng</div>
                            <div>{formatPrice(cartTotal)}</div>
                        </div>

                        <div style={{ marginTop: 20 }}>
                            <button
                                className="btn btn-primary btn-xl"
                                style={{ width: '100%', height: '40px' }}
                                disabled={loading || cart.length === 0}
                                onClick={() => navigate('/checkout')}
                            >
                                {loading ? 'Đang xử lý...' : `Thanh toán (${formatPrice(cartTotal)})`}
                            </button>
                        </div>

                        <div style={{ marginTop: 12, display: 'flex', gap: 8 }}>
                            <button
                                className="btn"
                                style={{ flex: 1 }}
                                disabled={loading || cart.length === 0}
                                onClick={() => { if (confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')) clear() }}
                            >
                                Xóa giỏ
                            </button>
                            <button className="btn" style={{ flex: 1 }} disabled={loading} onClick={() => navigate('/')}>Tiếp tục mua sắm</button>
                        </div>
                    </div>
                </div>

            </div>
        </main>
    )
}
    