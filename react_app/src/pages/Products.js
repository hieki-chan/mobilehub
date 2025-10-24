import React, { useState } from "react";
import { Table, Button, Form } from "react-bootstrap";

function Products() {
  const [products, setProducts] = useState([
    { id: 1, name: "iPhone 15", price: 1200 },
    { id: 2, name: "MacBook Air", price: 1600 },
  ]);
  const [newProduct, setNewProduct] = useState({ name: "", price: "" });

  const addProduct = () => {
    if (newProduct.name && newProduct.price) {
      setProducts([...products, { id: Date.now(), ...newProduct }]);
      setNewProduct({ name: "", price: "" });
    }
  };

  const deleteProduct = (id) =>
    setProducts(products.filter((p) => p.id !== id));

  return (
    <div className="container py-4">
      <h4 className="mb-3 text-warning">Product Management</h4>

      <Form className="mb-4">
        <Form.Control
          type="text"
          placeholder="Product Name"
          className="mb-2"
          value={newProduct.name}
          onChange={(e) =>
            setNewProduct({ ...newProduct, name: e.target.value })
          }
        />
        <Form.Control
          type="number"
          placeholder="Price"
          className="mb-2"
          value={newProduct.price}
          onChange={(e) =>
            setNewProduct({ ...newProduct, price: e.target.value })
          }
        />
        <Button variant="warning" className="text-white" onClick={addProduct}>
          Add Product
        </Button>
      </Form>

      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Product</th>
            <th>Price ($)</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td>{p.name}</td>
              <td>{p.price}</td>
              <td>
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => deleteProduct(p.id)}
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

export default Products;
