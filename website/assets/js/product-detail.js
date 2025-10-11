const alertBox = document.getElementById("alertBox");
const alertMsg = document.getElementById("alertMessage");

function showAlert(message) {
  alertMsg.textContent = message;
  alertBox.classList.add("show");
  setTimeout(() => alertBox.classList.remove("show"), 2000);
}

document.getElementById("btnAddCart").addEventListener("click", () => {
  showAlert("✅ Đã thêm sản phẩm vào giỏ hàng!");
});
document.getElementById("btnBuyNow").addEventListener("click", () => {
  showAlert("🛒 Chuyển đến trang thanh toán...");
});

// Danh sách ảnh (thay bằng ảnh thật sau)
const images = [
  "../images/hinh-nen-i16.jpg",
  "../images/hinh-nen-i16.jpg",
  "../images/hinh-nen-i16.jpg",
];

let current = 0;
const imgEl = document.getElementById("productImage");
const dots = document.querySelectorAll(".dot");

// Cập nhật ảnh và chấm
function showImage(index) {
  imgEl.style.opacity = 0;
  setTimeout(() => {
    imgEl.src = images[index];
    imgEl.style.opacity = 1;
  }, 300);
  dots.forEach((dot, i) => dot.classList.toggle("active", i === index));
}

// Nút next / prev
document.querySelector(".next-btn").addEventListener("click", () => {
  current = (current + 1) % images.length;
  showImage(current);
});

document.querySelector(".prev-btn").addEventListener("click", () => {
  current = (current - 1 + images.length) % images.length;
  showImage(current);
});

// Chấm bấm chọn ảnh
dots.forEach((dot, i) => {
  dot.addEventListener("click", () => {
    current = i;
    showImage(current);
  });
});

// Tự động chuyển sau 3s
setInterval(() => {
  current = (current + 1) % images.length;
  showImage(current);
}, 10000);

// === Popup Thông số kỹ thuật ===
const popupThongSo = document.getElementById("popupThongSo");
const btnThongSo = document.getElementById("btnThongSo");
const closePopup = document.querySelector(".close-popup");

btnThongSo.addEventListener("click", () => {
  popupThongSo.classList.add("show");
});

closePopup.addEventListener("click", () => {
  popupThongSo.classList.remove("show");
});

// Khi click ra ngoài popup thì cũng tắt
popupThongSo.addEventListener("click", (e) => {
  if (e.target === popupThongSo) {
    popupThongSo.classList.remove("show");
  }
});
// === Popup Viết đánh giá ===
const btnDanhGia = document.getElementById("btnDanhGia");
const popupDanhGia = document.getElementById("popupDanhGia");
const closeDanhGia = document.querySelector(".close-popup-dg");
const formDanhGia = document.getElementById("formDanhGia");

btnDanhGia.addEventListener("click", () => {
  popupDanhGia.classList.add("show");
});

closeDanhGia.addEventListener("click", () => {
  popupDanhGia.classList.remove("show");
});

popupDanhGia.addEventListener("click", (e) => {
  if (e.target === popupDanhGia) {
    popupDanhGia.classList.remove("show");
  }
});

formDanhGia.addEventListener("submit", (e) => {
  e.preventDefault();
  alert("Cảm ơn bạn đã gửi đánh giá!");
  popupDanhGia.classList.remove("show");
  formDanhGia.reset();
});
