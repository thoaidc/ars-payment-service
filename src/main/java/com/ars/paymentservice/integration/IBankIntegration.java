package com.ars.paymentservice.integration;

import com.ars.paymentservice.dto.request.PaymentRequestDTO;

public interface IBankIntegration {
    String getType();
    String genQR(PaymentRequestDTO paymentRequestDTO);
}
