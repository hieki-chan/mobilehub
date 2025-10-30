import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const ForgotPasswordPage = () => {
  const [phone, setPhone] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleSendCode = (e) => {
    e.preventDefault();
    if (!/^[0-9]{10,11}$/.test(phone)) {
      setMessage("Số điện thoại không hợp lệ!");
      return;
    }
    // Giả lập gửi mã
    setMessage(`Mã xác minh đã được gửi tới số ${phone}.`);
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-900 text-gray-100">
      <div className="bg-gray-800 p-8 rounded-2xl shadow-lg w-full max-w-sm">
        <h2 className="text-2xl font-bold text-center mb-6">Quên mật khẩu</h2>

        {message && (
          <p className="text-green-400 text-sm text-center mb-3">{message}</p>
        )}

        <form onSubmit={handleSendCode} className="space-y-4">
          <div>
            <label className="block mb-1 text-sm text-gray-300">
              Số điện thoại
            </label>
            <input
              type="tel"
              className="w-full px-4 py-2 rounded-lg bg-gray-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white py-2 rounded-lg transition duration-200"
          >
            Gửi mã xác nhận
          </button>
        </form>

        <div className="mt-4 text-center text-sm">
          <button
            onClick={() => navigate("/login")}
            className="text-gray-400 hover:underline"
          >
            Quay lại đăng nhập
          </button>
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
