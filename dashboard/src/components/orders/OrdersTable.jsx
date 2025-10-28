import React, { useState } from "react";
import { motion } from "framer-motion";
import { ChevronLeft, ChevronRight, Eye, Search, X } from "lucide-react";

const DuLieu_DonHang = [
  {
    id: "DH001",
    khachhang: "Mudassar",
    tongtien: 235.4,
    trangthai: "Đã giao",
    ngay: "2023-07-01",
  },
  {
    id: "DH002",
    khachhang: "Danish",
    tongtien: 412.0,
    trangthai: "Đang xử lý",
    ngay: "2023-07-02",
  },
  {
    id: "DH003",
    khachhang: "Ayesha",
    tongtien: 162.5,
    trangthai: "Đang giao",
    ngay: "2023-07-03",
  },
  {
    id: "DH004",
    khachhang: "Hassan",
    tongtien: 750.2,
    trangthai: "Chờ xử lý",
    ngay: "2023-07-04",
  },
  {
    id: "DH005",
    khachhang: "Sarah",
    tongtien: 95.8,
    trangthai: "Đã giao",
    ngay: "2023-07-05",
  },
  {
    id: "DH006",
    khachhang: "Zainab",
    tongtien: 310.75,
    trangthai: "Đang xử lý",
    ngay: "2023-07-06",
  },
  {
    id: "DH007",
    khachhang: "Rizwan",
    tongtien: 528.9,
    trangthai: "Đang giao",
    ngay: "2023-07-07",
  },
  {
    id: "DH008",
    khachhang: "Kiran",
    tongtien: 189.6,
    trangthai: "Đã giao",
    ngay: "2023-07-08",
  },
  {
    id: "DH009",
    khachhang: "Ali",
    tongtien: 675.0,
    trangthai: "Chờ xử lý",
    ngay: "2023-07-09",
  },
  {
    id: "DH010",
    khachhang: "Sara",
    tongtien: 225.4,
    trangthai: "Đã giao",
    ngay: "2023-07-10",
  },
  {
    id: "DH011",
    khachhang: "Kamran",
    tongtien: 330.6,
    trangthai: "Đang xử lý",
    ngay: "2023-07-11",
  },
  {
    id: "DH012",
    khachhang: "Farah",
    tongtien: 480.0,
    trangthai: "Đang giao",
    ngay: "2023-07-12",
  },
  {
    id: "DH013",
    khachhang: "Usman",
    tongtien: 560.2,
    trangthai: "Đã giao",
    ngay: "2023-07-13",
  },
  {
    id: "DH014",
    khachhang: "Asma",
    tongtien: 310.5,
    trangthai: "Chờ xử lý",
    ngay: "2023-07-14",
  },
  {
    id: "DH015",
    khachhang: "Bilal",
    tongtien: 745.8,
    trangthai: "Đang xử lý",
    ngay: "2023-07-15",
  },
  {
    id: "DH016",
    khachhang: "Imran",
    tongtien: 420.0,
    trangthai: "Đang giao",
    ngay: "2023-07-16",
  },
  {
    id: "DH017",
    khachhang: "Nida",
    tongtien: 250.7,
    trangthai: "Đã giao",
    ngay: "2023-07-17",
  },
  {
    id: "DH018",
    khachhang: "Hamza",
    tongtien: 555.3,
    trangthai: "Chờ xử lý",
    ngay: "2023-07-18",
  },
];

const BangDonHang = () => {
  const [tuKhoa, setTuKhoa] = useState("");
  const [donHangLoc, setDonHangLoc] = useState(DuLieu_DonHang);
  const [trangHienTai, setTrangHienTai] = useState(1);
  const [hienModal, setHienModal] = useState(false);
  const [donHangChon, setDonHangChon] = useState(null);
  const [trangThaiMoi, setTrangThaiMoi] = useState("");
  const soDongMoiTrang = 6;

  const tongSoTrang = Math.ceil(donHangLoc.length / soDongMoiTrang);

  const XuLyTimKiem = (e) => {
    const term = e.target.value.toLowerCase();
    setTuKhoa(term);
    const loc = DuLieu_DonHang.filter((dh) =>
      dh.khachhang.toLowerCase().includes(term)
    );
    setDonHangLoc(loc);
    setTrangHienTai(1);
  };

  const doiTrang = (soTrang) => setTrangHienTai(soTrang);
  const layDonHangTrangHienTai = () => {
    const start = (trangHienTai - 1) * soDongMoiTrang;
    return donHangLoc.slice(start, start + soDongMoiTrang);
  };

  const moModal = (donhang) => {
    setDonHangChon(donhang);
    setTrangThaiMoi(donhang.trangthai);
    setHienModal(true);
  };

  const luuTrangThai = () => {
    setDonHangLoc((prev) =>
      prev.map((dh) =>
        dh.id === donHangChon.id ? { ...dh, trangthai: trangThaiMoi } : dh
      )
    );
    setHienModal(false);
  };

  return (
    <motion.div
      className="bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 border border-gray-700 mb-6 relative z-10"
      initial={{ opacity: 0, y: 25 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3, delay: 0.3 }}
    >
      {/* Tiêu đề & tìm kiếm */}
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold text-gray-100">
          Danh sách đơn hàng
        </h2>

        <div className="relative flex items-center">
          <Search
            className="absolute left-3 text-gray-400 sm:left-2.5 top-2.5"
            size={20}
          />
          <input
            type="text"
            placeholder="Tìm kiếm khách hàng..."
            className="bg-gray-700 text-white placeholder-gray-400 rounded-lg pl-10 pr-4 py-2 w-full sm:w-auto focus:outline-none focus:ring-2 focus:ring-blue-500"
            onChange={XuLyTimKiem}
            value={tuKhoa}
          />
        </div>
      </div>

      {/* BẢNG */}
      <div className="overflow-x-auto" style={{ minHeight: "400px" }}>
        <table className="min-w-full divide-y divide-gray-400">
          <thead>
            <tr>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Mã đơn
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Khách hàng
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Tổng tiền (₫)
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Trạng thái
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Ngày
              </th>
              <th className="px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider">
                Thao tác
              </th>
            </tr>
          </thead>

          <tbody className="divide-y divide-gray-500">
            {layDonHangTrangHienTai().map((dh) => (
              <motion.tr
                key={dh.id}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 1.1, delay: 0.2 }}
              >
                <td className="px-6 py-4 whitespace-nowrap text-gray-100">
                  {dh.id}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-gray-300">
                  {dh.khachhang}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-gray-300">
                  {dh.tongtien.toFixed(2)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span
                    className={`px-3 inline-flex rounded-full text-xs leading-5 font-semibold ${
                      dh.trangthai === "Đã giao"
                        ? "bg-green-700 text-green-100"
                        : dh.trangthai === "Đang giao"
                        ? "bg-blue-700 text-blue-100"
                        : dh.trangthai === "Đang xử lý"
                        ? "bg-yellow-700 text-yellow-100"
                        : "bg-red-700 text-red-100"
                    }`}
                  >
                    {dh.trangthai}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-gray-300">
                  {dh.ngay}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <button onClick={() => moModal(dh)}>
                    <Eye className="text-blue-500 cursor-pointer" size={20} />
                  </button>
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Modal cập nhật trạng thái */}
      {hienModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <motion.div
            className="bg-gray-800 rounded-lg shadow-lg p-6 w-full max-w-xl"
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ duration: 0.3 }}
          >
            <h1 className="text-2xl font-semibold text-gray-100 mb-5">
              Cập nhật trạng thái đơn hàng
            </h1>
            <label className="text-sm text-gray-300">Tên khách hàng</label>
            <h2 className="text-lg mb-4 px-4 py-2 bg-gray-700 text-white rounded-md">
              {donHangChon.khachhang}
            </h2>

            <div className="flex flex-col space-y-1 mb-4">
              <label className="text-sm text-gray-300">
                Trạng thái hiện tại
              </label>
              <select
                className="px-4 py-2 bg-gray-700 text-white rounded-md"
                value={trangThaiMoi}
                onChange={(e) => setTrangThaiMoi(e.target.value)}
              >
                <option value="Chờ xử lý">Chờ xử lý</option>
                <option value="Đang xử lý">Đang xử lý</option>
                <option value="Đang giao">Đang giao</option>
                <option value="Đã giao">Đã giao</option>
              </select>
            </div>

            <div className="flex justify-end mt-5 space-x-2">
              <button
                onClick={() => setHienModal(false)}
                className="bg-gray-600 hover:bg-red-500 text-gray-100 px-4 py-2 rounded-md"
              >
                <X size={22} />
              </button>
              <button
                onClick={luuTrangThai}
                className="bg-blue-600 hover:bg-blue-800 text-white text-md px-3 py-3 rounded-md w-32"
              >
                Lưu thay đổi
              </button>
            </div>
          </motion.div>
        </div>
      )}

      {/* Phân trang */}
      <div className="flex flex-col md:flex-row justify-between mt-4 items-center">
        <div className="flex items-center">
          <button
            onClick={() => doiTrang(trangHienTai - 1)}
            disabled={trangHienTai === 1}
            className={`text-sm px-3 py-1 border rounded-md ${
              trangHienTai === 1
                ? "text-gray-400 border-gray-600"
                : "text-gray-100 border-gray-300 hover:bg-gray-300 hover:text-gray-800"
            }`}
          >
            <ChevronLeft size={18} />
          </button>
          <span className="mx-2 text-sm text-gray-100">
            Trang {trangHienTai} / {tongSoTrang}
          </span>
          <button
            onClick={() => doiTrang(trangHienTai + 1)}
            disabled={trangHienTai === tongSoTrang}
            className={`text-sm px-3 py-1 border rounded-md ${
              trangHienTai === tongSoTrang
                ? "text-gray-400 border-gray-600"
                : "text-gray-100 border-gray-300 hover:bg-gray-300 hover:text-gray-800"
            }`}
          >
            <ChevronRight size={18} />
          </button>
        </div>
        <div className="text-sm text-gray-300 mt-4 md:mt-0">
          Tổng đơn hàng: {donHangLoc.length}
        </div>
      </div>
    </motion.div>
  );
};

export default BangDonHang;
