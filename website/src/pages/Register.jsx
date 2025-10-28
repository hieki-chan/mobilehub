import { useState } from "react";
import GoogleLogin from "../components/GoogleLogin";
import PhoneLogin from "../components/PhoneLogin";
import { useNavigate } from "react-router-dom";


export default function Register() {
    const [remember, setRemember] = useState(true)
    const navigate = useNavigate();

    const onLoginSuccess = ({ user, token }) => {
        localStorage.setItem('user', JSON.stringify(user))
        if (remember) localStorage.setItem('token', token)

        navigate('/')
        window.dispatchEvent(new Event('user-changed'))
    }

    return (
        <>
            <main className="register-page">
                <div className="register-card">
                    <h2>Đăng ký</h2>
                    <p className="muted">Trang đăng ký</p>
                    <form className="register-form" noValidate>
                        <label className="field">
                            <div className="label">Email</div>
                            <input type="email" placeholder="Nhập email" />
                        </label>

                        <label className="field">
                            <div className="label">Mật khẩu</div>
                            <input type="password" placeholder="Nhập mật khẩu" />
                        </label>

                        <label className="field">
                            <div className="label">Xác nhận mật khẩu</div>
                            <input type="password" placeholder="Nhập lại mật khẩu" />
                        </label>

                    </form>

                    {/* {error && <div className="form-error" role="alert">{error} </div>} */}

                    <button type="button" className="btn btn-primary full">
                        Đăng ký
                    </button>
                    <div className="divider">Hoặc</div>
                    <GoogleLogin clientId="REPLACE_WITH_GOOGLE_CLIENT_ID" onSuccess={onLoginSuccess} />
                    <PhoneLogin onSuccess={onLoginSuccess} />
                </div>
            </main>
        </>
    )
}