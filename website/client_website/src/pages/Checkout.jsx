import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import useCart from '../hooks/useCart'
import { formatPrice } from '../utils/formatPrice'
import '../styles/pages/checkout.css'

export default function Checkout() {
  const navigate = useNavigate()
  const { cart, total, clear } = useCart()
  const [provinces, setProvinces] = useState([])
  const [districts, setDistricts] = useState([])
  const [wards, setWards] = useState([])

  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
    email: '',
    address: '',
    cityCode: '',
    cityCodeCode: '',
    wardCode: '',
    note: '',
    paymentMethod: 'cod',
    shippingMethod: 'standard'
  })

  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    // Nếu giỏ hàng trống, chuyển về trang chủ
    // if (!cart || cart.length === 0) {
    //   navigate('/')
    // }

    // Lấy thông tin user nếu đã đăng nhập
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        setFormData(prev => ({
          ...prev,
          fullName: user.name || '',
          email: user.email || '',
          phone: user.phone || ''
        }))
      } catch (e) {
        // ignore
      }
    }

    document.title = 'Thanh toán | MobileHub'
  }, [cart, navigate])
  useEffect(() => {
    fetch('https://provinces.open-api.vn/api/p/')
      .then(res => res.json())
      .then(data => setProvinces(data))
      .catch(err => console.error("Lỗi tải Tỉnh/Thành:", err))
  }, [])
  useEffect(() => {

    setDistricts([])
    setWards([])
    setFormData(prev => ({ ...prev, districtCode: '', wardCode: '' }))

    if (!formData.cityCode) return // Không gọi API nếu chưa chọn tỉnh


    fetch(`https://provinces.open-api.vn/api/p/${formData.cityCode}?depth=2`)
      .then(res => res.json())
      .then(data => {

        setDistricts(data.districts)
      })
      .catch(err => console.error("Lỗi tải Quận/Huyện:", err))

  }, [formData.cityCode])

  useEffect(() => {

    setWards([])
    setFormData(prev => ({ ...prev, wardCode: '' }))

    if (!formData.districtCode) return // Không gọi API nếu chưa chọn huyện

    fetch(`https://provinces.open-api.vn/api/d/${formData.districtCode}?depth=2`)
      .then(res => res.json())
      .then(data => {
        // API trả về object có key là wards
        setWards(data.wards)
      })
      .catch(err => console.error("Lỗi tải Phường/Xã:", err))

  }, [formData.districtCode])

  const handleChange = (e) => {
    const { name, value } = e.target

    // Khi người dùng thay đổi Tỉnh/Thành phố
    if (name === 'cityCode') {
      // Khi chọn tỉnh mới, tự động reset Huyện/Xã về rỗng
      setFormData(prev => ({
        ...prev,
        cityCode: value,
        districtCode: '',
        wardCode: ''
      }))
    }
    // Khi người dùng thay đổi Quận/Huyện
    else if (name === 'districtCode') {
      setFormData(prev => ({
        ...prev,
        districtCode: value,
        wardCode: ''
      }))
    }
    else {
      setFormData(prev => ({ ...prev, [name]: value }))
    }

    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  const validate = () => {
    const newErrors = {}

    if (!formData.fullName.trim()) newErrors.fullName = 'Vui lòng nhập họ tên'
    if (!formData.phone.trim()) newErrors.phone = 'Vui lòng nhập số điện thoại'
    else if (!/^[0-9]{10,11}$/.test(formData.phone.trim())) {
      newErrors.phone = 'Số điện thoại không hợp lệ'
    }
    if (!formData.email.trim()) newErrors.email = 'Vui lòng nhập email'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email.trim())) {
      newErrors.email = 'Email không hợp lệ'
    }
    if (!formData.address.trim()) newErrors.address = 'Vui lòng nhập địa chỉ'
    if (!formData.cityCode.trim()) newErrors.cityCode = 'Vui lòng chọn Tỉnh/Thành phố'
    if (!formData.districtCode.trim()) newErrors.districtCode = 'Vui lòng chọn Quận/Huyện'
    if (!formData.wardCode.trim()) newErrors.wardCode = 'Vui lòng chọn Phường/Xã'

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!validate()) {
      alert('Vui lòng điền đầy đủ thông tin')
      return
    }

    setLoading(true)

    // Giả lập gọi API
    setTimeout(() => {
      // Tạo mã đơn hàng
      const orderId = 'MH' + Date.now()

      // Lưu đơn hàng (demo - thực tế sẽ gọi API)
      const order = {
        id: orderId,
        items: cart,
        subtotal: cartSubtotal,
        shippingFee: shippingFee,
        total: finalTotal,
        customerInfo: formData,
        createdAt: new Date().toISOString(),
        status: 'pending'
      }

      // Lưu vào localStorage (demo)
      const orders = JSON.parse(localStorage.getItem('orders') || '[]')
      orders.push(order)
      localStorage.setItem('orders', JSON.stringify(orders))

      // Xóa giỏ hàng
      clear()

      setLoading(false)

      // Chuyển đến trang thành công
      alert(`Đặt hàng thành công! Mã đơn hàng: ${orderId}`)
      navigate('/')
    }, 1500)
  }

  // Tính toán chi tiết
  const cartSubtotal = cart.reduce((sum, item) => {
    const price = Number(item.price) || 0
    const qty = Number(item.qty) || 1
    return sum + (price * qty)
  }, 0)

  const shippingFee = formData.shippingMethod === 'express' ? 50000 : 0
  const finalTotal = cartSubtotal + shippingFee

  const getItemTotal = (item) => {
    const price = Number(item.price) || 0
    const qty = Number(item.qty) || 1
    return price * qty
  }

  return (
    <main className="checkout-container">
      <div className="checkout-content">
        <div className="checkout-left">
          <h2 className="section-title">Thông tin giao hàng</h2>

          <form onSubmit={handleSubmit} className="checkout-form">
            <div className="form-section">
              <h3>Thông tin người nhận</h3>

              <label className="field">
                <div className="label">Họ và tên <span className="required">*</span></div>
                <input
                  type="text"
                  name="fullName"
                  value={formData.fullName}
                  onChange={handleChange}
                  placeholder="Nhập họ và tên"
                />
                {errors.fullName && <div className="field-error">{errors.fullName}</div>}
              </label>

              <div className="form-row-2">
                <label className="field">
                  <div className="label">Số điện thoại <span className="required">*</span></div>
                  <input
                    type="tel"
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    placeholder="0912345678"
                  />
                  {errors.phone && <div className="field-error">{errors.phone}</div>}
                </label>

                <label className="field">
                  <div className="label">Email <span className="required">*</span></div>
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="email@example.com"
                  />
                  {errors.email && <div className="field-error">{errors.email}</div>}
                </label>
              </div>
            </div>

            <div className="form-section">
              <h3>Địa chỉ giao hàng</h3>

              <label className="field">
                <div className="label">Địa chỉ cụ thể <span className="required">*</span></div>
                <input
                  type="text"
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  placeholder="Số nhà, tên đường"
                />
                {errors.address && <div className="field-error">{errors.address}</div>}
              </label>

              <div className="form-row-3">
                {/* TỈNH/THÀNH PHỐ */}
                <label className="field">
                  <div className="label">Tỉnh/Thành phố <span className="required">*</span></div>
                  <select name="cityCode" value={formData.cityCode} onChange={handleChange}>
                    <option value="">Chọn Tỉnh/TP</option>
                    {/* Lặp qua danh sách Provinces đã được tải từ API */}
                    {provinces.map(p => (
                      <option key={p.code} value={p.code}>{p.name}</option>
                    ))}
                  </select>
                  {errors.cityCode && <div className="field-error">{errors.cityCode}</div>}
                </label>

                {/* QUẬN/HUYỆN */}
                <label className="field">
                  <div className="label">Quận/Huyện <span className="required">*</span></div>
                  <select
                    name="districtCode"
                    value={formData.districtCode}
                    onChange={handleChange}
                    disabled={!formData.cityCode || districts.length === 0} 
                  >
                    <option value="">Chọn Quận/Huyện</option>
                    {/* Lặp qua danh sách Districts đã được lọc */}
                    {districts.map(d => (
                      <option key={d.code} value={d.code}>{d.name}</option>
                    ))}
                  </select>
                  {errors.districtCode && <div className="field-error">{errors.districtCode}</div>}
                </label>

                {/* PHƯỜNG/XÃ */}
                <label className="field">
                  <div className="label">Phường/Xã <span className="required">*</span></div>
                  <select
                    name="wardCode"
                    value={formData.wardCode}
                    onChange={handleChange}
                    disabled={!formData.districtCode || wards.length === 0} 
                  >
                    <option value="">Chọn Phường/Xã</option>
                    {/* Lặp qua danh sách Wards đã được lọc */}
                    {wards.map(w => (
                      <option key={w.code} value={w.code}>{w.name}</option>
                    ))}
                  </select>
                  {errors.wardCode && <div className="field-error">{errors.wardCode}</div>}
                </label>
              </div>

              <label className="field">
                <div className="label">Ghi chú (tùy chọn)</div>
                <textarea
                  name="note"
                  value={formData.note}
                  onChange={handleChange}
                  placeholder="Ghi chú về đơn hàng, ví dụ: thời gian hay chỉ dẫn địa điểm giao hàng chi tiết hơn"
                  rows="3"
                />
              </label>
            </div>

            <div className="form-section">
              <h3>Phương thức vận chuyển</h3>

              <label className="radio-option">
                <input
                  type="radio"
                  name="shippingMethod"
                  value="standard"
                  checked={formData.shippingMethod === 'standard'}
                  onChange={handleChange}
                />
                <div className="radio-content">
                  <div className="radio-title">Giao hàng tiêu chuẩn</div>
                  <div className="radio-desc">Miễn phí - Giao trong 3-5 ngày</div>
                </div>
                <div className="radio-price">Miễn phí</div>
              </label>

              <label className="radio-option">
                <input
                  type="radio"
                  name="shippingMethod"
                  value="express"
                  checked={formData.shippingMethod === 'express'}
                  onChange={handleChange}
                />
                <div className="radio-content">
                  <div className="radio-title">Giao hàng nhanh</div>
                  <div className="radio-desc">Giao trong 1-2 ngày</div>
                </div>
                <div className="radio-price">{formatPrice(50000)}</div>
              </label>
            </div>

            <div className="form-section">
              <h3>Phương thức thanh toán</h3>

              <label className="radio-option">
                <input
                  type="radio"
                  name="paymentMethod"
                  value="cod"
                  checked={formData.paymentMethod === 'cod'}
                  onChange={handleChange}
                />
                <div className="radio-content">
                  <div className="radio-title">Thanh toán khi nhận hàng (COD)</div>
                  <div className="radio-desc">Thanh toán bằng tiền mặt khi nhận hàng</div>
                </div>
              </label>

              <label className="radio-option">
                <input
                  type="radio"
                  name="paymentMethod"
                  value="bank"
                  checked={formData.paymentMethod === 'bank'}
                  onChange={handleChange}
                />
                <div className="radio-content">
                  <div className="radio-title">Chuyển khoản ngân hàng</div>
                  <div className="radio-desc">Chuyển khoản qua ngân hàng</div>
                </div>
              </label>
            </div>
          </form>
        </div>

        <div className="checkout-right">
          <div className="order-summary">
            <h3>Đơn hàng của bạn</h3>

            <div className="summary-items">
              {cart.map((item, idx) => (
                <div key={idx} className="summary-item">
                  <img src={item.image || '/no-image.png'} alt={item.name} />
                  <div className="summary-item-info">
                    <div className="summary-item-name">{item.name}</div>
                    {item.capacity && <div className="muted">{item.capacity}</div>}
                    {item.color && <div className="muted">{item.color}</div>}
                    <div className="muted">SL: {item.qty}</div>
                  </div>
                  <div className="summary-item-price">
                    {formatPrice(getItemTotal(item))}
                  </div>
                </div>
              ))}
            </div>

            <div className="summary-divider"></div>

            <div className="summary-row">
              <span>Tạm tính</span>
              <span>{formatPrice(cartSubtotal)}</span>
            </div>

            <div className="summary-row">
              <span>Phí vận chuyển</span>
              <span>{shippingFee === 0 ? 'Miễn phí' : formatPrice(shippingFee)}</span>
            </div>

            <div className="summary-divider"></div>

            <div className="summary-total">
              <span>Tổng cộng</span>
              <span className="total-price">{formatPrice(finalTotal)}</span>
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-xl"
              onClick={handleSubmit}
              disabled={loading}
            >
              {loading ? 'Đang xử lý...' : 'Đặt hàng'}
            </button>

            <button
              type="button"
              className="btn btn-secondary"
              style={{ marginTop: 10, width: '100%' }}
              onClick={() => navigate('/cart')}
            >
              ← Quay lại giỏ hàng
            </button>
          </div>
        </div>
      </div>
    </main>
  )
}