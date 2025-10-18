
(function () {
  // Keep mock consistent with home.js
  const mockProducts = [
    { id: "v1", name: "Vphone Pro 6 - 256GB", price: 19990000, oldPrice: 21990000, images: ["https://via.placeholder.com/900x600?text=Vphone+Pro+6", "https://via.placeholder.com/900x600?text=Vphone+Pro+6+2"], tags: ["new"], status: "available", sku: "VPHONE-PRO-6-256", desc: "Vphone Pro 6 - Màn hình 6.7\", camera 108MP, pin 5000mAh.", specs: { "Cấu hình & Bộ nhớ": { "Hệ điều hành": "Android 15", "Chip xử lý (CPU)": "Snapdragon 6 Gen 4 5G 8 nhân", "Tốc độ CPU": "2.3 GHz", "RAM": "8 GB", "Dung lượng lưu trữ": "256 GB" }, "Camera & Màn hình": { "Camera chính": "108MP", "Camera phụ": "12MP", "Màn hình": "6.7 inch OLED" }, "Pin & Sạc": { "Dung lượng pin": "5000mAh", "Sạc": "65W" }, "Tiện ích": { "Cổng": "USB-C, NFC" }, "Kết nối": { "Bluetooth": "5.3" } }, variants: { capacity: ["128GB", "256GB"], color: [{ label: 'Đen', value: '#111827' }, { label: 'Xanh', value: '#0ea5e9' }, { label: 'Đỏ', value: '#dd0000' }] } },
    { id: "v2", name: "Vphone X - 128GB", price: 13990000, images: ["https://via.placeholder.com/900x600?text=Vphone+X"], status: "available", sku: "VPHONE-X-128", desc: "Vphone X - Mỏng nhẹ.", specs: { "Cấu hình & Bộ nhớ": { "Hệ điều hành": "Android 14", "RAM": "8GB", "Dung lượng lưu trữ": "128GB" } }, variants: { capacity: ["64GB", "128GB"], color: [{ label: 'Tím', value: '#7c3aed' }, { label: 'Xám', value: '#6b7280' }] } }
  ];

  function getParam(name) { return new URLSearchParams(location.search).get(name); }
  const id = getParam('id') || getParam('product') || 'v1';

  function formatPrice(v) { if (v == null || v === '') return ''; try { return new Intl.NumberFormat('vi-VN').format(Number(v)) + '₫' } catch (e) { return String(v) + '₫' } }

  const productsPool = (window.__MOCK_PRODUCTS__ && window.__MOCK_PRODUCTS__.length) ? window.__MOCK_PRODUCTS__ : mockProducts;
  const p = productsPool.find(x => String(x.id) === String(id)) || mockProducts[0];

  // Render basics
  document.getElementById('bcTitle').textContent = p.name;
  document.getElementById('pTitle').textContent = p.name;
  document.getElementById('skuVal').textContent = p.sku || p.id;
  document.getElementById('pPrice').textContent = p.price ? formatPrice(p.price) : 'Liên hệ';
  document.getElementById('pOld').textContent = p.oldPrice ? formatPrice(p.oldPrice) : '';
  document.getElementById('pShort').textContent = p.desc || '';
  document.getElementById('stockVal').textContent = (p.status === 'available') ? 'Còn hàng' : (p.status === 'coming_soon' ? 'Sắp có hàng' : 'Hết hàng');
  document.getElementById('longDesc').textContent = p.desc || '';

  // Images
  const mainImg = document.querySelector('#mainImage img');
  const thumbs = document.getElementById('thumbs');
  (p.images || [p.image || 'https://via.placeholder.com/900x600?text=No+Image']).forEach((src, i) => {
    if (i === 0) mainImg.src = src;
    const btn = document.createElement('button');
    btn.innerHTML = '<img src="' + src + '" alt="' + (p.name || '') + '"/>';
    btn.addEventListener('click', () => { mainImg.src = src; });
    thumbs.appendChild(btn);
  });

  // Capacity variants
  const capWrap = document.getElementById('capacityWrap');
  const capacityList = (p.variants && p.variants.capacity) || [];
  let selectedCap = capacityList[0] || null;
  capacityList.forEach(c => {
    const b = document.createElement('button');
    b.className = 'variant-btn'; b.textContent = c;
    b.addEventListener('click', () => {
      document.querySelectorAll('#capacityWrap .variant-btn').forEach(x => x.classList.remove('active'));
      b.classList.add('active'); selectedCap = c;
    });
    if (c === selectedCap) b.classList.add('active');
    capWrap.appendChild(b);
  });

  // Color variants (swatches)
  const colorWrap = document.getElementById('colorWrap');
  const colorList = (p.variants && p.variants.color) || [];
  let selectedColor = colorList[0] ? colorList[0].label : null;
  colorList.forEach(c => {
    const btn = document.createElement('button');
    btn.className = 'variant-btn';
    btn.style.display = 'flex';
    btn.style.alignItems = 'center';
    btn.style.gap = '8px';
    const sw = document.createElement('span');
    sw.className = 'color-swatch';
    sw.style.background = c.value;
    sw.title = c.label;
    btn.appendChild(sw);
    const txt = document.createElement('span');
    txt.textContent = c.label; btn.appendChild(txt);
    btn.addEventListener('click', () => {
      document.querySelectorAll('#colorWrap .variant-btn').forEach(x => x.classList.remove('active'));
      btn.classList.add('active'); selectedColor = c.label;
    });
    if (c.label === selectedColor) btn.classList.add('active');
    colorWrap.appendChild(btn);
  });

  // Accordion for specs (closed by default)
  const accordion = document.getElementById('accordion'); const specs = p.specs || {};
  Object.keys(specs).forEach(sectionTitle => {
    const item = document.createElement('div'); item.className = 'acc-item';
    item.innerHTML = '<div class="acc-head"><h4>' + sectionTitle + '</h4><div class="chev"><i class="fa fa-chevron-down"></i></div></div><div class="acc-body"></div>';
    const body = item.querySelector('.acc-body');
    const rows = specs[sectionTitle];
    if (typeof rows === 'object') {
      const table = document.createElement('table'); const tbody = document.createElement('tbody');
      Object.keys(rows).forEach(k => { const tr = document.createElement('tr'); tr.innerHTML = '<td>' + k + '</td><td>' + rows[k] + '</td>'; tbody.appendChild(tr); });
      table.appendChild(tbody); body.appendChild(table);
    } else { body.textContent = rows; }
    accordion.appendChild(item);
    const head = item.querySelector('.acc-head'); head.addEventListener('click', () => {
      const open = body.classList.toggle('open');
      const chev = head.querySelector('.chev i');
      if (open) chev.style.transform = 'rotate(180deg)'; else chev.style.transform = 'rotate(0deg)';
    });
  });

  // Related products
  const relatedGrid = document.getElementById('relatedGrid');
  const related = productsPool.filter(x => x.id !== p.id).slice(0, 4);
  relatedGrid.innerHTML = related.map(r => {
    const price = r.price ? formatPrice(r.price) : '<span class="muted">Liên hệ</span>';
    return `
          <article class="product-card" role="article" aria-label="${r.name}">
            <div class="img-wrap"><img src="${(r.images && r.images[0]) || r.image || 'https://via.placeholder.com/420x260?text=No+Image'}" alt="${r.name}"/></div>
            <div class="product-info"><h4 class="product-title">${r.name}</h4><div class="price-row"><div class="price">${price}</div></div>
              <div style="display:flex; gap:8px; margin-top:8px"><button class="btn btn-primary" data-pid="${r.id}">Mua ngay</button> <a class="btn btn-secondary" href="product.html?id=${encodeURIComponent(r.id)}">Xem</a></div>
            </div>
          </article>
        `;
  }).join('');

  // Quantity (compact)
  let qty = 1; const qtyDisplay = document.getElementById('qtyDisplay'); qtyDisplay.textContent = qty;
  document.getElementById('qtyInc').addEventListener('click', () => { qty = Math.min(99, qty + 1); qtyDisplay.textContent = qty; });
  document.getElementById('qtyDec').addEventListener('click', () => { qty = Math.max(1, qty - 1); qtyDisplay.textContent = qty; });

  // Cart: localStorage simple
  function getCart() { try { return JSON.parse(localStorage.getItem('mh_cart') || '[]') } catch (e) { return [] } }
  function saveCart(c) { localStorage.setItem('mh_cart', JSON.stringify(c)); }

  document.getElementById('addCart').addEventListener('click', () => {
    const cart = getCart(); const existing = cart.find(i => i.id === p.id && i.capacity === selectedCap && i.color === selectedColor);
    if (existing) existing.qty += qty; else cart.push({ id: p.id, name: p.name, price: p.price, qty, capacity: selectedCap, color: selectedColor });
    saveCart(cart); alert('Đã thêm vào giỏ hàng');
  });

  document.getElementById('buyNow').addEventListener('click', () => {
    localStorage.setItem('mh_cart', JSON.stringify([{ id: p.id, name: p.name, price: p.price, qty, capacity: selectedCap, color: selectedColor }])); location.href = 'cart.html';
  });

  // Instalment: present clear CTA + simple calc (demo)
  document.getElementById('instalment').addEventListener('click', () => {
    const months = Number(prompt('Chọn số tháng trả góp (3,6,9,12):', '6')) || 6;
    const fee = 0.02; // demo interest
    const principal = p.price || 0;
    const monthly = Math.ceil((principal * (1 + fee)) / months);
    alert(`Trả góp ${months} tháng — khoảng ${new Intl.NumberFormat('vi-VN').format(monthly)}₫/tháng (demo).`);
  });

  // share & favorite
  document.getElementById('shareBtn').addEventListener('click', () => { if (navigator.share) { navigator.share({ title: p.name, text: p.desc, url: location.href }).catch(() => { }); } else { navigator.clipboard?.writeText(location.href).then(() => alert('Đã sao chép liên kết')); } });
  const favBtn = document.getElementById('favBtn');
  function isFav() { const f = JSON.parse(localStorage.getItem('mh_fav') || '[]'); return f.includes(p.id); }
  function renderFav() { favBtn.innerHTML = isFav() ? '<i class="fa fa-heart" style="color:#ff4444"></i>' : '<i class="fa-regular fa-heart"></i>'; }
  favBtn.addEventListener('click', () => { const f = JSON.parse(localStorage.getItem('mh_fav') || '[]'); if (isFav()) { const idx = f.indexOf(p.id); if (idx > -1) f.splice(idx, 1); } else f.push(p.id); localStorage.setItem('mh_fav', JSON.stringify(f)); renderFav(); }); renderFav();

  // Reviews (localStorage) + "Viết đánh giá" button near reviews
  const reviewsKey = 'mh_reviews_' + p.id;
  function loadReviews() { try { return JSON.parse(localStorage.getItem(reviewsKey) || '[]') } catch (e) { return [] } }
  function saveReviews(arr) { localStorage.setItem(reviewsKey, JSON.stringify(arr)); }
  function renderReviews() { const list = loadReviews(); const el = document.getElementById('reviewsList'); if (!list.length) { el.innerHTML = '<div class="muted">Chưa có đánh giá nào. Hãy là người đầu tiên!</div>'; } else { el.innerHTML = list.map(r => `<div style="background:#fff;padding:12px;border-radius:8px;box-shadow:0 6px 18px rgba(16,24,40,0.04)"><div style="display:flex;gap:8px;align-items:center"><strong>${r.name}</strong><div class="muted" style="font-size:13px"> - ${'★'.repeat(r.rating)}</div></div><div class="muted" style="margin-top:6px">${r.comment}</div></div>`).join(''); } }
  renderReviews();

  document.getElementById('openReview').addEventListener('click', () => {
    // quick modal-like prompt (demo) — can replace with a proper modal quickly
    const name = prompt('Tên của bạn:'); if (!name) return;
    const rating = Number(prompt('Đánh giá (1-5):', '5')) || 5;
    const comment = prompt('Nội dung đánh giá:') || '';
    const arr = loadReviews(); arr.unshift({ name, rating: Math.max(1, Math.min(5, rating)), comment }); saveReviews(arr); renderReviews(); alert('Cảm ơn đánh giá của bạn!');
  });

  // add basic JSON-LD
  const ld = { "@context": "https://schema.org/", "@type": "Product", "name": p.name, "image": (p.images || []).slice(0, 5), "description": p.desc || "", "sku": p.sku || p.id, "offers": { "@type": "Offer", "priceCurrency": "VND", "price": p.price || 0, "availability": p.status === 'available' ? 'https://schema.org/InStock' : 'https://schema.org/OutOfStock', "url": location.href } };
  const s = document.createElement('script'); s.type = 'application/ld+json'; s.textContent = JSON.stringify(ld); document.head.appendChild(s);
})();

