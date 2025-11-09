import React from "react";
import { useNavigate } from "react-router-dom";

function formatPrice(v) {
  if (v === null || v === undefined || v === "") return "";
  try {
    return new Intl.NumberFormat("vi-VN").format(Number(v)) + "₫";
  } catch (e) {
    return String(v) + "₫";
  }
}

export default function ProductCard({ p, onQuickView }) {
  const navigate = useNavigate();
  const hasDiscount = p.discountInPercent && p.discountInPercent > 0;
  const finalPrice = hasDiscount
    ? p.price * (1 - p.discountInPercent / 100)
    : p.price;

  return (
    <article className="product-card" role="article" aria-label={p.name}>
      <div className="img-wrap" onClick={() => navigate(`/product/${p.id}`)}>
        <img
          loading="lazy"
          src={p.imageUrl || "/no-image.png"}
          alt={p.name}
          onError={(e) => {
            e.currentTarget.onerror = null;
            e.currentTarget.src = "/no-image.png";
          }}
        />
        {hasDiscount && (
          <span className="badge sale">-{p.discountInPercent}%</span>
        )}
      </div>

      <div className="product-info">
        <h4 className="product-title">{p.name}</h4>

        <div className="product-sub">
          <span>RAM {p.defaultVariant?.ram || 0} GB</span> · <span>SSD {p.defaultVariant?.storage_cap || 0} GB</span> 
        </div>

        <div className="price-row">
          <div className="price-final">{formatPrice(finalPrice)}</div>
          {hasDiscount && (
            <div className="price-old">{formatPrice(p.price)}</div>
          )}
        </div>

        <div className="bonus">Quà {formatPrice(1000000)}</div>

        <div className="rating">
          <i className="fa fa-star"></i> 4.9 <span>· Đã bán 2.1k</span>
        </div>

        <div className="btn-wrap">
          <button
            className="btn btn-primary"
            onClick={() => navigate(`/product/${p.id}`)}
          >
            Mua ngay
          </button>
          <button
            className="btn btn-secondary quick-view"
            onClick={() => onQuickView(p.id)}
          >
            Xem nhanh
          </button>
        </div>
      </div>
    </article>
  );
}
