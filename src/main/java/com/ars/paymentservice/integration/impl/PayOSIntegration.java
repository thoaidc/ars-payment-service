package com.ars.paymentservice.integration.impl;

import com.ars.paymentservice.constants.PaymentConstants;
import com.ars.paymentservice.dto.request.PaymentRequestDTO;
import com.ars.paymentservice.integration.IBankIntegration;
import com.ars.paymentservice.properties.PayOSProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

@Service(PaymentConstants.Service.PAY_OS)
public class PayOSIntegration implements IBankIntegration {
    private static final Logger log = LoggerFactory.getLogger(PayOSIntegration.class);
    private final ObjectMapper objectMapper;
    private final PayOSProperties payOSProperties;
    private final PayOS payOS;

    public PayOSIntegration(PayOS payOS, ObjectMapper objectMapper, PayOSProperties payOSProperties) {
        this.payOS = payOS;
        this.objectMapper = objectMapper;
        this.payOSProperties = payOSProperties;
    }

    @Override
    public String getType() {
        return PaymentConstants.Service.PAY_OS;
    }

    @Override
    public <T> T createPayment(PaymentRequestDTO paymentRequestDTO, Class<T> responseType) {
        try {
            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                    .orderCode(paymentRequestDTO.getTransNumberId())
                    .amount(paymentRequestDTO.getAmount().longValue())
                    .description(paymentRequestDTO.getPaymentContent())
                    .returnUrl(payOSProperties.getMoneyReceiveConfig().getReturnUrl())
                    .cancelUrl(payOSProperties.getMoneyReceiveConfig().getCancelUrl())
                    .build();
            CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);
            return objectMapper.convertValue(data, responseType);
        } catch (Exception e) {
            log.error(
                "[PAY_OS_GEN_QR_ERROR] - transNumberId: {}, amount: {}, content: {}, {}",
                paymentRequestDTO.getTransNumberId(),
                paymentRequestDTO.getAmount().longValue(),
                paymentRequestDTO.getPaymentContent(),
                e.getMessage()
            );
        }

        return null;
    }
}
