const uploadArea = document.getElementById('uploadArea');
const fileInput = document.getElementById('fileInput');
const previewContainer = document.getElementById('previewContainer');
const form = document.getElementById('productForm');
let selectedFiles = [];

uploadArea.addEventListener('click', () => fileInput.click());

uploadArea.addEventListener('dragover', (e) => {
    e.preventDefault();
    uploadArea.classList.add('dragover');
});

uploadArea.addEventListener('dragleave', () => {
    uploadArea.classList.remove('dragover');
});

uploadArea.addEventListener('drop', (e) => {
    e.preventDefault();
    uploadArea.classList.remove('dragover');
    const files = Array.from(e.dataTransfer.files).filter(f => f.type.startsWith('image/'));
    handleFiles(files);
});

fileInput.addEventListener('change', (e) => {
    const files = Array.from(e.target.files);
    handleFiles(files);
});

function handleFiles(files) {
    selectedFiles = [...selectedFiles, ...files];
    displayPreviews();
}

function displayPreviews() {
    previewContainer.innerHTML = '';
    selectedFiles.forEach((file, index) => {
        const reader = new FileReader();
        reader.onload = (e) => {
            const div = document.createElement('div');
            div.className = 'preview-item';
            div.innerHTML = `
                <img src="${e.target.result}" alt="Preview">
                <button type="button" class="remove-btn" onclick="removeFile(${index})">×</button>
            `;
            previewContainer.appendChild(div);
        };
        reader.readAsDataURL(file);
    });
}

function removeFile(index) {
    selectedFiles.splice(index, 1);
    displayPreviews();
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const submitBtn = form.querySelector('.submit-btn');
    const originalText = submitBtn.textContent;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Đang xử lý...';
    
    try {
        // Chuyển đổi images thành base64
        const imagePromises = selectedFiles.map(file => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = () => resolve(reader.result);
                reader.onerror = reject;
                reader.readAsDataURL(file);
            });
        });
        
        const imageBase64Array = await Promise.all(imagePromises);
        
        const requestData = {
            name: document.getElementById('name').value,
            description: document.getElementById('description').value,
            images: imageBase64Array
        };

        const response = await fetch('http://localhost:8081/api/products', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) {
            throw new Error('Lỗi khi tạo sản phẩm: ' + response.status);
        }

        const result = await response.json();
        console.log('Kết quả:', result);
        
        alert('✅ Sản phẩm đã được thêm thành công!\n\nTên: ' + result.name);
        
        // Upload files as multipart to the media endpoint (backend expects multipart/form-data)
        const uploadResults = await uploadMediaFiles(selectedFiles);
        console.log('Kết quả tải media:', uploadResults);
        const failed = uploadResults.filter(r => r && r.error);
        if (failed.length) {
            console.warn(`${failed.length} file(s) failed to upload`, failed);
            alert('⚠️ Một số tệp tải lên thất bại. Kiểm tra console để biết chi tiết.');
        } else {
            alert('✅ Tất cả tệp đã được tải lên thành công.');
        }
 
        form.reset();
        selectedFiles = [];
        previewContainer.innerHTML = '';
    } catch (error) {
        console.error('Lỗi:', error);
        alert('❌ Có lỗi xảy ra: ' + error.message);
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
});


/**
 * Tải lên một hoặc nhiều tệp tin lên server.
 *
 * @param {FileList | File[]} selectedFiles - Danh sách các tệp tin cần tải lên.
 * @param {string} [folder=''] - Tên thư mục tùy chọn để lưu trữ trên cloud.
 * @returns {Promise<Object[]>} - Promise trả về dữ liệu phản hồi từ server cho mỗi lần tải lên thành công.
 */
async function uploadMediaFiles(selectedFiles, folder = '') {
    const port = 8082;
    const uploadUrl = `http://localhost:${port}/api/media`; // match backend mapping

    if (!selectedFiles || selectedFiles.length === 0) {
        console.error("Không có tệp tin nào được chọn.");
        return [];
    }

    const uploadPromises = Array.from(selectedFiles).map(async (file) => {
        const formData = new FormData();
        formData.append('file', file); // must match @RequestParam("file")
        if (folder) formData.append('folder', folder);

        try {
            const response = await fetch(uploadUrl, {
                method: 'POST',
                body: formData, // do NOT set Content-Type manually
                // Add auth headers here if required
            });

            if (!response.ok) {
                const errText = await response.text().catch(() => '');
                throw new Error(`Tải lên ${file.name} thất bại. Status: ${response.status} ${errText}`);
            }

            return await response.json();
        } catch (error) {
            console.error(`Lỗi khi tải lên ${file.name}:`, error);
            return { error: error.message || String(error), fileName: file.name };
        }
    });

    const results = await Promise.all(uploadPromises);
    return results;
}