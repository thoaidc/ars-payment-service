package com.ars.paymentservice.service;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.dct.model.dto.response.BaseResponseDTO;

public interface PaymentService {
    BaseResponseDTO getPaymentGateways();
    BaseResponseDTO getPaymentHistoriesWithPaging(SearchPaymentHistoriesRequestDTO requestDTO);
    BaseResponseDTO getPaymentHistoryDetail(Integer paymentHistoryId);
}
