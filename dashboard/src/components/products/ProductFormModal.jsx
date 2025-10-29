import { useEffect } from 'react'
import { motion } from 'framer-motion'
import { X } from 'lucide-react'

const ProductFormModal = ({product, isOpen, onClose, onSubmit, newProduct, setNewProduct }) => {
    const mode = newProduct && newProduct.id ? "edit" : "add"
    const title = mode === "edit" ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới";

    useEffect(() => {
        if (isOpen) document.body.style.overflow = 'hidden'
        else document.body.style.overflow = 'auto'
        return () => (document.body.style.overflow = 'auto')
    }, [isOpen])

    if (!isOpen) return null

    return (
        <div className="absolute top-0 left-0 w-full h-screen z-50 flex flex-col bg-black bg-opacity-60 backdrop-blur-sm">
            <motion.div
                className="flex flex-col h-full bg-gray-900 text-gray-100 border-l border-gray-700"
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3 }}
            >
                {/* ===== HEADER ===== */}
                <div className="sticky top-0 flex justify-between items-center px-6 py-4 bg-gray-900 border-b border-gray-700 z-10">
                    <h2 className="text-xl font-semibold text-white">🛒 {title}</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-red-500 transition-colors"
                    >
                        <X size={26} />
                    </button>
                </div>

                {/* ===== BODY (scrollable) ===== */}
                <div className="flex-1 overflow-y-auto p-6 space-y-10">
                    {/* --- Thông tin cơ bản --- */}
                    <section>
                        <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                            📄 Thông tin sản phẩm
                        </h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <Input label="Tên sản phẩm" keyName="name" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Thương hiệu" keyName="brand" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Giá bán (VNĐ)" keyName="price" type="number" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Ngày phát hành" keyName="release_date" type="date" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Textarea label="Mô tả" keyName="description" rows={3} newProduct={newProduct} setNewProduct={setNewProduct} />
                        </div>
                    </section>

                    {/* --- Hình ảnh --- */}
                    <section>
                        <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                            🖼️ Hình ảnh sản phẩm
                        </h3>
                        <input
                            type="file"
                            multiple
                            accept="image/*"
                            className="block w-full text-sm text-gray-400 file:mr-4 file:py-2 file:px-4
       file:rounded-md file:border-0 file:text-sm file:font-semibold
       file:bg-blue-600 file:text-white hover:file:bg-blue-700"
                            onChange={(e) => {
                                const files = Array.from(e.target.files)
                                const newFiles = [...(newProduct.images || []), ...files]
                                const newPreviews = [
                                    ...(newProduct.imagePreviews || []),
                                    ...files.map((f) => URL.createObjectURL(f))
                                ]

                                setNewProduct({
                                    ...newProduct,
                                    images: newFiles,
                                    imagePreviews: newPreviews
                                })
                            }}

                        />

                        {newProduct.imagePreviews?.length > 0 && (
                            <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-3">
                                {newProduct.imagePreviews.map((src, i) => (
                                    <div
                                        key={i}
                                        className="relative group rounded-lg overflow-hidden border border-gray-700"
                                    >
                                        <img
                                            src={src}
                                            alt={`preview-${i}`}
                                            className="w-full w-full object-cover group-hover:opacity-80 transition"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => {
                                                const newPreviews = newProduct.imagePreviews.filter((_, idx) => idx !== i)
                                                const newFiles = Array.from(newProduct.images).filter((_, idx) => idx !== i)
                                                setNewProduct({ ...newProduct, images: newFiles, imagePreviews: newPreviews })
                                            }}
                                            className="absolute top-1 right-1 bg-black/60 rounded-full p-1 text-gray-200 hover:text-red-400"
                                        >
                                            <X size={16} />
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </section>

                    {/* --- Thông số kỹ thuật --- */}
                    <section>
                        <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                            ⚙️ Thông số kỹ thuật
                        </h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <Input label="Hệ điều hành" keyName="os" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Bộ xử lý (CPU)" keyName="cpu" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Tốc độ CPU" keyName="cpu_speed" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Đồ họa (GPU)" keyName="gpu" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Dung lượng RAM" keyName="ram" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Dung lượng lưu trữ" keyName="storage_cap" newProduct={newProduct} setNewProduct={setNewProduct} />
                        </div>
                    </section>

                    {/* --- Camera & hiển thị --- */}
                    <section>
                        <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                            📷 Camera & Hiển thị
                        </h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <Input label="Camera sau" keyName="rear_cam" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Camera trước" keyName="front_cam" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Độ phân giải màn hình" keyName="screen_res" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Dung lượng pin" keyName="battery_cap" newProduct={newProduct} setNewProduct={setNewProduct} />
                        </div>
                    </section>

                    {/* --- Thông số vật lý --- */}
                    <section>
                        <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                            📱 Thông số vật lý
                        </h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <Input label="Chất liệu khung máy" keyName="material" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Input label="Kích thước & khối lượng" keyName="size_weight" newProduct={newProduct} setNewProduct={setNewProduct} />
                            <Textarea label="Tính năng nổi bật" keyName="features" rows={2} newProduct={newProduct} setNewProduct={setNewProduct} />
                        </div>
                    </section>

                    {/* Add bottom spacing to prevent footer overlap */}
                    <div className="h-16"></div>
                </div>

                {/* ===== FOOTER (cố định) ===== */}
                <div className="sticky bottom-0 bg-gray-900 border-t border-gray-700 flex justify-end gap-3 px-6 py-4 z-10">
                    <button
                        type="button"
                        onClick={onClose}
                        className="bg-gray-600 hover:bg-red-500 px-5 py-2 rounded-md text-white transition"
                    >
                        Huỷ
                    </button>
                    <button
                        type="button"
                        onClick={onSubmit}
                        className="bg-blue-600 hover:bg-blue-800 px-6 py-2 rounded-md text-white transition"
                    >
                        Lưu
                    </button>
                </div>
            </motion.div>
        </div>
    )
}

export default ProductFormModal

// ======= COMPONENT REUSABLE =======
const Input = ({ label, keyName, type = 'text', newProduct, setNewProduct }) => (
    <div>
        <label className="text-sm text-gray-300 block mb-1">{label} *</label>
        <input
            type={type}
            className="w-full px-3 py-2 rounded-md bg-gray-800 text-white focus:ring-2 focus:ring-blue-500 outline-none"
            value={newProduct[keyName] || ''}
            onChange={(e) => setNewProduct({ ...newProduct, [keyName]: e.target.value })}
        />
    </div>
)

const Textarea = ({ label, keyName, rows, newProduct, setNewProduct }) => (
    <div className="col-span-2">
        <label className="text-sm text-gray-300 block mb-1">{label} *</label>
        <textarea
            rows={rows}
            className="w-full px-3 py-2 rounded-md bg-gray-800 text-white focus:ring-2 focus:ring-blue-500 outline-none"
            value={newProduct[keyName] || ''}
            onChange={(e) => setNewProduct({ ...newProduct, [keyName]: e.target.value })}
        ></textarea>
    </div>
)
