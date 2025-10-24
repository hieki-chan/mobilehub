import React from "react";
import { Navbar, Container, Form, Button } from "react-bootstrap";
import { FaShoppingBag, FaSearch } from "react-icons/fa";
import { Link } from "react-router-dom";
import DashboardMenu from "./DashboardMenu";

function Header() {
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
          Electro
        </Navbar.Brand>

        {/* Search Bar */}
        <Form className="d-flex mx-auto" style={{ width: "50%" }}>
          <Form.Control
            type="search"
            placeholder="Search Looking For?"
            className="me-2"
          />
          <Button variant="warning" className="text-white">
            <FaSearch />
          </Button>
        </Form>

        {/* Dashboard Menu (dropdown riêng) */}
        <DashboardMenu />
      </Container>
    </Navbar>
  );
}

export default Header;
