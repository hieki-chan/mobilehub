import React, { useEffect, useRef, useState } from 'react'

const GOOGLE_CLIENT_ID = 'REPLACE_WITH_GOOGLE_CLIENT_ID' // giữ nguyên

export default function GoogleLogin() {
  const googleBtnRef = useRef(null)
  const [googleLoading, setGoogleLoading] = useState(false)

  useEffect(() => {
    if (!GOOGLE_CLIENT_ID) return
    const id = 'google-identity-client'
    if (!document.getElementById(id)) {
      const s = document.createElement('script')
      s.src = 'https://accounts.google.com/gsi/client'
      s.id = id
      s.async = true
      s.defer = true
      document.head.appendChild(s)
      s.onload = initGSI
    } else { initGSI() }

    function initGSI() {
      if (!window.google || !window.google.accounts || !window.google.accounts.id) return
      try {
        window.google.accounts.id.initialize({
          client_id: GOOGLE_CLIENT_ID,
          callback: async (resp) => {
            setGoogleLoading(true)
            try {
              // giữ nguyên logic gốc
            } finally { setGoogleLoading(false) }
          }
        })
        if (googleBtnRef.current) {
          window.google.accounts.id.renderButton(googleBtnRef.current, { theme: 'outline', size: 'large', width: '220', locale: 'vi' })
        }
      } catch (e) { console.warn('GSI init error', e) }
    }
  }, [])

  return (
    <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 12 }}>
      <div ref={googleBtnRef} />
      {googleLoading && <div className="muted">Đang đăng nhập bằng Google...</div>}
    </div>
  )
}
