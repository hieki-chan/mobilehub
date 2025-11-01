// src/hooks/useFav.js
import { useState, useEffect } from 'react'
const KEY = 'mh_fav'
function read(){ try{ return JSON.parse(localStorage.getItem(KEY)||'[]') }catch{return []}}
export default function useFav(){
  const [fav, setFav] = useState(read())
  useEffect(()=> localStorage.setItem(KEY, JSON.stringify(fav)), [fav])
  const toggle = (id)=> setFav(prev => prev.includes(id) ? prev.filter(x=>x!==id) : [...prev, id])
  const isFav = (id) => fav.includes(id)
  return { fav, toggle, isFav }
}
