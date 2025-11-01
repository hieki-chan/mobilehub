import axios from "axios";

const API_BASE_URL_ADMIN = "http://localhost:8087/api/admin/products"; // base API của bạn

export const createAdminProduct = async (productData) => {
  const formData = new FormData();

  // 🧩 clone dữ liệu từ form
  const request = {
    name: productData.name || "",
    description: productData.description || "",
    price: Number(productData.price) || 0,
    discount: {
      ...productData.discount,
      // format lại ngày cho đúng kiểu LocalDateTime
      startDate: formatDateTime(productData.discount?.startDate),
      endDate: formatDateTime(productData.discount?.endDate),
      valueInPercent: Number(productData.discount?.valueInPercent) || 0,
    },
    spec: {
      ...productData.spec,
      release_date: formatDateTime(productData.spec?.release_date),
    },
  };

  // Gắn JSON request
  formData.append(
    "request",
    new Blob([JSON.stringify(request)], { type: "application/json" })
  );

  // Gắn file ảnh (nếu có)
  if (productData.images?.length > 0) {
    for (const file of productData.images) {
      formData.append("files", file);
    }
  }

  console.log("📤 Sending:", request);

  // ⚠️ KHÔNG set Content-Type, axios tự thêm boundary
  const res = await axios.post(API_BASE_URL_ADMIN, formData);

  return res.data;
};

export const fetchAdminProducts = async (page = 0, size = 10) => {
  try {
    const response = await axios.get(`${API_BASE_URL_ADMIN}`, {
      params: { page, size },
    });

    // log toàn bộ data ra console
    console.log("✅ Product list:", response.data);

    return response.data;
  } catch (error) {
    console.error("❌ Error fetching products:", error);
    throw error;
  }
};


export const updateAdminProduct = async (productId, productData) => {
  const request = {
    name: productData.name || "",
    description: productData.description || "",
    price: Number(productData.price) || 0,
    discount: {
      ...productData.discount,
      valueInPercent: Number(productData.discount?.valueInPercent) || 0,
      startDate: formatDateTime(productData.discount?.startDate),
      endDate: formatDateTime(productData.discount?.endDate),
    },
    spec: {
      ...productData.spec,
      release_date: formatDateTime(productData.spec?.release_date),
    },
  };

  console.log("📤 Request gửi lên:", request);

  const res = await axios.put(
    `${API_BASE_URL_ADMIN}/${productId}`, // nhớ có dấu "/" nếu cần
    request,
    {
      headers: { "Content-Type": "application/json" },
    }
  );

  return res.data;
};



export const deleteAdminProduct = async (productId) => {
  const res = await axios.delete(`${API_BASE_URL_ADMIN}/${productId}`);
  return res.data;
};

export const getAdminProductDetail = async (productId) => {
  try {
    const response = await axios.get(`${API_BASE_URL_ADMIN}/${productId}/detail`);

    console.log("✅ Product detail:", response.data);
    return response.data;
  } catch (error) {
    console.error(`❌ Error fetching product detail (ID: ${productId}):`, error);
    throw error;
  }
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return null;
  // Nếu input là dạng "2025-11-14" → convert thành "2025-11-14T00:00:00"
  return dateStr.includes("T") ? dateStr : `${dateStr}T00:00:00`;
};