// Pie Chart
new Chart(document.getElementById("pieChart"), {
  type: "doughnut",
  data: {
    labels: ["Iphone", "Samsung", "oppo", "remie"],
    datasets: [
      {
        data: [72, 18, 6, 4],
        backgroundColor: ["#f7c948", "#d3d3d3", "#fff6b2", "#eee"],
      },
    ],
  },
  options: {
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "right",
        labels: {
          boxWidth: 15,
          padding: 10,
        },
      },
    },
    layout: {
      padding: {
        left: 10,
        right: 10,
        top: 10,
        bottom: 10,
      },
    },
  },
});

// Line Chart – Full 12 Months (Jan → Dec)
new Chart(document.getElementById("lineChart"), {
  type: "line",
  data: {
    labels: [
      "Jan",
      "Feb",
      "Mar",
      "Apr",
      "May",
      "Jun",
      "Jul",
      "Aug",
      "Sep",
      "Oct",
      "Nov",
      "Dec",
    ],
    datasets: [
      {
        label: "Sales",
        data: [0.8, 1.6, 1.2, 2.1, 2.4, 1.0, 1.8, 2.3, 1.7, 2.6, 2.1, 1.5],
        fill: true,
        borderColor: "#f7c948",
        backgroundColor: "rgba(247,201,72,0.3)",
        tension: 0.35,
        pointBackgroundColor: "#f7c948",
        pointBorderWidth: 2,
      },
    ],
  },

  options: {
    responsive: true,
    scales: {
      y: {
        beginAtZero: true,
        max: 3,
        ticks: {
          callback: function (value) {
            return "$" + value.toFixed(1) + "M";
          },
        },
      },
    },
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: function (context) {
            return "Sales: $" + context.parsed.y.toFixed(2) + "M";
          },
        },
      },
    },
  },
});

// Ads Expense
new Chart(document.getElementById("barChart"), {
  type: "bar",
  data: {
    labels: ["M", "T", "W", "T", "F", "S", "S"],
    datasets: [
      {
        data: [60, 120, 200, 90, 160, 180, 120],
        backgroundColor: [
          "#000",
          "#f7c948",
          "#000",
          "#f7c948",
          "#000",
          "#f7c948",
          "#000",
        ],
      },
    ],
  },
  options: { plugins: { legend: { display: false } } },
});

// Repayments - Line Chart (4 tháng)
const repayCtx = document.getElementById("repayChart").getContext("2d");
new Chart(repayCtx, {
  type: "line",
  data: {
    labels: ["Tháng 1", "Tháng 4", "Tháng 8", "Tháng 12"],
    datasets: [
      {
        label: "Nợ phải trả",
        data: [0.9, 1.2, 0.8, 1.5],
        fill: true,
        borderColor: "#f7c948",
        backgroundColor: "rgba(247, 201, 72, 0.3)",
        tension: 0.35,
        pointBackgroundColor: "#f7c948",
        pointBorderWidth: 2,
      },
    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        max: 2,
        ticks: {
          callback: function (value) {
            return "$" + value.toFixed(1) + "M";
          },
        },
      },
      x: {
        grid: { color: "rgba(0,0,0,0.05)" },
      },
    },
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: function (context) {
            return "Nợ: $" + context.parsed.y.toFixed(2) + "M";
          },
        },
      },
    },
  },
});

let editIndex = -1; // -1 = thêm mới, khác -1 = sửa
// Data mẫu
let products = [
  {
    id: 1,
    name: "Galaxy S23",
    brand: "Samsung",
    price: 16990000,
    stock: 12,
    image: "https://via.placeholder.com/60",
  },
  {
    id: 2,
    name: "iPhone 14",
    brand: "Apple",
    price: 24990000,
    stock: 8,
    image: "https://via.placeholder.com/60",
  },
  {
    id: 3,
    name: "Redmi Note 12",
    brand: "Xiaomi",
    price: 4990000,
    stock: 20,
    image: "https://via.placeholder.com/60",
  },
  {
    id: 1,
    name: "Galaxy S23",
    brand: "Samsung",
    price: 16990000,
    stock: 12,
    image: "https://via.placeholder.com/60",
  },
  {
    id: 1,
    name: "Galaxy S23",
    brand: "Samsung",
    price: 16990000,
    stock: 12,
    image: "https://via.placeholder.com/60",
  },
];

function renderProducts() {
  const tbody = document.getElementById("productsTable");
  tbody.innerHTML = "";

  let totalRevenue = 0; // biến tính doanh thu

  products.forEach((p, i) => {
    tbody.innerHTML += `
      <tr>
        <td>${i + 1}</td>
        <td><img src="${p.image}" class="img-thumbnail" width="60"></td>
        <td>${p.name}</td>
        <td>${p.brand}</td>
        <td>${p.price.toLocaleString()}₫</td>
        <td>${p.stock}</td>
        <td>
          <button class="btn btn-sm btn-primary btn-edit" data-index="${i}">
            <i class="bi bi-pencil"></i>
          </button>
          <button class="btn btn-sm btn-danger btn-delete" data-index="${i}">
            <i class="bi bi-trash"></i>
          </button>
        </td>
      </tr>
    `;

    // cộng dồn doanh thu = giá * số lượng
    totalRevenue += p.price * p.stock;
  });
  // --- Gắn sự kiện xoá ---
  document.querySelectorAll(".btn-delete").forEach((btn) => {
    btn.addEventListener("click", function () {
      const index = this.getAttribute("data-index");
      products.splice(index, 1);
      renderProducts();
    });
  });

  // --- Gắn sự kiện sửa ---
  document.querySelectorAll(".btn-edit").forEach((btn) => {
    btn.addEventListener("click", function () {
      const index = this.getAttribute("data-index");
      editIndex = index;
      const p = products[index];

      document.getElementById("productName").value = p.name;
      document.getElementById("productBrand").value = p.brand;
      document.getElementById("productPrice").value = p.price;
      document.getElementById("productStock").value = p.stock;
      document.getElementById("productImage").value = p.image;

      document.getElementById("addProductLabel").innerText =
        "Chỉnh sửa sản phẩm";
      document.querySelector("#addProductForm button[type=submit]").innerText =
        "Cập nhật";

      const modal = new bootstrap.Modal(
        document.getElementById("addProductModal")
      );
      modal.show();
    });
  });
}

renderProducts();

document
  .getElementById("addProductForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    const name = document.getElementById("productName").value.trim();
    const brand = document.getElementById("productBrand").value.trim();
    const price = parseInt(document.getElementById("productPrice").value);
    const stock = parseInt(document.getElementById("productStock").value);
    const image =
      document.getElementById("productImage").value.trim() ||
      "https://via.placeholder.com/60";

    if (editIndex === -1) {
      // 👉 Thêm mới
      products.push({
        id: products.length + 1,
        name,
        brand,
        price,
        stock,
        image,
      });
    } else {
      // 👉 Sửa sản phẩm
      products[editIndex] = {
        ...products[editIndex], // giữ id cũ
        name,
        brand,
        price,
        stock,
        image,
      };
      editIndex = -1; // reset về thêm mới
    }

    renderProducts();

    // reset form
    const modal = bootstrap.Modal.getInstance(
      document.getElementById("addProductModal")
    );
    modal.hide();
    document.getElementById("addProductForm").reset();

    // đổi lại tiêu đề & nút
    document.getElementById("addProductLabel").innerText =
      "Thêm sản phẩ  m mới";
    document.querySelector("#addProductForm button[type=submit]").innerText =
      "Lưu";
  });
