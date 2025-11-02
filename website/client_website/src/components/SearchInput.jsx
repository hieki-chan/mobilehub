import React, { forwardRef } from 'react'

const SearchInput = forwardRef(({ 
  value, 
  onChange, 
  onKeyDown,
  onSearch,
  placeholder = 'Tìm kiếm sản phẩm...',
  isOpen,
  activeIndex
}, ref) => {
  return (
    <div className="search-input-wrap">
      <input
        ref={ref}
        value={value}
        type="search"
        autoComplete="off"
        onChange={onChange}
        onKeyDown={onKeyDown}
        placeholder={placeholder}
        aria-label="Tìm kiếm sản phẩm"
        aria-autocomplete="list"
        aria-controls="search-suggestions"
        aria-expanded={isOpen}
        aria-activedescendant={
          activeIndex >= 0 ? `suggestion-${activeIndex}` : undefined
        }
      />
      <button 
        className="search-btn" 
        aria-label="Tìm kiếm" 
        onClick={onSearch}
        type="button"
      >
        <i className="fa fa-magnifying-glass"></i>
      </button>
    </div>
  )
})

SearchInput.displayName = 'SearchInput'

export default SearchInput