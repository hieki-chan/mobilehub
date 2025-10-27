(function () {
    // 1. Tạo div menu ở đầu body
    var menuDiv = document.getElementById("sidebar");
    menuDiv.setAttribute("w3-include-html", "/website/admin_dashboard/dist/menu/menu.html");
    document.addEventListener("DOMContentLoaded", function () {
        document.body.prepend(menuDiv);
    });

    // 2. Hàm load w3.js nếu chưa có
    function loadW3(callback) {
        if (typeof w3 !== "undefined") {
            callback();
        } else {
            const script = document.createElement("script");
            script.src = "https://www.w3schools.com/lib/w3.js"; // có thể đổi link nếu muốn local
            script.onload = callback;
            document.head.appendChild(script);
        }
    }

    // 3. Gọi includeHTML sau khi w3js load
    loadW3(function () {
       w3.includeHTML(function () {
            // 🔥 BẮN EVENT SAU KHI MENU LOAD XONG
            document.dispatchEvent(new Event("menu-loaded"));
        });
    });
})();
