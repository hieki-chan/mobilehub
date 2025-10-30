import { useEffect } from "react"

const ProductDiscountTab = ({ newProduct, setNewProduct }) => {
  // ✅ Tự động tính giá sau khi giảm khi giá hoặc % thay đổi
  useEffect(() => {
    const price = parseFloat(newProduct.price) || 0
    const discountPercent = parseFloat(newProduct.discountPercent) || 0
    const discountedPrice = price - (price * discountPercent) / 100
    setNewProduct((prev) => ({
      ...prev,
      discountPrice: discountedPrice > 0 ? Math.round(discountedPrice) : 0,
    }))
  }, [newProduct.price, newProduct.discountPercent])

  return (
    <div className="space-y-6">
      <h3 className="text-lg font-semibold border-b border-gray-700 pb-1">
        💰 Giá & khuyến mãi
      </h3>

      {/* ===== Hàng 1: Giá gốc ===== */}
      <div>
        <Input
          label="Giá gốc (VNĐ)"
          keyName="price"
          type="number"
          min={0}
          newProduct={newProduct}
          setNewProduct={setNewProduct}
        />
      </div>

      {/* ===== Hàng 2: Phần trăm giảm & Giá sau khi giảm ===== */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <Input
          label="Phần trăm giảm (%)"
          keyName="discountPercent"
          type="number"
          min={0}
          max={100}
          newProduct={newProduct}
          setNewProduct={setNewProduct}
        />

        <div>
          <label className="text-sm text-gray-300 block mb-1">
            Giá sau khi giảm (VNĐ)
          </label>
          <input
            type="text"
            readOnly
            className="w-full px-3 py-2 rounded-md bg-gray-800 text-green-400 font-semibold cursor-default outline-none"
            value={
              newProduct.discountPrice
                ? newProduct.discountPrice.toLocaleString("vi-VN")
                : "0"
            }
          />
        </div>
      </div>

      {/* ===== Hàng 3: Ngày bắt đầu & kết thúc ===== */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <Input
          label="Ngày bắt đầu"
          keyName="discountStart"
          type="date"
          newProduct={newProduct}
          setNewProduct={setNewProduct}
        />
        <Input
          label="Ngày kết thúc"
          keyName="discountEnd"
          type="date"
          newProduct={newProduct}
          setNewProduct={setNewProduct}
        />
      </div>

      {/* ===== Hàng 4: Ghi chú ===== */}
      <Textarea
        label="Ghi chú khuyến mãi"
        keyName="discountNote"
        rows={2}
        newProduct={newProduct}
        setNewProduct={setNewProduct}
      />
    </div>
  )
}

export default ProductDiscountTab

// === COMPONENTS DÙNG CHUNG ===
const Input = ({
  label,
  keyName,
  type = "text",
  min,
  max,
  newProduct,
  setNewProduct,
}) => (
  <div>
    <label className="text-sm text-gray-300 block mb-1">{label}</label>
    <input
      type={type}
      min={min}
      max={max}
      className="w-full px-3 py-2 rounded-md bg-gray-800 text-white focus:ring-2 focus:ring-blue-500 outline-none"
      value={newProduct[keyName] || ""}
      onChange={(e) => {
        let val = e.target.value
        if (type === "number") {
          const num = parseFloat(val)
          if (min !== undefined && num < min) val = min
          if (max !== undefined && num > max) val = max
        }
        setNewProduct({ ...newProduct, [keyName]: val })
      }}
    />
  </div>
)

const Textarea = ({ label, keyName, rows, newProduct, setNewProduct }) => (
  <div>
    <label className="text-sm text-gray-300 block mb-1">{label}</label>
    <textarea
      rows={rows}
      className="w-full px-3 py-2 rounded-md bg-gray-800 text-white focus:ring-2 focus:ring-blue-500 outline-none"
      value={newProduct[keyName] || ""}
      onChange={(e) =>
        setNewProduct({ ...newProduct, [keyName]: e.target.value })
      }
    ></textarea>
  </div>
)
