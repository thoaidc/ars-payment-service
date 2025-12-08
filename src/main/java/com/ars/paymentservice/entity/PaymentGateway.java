package com.ars.paymentservice.entity;

import com.dct.config.entity.AbstractAuditingEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_gateway")
@SuppressWarnings("unused")
public class PaymentGateway extends AbstractAuditingEntity {
    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
