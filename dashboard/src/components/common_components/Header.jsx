import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { logout } from "../../api/AuthApi";
import { ChevronRight, Bell } from "lucide-react";

const normalizePath = (path) =>
  (path || []).map((seg) =>
    typeof seg === "string" ? { label: seg, to: undefined } : seg
  );

const Header = ({
  path = [
    { label: "Mobilehub", to: "/" },
    { label: "Người dùng", to: "/users" },
  ],
  notificationsCount = 0,
  userName = "Admin",
}) => {
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  const segments = normalizePath(path);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target))
        setOpen(false);
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const openUserProfile = () => {
    setOpen(false);
    navigate("/settings");
  };

  const handleBreadcrumbClick = (seg, isLast) => {
    if (isLast) return;
    if (seg?.to) navigate(seg.to);
  };

  return (
    <header className="sticky top-0 z-50 w-full bg-white/70 backdrop-blur-md border-b border-gray-200">
      <div className="px-4 py-3 sm:px-6 lg:px-8 flex items-center justify-between">
        {/* Breadcrumb */}
        <nav
          className="flex items-center text-sm font-medium text-gray-600 space-x-1"
          aria-label="Breadcrumb"
        >
          {segments.map((seg, index) => {
            const isLast = index === segments.length - 1;
            return (
              <div key={index} className="flex items-center">
                <span
                  onClick={() => handleBreadcrumbClick(seg, isLast)}
                  className={`hover:text-blue-600 cursor-pointer ${
                    isLast ? "text-gray-900 font-semibold cursor-default" : ""
                  }`}
                >
                  {seg.label}
                </span>
                {index < segments.length - 1 && (
                  <ChevronRight size={16} className="mx-1 text-gray-400" />
                )}
              </div>
            );
          })}
        </nav>

        {/* Notifications & User */}
        <div className="flex items-center gap-3">
          <button
            type="button"
            className="relative p-2 rounded-md text-gray-600 hover:bg-gray-100 focus:outline-none"
            aria-label="Thông báo"
            onClick={() => console.log("Notifications clicked")}
          >
            <Bell size={20} />
            {notificationsCount > 0 && (
              <span className="absolute -top-1 -right-1 inline-flex items-center justify-center px-1.5 py-0.5 text-xs font-semibold text-white bg-red-500 rounded-full">
                {notificationsCount}
              </span>
            )}
          </button>

          <div className="relative" ref={dropdownRef}>
            <button
              type="button"
              onClick={() => setOpen((v) => !v)}
              className="flex items-center gap-2 px-2 py-1 rounded-md hover:bg-gray-100 focus:outline-none"
            >
              <div className="w-8 h-8 rounded-full bg-orange-500 flex items-center justify-center text-white font-medium">
                {userName.charAt(0).toUpperCase()}
              </div>
              <span className="text-gray-800 hidden sm:inline">{userName}</span>
              <svg
                className="w-4 h-4 text-gray-500"
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
              <div className="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white border border-gray-200 z-50">
                <div className="py-1">
                  <button
                    onClick={() => {
                      setOpen(false);
                      openUserProfile();
                    }}
                    className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full text-left"
                  >
                    Thông tin
                  </button>
                  <button
                    onClick={() => {
                      setOpen(false);
                      logout();
                      navigate("/login");
                    }}
                    className="block px-4 py-2 text-sm text-gray-700 hover:bg-red-600 hover:text-white w-full text-left"
                  >
                    Đăng xuất
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
