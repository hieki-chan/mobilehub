(() => {
  const form = document.getElementById("signupForm");
  form.addEventListener(
    "submit",
    (event) => {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      } else {
        event.preventDefault();
        alert("Đăng ký thành công (demo)!");
        window.location.href = "login.html";
      }
      form.classList.add("was-validated");
    },
    false
  );
})();
