package com.ars.paymentservice.dto.request;

import java.math.BigDecimal;

public class PaymentRequestDTO {
    private String transId;
    private Long transNumberId;
    private BigDecimal amount;
    private String locale;
    private String currencyCode;
    private String paymentContent;
    private String vnpBankCode;
    private String vpnOrderType;
    private String vnpCusIpAddress;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Long getTransNumberId() {
        return transNumberId;
    }

    public void setTransNumberId(Long transNumberId) {
        this.transNumberId = transNumberId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getPaymentContent() {
        return paymentContent;
    }

    public void setPaymentContent(String paymentContent) {
        this.paymentContent = paymentContent;
    }

    public String getVnpBankCode() {
        return vnpBankCode;
    }

    public void setVnpBankCode(String vnpBankCode) {
        this.vnpBankCode = vnpBankCode;
    }

    public String getVpnOrderType() {
        return vpnOrderType;
    }

    public void setVpnOrderType(String vpnOrderType) {
        this.vpnOrderType = vpnOrderType;
    }

    public String getVnpCusIpAddress() {
        return vnpCusIpAddress;
    }

    public void setVnpCusIpAddress(String vnpCusIpAddress) {
        this.vnpCusIpAddress = vnpCusIpAddress;
    }
}
