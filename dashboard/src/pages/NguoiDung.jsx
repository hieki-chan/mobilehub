import React, { useState } from "react";
import { Table, Button, Form } from "react-bootstrap";

function NguoiDung() {
  const [users, setUsers] = useState([
    { id: 1, name: "Nguyễn Văn A", email: "a@gmail.com" },
    { id: 2, name: "Trần Thị B", email: "b@gmail.com" },
  ]);

  const [newUser, setNewUser] = useState({ name: "", email: "" });

  const themNguoiDung = () => {
    if (newUser.name && newUser.email) {
      setUsers([...users, { id: Date.now(), ...newUser }]);
      setNewUser({ name: "", email: "" });
    } else {
      alert("Vui lòng nhập đầy đủ thông tin!");
    }
  };

  const xoaNguoiDung = (id) => {
    if (window.confirm("Bạn có chắc muốn xóa người dùng này không?")) {
      setUsers(users.filter((u) => u.id !== id));
    }
  };

  return (
    <div className="container py-4">
      <h4 className="mb-3 text-warning">👤 Quản lý người dùng</h4>

      <Form className="mb-4">
        <Form.Control
          type="text"
          placeholder="Tên người dùng"
          className="mb-2"
          value={newUser.name}
          onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
        />
        <Form.Control
          type="email"
          placeholder="Email"
          className="mb-2"
          value={newUser.email}
          onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
        />
        <Button
          variant="warning"
          className="text-white"
          onClick={themNguoiDung}
        >
          ➕ Thêm người dùng
        </Button>
      </Form>

      <Table striped bordered hover>
        <thead>
          <tr className="text-center">
            <th>Tên</th>
            <th>Email</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.name}</td>
              <td>{u.email}</td>
              <td className="text-center">
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => xoaNguoiDung(u.id)}
                >
                  🗑 Xóa
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}

export default NguoiDung;
