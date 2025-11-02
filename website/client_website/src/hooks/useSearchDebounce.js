import { useState, useEffect, useRef } from 'react'

export function useSearchDebounce(query, products, delay = 300) {
  const [suggestions, setSuggestions] = useState([])
  const [isSearching, setIsSearching] = useState(false)
  const timerRef = useRef(null)

  useEffect(() => {
    if (!query || query.trim().length < 1) {
      setSuggestions([])
      setIsSearching(false)
      return
    }

    if (timerRef.current) {
      clearTimeout(timerRef.current)
    }

    setIsSearching(true)

    timerRef.current = setTimeout(() => {
      const term = query.trim().toLowerCase()
      
      if (!Array.isArray(products) || products.length === 0) {
        setSuggestions([])
        setIsSearching(false)
        return
      }

      const results = products
        .filter(p => p?.name?.toLowerCase().includes(term))
        .slice(0, 6)

      setSuggestions(results)
      setIsSearching(false)
    }, delay)

    return () => {
      if (timerRef.current) {
        clearTimeout(timerRef.current)
      }
    }
  }, [query, products, delay])

  return { suggestions, isSearching }
}