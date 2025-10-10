function selectAccount(email) {
  if (email === "other") {
    // alert("Chọn: Sử dụng tài khoản khác");
    setTimeout(() => {
      window.location.href = "AccountDiffence.html"; // Trang chính
    }, 300);
  } else {
    setTimeout(() => {
      window.location.href = "index.html"; // Trang chính
    }, 0);
  }
}
