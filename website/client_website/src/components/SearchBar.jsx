import React, { useState, useRef, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { products as mockProducts } from '../data/products'
import { useSearchDebounce } from '../hooks/useSearchDebounce'
import { useSearchKeyboard } from '../hooks/useSearchKeyboard'
import SearchInput from './SearchInput'
import SearchSuggestions from './SearchSuggestions'
import '../styles/search-bar.css'

export default function SearchBar({ 
  placeholder = 'Tìm kiếm sản phẩm...', 
  products 
}) {
  const navigate = useNavigate()
  const productList = products || mockProducts || []
  
  // State
  const [query, setQuery] = useState('')
  const [isOpen, setIsOpen] = useState(false)
  const [activeIndex, setActiveIndex] = useState(-1)
  
  // Refs
  const inputRef = useRef(null)
  const containerRef = useRef(null)

  // Custom hooks
  const { suggestions, isSearching } = useSearchDebounce(query, productList)

  // Handlers
  const handleSubmitSearch = useCallback((term) => {
    const searchTerm = (term || query).trim()
    if (!searchTerm) return

    const encoded = encodeURIComponent(searchTerm)
    setIsOpen(false)
    setActiveIndex(-1)
    navigate(`/search?q=${encoded}`)
  }, [query, navigate])

  const handleSelectProduct = useCallback((productId) => {
    if (!productId) return
    
    setIsOpen(false)
    setActiveIndex(-1)
    setQuery('')
    navigate(`/product/${productId}`)
  }, [navigate])

  const handleClose = useCallback(() => {
    setIsOpen(false)
    setActiveIndex(-1)
    inputRef.current?.blur()
  }, [])

  // Keyboard navigation
  const { handleKeyDown } = useSearchKeyboard({
    isOpen,
    suggestions,
    activeIndex,
    setActiveIndex,
    onSelect: handleSelectProduct,
    onSubmit: handleSubmitSearch,
    onClose: handleClose
  })

  // Click outside to close
  useEffect(() => {
    function handleClickOutside(e) {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setIsOpen(false)
        setActiveIndex(-1)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  // Open suggestions khi có kết quả
  useEffect(() => {
    if (suggestions.length > 0 && query.trim().length > 0) {
      setIsOpen(true)
    }
  }, [suggestions, query])

  return (
    <div className="searchbar" ref={containerRef}>
      <SearchInput
        ref={inputRef}
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onKeyDown={handleKeyDown}
        onSearch={() => handleSubmitSearch()}
        placeholder={placeholder}
        isOpen={isOpen}
        activeIndex={activeIndex}
      />

      {isSearching && (
        <div 
          className="search-loading" 
          aria-live="polite"
          style={{
            position: 'absolute',
            top: '100%',
            left: '50%',
            transform: 'translateX(-50%)',
            marginTop: '8px',
            fontSize: '12px',
            color: 'var(--muted)'
          }}
        >
          Đang tìm kiếm...
        </div>
      )}

      {isOpen && (
        <SearchSuggestions
          suggestions={suggestions}
          query={query}
          activeIndex={activeIndex}
          onSelect={handleSelectProduct}
          onViewAll={() => handleSubmitSearch()}
          onMouseEnter={setActiveIndex}
          onMouseLeave={() => setActiveIndex(-1)}
        />
      )}
    </div>
  )
}