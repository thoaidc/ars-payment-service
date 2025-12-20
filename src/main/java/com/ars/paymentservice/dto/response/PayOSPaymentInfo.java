package com.ars.paymentservice.dto.response;

public class PayOSPaymentInfo {
    private String accountNumber;
    private String accountName;
    private Long amount;
    private String description;
    private Long orderCode;
    private String currency;
    private String paymentLinkId;
    private String status;
    private Long expiredAt;
    private String checkoutUrl;
    private String qrCode;

    public PayOSPaymentInfo() {}

    public PayOSPaymentInfo(
        String accountNumber,
        String accountName,
        Long amount,
        String description,
        Long orderCode,
        String currency,
        String paymentLinkId,
        String status,
        Long expiredAt,
        String checkoutUrl,
        String qrCode
    ) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.amount = amount;
        this.description = description;
        this.orderCode = orderCode;
        this.currency = currency;
        this.paymentLinkId = paymentLinkId;
        this.status = status;
        this.expiredAt = expiredAt;
        this.checkoutUrl = checkoutUrl;
        this.qrCode = qrCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(Long orderCode) {
        this.orderCode = orderCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentLinkId() {
        return paymentLinkId;
    }

    public void setPaymentLinkId(String paymentLinkId) {
        this.paymentLinkId = paymentLinkId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
