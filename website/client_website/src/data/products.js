// src/data/products.js
export const products = [
  {
    id: "v1",
    name: "Vphone Pro 6 - 256GB",
    price: 19990000,
    oldPrice: 21990000,
    images: [
      "https://via.placeholder.com/900x600?text=Vphone+Pro+6",
      "https://via.placeholder.com/900x600?text=Vphone+Pro+6+2"
    ],
    tags: ["new"],
    status: "available",
    sku: "VPHONE-PRO-6-256",
    desc: "Vphone Pro 6 - Màn hình 6.7\", camera 108MP, pin 5000mAh.",
    
    // Flat structure - dễ map từ database
    battery_cap: "5000mAh",
    brand: "Vphone",
    cpu: "Snapdragon 6 Gen 4 5G",
    cpu_speed: "2.3 GHz",
    features: "NFC, USB-C, Bluetooth 5.3",
    front_cam: "12MP",
    gpu: "Adreno 710",
    material: "Nhôm + Kính cường lực",
    os: "Android 15",
    ram: "8 GB",
    rear_cam: "108MP",
    release_date: "2025-11-01",
    screen_res: "1080x2400",
    size_weight: "6.7 inch - 195g",
    storage_cap: "256GB",
    
    variants: {
      capacity: ["128GB", "256GB"],
      color: [
        { label: "Đen", value: "#111827" },
        { label: "Xanh", value: "#0ea5e9" },
        { label: "Đỏ", value: "#dd0000" }
      ]
    }
  },
  {
    id: "v2",
    name: "Vphone X - 128GB",
    price: 13990000,
    images: ["https://via.placeholder.com/900x600?text=Vphone+X"],
    status: "available",
    sku: "VPHONE-X-128",
    desc: "Vphone X - Mỏng nhẹ, hiệu năng tốt.",
    
    battery_cap: "4500mAh",
    brand: "Vphone",
    cpu: "Snapdragon 7 Gen 3",
    cpu_speed: "2.5 GHz",
    features: "NFC, USB-C, Bluetooth 5.3",
    front_cam: "16MP",
    gpu: "Adreno 725",
    material: "Nhôm + Kính cường lực",
    os: "Android 14",
    ram: "8 GB",
    rear_cam: "64MP",
    release_date: "2024-09-15",
    screen_res: "1080x2400",
    size_weight: "6.5 inch - 180g",
    storage_cap: "128GB",
    
    variants: { 
      capacity: ["64GB", "128GB"], 
      color: [
        { label: "Tím", value: "#7c3aed" }, 
        { label: "Xám", value: "#6b7280" }
      ] 
    }
  },
  {
    id: "v3",
    name: "Galaxy S25 Ultra",
    price: 32990000,
    images: ["https://via.placeholder.com/900x600?text=Galaxy+S25+Ultra"],
    status: "available",
    sku: "SAMSUNG-S25-ULTRA",
    desc: "Samsung Galaxy S25 Ultra - Flagship đỉnh cao với S Pen.",
    
    battery_cap: "5000mAh",
    brand: "Samsung",
    cpu: "Snapdragon 8 Gen 3",
    cpu_speed: "3.2 GHz",
    features: "NFC, USB-C, S Pen, Bluetooth 5.3",
    front_cam: "12MP",
    gpu: "Adreno 750",
    material: "Titan + Kính Gorilla Glass Victus",
    os: "Android 14",
    ram: "12 GB",
    rear_cam: "200MP",
    release_date: "2025-02-01",
    screen_res: "1440x3120",
    size_weight: "6.8 inch - 233g",
    storage_cap: "512GB",
    
    variants: {
      capacity: ["256GB", "512GB"],
      color: [
        { label: "Đen", value: "#111827" },
        { label: "Xanh", value: "#0ea5e9" }
      ]
    }
  },
  {
    id: "v4",
    name: "iPhone 17 Pro",
    price: 42990000,
    images: ["https://via.placeholder.com/900x600?text=iPhone+17+Pro"],
    status: "available",
    sku: "APPLE-IP17-PRO",
    desc: "iPhone 17 Pro - Chip A18 Pro mạnh mẽ, camera 48MP.",
    
    battery_cap: "4323mAh",
    brand: "Apple",
    cpu: "Apple A18 Pro",
    cpu_speed: "3.8 GHz",
    features: "Face ID, MagSafe, USB-C",
    front_cam: "12MP",
    gpu: "Apple GPU 6 nhân",
    material: "Titan + Kính Ceramic Shield",
    os: "iOS 18",
    ram: "8 GB",
    rear_cam: "48MP",
    release_date: "2024-09-22",
    screen_res: "1284x2778",
    size_weight: "6.3 inch - 221g",
    storage_cap: "256GB",
    
    variants: {
      capacity: ["128GB", "256GB", "512GB"],
      color: [
        { label: "Titan Tự nhiên", value: "#d4c5b9" },
        { label: "Titan Đen", value: "#3b3b3b" }
      ]
    }
  },
  {
    id: "v5",
    name: "Google Pixel 9",
    price: 24990000,
    images: ["https://via.placeholder.com/900x600?text=Pixel+9"],
    status: "available",
    sku: "GOOGLE-PIXEL-9",
    desc: "Google Pixel 9 - Camera AI đỉnh cao, Tensor G4.",
    
    battery_cap: "4700mAh",
    brand: "Google",
    cpu: "Google Tensor G4",
    cpu_speed: "2.9 GHz",
    features: "NFC, USB-C, Magic Eraser",
    front_cam: "10.5MP",
    gpu: "Mali-G715",
    material: "Nhôm + Kính Gorilla Glass",
    os: "Android 15",
    ram: "12 GB",
    rear_cam: "50MP",
    release_date: "2024-10-10",
    screen_res: "1080x2400",
    size_weight: "6.3 inch - 198g",
    storage_cap: "256GB",
    
    variants: {
      capacity: ["128GB", "256GB"],
      color: [
        { label: "Xanh Mint", value: "#98d8c8" },
        { label: "Hồng", value: "#f7a8b8" }
      ]
    }
  },
  {
    id: "v6",
    name: "Vphone Lite - 64GB",
    price: 4990000,
    images: ["https://via.placeholder.com/900x600?text=Vphone+Lite"],
    status: "available",
    sku: "VPHONE-LITE-64",
    desc: "Vphone Lite - Giá mềm, pin ổn, phù hợp sinh viên.",
    
    battery_cap: "4000mAh",
    brand: "Vphone",
    cpu: "Snapdragon 4 Gen 2",
    cpu_speed: "2.0 GHz",
    features: "USB-C, Bluetooth 5.1",
    front_cam: "8MP",
    gpu: "Adreno 619",
    material: "Nhựa + Kính",
    os: "Android 13",
    ram: "4 GB",
    rear_cam: "50MP",
    release_date: "2024-06-20",
    screen_res: "720x1600",
    size_weight: "6.5 inch - 185g",
    storage_cap: "64GB",
    
    variants: {
      capacity: ["64GB"],
      color: [
        { label: "Xanh", value: "#0ea5e9" },
        { label: "Đen", value: "#111827" }
      ]
    }
  },
  {
    id: "v7",
    name: "ROG Phone 8 Gaming",
    price: 18990000,
    oldPrice: 19990000,
    images: ["https://via.placeholder.com/900x600?text=ROG+Phone+8"],
    tags: ["sale"],
    status: "available",
    sku: "ASUS-ROG-8",
    desc: "Phone gaming chuyên nghiệp với tản nhiệt tốt, AirTriggers.",
    
    battery_cap: "5500mAh",
    brand: "Asus",
    cpu: "Snapdragon 8 Gen 2",
    cpu_speed: "3.0 GHz",
    features: "AirTriggers, RGB, USB-C, Bluetooth 5.3",
    front_cam: "32MP",
    gpu: "Adreno 740",
    material: "Nhôm + Kính",
    os: "Android 13",
    ram: "16 GB",
    rear_cam: "50MP",
    release_date: "2024-04-15",
    screen_res: "1080x2448",
    size_weight: "6.78 inch - 239g",
    storage_cap: "512GB",
    
    variants: {
      capacity: ["512GB"],
      color: [
        { label: "Đen", value: "#111827" }
      ]
    }
  },
  {
    id: "v8",
    name: "Vphone Mini (Sắp ra mắt)",
    price: null,
    images: ["https://via.placeholder.com/900x600?text=Coming+Soon"],
    status: "coming_soon",
    sku: "VPHONE-MINI",
    desc: "Vphone Mini - Kích thước nhỏ gọn, hiệu năng mạnh mẽ.",
    
    battery_cap: "4800mAh",
    brand: "Vphone",
    cpu: "Snapdragon 6 Gen 1",
    cpu_speed: "2.2 GHz",
    features: "NFC, USB-C, Bluetooth 5.2",
    front_cam: "16MP",
    gpu: "Adreno 710",
    material: "Nhôm",
    os: "Android 14",
    ram: "6 GB",
    rear_cam: "64MP",
    release_date: "2025-12-01",
    screen_res: "1080x2400",
    size_weight: "6.1 inch - 165g",
    storage_cap: "128GB",
    
    variants: {
      capacity: ["128GB"],
      color: [
        { label: "Trắng", value: "#ffffff" },
        { label: "Xanh", value: "#0ea5e9" }
      ]
    }
  }
]


export function getGroupedSpecs(spec, variant) {
  if (!spec) return {}
  
  return {
    "Cấu hình": {
      "Hệ điều hành": spec.os,
      "Chip xử lý (CPU)": spec.cpu,
      "Tốc độ CPU": spec.cpu_speed,
      "Chip đồ họa (GPU)": spec.gpu
    },
    "Bộ nhớ": {
      "RAM": variant.ram,
      "Dung lượng lưu trữ": variant.storage_cap
    },
    "Camera": {
      "Camera sau": spec.rear_cam,
      "Camera trước": spec.front_cam
    },
    "Màn hình": {
      "Kích thước & Trọng lượng": spec.size_weight,
      "Độ phân giải": spec.screen_res
    },
    "Pin & Sạc": {
      "Dung lượng pin": spec.battery_cap
    },
    "Thiết kế": {
      "Chất liệu": spec.material,
      "Màu sắc": variant.color_label,
      "Hãng": spec.brand,
      "Tính năng": spec.features,
      "Ngày ra mắt": spec.release_date
    }
  }
}