import React from "react";
import { Navbar, Container, Form, Button } from "react-bootstrap";
import { FaShoppingBag, FaSearch } from "react-icons/fa";
import { Link } from "react-router-dom";
import MenuBangDieuKhien from "./MenuBangDieuKhien";

function ThanhTieuDe() {
  return (
    <Navbar expand="lg" className="bg-light border-bottom shadow-sm">
      <Container>
        {/* Logo */}
        <Navbar.Brand
          as={Link}
          to="/"
          className="text-warning fw-bold fs-3 d-flex align-items-center"
        >
          <FaShoppingBag className="me-2" />
          MOBILEHUB
        </Navbar.Brand>

        {/* Thanh tìm kiếm */}
        <Form className="d-flex mx-auto" style={{ width: "50%" }}>
          <Form.Control
            type="search"
            placeholder="Tìm sản phẩm bạn cần..."
            className="me-2"
          />
          <Button variant="warning" className="text-white">
            <FaSearch />
          </Button>
        </Form>

        {/* Menu bảng điều khiển */}
        <MenuBangDieuKhien />
      </Container>
    </Navbar>
  );
}

export default ThanhTieuDe;
