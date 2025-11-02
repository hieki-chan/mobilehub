import { useCallback } from 'react'

export function useSearchKeyboard({
  isOpen,
  suggestions,
  activeIndex,
  setActiveIndex,
  onSelect,
  onSubmit,
  onClose
}) {
  const handleKeyDown = useCallback((e) => {
    if (!isOpen || suggestions.length === 0) {
      if (e.key === 'Enter') {
        onSubmit()
      }
      return
    }

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault()
        setActiveIndex(prev => 
          prev < suggestions.length - 1 ? prev + 1 : prev
        )
        break
        
      case 'ArrowUp':
        e.preventDefault()
        setActiveIndex(prev => prev > 0 ? prev - 1 : -1)
        break
        
      case 'Enter':
        e.preventDefault()
        if (activeIndex >= 0 && suggestions[activeIndex]) {
          onSelect(suggestions[activeIndex].id)
        } else {
          onSubmit()
        }
        break
        
      case 'Escape':
        e.preventDefault()
        onClose()
        break
        
      default:
        break
    }
  }, [isOpen, suggestions, activeIndex, setActiveIndex, onSelect, onSubmit, onClose])

  return { handleKeyDown }
}