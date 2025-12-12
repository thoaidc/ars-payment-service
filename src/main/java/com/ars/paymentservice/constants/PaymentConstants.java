package com.ars.paymentservice.constants;

public interface PaymentConstants {
    interface Status {
        String PENDING = "PENDING";
        String SUCCESS = "SUCCESS";
        String FAILURE = "FAILURE";
        String REFUND = "REFUND";
    }

    interface Type {
        String PAY_FOR_ORDER = "PAY_FOR_ORDER";
        String PAY_FOR_SUBSCRIPTION = "PAY_FOR_SUBSCRIPTION";
    }

    interface Service {
        String VN_PAY = "vn_pay";
        String PAY_OS = "pay_os";
    }

    int DEFAULT_PAYMENT_EXPIRED_DURATION = 5; // 5 minutes
}
