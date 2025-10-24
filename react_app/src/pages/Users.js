import React, { useState } from "react";
import { Table, Button, Form } from "react-bootstrap";

function Users() {
  const [users, setUsers] = useState([
    { id: 1, name: "John Doe", email: "john@example.com" },
    { id: 2, name: "Jane Smith", email: "jane@example.com" },
  ]);
  const [newUser, setNewUser] = useState({ name: "", email: "" });

  const addUser = () => {
    if (newUser.name && newUser.email) {
      setUsers([...users, { id: Date.now(), ...newUser }]);
      setNewUser({ name: "", email: "" });
    }
  };

  const deleteUser = (id) => setUsers(users.filter((u) => u.id !== id));

  return (
    <div className="container py-4">
      <h4 className="mb-3 text-warning">User Management</h4>

      <Form className="mb-4">
        <Form.Control
          type="text"
          placeholder="Name"
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
        <Button variant="warning" className="text-white" onClick={addUser}>
          Add User
        </Button>
      </Form>

      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.name}</td>
              <td>{u.email}</td>
              <td>
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => deleteUser(u.id)}
                >
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}

export default Users;
