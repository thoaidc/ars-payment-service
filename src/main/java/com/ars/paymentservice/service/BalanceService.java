package com.ars.paymentservice.service;

import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.event.ChangeBalanceAmountEvent;

public interface BalanceService {
    BaseResponseDTO getBalanceForShop();
    BaseResponseDTO getBalanceForAdmin();
    void updateBalanceAmount(ChangeBalanceAmountEvent changeBalanceAmountEvent);
}
