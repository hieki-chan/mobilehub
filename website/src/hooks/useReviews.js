// src/hooks/useReviews.js
import { useState, useEffect } from 'react'
export default function useReviews(productId){
  const KEY = 'mh_reviews_' + productId
  const read = ()=> { try { return JSON.parse(localStorage.getItem(KEY) || '[]') } catch { return [] } }
  const [list, setList] = useState(read)
  useEffect(()=> localStorage.setItem(KEY, JSON.stringify(list)), [list])
  const add = (r) => setList(prev => [r, ...prev])
  return { list, add }
}
