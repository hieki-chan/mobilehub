// src/pages/Profile.jsx
import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import useFav from '../hooks/useFav'
import useCart from '../hooks/useCart'
import { products as mockProducts } from '../data/products'
import { formatPrice } from '../utils/formatPrice'
import '../styles/pages/profile.css'
import { logout } from '../api/AuthApi'

// --- Component con: Hiển thị & Sửa thông tin User ---
const UserInfo = ({ user, onUpdateUser }) => {
  const [editing, setEditing] = useState(false)
  const [formData, setFormData] = useState({
    name: user.name || '',
    email: user.email || '',
    phone: user.phone || '',
    address: user.address || ''
  })
  const [errors, setErrors] = useState({})

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  const validate = () => {
    const newErrors = {}
    if (!formData.name.trim()) newErrors.name = 'Vui lòng nhập họ tên'
    if (!formData.email.trim()) newErrors.email = 'Vui lòng nhập email'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email.trim())) {
      newErrors.email = 'Email không hợp lệ'
    }
    if (formData.phone && !/^[0-9]{10,11}$/.test(formData.phone.trim())) {
      newErrors.phone = 'Số điện thoại không hợp lệ'
    }
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!validate()) return

    // Cập nhật user
    const updatedUser = { ...user, ...formData }
    localStorage.setItem('user', JSON.stringify(updatedUser))
    window.dispatchEvent(new Event('user-changed'))
    onUpdateUser(updatedUser)
    setEditing(false)
    alert('Cập nhật thông tin thành công!')
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h3>Thông tin tài khoản</h3>
        {!editing && (
          <button className="btn btn-secondary" onClick={() => setEditing(true)}>
            <i className="fa fa-edit"></i> Chỉnh sửa
          </button>
        )}
      </div>

      <form onSubmit={handleSubmit}>
        <label className="field">
          <div className="label">Họ tên <span className="required">*</span></div>
          <input 
            type="text" 
            name="name"
            value={formData.name} 
            onChange={handleChange}
            disabled={!editing}
          />
          {errors.name && <div className="field-error">{errors.name}</div>}
        </label>

        <label className="field">
          <div className="label">Email <span className="required">*</span></div>
          <input 
            type="email" 
            name="email"
            value={formData.email} 
            onChange={handleChange}
            disabled={!editing}
          />
          {errors.email && <div className="field-error">{errors.email}</div>}
        </label>

        <label className="field">
          <div className="label">Số điện thoại</div>
          <input 
            type="tel" 
            name="phone"
            value={formData.phone} 
            onChange={handleChange}
            placeholder="0912345678"
            disabled={!editing}
          />
          {errors.phone && <div className="field-error">{errors.phone}</div>}
        </label>

        <label className="field">
          <div className="label">Địa chỉ</div>
          <textarea 
            name="address"
            value={formData.address} 
            onChange={handleChange}
            placeholder="Nhập địa chỉ của bạn"
            rows="3"
            disabled={!editing}
          />
        </label>

        {editing && (
          <div style={{ display: 'flex', gap: 10, marginTop: 16 }}>
            <button type="submit" className="btn btn-primary">
              <i className="fa fa-save"></i> Lưu thay đổi
            </button>
            <button 
              type="button" 
              className="btn btn-secondary" 
              onClick={() => {
                setFormData({
                  name: user.name || '',
                  email: user.email || '',
                  phone: user.phone || '',
                  address: user.address || ''
                })
                setErrors({})
                setEditing(false)
              }}
            >
              Hủy
            </button>
          </div>
        )}
      </form>
    </div>
  )
}

// --- Component con: Lịch sử đơn hàng ---
const OrderHistory = () => {
  const navigate = useNavigate()
  const [orders, setOrders] = useState([])
  const [selectedOrder, setSelectedOrder] = useState(null)

  useEffect(() => {
    // Lấy đơn hàng từ localStorage
    const storedOrders = JSON.parse(localStorage.getItem('orders') || '[]')
    // Sắp xếp theo thời gian mới nhất
    const sorted = storedOrders.sort((a, b) => 
      new Date(b.createdAt) - new Date(a.createdAt)
    )
    setOrders(sorted)
  }, [])

  const getStatusBadge = (status) => {
    const statusMap = {
      pending: { label: 'Chờ xác nhận', class: 'status-pending' },
      confirmed: { label: 'Đã xác nhận', class: 'status-confirmed' },
      shipping: { label: 'Đang giao', class: 'status-shipping' },
      delivered: { label: 'Đã giao', class: 'status-delivered' },
      cancelled: { label: 'Đã hủy', class: 'status-cancelled' }
    }
    const info = statusMap[status] || statusMap.pending
    return <span className={`order-status ${info.class}`}>{info.label}</span>
  }

  const formatDate = (dateStr) => {
    const date = new Date(dateStr)
    return date.toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  if (orders.length === 0) {
    return (
      <div>
        <h3>Lịch sử đơn hàng</h3>
        <div className="empty-state">
          <i className="fa fa-receipt"></i>
          <div>Bạn chưa có đơn hàng nào</div>
          <button className="btn btn-primary" onClick={() => navigate('/')}>
            Khám phá sản phẩm
          </button>
        </div>
      </div>
    )
  }

  return (
    <div>
      <h3>Lịch sử đơn hàng ({orders.length})</h3>
      <div className="orders-list">
        {orders.map(order => (
          <div key={order.id} className="order-card">
            <div className="order-header">
              <div className="order-info">
                <div className="order-id">
                  <strong>Mã đơn:</strong> {order.id}
                </div>
                <div className="order-date">{formatDate(order.createdAt)}</div>
              </div>
              {getStatusBadge(order.status)}
            </div>

            <div className="order-items">
              {order.items.slice(0, 2).map((item, idx) => (
                <div key={idx} className="order-item-preview">
                  <img src={item.image || '/no-image.png'} alt={item.name} />
                  <div className="order-item-info">
                    <div className="order-item-name">{item.name}</div>
                    <div className="order-item-meta">
                      {item.capacity && <span>{item.capacity}</span>}
                      {item.color && <span> • {item.color}</span>}
                      <span> • SL: {item.qty}</span>
                    </div>
                  </div>
                  <div className="order-item-price">
                    {formatPrice(item.price * item.qty)}
                  </div>
                </div>
              ))}
              {order.items.length > 2 && (
                <div className="order-more-items">
                  và {order.items.length - 2} sản phẩm khác
                </div>
              )}
            </div>

            <div className="order-footer">
              <div className="order-total">
                <span>Tổng tiền:</span>
                <strong>{formatPrice(order.total)}</strong>
              </div>
              <button 
                className="btn btn-secondary btn-sm"
                onClick={() => setSelectedOrder(order)}
              >
                Xem chi tiết
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Modal chi tiết đơn hàng */}
      {selectedOrder && (
        <div className="modal-overlay open" onClick={() => setSelectedOrder(null)}>
          <div className="modal order-detail-modal" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setSelectedOrder(null)}>&times;</button>
            <div className="modal-body">
              <h3>Chi tiết đơn hàng</h3>
              
              <div className="order-detail-section">
                <h4>Thông tin đơn hàng</h4>
                <div className="order-detail-row">
                  <span>Mã đơn:</span>
                  <strong>{selectedOrder.id}</strong>
                </div>
                <div className="order-detail-row">
                  <span>Ngày đặt:</span>
                  <span>{formatDate(selectedOrder.createdAt)}</span>
                </div>
                <div className="order-detail-row">
                  <span>Trạng thái:</span>
                  {getStatusBadge(selectedOrder.status)}
                </div>
              </div>

              <div className="order-detail-section">
                <h4>Thông tin người nhận</h4>
                <div className="order-detail-row">
                  <span>Họ tên:</span>
                  <span>{selectedOrder.customerInfo.fullName}</span>
                </div>
                <div className="order-detail-row">
                  <span>Số điện thoại:</span>
                  <span>{selectedOrder.customerInfo.phone}</span>
                </div>
                <div className="order-detail-row">
                  <span>Email:</span>
                  <span>{selectedOrder.customerInfo.email}</span>
                </div>
                <div className="order-detail-row">
                  <span>Địa chỉ:</span>
                  <span>{selectedOrder.customerInfo.address}</span>
                </div>
              </div>

              <div className="order-detail-section">
                <h4>Sản phẩm</h4>
                {selectedOrder.items.map((item, idx) => (
                  <div key={idx} className="order-detail-item">
                    <img src={item.image || '/no-image.png'} alt={item.name} />
                    <div className="order-detail-item-info">
                      <div className="order-detail-item-name">{item.name}</div>
                      <div className="order-detail-item-meta">
                        {item.capacity && <span>{item.capacity}</span>}
                        {item.color && <span> • {item.color}</span>}
                      </div>
                      <div>SL: {item.qty}</div>
                    </div>
                    <div className="order-detail-item-price">
                      {formatPrice(item.price * item.qty)}
                    </div>
                  </div>
                ))}
              </div>

              <div className="order-detail-section">
                <h4>Thanh toán</h4>
                <div className="order-detail-row">
                  <span>Tạm tính:</span>
                  <span>{formatPrice(selectedOrder.subtotal)}</span>
                </div>
                <div className="order-detail-row">
                  <span>Phí vận chuyển:</span>
                  <span>{selectedOrder.shippingFee === 0 ? 'Miễn phí' : formatPrice(selectedOrder.shippingFee)}</span>
                </div>
                <div className="order-detail-row order-detail-total">
                  <span>Tổng cộng:</span>
                  <strong>{formatPrice(selectedOrder.total)}</strong>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// --- Component con: Danh sách yêu thích ---
const Wishlist = () => {
  const navigate = useNavigate()
  const { fav, toggle } = useFav()
  const pool = (window.__MOCK_PRODUCTS__ && window.__MOCK_PRODUCTS__.length) ? window.__MOCK_PRODUCTS__ : mockProducts
  const favProducts = pool.filter(p => fav.includes(p.id))

  if (favProducts.length === 0) {
    return (
      <div>
        <h3>Sản phẩm yêu thích</h3>
        <div className="empty-state">
          <i className="fa fa-heart"></i>
          <div>Bạn chưa yêu thích sản phẩm nào</div>
          <button className="btn btn-primary" onClick={() => navigate('/')}>
            Khám phá sản phẩm
          </button>
        </div>
      </div>
    )
  }

  return (
    <div>
      <h3>Sản phẩm yêu thích ({favProducts.length})</h3>
      <div className="wishlist-grid">
        {favProducts.map(p => (
          <div key={p.id} className="wishlist-item">
            <button 
              className="wishlist-remove"
              onClick={() => toggle(p.id)}
              title="Bỏ yêu thích"
            >
              <i className="fa fa-times"></i>
            </button>
            <img 
              src={p.image || p.images?.[0] || '/no-image.png'} 
              alt={p.name}
              onClick={() => navigate(`/product/${p.id}`)}
            />
            <div className="wishlist-info">
              <div 
                className="wishlist-name"
                onClick={() => navigate(`/product/${p.id}`)}
              >
                {p.name}
              </div>
              <div className="wishlist-price">
                {p.price ? formatPrice(p.price) : 'Liên hệ'}
              </div>
              <button 
                className="btn btn-primary btn-sm"
                onClick={() => navigate(`/product/${p.id}`)}
              >
                Xem sản phẩm
              </button>
            </div>
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
      navigate('/login', { replace: true })
    }
  }, [navigate])

  // 3. Xử lý Logout
  const handleLogout = () => {
    logout();
    clear()
    window.dispatchEvent(new Event('user-changed'))
    navigate('/', { replace: true })
  }

  // 4. Xử lý cập nhật user
  const handleUpdateUser = (updatedUser) => {
    setUser(updatedUser)
  }
  
  // Nếu chưa kịp check user, hiển thị loading
  if (!user) {
    return (
      <main className="container main-content" style={{paddingTop: 20, textAlign: 'center'}}>
        <div className="loading-spinner">
          <i className="fa fa-spinner fa-spin" style={{fontSize: 32}}></i>
          <div style={{marginTop: 12}}>Đang tải...</div>
        </div>
      </main>
    )
  }

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
        {view === 'info' && <UserInfo user={user} onUpdateUser={handleUpdateUser} />}
        {view === 'orders' && <OrderHistory />}
        {view === 'wishlist' && <Wishlist />}
      </section>
    </main>
  )
}