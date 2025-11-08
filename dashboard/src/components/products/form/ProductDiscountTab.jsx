import { useEffect } from "react"
import {Input, Textarea} from "../../common_components/FormInput"


const ProductDiscountTab = ({ newProduct, setNewProduct }) => {
  // ✅ Tự động tính giá sau khi giảm khi giá hoặc % thay đổi
  useEffect(() => {
    const price = parseFloat(newProduct.price) || 0;
    const discountPercent = parseFloat(newProduct.discount?.valueInPercent) || 0;
    const discountedPrice = price - (price * discountPercent) / 100;
    setNewProduct((prev) => ({
      ...prev,
      discountPrice: discountedPrice > 0 ? Math.round(discountedPrice) : 0,
    }));
  }, [newProduct.price, newProduct.discount?.valueInPercent]);

  return (
    <div className="space-y-6">
      <h3 className="text-lg font-semibold border-b border-gray-700 pb-1">
        💰 Giá & khuyến mãi
      </h3>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <Input
          label="Phần trăm giảm (%)"
          keyName="discount.valueInPercent"
          type="number"
          min={0}
          max={100}
          newProduct={newProduct}
          setNewProduct={setNewProduct}
        />

        <div>
          <label className="text-sm text-orange-500 block mb-1">
            Giá sau khi giảm (VNĐ)
          </label>
          <input
            type="text"
            readOnly
            className="w-full px-3 py-2 rounded-md bg-gray-800 text-orange-400 font-semibold cursor-default outline-none"
            value={
              newProduct.discountPrice
                ? newProduct.discountPrice.toLocaleString("vi-VN")
                : "0"
            }
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <Input
          label="Ngày bắt đầu"
          keyName="discount.startDate"
          type="date"
          newProduct={newProduct}
          setNewProduct={setNewProduct}
        />
        <Input
          label="Ngày kết thúc"
          keyName="discount.endDate"
          type="date"
          newProduct={newProduct}
          setNewProduct={setNewProduct}
        />
      </div>
    </div>
  );
};

export default ProductDiscountTab;