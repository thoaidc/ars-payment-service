package com.ars.paymentservice.service.impl;

import com.ars.paymentservice.dto.mapping.PaymentGatewayResponse;
import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.repository.PaymentGatewayRepository;
import com.ars.paymentservice.repository.PaymentHistoryRepository;
import com.ars.paymentservice.service.PaymentService;
import com.dct.model.dto.response.BaseResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public PaymentServiceImpl(PaymentGatewayRepository paymentGatewayRepository,
                              PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentGatewayRepository = paymentGatewayRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    @Override
    public BaseResponseDTO getPaymentGateways() {
        List<PaymentGatewayResponse> paymentGatewayResponses = paymentGatewayRepository.getPaymentGateways();
        return BaseResponseDTO.builder().total((long) paymentGatewayResponses.size()).ok(paymentGatewayResponses);
    }

    @Override
    public BaseResponseDTO getPaymentHistoriesWithPaging(SearchPaymentHistoriesRequestDTO requestDTO) {
        return null;
    }

    @Override
    public BaseResponseDTO getPaymentHistoryDetail(Integer paymentHistoryId) {
        return null;
    }
}
