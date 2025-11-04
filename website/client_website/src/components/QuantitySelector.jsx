// src/components/QuantitySelector.jsx
import React from 'react'

export default function QuantitySelector({ qty, setQty }) {
  return (
    <div className="qty-compact">
      <button onClick={() => setQty(q => Math.max(1, q-1))}>-</button>
      <div className="num" id="qtyDisplay">{qty}</div>
      <button onClick={() => setQty(q => Math.min(99, q+1))}>+</button>
    </div>
  )
}
