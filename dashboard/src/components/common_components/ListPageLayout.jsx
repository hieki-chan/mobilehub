import React, { useState, useRef, useEffect } from "react";
import {
    Search,
    SlidersHorizontal,
    Eye,
    MoreHorizontal,
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    ChevronsLeft,
    ChevronsRight,
    RotateCcw
} from "lucide-react";
import { createPortal } from "react-dom";


const ListPageLayout = ({
    title = "Danh sách",
    addLabel = "Thêm mới",
    viewMode,
    setViewMode,
    onAdd,
    onExport,
    onToggleFilters,
    children,
    currentPage,
    totalPages,
    itemsPerPage,
    totalItems,
    startItem,
    endItem,
    onPageChange,
    onItemsPerPageChange,
    searchQuery,
    onSearchChange,
    searchField,
    onSearchFieldChange,
    searchOptions = [],
    onRefresh
}) => {
    const [showFilters, setShowFilters] = useState(false);
    const [showSearchMenu, setShowSearchMenu] = useState(false);
    const searchMenuRef = useRef(null);
    const dropdownRef = useRef(null);

    const handleToggleFilters = () => {
        setShowFilters((prev) => !prev);
        onToggleFilters?.();
    };

    // 🔒 Đóng menu chọn tiêu chí khi click outside
    useEffect(() => {
        const handleClickOutside = (e) => {
            // Nếu click không nằm trong cả ô search lẫn dropdown
            if (
                searchMenuRef.current &&
                !searchMenuRef.current.contains(e.target) &&
                dropdownRef.current &&
                !dropdownRef.current.contains(e.target)
            ) {
                setShowSearchMenu(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const currentOption =
        searchOptions.find((o) => o.value === searchField)?.label || "Không xác định";

    return (
        <div className="mx-auto bg-white border-b border-gray-200">
            {/* ===== Toolbar ===== */}
            <div className="p-4 border-b border-gray-200 sticky top-[64px] bg-white z-30">
                <div className="flex flex-wrap items-center justify-between gap-3 md:gap-4 mb-2">
                    {/* Bên trái */}
                    <div className="flex flex-wrap items-center gap-2 sm:gap-3">
                        <div className="flex items-center gap-1 text-sm">
                            <button
                                onClick={() => setViewMode("table")}
                                className={`px-3 py-1.5 rounded font-medium flex items-center gap-1.5 ${viewMode === "table"
                                    ? "bg-gray-900 text-white"
                                    : "text-gray-600 hover:bg-gray-50"
                                    }`}
                            >
                                <div className="w-3 h-3 border-2 border-current"></div>
                                <span className="hidden sm:inline">Bảng</span>
                            </button>

                            <button
                                onClick={() => setViewMode("grid")}
                                className={`px-3 py-1.5 rounded font-medium flex items-center gap-1.5 ${viewMode === "grid"
                                    ? "bg-gray-900 text-white"
                                    : "text-gray-600 hover:bg-gray-50"
                                    }`}
                            >
                                <div className="w-3 h-3 grid grid-cols-2 gap-0.5">
                                    <div className="bg-current"></div>
                                    <div className="bg-current"></div>
                                    <div className="bg-current"></div>
                                    <div className="bg-current"></div>
                                </div>
                                <span className="hidden sm:inline">Lưới</span>
                            </button>
                        </div>

                        <div className="hidden sm:block h-5 w-px bg-gray-300"></div>

                        <button
                            onClick={handleToggleFilters}
                            className={`flex items-center gap-1.5 px-3 py-1.5 text-sm rounded transition-all ${showFilters
                                ? "bg-orange-500 text-white hover:bg-orange-600"
                                : "text-gray-900 hover:bg-orange-600 hover:text-white"
                                }`}
                        >
                            <SlidersHorizontal size={14} />
                            <span className="hidden sm:inline">Bộ lọc</span>
                        </button>
                    </div>

                    {/* Bên phải */}
                    <div className="flex flex-wrap items-center gap-2 sm:gap-3">
                        {/* 🔍 Ô tìm kiếm */}
                        <div className="relative flex items-center gap-2" ref={searchMenuRef}>
                            <div className="flex items-center border border-gray-300 rounded-md overflow-hidden">
                                <Search size={16} className="text-gray-500 ml-2" />
                                <input
                                    type="text"
                                    placeholder="Nhập nội dung tìm kiếm..."
                                    value={searchQuery}
                                    onChange={(e) => onSearchChange?.(e.target.value)}
                                    className="px-2 py-1.5 text-sm text-gray-700 focus:outline-none w-40 sm:w-56"
                                />

                                {/* 🏷️ Label hiển thị tiêu chí hiện tại - có thể click */}
                                <button
                                    onClick={() => setShowSearchMenu((prev) => !prev)}
                                    className="text-xs sm:text-sm text-gray-600 border-l border-gray-300 px-2 py-1 bg-gray-50 whitespace-nowrap hover:bg-gray-100 focus:outline-none select-none"
                                >
                                    Theo: {searchOptions.find((o) => o.value === searchField)?.label || "—"}
                                </button>
                            </div>

                            {/* Dropdown xuất hiện ngay dưới nút "Theo:" */}
                            {showSearchMenu &&
                                createPortal(
                                    <div
                                        ref={dropdownRef}
                                        className="fixed bg-white border border-gray-200 rounded-md shadow-lg z-[99999] w-[160px] sm:w-[180px]"
                                        style={{
                                            top:
                                                searchMenuRef.current?.getBoundingClientRect().bottom +
                                                window.scrollY +
                                                4, // vị trí ngay dưới
                                            left:
                                                searchMenuRef.current?.getBoundingClientRect().right -
                                                180 +
                                                window.scrollX, // canh phải đẹp
                                        }}
                                    >
                                        {searchOptions.map((opt) => (
                                            <button
                                                key={opt.value}
                                                onClick={() => {
                                                    onSearchFieldChange?.(opt.value);
                                                    setShowSearchMenu(false);
                                                }}
                                                className={`pointer-events-auto w-full text-left px-4 py-2 text-sm flex justify-between ${searchField === opt.value
                                                    ? "bg-gray-100 text-gray-900 font-medium"
                                                    : "text-gray-700 hover:bg-gray-50"
                                                    }`}
                                            >
                                                {opt.label}
                                                {searchField === opt.value && <span>✓</span>}
                                            </button>
                                        ))}
                                    </div>,
                                    document.body
                                )}
                        </div>


                        {/* Xuất file */}
                        <button
                            onClick={onExport}
                            className="px-3 py-1.5 text-sm bg-gray-900 text-white rounded hover:bg-gray-800"
                        >
                            <span className="hidden sm:inline">Xuất file</span>
                            <Eye className="sm:hidden" size={16} />
                        </button>

                        {/* Thêm mới */}
                        <button
                            onClick={onAdd}
                            className="px-3 py-1.5 text-sm bg-orange-500 text-white rounded hover:bg-orange-600 flex items-center gap-1.5"
                        >
                            <span className="hidden sm:inline">{addLabel}</span>
                            <ChevronDown size={14} />
                        </button>

                        <button
                            onClick={onRefresh}
                            className="p-1.5 text-gray-600 hover:bg-gray-100 rounded"
                            title="Làm mới danh sách"
                        >
                            <RotateCcw size={18} />
                        </button>
                    </div>
                </div>
            </div>

            {/* ===== Nội dung chính ===== */}
            <div>{children}</div>

            {/* ===== Phân trang ===== */}
            <div className="flex items-center justify-between px-4 py-3 border-t border-gray-200 text-sm">
                <div className="flex items-center gap-4">
                    <div className="flex items-center gap-2">
                        <span className="text-gray-600">Số dòng / trang:</span>
                        <select
                            value={itemsPerPage}
                            onChange={(e) => onItemsPerPageChange(Number(e.target.value))}
                            className="border border-gray-300 rounded px-2 py-1 text-gray-700"
                        >
                            <option value={5}>5</option>
                            <option value={15}>15</option>
                            <option value={25}>25</option>
                            <option value={50}>50</option>
                        </select>
                    </div>
                    <span className="text-gray-600">
                        {startItem}-{endItem} trên {totalItems} dòng
                    </span>
                </div>

                <div className="flex items-center gap-1">
                    <button
                        onClick={() => onPageChange(1)}
                        disabled={currentPage === 1}
                        className="p-1.5 text-gray-600 hover:bg-gray-100 rounded disabled:opacity-30"
                    >
                        <ChevronsLeft size={18} />
                    </button>
                    <button
                        onClick={() => onPageChange(currentPage - 1)}
                        disabled={currentPage === 1}
                        className="p-1.5 text-gray-600 hover:bg-gray-100 rounded disabled:opacity-30"
                    >
                        <ChevronLeft size={18} />
                    </button>

                    <span className="mx-3 text-gray-700 font-medium">
                        Trang {currentPage}/{totalPages}
                    </span>

                    <button
                        onClick={() => onPageChange(currentPage + 1)}
                        disabled={currentPage === totalPages}
                        className="p-1.5 text-gray-600 hover:bg-gray-100 rounded disabled:opacity-30"
                    >
                        <ChevronRight size={18} />
                    </button>
                    <button
                        onClick={() => onPageChange(totalPages)}
                        disabled={currentPage === totalPages}
                        className="p-1.5 text-gray-600 hover:bg-gray-100 rounded disabled:opacity-30"
                    >
                        <ChevronsRight size={18} />
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ListPageLayout;
