import React, { useState } from "react";
import { Table, Button, Form } from "react-bootstrap";

function SanPham() {
  const [products, setProducts] = useState([
    {
      id: 1,
      name: "Điện thoại iPhone 15",
      description: "Điện thoại cao cấp của Apple",
      price: 29990000,
      status: 1,
      product_discount_id: 101,
      product_spec_id: 201,
    },
  ]);

  const [newProduct, setNewProduct] = useState({
    name: "",
    description: "",
    price: "",
    status: 1,
    product_discount_id: "",
    product_spec_id: "",
  });

  const themSanPham = () => {
    if (!newProduct.name || !newProduct.price) {
      alert("Vui lòng nhập tên và giá sản phẩm!");
      return;
    }
    const newItem = {
      id: Date.now(),
      ...newProduct,
      price: parseFloat(newProduct.price),
    };
    setProducts([...products, newItem]);
    setNewProduct({
      name: "",
      description: "",
      price: "",
      status: 1,
      product_discount_id: "",
      product_spec_id: "",
    });
  };

  const xoaSanPham = (id) => {
    if (window.confirm("Bạn có chắc muốn xóa sản phẩm này không?")) {
      setProducts(products.filter((p) => p.id !== id));
    }
  };

  const hienTrangThai = (status) => (status === 1 ? "Đang bán" : "Ngừng bán");

  return (
    <div className="container py-4">
      <h3 className="text-center text-warning mb-4">📦 Quản lý sản phẩm</h3>

      <div className="border rounded p-3 mb-4 bg-light">
        <h5 className="mb-3">Thêm sản phẩm mới</h5>
        <Form>
          <div className="row g-2">
            <div className="col-md-4">
              <Form.Control
                type="text"
                placeholder="Tên sản phẩm"
                value={newProduct.name}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, name: e.target.value })
                }
              />
            </div>
            <div className="col-md-4">
              <Form.Control
                type="text"
                placeholder="Mô tả"
                value={newProduct.description}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, description: e.target.value })
                }
              />
            </div>
            <div className="col-md-2">
              <Form.Control
                type="number"
                placeholder="Giá (VNĐ)"
                value={newProduct.price}
                onChange={(e) =>
                  setNewProduct({ ...newProduct, price: e.target.value })
                }
              />
            </div>
            <div className="col-md-2">
              <Button
                variant="warning"
                className="text-white w-100"
                onClick={themSanPham}
              >
                ➕ Thêm
              </Button>
            </div>
          </div>
        </Form>
      </div>

      <Table bordered hover responsive>
        <thead className="table-warning text-center">
          <tr>
            <th>Mã</th>
            <th>Tên sản phẩm</th>
            <th>Mô tả</th>
            <th>Giá (VNĐ)</th>
            <th>Trạng thái</th>
            <th>Mã giảm giá</th>
            <th>Mã thông số</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.description}</td>
              <td>{p.price.toLocaleString()}</td>
              <td>{hienTrangThai(p.status)}</td>
              <td>{p.product_discount_id}</td>
              <td>{p.product_spec_id}</td>
              <td className="text-center">
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => xoaSanPham(p.id)}
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

export default SanPham;
