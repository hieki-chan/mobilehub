import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import SearchBar from './SearchBar'
import useCart from '../hooks/useCart'

export default function Header() {
  const navigate = useNavigate()
  const [user, setUser] = useState(null)

  // safe cart count (fallback 0 nếu không có hook)
  let cartCount = 0
  try {
    const cartHook = useCart()
    if (cartHook && typeof cartHook.count === 'function') cartCount = cartHook.count()
  } catch (e) {
    cartCount = 0
  }

  useEffect(() => {
    // helper: read current user from localStorage
    const readUser = () => {
      const raw = localStorage.getItem('user')
      if (raw) {
        try {
          setUser(JSON.parse(raw))
        } catch {
          setUser(null)
        }
      } else {
        setUser(null)
      }
    }

    // đọc lần đầu
    readUser()

    // handler cho cả storage (cross-tab) và event custom (same-tab)
    const handler = () => readUser()

    window.addEventListener('storage', handler)        // khi thay đổi ở tab khác
    window.addEventListener('user-changed', handler)  // khi chúng ta dispatch trong cùng tab

    return () => {
      window.removeEventListener('storage', handler)
      window.removeEventListener('user-changed', handler)
    }
  }, [])

  const { clear } = useCart()
  const handleLogout = () => {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    // xóa cart local khi logout để không giữ giỏ của người trước
    clear()
    // (tùy chọn) remove per-user stored cart key if needed:
    // const user = JSON.parse(localStorage.getItem('user') || 'null')
    // if (user && user.id) localStorage.removeItem(`mh_cart_${user.id}`)
    window.dispatchEvent(new Event('user-changed'))
    navigate('/', { replace: true })
  }

  return (
    <>
      <header className="topbar" id="topbar" role="banner">
        <div className="container topbar-inner">
          <Link to="/" className="logo" aria-label="MobileHub - Trang chủ">
            <svg className="logo-mark" width="48" height="48" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg" aria-hidden>
              <rect width="120" height="120" rx="18" fill="#DD0000" />
              <g transform="translate(14,18)" fill="#ffffff">
                <circle cx="10" cy="10" r="6"></circle>
                <circle cx="34" cy="10" r="6"></circle>
                <circle cx="58" cy="10" r="6"></circle>
                <rect x="6" y="40" width="62" height="12" rx="6"></rect>
              </g>
            </svg>
            <div className="logo-text">
              <div className="brand">MobileHub</div>
              <div className="brand-sub">Điện thoại • Phụ kiện</div>
            </div>
          </Link>

          <div className="search-wrap" role="search" aria-label="Tìm sản phẩm">
            <SearchBar />
          </div>

          <nav className="top-actions" aria-label="Tài khoản và giỏ hàng">
            {user ? (
              <>
                <button className="action" onClick={() => navigate('/profile')} title="Xem hồ sơ">
                  <i className="fa fa-user"></i>
                  <span className="label">Xin chào, {user.name || user.email || 'Bạn'}</span>
                </button>

                <button className="action" onClick={handleLogout} title="Đăng xuất">
                  <i className="fa fa-right-from-bracket"></i>
                  <span className="label">Đăng xuất</span>
                </button>
              </>
            ) : (
              <Link to="/login" className="action" title="Đăng nhập">
                <i className="fa fa-user"></i>
                <span className="label">Đăng nhập</span>
              </Link>
            )}

            <Link to="/cart" className="action" aria-label="Giỏ hàng">
              <i className="fa fa-shopping-cart"></i>
              <span className="label">Giỏ hàng</span>
              {cartCount > 0 && <span className="cart-badge" aria-hidden>{cartCount}</span>}
            </Link>
          </nav>
        </div>
      </header>

      <nav className="main-nav" id="mainNav" role="navigation" aria-label="Main navigation">
        <div className="container nav-inner">
          <ul className="menu" role="menubar" aria-label="Danh mục">
            <li><a className="nav-item" href="#"><i className="fa fa-mobile-screen-button"></i><span>Điện thoại</span></a></li>
            <li><a className="nav-item" href="#"><i className="fa fa-tablet"></i><span>Tablet</span></a></li>
            <li><a className="nav-item" href="#"><i className="fa fa-headphones"></i><span>Phụ kiện</span></a></li>
            <li><a className="nav-item" href="#"><i className="fa fa-bolt"></i><span>Sạc &amp; Pin</span></a></li>
            <li><a className="nav-item" href="#"><i className="fa fa-tags"></i><span>Khuyến mãi</span></a></li>
            <li><a className="nav-item" href="#"><i className="fa fa-cog"></i><span>Dịch vụ</span></a></li>
          </ul>
        </div>
      </nav>
    </>
  )
}
