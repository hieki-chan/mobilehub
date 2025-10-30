import { X } from "lucide-react"
import ProductImageSection from "./ProductImageSection"


const ProductInfoTab = ({ newProduct, setNewProduct }) => {
    return (
        <div className="space-y-10">
            {/* --- Thông tin cơ bản --- */}
            <section>
                <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                    📄 Thông tin sản phẩm
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <Input label="Tên sản phẩm" keyName="name" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Thương hiệu" keyName="brand" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Ngày phát hành" keyName="release_date" type="date" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Textarea label="Mô tả" keyName="description" rows={3} newProduct={newProduct} setNewProduct={setNewProduct} />
                </div>
            </section>

            {/* --- Hình ảnh --- */}
            <ProductImageSection newProduct={newProduct} setNewProduct={setNewProduct} />


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
        </div>
    )
}

export default ProductInfoTab

// === COMPONENTS DÙNG CHUNG ===
const Input = ({ label, keyName, type = "text", newProduct, setNewProduct }) => (
    <div>
        <label className="text-sm text-gray-300 block mb-1">{label} *</label>
        <input
            type={type}
            className="w-full px-3 py-2 rounded-md bg-gray-800 text-white focus:ring-2 focus:ring-blue-500 outline-none"
            value={newProduct[keyName] || ""}
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
            value={newProduct[keyName] || ""}
            onChange={(e) => setNewProduct({ ...newProduct, [keyName]: e.target.value })}
        ></textarea>
    </div>
)
