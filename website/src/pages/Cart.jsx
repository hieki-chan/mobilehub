// src/pages/Cart.jsx
import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import useCart from '../hooks/useCart'
import { formatPrice } from '../utils/formatPrice'
import { products as mockProducts } from '../data/products'
import '../styles/product-detail.css'
import '../styles/cart.css'

export default function Cart() {
    const navigate = useNavigate()
    const { cart, remove, updateQty, clear, total, count } = useCart()
    useEffect(() => {
        console.log('Cart mounted')
        document.title = 'Giỏ hàng | MobileHub'
    }, [])

    // find product meta (image...) from mockProducts if available
    const withMeta = cart.map(item => {
        const meta = (mockProducts || []).find(p => String(p.id) === String(item.id)) || {}
        return { ...item, image: item.image || meta.images?.[0] || meta.image || null }
    })

    if (!cart || cart.length === 0) {
        // empty cart UI
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

    return (
        <main className="cart-container" style={{ padding: 20, position: 'relative' }}>
            <h3 className="section-title">Giỏ hàng ({count()} sản phẩm)</h3>

            <div className="cart-items">
                {withMeta.map((it, idx) => (
                    <div key={idx} className="cart-item">
                        <div className="cart-item-left">
                            <img src={'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/311178/asus-vivobook-go-15-e1504fa-r5-nj776w-140225-100949-251-400x400.jpg'} alt={it.name} className="cart-item-image" />
                        </div>
                        <div className="cart-item-right">
                            <div className="cart-item-info">
                                <a href="/" className="cart-item-info-name">{it.name}</a>
                                <div className="cart-item-info-price">
                                    <div className="old-price">{formatPrice(it.oldPrice)}</div>
                                    <div className="new-price">{formatPrice(it.price)}</div>
                                </div>
                            </div>
                            <div className="cart-item-info-other">
                                {it.capacity && <div className="cart-item-variant">Dung lượng: {it.capacity}</div>}
                                {it.color && <div className="cart-item-variant">Màu sắc: {it.color}</div>}
                            </div>
                            <div className="cart-item-quantity">
                                <button className="cart-item-quantity-remove" onClick={() => {
                                    if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ?')) remove(it)
                                }}>Xóa</button>
                                <div className="cart-item-quantity-btn">
                                    <button
                                        className="minus-btn"
                                        aria-label="Giảm"
                                        onClick={() => updateQty({ id: it.id, capacity: it.capacity, color: it.color }, (it.qty || 1) - 1)}
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
                            <div>{formatPrice(total())}</div>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                            <div className="muted">Phí vận chuyển</div>
                            <div className="muted">Miễn phí</div>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 750, fontSize: 18 }}>
                            <div>Tổng cộng</div>
                            <div>{formatPrice(total())}</div>
                        </div>

                        <div style={{ marginTop: 20 }}>
                            <button className="btn btn-primary btn-xl" style={{ width: '100%', height: '40px' }} onClick={() => {
                                if (!cart || cart.length === 0) return
                                // demo checkout
                                alert('Checkout demo — chuyển sang trang thanh toán')
                                navigate('/checkout')
                            }}>
                                Thanh toán ({formatPrice(total())})
                            </button>
                        </div>

                        <div style={{ marginTop: 12, display: 'flex', gap: 8 }}>
                            <button className="btn" style={{ flex: 1 }} onClick={() => clear()}>Xóa giỏ</button>
                            <button className="btn" style={{ flex: 1 }} onClick={() => navigate('/')}>Tiếp tục mua sắm</button>
                        </div>
                    </div>
                </div>

            </div>
        </main>
    )
}
