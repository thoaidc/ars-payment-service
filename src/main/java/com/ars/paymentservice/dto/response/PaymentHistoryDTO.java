package com.ars.paymentservice.dto.response;

import com.dct.model.constants.BaseDatetimeConstants;
import com.dct.model.dto.response.AuditingDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentHistoryDTO extends AuditingDTO {
    private Integer type;
    private String transId; // Response from BANK
    private Integer refId;
    private String refCode;
    private Integer receiverId;
    private String receiverName;
    private Integer userId;
    private String username;
    private Integer paymentGatewayId;
    private String paymentMethod;
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = BaseDatetimeConstants.Formatter.DD_MM_YYYY_HH_MM_SS_SLASH,
        timezone = BaseDatetimeConstants.ZoneID.DEFAULT
    )
    private Instant paymentTime;
    private BigDecimal amount;
    private String status;
    private String error;
    private String description;

    public PaymentHistoryDTO() {}

    public PaymentHistoryDTO(
        Integer id,
        Integer type,
        Integer userId,
        Integer receiverId,
        String transId,
        BigDecimal amount,
        String status,
        String description,
        String paymentMethod,
        Instant paymentTime
    ) {
        super.setId(id);
        this.type = type;
        this.transId = transId;
        this.receiverId = receiverId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.amount = amount;
        this.status = status;
        this.description = description;
    }

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

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
