// src/components/VariantSelector.jsx
import React from 'react'

export function CapacitySelector({ capacities = [], value, onChange }) {
  const formatStorage = (c) => {
    if (!c) return ''
    const num = parseFloat(c)
    if (num >= 1024) return (num / 1024) + 'TB'
    return num + 'GB'
  }

  return (
    <div>
      <div style={{ fontWeight: 700, marginBottom: 8 }}>Chọn dung lượng</div>
      <div className="variants">
        {capacities.map(c => (
          <button
            key={c}
            className={`variant-btn ${c === value ? 'active' : ''}`}
            onClick={() => onChange(c)}
          >
            {formatStorage(c)}
          </button>
        ))}
      </div>
    </div>
  )
}

export function ColorSelector({ colors = [], value, onChange }) {
  return (
    <div style={{ marginTop: 10 }}>
      <div style={{ fontWeight: 700, marginBottom: 8 }}>Chọn màu</div>
      <div id="colorWrap" className="color-wrap">
        {colors.map(c => (
          <button key={c.label} className={`variant-btn ${c.label === value ? 'active' : ''}`} onClick={() => onChange(c.label)} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span className="color-swatch" style={{ background: c.hex }} title={c.label}></span>
            <span>{c.label}</span>
          </button>
        ))}
      </div>
    </div>
  )
}
