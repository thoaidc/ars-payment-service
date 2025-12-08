package com.ars.paymentservice.service.impl;

import com.ars.paymentservice.constants.VNPayConstants;
import com.ars.paymentservice.dto.request.CreateQrRequestDTO;
import com.ars.paymentservice.dto.request.PaymentRequestDTO;
import com.ars.paymentservice.integration.IBankIntegration;
import com.ars.paymentservice.integration.factory.BankIntegrationFactory;
import com.ars.paymentservice.service.MoneyTransferService;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.constants.BaseHttpStatusConstants;
import com.dct.model.dto.response.BaseResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MoneyTransferServiceImpl implements MoneyTransferService {
    private static final Logger log = LoggerFactory.getLogger(MoneyTransferServiceImpl.class);
    private final BankIntegrationFactory bankIntegrationFactory;

    public MoneyTransferServiceImpl(BankIntegrationFactory bankIntegrationFactory) {
        this.bankIntegrationFactory = bankIntegrationFactory;
    }

    @Override
    public BaseResponseDTO createQrPayment(CreateQrRequestDTO requestDTO) {
        try {
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();

            paymentRequest.setTransId("TXNkjshfsdfsdjfsjdfjsdfj");
            paymentRequest.setAmount(new BigDecimal("100000.00"));
            paymentRequest.setPaymentContent("Thanh toan don hang abc");
            paymentRequest.setVpnOrderType("other");
            paymentRequest.setVnpCusIpAddress("127.0.0.1");

            IBankIntegration bankService = bankIntegrationFactory.getBankIntegration(requestDTO.getBankCode());
            String paymentQrURL = bankService.genQR(paymentRequest);
            return BaseResponseDTO.builder().ok(paymentQrURL);
        } catch (Exception e) {
            log.error("[CREATE_QR_PAYMENT_ERROR] - Bank code: {}", requestDTO.getBankCode(), e);
        }

        return BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.BAD_REQUEST)
                .success(Boolean.FALSE)
                .message(BaseExceptionConstants.INVALID_REQUEST_DATA)
                .build();
    }
}
