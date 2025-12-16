package com.ars.paymentservice.service;

import com.dct.model.event.ChangeBalanceAmountEvent;

public interface BalanceService {
    void updateBalanceAmount(ChangeBalanceAmountEvent changeBalanceAmountEvent);
}
