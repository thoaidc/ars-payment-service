package com.ars.paymentservice.entity;

import com.ars.paymentservice.dto.response.PaymentHistoryDTO;
import com.dct.config.entity.AbstractAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_history")
@SuppressWarnings("unused")
@SqlResultSetMappings(
    {
        @SqlResultSetMapping(
            name = "paymentHistoriesGetWithPaging",
            classes = {
                @ConstructorResult(
                    targetClass = PaymentHistoryDTO.class,
                    columns = {
                        @ColumnResult(name = "id", type = Integer.class),
                        @ColumnResult(name = "type", type = Integer.class),
                        @ColumnResult(name = "userId", type = Integer.class),
                        @ColumnResult(name = "receiverId", type = Integer.class),
                        @ColumnResult(name = "transId", type = String.class),
                        @ColumnResult(name = "amount", type = BigDecimal.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "description", type = String.class),
                        @ColumnResult(name = "paymentMethod", type = String.class),
                        @ColumnResult(name = "paymentTime", type = Instant.class)
                    }
                )
            }
        )
    }
)
public class PaymentHistory extends AbstractAuditingEntity {
    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "trans_id")
    private String transId; // Response from BANK

    @Column(name = "ref_id", nullable = false)
    private Integer refId;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "payment_gateway_id", nullable = false)
    private Integer paymentGatewayId;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payment_time")
    private Instant paymentTime;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "info", nullable = false)
    private String info;

    @Column(name = "error")
    private String error;

    @Column(name = "response")
    private String response;

    @Column(name = "description", nullable = false)
    private String description;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getPaymentGatewayId() {
        return paymentGatewayId;
    }

    public void setPaymentGatewayId(Integer paymentGatewayId) {
        this.paymentGatewayId = paymentGatewayId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Instant paymentTime) {
        this.paymentTime = paymentTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
