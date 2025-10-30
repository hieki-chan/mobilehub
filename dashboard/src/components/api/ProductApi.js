const BASE_URL = "http://localhost:8080/api/products"; // đổi port backend nếu cần

// ======== PRODUCT API CALLS ========
export const productApi = {
  // Lấy danh sách sản phẩm (GET /api/products)
  getAll: async (page = 0, size = 10) => {
    const res = await fetch(`${BASE_URL}?page=${page}&size=${size}`);
    const data = await res.json();
    console.log("📦 Product list:", data);
    return data;
  },

  // Lấy chi tiết 1 sản phẩm (GET /api/products/{id})
  getById: async (id) => {
    const res = await fetch(`${BASE_URL}/${id}`);
    const data = await res.json();
    console.log(`🔍 Product ${id}:`, data);
    return data;
  },

  // Tạo sản phẩm mới (POST /api/products)
  create: async (formData) => {
    const res = await fetch(BASE_URL, {
      method: "POST",
      body: formData, // FormData chứa request + files
    });
    const data = await res.json();
    console.log("🆕 Created product:", data);
    return data;
  },

  // Cập nhật (PUT /api/products/{id})
  update: async (id, body) => {
    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    const data = await res.json();
    console.log("✏️ Updated:", data);
    return data;
  },

  // Xoá (DELETE /api/products/{id})
  remove: async (id) => {
    const res = await fetch(`${BASE_URL}/${id}`, { method: "DELETE" });
    const data = await res.json();
    console.log("🗑️ Deleted:", data);
    return data;
  },

  // Sản phẩm giảm giá (GET /api/products/discounts)
  getDiscounts: async () => {
    const res = await fetch(`${BASE_URL}/discounts`);
    const data = await res.json();
    console.log("💸 Discounted products:", data);
    return data;
  },
};
