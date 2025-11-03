import { useState, useCallback } from "react"
import Cropper from "react-easy-crop"
import { X, Star, Upload, Crop as CropIcon } from "lucide-react"

const ProductImageSection = ({ newProduct, setNewProduct }) => {
  const [croppingImage, setCroppingImage] = useState(null)
  const [crop, setCrop] = useState({ x: 0, y: 0 })
  const [zoom, setZoom] = useState(1)
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(null)

  // === xử lý upload ảnh ===
  const handleFileChange = (e) => {
    const file = e.target.files?.[0]
    if (!file) return
    e.target.value = ""
    const url = URL.createObjectURL(file)
    setCroppingImage({ file, url })
  }

  // === xử lý crop ===
  const onCropComplete = useCallback((_, croppedAreaPixels) => {
    setCroppedAreaPixels(croppedAreaPixels)
  }, [])

  const getCroppedImage = useCallback(async () => {
    if (!croppingImage) return

    const image = await createCroppedImage(croppingImage.url, croppedAreaPixels)
    const blob = await fetch(image).then((res) => res.blob())
    const file = new File([blob], croppingImage.file.name, { type: "image/jpeg" })
    const url = URL.createObjectURL(file)

    const newImages = [...(newProduct.images || []), file]
    const newPreviews = [...(newProduct.imagePreviews || []), url]
    const mainImage = newProduct.mainImage || url

    setNewProduct({
      ...newProduct,
      images: newImages,
      imagePreviews: newPreviews,
      mainImage
    })
    setCroppingImage(null)
  }, [croppingImage, croppedAreaPixels, newProduct])

  // === xử lý xoá ảnh ===
  const handleRemove = (index) => {
    const newPreviews = newProduct.imagePreviews.filter((_, i) => i !== index)
    const newFiles = Array.from(newProduct.images).filter((_, i) => i !== index)
    let newMain = newProduct.mainImage

    if (newProduct.mainImage === newProduct.imagePreviews[index]) {
      newMain = newPreviews.length > 0 ? newPreviews[0] : null
    }

    setNewProduct({
      ...newProduct,
      images: newFiles,
      imagePreviews: newPreviews,
      mainImage: newMain
    })
  }

  const handleSetMain = (src) => {
    setNewProduct({ ...newProduct, mainImage: src })
  }

  return (
    <section>
      <h3 className="text-lg font-semibold mb-4 border-b border-gray-300 pb-1 text-gray-900">
        🖼️ Hình ảnh sản phẩm
      </h3>

      {/* Input ẩn */}
      <input
        id="file-upload"
        type="file"
        accept="image/*"
        className="hidden"
        onChange={handleFileChange}
      />

      {/* Nút upload */}
      <label
        htmlFor="file-upload"
        className="inline-flex items-center gap-2 px-5 py-3 bg-gradient-to-r from-orange-500 to-orange-600 text-white font-medium text-sm rounded-xl cursor-pointer shadow-md hover:scale-[1.02] active:scale-95 transition-all duration-200"
      >
        <Upload size={18} />
        <span>Tải ảnh lên</span>
      </label>

      {/* Danh sách ảnh */}
      {newProduct.imagePreviews?.length > 0 && (
        <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-3">
          {newProduct.imagePreviews.map((src, i) => (
            <div
              key={i}
              className={`relative rounded-lg overflow-hidden border-2 transition ${newProduct.mainImage === src
                ? "border-orange-500 shadow-[0_0_10px_rgba(249,115,22,0.6)]"
                : "border-gray-300"
                }`}
            >
              <div className="aspect-[3/4] w-full overflow-hidden">
                <img
                  src={src}
                  alt={`preview-${i}`}
                  className="object-cover w-full h-full"
                />
              </div>

              {/* Xoá */}
              <button
                type="button"
                onClick={() => handleRemove(i)}
                className="absolute top-1 right-1 bg-black/50 rounded-full p-1 text-gray-200 hover:text-red-400"
              >
                <X size={16} />
              </button>

              {/* Đặt ảnh chính */}
              <button
                type="button"
                onClick={() => handleSetMain(src)}
                className={`absolute bottom-1 left-1 flex items-center gap-1 px-2 py-1 rounded-md text-xs font-medium transition ${newProduct.mainImage === src
                  ? "bg-orange-500 text-white"
                  : "bg-black/50 text-gray-200 hover:bg-orange-600 hover:text-white"
                  }`}
              >
                <Star size={12} />
                {newProduct.mainImage === src ? "Ảnh chính" : "Đặt làm chính"}
              </button>
            </div>
          ))}
        </div>
      )}

      {/* === Modal Crop === */}
      {croppingImage && (
        <div className="fixed inset-0 z-50 bg-black/80 flex items-center justify-center">
          <div className="relative w-[90vw] max-w-lg h-[70vh] bg-white rounded-lg overflow-hidden flex flex-col">
            <h2 className="text-center py-3 border-b border-gray-200 text-gray-900 font-semibold">
              ✂️ Cắt ảnh
            </h2>

            <div className="relative flex-1">
              <Cropper
                image={croppingImage.url}
                crop={crop}
                zoom={zoom}
                aspect={3 / 4}
                onCropChange={setCrop}
                onZoomChange={setZoom}
                onCropComplete={onCropComplete}
                cropShape="rect"
                showGrid={true}
              />
            </div>

            <div className="flex justify-end gap-3 p-3 border-t border-gray-200 bg-gray-50">
              <button
                onClick={() => setCroppingImage(null)}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-red-500 hover:text-white transition"
              >
                Huỷ
              </button>
              <button
                onClick={getCroppedImage}
                className="px-5 py-2 bg-orange-500 text-white rounded hover:bg-orange-600 transition"
              >
                Lưu ảnh
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}

export default ProductImageSection

// === Hàm tạo ảnh crop ===
const createCroppedImage = (imageSrc, crop) =>
  new Promise((resolve) => {
    const image = new Image()
    image.src = imageSrc
    image.onload = () => {
      const canvas = document.createElement("canvas")
      const ctx = canvas.getContext("2d")

      canvas.width = crop.width
      canvas.height = crop.height

      ctx.drawImage(
        image,
        crop.x,
        crop.y,
        crop.width,
        crop.height,
        0,
        0,
        crop.width,
        crop.height
      )
      resolve(canvas.toDataURL("image/jpeg"))
    }
  })
