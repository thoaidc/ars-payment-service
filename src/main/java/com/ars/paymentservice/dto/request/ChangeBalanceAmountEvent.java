package com.ars.paymentservice.dto.request;

import java.math.BigDecimal;

public class ChangeBalanceAmountEvent {
    private Integer type;
    private Integer refId;
    private BigDecimal amount;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "type=" + type + ", refId=" + refId + ", amount=" + amount;
    }
}
