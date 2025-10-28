// src/hooks/useCart.js
import { useCallback, useEffect, useRef, useState } from 'react'

const STORAGE_KEY = 'mh_cart'

function readStoredCart() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
  } catch {
    return []
  }
}

export default function useCart() {
  const [cart, setCart] = useState(() => readStoredCart())
  const cartRef = useRef(cart)

  // keep ref in sync with state
  useEffect(() => {
    cartRef.current = cart
  }, [cart])

  // persist cart and notify listeners whenever cart changes
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(cart))
    } catch (e) {
      // ignore storage errors
    }
    window.dispatchEvent(new Event('cart-changed'))
  }, [cart])

  // listen to storage events (other tabs) and custom cart-changed (same tab)
  useEffect(() => {
    const onStorage = (e) => {
      if (e.key === STORAGE_KEY) {
        const newCart = readStoredCart()
        if (JSON.stringify(newCart) !== JSON.stringify(cartRef.current)) {
          setCart(newCart)
        }
      }
    }

    const onCartChanged = () => {
      const newCart = readStoredCart()
      if (JSON.stringify(newCart) !== JSON.stringify(cartRef.current)) {
        setCart(newCart)
      }
    }

    window.addEventListener('storage', onStorage)
    window.addEventListener('cart-changed', onCartChanged)
    return () => {
      window.removeEventListener('storage', onStorage)
      window.removeEventListener('cart-changed', onCartChanged)
    }
  }, [])

  // replace cart (only updates when different)
  const replaceCart = useCallback((newCart) => {
    setCart(prev => {
      const a = JSON.stringify(newCart || [])
      const b = JSON.stringify(prev || [])
      if (a !== b) return newCart || []
      return prev
    })
  }, [])

  // helper match by id + variants
  const matchItem = useCallback((a, b) => {
    return String(a.id) === String(b.id) &&
      (a.capacity || '') === (b.capacity || '') &&
      (a.color || '') === (b.color || '')
  }, [])

  // merge server cart into local cart; sumQty option available
  const mergeWith = useCallback((serverCart = [], { sumQty = false } = {}) => {
    setCart(prev => {
      const local = Array.isArray(prev) ? prev : []
      const map = new Map()
      local.forEach(item => map.set(item.id + '|' + (item.capacity||'') + '|' + (item.color||''), { ...item }))

      serverCart.forEach(s => {
        const key = s.id + '|' + (s.capacity||'') + '|' + (s.color||'')
        const existing = map.get(key)
        if (existing) {
          if (sumQty) {
            existing.qty = (Number(existing.qty) || 0) + (Number(s.qty) || 0)
          } else {
            map.set(key, { ...s })
          }
        } else {
          map.set(key, { ...s })
        }
      })

      const merged = Array.from(map.values())
      if (JSON.stringify(merged) !== JSON.stringify(local)) return merged
      return prev
    })
  }, [])

  // primary add implementation
  const addItem = useCallback((item) => {
    const normalized = {
      id: item.id,
      name: item.name,
      price: typeof item.price === 'number' ? item.price : Number(item.price) || 0,
      qty: Math.max(1, Number(item.qty || 1)),
      capacity: item.capacity || null,
      color: item.color || null,
      image: item.image || (item.images && item.images[0]) || null
    }

    setCart(prev => {
      const local = Array.isArray(prev) ? prev : []
      const idx = local.findIndex(i => matchItem(i, normalized))
      if (idx !== -1) {
        const copy = local.map(i => ({ ...i }))
        copy[idx].qty = Math.min(999, Number(copy[idx].qty || 0) + normalized.qty)
        return copy
      }
      return [...local, normalized]
    })
  }, [matchItem])

  const removeItem = useCallback((target) => {
    setCart(prev => {
      const local = Array.isArray(prev) ? prev : []
      if (typeof target === 'number') {
        const copy = [...local]; copy.splice(target, 1); return copy
      }
      return local.filter(i => !matchItem(i, target))
    })
  }, [matchItem])

  const updateItem = useCallback((idOrTarget, changes) => {
    setCart(prev => {
      const local = Array.isArray(prev) ? prev : []
      if (typeof idOrTarget === 'string' || typeof idOrTarget === 'number') {
        return local.map(p => p.id === idOrTarget ? { ...p, ...changes } : p)
      }
      const copy = local.map(i => ({ ...i }))
      const idx = copy.findIndex(i => matchItem(i, idOrTarget))
      if (idx === -1) return prev
      copy[idx] = { ...copy[idx], ...changes }
      return copy
    })
  }, [matchItem])

  const remove = useCallback((target) => removeItem(target), [removeItem])

  // updateQty helper (compat with Cart.jsx)
  const updateQty = useCallback((target, qty) => {
    setCart(prev => {
      const local = Array.isArray(prev) ? prev : []
      const copy = local.map(i => ({ ...i }))
      const idx = copy.findIndex(i => matchItem(i, target))
      if (idx === -1) return prev
      const q = Number(qty)
      if (q <= 0) {
        copy.splice(idx, 1)
      } else {
        copy[idx].qty = Math.min(999, q)
      }
      return copy
    })
  }, [matchItem])

  const clearCart = useCallback(() => setCart([]), [])

  const getTotal = useCallback(() => {
    return (cartRef.current || []).reduce((s, it) => s + (Number(it.price) || 0) * (Number(it.qty) || 0), 0)
  }, [])

  const count = useCallback(() => {
    return (cartRef.current || []).reduce((s, it) => s + (Number(it.qty) || 0), 0)
  }, [])

  // expose both modern names and legacy aliases used across the app
  return {
    cart,
    setCart,
    // modern functions
    replaceCart,
    mergeWith,
    addItem,
    updateItem,
    removeItem,
    clearCart,
    getTotal,
    // legacy / app-compatible aliases
    add: addItem,
    remove,
    updateQty,
    clear: clearCart,
    total: getTotal,
    count
  }
}
