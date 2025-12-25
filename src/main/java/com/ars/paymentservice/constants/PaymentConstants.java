package com.ars.paymentservice.constants;

public interface PaymentConstants {
    interface Status {
        String PENDING = "PENDING";
        String SUCCESS = "SUCCESS";
        String FAILURE = "FAILURE";
    }

    interface Service {
        String VN_PAY = "vn_pay";
        String PAY_OS = "pay_os";
        String SYSTEM = "SYSTEM";
    }

    int DEFAULT_PAYMENT_EXPIRED_DURATION = 5; // 5 minutes
    String TOPIC_PAYMENT_NOTIFICATION = "payment_notification_";
}
