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
      <div className="login-card">
        <h2>Quên mật khẩu</h2>
        <p className="muted">
          Nhập email của bạn để nhận liên kết khôi phục mật khẩu
        </p>

        {error && <div className="form-error" role="alert">{error}</div>}
        
        {success && (
          <div className="form-success" role="alert">
            Email khôi phục đã được gửi! Vui lòng kiểm tra hộp thư của bạn.
            <br />
            <small className="muted">Đang chuyển về trang đăng nhập...</small>
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
                  placeholder="Nhập địa chỉ email của bạn"
                  required 
                  autoComplete="email"
                  disabled={loading}
                />
              </label>

              <button type="submit" className="btn btn-primary full" disabled={loading}>
                {loading ? 'Đang gửi...' : 'Gửi email khôi phục'}
              </button>
            </form>

            <div style={{ marginTop: 16, textAlign: 'center' }}>
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

        <p className="register" style={{ marginTop: 16 }}>
          Bạn chưa có tài khoản? {' '}
          <a className="register-link" onClick={() => navigate('/register')}>
            Đăng ký ngay!
          </a>
        </p>
      </div>
    </main>
  )
}