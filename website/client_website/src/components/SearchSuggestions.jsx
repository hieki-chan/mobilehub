import React, { memo } from 'react'
import SearchSuggestionItem from './SearchSuggestionItem'

const SearchSuggestions = memo(({ 
  suggestions,
  query,
  activeIndex,
  onSelect,
  onViewAll,
  onMouseEnter,
  onMouseLeave
}) => {
  if (suggestions.length === 0) return null

  return (
    <ul 
      id="search-suggestions"
      className="search-suggestions" 
      role="listbox"
      aria-label="Gợi ý sản phẩm"
    >
      {suggestions.map((product, index) => (
        <SearchSuggestionItem
          key={product.id}
          product={product}
          index={index}
          isActive={index === activeIndex}
          onClick={() => onSelect(product.id)}
          onMouseEnter={() => onMouseEnter(index)}
          onMouseLeave={onMouseLeave}
        />
      ))}
      
      <li 
        className="search-suggest-more" 
        onClick={onViewAll}
        role="option"
      >
        Xem tất cả kết quả cho "{query}"
      </li>
    </ul>
  )
})

SearchSuggestions.displayName = 'SearchSuggestions'

export default SearchSuggestions