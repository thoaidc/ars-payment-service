package com.ars.paymentservice.integration.impl;


import com.ars.paymentservice.constants.PaymentConstants;
import com.ars.paymentservice.dto.request.PaymentRequestDTO;
import com.ars.paymentservice.integration.IBankIntegration;
import org.springframework.stereotype.Service;

@Service(PaymentConstants.Service.PAY_OS)
public class PayOSIntegration implements IBankIntegration {

    @Override
    public String getType() {
        return PaymentConstants.Service.PAY_OS;
    }

    @Override
    public String genQR(PaymentRequestDTO paymentRequestDTO) {
        return "";
    }
}
