import React, { useMemo } from "react";
import { Plus, Trash2, Crown, HardDrive, Gauge, Tag } from "lucide-react";
import ProductImageSection from "./ProductImageSection";

const ProductVariantTab = ({ newProduct, setNewProduct }) => {
    const variants = newProduct.variants ?? [];

    const defaultIdx = useMemo(() => {
        if (Number.isInteger(newProduct.defaultVariantIndex)) {
            return Math.min(Math.max(newProduct.defaultVariantIndex, 0), Math.max(variants.length - 1, 0));
        }
        return 0;
    }, [newProduct.defaultVariantIndex, variants.length]);

    const commit = (patch) => setNewProduct((prev) => ({ ...prev, ...patch }));

    const addVariant = () => {
        const v = {
            color_label: "",
            color_hex: "#000000",
            storage_cap: 0,
            ram: 0,
            price: 0,
            images: [],
            imagePreviews: [],
        };
        const nextVariants = [...variants, v];
        commit({ variants: nextVariants, defaultVariantIndex: defaultIdx });
    };

    const removeVariant = (i) => {
        const nextVariants = variants.filter((_, idx) => idx !== i);
        let nextDefault = defaultIdx;
        if (i === defaultIdx) nextDefault = 0;
        else if (i < defaultIdx) nextDefault = defaultIdx - 1;
        commit({
            variants: nextVariants,
            defaultVariantIndex: Math.max(0, Math.min(nextDefault, nextVariants.length - 1)),
        });
    };

    const updateVariantField = (i, field, value) => {
        const next = variants.map((v, idx) => (idx === i ? { ...v, [field]: value } : v));
        commit({ variants: next });
    };

    const setDefaultVariant = (i) => {
        commit({ defaultVariantIndex: i });
    };

    const getVariantKeyPreview = (vi) => {
        const variant = variants[vi];
        if (!variant) return null;
        return (
            variant.imagePreviews?.[0] ||
            variant.imageUrl ||
            variant.subImageUrls?.[0] ||
            null
        );
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold">Phiên bản</h3>
                <button onClick={addVariant} className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-gray-900 text-white hover:bg-gray-800">
                    <Plus size={16} /> Thêm phiên bản
                </button>
            </div>

            {variants.length === 0 && <p className="text-gray-500">Chưa có phiên bản nào. Nhấn <b>Thêm phiên bản</b> để bắt đầu.</p>}

            <div className="space-y-5">
                {variants.map((v, i) => (
                    <div key={v.id || i} className={`border rounded-lg p-4 bg-white transition ${i === defaultIdx ? "border-yellow-400 shadow-[0_0_8px_rgba(234,179,8,0.4)]" : "border-gray-200"}`}>
                        <div className="flex items-center justify-between gap-3 mb-3">
                            <div className="flex items-center gap-3">
                                <img
                                    src={getVariantKeyPreview(i) || "data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs="}
                                    alt="preview"
                                    className="w-12 h-12 rounded-md object-cover border"
                                />
                                <div className="text-sm text-gray-600">
                                    <div>Mã phiên bản: #{v.id ?? "—"}</div>
                                    <div>Tổng ảnh: {v.imagePreviews?.length ?? 0}</div>
                                </div>
                            </div>

                            <div className="flex items-center gap-2">
                                <button
                                    onClick={() => setDefaultVariant(i)}
                                    className={`inline-flex items-center gap-1 px-3 py-1 rounded-md border ${i === defaultIdx ? "bg-yellow-100 border-yellow-400 text-yellow-700" : "border-gray-300 text-gray-700 hover:bg-gray-50"}`}
                                    title="Đặt làm mặc định"
                                >
                                    <Crown size={16} />
                                    {i === defaultIdx ? "Phiên bản mặc định" : "Đặt mặc định"}
                                </button>

                                <button onClick={() => removeVariant(i)} className="inline-flex items-center gap-1 px-3 py-1 rounded-md border border-red-300 text-red-600 hover:bg-red-50">
                                    <Trash2 size={16} /> Xóa
                                </button>
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
                            <div>
                                <label className="text-sm text-gray-700 block mb-1">Tên màu</label>
                                <input type="text" value={v.color_label} onChange={(e) => updateVariantField(i, "color_label", e.target.value)} className="w-full px-3 py-2 border rounded-md" placeholder="VD: Black" />
                            </div>

                            <div>
                                <label className="text-sm text-gray-700 block mb-1">Mã màu</label>
                                <div className="flex items-center gap-2">
                                    <input type="text" value={v.color_hex} onChange={(e) => updateVariantField(i, "color_hex", e.target.value)} className="w-full px-3 py-2 border rounded-md" placeholder="#000000" />
                                    <input type="color" value={v.color_hex || "#000000"} onChange={(e) => updateVariantField(i, "color_hex", e.target.value)} className="h-10 w-10 rounded-md border" title="Chọn màu" />
                                </div>
                            </div>

                            <div>
                                <label className="text-sm text-gray-700 block mb-1">Storage (GB)</label>
                                <div className="flex items-center gap-2">
                                    <HardDrive size={16} className="text-gray-500" />
                                    <input type="number" min={0} value={v.storage_cap} onChange={(e) => updateVariantField(i, "storage_cap", Number(e.target.value))} className="w-full px-3 py-2 border rounded-md" placeholder="128" />
                                </div>
                            </div>

                            <div>
                                <label className="text-sm text-gray-700 block mb-1">RAM (GB)</label>
                                <div className="flex items-center gap-2">
                                    <Gauge size={16} className="text-gray-500" />
                                    <input type="number" min={0} value={v.ram} onChange={(e) => updateVariantField(i, "ram", Number(e.target.value))} className="w-full px-3 py-2 border rounded-md" placeholder="8" />
                                </div>
                            </div>

                            <div>
                                <label className="text-sm text-gray-700 block mb-1">Giá (VNĐ)</label>
                                <div className="flex items-center gap-2">
                                    <Tag size={16} className="text-gray-500" />
                                    <input type="number" min={0} value={v.price} onChange={(e) => updateVariantField(i, "price", Number(e.target.value))} className="w-full px-3 py-2 border rounded-md" placeholder="15990000" />
                                </div>
                            </div>
                        </div>

                        <ProductImageSection newProduct={newProduct} setNewProduct={setNewProduct} activeVariantIndex={i} />
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ProductVariantTab;
