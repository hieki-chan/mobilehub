import React, { useState, useEffect } from "react";
import ListPageLayout from "../common_components/ListPageLayout";
import ListFilterBar from "../common_components/ListFilterBar";
import ProductGridView from "./ProductGridView";
import ProductTableView from "./ProductTableView";
import { fetchAdminProducts, deleteAdminProduct } from "../../api/ProductApi";
import { showPopupConfirm } from "../common_components/PopupConfirm";

const ProductListSection = ({ onAddClick, onEditClick, reloadFlag }) => {
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);

  const [viewMode, setViewMode] = useState("table");

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
        const normalized = data.content.map((item) => {
          const variant = item.defaultVariant ?? {};
          return {
            id: item.id ?? 0,
            name: item.name ?? "Chưa có tên",
            status: item.status ?? "INACTIVE",
            sales: item.discountInPercent ?? 0,

            color_label: variant.color_label ?? "",
            color_hex: variant.color_hex ?? "",
            ram: variant.ram,
            storage_cap: variant.storage_cap,
            price: Number(variant.price) || 0,
            imageUrl:
              variant.imageUrl && variant.imageUrl.trim() !== ""
                ? variant.imageUrl
                : "https://via.placeholder.com/100x100?text=No+Image",

            category: "Không xác định",
            stock: 0,
          };
        });

        setProducts(normalized);
        setFilteredProducts(normalized);
      }
    } catch (err) {
      console.error("❌ Lỗi tải sản phẩm:", err);
    }
  };


  const handleFilter = () => {
    let result = [...products];

    if (selectedCategory !== "ALL") {
      result = result.filter((p) => p.category === selectedCategory);
    }

    if (selectedStatus !== "ALL") {
      result = result.filter((p) => p.status === selectedStatus);
    }

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

  const getPageProducts = () => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredProducts.slice(start, start + itemsPerPage);
  };

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
      <ListFilterBar
        filters={[
          {
            label: "Danh mục",
            value: selectedCategory,
            onChange: setSelectedCategory,
            options: [
              { label: "Tất cả", value: "ALL" },
              { label: "Electronics", value: "Electronics" },
              { label: "Accessories", value: "Accessories" },
              { label: "Fitness", value: "Fitness" },
              { label: "Home Usage", value: "Home Usage" },
            ],
          },
          {
            label: "Trạng thái",
            value: selectedStatus,
            onChange: setSelectedStatus,
            options: [
              { label: "Tất cả", value: "ALL" },
              { label: "ACTIVE", value: "ACTIVE" },
              { label: "INACTIVE", value: "INACTIVE" },
            ],
          },
        ]}
      />

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
