import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import SearchBar from './SearchBar'
import useCart from '../hooks/useCart'

export default function Header() {
  const navigate = useNavigate()
  const [user, setUser] = useState(null)
  const [megaMenuOpen, setMegaMenuOpen] = useState(false)

  // safe cart count
  let cartCount = 0
  try {
    const cartHook = useCart()
    if (cartHook && typeof cartHook.count === 'function') cartCount = cartHook.count()
  } catch (e) {
    cartCount = 0
  }

  useEffect(() => {
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

    readUser()
    const handler = () => readUser()

    window.addEventListener('storage', handler)
    window.addEventListener('user-changed', handler)

    return () => {
      window.removeEventListener('storage', handler)
      window.removeEventListener('user-changed', handler)
    }
  }, [])

  const { clear } = useCart()
  const handleLogout = () => {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    clear()
    window.dispatchEvent(new Event('user-changed'))
    navigate('/', { replace: true })
  }

  const phoneCategories = [
    { label: 'iPhone', path: '/search?q=iPhone' },
    { label: 'Samsung', path: '/search?q=Samsung' },
    { label: 'Oppo', path: '/search?q=Oppo' },
    { label: 'Xiaomi', path: '/search?q=Xiaomi' },
    { label: 'Vivo', path: '/search?q=Vivo' },
    { label: 'Realme', path: '/search?q=Realme' },
  ]

  return (
    <>
      <header className="topbar" id="topbar" role="banner">
        <div className="container topbar-inner">
          <Link to="/" className="logo" aria-label="MobileHub - Trang chủ">
            <svg className="logo-mark" width="48" height="48" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
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
                  <span className="action-label">Xin chào, {user.name || user.email || 'Bạn'}</span>
                </button>

                <button className="action" onClick={handleLogout} title="Đăng xuất">
                  <i className="fa fa-right-from-bracket"></i>
                  <span className="action-label">Đăng xuất</span>
                </button>
              </>
            ) : (
              <Link to="/login" className="action" title="Đăng nhập">
                <i className="fa fa-user"></i>
                <span className="action-label">Đăng nhập</span>
              </Link>
            )}

            <Link to="/cart" className="action" aria-label="Giỏ hàng">
              <i className="fa fa-shopping-cart"></i>
              <span className="action-label">Giỏ hàng</span>
              {cartCount > 0 && <span className="cart-badge" aria-hidden="true">{cartCount}</span>}
            </Link>
          </nav>
        </div>
      </header>

      <nav className="main-nav" id="mainNav" role="navigation" aria-label="Main navigation">
        <div className="container nav-inner">
          <ul className="menu" role="menubar" aria-label="Danh mục">
            <li 
              className="menu-item-with-submenu"
              onMouseEnter={() => setMegaMenuOpen(true)}
              onMouseLeave={() => setMegaMenuOpen(false)}
            >
              <div className="nav-item-wrapper">
                <Link to="/search?q=Điện thoại" className="nav-item">
                  <i className="fa fa-mobile-screen-button"></i>
                  <span>Điện thoại</span>
                  <i className="fa fa-chevron-down" style={{ fontSize: 10, marginLeft: 4 }}></i>
                </Link>
                
                {megaMenuOpen && (
                  <div 
                    className="mega-menu"
                    onMouseEnter={() => setMegaMenuOpen(true)}
                    onMouseLeave={() => setMegaMenuOpen(false)}
                  >
                    <div className="mega-menu-content">
                      <div className="mega-menu-section">
                        <h4>Thương hiệu</h4>
                        <ul>
                          {phoneCategories.map((cat, idx) => (
                            <li key={idx}>
                              <Link to={cat.path} onClick={() => setMegaMenuOpen(false)}>
                                {cat.label}
                              </Link>
                            </li>
                          ))}
                        </ul>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </li>
            
            <li><Link to="/search?q=Tablet" className="nav-item"><i className="fa fa-tablet"></i><span>Tablet</span></Link></li>
            <li><Link to="/search?q=Phụ kiện" className="nav-item"><i className="fa fa-headphones"></i><span>Phụ kiện</span></Link></li>
            <li><Link to="/search?q=Sạc Pin" className="nav-item"><i className="fa fa-bolt"></i><span>Sạc &amp; Pin</span></Link></li>
            <li><Link to="/search?q=Khuyến mãi" className="nav-item"><i className="fa fa-tags"></i><span>Khuyến mãi</span></Link></li>
            <li><Link to="/search?q=Dịch vụ" className="nav-item"><i className="fa fa-cog"></i><span>Dịch vụ</span></Link></li>
          </ul>
        </div>
      </nav>
    </>
  )
}