import React, { useMemo } from 'react'
import { formatPrice } from '../../utils/formatPrice'
import '../../styles/pages/cart.css'

export default function CartItem({ item, loading, remove, updateQty, updateVariant }) {
  const selectedVariant = useMemo(() => {
    return item.variants?.find(v => v.id === item.variantId) || item.variants?.[0]
  }, [item])

  const finalPrice = useMemo(() => {
    if (!selectedVariant) return 0
    const discounted = selectedVariant.price * (1 - (item.discountInPercent || 0) / 100)
    return Math.round(discounted)
  }, [selectedVariant, item.discountInPercent])

  const subtotal = useMemo(() => (finalPrice * (item.quantity || 1)), [finalPrice, item.quantity])

  return (
    <div className="cart-item">
      <div className="cart-item-left">
        <img
          src={selectedVariant?.imageUrl}
          alt={item.name}
          className="cart-item-image"
        />
      </div>

      <div className="cart-item-right">
        <div className="cart-item-info">
          <a href={`/product/${item.productId}`} className="cart-item-info-name">
            {item.name}
          </a>

          <div className="cart-item-info-price">
            {item.discountInPercent > 0 && (
              <div className="old-price">{formatPrice(selectedVariant?.price)}</div>
            )}
            <div className="new-price">{formatPrice(finalPrice)}</div>
          </div>
        </div>

        <div className="cart-item-variants">
          <label>Phiên bản:</label>
          <div className="variant-select-wrapper">
            <select
              disabled={loading}
              value={item.variantId}
              onChange={(e) => updateVariant(item, Number(e.target.value))}
            >
              {item.variants?.map((v, i) => (
                <option key={i} value={v.id || i}>
                  {`${v.color_label || ''} - ${v.storage_cap || ''}GB - ${v.ram}GB RAM`}
                </option>
              ))}
            </select>

            {(() => {
              const selected = item.variants?.find(
                (v) => v.id === item.variantId || v === item.selectedVariant
              )
              return selected ? (
                <span
                  className="color-preview"
                  style={{
                    backgroundColor: selected.color_hex,
                    border: '1px solid #ccc',
                  }}
                ></span>
              ) : null
            })()}
          </div>
        </div>

        <div className="cart-item-quantity">
          <button
            className="cart-item-quantity-remove"
            disabled={loading}
            onClick={() => {
              if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ?')) remove(item)
            }}
          >
            Xóa
          </button>

          <div className="cart-item-quantity-btn">
            <button
              className="minus-btn"
              disabled={loading}
              aria-label="Giảm"
              onClick={() => {
                const newQty = (item.quantity || 1) - 1
                if (newQty <= 0) {
                  if (confirm('Bạn có chắc muốn xóa sản phẩm này?')) remove(item)
                } else {
                  updateQty(item, newQty)
                }
              }}
            >
              -
            </button>

            <input
              type="text"
              inputMode="numeric"
              pattern="^[1-9][0-9]*$"
              className="no-spinners"
              value={item.quantity || 1}
              readOnly
            />

            <button
              className="add-btn"
              disabled={loading}
              aria-label="Tăng"
              onClick={() => updateQty(item, (item.quantity || 1) + 1)}
            >
              +
            </button>
          </div>
        </div>

        <div className="cart-item-subtotal">
          Tổng: <b>{formatPrice(subtotal)}</b>
        </div>
      </div>
    </div>
  )
}
