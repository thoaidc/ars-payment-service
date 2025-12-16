package com.ars.paymentservice.service.impl;

import com.ars.paymentservice.dto.request.ChangeBalanceAmountEvent;
import com.ars.paymentservice.entity.Balance;
import com.ars.paymentservice.repository.BalanceRepository;
import com.ars.paymentservice.service.BalanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceServiceImpl(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    public void updateBalanceAmount(ChangeBalanceAmountEvent changeBalanceAmountEvent) {
        Integer refId = changeBalanceAmountEvent.getRefId();
        Integer balanceType = changeBalanceAmountEvent.getType();
        Optional<Balance> balanceOptional = balanceRepository.findByTypeAndRefId(balanceType, refId);
        Balance balance = balanceOptional.orElseGet(Balance::new);
        balance.setRefId(refId);
        balance.setType(balanceType);
        BigDecimal oldBalance = Optional.ofNullable(balance.getBalance()).orElse(BigDecimal.ZERO);
        BigDecimal additionalBalance = Optional.ofNullable(changeBalanceAmountEvent.getAmount()).orElse(BigDecimal.ZERO);
        balance.setBalance(oldBalance.add(additionalBalance));
        balanceRepository.save(balance);
    }
}
