import React from "react";
import { Dropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import { FaUser } from "react-icons/fa";

function DashboardMenu() {
  return (
    <Dropdown align="end">
      <Dropdown.Toggle
        variant="outline-warning"
        id="dropdown-dashboard"
        className="d-flex align-items-center"
      >
        <FaUser className="me-2" /> My Dashboard
      </Dropdown.Toggle>

      <Dropdown.Menu>
        <Dropdown.Item as={Link} to="/users">
          Manage Users
        </Dropdown.Item>
        <Dropdown.Item as={Link} to="/products">
          Manage Products
        </Dropdown.Item>
      </Dropdown.Menu>
    </Dropdown>
  );
}

export default DashboardMenu;
