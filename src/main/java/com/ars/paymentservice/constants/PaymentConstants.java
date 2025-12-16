package com.ars.paymentservice.constants;

public interface PaymentConstants {
    interface Status {
        String PENDING = "PENDING";
        String SUCCESS = "SUCCESS";
        String FAILURE = "FAILURE";
        String REFUND = "REFUND";
    }

    interface Type {
        Integer PAY_FOR_ORDER = 1;
        Integer SYSTEM_ADD_MONEY = 2;
        Integer SYSTEM_REFUND = 3;
    }

    interface Service {
        String VN_PAY = "vn_pay";
        String PAY_OS = "pay_os";
    }

    int DEFAULT_PAYMENT_EXPIRED_DURATION = 5; // 5 minutes
}
