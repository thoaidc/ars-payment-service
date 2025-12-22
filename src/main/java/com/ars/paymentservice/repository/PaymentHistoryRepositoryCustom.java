package com.ars.paymentservice.repository;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.dto.response.PaymentHistoryDTO;
import com.dct.model.dto.request.BaseRequestDTO;
import com.dct.model.dto.response.BaseResponseDTO;
import org.springframework.data.domain.Page;

public interface PaymentHistoryRepositoryCustom {
    Page<PaymentHistoryDTO> getAllWithPaging(SearchPaymentHistoriesRequestDTO requestDTO);
    BaseResponseDTO getFinanceStatisticForShop(Integer shopId, BaseRequestDTO requestDTO);
    BaseResponseDTO getFinanceStatisticForAdmin(BaseRequestDTO requestDTO);
}
