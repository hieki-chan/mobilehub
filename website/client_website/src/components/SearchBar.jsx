import React, { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { products as mockProducts } from '../data/products'
import '../styles/search-bar.css'

export default function SearchBar({ placeholder = 'Tìm kiếm sản phẩm...' }) {
  const navigate = useNavigate()
  const [q, setQ] = useState('')
  const [suggestions, setSuggestions] = useState([])
  const [open, setOpen] = useState(false)
  const [activeIndex, setActiveIndex] = useState(-1)
  const inputRef = useRef(null)
  const containerRef = useRef(null)
  const DEBOUNCE_MS = 220

  // debounce search
  useEffect(() => {
    if (!q || q.trim().length < 1) {
      setSuggestions([])
      setOpen(false)
      setActiveIndex(-1)
      return
    }
    const id = setTimeout(() => {
      const term = q.trim().toLowerCase()
      const results = (mockProducts || []).filter(p => p.name.toLowerCase().includes(term)).slice(0, 6)
      setSuggestions(results)
      setOpen(true)
      setActiveIndex(-1)
    }, DEBOUNCE_MS)
    return () => clearTimeout(id)
  }, [q])

  // click outside to close
  useEffect(() => {
    function onDoc(e) {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setOpen(false)
        setActiveIndex(-1)
      }
    }
    document.addEventListener('mousedown', onDoc)
    return () => document.removeEventListener('mousedown', onDoc)
  }, [])

  function submitSearch(term) {
    const encoded = encodeURIComponent((term || q).trim())
    if (!encoded) return
    setOpen(false)
    setActiveIndex(-1)
    navigate(`/search?q=${encoded}`)
  }

  function onKeyDown(e) {
    if (!open || !suggestions.length) {
      if (e.key === 'Enter') submitSearch()
      return
    }
    if (e.key === 'ArrowDown') {
      e.preventDefault()
      setActiveIndex(i => Math.min(i + 1, suggestions.length - 1))
    } else if (e.key === 'ArrowUp') {
      e.preventDefault()
      setActiveIndex(i => Math.max(i - 1, 0))
    } else if (e.key === 'Enter') {
      e.preventDefault()
      const sel = activeIndex >= 0 ? suggestions[activeIndex] : suggestions[0]
      if (sel) navigate(`/product/${sel.id}`)
      setOpen(false)
      setActiveIndex(-1)
    } else if (e.key === 'Escape') {
      setOpen(false)
      setActiveIndex(-1)
    }
  }

  return (
    <div className="searchbar" ref={containerRef}>
      <div className="search-input-wrap">
        <input
          ref={inputRef}
          value={q}
          type="search"
          autoComplete="off"
          onChange={e => setQ(e.target.value)}
          onKeyDown={onKeyDown}
          placeholder={placeholder}
          aria-label="Tìm kiếm sản phẩm"
        />
        <button className="search-btn" aria-label="Tìm kiếm" onClick={() => submitSearch()}>
          <i className="fa fa-magnifying-glass"></i>

        </button>
      </div>


      {open && suggestions.length > 0 && (
        <ul className="search-suggestions" role="listbox">
          {suggestions.map((s, i) => (
            <li
              key={s.id}
              role="option"
              aria-selected={i === activeIndex}
              className={i === activeIndex ? 'active' : ''}
              onMouseEnter={() => setActiveIndex(i)}
              onMouseLeave={() => setActiveIndex(-1)}
              onClick={() => navigate(`/product/${s.id}`)}
            >
              <img src={s.image || s.images?.[0] || '/no-image.png'} alt={s.name} />
              <div className="s-meta">
                <div className="s-name">{s.name}</div>
                <div className="s-price">{s.price ? new Intl.NumberFormat('vi-VN').format(s.price) + '₫' : ''}</div>
              </div>
            </li>
          ))}
          <li className="search-suggest-more" onClick={() => submitSearch()}>
            Xem tất cả kết quả cho “{q}”
          </li>
        </ul>
      )}
    </div>
  )
}
