import api from "./api";

const API_BASE_URL_ADMIN = "admin/products"; 

const formatDateTime = (dateStr) => {
  if (!dateStr) return null;
  return dateStr.includes("T") ? dateStr : `${dateStr}T00:00:00`;
};

export const createAdminProduct = async (productData) => {
  const formData = new FormData();

  const request = {
    name: productData.name || "",
    description: productData.description || "",
    price: Number(productData.price) || 0,
    discount: {
      ...productData.discount,
      startDate: formatDateTime(productData.discount?.startDate),
      endDate: formatDateTime(productData.discount?.endDate),
      valueInPercent: Number(productData.discount?.valueInPercent) || 0,
    },
    spec: {
      ...productData.spec,
      release_date: formatDateTime(productData.spec?.release_date),
    },
  };

  formData.append("request", new Blob([JSON.stringify(request)], { type: "application/json" }));
  if (productData.images?.length > 0) {
    for (const file of productData.images) formData.append("files", file);
  }

  const res = await api.post(API_BASE_URL_ADMIN, formData);
  return res.data;
};

export const fetchAdminProducts = async (page = 0, size = 10) => {
  const res = await api.get(API_BASE_URL_ADMIN, { params: { page, size } });
  return res.data;
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

  const res = await api.put(`${API_BASE_URL_ADMIN}/${productId}`, request);
  return res.data;
};

export const deleteAdminProduct = async (productId) => {
  const res = await api.delete(`${API_BASE_URL_ADMIN}/${productId}`);
  return res.data;
};

export const getAdminProductDetail = async (productId) => {
  const res = await api.get(`${API_BASE_URL_ADMIN}/${productId}/detail`);
  return res.data;
};
