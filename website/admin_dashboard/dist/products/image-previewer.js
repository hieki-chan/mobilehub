// Image preview functionality
const imagePreviewContainer = document.getElementById('imagePreview');
let selectedFiles = [];
let draggedIndex = null;

document.getElementById('files').addEventListener('change', function (e) {
    const newFiles = Array.from(e.target.files);
    selectedFiles = [...selectedFiles, ...newFiles];
    displayImagePreviews(selectedFiles);
    this.value = '';
});

function displayImagePreviews(files) {
    imagePreviewContainer.innerHTML = '';

    files.forEach((file, index) => {
        const reader = new FileReader();
        reader.onload = function (e) {
            const card = document.createElement('div');
            card.className = 'image-preview-card';
            card.draggable = true;
            card.dataset.index = index;

            // Drag events
            card.addEventListener('dragstart', handleDragStart);
            card.addEventListener('dragover', handleDragOver);
            card.addEventListener('drop', handleDrop);
            card.addEventListener('dragend', handleDragEnd);

            card.innerHTML = `
    <div class="image-wrapper" onclick="viewImage('${e.target.result}')">
        <img src="${e.target.result}" alt="Preview">
        <div class="image-overlay">
            <i class="bi bi-eye-fill"></i>
        </div>
    </div>
    <button type="button" class="remove-btn" onclick="removeImage(${index})"></button>
`;
            imagePreviewContainer.appendChild(card);
        };
        reader.readAsDataURL(file);
    });
}

function handleDragStart(e) {
    draggedIndex = parseInt(e.currentTarget.dataset.index);
    e.currentTarget.classList.add('dragging');
    e.dataTransfer.effectAllowed = 'move';
}

function handleDragOver(e) {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';

    const afterElement = getDragAfterElement(imagePreviewContainer, e.clientX);
    const draggingElement = document.querySelector('.dragging');

    if (afterElement == null) {
        imagePreviewContainer.appendChild(draggingElement);
    } else {
        imagePreviewContainer.insertBefore(draggingElement, afterElement);
    }
}

function handleDrop(e) {
    e.preventDefault();
    const dropIndex = parseInt(e.currentTarget.dataset.index);

    if (draggedIndex !== dropIndex) {
        // Reorder array
        const draggedFile = selectedFiles[draggedIndex];
        selectedFiles.splice(draggedIndex, 1);

        // Adjust drop index if needed
        const newDropIndex = draggedIndex < dropIndex ? dropIndex - 1 : dropIndex;
        selectedFiles.splice(newDropIndex, 0, draggedFile);

        displayImagePreviews(selectedFiles);
    }
}

function handleDragEnd(e) {
    e.currentTarget.classList.remove('dragging');
    document.querySelectorAll('.image-preview-card').forEach(card => {
        card.classList.remove('drag-over');
    });
}

function getDragAfterElement(container, x) {
    const draggableElements = [...container.querySelectorAll('.image-preview-card:not(.dragging)')];

    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = x - box.left - box.width / 2;

        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child };
        } else {
            return closest;
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
}

function removeImage(index) {
    selectedFiles.splice(index, 1);
    displayImagePreviews(selectedFiles);
}

function viewImage(src) {
    const modal = document.getElementById('imageModal');
    const modalImg = document.getElementById('modalImage');
    modalImg.src = src;
    modal.style.display = 'block';
    modal.classList.add('show');
    document.body.classList.add('modal-open');

    const backdrop = document.createElement('div');
    backdrop.className = 'modal-backdrop fade show';
    backdrop.id = 'imageModalBackdrop';
    document.body.appendChild(backdrop);

    backdrop.onclick = function () {
        closeImageModal();
    };
}

function closeImageModal() {
    const modal = document.getElementById('imageModal');
    const backdrop = document.getElementById('imageModalBackdrop');

    modal.style.display = 'none';
    modal.classList.remove('show');
    document.body.classList.remove('modal-open');

    if (backdrop) {
        backdrop.remove();
    }
}