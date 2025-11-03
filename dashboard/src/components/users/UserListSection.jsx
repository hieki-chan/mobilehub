import React, { useState, useEffect } from "react";
import ListPageLayout from "../common_components/ListPageLayout";
import UserGridView from "./UserGridView";
import UserTableView from "./UserTableView";
import UserFormModal from "./form/UserFormModal";

// ==== DỮ LIỆU MẪU ====
const initialUsers = [
  {
    id: 1,
    name: "Liam Smith",
    email: "smith@example.com",
    role: "ADMIN",
    status: "Active",
    createdDate: "24 Jun 2024, 9:23 pm",
    avatar: "LS",
    color: "bg-orange-500",
  },
  {
    id: 2,
    name: "Noah Anderson",
    email: "anderson@example.com",
    role: "EMPLOYEE",
    status: "Active",
    createdDate: "15 Mar 2023, 2:45 pm",
    avatar: "NA",
    color: "bg-teal-500",
  },
  {
    id: 3,
    name: "Isabella Garcia",
    email: "garcia@example.com",
    role: "USER",
    status: "Inactive",
    createdDate: "10 Apr 2022, 11:30 am",
    avatar: "IG",
    color: "bg-purple-500",
  },
  {
    id: 4,
    name: "William Clark",
    email: "clark@example.com",
    role: "ADMIN",
    status: "Active",
    createdDate: "28 Feb 2023, 6:15 pm",
    avatar: "WC",
    color: "bg-blue-500",
  },
  {
    id: 5,
    name: "James Hall",
    email: "hall@example.com",
    role: "EMPLOYEE",
    status: "Active",
    createdDate: "19 May 2024, 7:55 am",
    avatar: "JH",
    color: "bg-pink-500",
  },
];

const UserListSection = () => {
  const [users, setUsers] = useState(initialUsers);
  const [filteredUsers, setFilteredUsers] = useState(initialUsers);

  const [showForm, setShowForm] = useState(false);

  const [viewMode, setViewMode] = useState("table");
  const [showFilters, setShowFilters] = useState(false);

  // ==== BỘ LỌC ====
  const [selectedRole, setSelectedRole] = useState("ALL");
  const [selectedStatus, setSelectedStatus] = useState("ALL");

  // ==== TÌM KIẾM ====
  const [searchField, setSearchField] = useState("name");
  const [searchQuery, setSearchQuery] = useState("");

  const searchOptions = [
    { label: "Tên", value: "name" },
    { label: "Email", value: "email" },
    { label: "Vai trò", value: "role" },
    { label: "Trạng thái", value: "status" },
    { label: "Ngày tạo", value: "createdDate" },
  ];

  // ==== PHÂN TRANG ====
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(15);
  const totalItems = filteredUsers.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const startItem = (currentPage - 1) * itemsPerPage + 1;
  const endItem = Math.min(currentPage * itemsPerPage, totalItems);

  // ==== LỌC DỮ LIỆU ====
  const handleFilter = () => {
    let result = [...users];

    // Bộ lọc vai trò
    if (selectedRole !== "ALL") {
      result = result.filter((u) => u.role === selectedRole);
    }

    // Bộ lọc trạng thái
    if (selectedStatus !== "ALL") {
      result = result.filter((u) => u.status === selectedStatus);
    }

    // Tìm kiếm
    if (searchQuery.trim() !== "") {
      result = result.filter((u) =>
        u[searchField]?.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    setFilteredUsers(result);
    setCurrentPage(1);
  };

  // 🧠 Tự lọc lại mỗi khi searchQuery, searchField, role, status thay đổi
  useEffect(() => {
    handleFilter();
  }, [searchQuery, searchField, selectedRole, selectedStatus, users]);

  // ==== TÍNH NGƯỜI DÙNG TRANG HIỆN TẠI ====
  const getPageUsers = () => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredUsers.slice(start, start + itemsPerPage);
  };

  // Khi bấm "Thêm người dùng"
  const handleAddUser = () => {
    setShowForm(true);
  };

  // Khi submit form
  const handleSubmitUser = (formData) => {
    const newUser = {
      id: users.length + 1,
      ...formData,
      createdDate: new Date().toLocaleString("vi-VN"),
      avatar: formData.name
        ? formData.name
          .split(" ")
          .map((n) => n[0])
          .join("")
          .toUpperCase()
        : "NU",
      color: "bg-gray-500",
    };
    setUsers([newUser, ...users]);
  };

  // ==== XÓA NGƯỜI DÙNG ====
  const handleDelete = (id) => {
    if (window.confirm("Xóa người dùng này?")) {
      setUsers(users.filter((u) => u.id !== id));
    }
  };

  // ==== XUẤT FILE CSV ====
  const exportToCSV = () => {
    const csv = [
      ["Họ tên", "Email", "Vai trò", "Trạng thái", "Ngày tạo"].join(","),
      ...filteredUsers.map((u) =>
        [u.name, u.email, u.role, u.status, u.createdDate].join(",")
      ),
    ].join("\n");

    const blob = new Blob([csv], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "nguoi_dung.csv";
    a.click();
  };

  const handleRefresh = () => {
    setSearchQuery("");
    setFilteredUsers(users);
    setCurrentPage(1);
  };

  // ==== RENDER ====
  return (
    <div>
      <ListPageLayout
        title="Người dùng"
        addLabel="Thêm người dùng"
        viewMode={viewMode}
        setViewMode={setViewMode}
        onAdd={handleAddUser}
        onExport={exportToCSV}
        onToggleFilters={() => setShowFilters((prev) => !prev)}
        // 🔍 Props tìm kiếm
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        searchField={searchField}
        onSearchFieldChange={setSearchField}
        searchOptions={searchOptions}
        // 📄 Phân trang
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
        {/* ==== FILTERS ==== */}
        {showFilters && (
          <div className="sticky top-[128px] z-30 p-4 border-b border-gray-200 bg-gray-50 flex flex-wrap items-center gap-3 sm:gap-4">
            {/* Vai trò */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2">
              <label className="text-sm font-medium text-gray-700">
                Vai trò:
              </label>
              <select
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value)}
                className="border border-gray-300 rounded-md px-3 py-1.5 text-sm text-gray-800 focus:ring-2 focus:ring-gray-900 focus:outline-none w-full sm:w-auto"
              >
                <option value="ALL">Tất cả</option>
                <option value="ADMIN">ADMIN</option>
                <option value="EMPLOYEE">EMPLOYEE</option>
                <option value="USER">USER</option>
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
                <option value="Active">Active</option>
                <option value="Inactive">Inactive</option>
              </select>
            </div>
          </div>
        )}

        {/* ==== DANH SÁCH NGƯỜI DÙNG ==== */}
        <div className="relative">
          {viewMode === "table" ? (
            <UserTableView users={getPageUsers()} onDelete={handleDelete} />
          ) : (
            <UserGridView users={getPageUsers()} onDelete={handleDelete} />
          )}

          {/* Khi modal mở -> disable phần view */}
          {showForm && (
            <div className="absolute inset-0 bg-white/40 backdrop-blur-[1px] cursor-not-allowed z-40" />
          )}
        </div>

        {/* Modal thêm user */}
        <UserFormModal
          isOpen={showForm}
          onClose={() => setShowForm(false)}
          onSubmit={handleSubmitUser}
        />

      </ListPageLayout>
    </div>
  );
};

export default UserListSection;
