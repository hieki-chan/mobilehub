import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import ProductGallery from '../components/ProductGallery'
import { CapacitySelector, ColorSelector } from '../components/VariantSelector'
import QuantitySelector from '../components/QuantitySelector'
import ActionButtons from '../components/ActionButtons'
import ProductSpecs from '../components/ProductSpecs'
import Reviews from '../components/Reviews'
import RelatedProducts from '../components/RelatedProducts'
import useCart from '../hooks/useCart'
import useFav from '../hooks/useFav'
import { formatPrice } from '../utils/formatPrice'
import '../styles/pages/product-detail.css'
import { getProductDetails } from '../api/ProductApi'
import { getGroupedSpecs } from '../data/products'

export default function ProductDetail() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [p, setProduct] = useState(null)
  const [storageCap, setStorageCap] = useState(null)
  const [color, setColor] = useState(null)
  const [qty, setQty] = useState(1)
  const [adding, setAdding] = useState(false) 
  const { add } = useCart()
  const { isFav, toggle } = useFav()

  useEffect(() => {
    const load = async () => {
      try {
        const data = await getProductDetails(id)
        setProduct(data)

        // set default variant
        const defaultVariant = data.variants.find(v => v.id === data.defaultVariantId)
        if (defaultVariant) {
          setStorageCap(defaultVariant.storage_cap)
          setColor(defaultVariant.color_label)
        }

        document.title = data.name + ' | MobileHub'
      } catch (err) {
        console.error(err)
      }
    }
    load()
  }, [id])

  if (!p) return <div className="loading">Đang tải...</div>

  const storageOptions = [...new Set(p.variants?.map(v => v.storage_cap) || [])]
  const colors = (p.variants || []).map(v => ({ label: v.color_label, value: v.color_label, hex: v.color_hex }))
  const selectedVariant =
    p.variants?.find(v => v.storage_cap === storageCap && v.color_label === color) ||
    p.variants?.find(v => v.id === p.defaultVariantId)

  const galleryImages = selectedVariant
    ? [selectedVariant.imageUrl, ...(selectedVariant.subImageUrls || [])]
    : p.images || [p.image]

  const groupedSpecs = getGroupedSpecs(p.spec, selectedVariant)

  const onAddCart = async () => {
    if (adding) return
    setAdding(true)
    try {
      await add({
        productId: p.id,
        variantId: selectedVariant.id,
        quantity: qty
      })
      alert('Đã thêm vào giỏ hàng')
    } catch (err) {
      console.error(err)
      alert('Có lỗi khi thêm sản phẩm vào giỏ')
    } finally {
      setAdding(false)
    }
  }

  const onBuyNow = async () => {
    if (adding) return
    setAdding(true)
    try {
      await add({
        productId: p.id,
        variantId: selectedVariant.id,
        quantity: qty,
        capacity: storageCap,
        color: color
      })
      navigate('/cart')
    } catch (err) {
      console.error(err)
      alert('Có lỗi khi thêm sản phẩm vào giỏ')
    } finally {
      setAdding(false)
    }
  }

  return (
    <main className="pdp-container" id="mainContent">
      <ProductGallery images={galleryImages}/>

      <aside className="pdp-info" aria-label="Thông tin sản phẩm">
        <div className="muted">
          SKU: <strong id="skuVal">{selectedVariant.id}-{selectedVariant.color_label}</strong>
        </div>

        <h1 id="pTitle">{p.name}</h1>

        <div className="rating">
          <div id="stars">★★★★★</div>
          <div className="muted" id="reviewCount">(0 đánh giá)</div>
        </div>

        <div className="price-row">
          <div className="price" id="pPrice">
            {selectedVariant?.price ? formatPrice(selectedVariant.price) : 'Liên hệ'}
          </div>
          {p.discount?.percent > 0 && (
            <div className="old" id="pOld">
              {formatPrice(selectedVariant.price / (1 - p.discount.percent / 100))}
            </div>
          )}
        </div>

        <div className="muted" id="pShort">{p.description}</div>

        <CapacitySelector capacities={storageOptions} value={storageCap} onChange={setStorageCap} />
        <ColorSelector colors={colors} value={color} onChange={setColor} />
        <QuantitySelector qty={qty} setQty={setQty} />

        <ActionButtons
          product={p}
          qty={qty}
          capacity={storageCap}
          color={color}
          onAddCart={onAddCart}
          onBuyNow={onBuyNow}
          disabled={adding}
        />

        <div className="stock-info-row">
          <div className="muted" id="stockInfo">
            Tình trạng: <strong id="stockVal">
              {p.status === 'available' ? 'Còn hàng' : p.status === 'coming_soon' ? 'Sắp có' : 'Hết hàng'}
            </strong>
          </div>

          <div className="social-actions">
            <button
              className={`social-btn favorite-btn ${isFav(p.id) ? 'active' : ''}`}
              onClick={() => toggle(p.id)}
              aria-pressed={isFav(p.id)}
            >
              <i className={isFav(p.id) ? 'fa fa-heart' : 'fa-regular fa-heart'}></i>
            </button>

            <button
              className="social-btn share-btn"
              onClick={() => {
                if (navigator.share) {
                  navigator.share({ title: p.name, text: p.description, url: location.href }).catch(() => {})
                } else {
                  navigator.clipboard?.writeText(location.href).then(() => alert('Đã sao chép liên kết'))
                }
              }}
            >
              <i className="fa fa-share-nodes"></i>
            </button>
          </div>
        </div>
      </aside>

      <section style={{ gridColumn: '1 / -1' }}>
        <div className="desc">
          <h3>Mô tả sản phẩm</h3>
          <p id="longDesc">{p.description}</p>
        </div>

        <ProductSpecs specs={groupedSpecs} />
        <Reviews productId={p.id} />
        <RelatedProducts products={[]} currentId={p.id} />
      </section>
    </main>
  )
}
