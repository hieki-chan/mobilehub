import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import PhoneLogin from '../components/PhoneLogin'
import GoogleLogin from '../components/GoogleLogin'
import '../styles/pages/login.css'

// mockSignIn
export async function mockSignIn({ email, password }) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!email || email.indexOf('@') === -1) return reject(new Error('Email không hợp lệ'))
      if (!password || password.length < 6) return reject(new Error('Mật khẩu phải >= 6 ký tự'))
      const name = email.split('@')[0].replace(/[^\w]/g, '')
      resolve({
        user: { id: 'u_' + Date.now(), name: name.charAt(0).toUpperCase() + name.slice(1), email },
        token: 'demo-token-' + Math.random().toString(36).slice(2, 10)
      })
    }, 500)
  })
}

export default function Login() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [remember, setRemember] = useState(true)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onLoginSuccess = ({ user, token }) => {
    localStorage.setItem('user', JSON.stringify(user))
    if (remember) localStorage.setItem('token', token)
      
    navigate('/')
    window.dispatchEvent(new Event('user-changed'))
  }

  const submitEmail = async (e) => {
    e.preventDefault()
    setError(''); setLoading(true)
    try {
      const res = await mockSignIn({ email: email.trim(), password })
      onLoginSuccess(res)
    } catch (err) {
      setError(err.message || 'Đăng nhập thất bại')
      setLoading(false)
    }
  }

  return (
    <>
    <main className="login-page">
      <div className="login-card">
        <h2>Đăng nhập</h2>
        <p className="muted">Đăng nhập để tiếp tục</p>

        {error && <div className="form-error" role="alert">{error}</div>}

        <form onSubmit={submitEmail} className="login-form" noValidate>
          <label className="field"><div className="label">Email</div>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required autoComplete="email" />
          </label>

          <label className="field"><div className="label">Mật khẩu</div>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required autoComplete="current-password" />
          </label>

          <div className="form-row">
            <label className="checkbox">
              <input type="checkbox" checked={remember} onChange={e => setRemember(e.target.checked)} />
              <span>Ghi nhớ đăng nhập</span>
            </label>
          </div>

          <button type="submit" className="btn btn-primary full" disabled={loading}>
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
          <a className="forgot-password" href="">Quên mật khẩu?</a>
        </form>

        <div className="divider">Hoặc</div>

        <GoogleLogin clientId="REPLACE_WITH_GOOGLE_CLIENT_ID" onSuccess={onLoginSuccess} />
        <PhoneLogin onSuccess={onLoginSuccess} />
        <p className="register">Bạn chưa có tài khoản? <a className="register-link" onClick={() => {navigate('/register')}}>Đăng ký ngay!</a></p>
      </div>
    </main>

    </>
      
  )
}
