package com.ars.paymentservice.constants;

public interface VNPayConstants {
    // VNPay params key
    String VNP_VERSION_KEY = "vnp_Version";
    String VNP_COMMAND_KEY = "vnp_Command";
    String VNP_TMN_CODE_KEY = "vnp_TmnCode";
    String VNP_AMOUNT_KEY = "vnp_Amount";
    String VNP_BANK_CODE_KEY = "vnp_BankCode";
    String VNP_CREATED_DATE_KEY = "vnp_CreateDate";
    String VNP_CURRENCY_KEY = "vnp_CurrCode";
    String VNP_CUSTOMER_IP_KEY = "vnp_IpAddr";
    String VNP_LOCALE_KEY = "vnp_Locale";
    String VNP_ORDER_INFO_KEY = "vnp_OrderInfo";
    String VNP_ORDER_TYPE_KEY = "vnp_OrderType";
    String VNP_RETURN_URL_KEY = "vnp_ReturnUrl";
    String VNP_EXPIRED_DATE_KEY = "vnp_ExpireDate";
    String VNP_TRANSACTION_REF_ID_KEY = "vnp_TxnRef";
    String VNP_SECURE_HASH_KEY = "vnp_SecureHash";

    // Default params value
    int VNP_ROUNDING_FACTOR = 100;
    String VNP_COMMAND = "pay";
    String VNP_CURRENCY_CODE = "VND";
    String VNP_LOCALE_VN = "vn";
    String VNP_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    String VNP_BANK_CODE_QR = "VNPAYQR";
}
