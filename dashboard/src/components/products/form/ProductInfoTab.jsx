import { X } from "lucide-react"
import { Input, Textarea } from "../../common_components/FormInput"

const ProductInfoTab = ({ newProduct, setNewProduct }) => {
    const isEditMode = !!newProduct?.id
    return (
        <div className="space-y-10">
            <section>
                <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                    📄 Thông tin sản phẩm
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    {isEditMode && (
                        <div className="col-span-2">
                            <label className="text-sm font-medium text-orange-400 block mb-1">
                                Mã sản phẩm
                            </label>
                            <input
                                type="text"
                                readOnly
                                className="w-full px-3 py-2 rounded-md bg-gray-800 text-orange-400 cursor-not-allowed border border-gray-700"
                                value={newProduct.id}
                            />
                        </div>
                    )}
                    <Input label="Tên sản phẩm" keyName="name" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Thương hiệu" keyName="spec.brand" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Ngày phát hành" keyName="spec.release_date" type="date" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Textarea label="Mô tả" keyName="description" rows={3} newProduct={newProduct} setNewProduct={setNewProduct} />
                </div>
            </section>

            <section>
                <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                    ⚙️ Thông số kỹ thuật
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <Input label="Hệ điều hành" keyName="spec.os" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Bộ xử lý (CPU)" keyName="spec.cpu" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Tốc độ CPU" keyName="spec.cpu_speed" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Đồ họa (GPU)" keyName="spec.gpu" newProduct={newProduct} setNewProduct={setNewProduct} />
                </div>
            </section>

            <section>
                <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                    📷 Camera & Hiển thị
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <Input label="Camera sau" keyName="spec.rear_cam" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Camera trước" keyName="spec.front_cam" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Độ phân giải màn hình" keyName="spec.screen_res" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Dung lượng pin" keyName="spec.battery_cap" newProduct={newProduct} setNewProduct={setNewProduct} />
                </div>
            </section>

            <section>
                <h3 className="text-lg font-semibold mb-4 border-b border-gray-700 pb-1">
                    📱 Thông số vật lý
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <Input label="Chất liệu khung máy" keyName="spec.material" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Input label="Kích thước & khối lượng" keyName="spec.size_weight" newProduct={newProduct} setNewProduct={setNewProduct} />
                    <Textarea label="Tính năng nổi bật" keyName="spec.features" rows={2} newProduct={newProduct} setNewProduct={setNewProduct} />
                </div>
            </section>
        </div>
    )
}

export default ProductInfoTab