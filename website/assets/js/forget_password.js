document.getElementById("forgotForm").addEventListener("submit", function (e) {
  e.preventDefault();
  const email = document.getElementById("emailInput").value.trim();

  if (email === "") {
    alert("Vui lòng nhập email hoặc số điện thoại!");
    return;
  }

  // Giả lập gửi yêu cầu đặt lại mật khẩu
  alert(
    "Liên kết đặt lại mật khẩu đã được gửi đến: " +
      email +
      "\\nVui lòng kiểm tra hộp thư của bạn."
  );

  // Reset form
  document.getElementById("forgotForm").reset();
});
