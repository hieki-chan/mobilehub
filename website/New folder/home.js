// home.js 

(function () {
  // Config
  const API_BASE = window.__API_BASE__ || 'http://localhost:3000';
  const API_PRODUCTS = `${API_BASE}/api/products`;
  const USE_MOCK = (typeof window.__USE_MOCK__ !== 'undefined') ? window.__USE_MOCK__ : true;

  // Mock data (phone-focused)
  const mockProducts = window.__MOCK_PRODUCTS__ || [
    { id: "v1", name: "Vphone Pro 6 - 256GB", price: 19990000, oldPrice: 21990000, image: "https://via.placeholder.com/420x260?text=Vphone+Pro+6", tags: ["new"], status: "available", desc: "Vphone Pro 6 - Màn hình 6.7\", camera 108MP, pin 5000mAh." },
    { id: "v2", name: "Vphone X - 128GB", price: 13990000, image: "https://via.placeholder.com/420x260?text=Vphone+X", status: "available", desc: "Vphone X - Mỏng nhẹ, hiệu năng tốt, chụp ổn." },
    { id: "v3", name: "Galaxy S25 Ultra", price: 32990000, image: "https://via.placeholder.com/420x260?text=Galaxy+S25+Ultra", status: "available", desc: "Samsung Galaxy S25 Ultra - Flagship camera." },
    { id: "v4", name: "iPhone 17 Pro", price: 42990000, image: "https://via.placeholder.com/420x260?text=iPhone+17+Pro", status: "available", desc: "iPhone 17 Pro - iOS, chip mới, sạc nhanh." },
    { id: "v5", name: "Pixel 9", price: 24990000, image: "https://via.placeholder.com/420x260?text=Pixel+9", status: "available", desc: "Google Pixel 9 - Camera tối ưu phần mềm." },
    { id: "v6", name: "Vphone Lite - 64GB", price: 4990000, image: "https://via.placeholder.com/420x260?text=Vphone+Lite", status: "available", desc: "Vphone Lite - Giá mềm, pin ổn." },
    { id: "v7", name: "Gaming Phone Z", price: 18990000, oldPrice: 19990000, image: "https://via.placeholder.com/420x260?text=Gaming+Phone+Z", tags: ["sale"], status: "available", desc: "Phone tối ưu chơi game, tản nhiệt tốt." },
    { id: "v8", name: "Sắp có: Vphone Mini", price: null, image: "https://via.placeholder.com/420x260?text=Coming+Soon", status: "coming_soon", desc: "Vphone Mini - ra mắt sắp tới." }
  ];

  // State
  let productsCache = [];

  // Helpers
  function escapeHtml(str) {
    return String(str || '').replace(/[&<>"']/g, s =>
      ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[s])
    );
  }

  function formatPrice(v) {
    if (v === null || v === undefined || v === '') return '';
    try { return new Intl.NumberFormat('vi-VN').format(Number(v)) + '₫'; }
    catch (e) { return String(v) + '₫'; }
  }

  function normalizeProduct(apiData = {}) {
    return {
      id: apiData.id || apiData._id || apiData.sku || '',
      name: apiData.name || apiData.product_name || apiData.title || 'Sản phẩm',
      price: (apiData.price ?? apiData.cost ?? apiData.amount) || null,
      oldPrice: (apiData.oldPrice ?? apiData.original_price) || null,
      image: apiData.image || apiData.img_url || apiData.thumbnail || '',
      desc: apiData.desc || apiData.description || '',
      status: apiData.status || (apiData.available ? 'available' : 'available'),
      tags: Array.isArray(apiData.tags) ? apiData.tags : (apiData.badges || [])
    };
  }

  // Renderers
  function renderSkeleton(count = 8) {
    const arr = new Array(count).fill(0);
    return `<div class="skeleton-grid">${arr.map(() => `
      <div class="skeleton-card" aria-hidden="true">
        <div class="skel-rect skel-img"></div>
        <div class="skel-rect skel-line"></div>
        <div class="skel-rect skel-line short"></div>
        <div style="flex:1"></div>
        <div class="skel-rect skel-btn"></div>
      </div>
    `).join('')}</div>`;
  }

  function renderCard(p) {
    const name = escapeHtml(p.name);
    const imgSrc = p.image || 'https://via.placeholder.com/420x260?text=No+Image';
    const priceHtml = p.price ? `<div class="price">${formatPrice(p.price)}</div>` : (p.status === 'coming_soon' ? `<div class="muted">Sắp mở bán</div>` : '');
    const oldHtml = p.oldPrice ? `<div class="old">${formatPrice(p.oldPrice)}</div>` : '';
    const badges = (p.tags || []).map(t => `<span class="badge ${escapeHtml(t)}">${escapeHtml(t === 'sale' ? 'Giảm giá' : (t === 'new' ? 'Mới' : t))}</span>`).join('');
    const cta = p.status === 'coming_soon'
      ? `<button class="btn" disabled> Sắp mở bán </button>`
      : `<button class="btn btn-primary" data-product-id="${escapeHtml(p.id)}">Mua ngay</button> <button class="btn btn-secondary quick-view" data-product-id="${escapeHtml(p.id)}">Xem nhanh</button>`;

    return `
      <article class="product-card" role="article" aria-label="${name}">
        <div class="img-wrap">
          <img loading="lazy" src="${imgSrc}" alt="${name}"
               onerror="this.onerror=null;this.src='https://via.placeholder.com/420x260?text=No+Image';" />
          ${badges}
        </div>
        <div class="product-info">
          <h4 class="product-title">${name}</h4>
          <div class="price-row">
            ${priceHtml}
            ${oldHtml}
          </div>
          ${cta}
        </div>
      </article>
    `;
  }

  // Modal
  function ensureModal() {
    if (document.getElementById('quickViewModal')) return;
    const overlay = document.createElement('div');
    overlay.id = 'quickViewModal';
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
      <div class="modal" role="dialog" aria-modal="true" aria-labelledby="qvTitle">
        <button class="modal-close" aria-label="Đóng">&times;</button>
        <div class="modal-body">
          <div class="modal-img"><img src="" alt=""></div>
          <div class="modal-info">
            <h3 id="qvTitle"></h3>
            <div class="price" id="qvPrice"></div>
            <div class="muted" id="qvOld"></div>
            <div class="muted" id="qvDesc" style="margin-top:8px"></div>
            <div class="modal-actions">
              <button class="btn btn-primary" id="qvBuy">Mua ngay</button>
              <button class="btn btn-secondary" id="qvClose">Đóng</button>
            </div>
          </div>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    overlay.addEventListener('click', (e) => { if (e.target === overlay) closeModal(); });
    overlay.querySelector('.modal-close').addEventListener('click', closeModal);
    overlay.querySelector('#qvClose').addEventListener('click', closeModal);
    window.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeModal(); });
  }

  function openModal() {
    const overlay = document.getElementById('quickViewModal');
    if (!overlay) return;
    overlay.classList.add('open');
    document.body.style.overflow = 'hidden';
    const buy = overlay.querySelector('#qvBuy');
    if (buy) buy.focus();
  }

  function closeModal() {
    const overlay = document.getElementById('quickViewModal');
    if (!overlay) return;
    overlay.classList.remove('open');
    document.body.style.overflow = '';
  }

  function renderQuickView(productId) {
    const p = productsCache.find(x => String(x.id) === String(productId));
    if (!p) return alert('Không tìm thấy sản phẩm');
    ensureModal();
    const overlay = document.getElementById('quickViewModal');
    overlay.querySelector('.modal-img img').src = p.image || 'https://via.placeholder.com/420x260?text=No+Image';
    overlay.querySelector('.modal-img img').alt = p.name || 'Sản phẩm';
    overlay.querySelector('#qvTitle').textContent = p.name || '';
    overlay.querySelector('#qvPrice').textContent = p.price ? formatPrice(p.price) : '';
    overlay.querySelector('#qvOld').textContent = p.oldPrice ? formatPrice(p.oldPrice) : '';
    overlay.querySelector('#qvDesc').textContent = p.desc || '';
    const buyBtn = overlay.querySelector('#qvBuy');
    if (p.status === 'coming_soon') {
      buyBtn.disabled = true;
      buyBtn.textContent = 'Sắp mở bán';
    } else {
      buyBtn.disabled = false;
      buyBtn.textContent = 'Mua ngay';
      buyBtn.onclick = () => { window.location.href = `product.html?id=${encodeURIComponent(p.id)}`; };
    }
    openModal();
  }

  // Fetch
  async function fetchProductsFromApi() {
    const res = await fetch(API_PRODUCTS, { credentials: 'include' });
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const data = await res.json();
    if (Array.isArray(data)) return data;
    if (Array.isArray(data.items)) return data.items;
    if (Array.isArray(data.data)) return data.data;
    return [];
  }

  // Main
  async function loadProducts() {
    const grid = document.getElementById('productGrid');
    if (!grid) return;
    grid.innerHTML = renderSkeleton(8);

    try {
      let productsRaw;
      if (USE_MOCK) {
        await new Promise(r => setTimeout(r, 300));
        productsRaw = mockProducts;
      } else {
        productsRaw = await fetchProductsFromApi();
      }

      const products = (productsRaw || []).map(normalizeProduct);
      productsCache = products;

      if (!products.length) {
        grid.innerHTML = `<div class="loading" style="padding:18px; text-align:center">Không có sản phẩm nào.</div>`;
        return;
      }

      grid.innerHTML = products.map(p => renderCard(p)).join('');

      grid.querySelectorAll('.btn-primary').forEach(btn => {
        btn.addEventListener('click', (e) => {
          const id = e.currentTarget.getAttribute('data-product-id');
          window.location.href = `product.html?id=${encodeURIComponent(id)}`;
        });
      });
      grid.querySelectorAll('.quick-view').forEach(btn => {
        btn.addEventListener('click', (e) => {
          const id = e.currentTarget.getAttribute('data-product-id');
          renderQuickView(id);
        });
      });

    } catch (err) {
      console.error('Lỗi tải sản phẩm:', err);
      grid.innerHTML = `<div class="loading" role="alert" style="padding:18px; text-align:center">Không thể tải sản phẩm. Vui lòng thử lại.</div>`;
    }
  }

  // Init + exports
  document.addEventListener('DOMContentLoaded', loadProducts);
  window.__reloadProducts = loadProducts;
  window.__openQuickView = renderQuickView;
})();

/* Carousel */
document.addEventListener('DOMContentLoaded', () => {
    const track = document.getElementById('carouselTrack');
    const slides = Array.from(track?.children || []);
    const nextBtn = document.getElementById('nextBtn');
    const prevBtn = document.getElementById('prevBtn');
    const dotsContainer = document.getElementById('carouselDots');
    if (!track || slides.length === 0) return;

    // Dots
    slides.forEach((_, i) => {
        const dot = document.createElement('button');
        dot.setAttribute('aria-label', `Slide ${i + 1}`);
        dot.addEventListener('click', () => goTo(i));
        dotsContainer.appendChild(dot);
    });
    const dots = Array.from(dotsContainer.children);

    let current = 0;
    let timer;

    function goTo(i) {
        current = (i + slides.length) % slides.length;
        track.style.transform = `translateX(-${current * 100}%)`;
        updateDots();
    }

    function updateDots() {
        dots.forEach((d, i) => d.classList.toggle('active', i === current));
    }

    nextBtn?.addEventListener('click', () => {
        goTo(current + 1);
        resetTimer();
    });

    prevBtn?.addEventListener('click', () => {
        goTo(current - 1);
        resetTimer();
    });

    function startAuto() {
        timer = setInterval(() => goTo(current + 1), 4000);
    }

    function resetTimer() {
        clearInterval(timer);
        startAuto();
    }

    goTo(0);
    startAuto();
});
