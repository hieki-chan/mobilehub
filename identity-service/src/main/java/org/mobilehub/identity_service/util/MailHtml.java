package org.mobilehub.identity_service.util;

public final class MailHtml {
    static final String appName = "Mobilehub";

    public static String buildOtpHtmlBody(String otp, long expireMinutes) {
        return """
<div style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 24px;">
    <div style="max-width: 480px; margin: auto; background-color: #ffffff; border-radius: 8px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); text-align: center;">
        <h2 style="color: #333; margin-bottom: 16px;">🔒 Mã Xác Thực (OTP)</h2>

        <p style="color: #555; font-size: 14px; margin-bottom: 8px;">
            Dưới đây là mã OTP để bạn hoàn tất xác thực trên <b>%s</b>:
        </p>

        <div style="font-size: 32px; font-weight: bold; color: #dc2626; letter-spacing: 4px; margin: 16px 0;">
            %s
        </div>

        <p style="color: #777; font-size: 12px;">
            Mã này sẽ hết hạn trong <b>%d phút%s</b>. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.
        </p>
    </div>

    <p style="text-align:center; font-size: 12px; color: #aaa; margin-top: 16px;">
        © %d %s. Mọi quyền được bảo lưu.
    </p>
</div>
""".formatted(appName, otp, expireMinutes, expireMinutes > 1 ? "s" : "", java.time.Year.now().getValue(), appName);
    }

    public static String buildResetPasswordHtmlBody(String resetUrl, long expireMinutes) {
        return """
<div style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 24px;">
    <div style="max-width: 480px; margin: auto; background-color: #ffffff; border-radius: 8px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); text-align: center;">
        
        <h2 style="color: #333; margin-bottom: 16px;">🔐 Đặt Lại Mật Khẩu</h2>
        
        <p style="color: #555; font-size: 14px; margin-bottom: 12px;">
            Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản trên <b>%s</b>.
        </p>

        <p style="color: #555; font-size: 14px; margin-bottom: 12px;">
            Vui lòng nhấn vào liên kết bên dưới để tạo mật khẩu mới:
        </p>

        <p style="word-break: break-word; color: #1E90FF; font-size: 14px; margin: 16px 0;">
            <a href="%s" style="color: #1E90FF; text-decoration: none;">%s</a>
        </p>

        <p style="color: #777; font-size: 12px; margin-top: 16px;">
            Liên kết này sẽ hết hạn sau <b>%d phút</b>.
        </p>

    </div>

    <p style="text-align:center; font-size: 12px; color: #aaa; margin-top: 16px;">
        © %d %s. Mọi quyền được bảo lưu.
    </p>
</div>
""".formatted(
                appName,
                resetUrl,
                resetUrl,
                expireMinutes,
                java.time.Year.now().getValue(),
                appName
        );
    }
}
