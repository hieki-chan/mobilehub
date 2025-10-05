const products = [
    { id: 1, name: 'RAVINOL CVT Fluid 1LT', status: 'Disabled', stock: 12, price: 8600.00 },
    { id: 2, name: 'RAVINOL Longlife LHG SAE 5W-30 5 LTS', status: 'Available', stock: 5, price: 33000.00 },
    { id: 3, name: 'RAVINOL VMP SAE 5W-30 1 LTS', status: 'Available', stock: 15, price: 3500.00 },
    { id: 4, name: 'RAVINOL Format Super SAE 10W-40 4 LTS', status: 'Available', stock: 12, price: 6000.00 },
    { id: 5, name: 'RAVINOL VMP SAE 5W-30 208 LTS', status: 'Available', stock: 0, price: 10500.00 },
    { id: 6, name: 'RAVINOL Format Extra SAE 20W-50 4 LTS', status: 'Available', stock: 18, price: 8600.00 },
    { id: 7, name: 'RAVINOL Longlife LHG SAE 5W-30 20 LTS', status: 'Available', stock: 5, price: 89250.00 },
    { id: 8, name: 'RAVINOL Gamabase PSG SAE 75W-90 1LT', status: 'Available', stock: 20, price: 9300.00 },
    { id: 9, name: 'RAVINOL Turbo C HD-C SAE 15W-40 20 LTS', status: 'Available', stock: 8, price: 45500.00 }
];

function formatPrice(price) {
    return '$' + price.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
}

function renderProducts(productsToRender) {
    const tbody = document.getElementById('product-productTable');
    tbody.innerHTML = '';

    productsToRender.forEach(product => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><input type="checkbox"></td>
            <td>
                <div class="product-product-info">
                    <div class="product-product-image">🛢️</div>
                    <span>${product.name}</span>
                </div>
            </td>
            <td>
                <span class="product-status product-status-${product.status.toLowerCase()}">
                    ${product.status}
                </span>
            </td>
            <td>${product.stock}</td>
            <td class="product-price">${formatPrice(product.price)}</td>
        `;
        tbody.appendChild(row);
    });
}

// Initial render
renderProducts(products);

// Search functionality
document.getElementById('product-searchInput').addEventListener('input', function(e) {
    const searchTerm = e.target.value.toLowerCase();
    const filtered = products.filter(p => 
        p.name.toLowerCase().includes(searchTerm)
    );
    renderProducts(filtered);
});

// Select all checkbox
document.getElementById('product-selectAll').addEventListener('change', function(e) {
    const checkboxes = document.querySelectorAll('tbody input[type="checkbox"]');
    checkboxes.forEach(cb => cb.checked = e.target.checked);
});

// Clear filters
document.querySelector('.product-clear-filters').addEventListener('click', function() {
    document.getElementById('product-searchInput').value = '';
    document.getElementById('product-categoryFilter').selectedIndex = 0;
    document.getElementById('product-productFilter').selectedIndex = 0;
    renderProducts(products);
});

// Filter by status
document.getElementById('product-productFilter').addEventListener('change', function(e) {
    const status = e.target.value;
    if (status === 'All Products') {
        renderProducts(products);
    } else {
        const filtered = products.filter(p => p.status === status);
        renderProducts(filtered);
    }
});
