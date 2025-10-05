document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.querySelector("form");
  const usernameInput = document.getElementById("usernameInput");
  const passwordInput = document.getElementById("passwordInput");

  loginForm.addEventListener("submit", function (e) {
    e.preventDefault();

    // Kiểm tra rỗng
    if (
      usernameInput.value.trim() === "" ||
      passwordInput.value.trim() === ""
    ) {
      alert("Vui lòng nhập đầy đủ Tên người dùng/Số điện thoại và Mật khẩu!");
      return;
    }

    // Nếu ok -> giả lập đăng nhập thành công
    alert("Đăng nhập thành công với tài khoản: " + usernameInput.value);
  });
});
