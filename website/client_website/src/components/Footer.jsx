import React from "react";
import { FaFacebook, FaInstagram, FaTwitter, FaGithub } from "react-icons/fa";
import "../styles/components/footer.css";

export default function Footer() {
  return (
    <footer className="footer light">
      <div className="footer-container">
        {/* Brand */}
        <div className="footer-brand">
          <span className="footer-logo">MobileHub</span>
          <p className="footer-slogan">
            Kết nối công nghệ, nâng tầm trải nghiệm.
          </p>
        </div>

        {/* Links */}
        <div className="footer-links">
          <a href="#">Về chúng tôi</a>
          <a href="#">Sản phẩm</a>
          <a href="#">Chính sách</a>
          <a href="#">Liên hệ</a>
        </div>

        {/* Social */}
        <div className="footer-social">
          <a href="#"><FaFacebook /></a>
          <a href="#"><FaInstagram /></a>
          <a href="#"><FaTwitter /></a>
          <a href="#"><FaGithub /></a>
        </div>
      </div>

      <div className="footer-bottom">
        © {new Date().getFullYear()} MobileHub — Demo Project
      </div>
    </footer>
  );
}
