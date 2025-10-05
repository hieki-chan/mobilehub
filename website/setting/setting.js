// --- Helpers ---
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

// --- Avatar preview ---
const avatarInput = document.getElementById("avatarInput");
const avatarPreview = document.getElementById("avatarPreview");

avatarInput.addEventListener("change", (e) => {
  const file = e.target.files[0];
  if (!file) return;
  const url = URL.createObjectURL(file);
  avatarPreview.src = url;
  localStorage.setItem("mock_avatar", url);
});

// --- Profile form ---
const profileForm = document.getElementById("profileForm");
profileForm.addEventListener("submit", (e) => {
  e.preventDefault();
  if (!profileForm.checkValidity()) {
    profileForm.classList.add("was-validated");
    return;
  }
  // cập nhật hiển thị
  document.getElementById("displayName").textContent =
    document.getElementById("inputName").value;
  document.getElementById("emailDisplay").textContent =
    document.getElementById("inputEmail").value;
  document.getElementById("phoneDisplay").textContent =
    document.getElementById("inputPhone").value;

  // lưu vào localStorage
  localStorage.setItem("mock_name", document.getElementById("inputName").value);
  localStorage.setItem(
    "mock_email",
    document.getElementById("inputEmail").value
  );
  localStorage.setItem(
    "mock_phone",
    document.getElementById("inputPhone").value
  );

  showToast("OK", "Hồ sơ đã được cập nhật.");
});

document.getElementById("btnCancelProfile").addEventListener("click", () => {
  document.getElementById("inputName").value =
    document.getElementById("displayName").textContent;
  document.getElementById("inputEmail").value =
    document.getElementById("emailDisplay").textContent;
  document.getElementById("inputPhone").value =
    document.getElementById("phoneDisplay").textContent;
  profileForm.classList.remove("was-validated");
});

// --- Password form ---
const passwordForm = document.getElementById("passwordForm");
passwordForm.addEventListener("submit", (e) => {
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
  document.getElementById("currentPassword").value = "";
  document.getElementById("newPassword").value = "";
  document.getElementById("confirmPassword").value = "";
  passwordForm.classList.remove("was-validated");
  showToast("Thành công", "Mật khẩu đã được cập nhật.");
});

// --- Two factor auth ---
const twoFactor = document.getElementById("twoFactor");
twoFactor.addEventListener("change", (e) => {
  if (e.target.checked) {
    showToast("2FA", "Xác thực hai yếu tố đã được bật.");
  } else {
    showToast("2FA", "Xác thực hai yếu tố đã bị tắt.");
  }
});

// --- Delete account ---
const confirmInput = document.getElementById("confirmDeleteInput");
const btnConfirmDelete = document.getElementById("btnConfirmDelete");
confirmInput.addEventListener("input", () => {
  btnConfirmDelete.disabled = confirmInput.value.trim() !== "DELETE";
});
btnConfirmDelete.addEventListener("click", () => {
  const modal = bootstrap.Modal.getInstance(
    document.getElementById("deleteModal")
  );
  modal.hide();
  showToast("Đã xóa", "Tài khoản của bạn đã bị xóa (mock).");
});

// --- Load từ localStorage ---
window.addEventListener("load", () => {
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
});

// --- Accessibility: focus outline ---
(function () {
  function handleFirstTab(e) {
    if (e.key === "Tab") document.body.classList.add("user-is-tabbing");
    window.removeEventListener("keydown", handleFirstTab);
  }
  window.addEventListener("keydown", handleFirstTab);
})();

// trở về màn hình menu

document.getElementById("btnGoHome").addEventListener("click", () => {
  window.location.href = "menu.html";
});
