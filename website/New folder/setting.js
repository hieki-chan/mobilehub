// ====== Helpers ======
function showToast(title, message, autohide = true) {
  const id = "t" + Date.now();
  const toastHTML = `
    <div id="${id}" class="toast align-items-center text-bg-primary border-0" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">
          <strong>${title}</strong><div>${message}</div>
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>`;
  const container = document.getElementById("toastContainer");
  container.insertAdjacentHTML("beforeend", toastHTML);
  const tEl = document.getElementById(id);
  const toast = new bootstrap.Toast(tEl, { autohide, delay: 3000 });
  toast.show();
  tEl.addEventListener("hidden.bs.toast", () => tEl.remove());
}

// ====== Avatar preview ======
const avatarInput = document.getElementById("avatarInput");
const avatarPreview = document.getElementById("avatarPreview");
avatarInput?.addEventListener("change", (e) => {
  const file = e.target.files[0];
  if (!file) return;
  const url = URL.createObjectURL(file);
  avatarPreview.src = url;
  localStorage.setItem("mock_avatar", url);
});

// ====== Load dữ liệu từ localStorage ======
function loadProfile() {
  const savedName = localStorage.getItem("mock_name");
  const savedEmail = localStorage.getItem("mock_email");
  const savedPhone = localStorage.getItem("mock_phone");
  const savedAvatar = localStorage.getItem("mock_avatar");

  if (savedName) {
    document.getElementById("displayName").textContent = savedName;
    document.getElementById("inputName").value = savedName;
  }
  if (savedEmail) {
    document.getElementById("emailDisplay").textContent = savedEmail;
    document.getElementById("inputEmail").value = savedEmail;
  }
  if (savedPhone) {
    document.getElementById("phoneDisplay").textContent = savedPhone;
    document.getElementById("inputPhone").value = savedPhone;
  }
  if (savedAvatar) avatarPreview.src = savedAvatar;
}
window.addEventListener("load", loadProfile);

// ====== Profile form ======
const profileForm = document.getElementById("profileForm");
profileForm?.addEventListener("submit", (e) => {
  e.preventDefault();
  if (!profileForm.checkValidity()) {
    profileForm.classList.add("was-validated");
    return;
  }

  const name = document.getElementById("inputName").value;
  const email = document.getElementById("inputEmail").value;
  const phone = document.getElementById("inputPhone").value;

  document.getElementById("displayName").textContent = name;
  document.getElementById("emailDisplay").textContent = email;
  document.getElementById("phoneDisplay").textContent = phone;

  localStorage.setItem("mock_name", name);
  localStorage.setItem("mock_email", email);
  localStorage.setItem("mock_phone", phone);

  showToast("OK", "Hồ sơ đã được cập nhật.");
});

// ====== Password form ======
const passwordForm = document.getElementById("passwordForm");
passwordForm?.addEventListener("submit", (e) => {
  e.preventDefault();
  const newPwd = document.getElementById("newPassword");
  const confirm = document.getElementById("confirmPassword");
  let valid = true;

  if (!passwordForm.checkValidity()) valid = false;
  if (newPwd.value !== confirm.value) {
    confirm.classList.add("is-invalid");
    valid = false;
  } else {
    confirm.classList.remove("is-invalid");
  }

  if (!valid) {
    passwordForm.classList.add("was-validated");
    return;
  }

  passwordForm.reset();
  passwordForm.classList.remove("was-validated");
  showToast("Thành công", "Mật khẩu đã được cập nhật.");
});

// ====== Two-factor authentication ======
const twoFactor = document.getElementById("twoFactor");
twoFactor?.addEventListener("change", (e) => {
  if (e.target.checked) {
    showToast("2FA", "Xác thực hai yếu tố đã được bật.");
  } else {
    showToast("2FA", "Xác thực hai yếu tố đã bị tắt.");
  }
});

// ====== Delete account ======
const btnDelete = document.getElementById("btnDelete");
btnDelete?.addEventListener("click", () => {
  // Xóa thông tin hiển thị
  document.getElementById("displayName").textContent = "";
  document.getElementById("emailDisplay").textContent = "";
  document.getElementById("phoneDisplay").textContent = "";
  avatarPreview.src = "../images/anh_bia.png";

  // Xóa input form
  document.getElementById("inputName").value = "";
  document.getElementById("inputEmail").value = "";
  document.getElementById("inputPhone").value = "";

  // Xóa localStorage
  localStorage.removeItem("mock_name");
  localStorage.removeItem("mock_email");
  localStorage.removeItem("mock_phone");
  localStorage.removeItem("mock_avatar");

  // Xóa navbar user (nếu có)
  const navUser = document.getElementById("navUser");
  if (navUser) navUser.textContent = "";

  showToast("Đã xóa", "Tài khoản và thông tin liên quan đã bị xóa (mock).");
});

// ====== Checkbox style: đổi xanh → đen ======
document.querySelectorAll('input[type="checkbox"]').forEach((cb) => {
  cb.addEventListener("change", (e) => {
    if (cb.checked) {
      cb.style.backgroundColor = "#343a40";
      cb.style.borderColor = "#343a40";
    } else {
      cb.style.backgroundColor = "";
      cb.style.borderColor = "";
    }
  });
});

// ====== Accessibility: focus outline ======
(function () {
  function handleFirstTab(e) {
    if (e.key === "Tab") document.body.classList.add("user-is-tabbing");
    window.removeEventListener("keydown", handleFirstTab);
  }
  window.addEventListener("keydown", handleFirstTab);
})();
