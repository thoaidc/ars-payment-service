package com.ars.paymentservice.repository;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.dto.response.FinanceStatisticDTO;
import com.ars.paymentservice.dto.response.PaymentHistoryDTO;
import com.dct.model.dto.request.BaseRequestDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface PaymentHistoryRepositoryCustom {
    Page<PaymentHistoryDTO> getAllWithPaging(SearchPaymentHistoriesRequestDTO requestDTO);
    Optional<FinanceStatisticDTO> getFinanceStatisticForShop(Integer shopId, BaseRequestDTO requestDTO);
    Optional<FinanceStatisticDTO> getFinanceStatisticForAdmin(BaseRequestDTO requestDTO);
}
