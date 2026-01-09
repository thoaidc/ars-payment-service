package com.ars.paymentservice.service.impl;

import com.ars.paymentservice.constants.PaymentConstants;
import com.ars.paymentservice.entity.Balance;
import com.ars.paymentservice.entity.PaymentHistory;
import com.ars.paymentservice.repository.BalanceRepository;
import com.ars.paymentservice.repository.PaymentHistoryRepository;
import com.ars.paymentservice.service.BalanceService;

import com.dct.config.common.Common;
import com.dct.model.constants.BasePaymentConstants;
import com.dct.model.dto.auth.BaseUserDTO;
import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.event.ChangeBalanceAmountEvent;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public BalanceServiceImpl(BalanceRepository balanceRepository, PaymentHistoryRepository paymentHistoryRepository) {
        this.balanceRepository = balanceRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    @Override
    @Transactional
    public BaseResponseDTO getBalanceForShop() {
        BaseUserDTO userDTO = Common.getUserWithAuthorities();

        if (Objects.isNull(userDTO.getShopId())) {
            return BaseResponseDTO.builder().ok();
        }

        Integer balanceType = BasePaymentConstants.BalanceType.SHOP;
        Optional<Balance> balanceOptional = balanceRepository.findByTypeAndRefId(balanceType, userDTO.getShopId());
        Balance balance = balanceOptional.orElseGet(Balance::new);

        if (Objects.isNull(balance.getId())) {
            balance.setBalance(BigDecimal.ZERO);
            balance.setRefId(userDTO.getId());
            balance.setType(balanceType);
            balanceRepository.save(balance);
        }

        return BaseResponseDTO.builder().ok(balance.getBalance());
    }

    @Override
    @Transactional
    public BaseResponseDTO getBalanceForAdmin() {
        Common.getUserWithAuthorities();
        Integer balanceType = BasePaymentConstants.BalanceType.SYSTEM;
        Integer refId = BasePaymentConstants.SYSTEM_ACCOUNT_ID;
        Optional<Balance> balanceOptional = balanceRepository.findByTypeAndRefId(balanceType, refId);
        Balance balance = balanceOptional.orElse(null);

        if (Objects.isNull(balance)) {
            balance = new Balance();
            balance.setBalance(BigDecimal.ZERO);
            balance.setRefId(refId);
            balance.setType(balanceType);
            balanceRepository.save(balance);
        }

        return BaseResponseDTO.builder().ok(balance.getBalance());
    }

    @Override
    @Transactional
    public void updateBalanceAmount(ChangeBalanceAmountEvent changeBalanceAmountEvent) {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setRefId(changeBalanceAmountEvent.getRefId());
        paymentHistory.setUserId(changeBalanceAmountEvent.getSenderId());
        paymentHistory.setReceiverId(changeBalanceAmountEvent.getReceiverId());
        paymentHistory.setAmount(changeBalanceAmountEvent.getAmount());
        paymentHistory.setPaymentMethod(PaymentConstants.Service.SYSTEM);
        paymentHistory.setPaymentGatewayId(BasePaymentConstants.SYSTEM_ACCOUNT_ID);
        paymentHistory.setDescription(changeBalanceAmountEvent.getDescription());
        paymentHistory.setStatus(PaymentConstants.Status.SUCCESS);
        paymentHistory.setTransId(UUID.randomUUID().toString());
        paymentHistory.setPaymentTime(Instant.now());

        switch (changeBalanceAmountEvent.getType()) {
            case BasePaymentConstants.BalanceType.SYSTEM:
                paymentHistory.setType(BasePaymentConstants.PaymentType.SYSTEM_COLLECT_FEE);
                break;
            case BasePaymentConstants.BalanceType.SHOP:
                paymentHistory.setType(BasePaymentConstants.PaymentType.SYSTEM_PAY_SHOP);
                break;
        }

        Integer accountOwnerId = changeBalanceAmountEvent.getReceiverId();
        Integer balanceType = changeBalanceAmountEvent.getType();
        Optional<Balance> balanceOptional = balanceRepository.findByTypeAndRefId(balanceType, accountOwnerId);
        Balance balance = balanceOptional.orElseGet(Balance::new);
        balance.setRefId(accountOwnerId);
        balance.setType(balanceType);
        BigDecimal oldBalance = Optional.ofNullable(balance.getBalance()).orElse(BigDecimal.ZERO);
        BigDecimal additionalBalance = Optional.ofNullable(changeBalanceAmountEvent.getAmount()).orElse(BigDecimal.ZERO);
        balance.setBalance(oldBalance.add(additionalBalance));
        balanceRepository.save(balance);
        paymentHistoryRepository.save(paymentHistory);
    }
}
