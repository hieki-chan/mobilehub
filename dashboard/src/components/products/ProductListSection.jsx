import React, { useState, useEffect } from "react";
import ListPageLayout from "../common_components/ListPageLayout";
import ProductGridView from "./ProductGridView";
import ProductTableView from "./ProductTableView";
import { fetchAdminProducts, deleteAdminProduct } from "../../api/ProductApi";
import { showPopupConfirm } from "../common_components/PopupConfirm";

const ProductListSection = ({ onAddClick, onEditClick, reloadFlag }) => {
  // ==== STATE CƠ BẢN ====
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);

  const [viewMode, setViewMode] = useState("table");
  const [showFilters, setShowFilters] = useState(false);

  // ==== FILTERS ====
  const [selectedStatus, setSelectedStatus] = useState("ALL");
  const [selectedCategory, setSelectedCategory] = useState("ALL");

  // ==== SEARCH ====
  const [searchField, setSearchField] = useState("name");
  const [searchQuery, setSearchQuery] = useState("");

  const searchOptions = [
    { label: "Tên sản phẩm", value: "name" },
    { label: "Danh mục", value: "category" },
    { label: "Trạng thái", value: "status" },
  ];

  // ==== PAGINATION ====
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(15);
  const totalItems = filteredProducts.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const startItem = (currentPage - 1) * itemsPerPage + 1;
  const endItem = Math.min(currentPage * itemsPerPage, totalItems);

  // ==== FETCH DATA ====
  useEffect(() => {
    loadProducts();
  }, [reloadFlag]);

  const loadProducts = async () => {
    try {
      const data = await fetchAdminProducts(0, 100);
      if (data?.content) {
        const normalized = data.content.map((item) => ({
          id: item.id ?? 0,
          name: item.name ?? "Chưa có tên",
          category: item.category ?? "Không xác định",
          price: item.price ?? 0,
          stock: item.stock ?? 0,
          sales: item.discountInPercent ?? 0,
          status: item.status ?? "INACTIVE",
          imageUrl:
            item.imageUrl && item.imageUrl.trim() !== ""
              ? item.imageUrl
              : "https://via.placeholder.com/100x100?text=No+Image",
        }));
        setProducts(normalized);
        setFilteredProducts(normalized);
      }
    } catch (err) {
      console.error("Lỗi tải sản phẩm:", err);
    }
  };

  // ==== HANDLE FILTER + SEARCH ====
  const handleFilter = () => {
    let result = [...products];

    // Lọc theo danh mục (nếu có)
    if (selectedCategory !== "ALL") {
      result = result.filter((p) => p.category === selectedCategory);
    }

    // Lọc theo trạng thái
    if (selectedStatus !== "ALL") {
      result = result.filter((p) => p.status === selectedStatus);
    }

    // Tìm kiếm
    if (searchQuery.trim() !== "") {
      result = result.filter((p) =>
        p[searchField]?.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    setFilteredProducts(result);
    setCurrentPage(1);
  };

  useEffect(() => {
    handleFilter();
  }, [searchQuery, searchField, selectedCategory, selectedStatus, products]);

  // ==== PHÂN TRANG ====
  const getPageProducts = () => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredProducts.slice(start, start + itemsPerPage);
  };

  // ==== XOÁ ====
  const handleDelete = async (id) => {
    const confirmed = await showPopupConfirm(
      "Xác nhận xoá sản phẩm",
      "Bạn có chắc muốn xoá sản phẩm này?"
    );
    if (!confirmed) return;
    try {
      await deleteAdminProduct(id);
      setProducts((prev) => prev.filter((p) => p.id !== id));
    } catch (err) {
      console.error("❌ Lỗi xoá:", err);
    }
  };

  // ==== EXPORT CSV ====
  const exportToCSV = () => {
    const csv = [
      ["Tên sản phẩm", "Danh mục", "Giá", "Kho", "Trạng thái"].join(","),
      ...filteredProducts.map((p) =>
        [
          p.name,
          p.category,
          p.price,
          p.stock,
          p.status === "ACTIVE" ? "Hoạt động" : "Ngừng",
        ].join(",")
      ),
    ].join("\n");

    const blob = new Blob([csv], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "san_pham.csv";
    a.click();
  };

  const handleRefresh = () => {
    setSearchQuery("");
    loadProducts();
    setCurrentPage(1);
  };

  // ==== RENDER ====
  return (
    <ListPageLayout
      title="Sản phẩm"
      addLabel="Thêm sản phẩm"
      viewMode={viewMode}
      setViewMode={setViewMode}
      onAdd={onAddClick}
      onExport={exportToCSV}
      onToggleFilters={() => setShowFilters((prev) => !prev)}
      searchQuery={searchQuery}
      onSearchChange={setSearchQuery}
      searchField={searchField}
      onSearchFieldChange={setSearchField}
      searchOptions={searchOptions}
      currentPage={currentPage}
      totalPages={totalPages}
      itemsPerPage={itemsPerPage}
      totalItems={totalItems}
      startItem={startItem}
      endItem={endItem}
      onPageChange={setCurrentPage}
      onItemsPerPageChange={(num) => {
        setItemsPerPage(num);
        setCurrentPage(1);
      }}
      onRefresh={handleRefresh}
    >
      {/* ==== BỘ LỌC ==== */}
      {showFilters && (
        <div className="sticky top-[128px] z-30 p-4 border-b border-gray-200 bg-gray-50 flex flex-wrap items-center gap-3 sm:gap-4">
          {/* Danh mục */}
          <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2">
            <label className="text-sm font-medium text-gray-700">
              Danh mục:
            </label>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-1.5 text-sm text-gray-800 focus:ring-2 focus:ring-gray-900 focus:outline-none w-full sm:w-auto"
            >
              <option value="ALL">Tất cả</option>
              <option value="Electronics">Electronics</option>
              <option value="Accessories">Accessories</option>
              <option value="Fitness">Fitness</option>
              <option value="Home Usage">Home Usage</option>
            </select>
          </div>

          {/* Trạng thái */}
          <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2 sm:ml-6">
            <label className="text-sm font-medium text-gray-700">
              Trạng thái:
            </label>
            <select
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-1.5 text-sm text-gray-800 focus:ring-2 focus:ring-gray-900 focus:outline-none w-full sm:w-auto"
            >
              <option value="ALL">Tất cả</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="INACTIVE">INACTIVE</option>
            </select>
          </div>
        </div>
      )}

      {/* ==== DANH SÁCH SẢN PHẨM ==== */}
      {viewMode === "table" ? (
        <ProductTableView
          products={getPageProducts()}
          onDelete={handleDelete}
          onEdit={onEditClick}
        />
      ) : (
        <ProductGridView
          products={getPageProducts()}
          onDelete={handleDelete}
          onEdit={onEditClick}
        />
      )}
    </ListPageLayout>
  );
};

export default ProductListSection;
