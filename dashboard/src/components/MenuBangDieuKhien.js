import React from "react";
import { Dropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import { FaUser } from "react-icons/fa";

function MenuBangDieuKhien() {
  return (
    <Dropdown align="end">
      <Dropdown.Toggle
        variant="outline-warning"
        id="dropdown-dashboard"
        className="d-flex align-items-center"
      >
        <FaUser className="me-2" /> Bảng điều khiển
      </Dropdown.Toggle>

      <Dropdown.Menu>
        <Dropdown.Item as={Link} to="/nguoi-dung">
          Quản lý người dùng
        </Dropdown.Item>
        <Dropdown.Item as={Link} to="/san-pham">
          Quản lý sản phẩm
        </Dropdown.Item>
      </Dropdown.Menu>
    </Dropdown>
  );
}

export default MenuBangDieuKhien;
//12312312
