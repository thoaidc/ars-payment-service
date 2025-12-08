package com.ars.paymentservice.constants;

public enum VNPayResponseCode {
    // Mã lỗi 00: Giao dịch thành công
    SUCCESS("00", "Giao dịch thành công"),

    // Mã lỗi 07: Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).
    SUSPICIOUS_DEBITED("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường)."),

    // Mã lỗi 09: Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.
    CARD_NOT_REGISTERED("09", "Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng."),

    // Mã lỗi 10: Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần
    AUTH_FAILED_TOO_MANY_TIMES("10", "Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần"),

    // Mã lỗi 11: Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.
    EXPIRED("11", "Đã hết hạn chờ thanh toán."),

    // Mã lỗi 12: Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.
    CARD_LOCKED("12", "Thẻ/Tài khoản của khách hàng bị khóa."),

    // Mã lỗi 13: Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP).
    OTP_INCORRECT("13", "Quý khách nhập sai mật khẩu xác thực giao dịch (OTP)."),

    // Mã lỗi 24: Giao dịch không thành công do: Khách hàng hủy giao dịch
    USER_CANCELLED("24", "Khách hàng hủy giao dịch"),

    // Mã lỗi 51: Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.
    INSUFFICIENT_FUNDS("51", "Tài khoản của quý khách không đủ số dư để thực hiện giao dịch."),

    // Mã lỗi 65: Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.
    DAILY_LIMIT_EXCEEDED("65", "Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày."),

    // Mã lỗi 75: Ngân hàng thanh toán đang bảo trì.
    BANK_MAINTENANCE("75", "Ngân hàng thanh toán đang bảo trì."),

    // Mã lỗi 79: Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định.
    PASS_INCORRECT_TOO_MANY_TIMES("79", "KH nhập sai mật khẩu thanh toán quá số lần quy định."),

    // Mã lỗi 99: Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)
    OTHER_ERROR("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)");

    private final String code;
    private final String description;

    VNPayResponseCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // Phương thức tiện ích để lấy Enum từ mã lỗi
    public static VNPayResponseCode fromCode(String code) {
        for (VNPayResponseCode status : VNPayResponseCode.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return VNPayResponseCode.OTHER_ERROR; // Mặc định trả về lỗi 99 nếu không tìm thấy mã
    }
}
