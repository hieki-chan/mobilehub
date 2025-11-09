import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import '../styles/pages/login.css'

// Mock forgot password function
async function mockForgotPassword({ email }) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!email || email.indexOf('@') === -1) {
        return reject(new Error('Email không hợp lệ'))
      }
      // Giả lập gửi email thành công
      resolve({ success: true, message: 'Đã gửi email khôi phục mật khẩu' })
    }, 500)
  })
}

export default function ForgotPassword() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)

  const submitForgotPassword = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess(false)
    setLoading(true)
    
    try {
      await mockForgotPassword({ email: email.trim() })
      setSuccess(true)
      setLoading(false)
      
      // Tự động chuyển về trang đăng nhập sau 3 giây
      setTimeout(() => {
        navigate('/login')
      }, 3000)
    } catch (err) {
      setError(err.message || 'Không thể gửi email khôi phục')
      setLoading(false)
    }
  }

  return (
    <main className="login-page">
      <div className="login-container">
        {/* Left Side - Illustration */}
        <div className="login-illustration">
          <div className="illustration-content">
            <svg width="400" height="400" viewBox="0 0 400 400" fill="none" xmlns="http://www.w3.org/2000/svg">
              {/* Lock Icon */}
              <rect x="150" y="160" width="100" height="120" rx="15" fill="#fee2e2" stroke="#dc2626" strokeWidth="3"/>
              <rect x="170" y="180" width="60" height="80" rx="8" fill="#fef3f3"/>
              
              {/* Keyhole */}
              <circle cx="200" cy="215" r="12" fill="#dc2626" opacity="0.3"/>
              <rect x="195" y="215" width="10" height="25" rx="3" fill="#dc2626" opacity="0.3"/>
              
              {/* Lock Top */}
              <path d="M 165 160 L 165 130 Q 165 100, 200 100 Q 235 100, 235 130 L 235 160" 
                    stroke="#dc2626" strokeWidth="8" fill="none" strokeLinecap="round"/>
              
              {/* Email Envelope */}
              <g transform="translate(280, 90)">
                <rect x="0" y="0" width="80" height="60" rx="8" fill="#dbeafe" stroke="#0ea5e9" strokeWidth="2"/>
                <path d="M 0 0 L 40 35 L 80 0" stroke="#0ea5e9" strokeWidth="2" fill="none"/>
                <path d="M 0 60 L 30 35" stroke="#0ea5e9" strokeWidth="2"/>
                <path d="M 80 60 L 50 35" stroke="#0ea5e9" strokeWidth="2"/>
              </g>
              
              {/* Question Mark */}
              <circle cx="90" cy="180" r="30" fill="#fbbf24" opacity="0.2"/>
              <text x="90" y="195" fontSize="40" fontWeight="bold" fill="#f59e0b" textAnchor="middle">?</text>
              
              {/* Key Icon */}
              <g transform="translate(260, 220) rotate(-20)">
                <circle cx="0" cy="0" r="15" fill="#10b981" opacity="0.3"/>
                <circle cx="0" cy="0" r="8" fill="none" stroke="#10b981" strokeWidth="3"/>
                <rect x="8" y="-3" width="50" height="6" rx="3" fill="#10b981" opacity="0.3"/>
                <rect x="48" y="-8" width="6" height="16" fill="#10b981" opacity="0.3"/>
                <rect x="38" y="-8" width="6" height="10" fill="#10b981" opacity="0.3"/>
              </g>
              
              {/* Decorative elements */}
              <circle cx="80" cy="300" r="25" fill="#dc2626" opacity="0.1"/>
              <circle cx="340" cy="280" r="30" fill="#0ea5e9" opacity="0.1"/>
              
              {/* Abstract shapes */}
              <path d="M 60 120 Q 80 100, 100 120" stroke="#0ea5e9" strokeWidth="3" fill="none" opacity="0.3"/>
              <path d="M 320 320 Q 340 300, 360 320" stroke="#10b981" strokeWidth="3" fill="none" opacity="0.3"/>
            </svg>
          </div>
        </div>

        {/* Right Side - Forgot Password Form */}
        <div className="login-card">
          <h2 className="login-title">Quên mật khẩu</h2>
          <p className="muted">
            Nhập email để nhận liên kết khôi phục 🔐
          </p>

          {error && <div className="form-error" role="alert">{error}</div>}
          
          {success && (
            <div className="form-success" role="alert">
              Email khôi phục đã được gửi! Vui lòng kiểm tra hộp thư.
              <br />
              <small className="muted" style={{ fontSize: '13px' }}>Đang chuyển về trang đăng nhập...</small>
            </div>
          )}

          {!success && (
            <>
              <form onSubmit={submitForgotPassword} className="login-form" noValidate>
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

                <button type="submit" className="btn btn-primary full" disabled={loading}>
                  {loading ? 'Đang gửi...' : 'Gửi email khôi phục'}
                </button>
              </form>

              <div style={{ marginTop: 20, textAlign: 'center' }}>
                <a 
                  className="forgot-password" 
                  onClick={() => navigate('/login')}
                  style={{ cursor: 'pointer', textAlign: 'center', display: 'inline-block' }}
                >
                  ← Quay lại đăng nhập
                </a>
              </div>
            </>
          )}

          <p className="register" style={{ marginTop: 20 }}>
            Bạn chưa có tài khoản?{' '}
            <a className="register-link" onClick={() => navigate('/register')} style={{ cursor: 'pointer' }}>
              Đăng ký ngay!
            </a>
          </p>
        </div>
      </div>
    </main>
  )
}