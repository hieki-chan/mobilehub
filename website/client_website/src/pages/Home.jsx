import React, { useEffect, useRef, useState } from "react";
import ProductCard from "../components/home/ProductCard";
import { useNavigate } from "react-router-dom";
import QuickViewModal from "../components/home/QuickViewModal";
import { formatPrice } from "../utils/formatPrice";
import { fetchProducts } from "../api/ProductApi";
import "../styles/pages/home.css";

export default function Home() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [last, setLast] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalProduct, setModalProduct] = useState(null);

  const trackRef = useRef(null);
  const didFetch = useRef(false);

  const itemsPerPage = 10;
  const [current, setCurrent] = useState(0);
  const slidesCount = 3;
  const navigate = useNavigate();

  useEffect(() => {
    if (didFetch.current) return;
    didFetch.current = true;
    loadProducts();
  }, []);;

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrent((c) => (c + 1) % slidesCount);
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    const track = trackRef.current;
    if (track) track.style.transform = `translateX(-${current * 100}%)`;
  }, [current]);

  const loadProducts = async () => {
    if (loading || last) return;
    try {
      setLoading(true);
      const data = await fetchProducts(page, itemsPerPage);
      setProducts((prev) => [...prev, ...data.content]);
      setLast(data.last);
      setPage((prev) => prev + 1);
    } catch (err) {
      console.error("Lỗi khi lấy sản phẩm:", err);
    } finally {
      setLoading(false);
    }
  };

  const openQuickView = (id) => {
    const p = products.find((x) => String(x.id) === String(id));
    if (!p) return alert("Không tìm thấy sản phẩm");
    setModalProduct(p);
    console.log(p);
    setModalOpen(true);
    document.body.style.overflow = "hidden";
  };

  const closeQuickView = () => {
    setModalOpen(false);
    setModalProduct(null);
    document.body.style.overflow = "";
  };

  const navigateToSearch = (term) => {
    navigate(`/search?q=${encodeURIComponent(term)}`);
  };

  const popularTerms = [
    "Vphone Pro 6",
    "Vphone X",
    "iPhone 17",
    "Samsung S25",
    "Pixel 9",
    "Điện thoại chơi game",
    "Camera phone",
    "Pin dự phòng",
    "Ốp lưng",
    "Sạc 65W",
  ];

  return (
    <div>
      <section className="hero" aria-label="Banner chính">
        <div className="carousel" id="carousel" aria-roledescription="carousel">
          <div className="carousel-track" ref={trackRef}>
            <div
              className="slide"
              style={{
                backgroundImage: "url('http://localhost:5173/carousel1.jpg')",
              }}
            >
              <div className="slide-content">
                <h2>Khuyến mãi điện thoại — Giá tốt mỗi ngày</h2>
                <p className="muted">
                  Chọn model yêu thích, giao nhanh toàn quốc.
                </p>
              </div>
            </div>
            <div
              className="slide"
              style={{
                backgroundImage: "url('http://localhost:5173/carousel2.jpg')",
              }}
            >
              <div className="slide-content">
                <h2>Iphone 17 series</h2>
                <p className="muted">
                  Hiệu năng mạnh — Camera chuyên nghiệp.
                </p>
              </div>
            </div>
            <div
              className="slide"
              style={{
                backgroundImage: "url('http://localhost:5173/carousel3.jpg')",
              }}
              
            >
              <div className="slide-content">
                <h2>Flash Sale — Số lượng có hạn</h2>
                <p className="muted">Đừng bỏ lỡ giá rẻ trong ngày.</p>
              </div>
            </div>
          </div>

          <button
            className="carousel-arrow prev"
            onClick={() =>
              setCurrent((c) => (c - 1 + slidesCount) % slidesCount)
            }
            aria-label="Slide trước"
          >
            <i className="fa fa-chevron-left"></i>
          </button>
          <button
            className="carousel-arrow next"
            onClick={() => setCurrent((c) => (c + 1) % slidesCount)}
            aria-label="Slide sau"
          >
            <i className="fa fa-chevron-right"></i>
          </button>

          <div className="carousel-dots" aria-label="Chọn slide">
            {[0, 1, 2].map((i) => (
              <button
                key={i}
                aria-label={`Slide ${i + 1}`}
                className={i === current ? "active" : ""}
                onClick={() => setCurrent(i)}
              ></button>
            ))}
          </div>
        </div>
      </section>

      <main className="container main-content" id="mainContent">
        <h3 className="section-title">Sản phẩm</h3>
        <section
          className="products-grid"
          id="productGrid"
          aria-label="Danh sách sản phẩm"
        >
          {loading ? (
            Array.from({ length: itemsPerPage }).map((_, i) => (
              <div className="skeleton-card" key={i} aria-hidden="true">
                <div className="skel-rect skel-img"></div>
                <div className="skel-rect skel-line"></div>
                <div className="skel-rect skel-line short"></div>
                <div style={{ flex: 1 }}></div>
                <div className="skel-rect skel-btn"></div>
              </div>
            ))
          ) : !products.length ? (
            <div
              className="loading"
              style={{ padding: 18, textAlign: "center" }}
            >
              Không có sản phẩm nào.
            </div>
          ) : (
            products.map((p, index) => (
              <ProductCard
                key={p.id}
                p={{
                  id: p.id,
                  name: p.name,
                  price: p.defaultVariant.price,
                  imageUrl: p.defaultVariant.imageUrl,
                  discountInPercent: p.discountInPercent,
                }}
                onQuickView={openQuickView}
              />
            ))
          )}
        </section>

        {!last && !loading && (
          <div style={{ textAlign: "center", marginTop: "16px" }}>
            <button className="btn btn-outline" onClick={loadProducts}>
              Xem thêm
            </button>
          </div>
        )}

        <section
          className="popular-searches container"
          aria-label="Mọi người cũng tìm kiếm"
        >
          <h4 className="popular-title">Mọi người cũng tìm kiếm</h4>
          <div className="tags-wrap">
            {popularTerms.map((term, index) => (
              <a
                key={index}
                onClick={() => navigateToSearch(term)}
                className="tag"
                href={`/search?q=${encodeURIComponent(term)}`}
              >
                {term}
              </a>
            ))}
          </div>
        </section>
      </main>

      {modalOpen && (
        <QuickViewModal
          product={modalProduct}
          onClose={closeQuickView}
        />
      )}
    </div>
  );
}
