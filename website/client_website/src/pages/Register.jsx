import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import GoogleLogin from '../components/GoogleLogin'
import '../styles/pages/login.css'

// Mock register function
async function mockRegister({ email, password, confirmPassword }) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!email || email.indexOf('@') === -1) {
        return reject(new Error('Email không hợp lệ'))
      }
      if (!password || password.length < 6) {
        return reject(new Error('Mật khẩu phải có ít nhất 6 ký tự'))
      }
      if (password !== confirmPassword) {
        return reject(new Error('Mật khẩu xác nhận không khớp'))
      }
      
      const name = email.split('@')[0].replace(/[^\w]/g, '')
      resolve({
        user: { 
          id: 'u_' + Date.now(), 
          name: name.charAt(0).toUpperCase() + name.slice(1), 
          email 
        },
        token: 'demo-token-' + Math.random().toString(36).slice(2, 10)
      })
    }, 500)
  })
}

export default function Register() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [remember, setRemember] = useState(true)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onLoginSuccess = ({ user, token }) => {
    localStorage.setItem('user', JSON.stringify(user))
    if (remember) localStorage.setItem('token', token)
    
    navigate('/')
    window.dispatchEvent(new Event('user-changed'))
  }

  const submitRegister = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    
    try {
      const res = await mockRegister({ 
        email: email.trim(), 
        password, 
        confirmPassword 
      })
      onLoginSuccess(res)
    } catch (err) {
      setError(err.message || 'Đăng ký thất bại')
      setLoading(false)
    }
  }

  return (
    <main className="login-page">
      <div className="login-container">
        {/* Left Side - Illustration */}
        

        {/* Right Side - Register Form */}
        <div className="login-card">
          <h2 className="login-title">Đăng ký tài khoản</h2>
          <p className="muted">Tạo tài khoản mới để bắt đầu 🚀</p>

          {error && <div className="form-error" role="alert">{error}</div>}

          <form onSubmit={submitRegister} className="login-form" noValidate>
            <label className="field">
              <div className="label">Email</div>
              <input 
                type="email" 
                value={email} 
                onChange={e => setEmail(e.target.value)} 
                placeholder="example@email.com"
                required 
                autoComplete="email"
                disabled={loading}
              />
            </label>

            <label className="field">
              <div className="label">Mật khẩu</div>
              <input 
                type="password" 
                value={password} 
                onChange={e => setPassword(e.target.value)} 
                placeholder="••••••••"
                required 
                autoComplete="new-password"
                disabled={loading}
              />
            </label>

            <label className="field">
              <div className="label">Xác nhận mật khẩu</div>
              <input 
                type="password" 
                value={confirmPassword} 
                onChange={e => setConfirmPassword(e.target.value)} 
                placeholder="••••••••"
                required 
                autoComplete="new-password"
                disabled={loading}
              />
            </label>

            <button type="submit" className="btn btn-primary full" disabled={loading}>
              {loading ? 'Đang đăng ký...' : 'Đăng ký'}
            </button>
          </form>

          <div className="divider">Hoặc đăng ký bằng</div>

          <GoogleLogin clientId="REPLACE_WITH_GOOGLE_CLIENT_ID" onSuccess={onLoginSuccess} />
          
          <p className="register">
            Bạn đã có tài khoản?{' '}
            <a className="register-link" onClick={() => navigate('/login')} style={{ cursor: 'pointer' }}>
              Đăng nhập ngay!
            </a>
          </p>
        </div>
        <div className="login-illustration">
          <div className="illustration-content">
            <svg width="400" height="400" viewBox="0 0 400 400" fill="none" xmlns="http://www.w3.org/2000/svg">
              {/* User Profile Card */}
              <rect x="100" y="100" width="200" height="250" rx="20" fill="#fef3f3" stroke="#dc2626" strokeWidth="3"/>
              
              {/* Avatar Circle */}
              <circle cx="200" cy="160" r="35" fill="#fee2e2"/>
              <circle cx="200" cy="160" r="25" fill="#dc2626" opacity="0.3"/>
              <path d="M 190 155 Q 200 145, 210 155" stroke="#dc2626" strokeWidth="3" fill="none" strokeLinecap="round"/>
              <circle cx="195" cy="158" r="2" fill="#dc2626"/>
              <circle cx="205" cy="158" r="2" fill="#dc2626"/>
              
              {/* Form Fields */}
              <rect x="120" y="210" width="160" height="12" rx="6" fill="#fee2e2"/>
              <rect x="120" y="230" width="160" height="12" rx="6" fill="#fee2e2"/>
              <rect x="120" y="250" width="160" height="12" rx="6" fill="#fee2e2"/>
              
              {/* Button */}
              <rect x="130" y="280" width="140" height="35" rx="10" fill="#dc2626"/>
              <rect x="160" y="292" width="80" height="10" rx="3" fill="#fff"/>
              
              {/* Checkmark Icon */}
              <circle cx="340" cy="120" r="25" fill="#10b981" opacity="0.2"/>
              <path d="M 330 120 L 337 127 L 350 113" stroke="#10b981" strokeWidth="4" fill="none" strokeLinecap="round" strokeLinejoin="round"/>
              
              {/* Decorative elements */}
              <circle cx="70" cy="150" r="20" fill="#0ea5e9" opacity="0.1"/>
              <circle cx="350" cy="280" r="30" fill="#fbbf24" opacity="0.15"/>
              <circle cx="80" cy="320" r="25" fill="#dc2626" opacity="0.1"/>
              
              {/* Abstract shapes */}
              <path d="M 60 220 Q 80 200, 100 220" stroke="#0ea5e9" strokeWidth="3" fill="none" opacity="0.3"/>
              <path d="M 320 180 Q 340 160, 360 180" stroke="#dc2626" strokeWidth="3" fill="none" opacity="0.3"/>
              <path d="M 320 320 Q 340 300, 360 320" stroke="#10b981" strokeWidth="3" fill="none" opacity="0.3"/>
            </svg>
          </div>
        </div>
      </div>
    </main>
  )
}