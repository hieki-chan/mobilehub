import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Header = ({
  title,
  onProfile,
  onLogout,
  notificationsCount = 0,
  userName = "User",
}) => {
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    function handleClickOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const openUserProfile = () => {
    setOpen(false);
    navigate("/settings"); // Navigate to user profile page
  };

  return (
    <header className="sticky top-0 z-[50] w-full bg-gray-800/50 backdrop-blur-lg shadow-lg border-b border-gray-700">
      <div className="px-4 py-3 sm:px-6 lg:px-8">
        <div className="flex items-center gap-4">
          {/* Left: title */}
          <div className="flex-shrink-0">
            <h1 className="text-2xl font-semibold text-gray-100">{title}</h1>
          </div>

          {/* Middle: search */}
          <div className="flex-1">
            <form
              className="max-w-lg mx-auto"
              onSubmit={(e) => e.preventDefault()}
            >
              <div className="relative text-gray-400">
                <input
                  type="text"
                  placeholder="Tìm kiếm..."
                  className="w-full pl-10 pr-4 py-2 rounded-lg bg-gray-700 bg-opacity-60 text-gray-100 placeholder-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <svg
                    className="w-5 h-5"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M21 21l-4.35-4.35M11 19a8 8 0 100-16 8 8 0 000 16z"
                    />
                  </svg>
                </div>
              </div>
            </form>
          </div>

          {/* Right: notifications and account */}
          <div className="flex items-center gap-3">
            <button
              type="button"
              className="relative p-2 rounded-md text-gray-200 hover:bg-gray-700 hover:bg-opacity-50 focus:outline-none"
              aria-label="Thông báo"
              onClick={() => console.log("Notifications clicked")}
            >
              <svg
                className="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                />
              </svg>

              {notificationsCount > 0 && (
                <span className="absolute -top-1 -right-1 z-50 inline-flex items-center justify-center px-1.5 py-0.5 text-xs font-semibold leading-none text-white bg-red-500 rounded-full">
                  {notificationsCount}
                </span>
              )}
            </button>

            <div className="relative overflow-visible" ref={dropdownRef}>
              <button
                type="button"
                className="flex items-center gap-2 px-2 py-1 rounded-md hover:bg-gray-700 hover:bg-opacity-50 focus:outline-none"
                onClick={() => setOpen((v) => !v)}
                aria-haspopup="true"
                aria-expanded={open}
              >
                <div className="w-8 h-8 rounded-full bg-indigo-600 flex items-center justify-center text-white font-medium">
                  {userName.charAt(0).toUpperCase()}
                </div>
                <span className="text-gray-100 hidden sm:inline">
                  {userName}
                </span>
                <svg
                  className="w-4 h-4 text-gray-300"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M19 9l-7 7-7-7"
                  />
                </svg>
              </button>

              {open && (
                <div className="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-gray-800 ring-1 ring-black ring-opacity-5 z-500">
                  <div className="py-1">
                    <button
                      onClick={() => {
                        setOpen(false);
                        openUserProfile();
                        (onProfile || (() => console.log("Thông tin")))();
                      }}
                      className="block px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 w-full text-left"
                    >
                      Thông tin
                    </button>

                    <button
                      onClick={() => {
                        // Đóng menu
                        setOpen(false);

                        // Xóa thông tin đăng nhập
                        localStorage.removeItem("isLoggedIn");
                        localStorage.removeItem("username");

                        // Chuyển hướng về trang login
                        navigate("/login");
                      }}
                      className="block px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 w-full text-left"
                    >
                      Đăng xuất
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
