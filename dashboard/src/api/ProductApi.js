import api from "./api";

const API_BASE_URL_ADMIN = "admin/products";

const normalizeDateTime = (dt) => {
  if (!dt) return null;
  if (dt instanceof Date) return dt.toISOString();
  const s = String(dt);
  return s.includes("T") ? s : `${s}T00:00:00`;
};

const toNumberOrZero = (v) =>
  v === "" || v == null || Number.isNaN(Number(v)) ? 0 : Number(v);

const sanitizeVariants = (variants = []) =>
  variants.map((v) => ({
    color_label: v.color_label || "",
    color_hex: v.color_hex || "#000000",
    storage_cap: toNumberOrZero(v.storage_cap),
    ram: toNumberOrZero(v.ram),
    price: toNumberOrZero(v.price),
  }));

export const createAdminProduct = async (productData) => {
  const formData = new FormData();
  const allFiles = [];
  const imageMap = [];

  (productData.variants || []).forEach((v) => {
    const vf = Array.isArray(v.images) ? v.images.filter((x) => x instanceof File) : [];
    const start = allFiles.length;
    const idxs = vf.map((_, j) => start + j);
    allFiles.push(...vf);
    imageMap.push(idxs);
  });

  const variants = sanitizeVariants(productData.variants || []);

  const defaultVariantIndex = Number.isInteger(productData.defaultVariantIndex)
    ? Math.min(Math.max(productData.defaultVariantIndex, 0), Math.max(variants.length - 1, 0))
    : 0;

  const request = {
    name: productData.name || "",
    description: productData.description || "",
    status: productData.status || "ACTIVE",
    discount: {
      valueInPercent: toNumberOrZero(productData.discount?.valueInPercent),
      startDate: normalizeDateTime(productData.discount?.startDate),
      endDate: normalizeDateTime(productData.discount?.endDate),
    },
    spec: {
      ...productData.spec,
      release_date: normalizeDateTime(productData.spec?.release_date),
    },
    variants,
    defaultVariantIndex,
    imageMap,
  };

  console.log(request);

  formData.append("request", new Blob([JSON.stringify(request)], { type: "application/json" }));
  allFiles.forEach((f) => formData.append("files", f));
  if (allFiles.length === 0) formData.append("files", new Blob([]));

  console.log(allFiles.length);

  const res = await api.post(API_BASE_URL_ADMIN, formData, { headers: { "Content-Type": "multipart/form-data" } });
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
      startDate: normalizeDateTime(productData.discount?.startDate),
      endDate: normalizeDateTime(productData.discount?.endDate),
    },
    spec: {
      ...productData.spec,
      release_date: normalizeDateTime(productData.spec?.release_date),
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
