
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
      <div className="login-card">
        <h2>Đăng ký</h2>
        <p className="muted">Tạo tài khoản mới để tiếp tục</p>

        {error && <div className="form-error" role="alert">{error}</div>}

        <form onSubmit={submitRegister} className="login-form" noValidate>
          <label className="field">
            <div className="label">Email</div>
            <input 
              type="email" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              placeholder="Nhập email"
              required 
              autoComplete="email" 
            />
          </label>

          <label className="field">
            <div className="label">Mật khẩu</div>
            <input 
              type="password" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              placeholder="Nhập mật khẩu (tối thiểu 6 ký tự)"
              required 
              autoComplete="new-password" 
            />
          </label>

          <label className="field">
            <div className="label">Xác nhận mật khẩu</div>
            <input 
              type="password" 
              value={confirmPassword} 
              onChange={e => setConfirmPassword(e.target.value)} 
              placeholder="Nhập lại mật khẩu"
              required 
              autoComplete="new-password" 
            />
          </label>

          <button type="submit" className="btn btn-primary full" disabled={loading}>
            {loading ? 'Đang đăng ký...' : 'Đăng ký'}
          </button>
        </form>

        <div className="divider">Hoặc</div>

        <GoogleLogin clientId="REPLACE_WITH_GOOGLE_CLIENT_ID" onSuccess={onLoginSuccess} />
        
        
        <p className="register">
          Bạn đã có tài khoản? {' '}
          <a className="register-link" onClick={() => navigate('/login')}>
            Đăng nhập ngay!
          </a>
        </p>
      </div>
    </main>
  )
}