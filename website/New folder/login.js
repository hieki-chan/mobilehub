// Hiệu ứng xuất hiện form
window.addEventListener("load", () => {
  document.getElementById("loginBox").classList.add("show");
});

// Xử lý nút đăng nhập
document.getElementById("loginBtn").addEventListener("click", () => {
  const phone = document.getElementById("phoneInput").value.trim();
  const pass = document.getElementById("passwordInput").value.trim();
  const box = document.getElementById("loginBox");

  if (phone === "" || pass === "") {
    box.classList.add("shake");
    setTimeout(() => box.classList.remove("shake"), 400);
    alert("Vui lòng nhập đầy đủ thông tin đăng nhập!");
  } else {
    alert("Đăng nhập thành công!");
  }
});
