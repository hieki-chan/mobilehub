import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import useCart from '../hooks/useCart'
import { formatPrice } from '../utils/formatPrice'
import CartItem from '../components/cart/CartItem'
import '../styles/pages/cart.css'

import { getText } from 'number-to-text-vietnamese'

export default function Cart() {
  const navigate = useNavigate()
  const { cart, remove, updateQty, updateVariant, clear, total, count, loading, error } = useCart()

  useEffect(() => {
    document.title = 'Giỏ hàng | MobileHub'
  }, [])

  if (error) {
    return (
      <main className="cart-container" style={{ padding: 28, textAlign: 'center' }}>
        <h2>Lỗi: {error}</h2>
        <button className="btn btn-primary" onClick={() => window.location.reload()}>
          Thử lại
        </button>
      </main>
    )
  }

  if (!cart || cart.length === 0) {
    return (
      <main className="cart-container" style={{ padding: 28, textAlign: 'center' }}>
        <h2>Giỏ hàng của bạn trống</h2>
        <p className="muted">Chưa có sản phẩm nào trong giỏ. Hãy thêm vài món bạn thích.</p>
        <div style={{ marginTop: 18, display: 'flex', justifyContent: 'center', gap: 12 }}>
          <button className="btn btn-primary btn-lg" onClick={() => navigate('/')}>
            Quay về trang chủ
          </button>
          <button className="btn" onClick={() => navigate(-1)}>
            Tiếp tục xem
          </button>
        </div>
      </main>
    )
  }

  const cartTotal = total()
  const cartCount = count()

  let totalInWords = ''
  const [intPart, decimalPart] = cartTotal.toString().split('.')

  totalInWords = getText(parseInt(intPart))

  if (decimalPart && parseInt(decimalPart) > 0) {
    const decimalText = decimalPart
      .split('')
      .map(d => getText(parseInt(d)))
      .join(' ')
    totalInWords += ' phẩy ' + decimalText
  }

  const totalInWordsFormatted = totalInWords.charAt(0).toUpperCase() + totalInWords.slice(1)

  return (
    <main className="cart-container" style={{ padding: 20, position: 'relative' }}>
      <h3 className="section-title">Giỏ hàng ({cartCount} sản phẩm)</h3>

      <div className="cart-items">
        {cart.map(item => (
          <CartItem
            key={item.id}
            item={item}
            loading={loading}
            remove={remove}
            updateQty={updateQty}
            updateVariant={updateVariant}
          />
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

            <div style={{ marginTop: 8, fontStyle: 'italic', color: '#555', fontSize: 14 }}>
              ({totalInWordsFormatted} đồng)
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
                onClick={() => {
                  if (confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')) clear()
                }}
              >
                Xóa giỏ
              </button>

              <button
                className="btn"
                style={{ flex: 1 }}
                disabled={loading}
                onClick={() => navigate('/')}
              >
                Tiếp tục mua sắm
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  )
}
