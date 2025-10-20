// product-create.js

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("productForm");
    if (!form) {
        console.error("❌ Không tìm thấy form với id='productForm'");
        return;
    }

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const submitBtn = form.querySelector("button[type='submit']");
        submitBtn.disabled = true;
        submitBtn.innerText = "Đang xử lý...";

        // ✅ Lấy selectedFiles từ image-previewer.js
        if (!window.selectedFiles || selectedFiles.length === 0) {
            alert("⚠️ Vui lòng chọn ít nhất 1 ảnh sản phẩm!");
            submitBtn.disabled = false;
            submitBtn.innerText = "OK";
            return;
        }

        const requestData = {
            name: form.name.value,
            description: form.description.value,
            price: parseFloat(form.price.value),
            os: form.os.value,
            cpu: form.cpu.value,
            cpu_speed: form.cpu_speed.value,
            gpu: form.gpu.value,
            ram: form.ram.value,
            storage_cap: form.storage_cap.value,
            battery_cap: form.battery_cap.value,
            rear_cam: form.rear_cam.value,
            front_cam: form.front_cam.value,
            screen_res: form.screen_res.value,
            features: form.features.value,
            material: form.material.value,
            size_weight: form.size_weight.value,
            brand: form.brand.value,
            release_date: form.release_date.value
        };

        const fd = new FormData();
        fd.append("request", new Blob([JSON.stringify(requestData)], { type: "application/json" }));

        // ✅ Dùng selectedFiles thay vì lấy từ <input type="file">
        for (let file of selectedFiles) {
            fd.append("files", file);
        }

        try {
            const response = await axios.post("http://localhost:8082/api/products/create", fd, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            alert("✅ Tạo sản phẩm thành công!");
            form.reset();
            imagePreviewContainer.innerHTML = "";
            selectedFiles = []; // Clear images

        } catch (err) {
            console.error(err);
            alert("❌ Có lỗi xảy ra khi tạo sản phẩm!");
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerText = "OK";
        }
    });
});
