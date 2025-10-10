document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.querySelector("form");
  const usernameInput = document.getElementById("usernameInput");
  const passwordInput = document.getElementById("passwordInput");

  loginForm.addEventListener("submit", function (e) {
    e.preventDefault();
    if (
      usernameInput.value.trim() === "" ||
      passwordInput.value.trim() === ""
    ) {
      alert("Vui lòng nhập đầy đủ Tên người dùng/Số điện thoại và Mật khẩu!");
      return;
    }

    alert("Đăng nhập thành công với tài khoản: " + usernameInput.value);
  });
});

function handleLogin(event) {
  event.preventDefault();

  const email = document.getElementById("email").value;

  if (email.trim() === "") {
    alert("Vui lòng nhập email hoặc số điện thoại!");
    return;
  }

  alert("Đăng nhập thành công với: " + email);

  // Chuyển về màn hình chính (index.html)
  window.location.href = "index.html";
}
document.getElementById("loginEmailBtn").addEventListener("click", function () {
  window.location.href = "loginWithGmail.html";
});
