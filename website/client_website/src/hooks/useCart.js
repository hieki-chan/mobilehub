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
      const data = await CartApi.getCart(userId)
      setCart(data.items || [])
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  const add = async (item, quantity) => {
    if (!userId) return
    setLoading(true)
    try {
      const data = await CartApi.addItem(userId, item)
      setCart(data.items || [])
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
    setLoading(true)
    try {
      const data = await CartApi.updateItem(userId, { itemId: item.id, quantity: qty })
      setCart(prev => prev.map(i => i.id === item.id ? { ...i, qty: qty } : i))
    } finally {
      setLoading(false)
    }
  }

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

  const count = () => cart.reduce((sum, i) => sum + (i.qty || 1), 0)
  const total = () => cart.reduce((sum, i) => sum + (i.price * (i.qty || 1)), 0)

  useEffect(() => {
    fetchCart()
  }, [userId])

  return { cart, add, remove, updateQty, clear, count, total, loading, error }
}
