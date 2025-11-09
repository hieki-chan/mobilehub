import React from "react";
import { formatPrice } from "../../utils/formatPrice";
import "../../styles/components/home/quickviewmodal.css";

export default function QuickViewModal({ product, onClose }) {
  if (!product) return null;

  const IMG_WIDTH = 340;
  const IMG_HEIGHT = 340;

  // Lấy data chuẩn từ JSON
  const price = product?.defaultVariant?.price ?? 0;
  const imageUrl = product?.defaultVariant?.imageUrl || "/no-image.png";
  const discount = product?.discountInPercent ?? 0;
  const finalPrice = discount > 0 ? price * (1 - discount / 100) : price;

  return (
    <div
      id="quickViewModal"
      className="modal-overlay open"
      onClick={(e) => {
        if (e.target.id === "quickViewModal") onClose();
      }}
    >
      <div className="modal" role="dialog" aria-modal="true">
        <button className="modal-close" aria-label="Đóng" onClick={onClose}>
          &times;
        </button>

        <div className="modal-body">
          <div
            className="modal-img"
            style={{
              width: `${IMG_WIDTH}px`,
              height: `${IMG_HEIGHT}px`,
              background: "#fafafa",
              borderRadius: "8px",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              overflow: "hidden",
            }}
          >
            <img
              src={imageUrl}
              alt={product.name}
              style={{
                width: `${IMG_WIDTH}px`,
                height: `${IMG_HEIGHT}px`,
                objectFit: "contain",
              }}
            />
          </div>

          <div className="modal-info">
            <h2 className="qv-title">{product.name}</h2>

            <div className="qv-sub">RAM 16 GB · SSD 512 GB</div>

            <div className="qv-rating">
              <i className="fa fa-star"></i> 4.9 · Đã bán 2.1k
            </div>

            <div className="qv-price">
              {discount > 0 ? (
                <>
                  <span className="qv-price-final">
                    {formatPrice(finalPrice)}
                  </span>
                  <span className="qv-price-old">{formatPrice(price)}</span>
                  <span className="qv-price-sale">-{discount}%</span>
                </>
              ) : (
                <span className="qv-price-final">{formatPrice(price)}</span>
              )}
            </div>

            <div className="qv-bonus">🎁 Quà tặng trị giá 1.000.000₫</div>

            <p className="qv-desc">
              {product.description ||
                "Thiết bị sở hữu hiệu năng mạnh mẽ, thiết kế sang trọng và camera chất lượng cao. Hoàn hảo cho mọi nhu cầu từ làm việc đến giải trí."}
            </p>

            <div className="qv-actions">
              <button
                className="btn btn-primary"
                onClick={() =>
                  (window.location.href = `/product/${product.id}`)
                }
              >
                Mua ngay
              </button>
              <button className="btn btn-primary" onClick={onClose}>
                Thêm vào giỏ hàng
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
