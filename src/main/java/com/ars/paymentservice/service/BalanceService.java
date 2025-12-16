package com.ars.paymentservice.service;

import com.ars.paymentservice.dto.request.ChangeBalanceAmountEvent;

public interface BalanceService {
    void updateBalanceAmount(ChangeBalanceAmountEvent changeBalanceAmountEvent);
}
