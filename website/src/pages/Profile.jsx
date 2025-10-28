// src/pages/Profile.jsx
import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import useFav from '../hooks/useFav'
import useCart from '../hooks/useCart'
import { products as mockProducts } from '../data/products'
import { formatPrice } from '../utils/formatPrice'
import '../styles/pages/profile.css'

// --- Component con: Hiển thị thông tin User ---
const UserInfo = ({ user }) => {
  return (
    <div>
      <h3>Thông tin tài khoản</h3>
      <label className="field">
        <div className="label">Họ tên</div>
        <input type="text" value={user.name || ''} readOnly disabled />
      </label>
      <label className="field">
        <div className="label">Email</div>
        <input type="email" value={user.email || ''} readOnly disabled />
      </label>
      <label className="field">
        <div className="label">Số điện thoại</div>
        <input type="tel" value={user.phone || '(Chưa có)'} readOnly disabled />
      </label>
      <button className="btn" disabled>Cập nhật (Demo)</button>
    </div>
  )
}

// --- Component con: Lịch sử đơn hàng (Placeholder) ---
const OrderHistory = () => {
  return (
    <div>
      <h3>Lịch sử đơn hàng</h3>
      <div className="muted" style={{textAlign: 'center', padding: '30px'}}>
        <i className="fa fa-receipt" style={{fontSize: 24, marginBottom: 10}}></i>
        <div>Bạn chưa có đơn hàng nào (Demo).</div>
      </div>
      {/* Logic render đơn hàng thật sẽ ở đây */}
    </div>
  )
}

// --- Component con: Danh sách yêu thích ---
const Wishlist = () => {
  const { fav } = useFav()
  const pool = (window.__MOCK_PRODUCTS__ && window.__MOCK_PRODUCTS__.length) ? window.__MOCK_PRODUCTS__ : mockProducts
  const favProducts = pool.filter(p => fav.includes(p.id))

  if (favProducts.length === 0) {
    return (
      <div>
        <h3>Sản phẩm yêu thích</h3>
        <div className="muted" style={{textAlign: 'center', padding: '30px'}}>
          <i className="fa fa-heart" style={{fontSize: 24, marginBottom: 10}}></i>
          <div>Bạn chưa yêu thích sản phẩm nào.</div>
        </div>
      </div>
    )
  }
  return (
    <div>
      <h3>Sản phẩm yêu thích ({favProducts.length})</h3>
      <div style={{display: 'flex', flexDirection: 'column', gap: '12px'}}>
        {favProducts.map(p => (
          <div key={p.id} className="wishlist-item">
            <img src={p.image || p.images?.[0] || '/no-image.png'} alt={p.name} />
            <div>
              <div className="name">{p.name}</div>
              <div className="price">{p.price ? formatPrice(p.price) : 'Liên hệ'}</div>
            </div>
            <button className="btn btn-primary" style={{marginLeft: 'auto'}}>Mua ngay</button>
          </div>
        ))}
      </div>
    </div>
  )
}


// --- Component Trang Profile chính ---
export default function Profile() {
  const navigate = useNavigate()
  const { clear } = useCart()
  const [user, setUser] = useState(null)
  const [view, setView] = useState('info') // 'info', 'orders', 'wishlist'

  // 1. Lấy thông tin user từ localStorage
  useEffect(() => {
    const raw = localStorage.getItem('user')
    if (raw) {
      try { setUser(JSON.parse(raw)) } catch { setUser(null) }
    }
  }, [])

  // 2. Bảo vệ route: Nếu không có user (hoặc token), đá về login
  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      // Dùng replace: true để người dùng không "back" lại trang profile sau khi bị đá về login
      navigate('/login', { replace: true })
    }
  }, [navigate])

  // 3. Xử lý Logout (giống Header)
  const handleLogout = () => {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    clear() // Xóa giỏ hàng khi logout
    window.dispatchEvent(new Event('user-changed')) // Báo cho Header
    // window.dispatchEvent(new Event('cart-changed')) // clear() đã làm điều này
    navigate('/', { replace: true })
  }
  
  // Nếu chưa kịp check user, hiển thị loading
  if (!user) {
    return <main className="container main-content" style={{paddingTop: 20, textAlign: 'center'}}>Đang tải...</main>
  }

  // 4. Render giao diện
  return (
    <main className="profile-page">
      {/* Sidebar Menu */}
      <aside className="profile-sidebar">
        <div className="welcome">
          <strong>Xin chào,</strong>
          <div className="muted">{user.name || user.email}</div>
        </div>
        <ul className="profile-nav">
          <li>
            <button className={view === 'info' ? 'active' : ''} onClick={() => setView('info')}>
              <i className="fa fa-user"></i> <span>Thông tin tài khoản</span>
            </button>
          </li>
          <li>
            <button className={view === 'orders' ? 'active' : ''} onClick={() => setView('orders')}>
              <i className="fa fa-receipt"></i> <span>Quản lý đơn hàng</span>
            </button>
          </li>
          <li>
            <button className={view === 'wishlist' ? 'active' : ''} onClick={() => setView('wishlist')}>
              <i className="fa fa-heart"></i> <span>Sản phẩm yêu thích</span>
            </button>
          </li>
          <li>
            <button className="logout" onClick={handleLogout}>
              <i className="fa fa-right-from-bracket"></i> <span>Đăng xuất</span>
            </button>
          </li>
        </ul>
      </aside>

      {/* Content Area */}
      <section className="profile-content">
        {view === 'info' && <UserInfo user={user} />}
        {view === 'orders' && <OrderHistory />}
        {view === 'wishlist' && <Wishlist />}
      </section>
    </main>
  )
}