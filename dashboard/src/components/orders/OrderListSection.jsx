import React, { useState, useEffect } from "react";
import ListPageLayout from "../common_components/ListPageLayout";
import OrderGridView from "./OrderGridView";
import OrderTableView from "./OrderTableView";
import {
  fetchAdminOrdersPaged,
  deleteAdminOrder,
} from "../../api/OrderApi";
import { showPopupConfirm } from "../common_components/PopupConfirm";

const OrderListSection = ({ reloadFlag }) => {
  const [orders, setOrders] = useState([]);
  const [filteredOrders, setFilteredOrders] = useState([]);

  const [viewMode, setViewMode] = useState("table");
  const [showFilters, setShowFilters] = useState(false);

  const [selectedStatus, setSelectedStatus] = useState("ALL");
  const [selectedPayment, setSelectedPayment] = useState("ALL");

  const [searchField, setSearchField] = useState("code");
  const [searchQuery, setSearchQuery] = useState("");

  const searchOptions = [
    { label: "Mã đơn", value: "code" },
    { label: "Người đặt", value: "customerName" },
    { label: "Email", value: "email" },
    { label: "Trạng thái", value: "status" },
  ];

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(15);
  const totalItems = filteredOrders.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const startItem = (currentPage - 1) * itemsPerPage + 1;
  const endItem = Math.min(currentPage * itemsPerPage, totalItems);

  useEffect(() => {
    loadOrders();
  }, [reloadFlag]);

  const loadOrders = async () => {
    try {
      const data = await fetchAdminOrdersPaged(0, 100);
      if (data?.content) {
        const normalized = data.content.map((o) => ({
          id: o.id ?? 0,
          code: o.code ?? "—",
          customerName: o.customerName ?? "Khách vãng lai",
          email: o.email ?? "—",
          total: o.totalAmount ?? 0,
          status: o.status ?? "PENDING",
          payment: o.paymentMethod ?? "COD",
          createdDate: o.createdAt ?? "—",
        }));
        setOrders(normalized);
        setFilteredOrders(normalized);
      }
    } catch (err) {
      console.error("❌ Lỗi tải đơn hàng:", err);
    }
  };

  useEffect(() => {
    handleFilter();
  }, [orders, selectedStatus, selectedPayment, searchQuery, searchField]);

  const handleFilter = () => {
    let result = [...orders];

    if (selectedStatus !== "ALL")
      result = result.filter((o) => o.status === selectedStatus);

    if (selectedPayment !== "ALL")
      result = result.filter((o) => o.payment === selectedPayment);

    if (searchQuery.trim() !== "")
      result = result.filter((o) => {
        const val = o[searchField] ?? "";
        return val.toString().toLowerCase().includes(searchQuery.toLowerCase());
      });

    setFilteredOrders(result);
    setCurrentPage(1);
  };

  const getPageOrders = () => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredOrders.slice(start, start + itemsPerPage);
  };

  const handleDelete = async (id) => {
    const confirmed = await showPopupConfirm(
      "Xác nhận xoá đơn hàng",
      "Bạn có chắc muốn xoá đơn hàng này?"
    );
    if (!confirmed) return;
    try {
      await deleteAdminOrder(id);
      setOrders((prev) => prev.filter((o) => o.id !== id));
    } catch (err) {
      console.error("❌ Lỗi xoá đơn hàng:", err);
    }
  };

  const exportToCSV = () => {
    const csv = [
      ["Mã đơn", "Khách hàng", "Email", "Tổng tiền", "Thanh toán", "Trạng thái"].join(","),
      ...filteredOrders.map((o) =>
        [
          o.code,
          o.customerName,
          o.email,
          o.total,
          o.payment,
          o.status,
        ].join(",")
      ),
    ].join("\n");

    const blob = new Blob([csv], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "don_hang.csv";
    a.click();
  };

  const handleRefresh = () => {
    setSearchQuery("");
    loadOrders();
    setCurrentPage(1);
  };

  return (
    <ListPageLayout
      title="Đơn hàng"
      addLabel=""
      viewMode={viewMode}
      setViewMode={setViewMode}
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
      {showFilters && (
        <div className="sticky top-[128px] z-30 p-4 border-b border-gray-200 bg-gray-50 flex flex-wrap items-center gap-3 sm:gap-4">
          <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2">
            <label className="text-sm font-medium text-gray-700">
              Thanh toán:
            </label>
            <select
              value={selectedPayment}
              onChange={(e) => setSelectedPayment(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-1.5 text-sm text-gray-800 focus:ring-2 focus:ring-gray-900 focus:outline-none w-full sm:w-auto"
            >
              <option value="ALL">Tất cả</option>
              <option value="COD">COD</option>
              <option value="VNPAY">VNPAY</option>
              <option value="PAYOS">PAYOS</option>
            </select>
          </div>

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
              <option value="PENDING">PENDING</option>
              <option value="PAID">PAID</option>
              <option value="SHIPPED">SHIPPED</option>
              <option value="CANCELLED">CANCELLED</option>
            </select>
          </div>
        </div>
      )}

      <div className="relative">
        {viewMode === "table" ? (
          <OrderTableView
            orders={getPageOrders()}
            onDelete={handleDelete}
          />
        ) : (
          <OrderGridView
            orders={getPageOrders()}
            onDelete={handleDelete}
          />
        )}
      </div>
    </ListPageLayout>
  );
};

export default OrderListSection;
