import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import ThanhTieuDe from "./components/ThanhTieuDe";
import NguoiDung from "./pages/NguoiDung";
import SanPham from "./pages/SanPham";

function UngDung() {
  return (
    <Router>
      <ThanhTieuDe />
      <Routes>
        <Route
          path="/"
          element={
            <div className="text-center p-5">
              Chào mừng đến trang quản lý Electro
            </div>
          }
        />
        <Route path="/nguoi-dung" element={<NguoiDung />} />
        <Route path="/san-pham" element={<SanPham />} />
      </Routes>
    </Router>
  );
}

export default UngDung;
