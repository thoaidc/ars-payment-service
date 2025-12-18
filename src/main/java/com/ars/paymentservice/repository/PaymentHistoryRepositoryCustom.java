package com.ars.paymentservice.repository;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.dto.response.PaymentHistoryDTO;
import org.springframework.data.domain.Page;

public interface PaymentHistoryRepositoryCustom {
    Page<PaymentHistoryDTO> getAllWithPaging(SearchPaymentHistoriesRequestDTO requestDTO);
}
