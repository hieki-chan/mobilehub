// Dữ liệu mẫu
const products = [
  {
    id: 1,
    name: "iPhone 15 Pro",
    description: "Điện thoại cao cấp của Apple",
    price: 29990000,
    status: 1,
    product_discount_id: 101,
    product_spec_id: 201,
  },
  {
    id: 2,
    name: "Samsung Galaxy S24",
    description: "Flagship mới của Samsung",
    price: 24990000,
    status: 1,
    product_discount_id: 102,
    product_spec_id: 202,
  },
  {
    id: 3,
    name: "Xiaomi 14 Ultra",
    description: "Camera Leica, hiệu năng mạnh",
    price: 19990000,
    status: 0,
    product_discount_id: 103,
    product_spec_id: 203,
  },
  {
    id: 5,
    name: "iPhone 16 Pro",
    description: "Điện thoại cao cấp của Apple",
    price: 29990000,
    status: 1,
    product_discount_id: 101,
    product_spec_id: 201,
  },
  {
    id: 6,
    name: "iPhone 17 Pro",
    description: "Điện thoại cao cấp của Apple",
    price: 29990000,
    status: 1,
    product_discount_id: 101,
    product_spec_id: 201,
  },
];

// In-memory specs and images stores (simple simulation)
const specs = [
  {
    id: 201,
    brand: "Apple",
    cpu: "A17",
    cpu_speed: "3.5GHz",
    ram: "8GB",
    storage_cap: "256GB",
  },
  {
    id: 202,
    brand: "Samsung",
    cpu: "Snapdragon",
    cpu_speed: "3.2GHz",
    ram: "12GB",
    storage_cap: "256GB",
  },
];

// DOM elements
const overlay = document.getElementById("overlay");
const addForm = document.getElementById("addProductForm");
const btnAdd = document.getElementById("btnAdd");
const closeBtn = document.getElementById("closeForm");
const tbody = document.getElementById("productList");

// Hàm render danh sách sản phẩm
function renderProducts() {
  tbody.innerHTML = "";
  products.forEach((p, i) => {
    const spec = specs.find((s) => s.id === p.product_spec_id) || null;
    tbody.innerHTML += `
      <tr>
        <td>${p.id}</td>
        <td>${p.name}</td>
        <td>${p.description}</td>
        <td>${p.price.toLocaleString("vi-VN")} ₫</td>
        <td>${p.status === 1 ? "Hoạt động" : "Ngừng"}</td>
        <td>${p.product_discount_id}</td>
        <td>${spec ? spec.brand + (spec.cpu ? ' - ' + spec.cpu : '') : p.product_spec_id}</td>
        <td>
          <button class="btn btn-sm btn-warning me-2" onclick="editProduct(${i})">
            <i class="bi bi-pencil"></i>
          </button>
          <button class="btn btn-sm btn-danger" onclick="deleteProduct(${i})">
            <i class="bi bi-trash"></i>
          </button>
        </td>
      </tr>
    `;
  });
}

// Mở modal khi nhấn + Thêm sản phẩm
btnAdd.addEventListener("click", () => {
  overlay.style.display = "flex";
  addForm.reset();
  delete addForm.dataset.editIndex;
});

// Đóng modal khi nhấn nút x
closeBtn.addEventListener("click", () => {
  overlay.style.display = "none";
});

// Thêm hoặc sửa sản phẩm khi submit form
addForm.addEventListener("submit", function (e) {
  e.preventDefault();

  const name = document.getElementById("productName").value;
  const desc = document.getElementById("productDesc").value;
  const price = parseFloat(document.getElementById("productPrice").value);
  const status = parseInt(document.getElementById("productStatus").value);
  const discount = parseInt(document.getElementById("productDiscount").value);
  // collect spec inputs
  const specData = {
    brand: document.getElementById("spec_brand").value || null,
    cpu: document.getElementById("spec_cpu").value || null,
    cpu_speed: document.getElementById("spec_cpu_speed").value || null,
    ram: document.getElementById("spec_ram").value || null,
    storage_cap: document.getElementById("spec_storage_cap").value || null,
    os: document.getElementById("spec_os").value || null,
    battery_cap: document.getElementById("spec_battery_cap").value || null,
    front_cam: document.getElementById("spec_front_cam").value || null,
    rear_cam: document.getElementById("spec_rear_cam").value || null,
    gpu: document.getElementById("spec_gpu").value || null,
    screen_res: document.getElementById("spec_screen_res").value || null,
    size_weight: document.getElementById("spec_size_weight").value || null,
    features: document.getElementById("spec_features").value || null,
    material: document.getElementById("spec_material").value || null,
    release_date: document.getElementById("spec_release_date").value || null,
  };

  // collect images (one URL per line)
  const imagesRaw = document.getElementById("productImages").value || "";
  const images = imagesRaw
    .split(/\r?\n/)
    .map((s) => s.trim())
    .filter(Boolean)
    .map((url, idx) => ({ id: idx + 1, url, is_main: idx === 0 }));

  const editIndex = this.dataset.editIndex;

  if (editIndex !== undefined) {
    // Sửa sản phẩm
    // update spec if exists or create
    let specId = products[editIndex].product_spec_id;
    if (specId && specs.find((s) => s.id === specId)) {
      const s = specs.find((s) => s.id === specId);
      Object.assign(s, specData);
    } else {
      specId = specs.length > 0 ? specs[specs.length - 1].id + 1 : 201;
      specs.push({ id: specId, ...specData });
    }

    products[editIndex] = {
      ...products[editIndex],
      name,
      description: desc,
      price,
      status,
      product_discount_id: discount,
      product_spec_id: specId,
      product_images: images,
    };
    delete this.dataset.editIndex;
  } else {
    // Thêm sản phẩm mới
    const newId =
      products.length > 0 ? products[products.length - 1].id + 1 : 1;
    // create new spec entry
    const newSpecId = specs.length > 0 ? specs[specs.length - 1].id + 1 : 201;
    specs.push({ id: newSpecId, ...specData });

    products.push({
      id: newId,
      name,
      description: desc,
      price,
      status,
      product_discount_id: discount,
      product_spec_id: newSpecId,
      product_images: images,
    });
  }

  renderProducts();
  this.reset();
  overlay.style.display = "none"; // ẩn modal sau khi submit
});

// Sửa sản phẩm
function editProduct(index) {
  const product = products[index];
  document.getElementById("productName").value = product.name;
  document.getElementById("productDesc").value = product.description;
  document.getElementById("productPrice").value = product.price;
  document.getElementById("productStatus").value = product.status;
  document.getElementById("productDiscount").value =
    product.product_discount_id;
  // populate spec fields if available
  const spec = specs.find((s) => s.id === product.product_spec_id) || {};
  document.getElementById("spec_brand").value = spec.brand || "";
  document.getElementById("spec_cpu").value = spec.cpu || "";
  document.getElementById("spec_cpu_speed").value = spec.cpu_speed || "";
  document.getElementById("spec_ram").value = spec.ram || "";
  document.getElementById("spec_storage_cap").value = spec.storage_cap || "";
  document.getElementById("spec_os").value = spec.os || "";
  document.getElementById("spec_battery_cap").value = spec.battery_cap || "";
  document.getElementById("spec_front_cam").value = spec.front_cam || "";
  document.getElementById("spec_rear_cam").value = spec.rear_cam || "";
  document.getElementById("spec_gpu").value = spec.gpu || "";
  document.getElementById("spec_screen_res").value = spec.screen_res || "";
  document.getElementById("spec_size_weight").value = spec.size_weight || "";
  document.getElementById("spec_features").value = spec.features || "";
  document.getElementById("spec_material").value = spec.material || "";
  document.getElementById("spec_release_date").value = spec.release_date || "";

  // populate images textarea
  const imgs = (product.product_images || []).map((i) => i.url).join("\n");
  document.getElementById("productImages").value = imgs;

  addForm.dataset.editIndex = index;
  overlay.style.display = "flex";
}

// Xóa sản phẩm
function deleteProduct(index) {
  if (confirm("Bạn có chắc muốn xóa sản phẩm này?")) {
    products.splice(index, 1);
    renderProducts();
  }
}

// Render lần đầu
renderProducts();
