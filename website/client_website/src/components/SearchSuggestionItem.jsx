import React, { memo } from 'react'
import { formatPrice } from '../utils/formatPrice'

const SearchSuggestionItem = memo(({ 
  product, 
  isActive,
  onClick,
  onMouseEnter,
  onMouseLeave,
  index
}) => {
  const handleImageError = (e) => {
    e.target.onerror = null
    e.target.src = '/no-image.png'
  }

  return (
    <li
      id={`suggestion-${index}`}
      role="option"
      aria-selected={isActive}
      className={isActive ? 'active' : ''}
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      onClick={onClick}
    >
      <img 
        src={product.image || product.images?.[0] || '/no-image.png'} 
        alt={product.name}
        onError={handleImageError}
        loading="lazy"
      />
      <div className="s-meta">
        <div className="s-name">{product.name}</div>
        {product.price && (
          <div className="s-price">{formatPrice(product.price)}</div>
        )}
      </div>
    </li>
  )
})

SearchSuggestionItem.displayName = 'SearchSuggestionItem'

export default SearchSuggestionItem