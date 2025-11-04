import React, { useState } from 'react'



// OTP mock giữ nguyên
let __mockOtpStore = {}
function mockSendOtp(phone) {
  return new Promise(res => setTimeout(() => {
    const code = '123456'
    __mockOtpStore[phone] = code
    res({ ok: true, otp: code })
  }, 350))
}
function mockVerifyOtp(phone, otp) {
  return new Promise((res, rej) => setTimeout(() => {
    if (String(__mockOtpStore[phone]) === String(otp)) res({ ok: true })
    else rej(new Error('OTP không đúng'))
  }, 300))
}

export default function PhoneLogin({ onSuccess: onLoginSuccess }) {
  const [showPhoneForm, setShowPhoneForm] = useState(false)
  const [phone, setPhone] = useState('')
  const [otpSent, setOtpSent] = useState(false)
  const [otpCode, setOtpCode] = useState('')
  const [otpLoading, setOtpLoading] = useState(false)
  const [phoneError, setPhoneError] = useState('')

  const sendOtp = async () => {
    setPhoneError(''); setOtpLoading(true)
    if (!phone || phone.trim().length < 8) { setPhoneError('Số điện thoại không hợp lệ'); setOtpLoading(false); return }
    try {
      const r = await mockSendOtp(phone.trim())
      setOtpSent(true)
      console.log('mock OTP:', r.otp)
    } catch (e) { setPhoneError(e.message || 'Gửi OTP thất bại') }
    finally { setOtpLoading(false) }
  }

  const verifyOtp = async () => {
    setPhoneError(''); setOtpLoading(true)
    try {
      await mockVerifyOtp(phone.trim(), otpCode.trim())
      const user = { id: 'ph_' + phone.trim(), name: phone.trim(), phone: phone.trim() }
      const token = 'phone-token-' + Math.random().toString(36).slice(2, 10)
      // gọi hàm onSuccess từ props
      onLoginSuccess({ user, token })
      // logic onLoginSuccess giữ nguyên
      setShowPhoneForm(false)
      setOtpSent(false)
      setOtpCode('')
      setPhone('')
      setPhoneError('')
    } catch (e) { setPhoneError(e.message || 'Xác thực OTP thất bại') }
    finally { setOtpLoading(false) }
  }

  return (
    <div className="phone-login-form">
      <button className="phone-login-btn" onClick={() => { setShowPhoneForm(true); }}>Đăng nhập bằng SMS</button>

      {showPhoneForm && (
        <div className="phone-overlay" role="dialog" aria-modal="true" onClick={() => setShowPhoneForm(false)}>
          <div className="phone-form" onClick={e => e.stopPropagation()}>
            <button className="phone-close" onClick={() => setShowPhoneForm(false)}>×</button>

            {!otpSent ? (
              <>
                <label className="field">
                  <div className="label">Số điện thoại</div>
                  <input type="tel" value={phone} onChange={e => setPhone(e.target.value)} placeholder="0912345678" />
                </label>
                {phoneError && <div className="form-error">{phoneError}</div>}
                <div className="otp-actions">
                  <button className="btn btn-primary" onClick={sendOtp} disabled={otpLoading}>
                    {otpLoading ? 'Gửi mã...' : 'Gửi mã OTP'}
                  </button>
                  <button className="btn" onClick={() => { setShowPhoneForm(false); setPhone(''); setPhoneError('') }}>
                    Hủy
                  </button>
                </div>
              </>
            ) : (
              <>
                <label className="field">
                  <div className="label">Nhập mã OTP</div>
                  <input type="text" value={otpCode} onChange={e => setOtpCode(e.target.value)} placeholder="123456" />
                </label>
                {phoneError && <div className="form-error">{phoneError}</div>}
                <div className="otp-actions">
                  <button className="btn btn-primary" onClick={verifyOtp} disabled={otpLoading}>
                    {otpLoading ? 'Xác thực...' : 'Xác thực OTP'}
                  </button>
                  <button className="btn" onClick={() => { setOtpSent(false); setOtpCode(''); setPhoneError('') }}>
                    Gửi lại số khác
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

    </div>
  )
}
