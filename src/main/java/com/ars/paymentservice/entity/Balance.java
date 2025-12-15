package com.ars.paymentservice.entity;

import com.dct.config.entity.AbstractAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "balance")
@SuppressWarnings("unused")
public class Balance extends AbstractAuditingEntity {
    @Column(name = "ref_id", nullable = false)
    private Integer refId;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
