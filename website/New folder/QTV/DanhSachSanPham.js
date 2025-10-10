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
    tbody.innerHTML += `
      <tr>
        <td>${p.id}</td>
        <td>${p.name}</td>
        <td>${p.description}</td>
        <td>${p.price.toLocaleString("vi-VN")} ₫</td>
        <td>${p.status === 1 ? "Hoạt động" : "Ngừng"}</td>
        <td>${p.product_discount_id}</td>
        <td>${p.product_spec_id}</td>
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
  const spec = parseInt(document.getElementById("productSpec").value);

  const editIndex = this.dataset.editIndex;

  if (editIndex !== undefined) {
    // Sửa sản phẩm
    products[editIndex] = {
      ...products[editIndex],
      name,
      description: desc,
      price,
      status,
      product_discount_id: discount,
      product_spec_id: spec,
    };
    delete this.dataset.editIndex;
  } else {
    // Thêm sản phẩm mới
    const newId =
      products.length > 0 ? products[products.length - 1].id + 1 : 1;
    products.push({
      id: newId,
      name,
      description: desc,
      price,
      status,
      product_discount_id: discount,
      product_spec_id: spec,
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
  document.getElementById("productSpec").value = product.product_spec_id;

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
