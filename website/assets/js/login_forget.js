const input = document.getElementById("usernameInput");
const btn = document.getElementById("submitBtn");

input.addEventListener("input", () => {
  btn.disabled = input.value.trim() === "";
});

btn.addEventListener("click", () => {
  alert("Link khôi phục sẽ được gửi đến: " + input.value);
});
