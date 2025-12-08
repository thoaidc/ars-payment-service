package com.ars.paymentservice.constants;

public interface PaymentConstants {
    interface Status {
        String SUCCESS = "SUCCESS";
        String FAILURE = "FAILURE";
    }

    interface Service {
        String VN_PAY = "vn_pay";
    }

    int DEFAULT_PAYMENT_EXPIRED_DURATION = 5; // 5 minutes
}
