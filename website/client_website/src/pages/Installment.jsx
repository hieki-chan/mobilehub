// src/pages/Installment.jsx
import React, { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { products as mockProducts } from '../data/products'
import { formatPrice } from '../utils/formatPrice'
import '../styles/pages/installment.css'

// Danh sách ngân hàng/công ty tài chính
const financialPartners = [
  {
    id: 'home_credit',
    name: 'Home Credit',
    logo: 'https://via.placeholder.com/80x40?text=HC',
    terms: [6, 9, 12, 18],
    interestRate: 0,
    requireCard: false,
    description: 'Duyệt nhanh 5 phút, không cần thẻ tín dụng'
  },
  {
    id: 'fe_credit',
    name: 'FE Credit',
    logo: 'https://via.placeholder.com/80x40?text=FE',
    terms: [6, 9, 12, 15, 18],
    interestRate: 0,
    requireCard: false,
    description: 'Lãi suất 0%, duyệt nhanh online'
  },
  {
    id: 'mcredit',
    name: 'MCredit',
    logo: 'https://via.placeholder.com/80x40?text=MC',
    terms: [6, 9, 12],
    interestRate: 0,
    requireCard: false,
    description: 'Trả góp 0% qua app MCredit'
  },
  {
    id: 'kredivo',
    name: 'Kredivo',
    logo: 'https://via.placeholder.com/80x40?text=KRD',
    terms: [3, 6, 12],
    interestRate: 0,
    requireCard: false,
    description: 'Mua trước trả sau linh hoạt'
  },
  {
    id: 'visa',
    name: 'Thẻ tín dụng Visa',
    logo: 'https://via.placeholder.com/80x40?text=VISA',
    terms: [3, 6, 9, 12],
    interestRate: 0,
    requireCard: true,
    description: 'Trả góp 0% qua thẻ Visa các ngân hàng'
  },
  {
    id: 'mastercard',
    name: 'Thẻ tín dụng Mastercard',
    logo: 'https://via.placeholder.com/80x40?text=MC',
    terms: [3, 6, 9, 12],
    interestRate: 0,
    requireCard: true,
    description: 'Trả góp 0% qua thẻ Mastercard'
  },
  {
    id: 'jcb',
    name: 'Thẻ tín dụng JCB',
    logo: 'https://via.placeholder.com/80x40?text=JCB',
    terms: [3, 6, 9, 12],
    interestRate: 0,
    requireCard: true,
    description: 'Trả góp 0% qua thẻ JCB'
  }
]

export default function Installment() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const productId = searchParams.get('productId')
  
  const [product, setProduct] = useState(null)
  const [selectedPartner, setSelectedPartner] = useState(null)
  const [selectedTerm, setSelectedTerm] = useState(12)
  const [showPartnerDetail, setShowPartnerDetail] = useState(false)
  
  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
    email: '',
    idCard: '',
    address: '',
    acceptTerms: false
  })
  const [errors, setErrors] = useState({})

  // Load thông tin sản phẩm
  useEffect(() => {
    if (productId) {
      const found = mockProducts.find(p => String(p.id) === String(productId))
      if (found) {
        setProduct(found)
        setSelectedPartner(financialPartners[0])
      } else {
        navigate('/')
      }
    } else {
      navigate('/')
    }
  }, [productId, navigate])

  // Load thông tin user nếu đã đăng nhập
  useEffect(() => {
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
  }, [])

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }))
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
    if (!formData.idCard.trim()) newErrors.idCard = 'Vui lòng nhập CMND/CCCD'
    else if (!/^[0-9]{9,12}$/.test(formData.idCard.trim())) {
      newErrors.idCard = 'CMND/CCCD không hợp lệ'
    }
    if (!formData.address.trim()) newErrors.address = 'Vui lòng nhập địa chỉ'
    if (!formData.acceptTerms) newErrors.acceptTerms = 'Vui lòng đồng ý với điều khoản'
    if (!selectedPartner) newErrors.partner = 'Vui lòng chọn hình thức trả góp'

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!validate()) {
      alert('Vui lòng điền đầy đủ thông tin')
      return
    }

    // Giả lập gửi hồ sơ
    alert(`Đã gửi hồ sơ trả góp!\n\nSản phẩm: ${product.name}\nHình thức: ${selectedPartner.name}\nKỳ hạn: ${selectedTerm} tháng\n\nChúng tôi sẽ liên hệ với bạn trong vòng 24h.`)
    navigate('/')
  }

  // Tính toán số tiền trả góp
  const calculateInstallment = () => {
    if (!product || !selectedTerm) return 0
    const price = product.price || 0
    const interestRate = selectedPartner?.interestRate || 0
    const total = price * (1 + interestRate)
    return Math.ceil(total / selectedTerm)
  }

  if (!product) {
    return (
      <main className="container" style={{ padding: '40px 20px', textAlign: 'center' }}>
        <div className="loading-spinner">
          <i className="fa fa-spinner fa-spin" style={{ fontSize: 32 }}></i>
          <div style={{ marginTop: 12 }}>Đang tải...</div>
        </div>
      </main>
    )
  }

  const monthlyPayment = calculateInstallment()

  return (
    <main className="installment-page">
      <div className="container">
        {/* Breadcrumb */}
        <div className="breadcrumb" style={{ marginBottom: 16 }}>
          <a onClick={() => navigate('/')}>Trang chủ</a>
          <span> / </span>
          <a onClick={() => navigate(`/product/${product.id}`)}>{product.name}</a>
          <span> / </span>
          <span>Trả góp</span>
        </div>

        <div className="installment-content">
          {/* Left: Form */}
          <div className="installment-left">
            <h2 className="page-title">
              <i className="fa fa-credit-card"></i>
              Đăng ký trả góp
            </h2>

            {/* Thông tin sản phẩm */}
            <section className="installment-section">
              <h3>Thông tin sản phẩm</h3>
              <div className="product-preview">
                <img src={product.image || product.images?.[0] || '/no-image.png'} alt={product.name} />
                <div className="product-preview-info">
                  <div className="product-preview-name">{product.name}</div>
                  <div className="product-preview-price">{formatPrice(product.price)}</div>
                </div>
              </div>
            </section>

            {/* Chọn hình thức trả góp */}
            <section className="installment-section">
              <h3>Chọn hình thức trả góp</h3>
              <div className="partner-grid">
                {financialPartners.map(partner => (
                  <div
                    key={partner.id}
                    className={`partner-card ${selectedPartner?.id === partner.id ? 'active' : ''}`}
                    onClick={() => {
                      setSelectedPartner(partner)
                      setSelectedTerm(partner.terms[0])
                    }}
                  >
                    <div className="partner-logo">
                      <img src={partner.logo} alt={partner.name} />
                    </div>
                    <div className="partner-name">{partner.name}</div>
                    <div className="partner-desc">{partner.description}</div>
                    {partner.requireCard && (
                      <div className="partner-badge">Cần thẻ tín dụng</div>
                    )}
                    {partner.interestRate === 0 && (
                      <div className="partner-badge interest-free">Lãi suất 0%</div>
                    )}
                  </div>
                ))}
              </div>

              {selectedPartner && (
                <button
                  className="btn btn-secondary"
                  style={{ marginTop: 12 }}
                  onClick={() => setShowPartnerDetail(true)}
                >
                  <i className="fa fa-info-circle"></i>
                  Xem điều kiện & thủ tục
                </button>
              )}
            </section>

            {/* Chọn kỳ hạn */}
            {selectedPartner && (
              <section className="installment-section">
                <h3>Chọn kỳ hạn trả góp</h3>
                <div className="term-grid">
                  {selectedPartner.terms.map(term => (
                    <button
                      key={term}
                      className={`term-btn ${selectedTerm === term ? 'active' : ''}`}
                      onClick={() => setSelectedTerm(term)}
                    >
                      <div className="term-months">{term} tháng</div>
                      <div className="term-payment">
                        {formatPrice(Math.ceil((product.price * (1 + selectedPartner.interestRate)) / term))}/tháng
                      </div>
                    </button>
                  ))}
                </div>
              </section>
            )}

            {/* Form thông tin */}
            <section className="installment-section">
              <h3>Thông tin khách hàng</h3>
              <form onSubmit={handleSubmit}>
                <label className="field">
                  <div className="label">Họ và tên <span className="required">*</span></div>
                  <input
                    type="text"
                    name="fullName"
                    value={formData.fullName}
                    onChange={handleChange}
                    placeholder="Nguyễn Văn A"
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

                <label className="field">
                  <div className="label">Số CMND/CCCD <span className="required">*</span></div>
                  <input
                    type="text"
                    name="idCard"
                    value={formData.idCard}
                    onChange={handleChange}
                    placeholder="001234567890"
                  />
                  {errors.idCard && <div className="field-error">{errors.idCard}</div>}
                </label>

                <label className="field">
                  <div className="label">Địa chỉ <span className="required">*</span></div>
                  <textarea
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố"
                    rows="3"
                  />
                  {errors.address && <div className="field-error">{errors.address}</div>}
                </label>

                <label className="checkbox-field">
                  <input
                    type="checkbox"
                    name="acceptTerms"
                    checked={formData.acceptTerms}
                    onChange={handleChange}
                  />
                  <span>
                    Tôi đã đọc và đồng ý với{' '}
                    <a href="#" onClick={(e) => { e.preventDefault(); setShowPartnerDetail(true) }}>
                      điều khoản & điều kiện
                    </a>
                  </span>
                </label>
                {errors.acceptTerms && <div className="field-error">{errors.acceptTerms}</div>}

                <button type="submit" className="btn btn-primary btn-xl" style={{ width: '100%', marginTop: 20 }}>
                  <i className="fa fa-paper-plane"></i>
                       Gửi hồ sơ trả góp
                </button>
              </form>
            </section>
          </div>

          {/* Right: Summary */}
          <div className="installment-right">
            <div className="installment-summary">
              <h3>Tóm tắt trả góp</h3>

              <div className="summary-item">
                <span>Giá sản phẩm:</span>
                <strong>{formatPrice(product.price)}</strong>
              </div>

              {selectedPartner && (
                <>
                  <div className="summary-item">
                    <span>Hình thức:</span>
                    <strong>{selectedPartner.name}</strong>
                  </div>

                  <div className="summary-item">
                    <span>Kỳ hạn:</span>
                    <strong>{selectedTerm} tháng</strong>
                  </div>

                  <div className="summary-item">
                    <span>Lãi suất:</span>
                    <strong className="interest-free-text">
                      {selectedPartner.interestRate === 0 ? '0%' : `${selectedPartner.interestRate * 100}%`}
                    </strong>
                  </div>

                  <div className="summary-divider"></div>

                  <div className="summary-total">
                    <span>Trả mỗi tháng:</span>
                    <strong>{formatPrice(monthlyPayment)}</strong>
                  </div>

                  <div className="summary-note">
                    <i className="fa fa-info-circle"></i>
                    <div>
                      <strong>Lưu ý:</strong>
                      <ul>
                        <li>Phí duyệt hồ sơ: Miễn phí</li>
                        <li>Thời gian duyệt: 5-30 phút</li>
                        <li>Giấy tờ cần có: CMND/CCCD</li>
                        {selectedPartner.requireCard && <li>Cần có thẻ tín dụng</li>}
                      </ul>
                    </div>
                  </div>
                </>
              )}

              {!selectedPartner && (
                <div className="summary-empty">
                  <i className="fa fa-hand-pointer"></i>
                  <p>Vui lòng chọn hình thức trả góp để xem chi tiết</p>
                </div>
              )}
            </div>

            {/* Ưu đãi */}
            <div className="installment-benefits">
              <h4>
                <i className="fa fa-gift"></i>
                Ưu đãi khi trả góp
              </h4>
              <ul>
                <li>
                  <i className="fa fa-check-circle"></i>
                  Lãi suất 0% toàn bộ kỳ hạn
                </li>
                <li>
                  <i className="fa fa-check-circle"></i>
                  Duyệt hồ sơ nhanh chóng
                </li>
                <li>
                  <i className="fa fa-check-circle"></i>
                  Không cần thẻ tín dụng
                </li>
                <li>
                  <i className="fa fa-check-circle"></i>
                  Nhận hàng ngay khi được duyệt
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      {/* Modal điều kiện & thủ tục */}
      {showPartnerDetail && selectedPartner && (
        <div className="modal-overlay open" onClick={() => setShowPartnerDetail(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: 600 }}>
            <button className="modal-close" onClick={() => setShowPartnerDetail(false)}>&times;</button>
            <div className="modal-body">
              <h3>{selectedPartner.name}</h3>
              
              <div className="partner-detail-section">
                <h4>Điều kiện</h4>
                <ul>
                  <li>✓ Công dân Việt Nam từ 18 tuổi trở lên</li>
                  <li>✓ Có CMND/CCCD còn hiệu lực</li>
                  <li>✓ Có thu nhập ổn định</li>
                  {selectedPartner.requireCard && <li>✓ Có thẻ tín dụng đang hoạt động</li>}
                </ul>
              </div>

              <div className="partner-detail-section">
                <h4>Thủ tục</h4>
                <ol>
                  <li>Điền đầy đủ thông tin vào form</li>
                  <li>Chụp ảnh CMND/CCCD 2 mặt</li>
                  <li>Chụp ảnh chân dung cầm CMND/CCCD</li>
                  {selectedPartner.requireCard && <li>Cung cấp thông tin thẻ tín dụng</li>}
                  <li>Chờ duyệt hồ sơ (5-30 phút)</li>
                  <li>Nhận hàng và ký hợp đồng</li>
                </ol>
              </div>

              <div className="partner-detail-section">
                <h4>Lãi suất & Phí</h4>
                <ul>
                  <li>Lãi suất: <strong>{selectedPartner.interestRate === 0 ? '0%' : `${selectedPartner.interestRate * 100}%`}/tháng</strong></li>
                  <li>Phí duyệt hồ sơ: <strong>Miễn phí</strong></li>
                  <li>Phí trả trước hạn: <strong>Không có</strong></li>
                </ul>
              </div>

              <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => setShowPartnerDetail(false)}>
                Đã hiểu
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  )
}