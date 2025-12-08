package com.ars.paymentservice.constants;

public enum VNPayTransactionStatus {
    // Mã lỗi 00: Giao dịch thành công
    SUCCESS("00", "Giao dịch thành công"),

    // Mã lỗi 01: Giao dịch chưa hoàn tất
    PENDING("01", "Giao dịch chưa hoàn tất"),

    // Mã lỗi 02: Giao dịch bị lỗi
    FAILED("02", "Giao dịch bị lỗi"),

    // Mã lỗi 04: Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY)
    REVERSED("04", "Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY)"),

    // Mã lỗi 05: VNPAY đang xử lý giao dịch này (GD hoàn tiền)
    REFUND_PENDING("05", "VNPAY đang xử lý giao dịch này (GD hoàn tiền)"),

    // Mã lỗi 06: VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền)
    REFUND_REQUESTED("06", "VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền)"),

    // Mã lỗi 07: Giao dịch bị nghi ngờ gian lận
    SUSPICIOUS("07", "Giao dịch bị nghi ngờ gian lận"),

    // Mã lỗi 09: GD Hoàn trả bị từ chối
    REFUND_REJECTED("09", "GD Hoàn trả bị từ chối");

    private final String code;
    private final String description;

    VNPayTransactionStatus(String code, String description) {
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
    public static VNPayTransactionStatus fromCode(String code) {
        for (VNPayTransactionStatus status : VNPayTransactionStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Mã VNPAY Transaction Status không hợp lệ: " + code);
    }
}
