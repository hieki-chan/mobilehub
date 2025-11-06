import React, { useState, useEffect } from "react";
import ListPageLayout from "../common_components/ListPageLayout";
import UserGridView from "./UserGridView";
import UserTableView from "./UserTableView";
import UserFormModal from "./form/UserFormModal";
import {
  fetchAdminUsersPaged,
  createAdminUser,
  updateAdminUser,
  deleteAdminUser,
} from "../../api/UserApi";
import { showPopupConfirm } from "../common_components/PopupConfirm";

const UserListSection = () => {
  const [users, setUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);

  const [showForm, setShowForm] = useState(false);
  const [formMode, setFormMode] = useState("create");
  const [editingUser, setEditingUser] = useState(null);

  const [viewMode, setViewMode] = useState("table");
  const [showFilters, setShowFilters] = useState(false);

  const [selectedRole, setSelectedRole] = useState("ALL");
  const [selectedStatus, setSelectedStatus] = useState("ALL");
  const [searchField, setSearchField] = useState("username");
  const [searchQuery, setSearchQuery] = useState("");

  const searchOptions = [
    { label: "Tên", value: "username" },
    { label: "Email", value: "email" },
    { label: "Vai trò", value: "role" },
    { label: "Trạng thái", value: "status" },
  ];

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(15);
  const totalItems = filteredUsers.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const startItem = (currentPage - 1) * itemsPerPage + 1;
  const endItem = Math.min(currentPage * itemsPerPage, totalItems);

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const data = await fetchAdminUsersPaged(currentPage - 1, itemsPerPage);
      if (data?.content) {
        const normalized = data.content.map((u) => ({
          id: u.id ?? 0,
          username: u.username ?? "Chưa có tên",
          email: u.email ?? "—",
          role: u.role ?? "USER",
          status: u.status ?? "Inactive",
          createdDate: u.createdAt ?? "—",
          avatar:
            (u.username
              ? u.username.slice(0, 1)
              : u.email?.slice(0, 2) ?? "?"
            ).toUpperCase(),
          color: "bg-orange-500",
        }));
        setUsers(normalized);
        setFilteredUsers(normalized);
      }
    } catch (err) {
      console.error("❌ Lỗi tải người dùng:", err);
    }
  };

  useEffect(() => {
    handleFilter();
  }, [users, selectedRole, selectedStatus, searchQuery, searchField]);

  const handleFilter = () => {
    let result = [...users];

    if (selectedRole !== "ALL") {
      result = result.filter((u) => u.role === selectedRole);
    }
    if (selectedStatus !== "ALL") {
      result = result.filter((u) => u.status === selectedStatus);
    }
    if (searchQuery.trim() !== "") {
      result = result.filter((u) => {
        const val = u[searchField] ?? "";
        return val.toString().toLowerCase().includes(searchQuery.toLowerCase());
      });
    }

    setFilteredUsers(result);
    setCurrentPage(1);
  };

  const getPageUsers = () => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredUsers.slice(start, start + itemsPerPage);
  };

  const handleAddUser = () => {
    setFormMode("create");
    setEditingUser(null);
    setShowForm(true);
  };

  const handleEditUser = (user) => {
    setFormMode("edit");
    setEditingUser(user);
    setShowForm(true);
  };

  const handleSubmitUser = async (formData) => {
    try {
      if (formMode === "edit" && editingUser) {
        await updateAdminUser(editingUser.id, formData);
        alert("✅ Cập nhật người dùng thành công!");
      } else {
        await createAdminUser(formData);
        alert("✅ Tạo người dùng thành công!");
      }
      await loadUsers();
      setShowForm(false);
    } catch (err) {
      console.error("❌ Lỗi lưu người dùng:", err);
      alert("Không thể lưu người dùng!");
    }
  };

  const handleDelete = async (id) => {
    const confirmed = await showPopupConfirm(
      "Xác nhận xoá người dùng",
      "Bạn có chắc muốn xoá người dùng này?"
    );
    if (!confirmed) return;
    try {
      await deleteAdminUser(id);
      setUsers((prev) => prev.filter((u) => u.id !== id));
    } catch (err) {
      console.error("❌ Lỗi xoá user:", err);
    }
  };

  const exportToCSV = () => {
    const csv = [
      ["Tên", "Email", "Vai trò", "Trạng thái", "Ngày tạo"].join(","),
      ...filteredUsers.map((u) =>
        [u.username, u.email, u.role, u.status, u.createdDate].join(",")
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
    loadUsers();
    setCurrentPage(1);
  };

  return (
    <ListPageLayout
      title="Người dùng"
      addLabel="Thêm người dùng"
      viewMode={viewMode}
      setViewMode={setViewMode}
      onAdd={handleAddUser}
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
            <label className="text-sm font-medium text-gray-700">Vai trò:</label>
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

          <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2 sm:ml-6">
            <label className="text-sm font-medium text-gray-700">Trạng thái:</label>
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

      <div className="relative">
        {viewMode === "table" ? (
          <UserTableView
            users={getPageUsers()}
            onDelete={handleDelete}
            onEdit={handleEditUser}
          />
        ) : (
          <UserGridView
            users={getPageUsers()}
            onDelete={handleDelete}
            onEdit={handleEditUser}
          />
        )}

        {showForm && (
          <div className="absolute inset-0 bg-white/40 backdrop-blur-[1px] cursor-not-allowed z-40" />
        )}
      </div>

      <UserFormModal
        isOpen={showForm}
        mode={formMode}
        initialData={editingUser}
        onClose={() => setShowForm(false)}
        onSuccess={loadUsers}
        onSubmit={handleSubmitUser}
      />
    </ListPageLayout>
  );
};

export default UserListSection;
