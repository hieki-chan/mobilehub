import { useState, useEffect } from 'react'
import { CartApi } from '../api/cartApi'

export default function useCart() {
  const [cart, setCart] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const userId = JSON.parse(localStorage.getItem("user") || "{}").id;

  const fetchCart = async () => {
    if (!userId) return
    setLoading(true)
    try {
      const res = await CartApi.getCart(userId)
      const items = res?.items || []
      setCart(items)
      console.log(items);
    } catch (err) {
      setError(err?.message || 'Lỗi khi tải giỏ hàng')
    } finally {
      setLoading(false)
    }
  }

  const add = async (item, quantity = 1) => {
    if (!userId) return
    setLoading(true)
    try {
      const res = await CartApi.addItem(userId, item)
      const items = res?.result?.items || []
      setCart(items)
      //console.log(cart);
    } finally {
      setLoading(false)
    }
  }

  const remove = async (item) => {
    if (!userId) return
    setLoading(true)
    try {
      await CartApi.removeItem(userId, item.id)
      setCart(prev => prev.filter(i => i.id !== item.id))
    } finally {
      setLoading(false)
    }
  }

  const updateQty = async (item, qty) => {
    if (!userId) return
    //setLoading(true)
    try {
      const res = await CartApi.updateItemQuantity(userId, item.id, qty);
      const { quantity } = res || {}
      //console.log(quantity)

      if (quantity !== undefined) {
        setCart(prev =>
          prev.map(i =>
            i.id === item.id ? { ...i, quantity: quantity } : i
          )
        )
      }
    } finally {
      //setLoading(false)
    }
  }

  const updateVariant = async (item, variantId) => {
    if (!userId) return;
    try {
      const updated = await CartApi.updateItemVariant(userId, item.id, variantId);
      setCart(prev =>
        prev.map(i => i.id === item.id ? { ...i, variantId: updated.variantId } : i)
      );
    } finally { }
  };

  const clear = async () => {
    if (!userId) return
    setLoading(true)
    try {
      await CartApi.clearCart(userId)
      setCart([])
    } finally {
      setLoading(false)
    }
  }

  const count = () => cart.reduce((sum, i) => sum + (i.quantity || 1), 0)
  const total = () => cart.reduce((sum, item) => {
    const variant = item.variants?.find(v => v.id === item.variantId);
    if (!variant) return sum;

    const price = variant.price || 0;

    const finalPrice = price * (1 - (item.discountInPercent || 0) / 100);

    return sum + finalPrice * (item.quantity || 1);
  }, 0);


  useEffect(() => {
    fetchCart()
  }, [userId])

  return { cart, add, remove, updateQty, updateVariant, clear, count, total, loading, error }
}
